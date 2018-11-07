.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

====================
 AAI Custom Queries
====================

Overview
========

Before You Start!
-----------------

It's important that you engage the AAI team before using these
queries. We're are actively evolving our schema, queries, and other
things in AAI. Queries and query parameters may change or be removed
in the future. It's important that, at the very least, we know who is
using which queries so we can be cautious of changes in the
future. And we can help you find the best way to get the data you
need.

Getting Started with the Custom Query API
-----------------------------------------

To execute a custom query, a client will perform a PUT on the query
API and include a payload indicating the starting node and the query
to be run. While the example below is for v11, this can be called in
any version v11 or higher. The version dictates which release's REST
API version the output will be based on.

API URI
=======

.. code::

   PUT /aai/v$/query?format={format}

When calling the query API, the client must specify the output format
as a query string. The currently available output formats are below,
along with examples.


count
-----

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
--- 

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
------

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
--------

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
----------------

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
------

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
--------

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
			   "value": "Beverley Hills"
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
			   "value": "-174.135344"
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
			   "value": "lab"
		       }
		   ],
		   "postal-code": [
		       {
			   "id": "1cs1sb-1crx3s-4kcl",
			   "value": "90210"
		       }
		   ],
		   "region": [
		       {
			   "id": "1cs2kr-1crx3s-4lxh",
			   "value": "West"
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
			   "value": "foo"
		       }
		   ],
		   "state": [
		       {
			   "id": "1cs1e3-1crx3s-4jk5",
			   "value": "CA"
		       }
		   ],
		   "street1": [
		       {
			   "id": "1cs07f-1crx3s-4h6t",
			   "value": "100 Main St"
		       }
		   ],
		   "street2": [
		       {
			   "id": "1cs0ln-1crx3s-4hz9",
			   "value": "Room 101"
		       }
		   ]
	       }
	   }
       ]
   }

Optional Query Parameters
-------------------------

depth
~~~~~

You can pass the depth query parameter to specify how many levels of
children/grandchildren to return. The default depth is 1.

.. code::
  
   PUT /aai/v$/query?format={resource OR resource_and_url}}&depth=0

nodesOnly
~~~~~~~~~

You can pass the nodesOnly query parameter to have the output only
contain the object properties with no relationships.

.. code:: 

   PUT /aai/v$/query?format={format}&nodesOnly=true

subgraph
~~~~~~~~

You can pass a subgraph query parameter that determines the behavior
of the output.  Using subgraph=prune returns all of the objects from
the query and only the edges between those objects. Using
subgraph=star returns all of the objects from the query plus all of
the objects they relate to.

The default is subgraph=star

.. code::

   PUT /aai/v$/query?format={format}&subgraph={subgraph}

Payload
-------

Typically the query payload will include both a "start" and a "query"
portion. The "start" can indicate one or more starting nodes in the
graph. If multiple nodes are specified, the result will contain the
query results for all of the start nodes. The "query" indicates the
name of the query to be run and also takes query parameters depending
on the query. Please reference the page for each specific saved query
for how it should be used, but keep in mind that any URI can be used
in the start parameter as long as it provides the same object
types. **Note: The start URI must adhere to standard percent-encoding
rules to properly account for special characters.**

.. code-block:: json

   {
     "start" : ["{namespace}/{resource}"],
     "query" : "query/{query-name}"
   }

There also the option to pass a "start" to the query API with no
specified query. This will return the input node(s) in the format
requested.

.. code-block:: json

   {
       "start" : ["{namespace}/{resource}"]
   }

Switching to Custom Query from Named Query
==========================================

You can find the custom query intended to replace the named query you
are using by searching this page (ctrl + f) for either the named query
name or the named query uuid. Suggested query parameters to use to
receive output in the closest format to the named query output are
also provided.

Available Queries
=================

access-service-fromServiceInstance
----------------------------------

The "**access-service-fromServiceInstance**" query allows a client to
provide A&AI a global-customer-id a service-type for a
service-subscription, and a service-instance-id to retrieve
service-subscription, customer, forwarding-path, configuration, evc,
forwarder, forwarder-evc, p-interface, pnf, lag-interface, and
logical-link of link-type LAG.

