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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.domain.model;

import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Map;

public class AAIResource {
    private AAIResource parent;
    private AAIResources children;

    private AAIResourceKeys aaiResourceKeys;

    private String namespace; // /Network/Vces/Vce/PortGroups/PortGroup/CvlanTags/CvlanTag ->
                              // "Network"

    private String resourceType; // node or container
    private String resourceClassName;
    private String simpleName; // Vce
    private String fullName; // /Network/Vces/Vce/PortGroups/PortGroup/CvlanTags/CvlanTag
    private String uri; // /network/vces/vce/{vnf-id}/port-groups/port-group/{interface-name}/cvlan-tags/cvlan-tag/{cvlan-tag}
    private String apiVersion;
    private String relationshipListClass;
    private String relationshipUtils;

    private Map<String, String> PropertyDataTypeMap;
    private Multimap<String, String> NodeMapIndexedProps;
    private Multimap<String, String> NodeAltKey1Props;
    private Multimap<String, String> NodeDependencies;
    private Multimap<String, String> NodeKeyProps;
    private Multimap<String, String> NodeReqProps;
    private Multimap<String, String> NodeNameProps;
    private Multimap<String, String> NodeUniqueProps;

    // if new dataTypes are added - make sure to update getAllFields() method below
    private ArrayList<String> stringFields;
    private ArrayList<String> stringListFields;
    private ArrayList<String> longFields;
    private ArrayList<String> intFields;
    private ArrayList<String> shortFields;
    private ArrayList<String> booleanFields;

    private ArrayList<String> requiredFields;
    private ArrayList<String> orderedFields;
    private AAIResource recurseToResource;
    private boolean allowDirectWrite;
    private boolean allowDirectRead;
    private ArrayList<String> autoGenUuidFields;

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public AAIResource getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(AAIResource parent) {
        this.parent = parent;
    }

    /**
     * Gets the children.
     *
     * @return the children
     */
    public AAIResources getChildren() {
        if (this.children == null) {
            this.children = new AAIResources();
        }
        return this.children;
    }

