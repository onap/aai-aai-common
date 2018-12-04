/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-18 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.edges;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.onap.aai.edges.enums.DirectionNotation;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.SchemaVersion;
import org.onap.aai.setup.SchemaVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import static com.jayway.jsonpath.Filter.filter;
import static com.jayway.jsonpath.Criteria.where;

/**
 * EdgeIngestor - ingests A&AI edge rule schema files per given config, serves that edge rule
 * 	information, including allowing various filters to extract particular rules.
 */
@Component
public class EdgeIngestor {

    private static final Logger LOG = LoggerFactory.getLogger(EdgeIngestor.class);

    private Map<SchemaVersion, List<DocumentContext>> versionJsonFilesMap;
	private static final String READ_START = "$.rules.[?]";
	private static final String READ_ALL_START = "$.rules.*";

	private SchemaVersions schemaVersions;

	private Set<String> multipleLabelKeys;

    private final LoadingCache<SchemaFilter,Multimap<String,EdgeRule>> cacheFilterStore;

    private final LoadingCache<String, String[]> cousinLabelStore;

	//-----ingest-----//
	/**
	 * Instantiates the EdgeIngestor bean.
	 *
	 * @param translator - ConfigTranslator autowired in by Spring framework which
	 * contains the configuration information needed to ingest the desired files.
	 */
	@Autowired
	public EdgeIngestor(ConfigTranslator translator, SchemaVersions schemaVersions) {
		Map<SchemaVersion, List<String>> filesToIngest = translator.getEdgeFiles();
		JsonIngestor ji = new JsonIngestor();
		this.schemaVersions = schemaVersions;
		versionJsonFilesMap = ji.ingest(filesToIngest);
        this.cacheFilterStore = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .build(
                new CacheLoader<SchemaFilter, Multimap<String, EdgeRule>>() {
                    @Override
                    public Multimap<String, EdgeRule> load(SchemaFilter key) {
                        return extractRules(key);
                    }
                }
            );

        this.cousinLabelStore = CacheBuilder.newBuilder()
            .maximumSize(50)
            .build(
                new CacheLoader<String, String[]>() {
                    @Override
                    public String[] load(String key) throws Exception {
                        return retrieveCousinLabels(key);
                    }
                }
            );
	}

	//-----methods for getting rule info-----//

	/**
	 * Gets list of all edge rules defined in the latest version's schema
	 *
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules associated with those types
	 * 		where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 		no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 * @throws EdgeRuleNotFoundException if none found
	 */
	public Multimap<String, EdgeRule> getAllCurrentRules() throws EdgeRuleNotFoundException {
		return getAllRules(schemaVersions.getDefaultVersion());
	}

    /**
     * Retrieves all the nodes that contain multiple edge labels
     *
     * A lazy instantiation to retrieve all this info on first call
     *
     * @return  a set containing a list of strings where each string is
     *          concatenated by a pipe (|) character such as aNodeType|bNodeType
     */
	public Set<String> getMultipleLabelKeys(){

	    if(multipleLabelKeys == null){
            multipleLabelKeys = new HashSet<>();
            try {
                final Multimap<String, EdgeRule> edges = this.getAllCurrentRules();
                if(edges == null || edges.isEmpty()){
                    LOG.warn("Unable to find any edge rules for the latest version");
                }
                edges.keySet().forEach((key) -> {
                    Collection<EdgeRule> rules = edges.get(key);
                    if(rules.size() > 1){
                        multipleLabelKeys.add(key);
                    }
                });
            } catch (EdgeRuleNotFoundException e) {
                LOG.info("For the latest schema version, unable to find any edges with multiple keys");
            }
        }

        return multipleLabelKeys;
    }
	/**
	 * Gets list of all edge rules defined in the given version's schema
	 *
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules associated with those types
	 * 		where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 		no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 * @throws EdgeRuleNotFoundException if none found
	 */
	public Multimap<String, EdgeRule> getAllRules(SchemaVersion v) throws EdgeRuleNotFoundException {
		Multimap<String, EdgeRule> found = extractRules(null, v);
		if (found.isEmpty()) {
			throw new EdgeRuleNotFoundException("No rules found for version " + v.toString() + ".");
		} else {
			return found;
		}
	}