availabilityZoneAndComplex-fromCloudRegion
------------------------------------------

  The "**availabilityZoneAndComplex-fromCloudRegion**" query allows a client
  to provide A&AI a cloud-owner and cloud-region-id to retrieve the
  availability-zones and complex.

cloud-region-and-source-FromConfiguration
-----------------------------------------

  The "**cloud-region-and-source-FromConfiguration**" query allows a client
  to provide A&AI with a configuration-id and retrieve the source
  cloud-region and source vnf..  Query needs to be submitted using
  format=simple&nodesOnly=true

cloudRegion-fromCountry
-----------------------

  The "cloudRegion-fromCountry" query allows a client to provide A&AI
  with a country and retrieve all appropriate cloud-regions.

cloudRegion-fromCountryCloudRegionVersion
-----------------------------------------

  The "**cloudRegion-fromCountryCloudRegionVersion**" query allows a client
  to provide A&AI with a country code and cloud-region-version and
  returns the appropriate cloud-regions.

cloudRegion-fromNfType
----------------------

  The "cloudRegion-fromNfType" query allows a client to provide A&AI
  with an nf-type and returns the cloud-regions running those vnfs.

cloudRegion-fromNfTypeVendorVersion
-----------------------------------

  The "**cloudRegion-fromNfTypeVendorVersion**" query allows a client to
  provide A&AI with an nf-type, application-vendor, and optional
  application-version and retrieve the cloud-regions.

cloud-region-fromVnf
--------------------

  The "**cloud-region-fromVnf**" query allows a client to provide A&AI
  with a vnf-id and retrieves the tenant, cloud-region, and cloud-owner.

cloud-region-sites
------------------

  The "**cloud-region-sites**" query allows a client to provide A&AI
  with a cloud-owner and retrieves the cloud-regions having that owner
  and all of the complexes containing those cloud-regions.

cluster-topology
----------------

  The "**cluster-topology**" query allows a client to quickly retrieve
  the topology of a given cluster/pnf.

colocated-devices
-----------------

  The "**colocated-devices**" query allows a client to provide A&AI a
  physical server and retrieves all other physical devices in the same
  location along with details on their physical interfaces and links.

complex-fromVnf
---------------

  The "**complex-fromVnf**" query allows a client to provide A&AI a vnf
  name or ID to retrieve the generic-vnf, pserver, complex, licenses,
  and entitlements.

count-vnf-byVnfType
-------------------

  The "**count-vnf-byVnfType**" query allows a client to get a list of
  the number of generic-vnfs for each vnf type.  Format must be set to
  "console", otherwise no data will be displayed.

destination-FromConfiguration
-----------------------------

  The "**destination-FromConfiguration**" query allows a client to
  provide A&AI with a configuration-id and retrieve the destination vnf
  or pnf..  Query needs to be submitted using
  format=simple&nodesOnly=true

fabric-information-fromVnf
--------------------------

  The fabric-information-fromVnf query will retrieve fabric information
  for a given VNF.

fn-topology
-----------

  The "**fn-topology**" query allows a client to provide A&AI
  service-instance-id or line-of-business-name then return vnf, vnfc,
  vserver, pserver, pnf.

generic-vnfFromModelbyRegion
----------------------------

  The "**generic-vnfFromModelbyRegion**" query allows a client to
  provide A&AI with a global-customer-id, service-type, model
  parameters, and cloud-region-id and retrieves the related
  generic-vnfs.

getComplexByPNFName
-------------------

  The "**getComplexByPnfName**" query allows a client to provide A&AI a
  PNF and retrieve the PNF details and its location.  This query is
  meant to replace the named query "getComplexByPnfName", which had
  named query uuid "d27ccfea-7098-42d7-a4cd-bbddb37bf205". The format
  closest to the original query can be achieved with
  ?format=resource&depth=0&nodesOnly=true

getComplexFromHostname
----------------------

  The "**getComplexFromHostname**" query allows a client to provide A&AI
  a pserver and retrieve the pserver details and its location.  This
  query is meant to replace the named query "dhv-complex-by-hostname",
  which had named query uuid "670a94e9-874f-4087-8501-62d4d289c519". The
  format closest to the original query can be achieved with
  ?format=simple

getCustomerVPNBondingServiceDetails
-----------------------------------

  The "**getCustomerVPNBondingServiceDetails**" query that takes
  customer (customer.global-customer-id) and service-type as input and
  return customer VPN Bonding service details.

