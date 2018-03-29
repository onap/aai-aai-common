package org.onap.aai.edges;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.onap.aai.edges.enums.AAIDirection;
import org.onap.aai.edges.enums.DirectionNotation;
import org.onap.aai.edges.enums.EdgeField;
import org.onap.aai.edges.enums.EdgeType;
import org.onap.aai.edges.exceptions.AmbiguousRuleChoiceException;
import org.onap.aai.edges.exceptions.EdgeRuleNotFoundException;
import org.onap.aai.setup.ConfigTranslator;
import org.onap.aai.setup.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.Criteria;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import static com.jayway.jsonpath.Filter.filter;
import com.jayway.jsonpath.JsonPath;
import static com.jayway.jsonpath.Criteria.where;

@Component
/**
 * EdgeIngestor - ingests A&AI edge rule schema files per given config, serves that edge rule
 * 	information, including allowing various filters to extract particular rules.
 */
public class EdgeIngestor {
	private Map<Version, List<DocumentContext>> versionJsonFilesMap;
	private static final String READ_START = "$.rules.[?]";
	private static final String READ_ALL_START = "$.rules.*";
	
	//-----ingest-----//
	@Autowired
	/**
	 * Instantiates the EdgeIngestor bean.
	 * 
	 * @param translator - ConfigTranslator autowired in by Spring framework which
	 * contains the configuration information needed to ingest the desired files.
	 */
	public EdgeIngestor(ConfigTranslator translator) {
		Map<Version, List<String>> filesToIngest = translator.getEdgeFiles();
		JsonIngestor ji = new JsonIngestor();
		versionJsonFilesMap = ji.ingest(filesToIngest);
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
		return getAllRules(Version.getLatest());
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
	public Multimap<String, EdgeRule> getAllRules(Version v) throws EdgeRuleNotFoundException {
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
		Multimap<String, EdgeRule> found = extractRules(q.getFilter(), q.getVersion());
		if (found.isEmpty()) {
			throw new EdgeRuleNotFoundException("No rules found for " + q.toString());
		} else {
			return found;
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
		Multimap<String, EdgeRule> found = extractRules(q.getFilter(), q.getVersion());
		
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
		return !extractRules(q.getFilter(), q.getVersion()).isEmpty();
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
		return getCousinRules(nodeType, Version.getLatest()); //default to latest
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
	public Multimap<String, EdgeRule> getCousinRules(String nodeType, Version v) {
		return extractRules(new EdgeRuleQuery.Builder(nodeType).edgeType(EdgeType.COUSIN).build().getFilter(), v);
	}
	
	/**
	 * Returns if the given node type has any cousin relationships in the current version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasCousinRule(String nodeType) {
		return hasCousinRule(nodeType, Version.getLatest());
	}
	
	/**
	 * Returns if the given node type has any cousin relationships in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasCousinRule(String nodeType, Version v) {
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
		return getChildRules(nodeType, Version.getLatest());
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
	public Multimap<String, EdgeRule> getChildRules(String nodeType, Version v) {
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
		return hasChildRule(nodeType, Version.getLatest());
	}
	
	/**
	 * Returns if the given node type has any child relationships (ie it contains another node type) in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasChildRule(String nodeType, Version v) {
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
		return getParentRules(nodeType, Version.getLatest());
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
	public Multimap<String, EdgeRule> getParentRules(String nodeType, Version v) {
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
		return hasParentRule(nodeType, Version.getLatest());
	}
	
	/**
	 * Returns if the given node type has any parent relationships (ie it is contained by another node type) in the given version.
	 * @param nodeType
	 * @return boolean
	 */
	public boolean hasParentRule(String nodeType, Version v) {
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
	private Multimap<String, EdgeRule> extractRules(Filter filter, Version v) {
		List<Map<String, String>> foundRules = new ArrayList<>();
		List<DocumentContext> docs = versionJsonFilesMap.get(v);
		if (docs != null) {
			for (DocumentContext doc : docs) {
				if (filter == null) {
					foundRules.addAll(doc.read(READ_ALL_START));
				} else {
					foundRules.addAll(doc.read(READ_START, filter));
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
	 * @param List<Map<String, String>> allFound - raw edge rule output read from json file(s) 
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
		
		if (!allFound.isEmpty()) {
			for (Map<String, String> raw : allFound) {
				EdgeRule converted = new EdgeRule(raw);
				String alphabetizedKey = alpher.buildAlphabetizedKey(raw.get(EdgeField.FROM.toString()), raw.get(EdgeField.TO.toString()));
				rules.put(alphabetizedKey, converted);
			}
		}
		
		return rules;
	}
}