	/**
	 * Finds the rules (if any) matching the given query criteria. If none, the returned Multimap
	 * will be empty.
	 *
	 * @param q - EdgeRuleQuery with filter criteria set
	 *
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 * @throws EdgeRuleNotFoundException if none found
	 */
	public Multimap<String, EdgeRule> getRules(EdgeRuleQuery q) throws EdgeRuleNotFoundException {
		Multimap<String, EdgeRule> found = null;
	    if(q.getVersion().isPresent()){
			found = extractRules(q.getFilter(), q.getVersion().get());
		} else {
	    	found = extractRules(q.getFilter(), schemaVersions.getDefaultVersion());
		}
		if (found.isEmpty()) {
			throw new EdgeRuleNotFoundException("No rules found for " + q.toString());
		} else {
			Multimap<String, EdgeRule> copy = ArrayListMultimap.create();
			found.entries().stream().forEach((entry) -> {
			    EdgeRule rule = new EdgeRule(entry.getValue());
			    if(!q.getFromType().equals(rule.getFrom())){
                    /* To maintain backwards compatibility with old EdgeRules API,
                     * where the direction of the returned EdgeRule would be
                     * flipped (if necessary) to match the directionality of
                     * the input params.
                     * ie, If the rule is from=A,to=B,direction=OUT,
                     * if the user asked (A,B) the direction would be OUT,
                     * if they asked (B,A), it would be IN to match.
                     */
			        rule.flipDirection();
                }
			    copy.put(entry.getKey(), rule);
            });

			return copy;
		}
	}

	/**
	 * Gets the rule satisfying the given filter criteria. If there are more than one
	 * that match, return the default rule. If there is no clear default to return, or
	 * no rules match at all, error.
	 *
	 * @param q - EdgeRuleQuery with filter criteria set
	 * @return EdgeRule satisfying given criteria
	 * @throws EdgeRuleNotFoundException if none found that match
	 * @throws AmbiguousRuleChoiceException if multiple match but no way to choice one from them
	 * 			Specifically, if multiple node type pairs come back (ie bar|foo and asdf|foo,
	 * 					no way to know which is appropriate over the others),
	 * 			or if there is a mix of Tree and Cousin edges because again there is no way to
	 * 					know which is "defaulter" than the other.
	 * 			The default property only clarifies among multiple cousin edges of the same node pair,
	 * 				ex: which l-interface|logical-link rule to default to.
	 */
	public EdgeRule getRule(EdgeRuleQuery q) throws EdgeRuleNotFoundException, AmbiguousRuleChoiceException {
		Multimap<String, EdgeRule> found = null;
		if(q.getVersion().isPresent()){
			found = extractRules(q.getFilter(), q.getVersion().get());
		} else {
			found = extractRules(q.getFilter(), schemaVersions.getDefaultVersion());
		}

		if (found.isEmpty()) {
			throw new EdgeRuleNotFoundException("No rule found for " + q.toString() + ".");
		}

		EdgeRule rule = null;
		if (found.keys().size() == 1) { //only one found, cool we're done
			for (Entry<String, EdgeRule> e : found.entries()) {
				rule = e.getValue();
			}
		} else {
			rule = getDefaultRule(found);
		}


		if (rule == null) { //should never get here though
			throw new EdgeRuleNotFoundException("No rule found for " + q.toString() + ".");
		} else {
            rule = new EdgeRule(rule);
			if (!q.getFromType().equals(rule.getFrom())) {
				/* To maintain backwards compatibility with old EdgeRules API,
				 * where the direction of the returned EdgeRule would be
				 * flipped (if necessary) to match the directionality of
				 * the input params.
				 * ie, If the rule is from=A,to=B,direction=OUT,
				 * if the user asked (A,B) the direction would be OUT,
				 * if they asked (B,A), it would be IN to match.
				 */
				rule.flipDirection();
			}
			return rule;
		}
	}



