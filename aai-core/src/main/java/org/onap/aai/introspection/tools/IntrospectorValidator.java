/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.introspection.tools;

import java.util.*;

import org.onap.aai.db.props.AAIProperties;
import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Introspector;
import org.onap.aai.introspection.IntrospectorWalker;
import org.onap.aai.introspection.Visibility;
import org.onap.aai.introspection.Wanderer;
import org.onap.aai.schema.enums.PropertyMetadata;

public class IntrospectorValidator implements Wanderer {

    private List<Issue> issues = null;
    private List<IssueResolver> issueResolvers = null;
    private boolean validateRequired = true;
    private final int maximumDepth;
    private int currentDepth = 0;

    private final Set<String> relationshipChain;

    /**
     * Instantiates a new introspector validator.
     *
     * @param builder the builder
     */
    private IntrospectorValidator(Builder builder) {
        this.validateRequired = builder.getValidateRequired();
        this.issueResolvers = builder.getResolvers();
        this.maximumDepth = builder.getMaximumDepth();
        issues = new ArrayList<>();

        relationshipChain = new HashSet<>();

        relationshipChain.add("relationship-list");
        relationshipChain.add("relationship");
        relationshipChain.add("relationship-data");
        relationshipChain.add("related-to-property");

    }

    /**
     * Validate.
     *
     * @param obj the obj
     * @return true, if successful
     * @throws AAIException
     */
    public boolean validate(Introspector obj) throws AAIException {
        IntrospectorWalker walker = new IntrospectorWalker(this);
        this.currentDepth = 0;
        walker.walk(obj);

        for (Issue m : issues) {
            if (!m.getSeverity().equals(Severity.WARNING)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets the issues.
     *
     * @return the issues
     */
    public List<Issue> getIssues() {
        return this.issues;
    }

    /**
     * Sets the issue resolvers.
     *
     * @param resolvers the new issue resolvers
     */
    public void setIssueResolvers(List<IssueResolver> resolvers) {
        issueResolvers = new ArrayList<>();
        for (IssueResolver resolver : resolvers) {
            issueResolvers.add(resolver);
        }
    }

    /**
     * Resolve issues.
     *
     * @return true, if successful
     */
    public boolean resolveIssues() {
        boolean result = true;
        for (Issue issue : issues) {
            for (IssueResolver resolver : issueResolvers) {
                if (resolver.resolveIssue(issue)) {
                    issue.setResolved(true);
                }
            }
            if (!issue.isResolved()) {
                result = false;
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processComplexObj(Introspector obj) {

        if (this.currentDepth > this.maximumDepth && !relationshipChain.contains(obj.getDbName())) {
            Issue message = this.buildMessage(Severity.CRITICAL, IssueType.EXCEEDED_ALLOWED_DEPTH,
                    "Maximum allowed depth of this object has been exceeded on: " + obj.getDbName());
            message.setIntrospector(obj);
            issues.add(message);
        }
        Set<String> requiredProps = obj.getRequiredProperties();
        Set<String> keys = obj.getKeys();
        Set<String> props = obj.getProperties();

        for (String prop : props) {
            Object value = obj.getValue(prop);
            if (keys.contains(prop)) {
                if (value == null) {
                    Issue message = this.buildMessage(Severity.CRITICAL, IssueType.MISSING_KEY_PROP,
                            "Missing key property: " + prop);
                    message.setIntrospector(obj);
                    message.setPropName(prop);
                    issues.add(message);
                }
            } else if (requiredProps.contains(prop)) {
                if (value == null && validateRequired) {
                    Issue message = this.buildMessage(Severity.CRITICAL, IssueType.MISSING_REQUIRED_PROP,
                            "Missing required property: " + prop);
                    message.setIntrospector(obj);
                    message.setPropName(prop);
                    issues.add(message);
                }
            }

            final Optional<String> visibility = obj.getPropertyMetadata(prop, PropertyMetadata.VISIBILITY);
            if (visibility.isPresent() && Visibility.internal.equals(Visibility.valueOf(visibility.get()))
                    && obj.getValue(prop) != null) {
                Issue message = this.buildMessage(Severity.ERROR, IssueType.PROPERTY_NOT_VISIBLE,
                        "client attemptted to set property not visible: " + prop);
                message.setIntrospector(obj);
                message.setPropName(prop);
                issues.add(message);

            }
            final Optional<String> requires = obj.getPropertyMetadata(prop, PropertyMetadata.REQUIRES);
            if (requires.isPresent() && (obj.getValue(prop) != null && obj.getValue(requires.get()) == null)) {
                Issue message = this.buildMessage(Severity.CRITICAL, IssueType.DEPENDENT_PROP_NOT_FOUND,
                        prop + " requires " + requires.get() + " to also be popluated.");
                message.setIntrospector(obj);
                message.setPropName(prop);
                issues.add(message);
            }
        }

        if (!relationshipChain.contains(obj.getDbName())) {
            this.currentDepth++;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processPrimitive(String propName, Introspector obj) {
        // NO OP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processPrimitiveList(String propName, Introspector obj) {
        // NO OP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modifyComplexList(List<Introspector> list, List<Object> listReference, Introspector parent,
            Introspector child) {
        // NO OP
    }

    /**
     * Builds the message.
     *
     * @param severity the severity
     * @param error the error
     * @param detail the detail
     * @return the issue
     */
    private Issue buildMessage(Severity severity, IssueType error, String detail) {
        Issue message = new Issue();
        message.setSeverity(severity);
        message.setType(error);
        message.setDetail(detail);

        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createComplexObjIfNull() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int createComplexListSize(Introspector parent, Introspector child) {
        return 0;
    }

    public static class Builder {

        private boolean validateRequired = true;
        private List<IssueResolver> issueResolvers = null;
        private int maximumDepth = AAIProperties.MAXIMUM_DEPTH;

        /**
         * Instantiates a new builder.
         *
         * @param llBuilder the ll builder
         */
        public Builder() {
            issueResolvers = new ArrayList<IssueResolver>();
        }

        /**
         * Validate required.
         *
         * @param validateRequired the validate required
         * @return the builder
         */
        public Builder validateRequired(boolean validateRequired) {
            this.validateRequired = validateRequired;
            return this;
        }

        public Builder restrictDepth(int depth) {
            this.maximumDepth = depth;
            return this;
        }

        /**
         * Adds the resolver.
         *
         * @param resolver the resolver
         * @return the builder
         */
        public Builder addResolver(IssueResolver resolver) {
            issueResolvers.add(resolver);
            return this;
        }

        /**
         * Builds the.
         *
         * @return the introspector validator
         */
        public IntrospectorValidator build() {
            return new IntrospectorValidator(this);
        }

        /**
         * Gets the validate required.
         *
         * @return the validate required
         */
        public boolean getValidateRequired() {
            return this.validateRequired;
        }

        /**
         * Gets the resolvers.
         *
         * @return the resolvers
         */
        public List<IssueResolver> getResolvers() {
            return this.issueResolvers;
        }

        public int getMaximumDepth() {
            return this.maximumDepth;
        }
    }

}