getDHVLogicalLink
-----------------

  The "**getDHVLogicalLink**" query allows a client to provide A&AI a
  VNF and retrieve its interface and link details.  This query is meant
  to replace the named query "logical-link-by-vnf-name", which had named
  query uuid "47e5e7c7-719e-45af-b96f-0c15fa0691b9". The format closest
  to the original query can be achieved with
  ?format=simple&nodes-only=true

getL3networkCloudRegionByNetworkRole
------------------------------------

  The "**getL3networkCloudRegionByNetworkRole**" query allows a client
  to provide A&AI a Network Role value and retrieve all L3 networks,
  their connected VNFs, VMs, Tenants and Cloud Regions.  This query is
  meant to replace the named query
  "l3network-cloud-region-by-network-role", which had named query uuid
  "96e54642-c0e1-4aa2-af53-e37c623b8d01". The format closest to the
  original query can be achieved with
  ?format=simple&depth=0&nodesOnly=true

getLogicalLinkByCloudRegionId
-----------------------------

  The "**getLogicalLinkByCloudRegionId**" query allows a client to
  provide A&AI a Cloud Region and retrieve its Links in that region.
  This query is meant to replace the named query
  "getLogicalLinkByCloudRegionId", which had named query uuid
  "25096aa7-bc97-4ece-8a81-41dd28cd0f7d". The format closest to the
  original query can be achieved with
  ?format=simple&depth=0&nodesOnly=true

getNetworks
-----------

  The getNetworks query will retrieve l3-networks for a given
  network-role, cloud-region and owning-entity

getNetworksByServiceInstance
----------------------------

  The "**getNetworksByServiceInstance**" query allows a client to return
  provider networks with associated vlan-tags and tenant networks with
  associated vlan-tags by service-instance-id.

getPinterfacePhysicalLinkBySvcInstId
------------------------------------

  The "**getPinterfacePhysicalLinkBySvcInstId**" query allows a client
  to provide A&AI a Service Instance and retrieves the related VNFs,
  VMs, Physical Server(s), Physical Interfaces and Links.  This query is
  meant to replace the named query
  "pinterface-physical-link-by-service-instance-id", which had named
  query uuid "75d55786-200b-49fd-92d7-1393e755d693". The format closest
  to the original query can be achieved with
  ?format=resource&depth=0&nodesOnly=true

getRouterRoadmTailSummary
-------------------------

  The "**getRouterRoadmTailSummary**" query allows a client to provide
  A&AI a PNF and retrieve its Physical Interfaces, Links, related PNFs
  and their Physical Interfaces, Service Instance(s), Service
  Subscriptions and Customers.  This query is meant to replace the named
  query "GetRouterRoadmTailSummary", which had named query uuid
  "cbf22b8a-f29a-4b9b-a466-a878095b258a". The format closest to the
  original query can be achieved with ?format=resource_and_url&depth=0

getServiceTopology
------------------

  The "**getServiceTopology**" query allows a client to provide A&AI
  with a service-instance and retrieve the generic-vnfs, vlans,
  vservers, l-interfaces, pservers, complexes, and
  allotted-resources. It then finds any service-instances attached to
  the allotted-resources and retrieves the above values for those
  service-instances except for pservers, complexes, and
  allotted-resources. The client must provide a path to the
  service-instance from customer and service-subscription.  This query
  is meant to replace the named query "dhv-service-topology-2", which
  had named query uuid "09236f18-a9d2-4468-9086-464b8385b706". The
  format closest to this original query can be found with
  format=simple&depth=0&nodesOnly=true

getSvcSubscriberModelInfo
-------------------------

  The "**getSvcSubscriberModelInfo**" query allows a client to provide
  A&AI a service-instance or a list of service-instances and retrieve
  the human readable model name and model version.  This query is meant
  to replace the named query "get-service-instance-model-info", which
  had named query uuid "6e806bc2-8f9b-4534-bb68-be91267ff6c8".

getVNFVpnBondingServiceDetails
------------------------------

  The "**getVNFVpnBondingServiceDetails**" query that takes customer
  (customer.global-customer-id) and service-type as input and return
  customer VPN Bonding service details.