	private EdgeRule getDefaultRule(Multimap<String, EdgeRule> found) throws AmbiguousRuleChoiceException {
		if (found.keySet().size() > 1) { //ie multiple node pairs (a|c and b|c not just all a|c) case
			StringBuilder sb = new StringBuilder();
			for (String k : found.keySet()) {
				sb.append(k).append(" ");
			}
			throw new AmbiguousRuleChoiceException("No way to select single rule from these pairs: " + sb.toString() + ".");
		}

		int defaultCount = 0;
		EdgeRule defRule = null;
		for (Entry<String, EdgeRule> e : found.entries()) {
			EdgeRule rule = e.getValue();
			if (rule.isDefault()) {
				defaultCount++;
				defRule = rule;
			}
		}
		if (defaultCount > 1) {
			throw new AmbiguousRuleChoiceException("Multiple defaults found.");
		} else if (defaultCount == 0) {
			throw new AmbiguousRuleChoiceException("No default found.");
		}

		return defRule;
	}

	/**
	 * Checks if there exists any rule that satisfies the given filter criteria.
	 *
	 * @param q - EdgeRuleQuery with filter criteria set
	 * @return boolean
	 */
	public boolean hasRule(EdgeRuleQuery q) {
	    if(q.getVersion().isPresent()){
			return !extractRules(q.getFilter(), q.getVersion().get()).isEmpty();
		} else {
	    	return !extractRules(q.getFilter(), schemaVersions.getDefaultVersion()).isEmpty();
		}
	}

