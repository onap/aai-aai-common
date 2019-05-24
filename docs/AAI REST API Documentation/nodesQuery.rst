.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Nodes Query
===========

A&AI Nodes Query Implementation Notes:	

Overview
--------

AAI continues to support this API, but clients may find that `Custom
Queries <customQueries.html>`_ meet the needs more most queries.

The Nodes Query mechanism is mostly being implemented in support of
searching the pservers which do not have the ipv4-oam-ipaddress
set. It will allow nodes to be searched based on certain properties of
the nodes. It will allow search based on properties being set, not set
or set to specific values.

The Nodes Query API is implemented using the GET method with the following URL:

.. code::

   /aai/<version>/search/nodes-query

   ex. /aai/v16/search/nodes-query

New version numbers will be supported and older versions eventually
retired. Please look for other AAI documentation for what version to
use in which environment/release.

The URL expects the following URL parameters:

**search-node-type** - node type of the node to be searched. 

**filter** – list of properties that specify the search
criterion. Format will be

.. code::

 filter=<property-name>:<EQUALS|DOES-NOT-EQUAL|EXISTS|DOES-NOT-EXIST>:<property-value>
 
 such as

 filter=ipv4-oam-address:DOES-NOT-EXIST:

For EXISTS and DOES-NOT-EXIST the property value is not specified
(second colon is included). Multiple filter criteria may be specified.

The queries return a search-results object back which includes a list
of result-data which contains the node-type and a link for each
resource that was found. 


Requirements
------------

* If the search-node-type is not supported by the application, a HTTP
  response code of 400 and Parameter passed error is returned

* If no nodes can be found that meet the search criteria a HTTP
  response code of 200 with an empty list is returned

* The search results can be asked to be returned in json or xml based
  on the ACCEPT header.
  
* If no filter params are specified, it would return all nodes of that node type.

Design
------

* REST GET api and dbmap classes added to process the search payload via a GET

* New method searchForNodes() created in dbgen:DbSearch that does the
  search based on the node type and the filter list and returns the
  SearchResults object

  - The search does not require the properties used in the search to
    be indexed

  - The filterParams will just be properties of the node itself.  A
    future version could have another parameter with some more
    interesting search stuff – we’ll see what other queries are being
    asked for and what makes sense to treat like this.

  - As other requests come in, this query mechanism may be re-used if
    the requirements are able to fit this.

Supported queries
-----------------

* Search pserver nodes for which ipv4-oam-address DOES-NOT-EXIST 

.. code::

 URL:
 /aai/v4/search/nodes-query?search-node-type=pserver&filter=ipv4-oam-address:DOES-NOT-EXIST:
 
Search result

.. code::

 <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>pserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v4/cloud-infrastructure/pservers/pserver/mygreatpserver</resource-link>
   </result-data>
   <result-data>
      <resource-type>pserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v4/cloud-infrastructure/pservers/pserver/myothergreatpserver/</resource-link>
   </result-data>
   <result-data>
      <resource-type>pserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v4/cloud-infrastructure/pservers/pserver/stillanothergreatpserver</resource-link>
   </result-data>
   <result-data>
      <resource-type>pserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v4/cloud-infrastructure/pservers/pserver/testbestestpserver</resource-link>
   </result-data>
 </search-results>