    /**
     * Gets the aai resource keys.
     *
     * @return the aai resource keys
     */
    public AAIResourceKeys getAaiResourceKeys() {
        if (aaiResourceKeys == null) {
            aaiResourceKeys = new AAIResourceKeys();
        }
        return aaiResourceKeys;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the namespace.
     *
     * @param namespace the new namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Gets the resource type.
     *
     * @return the resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Sets the resource type.
     *
     * @param resourceType the new resource type
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Gets the simple name.
     *
     * @return the simple name
     */
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * Sets the simple name.
     *
     * @param simpleName the new simple name
     */
    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    /**
     * Gets the full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name.
     *
     * @param fullName the new full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri.
     *
     * @param uri the new uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * Gets the resource class name.
     *
     * @return the resource class name
     */
    public String getResourceClassName() {
        return resourceClassName;
    }

    /**
     * Sets the resource class name.
     *
     * @param resourceClassName the new resource class name
     */
    public void setResourceClassName(String resourceClassName) {
        this.resourceClassName = resourceClassName;
    }

    /**
     * Gets the property data type map.
     *
     * @return the property data type map
     */
    public Map<String, String> getPropertyDataTypeMap() {
        return PropertyDataTypeMap;
    }

    /**
     * Sets the property data type map.
     *
     * @param propertyDataTypeMap the property data type map
     */
    public void setPropertyDataTypeMap(Map<String, String> propertyDataTypeMap) {
        PropertyDataTypeMap = propertyDataTypeMap;
    }

    /**
     * Gets the node map indexed props.
     *
     * @return the node map indexed props
     */
    public Multimap<String, String> getNodeMapIndexedProps() {
        return NodeMapIndexedProps;
    }

    /**
     * Sets the node map indexed props.
     *
     * @param nodeMapIndexedProps the node map indexed props
     */
    public void setNodeMapIndexedProps(Multimap<String, String> nodeMapIndexedProps) {
        NodeMapIndexedProps = nodeMapIndexedProps;
    }

    /**
     * Gets the node key props.
     *
     * @return the node key props
     */
    public Multimap<String, String> getNodeKeyProps() {
        return NodeKeyProps;
    }

    /**
     * Sets the node key props.
     *
     * @param nodeKeyProps the node key props
     */
    public void setNodeKeyProps(Multimap<String, String> nodeKeyProps) {
        this.NodeKeyProps = nodeKeyProps;
    }

    /**
     * Gets the node name props.
     *
     * @return the node name props
     */
    public Multimap<String, String> getNodeNameProps() {
        return NodeNameProps;
    }

    /**
     * Sets the node name props.
     *
     * @param nodeNameProps the node name props
     */
    public void setNodeNameProps(Multimap<String, String> nodeNameProps) {

        NodeNameProps = nodeNameProps;
    }

    /**
     * Gets the node unique props.
     *
     * @return the node unique props
     */
    public Multimap<String, String> getNodeUniqueProps() {
        return NodeUniqueProps;
    }

    /**
     * Sets the node unique props.
     *
     * @param nodeUniqueProps the node unique props
     */
    public void setNodeUniqueProps(Multimap<String, String> nodeUniqueProps) {
        NodeUniqueProps = nodeUniqueProps;
    }

    /**
     * Gets the node req props.
     *
     * @return the node req props
     */
    public Multimap<String, String> getNodeReqProps() {
        return NodeReqProps;
    }

    /**
     * Sets the node req props.
     *
     * @param nodeReqProps the node req props
     */
    public void setNodeReqProps(Multimap<String, String> nodeReqProps) {
        NodeReqProps = nodeReqProps;
    }

    /**
     * Gets the api version.
     *
     * @return the api version
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Sets the api version.
     *
     * @param apiVersion the new api version
     */
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    /**
     * Gets the relationship list class.
     *
     * @return the relationship list class
     */
    public String getRelationshipListClass() {
        return relationshipListClass;
    }

    /**
     * Sets the relationship list class.
     *
     * @param relationshipListClass the new relationship list class
     */
    public void setRelationshipListClass(String relationshipListClass) {
        this.relationshipListClass = relationshipListClass;
    }

    /**
     * Gets the relationship utils.
     *
     * @return the relationship utils
     */
    public String getRelationshipUtils() {
        return relationshipUtils;
    }

    /**
     * Sets the relationship utils.
     *
     * @param relationshipUtils the new relationship utils
     */
    public void setRelationshipUtils(String relationshipUtils) {
        this.relationshipUtils = relationshipUtils;
    }

    /**
     * Gets the string fields.
     *
     * @return the string fields
     */
    public ArrayList<String> getStringFields() {
        if (this.stringFields == null) {
            this.stringFields = new ArrayList<String>();
        }
        return this.stringFields;
    }

    /**
     * Sets the string fields.
     *
     * @param stringFields the new string fields
     */
    public void setStringFields(ArrayList<String> stringFields) {
        this.stringFields = stringFields;
    }

    /**
     * Gets the string list fields.
     *
     * @return the string list fields
     */
    public ArrayList<String> getStringListFields() {
        if (this.stringListFields == null) {
            this.stringListFields = new ArrayList<String>();
        }
        return this.stringListFields;
    }

    /**
     * Sets the string list fields.
     *
     * @param stringListFields the new string list fields
     */
    public void setStringListFields(ArrayList<String> stringListFields) {
        this.stringListFields = stringListFields;
    }

    /**
     * Gets the long fields.
     *
     * @return the long fields
     */
    public ArrayList<String> getLongFields() {
        if (this.longFields == null) {
            this.longFields = new ArrayList<String>();
        }
        return longFields;
    }

    /**
     * Sets the long fields.
     *
     * @param longFields the new long fields
     */
    public void setLongFields(ArrayList<String> longFields) {
        this.longFields = longFields;
    }

    /**
     * Gets the int fields.
     *
     * @return the int fields
     */
    public ArrayList<String> getIntFields() {
        if (this.intFields == null) {
            this.intFields = new ArrayList<String>();
        }
        return intFields;
    }

    /**
     * Sets the int fields.
     *
     * @param intFields the new int fields
     */
    public void setIntFields(ArrayList<String> intFields) {
        this.intFields = intFields;
    }

    /**
     * Gets the short fields.
     *
     * @return the short fields
     */
    public ArrayList<String> getShortFields() {
        if (this.shortFields == null) {
            this.shortFields = new ArrayList<String>();
        }
        return shortFields;
    }

    /**
     * Sets the short fields.
     *
     * @param shortFields the new short fields
     */
    public void setShortFields(ArrayList<String> shortFields) {
        this.shortFields = shortFields;
    }

    /**
     * Gets the boolean fields.
     *
     * @return the boolean fields
     */
    public ArrayList<String> getBooleanFields() {
        if (this.booleanFields == null) {
            this.booleanFields = new ArrayList<String>();
        }
        return booleanFields;
    }

    /**
     * Sets the boolean fields.
     *
     * @param booleanFields the new boolean fields
     */
    public void setBooleanFields(ArrayList<String> booleanFields) {
        this.booleanFields = booleanFields;
    }

    /**
     * Gets the required fields.
     *
     * @return the required fields
     */
    public ArrayList<String> getRequiredFields() {
        if (this.requiredFields == null) {
            this.requiredFields = new ArrayList<String>();
        }
        return requiredFields;
    }

    /**
     * Sets the required fields.
     *
     * @param requiredFields the new required fields
     */
    public void setRequiredFields(ArrayList<String> requiredFields) {
        this.requiredFields = requiredFields;
    }

    /**
     * Gets the ordered fields.
     *
     * @return the ordered fields
     */
    public ArrayList<String> getOrderedFields() {
        if (this.orderedFields == null) {
            this.orderedFields = new ArrayList<String>();
        }
        return this.orderedFields;
    }

    /**
     * Gets the all fields.
     *
     * @return the all fields
     */
    public ArrayList<String> getAllFields() {

        ArrayList<String> allFields = new ArrayList<String>();
        allFields.addAll(getBooleanFields());
        allFields.addAll(getStringListFields());
        allFields.addAll(getStringFields());
        allFields.addAll(getIntFields());
        allFields.addAll(getLongFields());
        allFields.addAll(getShortFields());

        return allFields;
    }

    /**
     * Gets the plural name.
     *
     * @return the plural name
     */
    public String getPluralName() {

        if (simpleName.contains("List") || simpleName.contains("-list"))
            return "";
        String[] fullNameList = getFullName().split("/");
        return fullNameList[fullNameList.length - 2];
    }

    /**
     * Sets the node alt key 1 props.
     *
     * @param _dbRulesNodeAltKey1Props the db rules node alt key 1 props
     */
    public void setNodeAltKey1Props(Multimap<String, String> _dbRulesNodeAltKey1Props) {
        this.NodeAltKey1Props = _dbRulesNodeAltKey1Props;
    }

    /**
     * Gets the node alt key 1 props.
     *
     * @return the node alt key 1 props
     */
    public Multimap<String, String> getNodeAltKey1Props() {
        return this.NodeAltKey1Props;
    }

    /**
     * Sets the node dependencies.
     *
     * @param _dbRulesNodeDependencies the db rules node dependencies
     */
    public void setNodeDependencies(Multimap<String, String> _dbRulesNodeDependencies) {
        this.NodeDependencies = _dbRulesNodeDependencies;
    }

    /**
     * Gets the node dependencies.
     *
     * @return the node dependencies
     */
    public Multimap<String, String> getNodeDependencies() {
        return this.NodeDependencies;
    }

    /**
     * Gets the recurse to resource.
     *
     * @return the recurse to resource
     */
    public AAIResource getRecurseToResource() {
        return this.recurseToResource;
    }

    /**
     * Sets the recurse to resource.
     *
     * @param ancestor the new recurse to resource
     */
    public void setRecurseToResource(AAIResource ancestor) {
        this.recurseToResource = ancestor;

    }

    /**
     * Sets the allow direct write.
     *
     * @param allowDirectWrite the new allow direct write
     */
    public void setAllowDirectWrite(boolean allowDirectWrite) {
        this.allowDirectWrite = allowDirectWrite;
    }

    /**
     * Checks if is allow direct write.
     *
     * @return true, if is allow direct write
     */
    public boolean isAllowDirectWrite() {
        return this.allowDirectWrite;
    }

    /**
     * Sets the allow direct read.
     *
     * @param allowDirectRead the new allow direct read
     */
    public void setAllowDirectRead(boolean allowDirectRead) {
        this.allowDirectRead = allowDirectRead;
    }

    /**
     * Checks if is allow direct read.
     *
     * @return true, if is allow direct read
     */
    public boolean isAllowDirectRead() {
        return this.allowDirectRead;
    }

    /**
     * Gets the auto gen uuid fields.
     *
     * @return the auto gen uuid fields
     */
    public ArrayList<String> getAutoGenUuidFields() {
        if (this.autoGenUuidFields == null) {
            this.autoGenUuidFields = new ArrayList<String>();
        }
        return this.autoGenUuidFields;
    }
}
