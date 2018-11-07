.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

==============
 AAI REST API
==============

Overview
========

The AAI REST API provides access to the AAI active inventory graph. The
API is largely configured off of models and configuration files. Each
vertex in the graph has an API that can be called separately or, if part
of a tree structure, as a nested element with one or more generations
(parent, grandparent, etc.).

The edges of the graph are provisioned using a relationship list
construct. For PUT methods, a relationship contains the vertex type or
category (related-to) and a list of relationship data which captures the
key pieces of data required to uniquely identify the resource. On a GET
method, the above information and a URL are returned. The URL can be
used to GET all the details of that object. The URL returned is suitable
for retrying failed commands but should not be expected to be cacheable
for very long periods (e.g., the version of the URL may get deprecated
when the release changes).

The REST API describes each API that AAI provides, independent of the
caller of the API, therefore there is no information to be found here
regarding the expectations on the callers.  That information is
conveyed in AID documents for each client.  AIDs will describe the
information expected from specific clients, but may not contain the
full payloads that would be needed on an update.  Please see the
concurrency notes referenced below in order to do the right kind of
PUTs (GET, replace just what you are changing, PUT) or use the PATCH
capability.

Deprecation Warnings and History
================================

AAI will maintain backwards compatibility for two prior releases.
This means, with the introduction of v14 AAI will support **v11**
(Amsterdam), **v13** (Beijing), and **v14** (Casablanca)

Casablanca (v14)
----------------

- A new API called recents API is now available mostly intended for DCAE use.

- A new and improved bulk api interface is also available now. 

- More details on the above APIs can be found in wiki pages referenced
  in sections below.

Beijing (v13)
-------------

- To handle security vulnerabilities that were raised as part of Nexus
  IQ scans in ONAP, the APIs are being hosted on a spring-boot with
  Jetty web container.

- The deletion rules are now applied to all the nodes that will be
  deleted by the delete request.

  For example, if the graph is:

  +------+----------+
  |nodeA |   nodeD  |
  +------+----------+

  If nodeA is parent of nodeB and nodeB is parent of nodeC

  +------+---------------------+
  |nodeA | CASCADE_TO_CHILDREN |
  +------+---------------------+
  |nodeB | CASCADE_TO_CHILDREN |
  +------+---------------------+
  |nodeC | ERROR_IF_IN_EDGES   |
  +------+---------------------+

- If request is to delete nodeA, it would fail because nodeC has an in
  edge from a node not being deleted in this transaction.

- A configurable server timeout was implemented to make sure the AAI
  server did not continue processing the request long after a client
  times out on their side. An error code ERR.5.4.7406 will be returned
  when this limit is hit. A configuration for clients known to have
  longer running queries currently overrides the default value.

- To handle a risk identified by Fortify scans, a maxOccurs of 5000
  was added to the XSD.

API changes
~~~~~~~~~~~

- DELETE request will generate a DMAAP event for each node deleted
  (not just the for which the DELETE request was made)

- Relationship list

    Starting with Casablanca, multiple edges can exist
    in the graph between the same 2 nodes. The REST API has been
    enhanced via changing the relationship-list so clients can specify
    which edge they are creating and differentiate multiple edges
    between the same 2 nodes. Backwards compatibility with older API
    versions that do notspecify the edge will be maintained.

- A new property “relationship-label” has been added that when
  specified will be used to create any new edge. If not specified the
  default edge label between the two nodes will be used. The
  relationship-label will always be returned with the v12 version of
  GETs whenever the relationship-list is returned.

.. code-block:: json

   {
       "relationship-list": {
	   "relationship": [
	       {
		   "related-link": "/aai/v12/cloud-infrastructure/complexes/complex/6d8f945d-8bd2-4fa2-ad37-36b21fc8fb23-PS2418",
		   "related-to": "complex",
		   "relationship-data": [
		       {
			   "relationship-key": "complex.physical-location-id",
			   "relationship-value": "6d8f945d-8bd2-4fa2-ad37-36b21fc8fb23-PS2418"
		       }
		   ],
		   "relationship-label": "locatedIn"
	       }
	   ]
       }
   }

Amsterdam (v11)
---------------

API retirements:

-  The actions/update API will be retired. Clients must switch to PATCH.
   There is one grandfathered usage for vpe update flows which will be
   retired in v11.

-  The edge tag query will be retired.

Notable attribute and/or valid value changes (generally also impacts
events):

-  The persona-model-id and persona-version will be replaced with
   model-invariant-id (same value as persona-model-id) and
   model-version-id (the UUID of the specific version of a model).
   Persona-model-customization-id will be replaced by
   model-customization-id.

-  The operational-state attribute will be replaced by
   operational-status and the only valid values will be in-service-path
   and out-of-service-path

-  The vpn-binding object will be split in two to reflect more than one
   route-target per binding. The route-target will be a child of
   vpn-binding and some attributes will move from vpn-binding to
   route-target.

-  The following license related attributes will be removed from
   generic-vnf: license-key, entitlement-assignment-group-uuid,
   entitlement-resource-uuid, license-assignment-group-uuid, and
   license-key-uuid due to the introduction of the entitlement and
   license children.

Event Specific:

-  Normal impacts due to renaming or adding attributes, splitting
   objects, etc. Please see swagger documentation for objects of
   interest.

-  In v11, clients that require lineage, children, or relationship
   information need to subscribe to a different DMaaP topic than the
   current one.

Relationship List

-  The related-link will be a URI and thus not contain
   https://{serverroot} (impacts events)

-  The related-link will be used on a PUT as the "first choice" to
   identify the related resource. The relationship-data structure, which
   contains the unordered set of keys, is still an acceptable way to
   relate two objects but, *if both the relationship-data and the
   related-link are passed, and they don't agree, the related-link will
   be used without warning that the data is inconsistent*.

-  The relationship-data will be ignored on PUT.

Future Warning
==============

In the future, the hope is that individual node definitions will be
separately versioned from API behavior and from one another (e.g.,
vserver hasn't changed in many releases and so doesn't need to have
its "definition" version updated).

Because relationships are starting to become more complex, it may be
necessary for AAI to expose to clients the exact relationship between
two nodes.  This will likely be done with a relationship-type
attribute of relationships in the relationship-list.

