Active and Available Inventory REST API v13
===========================================

.. toctree::
    :maxdepth: 3


Description
~~~~~~~~~~~


[Differences versus the previous schema version](apidocs/aai_swagger_v13.diff)

Copyright &copy; 2017 AT&amp;T Intellectual Property. All rights reserved.

Licensed under the Creative Commons License, Attribution 4.0 Intl. (the &quot;License&quot;); you may not use this documentation except in compliance with the License.

You may obtain a copy of the License at

(https://creativecommons.org/licenses/by/4.0/)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an &quot;AS IS&quot; BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

ECOMP and OpenECOMP are trademarks and service marks of AT&amp;T Intellectual Property.

This document is best viewed with Firefox or Chrome. Nodes can be found by appending /#/definitions/node-type-to-find to the path to this document. Edge definitions can be found with the node definitions.




Contact Information
~~~~~~~~~~~~~~~~~~~


None



None



None




License
~~~~~~~


`Apache 2.0 <http://www.apache.org/licenses/LICENSE-2.0.html>`_




Base URL
~~~~~~~~

https://None/aai/v13

NETWORK
~~~~~~~




PATCH ``/network/configurations/configuration/{configuration-id}``
------------------------------------------------------------------


Summary
+++++++

update an existing configuration

Description
+++++++++++

.. raw:: html

    Update an existing configuration
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        configuration-id | path | Yes | string |  |  | UUID assigned to configuration.


Request
+++++++



.. _d_ecdcd4e52dd9916ee05ece1e631a86ce:

Body
^^^^

Generic configuration object.
###### Related Nodes
- TO allotted-resource( configuration Uses allotted-resource, ONE2ONE, will delete target node)(2)
- TO logical-link( configuration Uses logical-link, ONE2MANY, will delete target node)(2)
- TO l-interface( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- TO pnf( configuration AppliesTo pnf, ONE2MANY, will delete target node)
- TO configuration( configuration BindsTo configuration, ONE2ONE, will delete target node)
- TO vpn-binding( configuration Uses vpn-binding, MANY2ONE, will delete target node)
- TO generic-vnf( configuration PartOf generic-vnf, MANY2ONE, will delete target node)
- TO vlan( configuration PartOf vlan, ONE2ONE, will delete target node)
- TO l3-network( configuration PartOf l3-network, ONE2ONE, will delete target node)
- TO service-instance( configuration BelongsTo service-instance, MANY2ONE, will delete target node)(4)
- TO pnf( configuration AppliesTo pnf, MANY2MANY, will delete target node)
- FROM metadatum( metadatum BelongsTo configuration, MANY2ONE, will delete target node)(1)
- FROM generic-vnf( generic-vnf Uses configuration, ONE2MANY, will delete target node)(3)
- FROM service-instance( service-instance Uses configuration, ONE2MANY, will delete target node)
- FROM forwarder( forwarder Uses configuration, ONE2ONE, will delete target node)(3)
- FROM forwarding-path( forwarding-path Uses configuration, ONE2ONE, will delete target node)(3)
- FROM evc( evc BelongsTo configuration, ONE2ONE, will delete target node)(1)
- FROM forwarder-evc( forwarder-evc BelongsTo configuration, ONE2ONE, will delete target node)(1)
- FROM service-instance( service-instance Uses configuration, MANY2MANY, will delete target node)
- FROM configuration( configuration BindsTo configuration, ONE2ONE, will delete target node)

-(1) IF this CONFIGURATION node is deleted, this FROM node is DELETED also
-(2) IF this CONFIGURATION node is deleted, this TO node is DELETED also
-(3) IF this FROM node is deleted, this CONFIGURATION is DELETED also
-(4) IF this TO node is deleted, this CONFIGURATION is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        configuration-id | Yes | string |  |  | UUID assigned to configuration.
        configuration-name | No | string |  |  | Name of the configuration.
        configuration-selflink | No | string |  |  | URL to endpoint where AAI can get more details from SDN-GC.
        configuration-sub-type | Yes | string |  |  | vprobe, pprobe.
        configuration-type | Yes | string |  |  | port-mirroring-configuration.
        management-option | No | string |  |  | Indicates the entity that will manage this feature. Could be an organization or the name of the application as well.
        model-customization-id | No | string |  |  | id of  the configuration used to customize the resource
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational.
        orchestration-status | No | string |  |  | Orchestration status of the configuration.
        tunnel-bandwidth | No | string |  |  | DHV Site Effective Bandwidth
        vendor-allowed-max-bandwidth | No | string |  |  | Velocloud Nominal Throughput - VNT

.. code-block:: javascript

    {
        "configuration-id": "somestring",
        "configuration-name": "somestring",
        "configuration-selflink": "somestring",
        "configuration-sub-type": "somestring",
        "configuration-type": "somestring",
        "management-option": "somestring",
        "model-customization-id": "somestring",
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "operational-status": "somestring",
        "orchestration-status": "somestring",
        "tunnel-bandwidth": "somestring",
        "vendor-allowed-max-bandwidth": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/configurations/configuration/{configuration-id}/evcs/evc/{evc-id}``
------------------------------------------------------------------------------------


Summary
+++++++

update an existing evc

Description
+++++++++++

.. raw:: html

    Update an existing evc
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        configuration-id | path | Yes | string |  |  | UUID assigned to configuration.
        evc-id | path | Yes | string |  |  | Unique/key field for the evc object


Request
+++++++



.. _d_22d29437390b7e26bddbd7c902189913:

Body
^^^^

evc object is an optional child object of the Configuration object.
###### Related Nodes
- TO configuration( evc BelongsTo configuration, ONE2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this EVC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cir-units | No | string |  |  | CIR units
        cir-value | No | string |  |  | Commited Information Rate
        collector-pop-clli | No | string |  |  | Collector POP CLLI (from the hostname of the access pnf)
        connection-diversity-group-id | No | string |  |  | Diversity Group ID
        esp-evc-cir-units | No | string |  |  | CIR units (For ESP)
        esp-evc-cir-value | No | string |  |  | Committed Information Rate (For ESP)
        esp-evc-circuit-id | No | string |  |  | EVC Circuit ID of ESP EVC
        esp-itu-code | No | string |  |  | Identifies ESP
        evc-id | Yes | string |  |  | Unique/key field for the evc object
        forwarding-path-topology | No | string |  |  | Point-to-Point, Multi-Point
        inter-connect-type-ingress | No | string |  |  | Interconnect type on ingress side of EVC.
        service-hours | No | string |  |  | formerly Performance Group
        tagmode-access-egress | No | string |  |  | tagMode for network side of EVC
        tagmode-access-ingress | No | string |  |  | tagode for collector side of EVC

.. code-block:: javascript

    {
        "cir-units": "somestring",
        "cir-value": "somestring",
        "collector-pop-clli": "somestring",
        "connection-diversity-group-id": "somestring",
        "esp-evc-cir-units": "somestring",
        "esp-evc-cir-value": "somestring",
        "esp-evc-circuit-id": "somestring",
        "esp-itu-code": "somestring",
        "evc-id": "somestring",
        "forwarding-path-topology": "somestring",
        "inter-connect-type-ingress": "somestring",
        "service-hours": "somestring",
        "tagmode-access-egress": "somestring",
        "tagmode-access-ingress": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/configurations/configuration/{configuration-id}/forwarder-evcs/forwarder-evc/{forwarder-evc-id}``
------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing forwarder-evc

Description
+++++++++++

.. raw:: html

    Update an existing forwarder-evc
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        configuration-id | path | Yes | string |  |  | UUID assigned to configuration.
        forwarder-evc-id | path | Yes | string |  |  | Key for forwarder-evc object


Request
+++++++



.. _d_859dedf919dad05968c2a6ca2ea302a8:

Body
^^^^

forwarder object is an optional child object of the Configuration object.
###### Related Nodes
- TO configuration( forwarder-evc BelongsTo configuration, ONE2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this FORWARDER-EVC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        circuit-id | No | string |  |  | Circuit ID from customer/ESP/ingress end of EVC, or reference to beater circuit on gateway/network/egress end of EVC
        cvlan | No | string |  |  | CVLAN value for ingress of egress forwarder.
        forwarder-evc-id | Yes | string |  |  | Key for forwarder-evc object
        ivlan | No | string |  |  | Internal VLAN.
        svlan | No | string |  |  | SVLAN value for ingress of egress forwarder.

.. code-block:: javascript

    {
        "circuit-id": "somestring",
        "cvlan": "somestring",
        "forwarder-evc-id": "somestring",
        "ivlan": "somestring",
        "svlan": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/configurations/configuration/{configuration-id}/metadata/metadatum/{metaname}``
------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing metadatum

Description
+++++++++++

.. raw:: html

    Update an existing metadatum
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        configuration-id | path | Yes | string |  |  | UUID assigned to configuration.
        metaname | path | Yes | string |  |  | 


Request
+++++++



.. _d_86c5a7078292838659223f545f7cca0a:

Body
^^^^

Key/value pairs
###### Related Nodes
- TO configuration( metadatum BelongsTo configuration, MANY2ONE, will delete target node)(4)
- TO connector( metadatum BelongsTo connector, MANY2ONE, will delete target node)(4)
- TO image( metadatum BelongsTo image, MANY2ONE, will delete target node)(4)
- TO model-ver( metadatum BelongsTo model-ver, MANY2ONE, will delete target node)(4)
- TO service-instance( metadatum BelongsTo service-instance, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this METADATUM is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metaname | Yes | string |  |  | 
        metaval | Yes | string |  |  | 

.. code-block:: javascript

    {
        "metaname": "somestring",
        "metaval": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/forwarding-paths/forwarding-path/{forwarding-path-id}``
------------------------------------------------------------------------


Summary
+++++++

update an existing forwarding-path

Description
+++++++++++

.. raw:: html

    Update an existing forwarding-path
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        forwarding-path-id | path | Yes | string |  |  | Unique ID of this FP


Request
+++++++



.. _d_46de7db8148c637b12aed1c8966df252:

Body
^^^^

Entity that describes the sequenced forwarding path between interfaces of services or resources
###### Related Nodes
- TO service-instance( forwarding-path AppliesTo service-instance, MANY2ONE, will delete target node)(4)
- TO configuration( forwarding-path Uses configuration, ONE2ONE, will delete target node)(2)
- FROM forwarder( forwarder BelongsTo forwarding-path, MANY2ONE, will delete target node)(1)

-(1) IF this FORWARDING-PATH node is deleted, this FROM node is DELETED also
-(2) IF this FORWARDING-PATH node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this FORWARDING-PATH is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarding-path-id | Yes | string |  |  | Unique ID of this FP
        forwarding-path-name | Yes | string |  |  | Name of the FP

.. code-block:: javascript

    {
        "forwarding-path-id": "somestring",
        "forwarding-path-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/forwarding-paths/forwarding-path/{forwarding-path-id}/forwarders/forwarder/{sequence}``
--------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing forwarder

Description
+++++++++++

.. raw:: html

    Update an existing forwarder
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        forwarding-path-id | path | Yes | string |  |  | Unique ID of this FP
        sequence | path | Yes | integer | int32 |  | Unique ID of this segmentation


Request
+++++++



.. _d_d4f446d151f5db0d4b9703506f700b79:

Body
^^^^

Entity describing a sequenced segment of forwarding path
###### Related Nodes
- TO forwarding-path( forwarder BelongsTo forwarding-path, MANY2ONE, will delete target node)(4)
- TO l-interface( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- TO configuration( forwarder Uses configuration, ONE2ONE, will delete target node)(2)
- TO lag-interface( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)
- TO p-interface( forwarder ForwardsTo p-interface, MANY2MANY, will delete target node)

-(2) IF this FORWARDER node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this FORWARDER is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarder-role | No | string |  |  | ingress, intermediate, egress
        sequence | Yes | integer | int32 |  | Unique ID of this segmentation

.. code-block:: javascript

    {
        "forwarder-role": "somestring",
        "sequence": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}``
----------------------------------------------------


Summary
+++++++

update an existing generic-vnf

Description
+++++++++++

.. raw:: html

    Update an existing generic-vnf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.


Request
+++++++



.. _d_ca0376d58aad84ab5c5935fa4c191551:

Body
^^^^

General purpose VNF
###### Related Nodes
- TO availability-zone( generic-vnf Uses availability-zone, MANY2MANY, will delete target node)
- TO complex( generic-vnf LocatedIn complex, MANY2MANY, will delete target node)
- TO configuration( generic-vnf Uses configuration, ONE2MANY, will delete target node)(2)
- TO ctag-pool( generic-vnf Uses ctag-pool, MANY2MANY, will delete target node)
- TO instance-group( generic-vnf MemberOf instance-group, MANY2MANY, will delete target node)
- TO ipsec-configuration( generic-vnf Uses ipsec-configuration, MANY2ONE, will delete target node)
- TO l3-network( generic-vnf Uses l3-network, MANY2MANY, will delete target node)
- TO pnf( generic-vnf HostedOn pnf, MANY2MANY, will delete target node)
- TO pserver( generic-vnf HostedOn pserver, MANY2MANY, will delete target node)
- TO vnf-image( generic-vnf Uses vnf-image, MANY2ONE, will delete target node)
- TO volume-group( generic-vnf DependsOn volume-group, ONE2MANY, will delete target node)
- TO vserver( generic-vnf HostedOn vserver, ONE2MANY, will delete target node)
- TO virtual-data-center( generic-vnf LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- TO model-ver( generic-vnf IsA model-ver, Many2One, will delete target node)
- TO nos-server( generic-vnf HostedOn nos-server, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource PartOf generic-vnf, MANY2MANY, will delete target node)
- FROM entitlement( entitlement BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM vnfc( vnfc BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM lag-interface( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM license( license BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM l-interface( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM network-profile( network-profile AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf generic-vnf, ONE2MANY, will delete target node)
- FROM site-pair-set( site-pair-set AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM vf-module( vf-module BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM line-of-business( line-of-business Uses generic-vnf, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo generic-vnf, MANY2MANY, will delete target node)
- FROM platform( platform Uses generic-vnf, MANY2MANY, will delete target node)
- FROM configuration( configuration PartOf generic-vnf, MANY2ONE, will delete target node)

-(1) IF this GENERIC-VNF node is deleted, this FROM node is DELETED also
-(2) IF this GENERIC-VNF node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this GENERIC-VNF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Client should send valid enumerated value
        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance, managed by MSO
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        ipv4-loopback0-address | No | string |  |  | v4 Loopback0 address
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by generic-vnf.
        is-closed-loop-disabled | Yes | boolean |  |  | used to indicate whether closed loop function is enabled on this node
        license-key | No | string |  |  | OBSOLETE -  do not use
        management-option | No | string |  |  | identifier of managed by ATT or customer
        management-v6-address | No | string |  |  | v6 management address
        mso-catalog-key | No | string |  |  | Corresponds to the SDN-C catalog id used to configure this VCE
        nm-lan-v6-address | No | string |  |  | v6 Loopback address
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational.  Valid values are in-service-path and out-of-service-path.
        orchestration-status | No | string |  |  | Orchestration status of this VNF, used by MSO.
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        regional-resource-zone | No | string |  |  | Regional way of organizing pservers, source of truth should define values
        service-id | No | string |  |  | Unique identifier of service, does not necessarily map to ASDC service models.  SOON TO BE DEPRECATED
        vcpu | No | integer | int64 |  | number of vcpus ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only by uCPE
        vcpu-units | No | string |  |  | units associated with vcpu, used for VNFs with no vservers/flavors, to be used only by uCPE
        vdisk | No | integer | int64 |  | number of vdisks ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only uCPE
        vdisk-units | No | string |  |  | units associated with vdisk, used for VNFs with no vservers/flavors, to be used only by uCPE
        vmemory | No | integer | int64 |  | number of GB of memory ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only by uCPE
        vmemory-units | No | string |  |  | units associated with vmemory, used for VNFs with no vservers/flavors, to be used only by uCPE
        vnf-id | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. code-block:: javascript

    {
        "equipment-role": "somestring",
        "heat-stack-id": "somestring",
        "in-maint": true,
        "ipv4-loopback0-address": "somestring",
        "ipv4-oam-address": "somestring",
        "is-closed-loop-disabled": true,
        "license-key": "somestring",
        "management-option": "somestring",
        "management-v6-address": "somestring",
        "mso-catalog-key": "somestring",
        "nm-lan-v6-address": "somestring",
        "operational-status": "somestring",
        "orchestration-status": "somestring",
        "prov-status": "somestring",
        "regional-resource-zone": "somestring",
        "service-id": "somestring",
        "vcpu": 1,
        "vcpu-units": "somestring",
        "vdisk": 1,
        "vdisk-units": "somestring",
        "vmemory": 1,
        "vmemory-units": "somestring",
        "vnf-id": "somestring",
        "vnf-name": "somestring",
        "vnf-name2": "somestring",
        "vnf-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/entitlements/entitlement/{group-uuid}/{resource-uuid}``
----------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing entitlement

Description
+++++++++++

.. raw:: html

    Update an existing entitlement
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        group-uuid | path | Yes | string |  |  | Unique ID for the entitlement group the resource comes from, should be uuid.
        resource-uuid | path | Yes | string |  |  | Unique ID of an entitlement resource.


Request
+++++++



.. _d_d1848f6a2192289cc1b9ccfec6de8fa8:

Body
^^^^

Metadata for entitlement group.
###### Related Nodes
- TO generic-vnf( entitlement BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( entitlement BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this ENTITLEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the entitlement group the resource comes from, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of an entitlement resource.

.. code-block:: javascript

    {
        "group-uuid": "somestring",
        "resource-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}``
----------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
--------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
--------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}``
--------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing lag-interface

Description
+++++++++++

.. raw:: html

    Update an existing lag-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface


Request
+++++++



.. _d_f61b51fbced469e399bfd4eae4c3ea5d:

Body
^^^^

Link aggregate interface
###### Related Nodes
- TO generic-vnf( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-link( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(2)
- TO logical-link( lag-interface Uses logical-link, MANY2MANY, will delete target node)(2)
- TO p-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- TO l-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- TO pnf( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM l-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)

-(1) IF this LAG-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this LAG-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this LAG-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-name | Yes | string |  |  | Name that identifies the link aggregate interface

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
--------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/licenses/license/{group-uuid}/{resource-uuid}``
--------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing license

Description
+++++++++++

.. raw:: html

    Update an existing license
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        group-uuid | path | Yes | string |  |  | Unique ID for the license group the resource belongs to, should be uuid.
        resource-uuid | path | Yes | string |  |  | Unique ID of a license resource.


Request
+++++++



.. _d_25a47ee6575bfa6d53284f5bf0598b55:

Body
^^^^

Metadata for license group.
###### Related Nodes
- TO generic-vnf( license BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( license BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this LICENSE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the license group the resource belongs to, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of a license resource.

.. code-block:: javascript

    {
        "group-uuid": "somestring",
        "resource-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/generic-vnfs/generic-vnf/{vnf-id}/vf-modules/vf-module/{vf-module-id}``
----------------------------------------------------------------------------------------


Summary
+++++++

update an existing vf-module

Description
+++++++++++

.. raw:: html

    Update an existing vf-module
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        vf-module-id | path | Yes | string |  |  | Unique ID of vf-module.


Request
+++++++



.. _d_127ac1ef6709b25c11c618a1545574af:

Body
^^^^

a deployment unit of VNFCs
###### Related Nodes
- TO generic-vnf( vf-module BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO l3-network( vf-module DependsOn l3-network, MANY2MANY, will delete target node)
- TO vnfc( vf-module Uses vnfc, ONE2MANY, will delete target node)
- TO volume-group( vf-module Uses volume-group, ONE2ONE, will delete target node)
- TO vserver( vf-module Uses vserver, ONE2MANY, will delete target node)
- TO model-ver( vf-module IsA model-ver, Many2One, will delete target node)

-(4) IF this TO node is deleted, this VF-MODULE is DELETED also
-VF-MODULE cannot be deleted if related to VNFC


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance.
        is-base-vf-module | Yes | boolean |  |  | used to indicate whether or not this object is base vf module
        orchestration-status | No | string |  |  | orchestration status of this vf-module, mastered by MSO
        vf-module-id | Yes | string |  |  | Unique ID of vf-module.
        vf-module-name | No | string |  |  | Name of vf-module

.. code-block:: javascript

    {
        "heat-stack-id": "somestring",
        "is-base-vf-module": true,
        "orchestration-status": "somestring",
        "vf-module-id": "somestring",
        "vf-module-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/instance-groups/instance-group/{id}``
------------------------------------------------------


Summary
+++++++

update an existing instance-group

Description
+++++++++++

.. raw:: html

    Update an existing instance-group
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        id | path | Yes | string |  |  | Instance Group ID, UUID assigned to this instance.


Request
+++++++



.. _d_35920a66c0a09f477c6b314db2a4cca6:

Body
^^^^

General mechanism for grouping instances
###### Related Nodes
- TO model( instance-group Targets model, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource MemberOf instance-group, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-network( l3-network MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l-interface( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- FROM pnf( pnf MemberOf instance-group, MANY2MANY, will delete target node)
- FROM service-instance( service-instance MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vip-ipv4-address-list( vip-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vnfc( vnfc MemberOf instance-group, MANY2MANY, will delete target node)
- FROM tenant( tenant MemberOf instance-group, ONE2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        description | Yes | string |  |  | Descriptive text to help identify the usage of this instance-group
        id | Yes | string |  |  | Instance Group ID, UUID assigned to this instance.
        instance-group-role | No | string |  |  | role of the instance group.
        model-invariant-id | No | string |  |  | ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | ASDC model version uid for this resource model.
        sub-type | No | string |  |  | Valid values for ha type are [geo-activeactive, geo-activestandby, local-activeactive, local-activestandby]
        type | Yes | string |  |  | Only valid value today is lower case ha for high availability

.. code-block:: javascript

    {
        "description": "somestring",
        "id": "somestring",
        "instance-group-role": "somestring",
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "sub-type": "somestring",
        "type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/ipsec-configurations/ipsec-configuration/{ipsec-configuration-id}``
------------------------------------------------------------------------------------


Summary
+++++++

update an existing ipsec-configuration

Description
+++++++++++

.. raw:: html

    Update an existing ipsec-configuration
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        ipsec-configuration-id | path | Yes | string |  |  | UUID of this configuration


Request
+++++++



.. _d_ce8b615a6d9f822fd95b8fe73cce678b:

Body
^^^^

IPSec configuration node will contain various configuration data for the NMTE VNF. This node will have an edge to the generic-vnf (vnf type = TE). Starting 1607, this data will be populated by SDN-C
###### Related Nodes
- FROM generic-vnf( generic-vnf Uses ipsec-configuration, MANY2ONE, will delete target node)
- FROM vig-server( vig-server BelongsTo ipsec-configuration, MANY2ONE, will delete target node)(1)

-(1) IF this IPSEC-CONFIGURATION node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dpd-frequency | No | string |  |  | Maximum number of DPD before claiming the tunnel is down
        dpd-interval | No | string |  |  | The time between DPD probe
        ike-version | No | string |  |  | can be 1 or 2
        ikev1-am-group-id | No | string |  |  | Group name defined in VIG for clients using aggressive mode
        ikev1-am-password | No | string |  |  | pre-shared key for the above group name
        ikev1-authentication | No | string |  |  | Contains values like md5, sha1, sha256, sha384
        ikev1-dh-group | No | string |  |  | Diffie-Hellman group like DH-GROUP2, DH-GROUP5, DH-GROUP14
        ikev1-encryption | No | string |  |  | Encyption values like 3des-cbc, des-cbc, aes-128-cbc,Â aes-192-cbc, aes-265-cbc
        ikev1-sa-lifetime | No | string |  |  | Lifetime for IKEv1 SA
        ipsec-authentication | No | string |  |  | md5, sha1, sha256, sha384
        ipsec-configuration-id | Yes | string |  |  | UUID of this configuration
        ipsec-encryption | No | string |  |  | 3des-cbc, des-cbc, aes-128-cbc,Â aes-192-cbc, aes-265-cbc
        ipsec-pfs | No | string |  |  | enable PFS or not
        ipsec-sa-lifetime | No | string |  |  | Life time for IPSec SA
        requested-customer-name | No | string |  |  | If the DMZ is a custom DMZ, this field will indicate the customer information
        requested-dmz-type | No | string |  |  | ATT can offer a shared DMZ or a DMZ specific to a customer
        requested-encryption-strength | No | string |  |  | Encryption values like 3des-cbc, des-cbc, aes-128-cbc, aes-192-cbc, aes-265-cbc
        requested-vig-address-type | No | string |  |  | Indicate the type of VIG server like AVPN, INTERNET, BOTH
        shared-dmz-network-address | No | string |  |  | Network address of shared DMZ
        xauth-user-password | No | string |  |  | Encrypted using the Juniper $9$ algorithm
        xauth-userid | No | string |  |  | user ID for xAuth, sm-user,ucpeHostName,nmteHostName

.. code-block:: javascript

    {
        "dpd-frequency": "somestring",
        "dpd-interval": "somestring",
        "ike-version": "somestring",
        "ikev1-am-group-id": "somestring",
        "ikev1-am-password": "somestring",
        "ikev1-authentication": "somestring",
        "ikev1-dh-group": "somestring",
        "ikev1-encryption": "somestring",
        "ikev1-sa-lifetime": "somestring",
        "ipsec-authentication": "somestring",
        "ipsec-configuration-id": "somestring",
        "ipsec-encryption": "somestring",
        "ipsec-pfs": "somestring",
        "ipsec-sa-lifetime": "somestring",
        "requested-customer-name": "somestring",
        "requested-dmz-type": "somestring",
        "requested-encryption-strength": "somestring",
        "requested-vig-address-type": "somestring",
        "shared-dmz-network-address": "somestring",
        "xauth-user-password": "somestring",
        "xauth-userid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/ipsec-configurations/ipsec-configuration/{ipsec-configuration-id}/vig-servers/vig-server/{vig-address-type}``
------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vig-server

Description
+++++++++++

.. raw:: html

    Update an existing vig-server
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        ipsec-configuration-id | path | Yes | string |  |  | UUID of this configuration
        vig-address-type | path | Yes | string |  |  | indicates whether the VIG is for AVPN or INTERNET


Request
+++++++



.. _d_bdb5565b83d0a58e6d00c44e876d490e:

Body
^^^^

vig-server contains information about a vig server used for IPSec-configuration. Populated by SDN-C from 1607
###### Related Nodes
- TO ipsec-configuration( vig-server BelongsTo ipsec-configuration, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this VIG-SERVER is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        ipaddress-v4-vig | No | string |  |  | v4 IP of the vig server
        ipaddress-v6-vig | No | string |  |  | v6 IP of the vig server
        vig-address-type | Yes | string |  |  | indicates whether the VIG is for AVPN or INTERNET

.. code-block:: javascript

    {
        "ipaddress-v4-vig": "somestring",
        "ipaddress-v6-vig": "somestring",
        "vig-address-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/l3-networks/l3-network/{network-id}``
------------------------------------------------------


Summary
+++++++

update an existing l3-network

Description
+++++++++++

.. raw:: html

    Update an existing l3-network
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-id | path | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.


Request
+++++++



.. _d_047b950ebe4fa983bb68a48097c4ceef:

Body
^^^^

Generic network definition
###### Related Nodes
- TO instance-group( l3-network MemberOf instance-group, MANY2MANY, will delete target node)
- TO network-policy( l3-network Uses network-policy, MANY2MANY, will delete target node)
- TO route-table-reference( l3-network Uses route-table-reference, MANY2MANY, will delete target node)
- TO vpn-binding( l3-network Uses vpn-binding, MANY2MANY, will delete target node)
- TO model-ver( l3-network IsA model-ver, Many2One, will delete target node)
- FROM allotted-resource( allotted-resource PartOf l3-network, MANY2MANY, will delete target node)
- FROM cloud-region( cloud-region Uses l3-network, MANY2MANY, will delete target node)
- FROM complex( complex Uses l3-network, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses l3-network, MANY2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- FROM ctag-assignment( ctag-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM segmentation-assignment( segmentation-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf l3-network, ONE2MANY, will delete target node)
- FROM subnet( subnet BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM tenant( tenant Uses l3-network, MANY2MANY, will delete target node)
- FROM vf-module( vf-module DependsOn l3-network, MANY2MANY, will delete target node)
- FROM configuration( configuration PartOf l3-network, ONE2ONE, will delete target node)

-(1) IF this L3-NETWORK node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-bound-to-vpn | Yes | boolean |  |  | Set to true if bound to VPN
        network-id | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        network-name | Yes | string |  |  | Name of the network, governed by some naming convention..
        network-role | No | string |  |  | Role the network plans - who defines these values?
        network-role-instance | No | integer | int64 |  | network role instance
        network-technology | No | string |  |  | Network technology - who defines these values?
        network-type | No | string |  |  | Type of the network - who defines these values?
        neutron-network-id | No | string |  |  | Neutron network id of this Interface
        service-id | No | string |  |  | Unique identifier of service from ASDC.  Does not strictly map to ASDC services.  SOON TO BE DEPRECATED

.. code-block:: javascript

    {
        "is-bound-to-vpn": true,
        "network-id": "somestring",
        "network-name": "somestring",
        "network-role": "somestring",
        "network-role-instance": 1,
        "network-technology": "somestring",
        "network-type": "somestring",
        "neutron-network-id": "somestring",
        "service-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/l3-networks/l3-network/{network-id}/ctag-assignments/ctag-assignment/{vlan-id-inner}``
-------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing ctag-assignment

Description
+++++++++++

.. raw:: html

    Update an existing ctag-assignment
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-id | path | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        vlan-id-inner | path | Yes | integer | int64 |  | id.


Request
+++++++



.. _d_d5270b11a41e64e6407bffbbb1bacad3:

Body
^^^^

###### Related Nodes
- TO l3-network( ctag-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(4)
- FROM service-instance( service-instance Uses ctag-assignment, ONE2MANY, will delete target node)

-(4) IF this TO node is deleted, this CTAG-ASSIGNMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | Yes | integer | int64 |  | id.

.. code-block:: javascript

    {
        "vlan-id-inner": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/l3-networks/l3-network/{network-id}/segmentation-assignments/segmentation-assignment/{segmentation-id}``
-------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing segmentation-assignment

Description
+++++++++++

.. raw:: html

    Update an existing segmentation-assignment
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-id | path | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        segmentation-id | path | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.


Request
+++++++



.. _d_6a8405a0fa38d927c63800a810ddbf23:

Body
^^^^

Openstack segmentation assignment.
###### Related Nodes
- TO l3-network( segmentation-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this SEGMENTATION-ASSIGNMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        segmentation-id | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.

.. code-block:: javascript

    {
        "segmentation-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/l3-networks/l3-network/{network-id}/subnets/subnet/{subnet-id}``
---------------------------------------------------------------------------------


Summary
+++++++

update an existing subnet

Description
+++++++++++

.. raw:: html

    Update an existing subnet
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-id | path | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        subnet-id | path | Yes | string |  |  | Subnet ID, should be UUID.


Request
+++++++



.. _d_8bf9ab8e091d31d615f38609e36fcc28:

Body
^^^^

###### Related Nodes
- TO l3-network( subnet BelongsTo l3-network, MANY2ONE, will delete target node)(4)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM host-route( host-route BelongsTo subnet, MANY2ONE, will delete target node)(1)
- FROM vip-ipv4-address-list( vip-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)

-(1) IF this SUBNET node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this SUBNET is DELETED also
-SUBNET cannot be deleted if related to L3-INTERFACE-IPV4-ADDRESS-LIST,L3-INTERFACE-IPV6-ADDRESS-LIST,VIP-IPV4-ADDRESS-LIST,VIP-IPV6-ADDRESS-LIST


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cidr-mask | No | string |  |  | cidr mask
        dhcp-enabled | Yes | boolean |  |  | dhcp enabled
        dhcp-end | No | string |  |  | the last address reserved for use by dhcp
        dhcp-start | No | string |  |  | the start address reserved for use by dhcp
        gateway-address | No | string |  |  | gateway ip address
        ip-assignment-direction | No | string |  |  | ip address assignment direction of the subnet
        ip-version | No | string |  |  | ip version
        network-start-address | No | string |  |  | network start address
        neutron-subnet-id | No | string |  |  | Neutron id of this subnet
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by MSO
        subnet-id | Yes | string |  |  | Subnet ID, should be UUID.
        subnet-name | No | string |  |  | Name associated with the subnet.
        subnet-role | No | string |  |  | role of the subnet, referenced when assigning IPs

.. code-block:: javascript

    {
        "cidr-mask": "somestring",
        "dhcp-enabled": true,
        "dhcp-end": "somestring",
        "dhcp-start": "somestring",
        "gateway-address": "somestring",
        "ip-assignment-direction": "somestring",
        "ip-version": "somestring",
        "network-start-address": "somestring",
        "neutron-subnet-id": "somestring",
        "orchestration-status": "somestring",
        "subnet-id": "somestring",
        "subnet-name": "somestring",
        "subnet-role": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/l3-networks/l3-network/{network-id}/subnets/subnet/{subnet-id}/host-routes/host-route/{host-route-id}``
------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing host-route

Description
+++++++++++

.. raw:: html

    Update an existing host-route
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-id | path | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        subnet-id | path | Yes | string |  |  | Subnet ID, should be UUID.
        host-route-id | path | Yes | string |  |  | host-route id


Request
+++++++



.. _d_dda7356f0d26edbabc2e3372aff4a1dd:

Body
^^^^

###### Related Nodes
- TO subnet( host-route BelongsTo subnet, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this HOST-ROUTE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        host-route-id | Yes | string |  |  | host-route id
        next-hop | Yes | string |  |  | Could be ip-address, hostname, or service-instance
        next-hop-type | No | string |  |  | Should be ip-address, hostname, or service-instance to match next-hop
        route-prefix | Yes | string |  |  | subnet prefix

.. code-block:: javascript

    {
        "host-route-id": "somestring",
        "next-hop": "somestring",
        "next-hop-type": "somestring",
        "route-prefix": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/lag-links/lag-link/{link-name}``
-------------------------------------------------


Summary
+++++++

update an existing lag-link

Description
+++++++++++

.. raw:: html

    Update an existing lag-link
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        link-name | path | Yes | string |  |  | Alphabetical concatenation of lag-interface names


Request
+++++++



.. _d_cbdb270b673401242656918a76f241e1:

Body
^^^^

LAG links can connect lag-interfaces
###### Related Nodes
- FROM lag-interface( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(3)
- FROM logical-link( logical-link Uses lag-link, MANY2MANY, will delete target node)

-(3) IF this FROM node is deleted, this LAG-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        link-name | Yes | string |  |  | Alphabetical concatenation of lag-interface names

.. code-block:: javascript

    {
        "link-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/logical-links/logical-link/{link-name}``
---------------------------------------------------------


Summary
+++++++

update an existing logical-link

Description
+++++++++++

.. raw:: html

    Update an existing logical-link
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        link-name | path | Yes | string |  |  | e.g., evc-name, or vnf-nameA_interface-nameA_vnf-nameZ_interface-nameZ


Request
+++++++



.. _d_7eb76ca0226a80f22632f9b59f7fcf60:

Body
^^^^

Logical links generally connect l-interfaces but are used to express logical connectivity between two points
###### Related Nodes
- TO l-interface( logical-link Source l-interface, ONE2MANY, will delete target node)(4)
- TO l-interface( logical-link Destination l-interface, ONE2MANY, will delete target node)(4)
- TO cloud-region( logical-link LocatedIn cloud-region, MANY2MANY, will delete target node)
- TO generic-vnf( logical-link BridgedTo generic-vnf, MANY2MANY, will delete target node)
- TO lag-link( logical-link Uses lag-link, MANY2MANY, will delete target node)
- TO logical-link( logical-link Uses logical-link, MANY2MANY, will delete target node)
- TO pnf( logical-link BridgedTo pnf, MANY2MANY, will delete target node)
- TO pserver( logical-link BridgedTo pserver, MANY2MANY, will delete target node)
- TO vpn-binding( logical-link Uses vpn-binding, MANY2MANY, will delete target node)
- TO virtual-data-center( logical-link LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- TO model-ver( logical-link IsA model-ver, Many2One, will delete target node)
- FROM configuration( configuration Uses logical-link, ONE2MANY, will delete target node)(3)
- FROM lag-interface( lag-interface Uses logical-link, MANY2MANY, will delete target node)(3)
- FROM l-interface( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(3)
- FROM p-interface( p-interface LinksTo logical-link, MANY2ONE, will delete target node)
- FROM service-instance( service-instance Uses logical-link, ONE2MANY, will delete target node)(3)
- FROM vlan( vlan Uses logical-link, MANY2MANY, will delete target node)(3)
- FROM logical-link( logical-link Uses logical-link, MANY2MANY, will delete target node)

-(3) IF this FROM node is deleted, this LOGICAL-LINK is DELETED also
-(4) IF this TO node is deleted, this LOGICAL-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        ip-version | No | string |  |  | v4, v6, or ds for dual stack (should be att-ip-version)
        link-name | Yes | string |  |  | e.g., evc-name, or vnf-nameA_interface-nameA_vnf-nameZ_interface-nameZ
        link-type | Yes | string |  |  | Type of logical link, e.g., evc
        routing-protocol | No | string |  |  | For example, static or BGP
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. code-block:: javascript

    {
        "in-maint": true,
        "ip-version": "somestring",
        "link-name": "somestring",
        "link-type": "somestring",
        "routing-protocol": "somestring",
        "speed-units": "somestring",
        "speed-value": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/multicast-configurations/multicast-configuration/{multicast-configuration-id}``
------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing multicast-configuration

Description
+++++++++++

.. raw:: html

    Update an existing multicast-configuration
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        multicast-configuration-id | path | Yes | string |  |  | Unique id of multicast configuration.


Request
+++++++



.. _d_bab72bd550d1cede8812be7ef191a566:

Body
^^^^

###### Related Nodes
- FROM vlan( vlan Uses multicast-configuration, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        multicast-configuration-id | Yes | string |  |  | Unique id of multicast configuration.
        multicast-protocol | Yes | string |  |  | protocol of multicast configuration
        rp-type | Yes | string |  |  | rp type of multicast configuration

.. code-block:: javascript

    {
        "multicast-configuration-id": "somestring",
        "multicast-protocol": "somestring",
        "rp-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/network-policies/network-policy/{network-policy-id}``
----------------------------------------------------------------------


Summary
+++++++

update an existing network-policy

Description
+++++++++++

.. raw:: html

    Update an existing network-policy
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        network-policy-id | path | Yes | string |  |  | UUID representing unique key to this instance


Request
+++++++



.. _d_aa7a59093b89f4be48a639c6f753fe11:

Body
^^^^

###### Related Nodes
- FROM allotted-resource( allotted-resource Uses network-policy, ONE2ONE, will delete target node)
- FROM l3-network( l3-network Uses network-policy, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | ID for the openStack Heat instance
        network-policy-fqdn | No | string |  |  | Contrail FQDN for the policy
        network-policy-id | Yes | string |  |  | UUID representing unique key to this instance

.. code-block:: javascript

    {
        "heat-stack-id": "somestring",
        "network-policy-fqdn": "somestring",
        "network-policy-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}``
-------------------------------------------


Summary
+++++++

update an existing newvce

Description
+++++++++++

.. raw:: html

    Update an existing newvce
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object


Request
+++++++



.. _d_f33ede34598aabc8a5f8eb99a9cd6644:

Body
^^^^

This object fills in the gaps from vce that were incorporated into generic-vnf.  This object will be retired with vce.
###### Related Nodes
- FROM l-interface( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(1)

-(1) IF this NEWVCE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Client should send valid enumerated value.
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by GenericVnf (v4-loopback0-ip-address).
        license-key | No | string |  |  | OBSOLETE -  do not use
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational
        prov-status | No | string |  |  | Trigger for operational monitoring of this VNF by BAU Service Assurance systems.
        vnf-id2 | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. code-block:: javascript

    {
        "equipment-role": "somestring",
        "ipv4-oam-address": "somestring",
        "license-key": "somestring",
        "operational-status": "somestring",
        "prov-status": "somestring",
        "vnf-id2": "somestring",
        "vnf-name": "somestring",
        "vnf-name2": "somestring",
        "vnf-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}``
-------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
-----------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
-----------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/newvces/newvce/{vnf-id2}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id2 | path | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/physical-links/physical-link/{link-name}``
-----------------------------------------------------------


Summary
+++++++

update an existing physical-link

Description
+++++++++++

.. raw:: html

    Update an existing physical-link
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        link-name | path | Yes | string |  |  | e.g., hostnameA_p-connection_nameA_hostnameZ+p_connection-nameZ


Request
+++++++



.. _d_980768c590515d54b0e3382b5e4bfd3d:

Body
^^^^

Collection of physical connections, typically between p-interfaces
###### Related Nodes
- FROM p-interface( p-interface LinksTo physical-link, MANY2ONE, will delete target node)(3)

-(3) IF this FROM node is deleted, this PHYSICAL-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        circuit-id | No | string |  |  | Circuit it
        dual-mode | No | string |  |  | Dual access mode (e.g., primary, secondary
        link-name | Yes | string |  |  | e.g., hostnameA_p-connection_nameA_hostnameZ+p_connection-nameZ
        management-option | No | string |  |  | To provide information on who manages this circuit. A&AI or 3rd party transport provider
        service-provider-bandwidth-down-units | No | string |  |  | Units for downstream BW value
        service-provider-bandwidth-down-value | No | integer | int32 |  | Downstream Bandwidth value agreed with the service provider
        service-provider-bandwidth-up-units | No | string |  |  | Units for the upstream BW value
        service-provider-bandwidth-up-value | No | integer | int32 |  | Upstream Bandwidth value agreed with the service provider
        service-provider-name | No | string |  |  | Name of the service Provider on this link.
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. code-block:: javascript

    {
        "circuit-id": "somestring",
        "dual-mode": "somestring",
        "link-name": "somestring",
        "management-option": "somestring",
        "service-provider-bandwidth-down-units": "somestring",
        "service-provider-bandwidth-down-value": 1,
        "service-provider-bandwidth-up-units": "somestring",
        "service-provider-bandwidth-up-value": 1,
        "service-provider-name": "somestring",
        "speed-units": "somestring",
        "speed-value": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}``
--------------------------------------


Summary
+++++++

update an existing pnf

Description
+++++++++++

.. raw:: html

    Update an existing pnf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.


Request
+++++++



.. _d_547a9c65ac53624f14a8e98650ddef2c:

Body
^^^^

PNF represents a physical network function. typically equipment used in the D1 world. in 1607, this will be populated by SDN-C to represent a premises router that a uCPE connects to. But this can be used to represent any physical device that is not an AIC node or uCPE.
###### Related Nodes
- TO complex( pnf LocatedIn complex, MANY2ONE, will delete target node)
- TO instance-group( pnf MemberOf instance-group, MANY2MANY, will delete target node)
- TO zone( pnf LocatedIn zone, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn pnf, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo pnf, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo pnf, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf pnf, ONE2MANY, will delete target node)
- FROM configuration( configuration AppliesTo pnf, ONE2MANY, will delete target node)
- FROM configuration( configuration AppliesTo pnf, MANY2MANY, will delete target node)

-(1) IF this PNF node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equip-model | No | string |  |  | Equipment model.  Source of truth should define valid values.
        equip-type | No | string |  |  | Equipment type.  Source of truth should define valid values.
        equip-vendor | No | string |  |  | Equipment vendor.  Source of truth should define valid values.
        frame-id | No | string |  |  | ID of the physical frame (relay rack) where pnf is installed.
        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        inv-status | No | string |  |  | CANOPI's inventory status.  Only set with values exactly as defined by CANOPI.
        ipaddress-v4-aim | No | string |  |  | IPV4 AIM address
        ipaddress-v4-loopback-0 | No | string |  |  | IPV4 Loopback 0 address
        ipaddress-v4-oam | No | string |  |  | ipv4-oam-address with new naming convention for IP addresses
        ipaddress-v6-aim | No | string |  |  | IPV6 AIM address
        ipaddress-v6-loopback-0 | No | string |  |  | IPV6 Loopback 0 address
        ipaddress-v6-oam | No | string |  |  | IPV6 OAM address
        management-option | No | string |  |  | identifier of managed by ATT or customer
        pnf-id | No | string |  |  | id of pnf
        pnf-name | Yes | string |  |  | unique name of Physical Network Function.
        pnf-name2 | No | string |  |  | name of Physical Network Function.
        pnf-name2-source | No | string |  |  | source of name2
        selflink | No | string |  |  | URL to endpoint where AAI can get more details.
        serial-number | No | string |  |  | Serial number of the device
        sw-version | No | string |  |  | sw-version is the version of SW for the hosted application on the PNF.

.. code-block:: javascript

    {
        "equip-model": "somestring",
        "equip-type": "somestring",
        "equip-vendor": "somestring",
        "frame-id": "somestring",
        "in-maint": true,
        "inv-status": "somestring",
        "ipaddress-v4-aim": "somestring",
        "ipaddress-v4-loopback-0": "somestring",
        "ipaddress-v4-oam": "somestring",
        "ipaddress-v6-aim": "somestring",
        "ipaddress-v6-loopback-0": "somestring",
        "ipaddress-v6-oam": "somestring",
        "management-option": "somestring",
        "pnf-id": "somestring",
        "pnf-name": "somestring",
        "pnf-name2": "somestring",
        "pnf-name2-source": "somestring",
        "selflink": "somestring",
        "serial-number": "somestring",
        "sw-version": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}``
------------------------------------------------------------------------------------


Summary
+++++++

update an existing lag-interface

Description
+++++++++++

.. raw:: html

    Update an existing lag-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface


Request
+++++++



.. _d_f61b51fbced469e399bfd4eae4c3ea5d:

Body
^^^^

Link aggregate interface
###### Related Nodes
- TO generic-vnf( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-link( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(2)
- TO logical-link( lag-interface Uses logical-link, MANY2MANY, will delete target node)(2)
- TO p-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- TO l-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- TO pnf( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM l-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)

-(1) IF this LAG-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this LAG-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this LAG-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-name | Yes | string |  |  | Name that identifies the link aggregate interface

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
----------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
----------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}``
--------------------------------------------------------------------------------


Summary
+++++++

update an existing p-interface

Description
+++++++++++

.. raw:: html

    Update an existing p-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface


Request
+++++++



.. _d_1b89d04f0a7bdc0f7445ec2a28d86140:

Body
^^^^

Physical interface (e.g., nic)
###### Related Nodes
- TO logical-link( p-interface LinksTo logical-link, MANY2ONE, will delete target node)
- TO physical-link( p-interface LinksTo physical-link, MANY2ONE, will delete target node)(2)
- TO pnf( p-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( p-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( p-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM lag-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(1)
- FROM sriov-pf( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo p-interface, MANY2MANY, will delete target node)

-(1) IF this P-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this P-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this P-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-identifier | No | string |  |  | CLEI or other specification for p-interface hardware.
        interface-name | Yes | string |  |  | Name that identifies the physical interface
        interface-role | No | string |  |  | Role specification for p-interface hardware.
        interface-type | No | string |  |  | Indicates the physical properties of the interface.
        port-description | No | string |  |  | Nature of the services and connectivity on this port.
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        selflink | No | string |  |  | URL to endpoint where AAI can get more details.
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. code-block:: javascript

    {
        "equipment-identifier": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "interface-type": "somestring",
        "port-description": "somestring",
        "prov-status": "somestring",
        "selflink": "somestring",
        "speed-units": "somestring",
        "speed-value": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
--------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/pnfs/pnf/{pnf-name}/p-interfaces/p-interface/{interface-name}/sriov-pfs/sriov-pf/{pf-pci-id}``
---------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-pf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-pf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        pnf-name | path | Yes | string |  |  | unique name of Physical Network Function.
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        pf-pci-id | path | Yes | string |  |  | Identifier for the sriov-pf


Request
+++++++



.. _d_1697b972bd787b44ec7d6037a9dd8b76:

Body
^^^^

SR-IOV Physical Function
###### Related Nodes
- TO p-interface( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(4)
- FROM sriov-vf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-PF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pf-pci-id | Yes | string |  |  | Identifier for the sriov-pf

.. code-block:: javascript

    {
        "pf-pci-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/route-table-references/route-table-reference/{route-table-reference-id}``
------------------------------------------------------------------------------------------


Summary
+++++++

update an existing route-table-reference

Description
+++++++++++

.. raw:: html

    Update an existing route-table-reference
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        route-table-reference-id | path | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.


Request
+++++++



.. _d_63cf8e39dbcd58a4d5b5e7930349620b:

Body
^^^^

Openstack route table reference.
###### Related Nodes
- FROM l3-network( l3-network Uses route-table-reference, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        route-table-reference-fqdn | Yes | string |  |  | FQDN entry in the route table.
        route-table-reference-id | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.

.. code-block:: javascript

    {
        "route-table-reference-fqdn": "somestring",
        "route-table-reference-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/site-pair-sets/site-pair-set/{site-pair-set-id}``
------------------------------------------------------------------


Summary
+++++++

update an existing site-pair-set

Description
+++++++++++

.. raw:: html

    Update an existing site-pair-set
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        site-pair-set-id | path | Yes | string |  |  | Unique id of site pair set.


Request
+++++++



.. _d_cf4788ba8f7a5868a474c9a551d891d9:

Body
^^^^

Set of instances for probes used to measure service level agreements
###### Related Nodes
- TO generic-vnf( site-pair-set AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM routing-instance( routing-instance BelongsTo site-pair-set, MANY2ONE, will delete target node)(1)

-(1) IF this SITE-PAIR-SET node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        site-pair-set-id | Yes | string |  |  | Unique id of site pair set.

.. code-block:: javascript

    {
        "site-pair-set-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/site-pair-sets/site-pair-set/{site-pair-set-id}/routing-instances/routing-instance/{routing-instance-id}``
---------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing routing-instance

Description
+++++++++++

.. raw:: html

    Update an existing routing-instance
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        site-pair-set-id | path | Yes | string |  |  | Unique id of site pair set.
        routing-instance-id | path | Yes | string |  |  | Unique id of routing instance


Request
+++++++



.. _d_83776bc44ba8e488b933402ad9fc052b:

Body
^^^^

###### Related Nodes
- TO site-pair-set( routing-instance BelongsTo site-pair-set, MANY2ONE, will delete target node)(4)
- FROM site-pair( site-pair BelongsTo routing-instance, MANY2ONE, will delete target node)(1)

-(1) IF this ROUTING-INSTANCE node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this ROUTING-INSTANCE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        routing-instance-id | Yes | string |  |  | Unique id of routing instance
        rpm-owner | No | string |  |  | rpm owner

.. code-block:: javascript

    {
        "routing-instance-id": "somestring",
        "rpm-owner": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/site-pair-sets/site-pair-set/{site-pair-set-id}/routing-instances/routing-instance/{routing-instance-id}/site-pairs/site-pair/{site-pair-id}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing site-pair

Description
+++++++++++

.. raw:: html

    Update an existing site-pair
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        site-pair-set-id | path | Yes | string |  |  | Unique id of site pair set.
        routing-instance-id | path | Yes | string |  |  | Unique id of routing instance
        site-pair-id | path | Yes | string |  |  | unique identifier of probe


Request
+++++++



.. _d_388ae35852e62ce685c588eba0b2274f:

Body
^^^^

###### Related Nodes
- TO routing-instance( site-pair BelongsTo routing-instance, MANY2ONE, will delete target node)(4)
- FROM class-of-service( class-of-service BelongsTo site-pair, MANY2ONE, will delete target node)(1)

-(1) IF this SITE-PAIR node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this SITE-PAIR is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        destination-equip-type | No | string |  |  | The type of destinatination equipment. Could be Router, UCPE, etc.
        destination-hostname | No | string |  |  | Hostname of the destination equipment to which SLAs are measured against.
        destination-ip | No | string |  |  | Prefix address
        ip-version | No | string |  |  | ip version, v4, v6
        site-pair-id | Yes | string |  |  | unique identifier of probe
        source-ip | No | string |  |  | Prefix address

.. code-block:: javascript

    {
        "destination-equip-type": "somestring",
        "destination-hostname": "somestring",
        "destination-ip": "somestring",
        "ip-version": "somestring",
        "site-pair-id": "somestring",
        "source-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/site-pair-sets/site-pair-set/{site-pair-set-id}/routing-instances/routing-instance/{routing-instance-id}/site-pairs/site-pair/{site-pair-id}/classes-of-service/class-of-service/{cos}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing class-of-service

Description
+++++++++++

.. raw:: html

    Update an existing class-of-service
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        site-pair-set-id | path | Yes | string |  |  | Unique id of site pair set.
        routing-instance-id | path | Yes | string |  |  | Unique id of routing instance
        site-pair-id | path | Yes | string |  |  | unique identifier of probe
        cos | path | Yes | string |  |  | unique identifier of probe


Request
+++++++



.. _d_8f89550f0e1140a32f409933ee340a59:

Body
^^^^

###### Related Nodes
- TO site-pair( class-of-service BelongsTo site-pair, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this CLASS-OF-SERVICE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cos | Yes | string |  |  | unique identifier of probe
        probe-id | No | string |  |  | identifier of probe
        probe-type | No | string |  |  | type of probe

.. code-block:: javascript

    {
        "cos": "somestring",
        "probe-id": "somestring",
        "probe-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vces/vce/{vnf-id}``
------------------------------------


Summary
+++++++

update an existing vce

Description
+++++++++++

.. raw:: html

    Update an existing vce
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.


Request
+++++++



.. _d_3c2bacb31fb510463082f50d89f18144:

Body
^^^^

Virtual Customer Edge Router, used specifically for Gamma.  This object is deprecated.
###### Related Nodes
- TO availability-zone( vce Uses availability-zone, MANY2MANY, will delete target node)
- TO complex( vce LocatedIn complex, MANY2MANY, will delete target node)
- TO vserver( vce HostedOn vserver, ONE2MANY, will delete target node)
- FROM entitlement( entitlement BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM license( license BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM port-group( port-group BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf vce, ONE2MANY, will delete target node)

-(1) IF this VCE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Network role being played by this VNF
        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance, managed by MSO
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure vce, also used for troubleshooting and is IP used for traps generated by VCE.
        license-key | No | string |  |  | OBSOLETE -  do not use
        mso-catalog-key | No | string |  |  | Corresponds to the SDN-C catalog id used to configure this VCE
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by MSO
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        regional-resource-zone | No | string |  |  | Regional way of organizing pservers, source of truth should define values
        service-id | No | string |  |  | Unique identifier of service, does not strictly map to ASDC services, SOON TO BE DEPRECATED.
        v6-vce-wan-address | No | string |  |  | Valid v6 IP address for the WAN Link on this router.  Implied length of /64.
        vnf-id | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.
        vpe-id | No | string |  |  | Unique ID of VPE connected to this VCE.

.. code-block:: javascript

    {
        "equipment-role": "somestring",
        "heat-stack-id": "somestring",
        "ipv4-oam-address": "somestring",
        "license-key": "somestring",
        "mso-catalog-key": "somestring",
        "operational-status": "somestring",
        "orchestration-status": "somestring",
        "prov-status": "somestring",
        "regional-resource-zone": "somestring",
        "service-id": "somestring",
        "v6-vce-wan-address": "somestring",
        "vnf-id": "somestring",
        "vnf-name": "somestring",
        "vnf-name2": "somestring",
        "vnf-type": "somestring",
        "vpe-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vces/vce/{vnf-id}/entitlements/entitlement/{group-uuid}/{resource-uuid}``
------------------------------------------------------------------------------------------


Summary
+++++++

update an existing entitlement

Description
+++++++++++

.. raw:: html

    Update an existing entitlement
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        group-uuid | path | Yes | string |  |  | Unique ID for the entitlement group the resource comes from, should be uuid.
        resource-uuid | path | Yes | string |  |  | Unique ID of an entitlement resource.


Request
+++++++



.. _d_d1848f6a2192289cc1b9ccfec6de8fa8:

Body
^^^^

Metadata for entitlement group.
###### Related Nodes
- TO generic-vnf( entitlement BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( entitlement BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this ENTITLEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the entitlement group the resource comes from, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of an entitlement resource.

.. code-block:: javascript

    {
        "group-uuid": "somestring",
        "resource-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vces/vce/{vnf-id}/licenses/license/{group-uuid}/{resource-uuid}``
----------------------------------------------------------------------------------


Summary
+++++++

update an existing license

Description
+++++++++++

.. raw:: html

    Update an existing license
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        group-uuid | path | Yes | string |  |  | Unique ID for the license group the resource belongs to, should be uuid.
        resource-uuid | path | Yes | string |  |  | Unique ID of a license resource.


Request
+++++++



.. _d_25a47ee6575bfa6d53284f5bf0598b55:

Body
^^^^

Metadata for license group.
###### Related Nodes
- TO generic-vnf( license BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( license BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this LICENSE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the license group the resource belongs to, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of a license resource.

.. code-block:: javascript

    {
        "group-uuid": "somestring",
        "resource-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vces/vce/{vnf-id}/port-groups/port-group/{interface-id}``
--------------------------------------------------------------------------


Summary
+++++++

update an existing port-group

Description
+++++++++++

.. raw:: html

    Update an existing port-group
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-id | path | Yes | string |  |  | Unique ID of the interface


Request
+++++++



.. _d_62a0d04ea171fcc6a1a4dbc52667d6e7:

Body
^^^^

Used to capture the network interfaces of this VCE
###### Related Nodes
- TO vce( port-group BelongsTo vce, MANY2ONE, will delete target node)(4)
- FROM cvlan-tag( cvlan-tag BelongsTo port-group, MANY2ONE, will delete target node)(1)

-(1) IF this PORT-GROUP node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this PORT-GROUP is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-id | Yes | string |  |  | Unique ID of the interface
        interface-role | No | string |  |  | Role assigned to this Interface, should use values as defined in ECOMP Yang models.
        neutron-network-id | No | string |  |  | Neutron network id of this Interface
        neutron-network-name | No | string |  |  | Neutron network name of this Interface

.. code-block:: javascript

    {
        "interface-id": "somestring",
        "interface-role": "somestring",
        "neutron-network-id": "somestring",
        "neutron-network-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vces/vce/{vnf-id}/port-groups/port-group/{interface-id}/cvlan-tags/cvlan-tag-entry/{cvlan-tag}``
-----------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing cvlan-tag-entry

Description
+++++++++++

.. raw:: html

    Update an existing cvlan-tag-entry
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnf-id | path | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        interface-id | path | Yes | string |  |  | Unique ID of the interface
        cvlan-tag | path | Yes | integer | int64 |  | See mis-na-virtualization-platform.yang


Request
+++++++



.. _d_9eb8536b45dbf20589f914fd244e6078:

Body
^^^^

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cvlan-tag | Yes | integer | int64 |  | See mis-na-virtualization-platform.yang

.. code-block:: javascript

    {
        "cvlan-tag": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vnfcs/vnfc/{vnfc-name}``
-----------------------------------------


Summary
+++++++

update an existing vnfc

Description
+++++++++++

.. raw:: html

    Update an existing vnfc
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnfc-name | path | Yes | string |  |  | Unique ID of vnfc.


Request
+++++++



.. _d_e8c782e6607830c8202e32933efce766:

Body
^^^^

###### Related Nodes
- TO generic-vnf( vnfc BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO instance-group( vnfc MemberOf instance-group, MANY2MANY, will delete target node)
- TO vip-ipv4-address-list( vnfc Uses vip-ipv4-address-list, MANY2MANY, will delete target node)
- TO vip-ipv6-address-list( vnfc Uses vip-ipv6-address-list, MANY2MANY, will delete target node)
- TO vserver( vnfc HostedOn vserver, ONE2MANY, will delete target node)
- FROM vf-module( vf-module Uses vnfc, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(1)

-(1) IF this VNFC node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this VNFC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-notation | No | string |  |  | Group notation of VNFC
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true)
        ipaddress-v4-oam-vip | No | string |  |  | Oam V4 vip address of this vnfc
        is-closed-loop-disabled | Yes | boolean |  |  | used to indicate whether closed loop function is enabled on this node
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        nfc-function | Yes | string |  |  | English description of function that the specific resource deployment is providing. Assigned as part of the customization of a resource in a service
        nfc-naming-code | Yes | string |  |  | Short code that is used in naming instances of the item being modeled
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by APP-C
        prov-status | No | string |  |  | prov status of this vnfc
        vnfc-name | Yes | string |  |  | Unique ID of vnfc.

.. code-block:: javascript

    {
        "group-notation": "somestring",
        "in-maint": true,
        "ipaddress-v4-oam-vip": "somestring",
        "is-closed-loop-disabled": true,
        "model-invariant-id": "somestring",
        "model-version-id": "somestring",
        "nfc-function": "somestring",
        "nfc-naming-code": "somestring",
        "orchestration-status": "somestring",
        "prov-status": "somestring",
        "vnfc-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vnfcs/vnfc/{vnfc-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
----------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnfc-name | path | Yes | string |  |  | Unique ID of vnfc.
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vnfcs/vnfc/{vnfc-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
----------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vnfc-name | path | Yes | string |  |  | Unique ID of vnfc.
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}``
----------------------------------------------------


Summary
+++++++

update an existing vpls-pe

Description
+++++++++++

.. raw:: html

    Update an existing vpls-pe
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 


Request
+++++++



.. _d_00ecef75a8d71ee9d4abc90feff3a5fb:

Body
^^^^

VPLS Provider Edge routers.
###### Related Nodes
- TO complex( vpls-pe LocatedIn complex, MANY2ONE, will delete target node)
- TO ctag-pool( vpls-pe Uses ctag-pool, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(1)

-(1) IF this VPLS-PE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-name | Yes | string |  |  | 
        equipment-role | No | string |  |  | Client should send valid enumerated value, e.g., VPLS-PE.
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by GenericVnf (v4-loopback0-ip-address).
        prov-status | No | string |  |  | Trigger for operational monitoring of this VNF by BAU Service Assurance systems.
        vlan-id-outer | No | integer | int64 |  | Temporary location for stag to get to VCE

.. code-block:: javascript

    {
        "equipment-name": "somestring",
        "equipment-role": "somestring",
        "ipv4-oam-address": "somestring",
        "prov-status": "somestring",
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}``
--------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing lag-interface

Description
+++++++++++

.. raw:: html

    Update an existing lag-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface


Request
+++++++



.. _d_f61b51fbced469e399bfd4eae4c3ea5d:

Body
^^^^

Link aggregate interface
###### Related Nodes
- TO generic-vnf( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-link( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(2)
- TO logical-link( lag-interface Uses logical-link, MANY2MANY, will delete target node)(2)
- TO p-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- TO l-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- TO pnf( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM l-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)

-(1) IF this LAG-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this LAG-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this LAG-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-name | Yes | string |  |  | Name that identifies the link aggregate interface

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
--------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the link aggregate interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}``
----------------------------------------------------------------------------------------------


Summary
+++++++

update an existing p-interface

Description
+++++++++++

.. raw:: html

    Update an existing p-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface


Request
+++++++



.. _d_1b89d04f0a7bdc0f7445ec2a28d86140:

Body
^^^^

Physical interface (e.g., nic)
###### Related Nodes
- TO logical-link( p-interface LinksTo logical-link, MANY2ONE, will delete target node)
- TO physical-link( p-interface LinksTo physical-link, MANY2ONE, will delete target node)(2)
- TO pnf( p-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( p-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( p-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM lag-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(1)
- FROM sriov-pf( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo p-interface, MANY2MANY, will delete target node)

-(1) IF this P-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this P-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this P-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-identifier | No | string |  |  | CLEI or other specification for p-interface hardware.
        interface-name | Yes | string |  |  | Name that identifies the physical interface
        interface-role | No | string |  |  | Role specification for p-interface hardware.
        interface-type | No | string |  |  | Indicates the physical properties of the interface.
        port-description | No | string |  |  | Nature of the services and connectivity on this port.
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        selflink | No | string |  |  | URL to endpoint where AAI can get more details.
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. code-block:: javascript

    {
        "equipment-identifier": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "interface-type": "somestring",
        "port-description": "somestring",
        "prov-status": "somestring",
        "selflink": "somestring",
        "speed-units": "somestring",
        "speed-value": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
----------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l-interface

Description
+++++++++++

.. raw:: html

    Update an existing l-interface
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface


Request
+++++++



.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

Body
^^^^

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. code-block:: javascript

    {
        "interface-description": "somestring",
        "interface-id": "somestring",
        "interface-name": "somestring",
        "interface-role": "somestring",
        "is-port-mirrored": true,
        "macaddr": "somestring",
        "management-option": "somestring",
        "network-name": "somestring",
        "selflink": "somestring",
        "v6-wan-link-ip": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-vf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-vf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        pci-id | path | Yes | string |  |  | PCI ID used to identify the sriov-vf


Request
+++++++



.. _d_74e32e7a157ccfd9e03147e9f35247de:

Body
^^^^

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. code-block:: javascript

    {
        "pci-id": "somestring",
        "vf-broadcast-allow": true,
        "vf-insert-stag": true,
        "vf-link-status": "somestring",
        "vf-mac-anti-spoof-check": true,
        "vf-mac-filter": "somestring",
        "vf-mirrors": "somestring",
        "vf-unknown-multicast-allow": true,
        "vf-unknown-unicast-allow": true,
        "vf-vlan-anti-spoof-check": true,
        "vf-vlan-filter": "somestring",
        "vf-vlan-strip": true
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vlan

Description
+++++++++++

.. raw:: html

    Update an existing vlan
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface


Request
+++++++



.. _d_02b51d5ca6048fc99565091bdd4861a7:

Body
^^^^

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. code-block:: javascript

    {
        "vlan-id-inner": 1,
        "vlan-id-outer": 1,
        "vlan-interface": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv4-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_81b5118b793ab488e0abd4e4abebd75d:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv4-address": "somestring",
        "l3-interface-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing l3-interface-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing l3-interface-ipv6-address-list
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        interface-name | path | Yes | string |  |  | Name given to the interface
        vlan-interface | path | Yes | string |  |  | String that identifies the interface
        l3-interface-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_7e4e75c848e45d161cd98b4b8736d86f:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "l3-interface-ipv6-address": "somestring",
        "l3-interface-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpls-pes/vpls-pe/{equipment-name}/p-interfaces/p-interface/{interface-name}/sriov-pfs/sriov-pf/{pf-pci-id}``
-----------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing sriov-pf

Description
+++++++++++

.. raw:: html

    Update an existing sriov-pf
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        equipment-name | path | Yes | string |  |  | 
        interface-name | path | Yes | string |  |  | Name that identifies the physical interface
        pf-pci-id | path | Yes | string |  |  | Identifier for the sriov-pf


Request
+++++++



.. _d_1697b972bd787b44ec7d6037a9dd8b76:

Body
^^^^

SR-IOV Physical Function
###### Related Nodes
- TO p-interface( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(4)
- FROM sriov-vf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-PF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pf-pci-id | Yes | string |  |  | Identifier for the sriov-pf

.. code-block:: javascript

    {
        "pf-pci-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpn-bindings/vpn-binding/{vpn-id}``
----------------------------------------------------


Summary
+++++++

update an existing vpn-binding

Description
+++++++++++

.. raw:: html

    Update an existing vpn-binding
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vpn-id | path | Yes | string |  |  | VPN ID, globally unique within A&AI


Request
+++++++



.. _d_97f7255ccc461fc49496f9e8a1c0d079:

Body
^^^^

VPN binding
###### Related Nodes
- TO customer( vpn-binding Uses customer, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource BelongsTo vpn-binding, MANY2MANY, will delete target node)
- FROM l3-network( l3-network Uses vpn-binding, MANY2MANY, will delete target node)
- FROM logical-link( logical-link Uses vpn-binding, MANY2MANY, will delete target node)
- FROM route-target( route-target BelongsTo vpn-binding, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration Uses vpn-binding, MANY2ONE, will delete target node)
- FROM service-instance( service-instance Uses vpn-binding, MANY2ONE, will delete target node)

-(1) IF this VPN-BINDING node is deleted, this FROM node is DELETED also
-VPN-BINDING cannot be deleted if related to ALLOTTED-RESOURCE,L3-NETWORK,LOGICAL-LINK


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        customer-vpn-id | No | string |  |  | id for this customer vpn
        route-distinguisher | No | string |  |  | Used to distinguish the distinct VPN routes of separate customers who connect to the provider in an MPLS network.
        vpn-id | Yes | string |  |  | VPN ID, globally unique within A&AI
        vpn-name | Yes | string |  |  | VPN Name
        vpn-platform | No | string |  |  | the platform associated with the VPN example AVPN, Mobility
        vpn-region | No | string |  |  | region of customer vpn
        vpn-type | No | string |  |  | Type of the vpn, should be taken from enumerated/valid values

.. code-block:: javascript

    {
        "customer-vpn-id": "somestring",
        "route-distinguisher": "somestring",
        "vpn-id": "somestring",
        "vpn-name": "somestring",
        "vpn-platform": "somestring",
        "vpn-region": "somestring",
        "vpn-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/vpn-bindings/vpn-binding/{vpn-id}/route-targets/route-target/{global-route-target}/{route-target-role}``
-------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing route-target

Description
+++++++++++

.. raw:: html

    Update an existing route-target
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        vpn-id | path | Yes | string |  |  | VPN ID, globally unique within A&AI
        global-route-target | path | Yes | string |  |  | Number used to identify an RT, globally unique in the network
        route-target-role | path | Yes | string |  |  | Role assigned to this route target


Request
+++++++



.. _d_5fa5ed2e9b1d2543dc43cc50bd5a9b8e:

Body
^^^^

Route target information
###### Related Nodes
- TO vpn-binding( route-target BelongsTo vpn-binding, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this ROUTE-TARGET is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        global-route-target | Yes | string |  |  | Number used to identify an RT, globally unique in the network
        route-target-role | Yes | string |  |  | Role assigned to this route target

.. code-block:: javascript

    {
        "global-route-target": "somestring",
        "route-target-role": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/network/zones/zone/{zone-id}``
---------------------------------------


Summary
+++++++

update an existing zone

Description
+++++++++++

.. raw:: html

    Update an existing zone
#
Note:  Endpoints that are not devoted to object relationships support both PUT and PATCH operations.
The PUT operation will entirely replace an existing object.
The PATCH operation sends a "description of changes" for an existing object.  The entire set of changes must be applied.  An error result means no change occurs.
#
Other differences between PUT and PATCH are:
#
- For PATCH, you can send any of the values shown in sample REQUEST body.  There are no required values.
- For PATCH, resource-id which is a required REQUEST body element for PUT, must not be sent.
- PATCH cannot be used to update relationship elements; there are dedicated PUT operations for this.


Parameters
++++++++++

.. csv-table::
    :delim: |
    :header: "Name", "Located in", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 15, 10, 10, 10, 20, 30

        zone-id | path | Yes | string |  |  | Code assigned by AIC to the zone


Request
+++++++



.. _d_3105cb0de2c0b0960a36d71dc07146a2:

Body
^^^^

A zone is a grouping of assets in a location homing to the same connections into the CBB
###### Related Nodes
- TO complex( zone LocatedIn complex, MANY2ONE, will delete target node)
- FROM cloud-region( cloud-region LocatedIn zone, MANY2ONE, will delete target node)
- FROM pnf( pnf LocatedIn zone, MANY2ONE, will delete target node)
- FROM pserver( pserver LocatedIn zone, MANY2ONE, will delete target node)
- FROM service-instance( service-instance LocatedIn zone, MANY2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        design-type | Yes | string |  |  | Design of zone [Medium/Large…]
        status | No | string |  |  | Status of a zone.
        zone-context | Yes | string |  |  | Context of zone [production/test]
        zone-id | Yes | string |  |  | Code assigned by AIC to the zone
        zone-name | Yes | string |  |  | English name associated with the zone

.. code-block:: javascript

    {
        "design-type": "somestring",
        "status": "somestring",
        "zone-context": "somestring",
        "zone-id": "somestring",
        "zone-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).




  
Data Structures
~~~~~~~~~~~~~~~

.. _d_8f89550f0e1140a32f409933ee340a59:

class-of-service Model Structure
--------------------------------

###### Related Nodes
- TO site-pair( class-of-service BelongsTo site-pair, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this CLASS-OF-SERVICE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cos | Yes | string |  |  | unique identifier of probe
        probe-id | No | string |  |  | identifier of probe
        probe-type | No | string |  |  | type of probe

.. _d_17183bcf291e8802fce75b60418dced0:

classes-of-service Model Structure
----------------------------------

class-of-service of probe


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        class-of-service | No | array of :ref:`class-of-service <d_8f89550f0e1140a32f409933ee340a59>` |  |  | 

.. _d_ecdcd4e52dd9916ee05ece1e631a86ce:

configuration Model Structure
-----------------------------

Generic configuration object.
###### Related Nodes
- TO allotted-resource( configuration Uses allotted-resource, ONE2ONE, will delete target node)(2)
- TO logical-link( configuration Uses logical-link, ONE2MANY, will delete target node)(2)
- TO l-interface( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- TO pnf( configuration AppliesTo pnf, ONE2MANY, will delete target node)
- TO configuration( configuration BindsTo configuration, ONE2ONE, will delete target node)
- TO vpn-binding( configuration Uses vpn-binding, MANY2ONE, will delete target node)
- TO generic-vnf( configuration PartOf generic-vnf, MANY2ONE, will delete target node)
- TO vlan( configuration PartOf vlan, ONE2ONE, will delete target node)
- TO l3-network( configuration PartOf l3-network, ONE2ONE, will delete target node)
- TO service-instance( configuration BelongsTo service-instance, MANY2ONE, will delete target node)(4)
- TO pnf( configuration AppliesTo pnf, MANY2MANY, will delete target node)
- FROM metadatum( metadatum BelongsTo configuration, MANY2ONE, will delete target node)(1)
- FROM generic-vnf( generic-vnf Uses configuration, ONE2MANY, will delete target node)(3)
- FROM service-instance( service-instance Uses configuration, ONE2MANY, will delete target node)
- FROM forwarder( forwarder Uses configuration, ONE2ONE, will delete target node)(3)
- FROM forwarding-path( forwarding-path Uses configuration, ONE2ONE, will delete target node)(3)
- FROM evc( evc BelongsTo configuration, ONE2ONE, will delete target node)(1)
- FROM forwarder-evc( forwarder-evc BelongsTo configuration, ONE2ONE, will delete target node)(1)
- FROM service-instance( service-instance Uses configuration, MANY2MANY, will delete target node)
- FROM configuration( configuration BindsTo configuration, ONE2ONE, will delete target node)

-(1) IF this CONFIGURATION node is deleted, this FROM node is DELETED also
-(2) IF this CONFIGURATION node is deleted, this TO node is DELETED also
-(3) IF this FROM node is deleted, this CONFIGURATION is DELETED also
-(4) IF this TO node is deleted, this CONFIGURATION is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        configuration-id | Yes | string |  |  | UUID assigned to configuration.
        configuration-name | No | string |  |  | Name of the configuration.
        configuration-selflink | No | string |  |  | URL to endpoint where AAI can get more details from SDN-GC.
        configuration-sub-type | Yes | string |  |  | vprobe, pprobe.
        configuration-type | Yes | string |  |  | port-mirroring-configuration.
        management-option | No | string |  |  | Indicates the entity that will manage this feature. Could be an organization or the name of the application as well.
        model-customization-id | No | string |  |  | id of  the configuration used to customize the resource
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational.
        orchestration-status | No | string |  |  | Orchestration status of the configuration.
        tunnel-bandwidth | No | string |  |  | DHV Site Effective Bandwidth
        vendor-allowed-max-bandwidth | No | string |  |  | Velocloud Nominal Throughput - VNT

.. _d_c76522a97f45952bf3e7f55b2f3d401a:

configurations Model Structure
------------------------------

Collection of configurations


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        configuration | No | array of :ref:`configuration <d_ecdcd4e52dd9916ee05ece1e631a86ce>` |  |  | 

.. _d_d5270b11a41e64e6407bffbbb1bacad3:

ctag-assignment Model Structure
-------------------------------

###### Related Nodes
- TO l3-network( ctag-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(4)
- FROM service-instance( service-instance Uses ctag-assignment, ONE2MANY, will delete target node)

-(4) IF this TO node is deleted, this CTAG-ASSIGNMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | Yes | integer | int64 |  | id.

.. _d_b857a65e2974d41e4905f57f00d8f53e:

ctag-assignments Model Structure
--------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        ctag-assignment | No | array of :ref:`ctag-assignment <d_d5270b11a41e64e6407bffbbb1bacad3>` |  |  | 

.. _d_9eb8536b45dbf20589f914fd244e6078:

cvlan-tag-entry Model Structure
-------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cvlan-tag | Yes | integer | int64 |  | See mis-na-virtualization-platform.yang

.. _d_83f35c028a86ec6dab43e75010c8786a:

cvlan-tags Model Structure
--------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cvlan-tag-entry | No | array of :ref:`cvlan-tag-entry <d_9eb8536b45dbf20589f914fd244e6078>` |  |  | 

.. _d_d1848f6a2192289cc1b9ccfec6de8fa8:

entitlement Model Structure
---------------------------

Metadata for entitlement group.
###### Related Nodes
- TO generic-vnf( entitlement BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( entitlement BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this ENTITLEMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the entitlement group the resource comes from, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of an entitlement resource.

.. _d_1d2e55b811e5b5390f874df9c36653f3:

entitlements Model Structure
----------------------------

Entitlements, keyed by group-uuid and resource-uuid, related to license management


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        entitlement | No | array of :ref:`entitlement <d_d1848f6a2192289cc1b9ccfec6de8fa8>` |  |  | 

.. _d_22d29437390b7e26bddbd7c902189913:

evc Model Structure
-------------------

evc object is an optional child object of the Configuration object.
###### Related Nodes
- TO configuration( evc BelongsTo configuration, ONE2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this EVC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cir-units | No | string |  |  | CIR units
        cir-value | No | string |  |  | Commited Information Rate
        collector-pop-clli | No | string |  |  | Collector POP CLLI (from the hostname of the access pnf)
        connection-diversity-group-id | No | string |  |  | Diversity Group ID
        esp-evc-cir-units | No | string |  |  | CIR units (For ESP)
        esp-evc-cir-value | No | string |  |  | Committed Information Rate (For ESP)
        esp-evc-circuit-id | No | string |  |  | EVC Circuit ID of ESP EVC
        esp-itu-code | No | string |  |  | Identifies ESP
        evc-id | Yes | string |  |  | Unique/key field for the evc object
        forwarding-path-topology | No | string |  |  | Point-to-Point, Multi-Point
        inter-connect-type-ingress | No | string |  |  | Interconnect type on ingress side of EVC.
        service-hours | No | string |  |  | formerly Performance Group
        tagmode-access-egress | No | string |  |  | tagMode for network side of EVC
        tagmode-access-ingress | No | string |  |  | tagode for collector side of EVC

.. _d_df1738cbf44e50cc7f1248603700e6db:

evcs Model Structure
--------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        evc | No | array of :ref:`evc <d_22d29437390b7e26bddbd7c902189913>` |  |  | 

.. _d_d4f446d151f5db0d4b9703506f700b79:

forwarder Model Structure
-------------------------

Entity describing a sequenced segment of forwarding path
###### Related Nodes
- TO forwarding-path( forwarder BelongsTo forwarding-path, MANY2ONE, will delete target node)(4)
- TO l-interface( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- TO configuration( forwarder Uses configuration, ONE2ONE, will delete target node)(2)
- TO lag-interface( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)
- TO p-interface( forwarder ForwardsTo p-interface, MANY2MANY, will delete target node)

-(2) IF this FORWARDER node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this FORWARDER is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarder-role | No | string |  |  | ingress, intermediate, egress
        sequence | Yes | integer | int32 |  | Unique ID of this segmentation

.. _d_859dedf919dad05968c2a6ca2ea302a8:

forwarder-evc Model Structure
-----------------------------

forwarder object is an optional child object of the Configuration object.
###### Related Nodes
- TO configuration( forwarder-evc BelongsTo configuration, ONE2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this FORWARDER-EVC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        circuit-id | No | string |  |  | Circuit ID from customer/ESP/ingress end of EVC, or reference to beater circuit on gateway/network/egress end of EVC
        cvlan | No | string |  |  | CVLAN value for ingress of egress forwarder.
        forwarder-evc-id | Yes | string |  |  | Key for forwarder-evc object
        ivlan | No | string |  |  | Internal VLAN.
        svlan | No | string |  |  | SVLAN value for ingress of egress forwarder.

.. _d_478677e831db685c2b17481684dea555:

forwarder-evcs Model Structure
------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarder-evc | No | array of :ref:`forwarder-evc <d_859dedf919dad05968c2a6ca2ea302a8>` |  |  | 

.. _d_4fdee3bc263b5b46d5774505c29bb53f:

forwarders Model Structure
--------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarder | No | array of :ref:`forwarder <d_d4f446d151f5db0d4b9703506f700b79>` |  |  | 

.. _d_46de7db8148c637b12aed1c8966df252:

forwarding-path Model Structure
-------------------------------

Entity that describes the sequenced forwarding path between interfaces of services or resources
###### Related Nodes
- TO service-instance( forwarding-path AppliesTo service-instance, MANY2ONE, will delete target node)(4)
- TO configuration( forwarding-path Uses configuration, ONE2ONE, will delete target node)(2)
- FROM forwarder( forwarder BelongsTo forwarding-path, MANY2ONE, will delete target node)(1)

-(1) IF this FORWARDING-PATH node is deleted, this FROM node is DELETED also
-(2) IF this FORWARDING-PATH node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this FORWARDING-PATH is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarding-path-id | Yes | string |  |  | Unique ID of this FP
        forwarding-path-name | Yes | string |  |  | Name of the FP

.. _d_eacee5119220522ce81fc74ca3cd9ee2:

forwarding-paths Model Structure
--------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        forwarding-path | No | array of :ref:`forwarding-path <d_46de7db8148c637b12aed1c8966df252>` |  |  | 

.. _d_ca0376d58aad84ab5c5935fa4c191551:

generic-vnf Model Structure
---------------------------

General purpose VNF
###### Related Nodes
- TO availability-zone( generic-vnf Uses availability-zone, MANY2MANY, will delete target node)
- TO complex( generic-vnf LocatedIn complex, MANY2MANY, will delete target node)
- TO configuration( generic-vnf Uses configuration, ONE2MANY, will delete target node)(2)
- TO ctag-pool( generic-vnf Uses ctag-pool, MANY2MANY, will delete target node)
- TO instance-group( generic-vnf MemberOf instance-group, MANY2MANY, will delete target node)
- TO ipsec-configuration( generic-vnf Uses ipsec-configuration, MANY2ONE, will delete target node)
- TO l3-network( generic-vnf Uses l3-network, MANY2MANY, will delete target node)
- TO pnf( generic-vnf HostedOn pnf, MANY2MANY, will delete target node)
- TO pserver( generic-vnf HostedOn pserver, MANY2MANY, will delete target node)
- TO vnf-image( generic-vnf Uses vnf-image, MANY2ONE, will delete target node)
- TO volume-group( generic-vnf DependsOn volume-group, ONE2MANY, will delete target node)
- TO vserver( generic-vnf HostedOn vserver, ONE2MANY, will delete target node)
- TO virtual-data-center( generic-vnf LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- TO model-ver( generic-vnf IsA model-ver, Many2One, will delete target node)
- TO nos-server( generic-vnf HostedOn nos-server, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource PartOf generic-vnf, MANY2MANY, will delete target node)
- FROM entitlement( entitlement BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM vnfc( vnfc BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM lag-interface( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM license( license BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM l-interface( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM network-profile( network-profile AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf generic-vnf, ONE2MANY, will delete target node)
- FROM site-pair-set( site-pair-set AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM vf-module( vf-module BelongsTo generic-vnf, MANY2ONE, will delete target node)(1)
- FROM line-of-business( line-of-business Uses generic-vnf, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo generic-vnf, MANY2MANY, will delete target node)
- FROM platform( platform Uses generic-vnf, MANY2MANY, will delete target node)
- FROM configuration( configuration PartOf generic-vnf, MANY2ONE, will delete target node)

-(1) IF this GENERIC-VNF node is deleted, this FROM node is DELETED also
-(2) IF this GENERIC-VNF node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this GENERIC-VNF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Client should send valid enumerated value
        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance, managed by MSO
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        ipv4-loopback0-address | No | string |  |  | v4 Loopback0 address
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by generic-vnf.
        is-closed-loop-disabled | Yes | boolean |  |  | used to indicate whether closed loop function is enabled on this node
        license-key | No | string |  |  | OBSOLETE -  do not use
        management-option | No | string |  |  | identifier of managed by ATT or customer
        management-v6-address | No | string |  |  | v6 management address
        mso-catalog-key | No | string |  |  | Corresponds to the SDN-C catalog id used to configure this VCE
        nm-lan-v6-address | No | string |  |  | v6 Loopback address
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational.  Valid values are in-service-path and out-of-service-path.
        orchestration-status | No | string |  |  | Orchestration status of this VNF, used by MSO.
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        regional-resource-zone | No | string |  |  | Regional way of organizing pservers, source of truth should define values
        service-id | No | string |  |  | Unique identifier of service, does not necessarily map to ASDC service models.  SOON TO BE DEPRECATED
        vcpu | No | integer | int64 |  | number of vcpus ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only by uCPE
        vcpu-units | No | string |  |  | units associated with vcpu, used for VNFs with no vservers/flavors, to be used only by uCPE
        vdisk | No | integer | int64 |  | number of vdisks ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only uCPE
        vdisk-units | No | string |  |  | units associated with vdisk, used for VNFs with no vservers/flavors, to be used only by uCPE
        vmemory | No | integer | int64 |  | number of GB of memory ordered for this instance of VNF, used for VNFs with no vservers/flavors, to be used only by uCPE
        vmemory-units | No | string |  |  | units associated with vmemory, used for VNFs with no vservers/flavors, to be used only by uCPE
        vnf-id | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. _d_4edde861e683d9eb8892191339359972:

generic-vnfs Model Structure
----------------------------

Collection of VNFs


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        generic-vnf | No | array of :ref:`generic-vnf <d_ca0376d58aad84ab5c5935fa4c191551>` |  |  | 

.. _d_dda7356f0d26edbabc2e3372aff4a1dd:

host-route Model Structure
--------------------------

###### Related Nodes
- TO subnet( host-route BelongsTo subnet, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this HOST-ROUTE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        host-route-id | Yes | string |  |  | host-route id
        next-hop | Yes | string |  |  | Could be ip-address, hostname, or service-instance
        next-hop-type | No | string |  |  | Should be ip-address, hostname, or service-instance to match next-hop
        route-prefix | Yes | string |  |  | subnet prefix

.. _d_5deb9156f4b39b2c5d33fa6bc051a5f9:

host-routes Model Structure
---------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        host-route | No | array of :ref:`host-route <d_dda7356f0d26edbabc2e3372aff4a1dd>` |  |  | 

.. _d_35920a66c0a09f477c6b314db2a4cca6:

instance-group Model Structure
------------------------------

General mechanism for grouping instances
###### Related Nodes
- TO model( instance-group Targets model, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource MemberOf instance-group, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l3-network( l3-network MemberOf instance-group, MANY2MANY, will delete target node)
- FROM l-interface( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- FROM pnf( pnf MemberOf instance-group, MANY2MANY, will delete target node)
- FROM service-instance( service-instance MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vip-ipv4-address-list( vip-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- FROM vnfc( vnfc MemberOf instance-group, MANY2MANY, will delete target node)
- FROM tenant( tenant MemberOf instance-group, ONE2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        description | Yes | string |  |  | Descriptive text to help identify the usage of this instance-group
        id | Yes | string |  |  | Instance Group ID, UUID assigned to this instance.
        instance-group-role | No | string |  |  | role of the instance group.
        model-invariant-id | No | string |  |  | ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | ASDC model version uid for this resource model.
        sub-type | No | string |  |  | Valid values for ha type are [geo-activeactive, geo-activestandby, local-activeactive, local-activestandby]
        type | Yes | string |  |  | Only valid value today is lower case ha for high availability

.. _d_8da546d164f80a1f7b810999a07a93b5:

instance-groups Model Structure
-------------------------------

Collection of openstack route table references


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        instance-group | No | array of :ref:`instance-group <d_35920a66c0a09f477c6b314db2a4cca6>` |  |  | 

.. _d_ce8b615a6d9f822fd95b8fe73cce678b:

ipsec-configuration Model Structure
-----------------------------------

IPSec configuration node will contain various configuration data for the NMTE VNF. This node will have an edge to the generic-vnf (vnf type = TE). Starting 1607, this data will be populated by SDN-C
###### Related Nodes
- FROM generic-vnf( generic-vnf Uses ipsec-configuration, MANY2ONE, will delete target node)
- FROM vig-server( vig-server BelongsTo ipsec-configuration, MANY2ONE, will delete target node)(1)

-(1) IF this IPSEC-CONFIGURATION node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dpd-frequency | No | string |  |  | Maximum number of DPD before claiming the tunnel is down
        dpd-interval | No | string |  |  | The time between DPD probe
        ike-version | No | string |  |  | can be 1 or 2
        ikev1-am-group-id | No | string |  |  | Group name defined in VIG for clients using aggressive mode
        ikev1-am-password | No | string |  |  | pre-shared key for the above group name
        ikev1-authentication | No | string |  |  | Contains values like md5, sha1, sha256, sha384
        ikev1-dh-group | No | string |  |  | Diffie-Hellman group like DH-GROUP2, DH-GROUP5, DH-GROUP14
        ikev1-encryption | No | string |  |  | Encyption values like 3des-cbc, des-cbc, aes-128-cbc,Â aes-192-cbc, aes-265-cbc
        ikev1-sa-lifetime | No | string |  |  | Lifetime for IKEv1 SA
        ipsec-authentication | No | string |  |  | md5, sha1, sha256, sha384
        ipsec-configuration-id | Yes | string |  |  | UUID of this configuration
        ipsec-encryption | No | string |  |  | 3des-cbc, des-cbc, aes-128-cbc,Â aes-192-cbc, aes-265-cbc
        ipsec-pfs | No | string |  |  | enable PFS or not
        ipsec-sa-lifetime | No | string |  |  | Life time for IPSec SA
        requested-customer-name | No | string |  |  | If the DMZ is a custom DMZ, this field will indicate the customer information
        requested-dmz-type | No | string |  |  | ATT can offer a shared DMZ or a DMZ specific to a customer
        requested-encryption-strength | No | string |  |  | Encryption values like 3des-cbc, des-cbc, aes-128-cbc, aes-192-cbc, aes-265-cbc
        requested-vig-address-type | No | string |  |  | Indicate the type of VIG server like AVPN, INTERNET, BOTH
        shared-dmz-network-address | No | string |  |  | Network address of shared DMZ
        xauth-user-password | No | string |  |  | Encrypted using the Juniper $9$ algorithm
        xauth-userid | No | string |  |  | user ID for xAuth, sm-user,ucpeHostName,nmteHostName

.. _d_166d50b0a87ac4cc0d7ce0cce3fbb85f:

ipsec-configurations Model Structure
------------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        ipsec-configuration | No | array of :ref:`ipsec-configuration <d_ce8b615a6d9f822fd95b8fe73cce678b>` |  |  | 

.. _d_216dd2d4c6ea4e87596ad58fa4d61e00:

l-interface Model Structure
---------------------------

Logical interfaces, e.g., a vnic.
###### Related Nodes
- TO generic-vnf( l-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(4)
- TO instance-group( l-interface MemberOf instance-group, MANY2MANY, will delete target node)
- TO l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(2)
- TO newvce( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(4)
- TO p-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(4)
- TO vserver( l-interface BindsTo vserver, MANY2ONE, will delete target node)(4)
- FROM allotted-resource( allotted-resource Uses l-interface, ONE2MANY, will delete target node)
- FROM lag-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(1)
- FROM logical-link( logical-link Source l-interface, ONE2MANY, will delete target node)(1)
- FROM logical-link( logical-link Destination l-interface, ONE2MANY, will delete target node)(1)
- FROM sriov-vf( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(1)
- FROM vlan( vlan LinksTo l-interface, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration AppliesTo l-interface, ONE2MANY, will delete target node)
- FROM forwarder( forwarder ForwardsTo l-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BelongsTo l-interface, MANY2ONE, will delete target node)(4)

-(1) IF this L-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this L-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this L-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-id | No | string |  |  | ID of interface
        interface-name | Yes | string |  |  | Name given to the interface
        interface-role | No | string |  |  | E.g., CUSTOMER, UPLINK, etc.
        is-port-mirrored | Yes | boolean |  |  | boolean indicatating whether or not port is a mirrored.
        macaddr | No | string |  |  | MAC address for the interface
        management-option | No | string |  |  | Whether A&AI should be managing this interface of not. Could have value like CUSTOMER
        network-name | No | string |  |  | Name of the network
        selflink | No | string |  |  | URL to endpoint where AAI can get more details
        v6-wan-link-ip | No | string |  |  | Questionably placed - v6 ip addr of this interface (is in vr-lan-interface from Mary B.

.. _d_0c8fd084d201268a67d1ac5d60a811d9:

l-interfaces Model Structure
----------------------------

Collection of logical interfaces.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        l-interface | No | array of :ref:`l-interface <d_216dd2d4c6ea4e87596ad58fa4d61e00>` |  |  | 

.. _d_81b5118b793ab488e0abd4e4abebd75d:

l3-interface-ipv4-address-list Model Structure
----------------------------------------------

IPv4 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv4-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV4-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv4-address | Yes | string |  |  | IP address
        l3-interface-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. _d_7e4e75c848e45d161cd98b4b8736d86f:

l3-interface-ipv6-address-list Model Structure
----------------------------------------------

IPv6 Address Range
###### Related Nodes
- TO instance-group( l3-interface-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO l3-network( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- TO subnet( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- TO l-interface( l3-interface-ipv6-address-list BelongsTo l-interface, MANY2ONE, will delete target node)(4)
- TO vlan( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(4)
- TO vnfc( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this L3-INTERFACE-IPV6-ADDRESS-LIST is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        l3-interface-ipv6-address | Yes | string |  |  | IP address
        l3-interface-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. _d_047b950ebe4fa983bb68a48097c4ceef:

l3-network Model Structure
--------------------------

Generic network definition
###### Related Nodes
- TO instance-group( l3-network MemberOf instance-group, MANY2MANY, will delete target node)
- TO network-policy( l3-network Uses network-policy, MANY2MANY, will delete target node)
- TO route-table-reference( l3-network Uses route-table-reference, MANY2MANY, will delete target node)
- TO vpn-binding( l3-network Uses vpn-binding, MANY2MANY, will delete target node)
- TO model-ver( l3-network IsA model-ver, Many2One, will delete target node)
- FROM allotted-resource( allotted-resource PartOf l3-network, MANY2MANY, will delete target node)
- FROM cloud-region( cloud-region Uses l3-network, MANY2MANY, will delete target node)
- FROM complex( complex Uses l3-network, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses l3-network, MANY2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf l3-network, MANY2MANY, will delete target node)
- FROM ctag-assignment( ctag-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM segmentation-assignment( segmentation-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf l3-network, ONE2MANY, will delete target node)
- FROM subnet( subnet BelongsTo l3-network, MANY2ONE, will delete target node)(1)
- FROM tenant( tenant Uses l3-network, MANY2MANY, will delete target node)
- FROM vf-module( vf-module DependsOn l3-network, MANY2MANY, will delete target node)
- FROM configuration( configuration PartOf l3-network, ONE2ONE, will delete target node)

-(1) IF this L3-NETWORK node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-bound-to-vpn | Yes | boolean |  |  | Set to true if bound to VPN
        network-id | Yes | string |  |  | Network ID, should be uuid. Unique across A&AI.
        network-name | Yes | string |  |  | Name of the network, governed by some naming convention..
        network-role | No | string |  |  | Role the network plans - who defines these values?
        network-role-instance | No | integer | int64 |  | network role instance
        network-technology | No | string |  |  | Network technology - who defines these values?
        network-type | No | string |  |  | Type of the network - who defines these values?
        neutron-network-id | No | string |  |  | Neutron network id of this Interface
        service-id | No | string |  |  | Unique identifier of service from ASDC.  Does not strictly map to ASDC services.  SOON TO BE DEPRECATED

.. _d_6213ed8f0b563e4a658749c3c9e26be3:

l3-networks Model Structure
---------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        l3-network | No | array of :ref:`l3-network <d_047b950ebe4fa983bb68a48097c4ceef>` |  |  | 

.. _d_f61b51fbced469e399bfd4eae4c3ea5d:

lag-interface Model Structure
-----------------------------

Link aggregate interface
###### Related Nodes
- TO generic-vnf( lag-interface BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO lag-link( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(2)
- TO logical-link( lag-interface Uses logical-link, MANY2MANY, will delete target node)(2)
- TO p-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- TO l-interface( lag-interface Uses l-interface, ONE2MANY, will delete target node)
- TO pnf( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM l-interface( l-interface BelongsTo lag-interface, MANY2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo lag-interface, MANY2MANY, will delete target node)

-(1) IF this LAG-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this LAG-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this LAG-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-description | No | string |  |  | Human friendly text regarding this interface.
        interface-name | Yes | string |  |  | Name that identifies the link aggregate interface

.. _d_9ce1cf98ae45904bf479ca51e5da0fa7:

lag-interfaces Model Structure
------------------------------

Collection of link aggregate interfaces.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        lag-interface | No | array of :ref:`lag-interface <d_f61b51fbced469e399bfd4eae4c3ea5d>` |  |  | 

.. _d_cbdb270b673401242656918a76f241e1:

lag-link Model Structure
------------------------

LAG links can connect lag-interfaces
###### Related Nodes
- FROM lag-interface( lag-interface LinksTo lag-link, MANY2MANY, will delete target node)(3)
- FROM logical-link( logical-link Uses lag-link, MANY2MANY, will delete target node)

-(3) IF this FROM node is deleted, this LAG-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        link-name | Yes | string |  |  | Alphabetical concatenation of lag-interface names

.. _d_87f285f8e151e933dc48cc08e43148e0:

lag-links Model Structure
-------------------------

Collection of link aggregation connections


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        lag-link | No | array of :ref:`lag-link <d_cbdb270b673401242656918a76f241e1>` |  |  | 

.. _d_25a47ee6575bfa6d53284f5bf0598b55:

license Model Structure
-----------------------

Metadata for license group.
###### Related Nodes
- TO generic-vnf( license BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO vce( license BelongsTo vce, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this LICENSE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-uuid | Yes | string |  |  | Unique ID for the license group the resource belongs to, should be uuid.
        resource-uuid | Yes | string |  |  | Unique ID of a license resource.

.. _d_636d252bff0930234ba2f611270573ae:

licenses Model Structure
------------------------

Licenses to be allocated across resources, keyed by group-uuid and resource-uuid, related to license management


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        license | No | array of :ref:`license <d_25a47ee6575bfa6d53284f5bf0598b55>` |  |  | 

.. _d_7eb76ca0226a80f22632f9b59f7fcf60:

logical-link Model Structure
----------------------------

Logical links generally connect l-interfaces but are used to express logical connectivity between two points
###### Related Nodes
- TO l-interface( logical-link Source l-interface, ONE2MANY, will delete target node)(4)
- TO l-interface( logical-link Destination l-interface, ONE2MANY, will delete target node)(4)
- TO cloud-region( logical-link LocatedIn cloud-region, MANY2MANY, will delete target node)
- TO generic-vnf( logical-link BridgedTo generic-vnf, MANY2MANY, will delete target node)
- TO lag-link( logical-link Uses lag-link, MANY2MANY, will delete target node)
- TO logical-link( logical-link Uses logical-link, MANY2MANY, will delete target node)
- TO pnf( logical-link BridgedTo pnf, MANY2MANY, will delete target node)
- TO pserver( logical-link BridgedTo pserver, MANY2MANY, will delete target node)
- TO vpn-binding( logical-link Uses vpn-binding, MANY2MANY, will delete target node)
- TO virtual-data-center( logical-link LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- TO model-ver( logical-link IsA model-ver, Many2One, will delete target node)
- FROM configuration( configuration Uses logical-link, ONE2MANY, will delete target node)(3)
- FROM lag-interface( lag-interface Uses logical-link, MANY2MANY, will delete target node)(3)
- FROM l-interface( l-interface LinksTo logical-link, MANY2MANY, will delete target node)(3)
- FROM p-interface( p-interface LinksTo logical-link, MANY2ONE, will delete target node)
- FROM service-instance( service-instance Uses logical-link, ONE2MANY, will delete target node)(3)
- FROM vlan( vlan Uses logical-link, MANY2MANY, will delete target node)(3)
- FROM logical-link( logical-link Uses logical-link, MANY2MANY, will delete target node)

-(3) IF this FROM node is deleted, this LOGICAL-LINK is DELETED also
-(4) IF this TO node is deleted, this LOGICAL-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        ip-version | No | string |  |  | v4, v6, or ds for dual stack (should be att-ip-version)
        link-name | Yes | string |  |  | e.g., evc-name, or vnf-nameA_interface-nameA_vnf-nameZ_interface-nameZ
        link-type | Yes | string |  |  | Type of logical link, e.g., evc
        routing-protocol | No | string |  |  | For example, static or BGP
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. _d_5754ab023790f6679c93fa07c0423eaf:

logical-links Model Structure
-----------------------------

Collection of logical connections


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        logical-link | No | array of :ref:`logical-link <d_7eb76ca0226a80f22632f9b59f7fcf60>` |  |  | 

.. _d_34f85031375a8205fdb27ce838465ed8:

metadata Model Structure
------------------------

Collection of metadatum (key/value pairs)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metadatum | No | array of :ref:`metadatum <d_86c5a7078292838659223f545f7cca0a>` |  |  | 

.. _d_86c5a7078292838659223f545f7cca0a:

metadatum Model Structure
-------------------------

Key/value pairs
###### Related Nodes
- TO configuration( metadatum BelongsTo configuration, MANY2ONE, will delete target node)(4)
- TO connector( metadatum BelongsTo connector, MANY2ONE, will delete target node)(4)
- TO image( metadatum BelongsTo image, MANY2ONE, will delete target node)(4)
- TO model-ver( metadatum BelongsTo model-ver, MANY2ONE, will delete target node)(4)
- TO service-instance( metadatum BelongsTo service-instance, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this METADATUM is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        metaname | Yes | string |  |  | 
        metaval | Yes | string |  |  | 

.. _d_bab72bd550d1cede8812be7ef191a566:

multicast-configuration Model Structure
---------------------------------------

###### Related Nodes
- FROM vlan( vlan Uses multicast-configuration, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        multicast-configuration-id | Yes | string |  |  | Unique id of multicast configuration.
        multicast-protocol | Yes | string |  |  | protocol of multicast configuration
        rp-type | Yes | string |  |  | rp type of multicast configuration

.. _d_9d7a81b86bf9a6f6cef2fca2948e3462:

multicast-configurations Model Structure
----------------------------------------

multicast configuration of generic-vnf ip-address


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        multicast-configuration | No | array of :ref:`multicast-configuration <d_bab72bd550d1cede8812be7ef191a566>` |  |  | 

.. _d_e83d2b2d3687cb1acac1b6557556e6f0:

network Model Structure
-----------------------

Namespace for network inventory resources.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        configurations | No | array of :ref:`configuration <d_ecdcd4e52dd9916ee05ece1e631a86ce>` |  |  | 
        forwarding-paths | No | array of :ref:`forwarding-path <d_46de7db8148c637b12aed1c8966df252>` |  |  | 
        generic-vnfs | No | array of :ref:`generic-vnf <d_ca0376d58aad84ab5c5935fa4c191551>` |  |  | 
        instance-groups | No | array of :ref:`instance-group <d_35920a66c0a09f477c6b314db2a4cca6>` |  |  | 
        ipsec-configurations | No | array of :ref:`ipsec-configuration <d_ce8b615a6d9f822fd95b8fe73cce678b>` |  |  | 
        l3-networks | No | array of :ref:`l3-network <d_047b950ebe4fa983bb68a48097c4ceef>` |  |  | 
        lag-links | No | array of :ref:`lag-link <d_cbdb270b673401242656918a76f241e1>` |  |  | 
        logical-links | No | array of :ref:`logical-link <d_7eb76ca0226a80f22632f9b59f7fcf60>` |  |  | 
        multicast-configurations | No | array of :ref:`multicast-configuration <d_bab72bd550d1cede8812be7ef191a566>` |  |  | 
        network-policies | No | array of :ref:`network-policy <d_aa7a59093b89f4be48a639c6f753fe11>` |  |  | 
        newvces | No | array of :ref:`newvce <d_f33ede34598aabc8a5f8eb99a9cd6644>` |  |  | 
        physical-links | No | array of :ref:`physical-link <d_980768c590515d54b0e3382b5e4bfd3d>` |  |  | 
        pnfs | No | array of :ref:`pnf <d_547a9c65ac53624f14a8e98650ddef2c>` |  |  | 
        route-table-references | No | array of :ref:`route-table-reference <d_63cf8e39dbcd58a4d5b5e7930349620b>` |  |  | 
        site-pair-sets | No | array of :ref:`site-pair-set <d_cf4788ba8f7a5868a474c9a551d891d9>` |  |  | 
        vces | No | array of :ref:`vce <d_3c2bacb31fb510463082f50d89f18144>` |  |  | 
        vnfcs | No | array of :ref:`vnfc <d_e8c782e6607830c8202e32933efce766>` |  |  | 
        vpls-pes | No | array of :ref:`vpls-pe <d_00ecef75a8d71ee9d4abc90feff3a5fb>` |  |  | 
        vpn-bindings | No | array of :ref:`vpn-binding <d_97f7255ccc461fc49496f9e8a1c0d079>` |  |  | 
        zones | No | array of :ref:`zone <d_3105cb0de2c0b0960a36d71dc07146a2>` |  |  | 

.. _d_f3997096d7ab8c5bd468be0172d7169b:

network-policies Model Structure
--------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        network-policy | No | array of :ref:`network-policy <d_aa7a59093b89f4be48a639c6f753fe11>` |  |  | 

.. _d_aa7a59093b89f4be48a639c6f753fe11:

network-policy Model Structure
------------------------------

###### Related Nodes
- FROM allotted-resource( allotted-resource Uses network-policy, ONE2ONE, will delete target node)
- FROM l3-network( l3-network Uses network-policy, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | ID for the openStack Heat instance
        network-policy-fqdn | No | string |  |  | Contrail FQDN for the policy
        network-policy-id | Yes | string |  |  | UUID representing unique key to this instance

.. _d_f33ede34598aabc8a5f8eb99a9cd6644:

newvce Model Structure
----------------------

This object fills in the gaps from vce that were incorporated into generic-vnf.  This object will be retired with vce.
###### Related Nodes
- FROM l-interface( l-interface BelongsTo newvce, MANY2ONE, will delete target node)(1)

-(1) IF this NEWVCE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Client should send valid enumerated value.
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by GenericVnf (v4-loopback0-ip-address).
        license-key | No | string |  |  | OBSOLETE -  do not use
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational
        prov-status | No | string |  |  | Trigger for operational monitoring of this VNF by BAU Service Assurance systems.
        vnf-id2 | Yes | string |  |  | Unique id of VNF, can't use same attribute name right now until we promote this new object
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.

.. _d_45e047a390e134c3d841a39c216b94fc:

newvces Model Structure
-----------------------

This object fills in the gaps from vce that were incorporated into generic-vnf.  This object will be retired with vce.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        newvce | No | array of :ref:`newvce <d_f33ede34598aabc8a5f8eb99a9cd6644>` |  |  | 

.. _d_1b89d04f0a7bdc0f7445ec2a28d86140:

p-interface Model Structure
---------------------------

Physical interface (e.g., nic)
###### Related Nodes
- TO logical-link( p-interface LinksTo logical-link, MANY2ONE, will delete target node)
- TO physical-link( p-interface LinksTo physical-link, MANY2ONE, will delete target node)(2)
- TO pnf( p-interface BindsTo pnf, MANY2ONE, will delete target node)(4)
- TO pserver( p-interface BindsTo pserver, MANY2ONE, will delete target node)(4)
- TO vpls-pe( p-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(4)
- FROM lag-interface( lag-interface Uses p-interface, MANY2MANY, will delete target node)
- FROM l-interface( l-interface BindsTo p-interface, MANY2ONE, will delete target node)(1)
- FROM sriov-pf( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(1)
- FROM forwarder( forwarder ForwardsTo p-interface, MANY2MANY, will delete target node)

-(1) IF this P-INTERFACE node is deleted, this FROM node is DELETED also
-(2) IF this P-INTERFACE node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this P-INTERFACE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-identifier | No | string |  |  | CLEI or other specification for p-interface hardware.
        interface-name | Yes | string |  |  | Name that identifies the physical interface
        interface-role | No | string |  |  | Role specification for p-interface hardware.
        interface-type | No | string |  |  | Indicates the physical properties of the interface.
        port-description | No | string |  |  | Nature of the services and connectivity on this port.
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        selflink | No | string |  |  | URL to endpoint where AAI can get more details.
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. _d_03f30ea27c9a67097baef45cc092bb44:

p-interfaces Model Structure
----------------------------

Collection of physical interfaces.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        p-interface | No | array of :ref:`p-interface <d_1b89d04f0a7bdc0f7445ec2a28d86140>` |  |  | 

.. _d_980768c590515d54b0e3382b5e4bfd3d:

physical-link Model Structure
-----------------------------

Collection of physical connections, typically between p-interfaces
###### Related Nodes
- FROM p-interface( p-interface LinksTo physical-link, MANY2ONE, will delete target node)(3)

-(3) IF this FROM node is deleted, this PHYSICAL-LINK is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        circuit-id | No | string |  |  | Circuit it
        dual-mode | No | string |  |  | Dual access mode (e.g., primary, secondary
        link-name | Yes | string |  |  | e.g., hostnameA_p-connection_nameA_hostnameZ+p_connection-nameZ
        management-option | No | string |  |  | To provide information on who manages this circuit. A&AI or 3rd party transport provider
        service-provider-bandwidth-down-units | No | string |  |  | Units for downstream BW value
        service-provider-bandwidth-down-value | No | integer | int32 |  | Downstream Bandwidth value agreed with the service provider
        service-provider-bandwidth-up-units | No | string |  |  | Units for the upstream BW value
        service-provider-bandwidth-up-value | No | integer | int32 |  | Upstream Bandwidth value agreed with the service provider
        service-provider-name | No | string |  |  | Name of the service Provider on this link.
        speed-units | No | string |  |  | Captures the units corresponding to the speed
        speed-value | No | string |  |  | Captures the numeric part of the speed

.. _d_36c560870e8e3b9cdc0070723dab2279:

physical-links Model Structure
------------------------------

Collection of physical connections, typically between p-interfaces


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        physical-link | No | array of :ref:`physical-link <d_980768c590515d54b0e3382b5e4bfd3d>` |  |  | 

.. _d_547a9c65ac53624f14a8e98650ddef2c:

pnf Model Structure
-------------------

PNF represents a physical network function. typically equipment used in the D1 world. in 1607, this will be populated by SDN-C to represent a premises router that a uCPE connects to. But this can be used to represent any physical device that is not an AIC node or uCPE.
###### Related Nodes
- TO complex( pnf LocatedIn complex, MANY2ONE, will delete target node)
- TO instance-group( pnf MemberOf instance-group, MANY2MANY, will delete target node)
- TO zone( pnf LocatedIn zone, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn pnf, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo pnf, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo pnf, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo pnf, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf pnf, ONE2MANY, will delete target node)
- FROM configuration( configuration AppliesTo pnf, ONE2MANY, will delete target node)
- FROM configuration( configuration AppliesTo pnf, MANY2MANY, will delete target node)

-(1) IF this PNF node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equip-model | No | string |  |  | Equipment model.  Source of truth should define valid values.
        equip-type | No | string |  |  | Equipment type.  Source of truth should define valid values.
        equip-vendor | No | string |  |  | Equipment vendor.  Source of truth should define valid values.
        frame-id | No | string |  |  | ID of the physical frame (relay rack) where pnf is installed.
        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        inv-status | No | string |  |  | CANOPI's inventory status.  Only set with values exactly as defined by CANOPI.
        ipaddress-v4-aim | No | string |  |  | IPV4 AIM address
        ipaddress-v4-loopback-0 | No | string |  |  | IPV4 Loopback 0 address
        ipaddress-v4-oam | No | string |  |  | ipv4-oam-address with new naming convention for IP addresses
        ipaddress-v6-aim | No | string |  |  | IPV6 AIM address
        ipaddress-v6-loopback-0 | No | string |  |  | IPV6 Loopback 0 address
        ipaddress-v6-oam | No | string |  |  | IPV6 OAM address
        management-option | No | string |  |  | identifier of managed by ATT or customer
        pnf-id | No | string |  |  | id of pnf
        pnf-name | Yes | string |  |  | unique name of Physical Network Function.
        pnf-name2 | No | string |  |  | name of Physical Network Function.
        pnf-name2-source | No | string |  |  | source of name2
        selflink | No | string |  |  | URL to endpoint where AAI can get more details.
        serial-number | No | string |  |  | Serial number of the device
        sw-version | No | string |  |  | sw-version is the version of SW for the hosted application on the PNF.

.. _d_d8b7f452c8f87517ad821c19c103dbed:

pnfs Model Structure
--------------------

Collection of Physical Network Functions.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pnf | No | array of :ref:`pnf <d_547a9c65ac53624f14a8e98650ddef2c>` |  |  | 

.. _d_62a0d04ea171fcc6a1a4dbc52667d6e7:

port-group Model Structure
--------------------------

Used to capture the network interfaces of this VCE
###### Related Nodes
- TO vce( port-group BelongsTo vce, MANY2ONE, will delete target node)(4)
- FROM cvlan-tag( cvlan-tag BelongsTo port-group, MANY2ONE, will delete target node)(1)

-(1) IF this PORT-GROUP node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this PORT-GROUP is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        interface-id | Yes | string |  |  | Unique ID of the interface
        interface-role | No | string |  |  | Role assigned to this Interface, should use values as defined in ECOMP Yang models.
        neutron-network-id | No | string |  |  | Neutron network id of this Interface
        neutron-network-name | No | string |  |  | Neutron network name of this Interface

.. _d_2a16a39ed468f1a204a5ef25cc880537:

port-groups Model Structure
---------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        port-group | No | array of :ref:`port-group <d_62a0d04ea171fcc6a1a4dbc52667d6e7>` |  |  | 

.. _d_2e28897978d0dd1b59eef0f2d7124064:

related-to-property Model Structure
-----------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        property-key | No | string |  |  | Key part of a key/value pair
        property-value | No | string |  |  | Value part of a key/value pair

.. _d_3a86fb483f7ec2bab2dad50fd3a6d612:

relationship Model Structure
----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        related-link | No | string |  |  | URL to the object in A&AI.
        related-to | No | string |  |  | A keyword provided by A&AI to indicate type of node.
        related-to-property | No | array of :ref:`related-to-property <d_2e28897978d0dd1b59eef0f2d7124064>` |  |  | 
        relationship-data | No | array of :ref:`relationship-data <d_4f9097a364db6ef400e8e74489f41157>` |  |  | 
        relationship-label | No | string |  |  | The edge label for this relationship.

.. _d_4f9097a364db6ef400e8e74489f41157:

relationship-data Model Structure
---------------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        relationship-key | Yes | string |  |  | A keyword provided by A&AI to indicate an attribute.
        relationship-value | Yes | string |  |  | Value of the attribute.

.. _d_63cf8e39dbcd58a4d5b5e7930349620b:

route-table-reference Model Structure
-------------------------------------

Openstack route table reference.
###### Related Nodes
- FROM l3-network( l3-network Uses route-table-reference, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        route-table-reference-fqdn | Yes | string |  |  | FQDN entry in the route table.
        route-table-reference-id | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.

.. _d_c0613c10116e974b94f29e871eb43514:

route-table-references Model Structure
--------------------------------------

Collection of openstack route table references


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        route-table-reference | No | array of :ref:`route-table-reference <d_63cf8e39dbcd58a4d5b5e7930349620b>` |  |  | 

.. _d_5fa5ed2e9b1d2543dc43cc50bd5a9b8e:

route-target Model Structure
----------------------------

Route target information
###### Related Nodes
- TO vpn-binding( route-target BelongsTo vpn-binding, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this ROUTE-TARGET is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        global-route-target | Yes | string |  |  | Number used to identify an RT, globally unique in the network
        route-target-role | Yes | string |  |  | Role assigned to this route target

.. _d_0431ab875c5ff082fd7fdea4af486c85:

route-targets Model Structure
-----------------------------

Collection of route target information


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        route-target | No | array of :ref:`route-target <d_5fa5ed2e9b1d2543dc43cc50bd5a9b8e>` |  |  | 

.. _d_83776bc44ba8e488b933402ad9fc052b:

routing-instance Model Structure
--------------------------------

###### Related Nodes
- TO site-pair-set( routing-instance BelongsTo site-pair-set, MANY2ONE, will delete target node)(4)
- FROM site-pair( site-pair BelongsTo routing-instance, MANY2ONE, will delete target node)(1)

-(1) IF this ROUTING-INSTANCE node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this ROUTING-INSTANCE is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        routing-instance-id | Yes | string |  |  | Unique id of routing instance
        rpm-owner | No | string |  |  | rpm owner

.. _d_ee2ad2271206830b82d31b6dbb919aa0:

routing-instances Model Structure
---------------------------------

set of probes related to generic-vnf routing instance


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        routing-instance | No | array of :ref:`routing-instance <d_83776bc44ba8e488b933402ad9fc052b>` |  |  | 

.. _d_6a8405a0fa38d927c63800a810ddbf23:

segmentation-assignment Model Structure
---------------------------------------

Openstack segmentation assignment.
###### Related Nodes
- TO l3-network( segmentation-assignment BelongsTo l3-network, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this SEGMENTATION-ASSIGNMENT is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        segmentation-id | Yes | string |  |  | Route Table Reference id, UUID assigned to this instance.

.. _d_59953d5041a09337d60282b352b28420:

segmentation-assignments Model Structure
----------------------------------------

Collection of openstack segmentation assignments


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        segmentation-assignment | No | array of :ref:`segmentation-assignment <d_6a8405a0fa38d927c63800a810ddbf23>` |  |  | 

.. _d_388ae35852e62ce685c588eba0b2274f:

site-pair Model Structure
-------------------------

###### Related Nodes
- TO routing-instance( site-pair BelongsTo routing-instance, MANY2ONE, will delete target node)(4)
- FROM class-of-service( class-of-service BelongsTo site-pair, MANY2ONE, will delete target node)(1)

-(1) IF this SITE-PAIR node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this SITE-PAIR is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        destination-equip-type | No | string |  |  | The type of destinatination equipment. Could be Router, UCPE, etc.
        destination-hostname | No | string |  |  | Hostname of the destination equipment to which SLAs are measured against.
        destination-ip | No | string |  |  | Prefix address
        ip-version | No | string |  |  | ip version, v4, v6
        site-pair-id | Yes | string |  |  | unique identifier of probe
        source-ip | No | string |  |  | Prefix address

.. _d_cf4788ba8f7a5868a474c9a551d891d9:

site-pair-set Model Structure
-----------------------------

Set of instances for probes used to measure service level agreements
###### Related Nodes
- TO generic-vnf( site-pair-set AppliesTo generic-vnf, MANY2MANY, will delete target node)
- FROM routing-instance( routing-instance BelongsTo site-pair-set, MANY2ONE, will delete target node)(1)

-(1) IF this SITE-PAIR-SET node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        site-pair-set-id | Yes | string |  |  | Unique id of site pair set.

.. _d_8f76c98bfa8eac14c188aac3dcdcfe1e:

site-pair-sets Model Structure
------------------------------

Collection of sets of instances for probes related to generic-vnf


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        site-pair-set | No | array of :ref:`site-pair-set <d_cf4788ba8f7a5868a474c9a551d891d9>` |  |  | 

.. _d_4e23b13feaa5f89020b91270bf9120f0:

site-pairs Model Structure
--------------------------

probe within a set


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        site-pair | No | array of :ref:`site-pair <d_388ae35852e62ce685c588eba0b2274f>` |  |  | 

.. _d_1697b972bd787b44ec7d6037a9dd8b76:

sriov-pf Model Structure
------------------------

SR-IOV Physical Function
###### Related Nodes
- TO p-interface( sriov-pf BelongsTo p-interface, ONE2ONE, will delete target node)(4)
- FROM sriov-vf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-PF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pf-pci-id | Yes | string |  |  | Identifier for the sriov-pf

.. _d_8d13421a51eff3978d96a25499cd76c4:

sriov-pfs Model Structure
-------------------------

Collection of SR-IOV Physical Functions.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        sriov-pf | No | array of :ref:`sriov-pf <d_1697b972bd787b44ec7d6037a9dd8b76>` |  |  | 

.. _d_74e32e7a157ccfd9e03147e9f35247de:

sriov-vf Model Structure
------------------------

SR-IOV Virtual Function (not to be confused with virtual network function)
###### Related Nodes
- TO l-interface( sriov-vf BelongsTo l-interface, ONE2ONE, will delete target node)(4)
- TO sriov-pf( sriov-vf Uses sriov-pf, MANY2ONE, will delete target node)

-(4) IF this TO node is deleted, this SRIOV-VF is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pci-id | Yes | string |  |  | PCI ID used to identify the sriov-vf
        vf-broadcast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows all broadcast traffic to reach the VM
        vf-insert-stag | No | boolean |  |  | This option, if set to true, instructs to insert outer tag after traffic comes out of VM.
        vf-link-status | No | string |  |  | This option is used to set the link status.  Valid values as of 1607 are on, off, and auto.
        vf-mac-anti-spoof-check | No | boolean |  |  | This option ensures anti MAC spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-mac-filter | No | string |  |  | When MAC filters are specified, VF-agent service configures VFs to do MAC level filtering before the traffic is passed to VM.
        vf-mirrors | No | string |  |  | This option defines the set of Mirror objects which essentially mirrors the traffic from source to set of collector VNF Ports.
        vf-unknown-multicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown multicast traffic to reach the VM
        vf-unknown-unicast-allow | No | boolean |  |  | This option, if set to true, sets the VF in promiscuous mode and allows unknown unicast traffic to reach the VM
        vf-vlan-anti-spoof-check | No | boolean |  |  | This option ensures anti VLAN spoof checks are done at the VF level to comply with security. The disable check will also be honored per the VNF needs for trusted VMs.
        vf-vlan-filter | No | string |  |  | This metadata provides option to specify list of VLAN filters applied on VF to pass the traffic to VM.
        vf-vlan-strip | No | boolean |  |  | When this field is set to true, VF will configured to strip the outer TAG before the traffic is passed to VM.

.. _d_baf16b6ae0f9c14694d5ef150e0aaaa8:

sriov-vfs Model Structure
-------------------------

Collection of SR-IOV Virtual Functions.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        sriov-vf | No | array of :ref:`sriov-vf <d_74e32e7a157ccfd9e03147e9f35247de>` |  |  | 

.. _d_8bf9ab8e091d31d615f38609e36fcc28:

subnet Model Structure
----------------------

###### Related Nodes
- TO l3-network( subnet BelongsTo l3-network, MANY2ONE, will delete target node)(4)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM host-route( host-route BelongsTo subnet, MANY2ONE, will delete target node)(1)
- FROM vip-ipv4-address-list( vip-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)

-(1) IF this SUBNET node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this SUBNET is DELETED also
-SUBNET cannot be deleted if related to L3-INTERFACE-IPV4-ADDRESS-LIST,L3-INTERFACE-IPV6-ADDRESS-LIST,VIP-IPV4-ADDRESS-LIST,VIP-IPV6-ADDRESS-LIST


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cidr-mask | No | string |  |  | cidr mask
        dhcp-enabled | Yes | boolean |  |  | dhcp enabled
        dhcp-end | No | string |  |  | the last address reserved for use by dhcp
        dhcp-start | No | string |  |  | the start address reserved for use by dhcp
        gateway-address | No | string |  |  | gateway ip address
        ip-assignment-direction | No | string |  |  | ip address assignment direction of the subnet
        ip-version | No | string |  |  | ip version
        network-start-address | No | string |  |  | network start address
        neutron-subnet-id | No | string |  |  | Neutron id of this subnet
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by MSO
        subnet-id | Yes | string |  |  | Subnet ID, should be UUID.
        subnet-name | No | string |  |  | Name associated with the subnet.
        subnet-role | No | string |  |  | role of the subnet, referenced when assigning IPs

.. _d_40a97799062e9021d5bf688f1dfdaf2d:

subnets Model Structure
-----------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        subnet | No | array of :ref:`subnet <d_8bf9ab8e091d31d615f38609e36fcc28>` |  |  | 

.. _d_3c2bacb31fb510463082f50d89f18144:

vce Model Structure
-------------------

Virtual Customer Edge Router, used specifically for Gamma.  This object is deprecated.
###### Related Nodes
- TO availability-zone( vce Uses availability-zone, MANY2MANY, will delete target node)
- TO complex( vce LocatedIn complex, MANY2MANY, will delete target node)
- TO vserver( vce HostedOn vserver, ONE2MANY, will delete target node)
- FROM entitlement( entitlement BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM license( license BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM port-group( port-group BelongsTo vce, MANY2ONE, will delete target node)(1)
- FROM service-instance( service-instance ComposedOf vce, ONE2MANY, will delete target node)

-(1) IF this VCE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-role | No | string |  |  | Network role being played by this VNF
        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance, managed by MSO
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure vce, also used for troubleshooting and is IP used for traps generated by VCE.
        license-key | No | string |  |  | OBSOLETE -  do not use
        mso-catalog-key | No | string |  |  | Corresponds to the SDN-C catalog id used to configure this VCE
        operational-status | No | string |  |  | Indicator for whether the resource is considered operational
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by MSO
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        regional-resource-zone | No | string |  |  | Regional way of organizing pservers, source of truth should define values
        service-id | No | string |  |  | Unique identifier of service, does not strictly map to ASDC services, SOON TO BE DEPRECATED.
        v6-vce-wan-address | No | string |  |  | Valid v6 IP address for the WAN Link on this router.  Implied length of /64.
        vnf-id | Yes | string |  |  | Unique id of VNF.  This is unique across the graph.
        vnf-name | Yes | string |  |  | Name of VNF.
        vnf-name2 | No | string |  |  | Alternate name of VNF.
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.
        vpe-id | No | string |  |  | Unique ID of VPE connected to this VCE.

.. _d_1ad5473da4dbc087adc8f617b4685758:

vces Model Structure
--------------------

Collection of Virtual Customer Edge Routers, used specifically for Gamma.  This object is deprecated.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vce | No | array of :ref:`vce <d_3c2bacb31fb510463082f50d89f18144>` |  |  | 

.. _d_127ac1ef6709b25c11c618a1545574af:

vf-module Model Structure
-------------------------

a deployment unit of VNFCs
###### Related Nodes
- TO generic-vnf( vf-module BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO l3-network( vf-module DependsOn l3-network, MANY2MANY, will delete target node)
- TO vnfc( vf-module Uses vnfc, ONE2MANY, will delete target node)
- TO volume-group( vf-module Uses volume-group, ONE2ONE, will delete target node)
- TO vserver( vf-module Uses vserver, ONE2MANY, will delete target node)
- TO model-ver( vf-module IsA model-ver, Many2One, will delete target node)

-(4) IF this TO node is deleted, this VF-MODULE is DELETED also
-VF-MODULE cannot be deleted if related to VNFC


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | Heat stack id corresponding to this instance.
        is-base-vf-module | Yes | boolean |  |  | used to indicate whether or not this object is base vf module
        orchestration-status | No | string |  |  | orchestration status of this vf-module, mastered by MSO
        vf-module-id | Yes | string |  |  | Unique ID of vf-module.
        vf-module-name | No | string |  |  | Name of vf-module

.. _d_0b0879f929cd039f865603db3ee61d38:

vf-modules Model Structure
--------------------------

Collection of vf-modules, a deployment unit of VNFCs


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vf-module | No | array of :ref:`vf-module <d_127ac1ef6709b25c11c618a1545574af>` |  |  | 

.. _d_bdb5565b83d0a58e6d00c44e876d490e:

vig-server Model Structure
--------------------------

vig-server contains information about a vig server used for IPSec-configuration. Populated by SDN-C from 1607
###### Related Nodes
- TO ipsec-configuration( vig-server BelongsTo ipsec-configuration, MANY2ONE, will delete target node)(4)

-(4) IF this TO node is deleted, this VIG-SERVER is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        ipaddress-v4-vig | No | string |  |  | v4 IP of the vig server
        ipaddress-v6-vig | No | string |  |  | v6 IP of the vig server
        vig-address-type | Yes | string |  |  | indicates whether the VIG is for AVPN or INTERNET

.. _d_20978d50d6b001b6a627208cf592e4b7:

vig-servers Model Structure
---------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vig-server | No | array of :ref:`vig-server <d_bdb5565b83d0a58e6d00c44e876d490e>` |  |  | 

.. _d_02b51d5ca6048fc99565091bdd4861a7:

vlan Model Structure
--------------------

Definition of vlan
###### Related Nodes
- TO l-interface( vlan LinksTo l-interface, MANY2ONE, will delete target node)(4)
- TO logical-link( vlan Uses logical-link, MANY2MANY, will delete target node)(2)
- TO multicast-configuration( vlan Uses multicast-configuration, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource PartOf vlan, MANY2MANY, will delete target node)
- FROM service-instance( service-instance ComposedOf vlan, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vlan, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration PartOf vlan, ONE2ONE, will delete target node)

-(1) IF this VLAN node is deleted, this FROM node is DELETED also
-(2) IF this VLAN node is deleted, this TO node is DELETED also
-(4) IF this TO node is deleted, this VLAN is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag
        vlan-interface | Yes | string |  |  | String that identifies the interface

.. _d_6f65ab4f5b06274bbbc0e525300c2402:

vlans Model Structure
---------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vlan | No | array of :ref:`vlan <d_02b51d5ca6048fc99565091bdd4861a7>` |  |  | 

.. _d_e8c782e6607830c8202e32933efce766:

vnfc Model Structure
--------------------

###### Related Nodes
- TO generic-vnf( vnfc BelongsTo generic-vnf, MANY2ONE, will delete target node)(4)
- TO instance-group( vnfc MemberOf instance-group, MANY2MANY, will delete target node)
- TO vip-ipv4-address-list( vnfc Uses vip-ipv4-address-list, MANY2MANY, will delete target node)
- TO vip-ipv6-address-list( vnfc Uses vip-ipv6-address-list, MANY2MANY, will delete target node)
- TO vserver( vnfc HostedOn vserver, ONE2MANY, will delete target node)
- FROM vf-module( vf-module Uses vnfc, ONE2MANY, will delete target node)
- FROM l3-interface-ipv4-address-list( l3-interface-ipv4-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(1)
- FROM l3-interface-ipv6-address-list( l3-interface-ipv6-address-list BelongsTo vnfc, MANY2ONE, will delete target node)(1)

-(1) IF this VNFC node is deleted, this FROM node is DELETED also
-(4) IF this TO node is deleted, this VNFC is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-notation | No | string |  |  | Group notation of VNFC
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true)
        ipaddress-v4-oam-vip | No | string |  |  | Oam V4 vip address of this vnfc
        is-closed-loop-disabled | Yes | boolean |  |  | used to indicate whether closed loop function is enabled on this node
        model-invariant-id | No | string |  |  | the ASDC model id for this resource or service model.
        model-version-id | No | string |  |  | the ASDC model version for this resource or service model.
        nfc-function | Yes | string |  |  | English description of function that the specific resource deployment is providing. Assigned as part of the customization of a resource in a service
        nfc-naming-code | Yes | string |  |  | Short code that is used in naming instances of the item being modeled
        orchestration-status | No | string |  |  | Orchestration status of this VNF, mastered by APP-C
        prov-status | No | string |  |  | prov status of this vnfc
        vnfc-name | Yes | string |  |  | Unique ID of vnfc.

.. _d_6436a1c047604dd6f2ff225ea54e4caf:

vnfcs Model Structure
---------------------

virtual network components associated with a vserver from application controller.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vnfc | No | array of :ref:`vnfc <d_e8c782e6607830c8202e32933efce766>` |  |  | 

.. _d_00ecef75a8d71ee9d4abc90feff3a5fb:

vpls-pe Model Structure
-----------------------

VPLS Provider Edge routers.
###### Related Nodes
- TO complex( vpls-pe LocatedIn complex, MANY2ONE, will delete target node)
- TO ctag-pool( vpls-pe Uses ctag-pool, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo vpls-pe, MANY2ONE, will delete target node)(1)

-(1) IF this VPLS-PE node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        equipment-name | Yes | string |  |  | 
        equipment-role | No | string |  |  | Client should send valid enumerated value, e.g., VPLS-PE.
        ipv4-oam-address | No | string |  |  | Address tail-f uses to configure generic-vnf, also used for troubleshooting and is IP used for traps generated by GenericVnf (v4-loopback0-ip-address).
        prov-status | No | string |  |  | Trigger for operational monitoring of this VNF by BAU Service Assurance systems.
        vlan-id-outer | No | integer | int64 |  | Temporary location for stag to get to VCE

.. _d_171dd084ef9283154af97c0826cb77d8:

vpls-pes Model Structure
------------------------

Collection of VPLS Provider Edge routers


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vpls-pe | No | array of :ref:`vpls-pe <d_00ecef75a8d71ee9d4abc90feff3a5fb>` |  |  | 

.. _d_97f7255ccc461fc49496f9e8a1c0d079:

vpn-binding Model Structure
---------------------------

VPN binding
###### Related Nodes
- TO customer( vpn-binding Uses customer, MANY2MANY, will delete target node)
- FROM allotted-resource( allotted-resource BelongsTo vpn-binding, MANY2MANY, will delete target node)
- FROM l3-network( l3-network Uses vpn-binding, MANY2MANY, will delete target node)
- FROM logical-link( logical-link Uses vpn-binding, MANY2MANY, will delete target node)
- FROM route-target( route-target BelongsTo vpn-binding, MANY2ONE, will delete target node)(1)
- FROM configuration( configuration Uses vpn-binding, MANY2ONE, will delete target node)
- FROM service-instance( service-instance Uses vpn-binding, MANY2ONE, will delete target node)

-(1) IF this VPN-BINDING node is deleted, this FROM node is DELETED also
-VPN-BINDING cannot be deleted if related to ALLOTTED-RESOURCE,L3-NETWORK,LOGICAL-LINK


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        customer-vpn-id | No | string |  |  | id for this customer vpn
        route-distinguisher | No | string |  |  | Used to distinguish the distinct VPN routes of separate customers who connect to the provider in an MPLS network.
        vpn-id | Yes | string |  |  | VPN ID, globally unique within A&AI
        vpn-name | Yes | string |  |  | VPN Name
        vpn-platform | No | string |  |  | the platform associated with the VPN example AVPN, Mobility
        vpn-region | No | string |  |  | region of customer vpn
        vpn-type | No | string |  |  | Type of the vpn, should be taken from enumerated/valid values

.. _d_4fc47e404ca27cedb234a39d6e4ed046:

vpn-bindings Model Structure
----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vpn-binding | No | array of :ref:`vpn-binding <d_97f7255ccc461fc49496f9e8a1c0d079>` |  |  | 

.. _d_3105cb0de2c0b0960a36d71dc07146a2:

zone Model Structure
--------------------

A zone is a grouping of assets in a location homing to the same connections into the CBB
###### Related Nodes
- TO complex( zone LocatedIn complex, MANY2ONE, will delete target node)
- FROM cloud-region( cloud-region LocatedIn zone, MANY2ONE, will delete target node)
- FROM pnf( pnf LocatedIn zone, MANY2ONE, will delete target node)
- FROM pserver( pserver LocatedIn zone, MANY2ONE, will delete target node)
- FROM service-instance( service-instance LocatedIn zone, MANY2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        design-type | Yes | string |  |  | Design of zone [Medium/Large…]
        status | No | string |  |  | Status of a zone.
        zone-context | Yes | string |  |  | Context of zone [production/test]
        zone-id | Yes | string |  |  | Code assigned by AIC to the zone
        zone-name | Yes | string |  |  | English name associated with the zone

.. _d_f053176a1da6ff2188eb87cc8c8e2177:

zones Model Structure
---------------------

Collection of zones


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        zone | No | array of :ref:`zone <d_3105cb0de2c0b0960a36d71dc07146a2>` |  |  | 