	/**
	 * Gets all cousin rules for the given node type in the latest schema version.
	 *
	 * @param nodeType
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getCousinRules(String nodeType) {
		return getCousinRules(nodeType, schemaVersions.getDefaultVersion()); //default to latest
	}


	public String[] retrieveCousinLabels(String nodeType){

	    Multimap<String, EdgeRule> cousinRules = getCousinRules(nodeType);
	    String[] cousinLabels = new String[cousinRules.size()];

	    return cousinRules.entries()
                .stream()
                .map((entry) -> entry.getValue().getLabel())
                .collect(Collectors.toList())
                .toArray(cousinLabels);
    }

    public String[] retrieveCachedCousinLabels(String nodeType) throws ExecutionException {
	    return cousinLabelStore.get(nodeType);
    }

	/**
	 * Gets all cousin rules for the given node type in the given schema version.
	 *
	 * @param nodeType
	 * @param v - the version of the edge rules to query
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getCousinRules(String nodeType, SchemaVersion v) {
		return extractRules(new EdgeRuleQuery.Builder(nodeType).edgeType(EdgeType.COUSIN).build().getFilter(), v);
	}

	/**
	 * Returns if the given node type has any cousin relationships in the current version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasCousinRule(String nodeType) {
		return hasCousinRule(nodeType, schemaVersions.getDefaultVersion());
	}

	/**
	 * Returns if the given node type has any cousin relationships in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasCousinRule(String nodeType, SchemaVersion v) {
		return !getCousinRules(nodeType, v).isEmpty();
	}

	/**
	 * Gets all rules where "{given nodeType} contains {otherType}" in the latest schema version.
	 *
	 * @param nodeType - node type that is the container in the returned relationships
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getChildRules(String nodeType) {
		return getChildRules(nodeType, schemaVersions.getDefaultVersion());
	}

	/**
	 * Gets all rules where "{given nodeType} contains {otherType}" in the given schema version.
	 *
	 * @param nodeType - node type that is the container in the returned relationships
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getChildRules(String nodeType, SchemaVersion v) {
		Filter from = assembleFilterSegments(where(EdgeField.FROM.toString()).is(nodeType), getSameDirectionContainmentCriteria());
		Filter to = assembleFilterSegments(where(EdgeField.TO.toString()).is(nodeType), getOppositeDirectionContainmentCriteria());
		Filter total = from.or(to);

		return extractRules(total, v);
	}

	/**
	 * Returns if the given node type has any child relationships (ie it contains another node type) in the current version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasChildRule(String nodeType) {
		return hasChildRule(nodeType, schemaVersions.getDefaultVersion());
	}

	/**
	 * Returns if the given node type has any child relationships (ie it contains another node type) in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasChildRule(String nodeType, SchemaVersion v) {
		return !getChildRules(nodeType, v).isEmpty();
	}

	/**
	 * Gets all rules where "{given nodeType} is contained by {otherType}" in the latest schema version.
	 *
	 * @param nodeType - node type that is the containee in the returned relationships
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getParentRules(String nodeType) {
		return getParentRules(nodeType, schemaVersions.getDefaultVersion());
	}

	/**
	 * Gets all rules where "{given nodeType} is contained by {otherType}" in the given schema version.
	 *
	 * @param nodeType - node type that is the containee in the returned relationships
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	public Multimap<String, EdgeRule> getParentRules(String nodeType, SchemaVersion v) {
		Filter from = assembleFilterSegments(where(EdgeField.FROM.toString()).is(nodeType), getOppositeDirectionContainmentCriteria());
		Filter to = assembleFilterSegments(where(EdgeField.TO.toString()).is(nodeType), getSameDirectionContainmentCriteria());
		Filter total = from.or(to);

		return extractRules(total, v);
	}

	/**
	 * Returns if the given node type has any parent relationships (ie it is contained by another node type) in the current version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasParentRule(String nodeType) {
		return hasParentRule(nodeType, schemaVersions.getDefaultVersion());
	}

	/**
	 * Returns if the given node type has any parent relationships (ie it is contained by another node type) in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasParentRule(String nodeType, SchemaVersion v) {
		return !getParentRules(nodeType, v).isEmpty();
	}

	/**
	 * Applies the given filter to the DocumentContext(s) for the given version to extract
	 * edge rules, and converts this extracted information into the Multimap form
	 *
	 * @param filter - JsonPath filter to read the DocumentContexts with. May be null
	 * 					to denote no filter, ie get all.
	 * @param v - The schema version to extract from
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Map will be empty if
	 * 			no rules are found.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	private Multimap<String, EdgeRule> extractRules(Filter filter, SchemaVersion v) {
	    SchemaFilter schemaFilter = new SchemaFilter(filter, v);
        try {
            return cacheFilterStore.get(schemaFilter);
        } catch (ExecutionException e) {
            LOG.info("Encountered exception during the retrieval of the rules");
            return ArrayListMultimap.create();
        }
    }

	public Multimap<String, EdgeRule> extractRules(SchemaFilter schemaFilter){
        List<Map<String, String>> foundRules = new ArrayList<>();
        List<DocumentContext> docs = versionJsonFilesMap.get(schemaFilter.getSchemaVersion());
        if (docs != null) {
            for (DocumentContext doc : docs) {
                if (schemaFilter.getFilter() == null) {
                    foundRules.addAll(doc.read(READ_ALL_START));
                } else {
                    foundRules.addAll(doc.read(READ_START, Filter.parse(schemaFilter.getFilter())));
                }
            }
        }

        return convertToEdgeRules(foundRules);
    }

	//-----filter building helpers-----//
	/**
	 * ANDs together the given start criteria with each criteria in the pieces list, and
	 * then ORs together these segments into one filter.
	 *
	 * JsonPath doesn't have an OR method on Criteria, only on Filters, so assembling
	 * a complete filter requires this sort of roundabout construction.
	 *
	 * @param start - Criteria of the form where(from/to).is(nodeType)
	 * 					(ie the start of any A&AI edge rule query)
	 * @param pieces - Other Criteria to be applied
	 * @return Filter constructed from the given Criteria
	 */
	private Filter assembleFilterSegments(Criteria start, List<Criteria> pieces) {
		List<Filter> segments = new ArrayList<>();
		for (Criteria c : pieces) {
			segments.add(filter(start).and(c));
		}
		Filter assembled = segments.remove(0);
		for (Filter f : segments) {
			assembled = assembled.or(f);
		}
		return assembled;
	}