To support the concept of events getting generated on specific changed
items, AAI will be migrating towards a model of asking clients to do
the most granular PUTs possible rather than leveraging the nested
elements of a tree structure.

The vce, port-group, cvlan-tag, newvce, vpe, oam-network, and
dvs-switch objects will eventually be deprecated in favor of
generic-vnf, l3-network, ctag-assignment, segmentation-assignment, and
TBD.

L3-network will eventually be replaced by virtual-network.

How to Use this Document
========================

The only attributes in our objects that are declared required are
those which we know will be present at the creation of each object and
which are needed to support the construction of the AAI Graph. This
does not imply that one of AAI's clients doesn't need data.

When you click on the API documentation, you will see the Summary of
APIs broken down by namespace (e.g., cloud-infrastructure, business,
network, service-design-and-creation). You can search for **Tag:**
(matching the explicit case) to move from namespace to namespace through
the Summary.

Search for **Paths** to skip past the Summary section where there will
be more detail about each API. Query parameters are provided here, as
well as links to our error codes.

Search for **Schema definitions** to see the definitions of the
payloads. In your browser URL, you can type /#/definitions/node-name at
the end of the html address to skip directly to a payload definition.

Note that the schema definitions now contain information about the
delete scope of a node, edges, and some related node information.
Given this information can now be generated, it is no longer repeated
in this document.

Once AAI has a model and configured it, the AAI development server can
be used to generate sample XML and JSON payloads, according to the
Accept header passed in the request. This is done by calling the
"plural" version of an API followed by the word example (e.g.,
/vserver/vservers/example). This returns a GET result array with one
entry. That single entry can be sent in a PUT request with actual data
(the resource-id does not need to be in the PUT payload as it is on the
URL).

Finally, custom queries that are not simple GETs of a resource must be
identified to AAI as separate user stories.  This includes searching
for a resource with other attributes on the same resource, as well as
searching for resources based on their relationship with other
objects.

AAI API Definition
==================

Namespaces
----------

Cloud Infrastructure Domain
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The Cloud Infrastructure domain (cloud-infrastructure) represents the
assets managed within a cloud site.  This includes the complex, the
physical servers, the availability zones, oam-networks, tenants, and
vserver-related resources (vservers, flavors, images, etc.).

Tenants, oam-networks, availability-zones, volume-groups, images,
flavors, and dvs-switches will have cloud-region as its parent node.

Network Domain
~~~~~~~~~~~~~~

The network namespace contains virtual and physical network resources
as well as connection resources such as physical links, logical links,
lag links, etc.

The vce/port-group/cvlan-tag tree represents an immature model that
blended several resources together in ways that were expedient but
which need to be re-evaluated.  A newvce object exists which was the
basis of the generic-vnf object.  Future efforts will attempt to
migrate vce and vpe into generic-vnf.

Business Domain
~~~~~~~~~~~~~~~

The business namespace captures customers, service-subscriptions, and
service-instances.  This domain is immature and will be evolving as
service design and creation starts to gel.

Customers and service-subscriptions in particular will be evolving
soon.  Any service that is customer facing will see customer and
service-subscription data offboarding to BSSs.  The
service-instance-id will be the "join point" within the BSS to
correlate the service-instance to the product and customer.  Services
that are for infrastructure purposes will have a new entity, an
owning-entity, to replace the customer.  The owning-entity will be
related to the SDC service models that use it.

Service Design and Creation
~~~~~~~~~~~~~~~~~~~~~~~~~~~

The service design and creation namespace captures data we invented
based on what we thought SDC would eventually provide.  The structure
of that data is definitely not what the current plans are for SDC
however we need to at least capture the spirit of what's intended and
communicate that across ONAP sub components that need it.

To date, there are only five containers:

1. Service-capabilities capture the pairings of service to
   resources. At the time of construction, the only data we had to
   denote service was the service-type from SO.  The vnf-type is the
   resource.
   
2. Service captures the service model instances and this will be
   deprecated in the future as things mature
   
3. Vnf-image captures information about the resource image needed for
   a VNF.  This was created due to there being no info available on
   the vservers that run on uCPE
   
4. Models captures model definitions (subgraph definitions using the
   AAI widgets)
   
5. named-queries capture subgraph definitions that allow different
   data to be retrieved for a given type of asset

Security
--------

All REST APIs must be called using https.

HTTPS Basic Authentication will be used to authenticate clients.  The
remote user from the HTTP Servlet Request is used against an AAI
policy to see if the authenticated user is authorized for the resource
and actions being request.

Client should use credentials provided to their system via AAF.

The following will be used for logging and interface diagnostic purposes.

 * X-FromAppId 	Unique Application ID assigned to the user of these APIs 
 * X-TransactionId Unique ID that identifies an API request

The X-FromAppId will be assigned to each application by the AAI team.
The X-TransactionId must be unique to each transaction within the
context of an X-FromAppId.

SO, SDN-C, and AAI have agreed to use the Java UUID class to generate
unique ids for X-TransactionId.

The Accept and Content-type header should be set to either
application/json or application/xml except as documented for PATCH.

Response Codes and Error Handling
---------------------------------

AAI will use the following HTTP codes

HTTP Codes:
~~~~~~~~~~~

- 200 – Success
- 201 – Created
- 202 - Accepted
- 204 – Success, no payload returned
- 400 - Bad Request
- 401 - Unauthorized
- 403 - Forbidden
- 404 - Not Found
- 405 – Method Not Allowed
- 409 - The request could not be completed due to a conflict with the
  current state of the target resource. This code is used in situations
  where the user might be able to resolve the conflict and resubmit the
  request. If the resource version doesn’t match AAI or a required
  resource version isn't sent but should have been