images-fromCloudRegionNfType
----------------------------

  The "**images-fromCloudRegionNfType**" query allows a client to
  provide A&AI with a cloud-region-id and nf-type and retrieve all
  related images.

instance-groups-byCloudRegion
-----------------------------

  The "**instance-groups-byCloudRegion**" query allows the user to get
  all instance-groups by cloud-region-id and filter by instance-group
  type/role/function.


ips-networks-fromVnf
--------------------

  The "**ips-networks-fromVnf**" query allows a client to provide A&AI
  one or more VNFs and retrieve various data all associated VIP and
  fixed IPs and their related networks.

l3-networks-by-cloud-region-network-role
----------------------------------------

  The "**l3-networks-by-cloud-region-network-role**" query retrieves
  l3-networks for a given cloud-region-id, tenant.tenant-id (Optional)
  and network-role.


linked-devices
--------------

  The "**linked-devices**" query allows a client to provide A&AI a
  generic-vnf, vserver, or newvce and retrieve all connected
  generic-vnfs, vservers, and newvces.

locationNetTypeNetRole-fromCloudRegion
--------------------------------------

  The "**locationNetTypeNetRole-fromCloudRegion**" query allows a client
  to provide A&AI with a cloud-region-id and returns the cloud-region,
  complex, and l3-networks.

network-collection-ByServiceInstance
------------------------------------

  The "**network-collection-ByServiceInstance**" query returns the
  service-instance and associated collection, instance-group and
  associated l3-networks for a given service-instance-id.

network-name-fromNetwork-role
-----------------------------

  The "** network-name-fromNetwork-role**" query allows a client to
  provide A&AI with a cloud-owner and cloud-region-id and retrieves the
  related l3-networks and network-policies.

nfType-fromCloudRegion
----------------------

  The "**nfType-fromCloudRegion**" query allows a client to provide A&AI
  with a cloud-region-id and returns a list of all generic-vnfs with an
  nf-type.

owning-entity-fromService-instance
----------------------------------

  The "**owning-entity-fromService-instance**" query allows a client to
  provide A&AI with a service-instance-id and retrieves the
  owning-entity.

pending-topology-detail
-----------------------

  The "**pending-topology-detail**" query allows a client to provide
  A&AI a generic as input and returns the generic-vnf, platform(s),
  line(s)-of-business, owning-entity, project, vnfc(s), vnfc ip
  address(es), vip ip addresses subnet(s), and l3-networks.

pnf-fromModel-byRegion
----------------------

  The "**pnf-fromModel-byRegion**" query allows a client to provide A&AI
  with a cloud-region, equip-vendor, equip-model, model-invariant-id of
  service-instance, model-version-id of service-instance and retrieves
  the pnf.

pnf-topology
------------

  The "**pnf-topology**" query allows a client to provide A&AI a D1
  Device, using the hostname, and retrieve data related to that device
  and its connected uCPE and/or other D1 device. This includes data
  about the D1 device itself (the pnf, and location) as well as about a
  connected uCPE (the pserver, interfaces and physical links used for
  the connection) and/or other D1 device (the pnf, interfaces and
  physical links used for the connection).

pserver-fromConfiguration
-------------------------

  The "**pserver-fromConfiguration**" query allows a client to provide
  A&AI with a configuration-id and retrieves the configuration and
  related l-interfaces, pservers, and generic-vnfs.

pserver-fromConfigurationFilterInterfaceId
------------------------------------------

  The "**pserver-fromConfigurationFilterInterfaceId**" query allows a
  client to provide A&AI with a configuration-id and interface-id and
  retrieves the configuration, l-interface, and related pservers, and
  generic-vnfs.

pservers-fromVnf
----------------

  The "**pservers-fromVnf**" query allows a client to provide A&AI a vNF
  and retrieve all of the pservers hosting that vNF.

pservers-withNoComplex
----------------------

  The "**pservers-withNoComplex**" query allows a client to get a list
  of pservers that have no edge to any complex.  Format must be set to
  "console", otherwise no data will be displayed.

related-to
----------

  The "**related-to**" query allows a client to provide A&AI any
  starting node and request all related nodes of a requested node-type.

service-fromPserverandSubsName
------------------------------

  The "**service-fromPServerandSubsName**" query allows a client to
  provide A&AI a hostname and subscriber name, then return service
  instance and service subscription information.