	/**
	 * Builds the sub-Criteria for a containment edge rule query where the direction
	 * and containment fields must match.
	 *
	 * Used for getChildRules() where the container node type is in the "from" position and
	 * for getParentRules() where the containee type is in the "to" position.
	 *
	 * @return List<Criteria> covering property permutations defined with either notation or explicit direction
	 */
	private List<Criteria> getSameDirectionContainmentCriteria() {
		List<Criteria> crits = new ArrayList<>();

		crits.add(where(EdgeField.CONTAINS.toString()).is(DirectionNotation.DIRECTION.toString()));

		crits.add(where(EdgeField.DIRECTION.toString()).is(Direction.OUT.toString())
				.and(EdgeField.CONTAINS.toString()).is(Direction.OUT.toString()));

		crits.add(where(EdgeField.DIRECTION.toString()).is(Direction.IN.toString())
				.and(EdgeField.CONTAINS.toString()).is(Direction.IN.toString()));

		return crits;
	}

	/**
	 * Builds the sub-Criteria for a containment edge rule query where the direction
	 * and containment fields must not match.
	 *
	 * Used for getChildRules() where the container node type is in the "to" position and
	 * for getParentRules() where the containee type is in the "from" position.
	 *
	 * @return List<Criteria> covering property permutations defined with either notation or explicit direction
	 */
	private List<Criteria> getOppositeDirectionContainmentCriteria() {
		List<Criteria> crits = new ArrayList<>();

		crits.add(where(EdgeField.CONTAINS.toString()).is(DirectionNotation.OPPOSITE.toString()));

		crits.add(where(EdgeField.DIRECTION.toString()).is(Direction.OUT.toString())
				.and(EdgeField.CONTAINS.toString()).is(Direction.IN.toString()));

		crits.add(where(EdgeField.DIRECTION.toString()).is(Direction.IN.toString())
				.and(EdgeField.CONTAINS.toString()).is(Direction.OUT.toString()));

		return crits;
	}

	//-----rule packaging helpers-----//
	/**
	 * Converts the raw output from reading the json file to the Multimap<String key, EdgeRule> format
	 *
	 * @param allFound - raw edge rule output read from json file(s)
	 * 			(could be empty if none found)
	 * @return Multimap<String, EdgeRule> of node names keys to the EdgeRules where the key takes the form of
	 * 			{alphabetically first nodetype}|{alphabetically second nodetype}. Will be empty if input
	 * 			was empty.
	 * 		ex: buildAlphabetizedKey("l-interface", "logical-link") -> "l-interface|logical-link"
	 * 			buildAlphabetizedKey("logical-link", "l-interface") -> "l-interface|logical-link"
	 *
	 * 	This is alphabetical order to normalize the keys, as sometimes there will be multiple
	 * 	rules for a pair of node types but the from/to value in the json is flipped for some of them.
	 */
	private Multimap<String, EdgeRule> convertToEdgeRules(List<Map<String, String>> allFound) {
		Multimap<String, EdgeRule> rules = ArrayListMultimap.create();

		TypeAlphabetizer alpher = new TypeAlphabetizer();

		for (Map<String, String> raw : allFound) {
			EdgeRule converted = new EdgeRule(raw);
			if (converted.getFrom().equals(converted.getTo())) {
				/* the way the code worked in the past was with outs and
				 * when we switched it to in the same-node-type to
				 * same-node-type parent child edges were failing because all
				 * of the calling code would pass the parent as the left argument,
				 * so it was either in that method swap the parent/child,
				 * flip the edge rule or make all callers swap. the last seemed
				 * like a bad idea. and felt like the edge flip was the better
				 * of the remaining 2 */
				converted.flipDirection();
			}
			String alphabetizedKey = alpher.buildAlphabetizedKey(raw.get(EdgeField.FROM.toString()), raw.get(EdgeField.TO.toString()));
			rules.put(alphabetizedKey, converted);
		}

		return rules;
	}

}