- 410 - You are using a version of the API that has been retired
- 412 – Precondition failed (If the resource version doesn’t match AAI or arequired resource version isn't sent but should have been
- 415 – Unsupported Media Type 500 - Internal Server Error

Successful PUT responses shall return the following codes:
 * 200 (OK): used when an existing resource has been modified and
   there is a response buffer
 * 201 (Created): MUST be used when a new resource is created
 * 202 (Accepted): used when AAI completed the action requested but
   may have taken other actions as well, which are returned in the
   response payload
 * 204 (No Content): used when the existing resource has been modified
   and there is no response buffer

Successful DELETE responses shall return the following codes:
 * 200 (OK): for a successful response if the response includes an
   entity describing the status.
 * 204 (No Content): if the action has been enacted but the response
   does not include an entity.

Successful GET responses shall return the following codes:
 * 200 (OK): for a successful response for a resource that has been found
 * 404 (Not Found) for a successful response retrieving a list of
   items and there were no items found, i.e., the GET of the specific
   plural resource was not found
 * 404 (Not Found): when a specific resource was not found

Failures:
 * 400 Bad Request will be returned if headers are missing
 * 404 Not Found will be returned if an unknown URL is used

In addition, the standard response buffer will be structured as follows unless otherwise specified.   
There are two types of requestErrors.

 * Service Exceptions – These exceptions occur when a service is
   unable to process a request and retrying the request will result in
   a consistent failure (e.g., an application provides invalid input).
 * Policy Exceptions – These exceptions occur when a policy criteria
   has not been met (e.g., the (N+1)th request arrives when an
   application’s service level agreement only allows N transactions
   per time interval).

Italics are specific to the error returned, and generally described in the notes

.. parsed-literal::

  HTTP/1.1 405 *Method Not Allowed* 
  Content-Type: application/json 
  Content-Length: nnnnn
  Date: *Thu, 04 Jun 2009 02:51:59 GMT* 
  {
     “requestError”:{ 
       “policyException”:{ 
       “messageId”:"*POL8007*", 
       “text”:”*The resource was invoked with an unsupported operation: %1.*”, 
       “variables”: [”*PUT*”]
     }
  }

Notes: 

a. On the first line, substitute the appropriate status response code. 
   
b. On the second line, substitute the appropriate content type. 
   
c. Express the requestError structure in the required content type (e.g., either JSON or XML).   AAI will use JSON.
   
d. ‘requestError’ contains either a ‘policyException’ or a ‘serviceException’ structure. 
   
e. url is optional

In 1512, AAI is introducing a response payload that is possible during a successful PUT.  This payload is used to inform the client that, while AAI completed the action requested, the result could be interpreted as a warning or request for additional action, as negotiated with the client.

Sample response to a vserver PUT where the pserver and complex did not exist:

.. code-block:: json

 {"responseMessages": {"responseMessage": [
      {
      "messageId": "INF0003",
      "text": "Success with additional info performing %1 on %2. Added %3 with key %4 (msg=%5) (rc=%6)",
      "variables": {"variable":       [
         "PUTvserver",
         "ccwvm388",
         "complex",
         "physical-location-id=fakeccwcomplex",
         "Added prerequisite object to db:complex",
         "0.3.0004"
      ]}
  },
      {
      "messageId": "INF0003",
      "text": "Success with additional info performing %1 on %2. Added %3 with key %4 (msg=%5) (rc=%6)",
      "variables": {"variable":       [
         "PUTvserver",
         "ccwvm388",
         "pserver",
         "hostname=fakeccwpserver",
         "Added prerequisite object to db:pserver",
         "0.3.0004"
      ]}
   }
 ]}}

Referential Integrity
---------------------

AAI is primarily a view to the relationships between instances of
services, physical and virtual components, etc.  It stores just the
details it needs to be efficient to its tasks and knows how to get
more details if needed.

As such, a transaction sent to AAI may be refused if would break
referential integrity.  The referential integrity rules of AAI are
still evolving as we understand the services and customers that will
use us.

AAI uses a graph database on a NoSQL data store. The following are
true for AAI:

* Some vertices are exposed to the outside world through APIs, others
  are internal to how we store the data (i.e., it may look like one
  resource to our customers but it is expressed as more than one
  vertex in our graph)
  
* Vertices that are internal to AAI will be deleted when the parent
  vertex is deleted, if deletion of the parent leaves the child vertex
  orphaned
  
* Vertices that are exposed need to be managed using specific rules
  for each vertex.
  
* Vertices may have more than just parent/child relationships.  One
  example is a vserver, which will be owned by a tenant and used by a
  VNF.  

The Relationship-List
---------------------

The REST interface does not lend itself to creating more than
parent-child relationships and the backend structure of AAI is a
graph.  A goal of AAI, and shared with ONAP, is to do as little coding
as possible to introduce a new service into the service design and
creation environment.

To that end, we've introduced a relationship-list structure.  AAI will
ask its clients to provide certain data in the relationship-list
structure.

Each relationship has a related-to attribute and a list of key/value
pairs.  The related-to attribute identifies the node type that the
resource being acted on is to be related to using the data in the
key/value pairs.  AAI will encode a set of rules for each resource
type to verify that only valid edges are being made.  AAI will keep
the directionality and cardinality, and the edge attributes within its
own logic.  In the near future, the definition of relationships, their
validity, and cardinality will be captured in the ONAP TOSCA models.

AAI also has a concept of a related-to category.  To date, the only
category is vnf.  The vnf category is used as the related-to value to
indicate that the relationship being establish is to a Virtual Network
Function of unknown type.  The vnf-id happens to be unique for all
services across all nodes in the graph.  By providing vnf.vnf-id with
a specific value, AAI can look at all VNFs in the graph and find the
appropriate vertex.  Note that this only applies to PUTs.

Category vnf is used for node types of vce, vpe, and generic-vnf.

If an attempt is made to add a relationship to a node that doesn't
exist (e.g., from a vserver to a vnf, and the vnf doesn't exist), a
unique message Id (3003) will be returned with a specific error code
(ERR.5.4.6129).  Arguments will tell the client which node type was
missing (e.g., vnf) and the key data for that node type (vnf.vnf-id).

Single relationships can be PUT to the graph in the following way:

.. code::

 https://{serverRoot}/{namespace}/{resource}/relationship-list/relationship

or

.. code::

 https://aai/v10/cloud-infrastructure/pservers/pserver/pserver-123456789-01/p-interfaces/p-interface/p-interface-name-123456789-01/l-interfaces/l-interface/l-interface-name-123456789-01/relationship-list/relationship

with a payload containing the relationship information.

AAI will accept and give preference to the related-link URI

XML

.. code-block:: xml

   <relationship xmlns="http://org.onap.aai.inventory/vX">
     <related-link>*/aai/v10/network/logical-links/logical-link/logical-link-123456789-01*</related-link>
     <related-to>logical-link</related-to>
     <relationship-data>
       <relationship-key>logical-link.link-name</relationship-key>
       <relationship-value>logical-link-123456789-01</relationship-value>
     </relationship-data>
   </relationship>

JSON

.. code-block:: json

   {
       "related-link": " /aai/v10/network/logical-links/logical-link/logical-link-123456789-01",
       "related-to": "logical-link",
       "relationship-data": [
	   {
	       "relationship-key": "logical-link.link-name",
	       "relationship-value": " logical-link-123456789-01"
	   }
       ]
   }

Health Check API
----------------

The util domain is where AAI locates utility functions.  There is
currently one utility function, echo, which serves as a ping test that
authenticated authorized clients can call to ensure there is
connectivity with AAI.

The URL for the echo utility is:

.. code::

   https://aai.onap:8443/aai/util/echo

If the response is unsuccessful, an error will be returned following
the standard format.

The successful payload returns the X-FromAppId and X-TransactionId
sent by the client.

Successful XML Response Payload
-------------------------------

.. code-block:: xml

   <Info>
      <responseMessages>
	 <responseMessage>
	    <messageId>INF0001</messageId>
	    <text>Success X-FromAppId=%1 X-TransactionId=%2 (msg=%3) (rc=%4)</text>
	    <variables>
	       <variable>CCW</variable>
	       <variable>CCW33335</variable>
	       <variable>Successful health check:OK</variable>
	       <variable>0.0.0002</variable>
	    </variables>
	 </responseMessage>
      </responseMessages>
   </Info>

Successful JSON Response Payload
--------------------------------

.. code-block:: json

   {
       "responseMessages": {
	   "responseMessage": [
	       {
		   "messageId": "INF0001",
		   "text": "Success X-FromAppId=%1 X-TransactionId=%2 (msg=%3) (rc=%4)",
		   "variables": {
		       "variable": [
			   "CCW",
			   "CCW33335",
			   "Successful health check:OK",
			   "0.0.0002"
		       ]
		   }
	       }
	   ]
       }
   }

AAI Resources CRUD APIs
=======================

The API structure is composed of: 

  * The HTTP command, which indicates the operation to perform 
  * The HTTP URI, which defines what object this operation is related to 
  * The HTTP version, which MUST be 1.1 

Available HTTP commands are: 

  * PUT: used to create or update an object 
  * DELETE: used to delete an object or a set of objects 
  * GET : used to query an object or set of objects
  * PATCH :  used to update specific fields owned by the client doing the update

The HTTP URI is built according to this pattern:

.. code::

   https://{serverRoot}/{namespace}/{resource}

* {serverRoot} refers to the server base url: hostname+port+base path+version. Port and base path are OPTIONAL but AAI will use port 8443 and base path aai. Note that the base path may change before production, so please make this configurable.  Versions will change as releases are made.

* {namespace} refers to the API namespace. Supported namespaces are cloud-infrastructure, business, service-design-and-creation, and network

* {resource} refers to how the object is identified according to the namespace specifications. 

Example GET Request

.. code::

   GET https://aai.onap:8443/aai /v11/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}

Swagger and XSD:
----------------

`Offered APIs <../platform/offeredapis.html>`_

Data Assumptions
----------------

Given AAI is largely a correlation engine among disparate inventory
types, AAI will for the most part accept values as they are sent,
without validating the format or value of the input.  **It is
incumbent upon the source of truth to provide valid information to
AAI.**

Clients should either use the PATCH API (section 7.5) to only change
the attribute values they mean to change, or do a GET prior to a PUT
and change only the data that they mean to affect.

The PUT REST APIs expect the payload passed to replace the resource in
AAI.  **A GET before PUT is vital in our concurrency scheme.  The
client will be returned an opaque value per entity which needs to be
returned back in the PUT. AAI will reject the PUT or DELETE if the
opaque value doesn't match what AAI has stored for that entity.**

If an attribute has been added to a model in vN+1, and a GET/PUT of a
vN resource is done, AAI should not affect the new attribute (i.e., it
should be left unchanged).

Concurrency Control
-------------------

Concurrency control for AAI is in place.

* A client always gets a resource before updating through PUT or deleting it.

* All resource updates and deletions are done via the AAI REST APIs

* This solution will apply to PUT and DELETE operations.

* The resource-version attribute is now in every container

* The PATCH REST verb is not subject to concurrency control, because
  it is only intended to be used by clients who are the definitive
  source of truth for the attributes they are changing.  An update
  through the PATCH API will however reset the resource-version so
  clients using PUT and DELETE will not risk updating with stale data.
  If a client would like us to do concurrency control for PATCH, we
  will need a feature request.  PATCH is recommended for clients who
  know they are the definitive source of data, as there is less risk
  of destroying other data.

If you use PUT, you MUST send back the entire resource, not just the
pieces you know about.  This is best illustrated by example.  Note:
Specific interfaces only show you the data you are responsible for but
that does not mean that's all the data that the resource you GET will
contain.  You are responsible to overlay only your changes and leave
everything else untouched.

Imagine this is the existing resource:

.. code-block:: json

   {
       "node-id": "valueOfNodeId",
       "node-name": "valueOfNodeName",
       "prov-status": "NVTPROV",
       "relationship-list": {
	   "relationship": [
	       {
		   "related-link": " /aai/v10/network/generic-vnfs/generic-vnf/generic-vnf-20160902a",
		   "related-to": "generic-vnf",
		   "relationship-data": [
		       {
			   "relationship-key": "generic-vnf.vnf-id",
			   "relationship-value": "generic-vnf-20160902a"
		       }
		   ]
	       },
	       {
		   "related-link": " /aai/v10/network/generic-vnfs/generic-vnf/generic-vnf-20161010",
		   "related-to": "generic-vnf",
		   "relationship-data": [
		       {
			   "relationship-key": "generic-vnf.vnf-id",
			   "relationship-value": "generic-vnf-20161010"
		       }
		   ]
	       }
	   ]
       },
       "resource-version": "1474912794"
   }

And you want to update the name and add a relationship to an l3-network.

The payload you need to send back, if you choose PUT, is this.  The
node-name and the third relationship block is the new data, and the
other data and relationships previously existed and must still be PUT.

.. code-block:: json

   {
       "node-id": "valueOfNodeId",
       "node-name": "NEWvalueOfNodeName",
       "prov-status": "NVTPROV",
       "relationship-list": {
	   "relationship": [
	       {
		   "related-link": " /aai/v10/network/generic-vnfs/generic-vnf/generic-vnf-20160902a",
		   "related-to": "generic-vnf",
		   "relationship-data": [
		       {
			   "relationship-key": "generic-vnf.vnf-id",
			   "relationship-value": "generic-vnf-20160902a"
		       }
		   ]
	       },
	       {
		   "related-link": " /aai/v10/network/generic-vnfs/generic-vnf/generic-vnf-20161010",
		   "related-to": "generic-vnf",
		   "relationship-data": [
		       {
			   "relationship-key": "generic-vnf.vnf-id",
			   "relationship-value": "generic-vnf-20161010"
		       }
		   ]
	       },
	       {
		   "related-link": " /aai/v10/network/l3-networks/l3-network/network-name-for-me",
		   "related-to": "l3-network",
		   "relationship-data": [
		       {
			   "relationship-key": "l3-network.network-name",
			   "relationship-value": "network-name-for-me"
		       }
		   ]
	       }
	   ]
       },
       "resource-version": "1474912794"
   }

A Warning About PUT and Lists
-----------------------------

The PUT verb is used to both create and replace a resource.  A given
resource may have child resources (e.g., customers have service
subscriptions, generic-vnfs have vf-modules, tenants have vservers and
vservers have volumes).

The following convention will be followed:

  If a resource is replaced and there are no tags for children, the
  children that exist will be left alone.

  If a resource is replaced and there are tags for children, the
  children will be replaced by the list passed.  If the list is empty,
  then children will be deleted.

Note that the relationship list is a type of child resource.  The same
conventions are followed.  It is especially critical to ensure that
you do not send an incomplete relationship list and therefore remove
edges in the graph.  See `The Relationship-List`_ for more information on
relationship lists.  See `Concurrency Control`_ for an example of GET followed by
PUT containing the entire resource (i.e., overlaying your changes on
what already exists so that you don't wipe out other data).

PATCH
-----

To move towards industry standards and to make our APIs easier to use
by clients who own specific attributes and do not require AAI to
enforce concurrency control around them, the PATCH verb has been
introduced.

.. _RFC 7386: https://tools.ietf.org/html/rfc7386

- RFC Algorithm implemented JSON Merge PATCH: `RFC 7386`_
- HTTP Verb = PATCH
- Clients can send a POST with "X-HTTP-Method-Override" = "PATCH" and
  Content-Type = "application/merge-patch+json" to send a PATCH
  request to AAI.
- PATCH does not support XML
- PATCH does not require a resource version to preform these modifications
- Clients should only send what they wish to modify and whose value they "own"
- PATCH returns a 200 with no response body for success

Example:

.. code::

   PATCH  https://aai.onap:8443/aai/v10/network/generic-vnfs/generic-vnf/cscf0001v
   {
      "vnf-id": "cscf0001v", <-- This key needs to be here but you cannot modify the key
      "regional-resource-zone": null,
      "ipv4-oam-address": "10.10.99.11"   
   }

This payload would result in the generic-vnf with the vnf-id =
cscf0001v having ipv4-oam-address set to "10.10.99.11" and
regional-resource-zone having its value removed from the database.

Note: PATCH is used only to update attributes on a single node that
already exists in AAI.  That means it is not applicable to lists of
any type.

 * You do not manage relationships with PATCH.  There is a
   relationship API for that.

 * You cannot include child objects in a PATCH payload, i.e., you
   cannot PATCH an l3-network's attributes as well as supply some
   subnet children or their attributes within the same PATCH payload.
   You can GET/overlay/PUT parent/child payloads or you can PUT or
   PATCH each object individually with separate REST API calls.

Optional Query Parameters
-------------------------

A **depth** query parameter is available allowing a query to stop after it
has reached a certain point in the graph.  This allows clients to
minimize the data that is returned to them and make the queries more
performant. A depth=0 will return information of the node referred to
by the URI only without any information on the children.

Example

.. code::

   GET https://aai.onap:8443/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}?depth=0

A **nodes-only** parameter is available allowing a query to only
display the properties of the nodes being queried without any
relationship information. This allows clients to minimize data that is
returned to them and make the queries more performant.

Example

.. code::

   GET https://aai.onap:8443/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}?nodes-only

These parameters may be used in combination with each other.

Example 

.. code::

   GET https://aai.onap:8443/aai/v14/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}?depth=0&nodes-only

Delete Scope and Edges
----------------------

An attempt to remove a node which would result in a delete scope being
violated will return error 5.4.6110.

The swagger documentation has been updated to show information about
delete scope and edges.

Here is a subset of the generic-vnf definition that will be used to
demonstrate how the delete scope and edges are documented.

The following table summarizes actions AAI will take upon deletion of a resource, i.e., its default delete scope:

+-----------------------------+--------------------------------------------------------------------+
| ERROR_IF_ANY_EDGES          | If the resource being deleted has any edges at all                 | 
|                             | an error should be returned                                        | 
+-----------------------------+--------------------------------------------------------------------+
| ERROR_IF_ANY_IN_EDGES       | If the resource being deleted has any edges that point IN towards  |
|                             | it, an error should be returned                                    |
+-----------------------------+--------------------------------------------------------------------+
| THIS_NODE_ONLY              | Delete the vertex being requested by first deleting its edge to    |
|                             | other vertices, but do not delete the other vertices.  Note, the   |
|                             | delete will be rejected if the deletion target has DEPENDENT       |
|                             | children (e.g., tenants that have vservers)                        |
+-----------------------------+--------------------------------------------------------------------+
| CASCADE_TO_CHILDREN         | Cascade the delete through vertices who have a parentOf            |
|                             | relationship to the vertex being deleted, as long as the vertex is |
|                             | orphaned by the delete of its parent                               |
+-----------------------------+--------------------------------------------------------------------+
| ERROR_4_IN_EDGES_OR_CASCADE | Error if there are any in edges and, if not, cascade to            |
|                             | children                                                           |
+-----------------------------+--------------------------------------------------------------------+

Edge Documentation

* Node A is the object being defined - e.g.,  generic-vnf

* Node B is the XXX of OUT TO XXX

* Direction is always Node A OUT TO Node B.  Node A has requirement
  satisfied by Node B and the relationship is the edgelabel

* Multiplicity is listed on the OUT TO edges

* The former hasDelTarget is indicated by deletion statements that
  make it clear what gets deleted as a side effect of deleting
  something else.

* The former isChild is indicated by full statements


.. parsed-literal::

 *generic-vnf: object*
 *General purpose VNF*

 *Default Delete Scope*

 CASCADE_TO_CHILDREN

  * *OUT TO vnfc (org.onap.relationships.inventory.Uses, One2Many, delete of generic-vnf will delete vnfc)*
  * *IN FROM l-interface (l-interface child of generic-vnf)*
  * *IN FROM service-instance*

Server Timeout
--------------

A Server timeout is implemented for these APIs to make sure the server
did not continue processing the request long after a client times out
on their side. An error code ERR.5.4.7406 will be returned when this
limit is hit. The default value for Resources API is 60 secs. The
clients should set their timeouts accordingly.

Bulk APIs
---------

The Bulk API allows clients to make multiple requests in a single
transaction. Please look for additional details on the following wiki
page

AAI Traversal APIs
==================

Not all queries of the graph are purely GETs of a specific resource
and its related vertexes.  The following capabilities are available to
meet more advanced search needs.  Please contact the AAI team if you
need another search.

Node Queries
------------

The Nodes Query mechanism was implemented in support of searching the
pservers which do not have the ipv4-oam-ipaddress set.  It will allow
nodes to be searched based on certain properties of the nodes. It will
allow search based on properties being set, not set or set to specific
values.

Generic Queries
---------------

The Generic Query mechanism allows to search for certain nodes of
“include” node types at a specified “depth” from the from a particular
start node of type “start-node-type” identified by specifying its
“key” values

Model Based Query and Delete
----------------------------

AAI supports a search and delete capability that allows a client to
retrieve or delete an instance of a service based on the model
subgraph definition provided to AAI by ASDC.

The instance filters must uniquely identify a service instance.  

The URL is as follows:

.. code::

   https://{serverRoot}/aai/search/model[?action=DELETE]

.. code-block:: json

   {
       "query-parameters": {
	   "model": {
	       "model-invariant-id": "$modelInvariantId",
	       "model-vers": {
		   "model-ver": [
		       {
			   "model-version-id": "$modelVersionId"
		       }
		   ]
	       }
	   },
	   "instance-filters": {
	       "instance-filter": [
		   {
		       "customer": {
			   "global-customer-id": "$globalCustID"
		       },
		       "service-instance": {
			   "resource-version": "$resourceversionID",
			   "service-instance-id": "$serviceInstanceID"
		       },
		       "service-subscription": {
			   "service-type": "$serviceType"
		       }
		   }
	       ]
	   }
       }
   }

Named Query
-----------

These queries provide the ability to upload a json file describing the
inputs and designed output based on traversing the graph in a
particular way. Existing named queries are supported but will be
migrated to custom queries. **Named queries will be deprecated (no new
queries, just support for existing ones) in Dublin and clients will be
asked to migrate to use the custom queries instead.**

Custom Query
------------

This API provides AAI clients an API for complex data retrieval. To
execute a custom query, a client will perform an HTTP PUT request on
the query API and include a payload indicating the starting node and
the query to be run. While the client is performing a PUT request,
this is actually a data query and no data is created or changed.

Assumptions
~~~~~~~~~~~

+----------+-----------------------------+--------------------------+
| No.      | Assumption                  | Approach                 |
+==========+=============================+==========================+
| 1        | Assume that client will     |                          |
|          | not request large amounts   |                          |
|          | of data from AAI w/out      |                          |
|          | using secondary filters     |                          |
+----------+-----------------------------+--------------------------+

Depdendencies
~~~~~~~~~~~~~

Data has been PUT to AAI prior to the query.

Custom Query URI
~~~~~~~~~~~~~~~~

.. code::

   PUT /aai/v$/query?format={format}

Query Formats
~~~~~~~~~~~~~

The format determines what information is returned from the
query. Acceptable formats are: count, id, pathed, resource,
resource_and_url, or simple.

count
^^^^^

Provides an count of the objects returned in the query.

.. code::

   PUT /aai/v$/query?format=count

Example reponse

.. code-block:: json

   {
     "results": [
       {
	 "pnf": 4,
	 "p-interface": 5,
	 "l-interface": 3,
	 "pserver": 1
       }
     ]
   }

id 
^^^ 

Provides an array of objects containing resource-type (AAI's node
type; i.e., pnf) and a URI using the vertex ID from AAI's graph.

.. code::

   PUT /aai/v$/query?format=id

Example Response
   
.. code-block:: json

   {
     "results": [
       {
	 "resource-type": "complex",
	 "resource-link": "/aai/v1/resources/id/8159312"
       },
       {
	 "resource-type": "complex",
	 "resource-link": "/aai/v1/resources/id/389256"
       }
     ]
   }

pathed
^^^^^^

Provides an array of objects containing resource-type (AAIs node type;
i.e., pnf) and a URI using the AAI REST API pathed URIs

.. code::

   PUT /aai/v$/query?format=pathed

Example Response
   
.. code-block:: json

   {
     "results": [
       {
	 "resource-type": "complex",
	 "resource-link": "/aai/v1/cloud-infrastructure/complexes/complex/complex1"
       },
       {
	 "resource-type": "complex",
	 "resource-link": "/aai/v1/cloud-infrastructure/complexes/complex/complex1"
       }
     ]
   }

resource
^^^^^^^^

Provides each object in the results array in the same format as AAI's
REST API with depth = 1 (first level children and cousin
relationships).

.. code::

   PUT /aai/v$/query?format=resource


Example Response

.. code-block:: json

   {
       "results": [
	   {
	       "complex": {
		   "city": "Anywhere",
		   "complex-name": "complex-mccomplexface",
		   "country": "USA",
		   "data-center-code": "CHG",
		   "latitude": "30.123456",
		   "longitude": "-78.135344",
		   "physical-location-id": "complextest1",
		   "physical-location-type": "lab",
		   "postal-code": "90210",
		   "region": "West",
		   "relationship-list": {
		       "relationship": [
			   {
			       "related-link": "/aai/v1/network/zones/zone/zone1",
			       "related-to": "zone",
			       "related-to-property": [
				   {
				       "property-key": "zone.zone-name",
				       "property-value": "zone-name1"
				   }
			       ],
			       "relationship-data": [
				   {
				       "relationship-key": "zone.zone-id",
				       "relationship-value": "zone1"
				   }
			       ],
			       "relationship-label": "org.onap.relationships.inventory.LocatedIn"
			   },
			   {
			       "related-link": "/aai/v1/cloud-infrastructure/cloud-regions/cloud-region/Cloud-Region/Region1",
			       "related-to": "cloud-region",
			       "related-to-property": [
				   {
				       "property-key": "cloud-region.owner-defined-type"
				   }
			       ],
			       "relationship-data": [
				   {
				       "relationship-key": "cloud-region.cloud-owner",
				       "relationship-value": "Cloud-Region"
				   },
				   {
				       "relationship-key": "cloud-region.cloud-region-id",
				       "relationship-value": "Region1"
				   }
			       ],
			       "relationship-label": "org.onap.relationships.inventory.LocatedIn"
			   }
		       ]
		   },
		   "resource-version": "1531233769164",
		   "state": "CA",
		   "street1": "100 Main St",
		   "street2": "C3-3W03"
	       }
	   }
       ]
   }

resource_and_uri
^^^^^^^^^^^^^^^^

Provides each object in the results array in the same format as AAI’s
REST API with depth = 1 (first level children and cousin
relationships) plus the pathed url for the result object in AAI.

.. code::

   PUT /aai/v$/query?format=resource_and_url

Example Response

.. code-block:: json

  {
      "results": [
	  {
	      "complex": {
		  "city": "Anywhere",
		  "complex-name": "complex-mccomplexface",
		  "country": "USA",
		  "data-center-code": "CHG",
		  "latitude": "30.123456",
		  "longitude": "-78.135344",
		  "physical-location-id": "complextest1",
		  "physical-location-type": "lab",
		  "postal-code": "90210",
		  "region": "West",
		  "relationship-list": {
		      "relationship": [
			  {
			      "related-link": "/aai/v1/network/zones/zone/zone1",
			      "related-to": "zone",
			      "related-to-property": [
				  {
				      "property-key": "zone.zone-name",
				      "property-value": "zone-name1"
				  }
			      ],
			      "relationship-data": [
				  {
				      "relationship-key": "zone.zone-id",
				      "relationship-value": "zone1"
				  }
			      ],
			      "relationship-label": "org.onap.relationships.inventory.LocatedIn"
			  },
			  {
			      "related-link": "/aai/v1/cloud-infrastructure/cloud-regions/cloud-region/Cloud-Region/Region1",
			      "related-to": "cloud-region",
			      "related-to-property": [
				  {
				      "property-key": "cloud-region.owner-defined-type"
				  }
			      ],
			      "relationship-data": [
				  {
				      "relationship-key": "cloud-region.cloud-owner",
				      "relationship-value": "Cloud-REgion"
				  },
				  {
				      "relationship-key": "cloud-region.cloud-region-id",
				      "relationship-value": "Region1"
				  }
			      ],
			      "relationship-label": "org.onap.relationships.inventory.LocatedIn"
			  }
		      ]
		  },
		  "resource-version": "1531233769164",
		  "state": "CA",
		  "street1": "100 Main St",
		  "street2": "C3-3W03"
	      },
	      "url": "/aai/v11/cloud-infrastructure/complexes/complex/complextest1"
	  }
      ]
  }

simple
^^^^^^

Provides each result object in a simplified format. The node-type,
graph vertex id, pathed url, object properties, and directly related
objects in the graph are all returned. Both direct parent/child
objects and cousin objects are included in the related-to array.

.. code::

   PUT /aai/v$/query?format=simple

Example Response

.. code-block:: json

   {
       "results": [
	   {
	       "id": "81924184",
	       "node-type": "complex",
	       "properties": {
		   "city": "Anywhere",
		   "complex-name": "complex-mccomplexface",
		   "country": "USA",
		   "data-center-code": "CHG",
		   "latitude": "30.123456",
		   "longitude": "-78.135344",
		   "physical-location-id": "complextest1",
		   "physical-location-type": "lab",
		   "postal-code": "90210",
		   "region": "West",
		   "resource-version": "1531233769164",
		   "state": "CA",
		   "street1": "100 Main St",
		   "street2": "C3-3W03"
	       },
	       "related-to": [
		   {
		       "id": "40968400",
		       "node-type": "zone",
		       "relationship-label": "org.onap.relationships.inventory.LocatedIn",
		       "url": "/aai/v1/network/zones/zone/zone1"
		   },
		   {
		       "id": "122884184",
		       "node-type": "cloud-region",
		       "relationship-label": "org.onap.relationships.inventory.LocatedIn",
		       "url": "/aai/v1/cloud-infrastructure/cloud-regions/cloud-region/Cloud-Region/Region1"
		   },
		   {
		       "id": "122884296",
		       "node-type": "rack",
		       "relationship-label": "org.onap.relationships.inventory.LocatedIn",
		       "url": "/aai/v1/cloud-infrastructure/complexes/complex/complextest1/racks/rack/rackname1-1test"
		   }
	       ],
	       "url": "/aai/v1/cloud-infrastructure/complexes/complex/complextest1"
	   }
       ]
   }

graphson
^^^^^^^^

Provides the results using the graphson standard.

.. code::

   PUT /aai/v$/query?format=graphson

Example Response

.. code-block:: json

   {
       "results": [
	   {
	       "id": 81924184,
	       "inE": {
		   "org.onap.relationships.inventory.LocatedIn": [
		       {
			   "id": "oeioq-oe3f4-74l-1crx3s",
			   "outV": 40968400,
			   "properties": {
			       "aai-uuid": "9e75af3d-aa7f-4e8e-a7eb-32d8096f03cc",
			       "contains-other-v": "NONE",
			       "delete-other-v": "NONE",
			       "prevent-delete": "IN",
			       "private": false
			   }
		       },
		       {
			   "id": "216a6j-215u1k-74l-1crx3s",
			   "outV": 122884184,
			   "properties": {
			       "aai-uuid": "4b3693be-b399-4355-8747-4ea2bb298dff",
			       "contains-other-v": "NONE",
			       "delete-other-v": "NONE",
			       "prevent-delete": "IN",
			       "private": false
			   }
		       },
		       {
			   "id": "215xjt-215u4o-74l-1crx3s",
			   "outV": 122884296,
			   "properties": {
			       "aai-uuid": "958b8e10-6c42-4145-9cc1-76f50bb3e513",
			       "contains-other-v": "IN",
			       "delete-other-v": "IN",
			       "prevent-delete": "NONE",
			       "private": false
			   }
		       }
		   ]
	       },
	       "label": "vertex",
	       "properties": {
		   "aai-created-ts": [
		       {
			   "id": "1crvgr-1crx3s-6bk5",
			   "value": 1531231973518
		       }
		   ],
		   "aai-last-mod-ts": [
		       {
			   "id": "215vkb-1crx3s-6dxh",
			   "value": 1531233769164
		       }
		   ],
		   "aai-node-type": [
		       {
			   "id": "215urv-1crx3s-69z9",
			   "value": "complex"
		       }
		   ],
		   "aai-uri": [
		       {
			   "id": "1crxfv-1crx3s-6gat",
			   "value": "/cloud-infrastructure/complexes/complex/complextest1"
		       }
		   ],
		   "aai-uuid": [
		       {
			   "id": "1crvuz-1crx3s-1ybp",
			   "value": "3959ceca-3a89-4e92-a2ff-073b6f409303"
		       }
		   ],
		   "city": [
		       {
			   "id": "1cs0zv-1crx3s-4irp",
			   "value": "Middletown"
		       }
		   ],
		   "complex-name": [
		       {
			   "id": "215wcr-1crx3s-4d8l",
			   "value": "chcil"
		       }
		   ],
		   "country": [
		       {
			   "id": "1cs26j-1crx3s-4l51",
			   "value": "USA"
		       }
		   ],
		   "data-center-code": [
		       {
			   "id": "215ssr-1crx3s-4bnp",
			   "value": "CHG"
		       }
		   ],
		   "last-mod-source-of-truth": [
		       {
			   "id": "215vyj-1crx3s-696t",
			   "value": "aai-AppId"
		       }
		   ],
		   "latitude": [
		       {
			   "id": "1cs2yz-1crx3s-4mpx",
			   "value": "30.123456"
		       }
		   ],
		   "longitude": [
		       {
			   "id": "1cs3d7-1crx3s-4nid",
			   "value": "-74.135344"
		       }
		   ],
		   "physical-location-id": [
		       {
			   "id": "1crzez-1crx3s-4a2t",
			   "value": "complextest1"
		       }
		   ],
		   "physical-location-type": [
		       {
			   "id": "1crzt7-1crx3s-4ged",
			   "value": "nj-lab"
		       }
		   ],
		   "postal-code": [
		       {
			   "id": "1cs1sb-1crx3s-4kcl",
			   "value": "07748"
		       }
		   ],
		   "region": [
		       {
			   "id": "1cs2kr-1crx3s-4lxh",
			   "value": "Northeast"
		       }
		   ],
		   "resource-version": [
		       {
			   "id": "215v63-1crx3s-glh",
			   "value": "1531233769164"
		       }
		   ],
		   "source-of-truth": [
		       {
			   "id": "1crv2j-1crx3s-6epx",
			   "value": "rx2202"
		       }
		   ],
		   "state": [
		       {
			   "id": "1cs1e3-1crx3s-4jk5",
			   "value": "NJ"
		       }
		   ],
		   "street1": [
		       {
			   "id": "1cs07f-1crx3s-4h6t",
			   "value": "200 Laurel Av"
		       }
		   ],
		   "street2": [
		       {
			   "id": "1cs0ln-1crx3s-4hz9",
			   "value": "C3-3W03"
		       }
		   ]
	       }
	   }
       ]
   }
   
Optional Query Parameters
~~~~~~~~~~~~~~~~~~~~~~~~~

depth
^^^^^

You can pass the depth query parameter to specify how many levels of
children/grandchildren to return. The default depth is 1.

.. code::
  
   PUT /aai/v$/query?format={format}&depth=0

nodesOnly
^^^^^^^^^

You can pass the nodesOnly query parameter to have the output only
contain the object properties with no relationships.

.. code:: 

   PUT /aai/v$/query?format={format}&nodesOnly=true

subgraph
^^^^^^^^

You can pass a subgraph query parameter that determines the behavior
of the output.  Using subgraph=prune returns all of the objects from
the query and only the edges between those objects. Using
subgraph=star returns all of the objects from the query plus all of
the objects they relate to.

The default is subgraph=star

.. code::

   PUT /aai/v$/query?format={format}&subgraph={subgraph}

HTTP Headers
~~~~~~~~~~~~

+--------------------------+--------------------------------------------------------------------------------------+
|   X-FromAppID={client ID}| Unique application identifier.                                                       |
+--------------------------+--------------------------------------------------------------------------------------+
|  X-TransactionID={UUDID} | must be a UUID and unique to each transaction within the context of an X-FromAppID.  |
+--------------------------+--------------------------------------------------------------------------------------+
|  Content-Type={format}   | format of the request. Should be application/json or application/xml.                |
+--------------------------+--------------------------------------------------------------------------------------+
|  Accept={format}         | format of the response. Should be application/json or application/xml.               |
+--------------------------+--------------------------------------------------------------------------------------+

Request Payload
~~~~~~~~~~~~~~~

Typically the query payload will include both a "start" and a "query"
portion. The "start" can indicate one or more starting nodes in the
graph. If multiple nodes are specified, the result will contain the
query results for all of the start nodes. The "query" indicates the
name of the query to be run and also takes query parameters depending
on the query. Please reference the queries on the AAI wiki for
specific saved queries and how they should be usServer Timeout A
Server timeout is implemented for these APIs to make sure the server
did not continue processing the request long after a client times out
on their side. An error code ERR.5.4.7406 will be returned when this
limit is hit. The default value for Traversal API is 60 secs. The
clients should set their timeouts accordingly.

List of Queries and Payloads
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For a full list of available custom queries, please refer to our
`Custom Queries <customQueries.html>`_ document