serviceModels-byDistributionStatus
----------------------------------

  The "**serviceModels-byDistributionStatus**" query allows a client to
  provide A&AI with a distribution-status and optional
  model-invariant-id and retrieve the model and model-ver.

service-sites
-------------

  The "**service-sites**" query allows a client to provide A&AI a
  service type and a customer id to retrieve the service-instances,
  cloud regions, generic-vnfs, and complexes.

service-topology
----------------

  The "**service-topology**" query allows a client to provide A&AI with
  a service-instance and retrieve the generic-vnfs, connected tenants,
  vservers, vnfcs, pservers, and their interfaces.  This query is
  intended to use with format=resource_and_url and depth=0, using the
  node urls to identify parent-child relationships.

site-l3network-cloudRegion
--------------------------

  The "**site-l3network-cloudRegion**" query allows a client to provide
  A&AI with a physical-location-id and returns the network-role,
  country, cloud-region-id and cloud-region-version in that location.

sites-byCloudRegionId
---------------------

  The "**sites-byCloudRegionId**" query allows a client to provide A&AI
  with a cloud-region-id and an optional cloud-region-version and
  returns the appropriate complexes.

sites-byCountryFilterCloudRegionId
----------------------------------

  The "**sites-byCountryFilterCloudRegionId**" query allows a client to
  provide A&AI with a 3-digit country code and cloud-region-id to
  retrieve the appropriate complexes.

sites-byCountryFilterCloudRegionVer
-----------------------------------

  The "**sites-byCountryFilterCloudRegionVer**" query allows a client to
  provide A&AI with a 3-digit country code and cloud-region-version
  number to retrieve the appropriate complexes.

so-request-vfModule
-------------------

  The "**so-request-vfModule**" query allows a client to provide A&AI a
  vf-module then return all the reference objects needed to send SO an
  orchestration request.

spaas-topology-fromServiceInstance
----------------------------------

  The "**spaas-topology-fromServiceInstance**" query allows a client to
  provide A&AI global-custom-id and service-type, then return vertical
  topology for overlay and underlay information.

topology-detail
---------------

  The "**topology-detail**" query allows a client to provide A&AI a
  generic-vnf as input and returns the generic-vnf, platform(s),
  line(s)-of-business, owning-entity, project, vnfc(s), vserver(s),
  vserver l-interface(s), ip address(es), subnet(s), l3-networks,
  cloud-region and complex.

topology-detail-fromVnf
-----------------------

  The "**topology-detail-fromVnf**" query allows a client to provide
  A&AI with a service-id of a VNF and retrieve various data related to
  that VNF. This includes data about the VNF itself (the generic-vnf),
  the related vnfc, the related vserver (along with the tenant,
  cloud-region, image and flavor) and the related pserver (along with
  the complex) as done in the topology-summary query. In addition, this
  query returns availability-zone, service-instance, l-interface,
  l3-interface-ipv4-address-list, l3-interface-ipv6-address-list, and
  volume-group.

topology-detail-fromVserver
---------------------------

  The "**topology-detail-fromVserver**" query allows a client to provide
  A&AI a vserver as input and returns the generic-vnf, platform(s),
  line(s)-of-business, owning-entity, project, vnfc(s), vserver(s),
  vserver l-interface(s), ip address(es), subnet(s), l3-networks,
  cloud-region and complex. Updated in 1806 to return the following
  additional objects: pserver, availability-zone, tenant, image, flavor,
  virtual-data-center, vf-module, and volume-group.

topology-fromCloudRegionIdandServiceId
--------------------------------------

  The "**topology-fromCloudRegionIdandServiceId**" query allows a client
  to provide A&AI cloud-owner, cloud-region-id and service-id, then
  return topology related to the service id.

topology-summary
----------------

  The "**topology-summary**" query allows a client to provide A&AI one
  or more VNFs and retrieve various data related to that VNF. This
  includes data about the VNF itself (the generic-vnf), the related
  vnfc, the related vserver (along with the tenant, cloud-region, image
  and flavor) and the related pserver (along with the complex).

topology-summary-fromCloudRegion
--------------------------------

  The "**topology-summary-fromCloudRegion**" query allows a client to
  provide A&AI a cloud region and retrieve a summary of the topology
  within that cloud region including the tenants, VMs, VNFs and physical
  servers.

