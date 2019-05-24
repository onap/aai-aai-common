.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

AAI Generic Query Implementation Notes

Overview
========

The Generic Query mechanism allows to search for certain nodes of
“include” node types at a specified “depth” from the from a particular
start node of type “start-node-type” identified by specifying its
“key” values.

The Generic Query is implemented using the GET method with the
following URL that takes 4 query params:

.. code-block::

   /aai/<version>/search/generic-query

   Ex. /aai/v16/search/generic-query

Please look for other AAI documentation for what version to use in
which environment/release.

**key** - multiple values that together specify the params to select a
 unique start node. For example for selecting a customer we would
 specify
 
.. code-block::

    key=customer.global-customer-id:ma9181-203-customerid&key=service-subscription.service-type:example-service-type

**start-node-type** - node type of the start node. For example
 start-node-type=service-instance

**include** - multiple values of the node types that need to be
 returned as part of the query-result. For example

.. code-block::

    include=vce&include=port-group

**depth** – look for include node types within a certain depth from
 the start-node-type

The queries return a search-results object back which includes a list
of result-data which contains the node-type and a link for each
resource that was found.   

Requirements
============

* At depth=0, only the start node is returned, include is not needed

* At any depth > 0 and <= 6, If start-node-type is in the include –
  that should also be returned

* The depth has a max value of 6 – an error will be returned if a
  value higher than 6 is used

* A special node type of “all” can be specified for include when all
  nodes under the start node are to be searched for

* If a start-node cannot be found based on the key a HTTP response
  code of 404 Node not found error is returned

* If no nodes can be found that meet the search criteria a HTTP
  response code of 200 with an empty list is returned

* The search results can be asked to be returned in json or xml based
  on the ACCEPT header. For 1504, the queries are expected to be used
  by MSO and therefore will use an ACCEPT header of application/xml.

Supported Queries
=================
 
Search customer and service-subscription by service instance id 

.. code-block::

   URL:

   /aai/v16/search/generic-query?key=service-instance.service-instance-id:testserviceinstance&start-node-type=service-instance&include=customer&include=service-subscription&include=service-instance&depth=2

Search result:

.. code-block:: xml

 <search-results xmlns="http://com.att.aai.inventory/v16">
   <result-data>
      <resource-type>customer</resource-type>
      <resource-link>https://mtinjvmsdn30.cip.att.com:8443/aai/v16/business/customers/customer/globalspanos/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-subscription</resource-type>
      <resource-link>https://mtinjvmsdn30.cip.att.com:8443/aai/v16/business/customers/customer/globalspanos/service-subscriptions/service-subscription/ptplgamma/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-instance</resource-type>
      <resource-link>https://mtinjvmsdn30.cip.att.com:8443/aai/v16/business/customers/customer/globalspanos/service-subscriptions/service-subscription/ptplgamma/service-instances/service-instance/arnoldave/</resource-link>
   </result-data>
 </search-results>

Search the VCE and its port groups and cvlan tags given the service instance id.

.. code-block::

   URL: /aai/v16/search/generic-query?key=service-instance.service-instance-id:arnoldave&start-node-type=service-instance&include=vce&include=port-group&include=cvlan-tag&depth=3

Search result:

.. code-block:: xml

 <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc2/cvlan-tags/cvlan-tag/333/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc1/cvlan-tags/cvlan-tag/333/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc1/cvlan-tags/cvlan-tag/111/</resource-link>
   </result-data>
   <result-data>
      <resource-type>port-group</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc1/</resource-link>
   </result-data>
   <result-data>
      <resource-type>vce</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc2/cvlan-tags/cvlan-tag/222/</resource-link>
   </result-data>
   <result-data>
      <resource-type>port-group</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/spanosvce/port-groups/port-group/spanosifc2/</resource-link>
   </result-data>
 </search-results>

Customer GET based on tenant id 

.. code-block::

   URL:

   /aai/v16/search/generic-query?key=tenant.tenant-id:spanospizzatenant&start-node-type=tenant&include=tenant&include=customer&include=service-subscription&depth=2

Search Result:

.. code-block:: xml

 <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>customer</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-subscription</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/service-subscriptions/service-subscription/ptplgamma/</resource-link>
   </result-data>
   <result-data>
      <resource-type>tenant</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/exampletenant/</resource-link>
   </result-data>
 </search-results>

Search the tenant given a customer and service subscription.  

.. code-block::

   URL:

   /aai/v16/search/generic-query?key=customer.global-customer-id:example-customer&key=service-subscription.service-type:example-service-type&start-node-type=service-subscription&include=tenant&include=service-subscription&depth=1

Search Result:

.. code-block::

  <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>service-subscription</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/service-subscriptions/service-subscription/ptplgamma/</resource-link>
   </result-data>
   <result-data>
      <resource-type>tenant</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/spanospizzatenant/</resource-link>
   </result-data>
 </search-results>

For a service Instance - get its resource-link given the service-instance-id 

.. code-block::

   URL:

   /aai/v16/search/generic-query?key=service-instance.service-instance-id:exampleservice&start-node-type=service-instance&depth=0

Search Result:

.. code-block:: xml

 <search-results xmlns="http://org.onap.aai.inventory/v4">
   <result-data>
      <resource-type>service-instance</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/service-subscriptions/service-subscription/ptplgamma/service-instances/service-instance/arnoldave/</resource-link>
   </result-data>
 </search-results>

Get service-instance and service-subscription from vce

.. code-block:: 

   URL:
   /aai/v16/search/generic-query?key=vce.vnf-id:spanosvce&start-node-type=vce&include=service-instance&include=service-subscription&depth=2

Search Result:

.. code-block:: xml

 <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>service-instance</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/service-subscriptions/service-subscription/ptplgamma/service-instances/service-instance/arnoldave/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-subscription</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/examplecustomer/service-subscriptions/service-subscription/ptplgamma/</resource-link>
   </result-data>
 </search-results>

Get all the nodes for a customer

.. code-block::

   URL:

   /aai/v16/search/generic-query?key=customer.global-customer-id:examplecustomer&start-node-type=customer&include=all&depth=6

Search Result:

.. code-block:: xml

 <search-results xmlns="http://org.onap.aai.inventory/v16">
   <result-data>
      <resource-type>service-capability</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/service-design-and-creation/service-capabilities/service-capability/ptplgamma/ptplbrocade-vce/</resource-link>
   </result-data>
   <result-data>
      <resource-type>vserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/examplepizzatenant/vservers/vserver/ccwvm1/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc2/cvlan-tags/cvlan-tag/333/</resource-link>
   </result-data>
   <result-data>
      <resource-type>oam-network</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/oam-networks/oam-network/examplentwk/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc1/cvlan-tags/cvlan-tag/333/</resource-link>
   </result-data>
   <result-data>
      <resource-type>dvs-switch</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/dvs-switches/dvs-switch/dvsswitch-id1/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc1/cvlan-tags/cvlan-tag/111/</resource-link>
   </result-data>
   <result-data>
      <resource-type>customer</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/globalexample/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-subscription</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/globalexample/service-subscriptions/service-subscription/ptplgamma/</resource-link>
   </result-data>
   <result-data>
      <resource-type>port-group</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc1/</resource-link>
   </result-data>
   <result-data>
      <resource-type>tenant</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/examplepizzatenant/</resource-link>
   </result-data>
   <result-data>
      <resource-type>service-instance</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/business/customers/customer/globalexample/service-subscriptions/service-subscription/ptplgamma/service-instances/service-instance/arnoldave/</resource-link>
   </result-data>
   <result-data>
      <resource-type>pserver</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/pservers/pserver/ptpnj101snd/</resource-link>
   </result-data>
   <result-data>
      <resource-type>availability-zone</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/availability-zones/availability-zone/ptplaz1/</resource-link>
   </result-data>
   <result-data>
      <resource-type>vce</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/</resource-link>
   </result-data>
   <result-data>
      <resource-type>image</resource-type>
      <resource-link>https://aai.onap:8443/aai/v1/cloud-infrastructure/images/image/valueOfImageId/</resource-link>
   </result-data>
   <result-data>
      <resource-type>cvlan-tag</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc2/cvlan-tags/cvlan-tag/222/</resource-link>
   </result-data>
   <result-data>
      <resource-type>port-group</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/network/vces/vce/examplevce/port-groups/port-group/exampleifc2/</resource-link>
   </result-data>
   <result-data>
      <resource-type>ipaddress</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/examplepizzatenant/vservers/vserver/ccwvm1/ipaddresses/ipaddress/10.10.10.5/guid of port or interface/</resource-link>
   </result-data>
   <result-data>
      <resource-type>flavor</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/flavors/flavor/valueOfFlavorId/</resource-link>
   </result-data>
   <result-data>
      <resource-type>ipaddress</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/tenants/tenant/examplepizzatenant/vservers/vserver/ccwvm1/ipaddresses/ipaddress/10.10.10.4/guid of port or interface/</resource-link>
   </result-data>
   <result-data>
      <resource-type>complex</resource-type>
      <resource-link>https://aai.onap:8443/aai/v16/cloud-infrastructure/complexes/complex/PTPLNJ08742/</resource-link>
   </result-data>
 </search-results>


 