topology-summary-fromTenant
---------------------------

  The "**topology-summary-fromTenant**" query allows a client to provide
  A&AI a tenant and retrieve a summary of the topology within that
  tenant including VMs, VNFs and physical servers and the containing
  cloud region.

ucpe-instance
-------------

  The "**ucpe-instance**" query allows a client to provide A&AI a
  physical server or physical network device, using the hostname, and
  retrieve the device and the complex it is located in. This includes
  the pserver or pnf itself and the complex.

ucpe-topology
-------------

  The "**ucpe-topology**" query allows a client to provide A&AI a uCPE
  physical server, using the hostname, and retrieve various data related
  to that uCPE. This includes data about the uCPE itself (the pserver,
  location, interfaces, hosted vnfs, service instances, service
  subscriptions and customer) as well as about a connected physical D1
  device (the pnf, interfaces and physical links).

vfModule-fromServiceInstance
----------------------------

  The "**vfModule-fromServiceInstance**" query allows a client to
  provide A&AI a service-instance-id to retrieve vf-module only.
 
vnf-instances-fromServiceInstancebyModelVersion
-----------------------------------------------

  The "**vnf-instances-fromServiceInstancebyModelVersion**" query allows
  a client to provide A&AI a list of service-instances for a customer
  and service-type and return the generic-vnfs using a particular
  model-version-id.

vnfs-fromPserver
----------------

  The "**vnfs-fromPserver**" query allows a client to provide A&AI with
  a pserver hostname and retrieve the generic-vnfs related to it. This
  query also supports pre-filtering the vnf results.

vnfs-fromServiceInstance
------------------------

  The "**vnfs-fromServiceInstance**" query allows a client to provide
  A&AI a service-instance and retrieve the related VNFs.

vnfs-vlans-fromServiceInstance
------------------------------

  The "**vnfs-vlans-fromServiceInstance**" query allows a client to
  provide A&AI a service-instance id, then return associated vnfs and
  corresponding VLAN ID assignment information for each VNF that is
  associated to the VNF.

vnf-topology-fromServiceInstance
--------------------------------

  The "**vnf-topology-fromServiceInstance**" query allows a client to
  provide A&AI a service-instance and retrieve much of the topology
  related to it. The related VNF, vservers and pserver, along with any
  IP addresses and l3-networks on the VNF or vserver, the
  service-instance and allotted-resource, the tenant and cloud region.

vnf-topology-fromVfModule
-------------------------

  The "**vnf-topology-fromVfModule**" query allows a client to provide
  A&AI a vf-module and retrieve much of the topology related to it. The
  related VNF, vservers and pserver, along with any IP addresses and
  l3-networks on the VNF or vserver, the service-instance and
  allotted-resource, the tenant and cloud region.

vnf-topology-fromVnf
--------------------

  The "**vnf-topology-fromVnf**" query allows a client to provide A&AI a
  generic-vnf and retrieve much of the topology related to it. The
  related VNF, vservers and pserver, along with any IP addresses and
  l3-networks on the VNF or vserver, the service-instance and
  allotted-resource, the tenant and cloud region.

vnf-to-service-instance
-----------------------

  The "**vnf-to-service-instance**" query allows a client to provide
  A&AI a VNF and retrieve the related Service Instance and ALL VNFs
  within that instance.  This query is meant to replace the named query
  "vnf-to-service-instance", which had named query uuid
  "a93ac487-409c-4e8c-9e5f-334ae8f99087".

vserver-fromInstanceGroup
-------------------------

  The "**vserver-fromInstanceGroup**" query allows a client to provide
  A&AI a instance-group.id to retrieve VNF and vserver information.

vserver-fromVnf
---------------

  The "**vserver-fromVnf**" query allows a client to provide A&AI with a
  vnf-id and nfc-function of the vnfc and retrieves the vserver, vnfc,
  and l-interface.

vserverlogicallink-frompServer
------------------------------

  The "**vserverlogicallink-frompServer**" query allows a client to
  provide A&AI a hostname, then return logical link of vserver from the
  compute node.

vservers-fromPserver-tree
-------------------------

  The "**vservers-fromPserver-tree**" query allows a client to provide
  A&AI one or more pservers and retrieve each pserver with the vservers
  it hosts nested under it in the output.
