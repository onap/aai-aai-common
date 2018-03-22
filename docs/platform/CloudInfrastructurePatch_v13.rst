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

CLOUDINFRASTRUCTURE
~~~~~~~~~~~~~~~~~~~




PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}``
------------------------------------------------------------------------------------------


Summary
+++++++

update an existing cloud-region

Description
+++++++++++

.. raw:: html

    Update an existing cloud-region
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key


Request
+++++++



.. _d_e2ebe53839e84504a61c3afbb9204b31:

Body
^^^^

cloud-region designates an installation of a cloud cluster or region or instantiation. In AT&Ts AIC cloud, this could be an LCP or DCP. Cloud regions are uniquely identified by a composite key, cloud-owner + cloud-region-id. The format of the cloud-owner is vendor-cloudname and we will use att-aic for AT&T's AIC.
###### Related Nodes
- TO complex( cloud-region LocatedIn complex, MANY2ONE, will delete target node)
- TO l3-network( cloud-region Uses l3-network, MANY2MANY, will delete target node)
- TO zone( cloud-region LocatedIn zone, MANY2ONE, will delete target node)
- FROM availability-zone( availability-zone BelongsTo cloud-region, MANY2ONE, will delete target node)(1)
- FROM dvs-switch( dvs-switch BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM flavor( flavor BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM group-assignment( group-assignment BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM image( image BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM oam-network( oam-network BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM snapshot( snapshot BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM tenant( tenant BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vip-ipv4-address-list( vip-ipv4-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM volume-group( volume-group BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM logical-link( logical-link LocatedIn cloud-region, MANY2MANY, will delete target node)
- FROM pserver( pserver LocatedIn cloud-region, MANY2ONE, will delete target node)

-(1) IF this CLOUD-REGION node is deleted, this FROM node is DELETED also
-CLOUD-REGION cannot be deleted if related to DVS-SWITCH,FLAVOR,GROUP-ASSIGNMENT,IMAGE,OAM-NETWORK,SNAPSHOT,TENANT,VIP-IPV4-ADDRESS-LIST,VIP-IPV6-ADDRESS-LIST,VOLUME-GROUP


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cloud-owner | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        cloud-region-version | No | string |  |  | Software version employed at the site.  NOTE - THIS FIELD IS NOT KEPT UP TO DATE.
        cloud-type | No | string |  |  | Type of the cloud (e.g., openstack)
        cloud-zone | No | string |  |  | Zone where the cloud is homed.  NOTE - THIS FIELD IS NOT CORRECTLY POPULATED.
        complex-name | No | string |  |  | complex name for cloud-region instance.  NOTE - THIS FIELD IS NOT CORRECTLY POPULATED.
        identity-url | No | string |  |  | URL of the keystone identity service
        owner-defined-type | No | string |  |  | Cloud-owner defined type indicator (e.g., dcp, lcp)
        sriov-automation | No | string |  |  | Whether the cloud region supports (true) or does not support (false) SR-IOV automation.

.. code-block:: javascript

    {
        "cloud-owner": "somestring",
        "cloud-region-id": "somestring",
        "cloud-region-version": "somestring",
        "cloud-type": "somestring",
        "cloud-zone": "somestring",
        "complex-name": "somestring",
        "identity-url": "somestring",
        "owner-defined-type": "somestring",
        "sriov-automation": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/availability-zones/availability-zone/{availability-zone-name}``
--------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing availability-zone

Description
+++++++++++

.. raw:: html

    Update an existing availability-zone
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        availability-zone-name | path | Yes | string |  |  | Name of the availability zone.  Unique across a cloud region


Request
+++++++



.. _d_58c3064a1cecac5d4fda67e953a06fc6:

Body
^^^^

Availability zone, a collection of compute hosts/pservers
###### Related Nodes
- TO complex( availability-zone LocatedIn complex, MANY2ONE, will delete target node)
- TO service-capability( availability-zone AppliesTo service-capability, MANY2MANY, will delete target node)
- TO cloud-region( availability-zone BelongsTo cloud-region, MANY2ONE, will delete target node)(4)
- FROM ctag-pool( ctag-pool AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM dvs-switch( dvs-switch AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses availability-zone, MANY2MANY, will delete target node)
- FROM pserver( pserver MemberOf availability-zone, MANY2ONE, will delete target node)
- FROM vce( vce Uses availability-zone, MANY2MANY, will delete target node)

-(4) IF this TO node is deleted, this AVAILABILITY-ZONE is DELETED also
-AVAILABILITY-ZONE cannot be deleted if related to CTAG-POOL,DVS-SWITCH,GENERIC-VNF,PSERVER,VCE


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        availability-zone-name | Yes | string |  |  | Name of the availability zone.  Unique across a cloud region
        hypervisor-type | Yes | string |  |  | Type of hypervisor.  Source of truth should define valid values.
        operational-status | No | string |  |  | State that indicates whether the availability zone should be used, etc.  Source of truth should define valid values.

.. code-block:: javascript

    {
        "availability-zone-name": "somestring",
        "hypervisor-type": "somestring",
        "operational-status": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/dvs-switches/dvs-switch/{switch-name}``
--------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing dvs-switch

Description
+++++++++++

.. raw:: html

    Update an existing dvs-switch
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        switch-name | path | Yes | string |  |  | DVS switch name


Request
+++++++



.. _d_67d86d7cef379f11a1535418401a2a87:

Body
^^^^

Digital virtual switch metadata, used by SDN-C to configure VCEs.  A&AI needs to receive this data from the PO deployment team and administer it using the provisioningTool.sh into A&AI. 
###### Related Nodes
- TO cloud-region( dvs-switch BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO availability-zone( dvs-switch AppliesTo availability-zone, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        switch-name | Yes | string |  |  | DVS switch name
        vcenter-url | Yes | string |  |  | URL used to reach the vcenter

.. code-block:: javascript

    {
        "switch-name": "somestring",
        "vcenter-url": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/flavors/flavor/{flavor-id}``
---------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing flavor

Description
+++++++++++

.. raw:: html

    Update an existing flavor
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        flavor-id | path | Yes | string |  |  | Flavor id, expected to be unique across cloud-region.


Request
+++++++



.. _d_8834036f4174d58539b63dbd7f1b4e20:

Body
^^^^

Openstack flavor.
###### Related Nodes
- TO cloud-region( flavor BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vserver( vserver Uses flavor, MANY2ONE, will delete target node)

-FLAVOR cannot be deleted if related to VSERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        flavor-disabled | No | boolean |  |  | Boolean as to whether this flavor is no longer enabled
        flavor-disk | No | integer | int32 |  | Disk space
        flavor-ephemeral | No | integer | int32 |  | Amount of ephemeral disk space
        flavor-id | Yes | string |  |  | Flavor id, expected to be unique across cloud-region.
        flavor-is-public | No | boolean |  |  | whether flavor is available to all users or private to the tenant it was created in.
        flavor-name | Yes | string |  |  | Flavor name
        flavor-ram | No | integer | int32 |  | Amount of memory
        flavor-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details
        flavor-swap | No | string |  |  | amount of swap space allocation
        flavor-vcpus | No | integer | int32 |  | Number of CPUs

.. code-block:: javascript

    {
        "flavor-disabled": true,
        "flavor-disk": 1,
        "flavor-ephemeral": 1,
        "flavor-id": "somestring",
        "flavor-is-public": true,
        "flavor-name": "somestring",
        "flavor-ram": 1,
        "flavor-selflink": "somestring",
        "flavor-swap": "somestring",
        "flavor-vcpus": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/group-assignments/group-assignment/{group-id}``
----------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing group-assignment

Description
+++++++++++

.. raw:: html

    Update an existing group-assignment
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        group-id | path | Yes | string |  |  | Group id, expected to be unique across cloud-region.


Request
+++++++



.. _d_59bfd229d49ef61ec70c75e66b3d39f6:

Body
^^^^

Openstack group-assignment used to store exclusivity groups (EG).
###### Related Nodes
- TO cloud-region( group-assignment BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM pserver( pserver MemberOf group-assignment, MANY2ONE, will delete target node)
- FROM tenant( tenant MemberOf group-assignment, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-description | No | string |  |  | Group description - description of the group
        group-id | Yes | string |  |  | Group id, expected to be unique across cloud-region.
        group-name | Yes | string |  |  | Group name - name assigned to the group
        group-type | Yes | string |  |  | Group type - the type of group this instance refers to

.. code-block:: javascript

    {
        "group-description": "somestring",
        "group-id": "somestring",
        "group-name": "somestring",
        "group-type": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/images/image/{image-id}``
------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing image

Description
+++++++++++

.. raw:: html

    Update an existing image
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        image-id | path | Yes | string |  |  | Image id, expected to be unique across cloud region


Request
+++++++



.. _d_165a519a94d7796347b48e402f71559b:

Body
^^^^

Openstack image.
###### Related Nodes
- TO cloud-region( image BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM metadatum( metadatum BelongsTo image, MANY2ONE, will delete target node)(1)
- FROM vserver( vserver Uses image, MANY2ONE, will delete target node)

-(1) IF this IMAGE node is deleted, this FROM node is DELETED also
-IMAGE cannot be deleted if related to VSERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | No | string |  |  | The application that the image instantiates.
        application-vendor | No | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        image-architecture | No | string |  |  | Operating system architecture.
        image-id | Yes | string |  |  | Image id, expected to be unique across cloud region
        image-name | Yes | string |  |  | Image name
        image-os-distro | Yes | string |  |  | The common name of the operating system distribution in lowercase
        image-os-version | Yes | string |  |  | The operating system version as specified by the distributor.
        image-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. code-block:: javascript

    {
        "application": "somestring",
        "application-vendor": "somestring",
        "application-version": "somestring",
        "image-architecture": "somestring",
        "image-id": "somestring",
        "image-name": "somestring",
        "image-os-distro": "somestring",
        "image-os-version": "somestring",
        "image-selflink": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/images/image/{image-id}/metadata/metadatum/{metaname}``
------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        image-id | path | Yes | string |  |  | Image id, expected to be unique across cloud region
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/oam-networks/oam-network/{network-uuid}``
----------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing oam-network

Description
+++++++++++

.. raw:: html

    Update an existing oam-network
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        network-uuid | path | Yes | string |  |  | UUID of the network. Unique across a cloud-region


Request
+++++++



.. _d_c56f1e34981a49f16c93e66fd0a7ab72:

Body
^^^^

OAM network, to be deprecated shortly.  Do not use for new purposes. 
###### Related Nodes
- TO cloud-region( oam-network BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO complex( oam-network AppliesTo complex, MANY2MANY, will delete target node)
- TO service-capability( oam-network AppliesTo service-capability, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cvlan-tag | Yes | integer | int64 |  | cvlan-id
        ipv4-oam-gateway-address | No | string |  |  | Used for VNF firewall rule so customer cannot send customer traffic over this oam network
        ipv4-oam-gateway-address-prefix-length | No | integer | int32 |  | Used for VNF firewall rule so customer cannot send customer traffic over this oam network
        network-name | Yes | string |  |  | Name of the network.
        network-uuid | Yes | string |  |  | UUID of the network. Unique across a cloud-region

.. code-block:: javascript

    {
        "cvlan-tag": 1,
        "ipv4-oam-gateway-address": "somestring",
        "ipv4-oam-gateway-address-prefix-length": 1,
        "network-name": "somestring",
        "network-uuid": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/snapshots/snapshot/{snapshot-id}``
---------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing snapshot

Description
+++++++++++

.. raw:: html

    Update an existing snapshot
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        snapshot-id | path | Yes | string |  |  | Snapshot id, this is the key UUID assoc associated in glance with the snapshots.


Request
+++++++



.. _d_9f103a0d7b838c7b1282367c9c288469:

Body
^^^^

Openstack snapshot
###### Related Nodes
- TO cloud-region( snapshot BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vserver( vserver Uses snapshot, ONE2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | No | string |  |  | The application that the image instantiates.
        application-vendor | No | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        prev-snapshot-id | No | string |  |  | This field contains the UUID of the previous snapshot (if any).
        snapshot-architecture | No | string |  |  | Operating system architecture
        snapshot-id | Yes | string |  |  | Snapshot id, this is the key UUID assoc associated in glance with the snapshots.
        snapshot-name | No | string |  |  | Snapshot name
        snapshot-os-distro | No | string |  |  | The common name of the operating system distribution in lowercase
        snapshot-os-version | No | string |  |  | The operating system version as specified by the distributor.
        snapshot-selflink | No | string |  |  | URL to endpoint where AAI can get more details

.. code-block:: javascript

    {
        "application": "somestring",
        "application-vendor": "somestring",
        "application-version": "somestring",
        "prev-snapshot-id": "somestring",
        "snapshot-architecture": "somestring",
        "snapshot-id": "somestring",
        "snapshot-name": "somestring",
        "snapshot-os-distro": "somestring",
        "snapshot-os-version": "somestring",
        "snapshot-selflink": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}``
---------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing tenant

Description
+++++++++++

.. raw:: html

    Update an existing tenant
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.


Request
+++++++



.. _d_bebffb13dacd8221b192ee923e7f6d55:

Body
^^^^

Openstack tenant
###### Related Nodes
- TO cloud-region( tenant BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO group-assignment( tenant MemberOf group-assignment, MANY2MANY, will delete target node)
- TO l3-network( tenant Uses l3-network, MANY2MANY, will delete target node)
- TO volume-group( tenant DependsOn volume-group, ONE2MANY, will delete target node)
- TO instance-group( tenant MemberOf instance-group, ONE2MANY, will delete target node)
- FROM service-subscription( service-subscription Uses tenant, MANY2MANY, will delete target node)
- FROM vserver( vserver BelongsTo tenant, MANY2ONE, will delete target node)
- FROM nos-server( nos-server BelongsTo tenant, MANY2ONE, will delete target node)

-TENANT cannot be deleted if related to VSERVER,NOS-SERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        tenant-context | No | string |  |  | This field will store the tenant context.
        tenant-id | Yes | string |  |  | Unique id relative to the cloud-region.
        tenant-name | Yes | string |  |  | Readable name of tenant

.. code-block:: javascript

    {
        "tenant-context": "somestring",
        "tenant-id": "somestring",
        "tenant-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/nos-servers/nos-server/{nos-server-id}``
------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing nos-server

Description
+++++++++++

.. raw:: html

    Update an existing nos-server
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        nos-server-id | path | Yes | string |  |  | Unique identifier for this nos relative to its tenant


Request
+++++++



.. _d_5aedd73948e3a9d09bf6392a711abfba:

Body
^^^^

nos-server is the execution environment that will have images, certain versions of VNOS, running on it.
###### Related Nodes
- TO pserver( nos-server HostedOn pserver, ONE2ONE, will delete target node)
- TO tenant( nos-server BelongsTo tenant, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn nos-server, MANY2ONE, will delete target node)(1)

-(1) IF this NOS-SERVER node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        nos-server-id | Yes | string |  |  | Unique identifier for this nos relative to its tenant
        nos-server-name | Yes | string |  |  | Name of nos
        nos-server-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        vendor | Yes | string |  |  | uCPE vendor

.. code-block:: javascript

    {
        "in-maint": true,
        "nos-server-id": "somestring",
        "nos-server-name": "somestring",
        "nos-server-selflink": "somestring",
        "prov-status": "somestring",
        "vendor": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}``
---------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vserver

Description
+++++++++++

.. raw:: html

    Update an existing vserver
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant


Request
+++++++



.. _d_39120694757a865f40339bfac43ea83b:

Body
^^^^

Virtual Servers, aka virtual machine or VM.
###### Related Nodes
- TO tenant( vserver BelongsTo tenant, MANY2ONE, will delete target node)
- TO flavor( vserver Uses flavor, MANY2ONE, will delete target node)
- TO image( vserver Uses image, MANY2ONE, will delete target node)
- TO pserver( vserver HostedOn pserver, MANY2ONE, will delete target node)
- TO snapshot( vserver Uses snapshot, ONE2ONE, will delete target node)
- TO volume (CHILD of vserver, vserver AttachesTo volume, ONE2MANY, will delete target node)(2)
- FROM generic-vnf( generic-vnf HostedOn vserver, ONE2MANY, will delete target node)
- FROM vce( vce HostedOn vserver, ONE2MANY, will delete target node)
- FROM l-interface( l-interface BindsTo vserver, MANY2ONE, will delete target node)(1)
- FROM vf-module( vf-module Uses vserver, ONE2MANY, will delete target node)
- FROM vnfc( vnfc HostedOn vserver, ONE2MANY, will delete target node)

-(1) IF this VSERVER node is deleted, this FROM node is DELETED also
-(2) IF this VSERVER node is deleted, this TO node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        is-closed-loop-disabled | Yes | boolean |  |  | Used to indicate whether closed loop function is enabled on this node
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        vserver-id | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
        vserver-name | Yes | string |  |  | Name of vserver
        vserver-name2 | No | string |  |  | Alternative name of vserver
        vserver-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. code-block:: javascript

    {
        "in-maint": true,
        "is-closed-loop-disabled": true,
        "prov-status": "somestring",
        "vserver-id": "somestring",
        "vserver-name": "somestring",
        "vserver-name2": "somestring",
        "vserver-selflink": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
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






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers/vserver/{vserver-id}/volumes/volume/{volume-id}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing volume

Description
+++++++++++

.. raw:: html

    Update an existing volume
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        tenant-id | path | Yes | string |  |  | Unique id relative to the cloud-region.
        vserver-id | path | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
        volume-id | path | Yes | string |  |  | Unique ID of block storage volume relative to the vserver.


Request
+++++++



.. _d_ad81b54f04738c58764245c7f19c8fb9:

Body
^^^^

Ephemeral Block storage volume.
###### Related Nodes
- FROM vserver (PARENT of volume, vserver AttachesTo volume, will delete target node)(3)

-(3) IF this FROM node is deleted, this VOLUME is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        volume-id | Yes | string |  |  | Unique ID of block storage volume relative to the vserver.
        volume-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. code-block:: javascript

    {
        "volume-id": "somestring",
        "volume-selflink": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/vip-ipv4-address-list/{vip-ipv4-address}``
-----------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vip-ipv4-address-list

Description
+++++++++++

.. raw:: html

    Update an existing vip-ipv4-address-list
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        vip-ipv4-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_406bbae3b294e03a31d5ac0f82f60e71:

Body
^^^^

IPv4 Address Range
###### Related Nodes
- TO cloud-region( vip-ipv4-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO instance-group( vip-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO subnet( vip-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vnfc( vnfc Uses vip-ipv4-address-list, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        vip-ipv4-address | Yes | string |  |  | IP address
        vip-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "vip-ipv4-address": "somestring",
        "vip-ipv4-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/vip-ipv6-address-list/{vip-ipv6-address}``
-----------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing vip-ipv6-address-list

Description
+++++++++++

.. raw:: html

    Update an existing vip-ipv6-address-list
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        vip-ipv6-address | path | Yes | string |  |  | IP address


Request
+++++++



.. _d_62c16656e50309371f72cbfd41409665:

Body
^^^^

IPv6 Address Range
###### Related Nodes
- TO cloud-region( vip-ipv6-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO instance-group( vip-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO subnet( vip-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vnfc( vnfc Uses vip-ipv6-address-list, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        vip-ipv6-address | Yes | string |  |  | IP address
        vip-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. code-block:: javascript

    {
        "is-floating": true,
        "vip-ipv6-address": "somestring",
        "vip-ipv6-prefix-length": 1,
        "vlan-id-inner": 1,
        "vlan-id-outer": 1
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/volume-groups/volume-group/{volume-group-id}``
---------------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing volume-group

Description
+++++++++++

.. raw:: html

    Update an existing volume-group
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

        cloud-owner | path | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | path | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        volume-group-id | path | Yes | string |  |  | Unique ID of volume-group.


Request
+++++++



.. _d_cdbfc6d4ba80ed973d1db6e8f35d8d0f:

Body
^^^^

Persistent block-level storage.
###### Related Nodes
- TO cloud-region( volume-group BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO complex( volume-group LocatedIn complex, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf DependsOn volume-group, ONE2MANY, will delete target node)
- FROM vf-module( vf-module Uses volume-group, ONE2ONE, will delete target node)
- FROM tenant( tenant DependsOn volume-group, ONE2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | Heat stack id corresponding to this volume-group
        model-customization-id | No | string |  |  | captures the id of all the configuration used to customize the resource for the service.
        orchestration-status | No | string |  |  | Orchestration status of this volume-group
        vf-module-model-customization-id | No | string |  |  | helps relate the volume group to the vf-module whose components will require the volume group
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.
        volume-group-id | Yes | string |  |  | Unique ID of volume-group.
        volume-group-name | Yes | string |  |  | Name of the volume group.

.. code-block:: javascript

    {
        "heat-stack-id": "somestring",
        "model-customization-id": "somestring",
        "orchestration-status": "somestring",
        "vf-module-model-customization-id": "somestring",
        "vnf-type": "somestring",
        "volume-group-id": "somestring",
        "volume-group-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/complexes/complex/{physical-location-id}``
------------------------------------------------------------------------


Summary
+++++++

update an existing complex

Description
+++++++++++

.. raw:: html

    Update an existing complex
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

        physical-location-id | path | Yes | string |  |  | Unique identifier for physical location, e.g., CLLI


Request
+++++++



.. _d_64057b030e6d579ee56e7170f34d8a2b:

Body
^^^^

Collection of physical locations that can house cloud-regions.
###### Related Nodes
- TO l3-network( complex Uses l3-network, MANY2MANY, will delete target node)
- FROM availability-zone( availability-zone LocatedIn complex, MANY2ONE, will delete target node)
- FROM cloud-region( cloud-region LocatedIn complex, MANY2ONE, will delete target node)
- FROM ctag-pool( ctag-pool BelongsTo complex, MANY2ONE, will delete target node)(1)
- FROM generic-vnf( generic-vnf LocatedIn complex, MANY2MANY, will delete target node)
- FROM oam-network( oam-network AppliesTo complex, MANY2MANY, will delete target node)
- FROM pnf( pnf LocatedIn complex, MANY2ONE, will delete target node)
- FROM pserver( pserver LocatedIn complex, MANY2ONE, will delete target node)
- FROM vce( vce LocatedIn complex, MANY2MANY, will delete target node)
- FROM volume-group( volume-group LocatedIn complex, MANY2ONE, will delete target node)
- FROM vpls-pe( vpls-pe LocatedIn complex, MANY2ONE, will delete target node)
- FROM zone( zone LocatedIn complex, MANY2ONE, will delete target node)

-(1) IF this COMPLEX node is deleted, this FROM node is DELETED also
-COMPLEX cannot be deleted if related to AVAILABILITY-ZONE,CLOUD-REGION,GENERIC-VNF,OAM-NETWORK,PNF,PSERVER,VCE,VOLUME-GROUP,VPLS-PE,ZONE


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        complex-name | No | string |  |  | Gamma complex name for LCP instance.
        data-center-code | No | string |  |  | Data center code which can be an alternate way to identify a complex
        identity-url | No | string |  |  | URL of the keystone identity service
        physical-location-id | Yes | string |  |  | Unique identifier for physical location, e.g., CLLI

.. code-block:: javascript

    {
        "complex-name": "somestring",
        "data-center-code": "somestring",
        "identity-url": "somestring",
        "physical-location-id": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/complexes/complex/{physical-location-id}/ctag-pools/ctag-pool/{target-pe}/{availability-zone-name}``
----------------------------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing ctag-pool

Description
+++++++++++

.. raw:: html

    Update an existing ctag-pool
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

        physical-location-id | path | Yes | string |  |  | Unique identifier for physical location, e.g., CLLI
        target-pe | path | Yes | string |  |  | The Target provider edge router
        availability-zone-name | path | Yes | string |  |  | Name of the availability zone


Request
+++++++



.. _d_e31ea01485d5c9544aaf8c11735994fc:

Body
^^^^

A collection of C tags (vlan tags) grouped for a specific purpose.
###### Related Nodes
- TO complex( ctag-pool BelongsTo complex, MANY2ONE, will delete target node)(4)
- TO availability-zone( ctag-pool AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses ctag-pool, MANY2MANY, will delete target node)
- FROM vpls-pe( vpls-pe Uses ctag-pool, MANY2MANY, will delete target node)

-(4) IF this TO node is deleted, this CTAG-POOL is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        availability-zone-name | Yes | string |  |  | Name of the availability zone
        ctag-pool-purpose | Yes | string |  |  | Describes what the intended purpose of this pool is.
        ctag-values | No | string |  |  | Comma separated list of ctags
        target-pe | Yes | string |  |  | The Target provider edge router

.. code-block:: javascript

    {
        "availability-zone-name": "somestring",
        "ctag-pool-purpose": "somestring",
        "ctag-values": "somestring",
        "target-pe": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/network-profiles/network-profile/{nm-profile-name}``
----------------------------------------------------------------------------------


Summary
+++++++

update an existing network-profile

Description
+++++++++++

.. raw:: html

    Update an existing network-profile
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

        nm-profile-name | path | Yes | string |  |  | Unique name of network profile.


Request
+++++++



.. _d_1476c2c383a619ffc8c0bc76c5b244c1:

Body
^^^^

Network profile populated by SDN-GP for SNMP
###### Related Nodes
- TO generic-vnf( network-profile AppliesTo generic-vnf, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        community-string | No | string |  |  | Encrypted SNMP community string
        nm-profile-name | Yes | string |  |  | Unique name of network profile.

.. code-block:: javascript

    {
        "community-string": "somestring",
        "nm-profile-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/operational-environments/operational-environment/{operational-environment-id}``
-------------------------------------------------------------------------------------------------------------


Summary
+++++++

update an existing operational-environment

Description
+++++++++++

.. raw:: html

    Update an existing operational-environment
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

        operational-environment-id | path | Yes | string |  |  | UUID of an operational environment


Request
+++++++



.. _d_3256ef82888286c05345a570f5382946:

Body
^^^^

It is a logical partition of the cloud which allows to have multiple environments in the production AIC.
###### Related Nodes
- TO operational-environment( operational-environment Uses operational-environment, ONE2ONE, will delete target node)
- FROM operational-environment( operational-environment Uses operational-environment, ONE2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        operational-environment-id | Yes | string |  |  | UUID of an operational environment
        operational-environment-name | Yes | string |  |  | Operational Environment name
        operational-environment-status | Yes | string |  |  | Status
        operational-environment-type | Yes | string |  |  | Operational Environment Type.
        tenant-context | Yes | string |  |  | Tenant Context.
        workload-context | Yes | string |  |  | Workload Context.

.. code-block:: javascript

    {
        "operational-environment-id": "somestring",
        "operational-environment-name": "somestring",
        "operational-environment-status": "somestring",
        "operational-environment-type": "somestring",
        "tenant-context": "somestring",
        "workload-context": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}``
-----------------------------------------------------------


Summary
+++++++

update an existing pserver

Description
+++++++++++

.. raw:: html

    Update an existing pserver
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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.


Request
+++++++



.. _d_7ce71d22ffe43209587502bc2ddf388d:

Body
^^^^

Compute host whose hostname must be unique and must exactly match what is sent as a relationship to a vserver.
###### Related Nodes
- TO group-assignment( pserver MemberOf group-assignment, MANY2ONE, will delete target node)
- TO availability-zone( pserver MemberOf availability-zone, MANY2ONE, will delete target node)
- TO cloud-region( pserver LocatedIn cloud-region, MANY2ONE, will delete target node)
- TO complex( pserver LocatedIn complex, MANY2ONE, will delete target node)
- TO zone( pserver LocatedIn zone, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn pserver, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo pserver, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo pserver, MANY2ONE, will delete target node)(1)
- FROM vserver( vserver HostedOn pserver, MANY2ONE, will delete target node)
- FROM nos-server( nos-server HostedOn pserver, ONE2ONE, will delete target node)

-(1) IF this PSERVER node is deleted, this FROM node is DELETED also
-PSERVER cannot be deleted if related to GENERIC-VNF,LOGICAL-LINK,VSERVER,NOS-SERVER,GROUP-ASSIGNMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        disk-in-gigabytes | No | integer | int32 |  | Disk size, in GBs
        equip-model | No | string |  |  | Equipment model.  Source of truth should define valid values.
        equip-type | No | string |  |  | Equipment type.  Source of truth should define valid values.
        equip-vendor | No | string |  |  | Equipment vendor.  Source of truth should define valid values.
        fqdn | No | string |  |  | Fully-qualified domain name
        hostname | Yes | string |  |  | Value from executing hostname on the compute node.
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        internet-topology | No | string |  |  | internet topology of Pserver
        inv-status | No | string |  |  | CANOPI's inventory status.  Only set with values exactly as defined by CANOPI.
        ipaddress-v4-aim | No | string |  |  | IPV4 AIM address
        ipaddress-v4-loopback-0 | No | string |  |  | IPV4 Loopback 0 address
        ipaddress-v6-aim | No | string |  |  | IPV6 AIM address
        ipaddress-v6-loopback-0 | No | string |  |  | IPV6 Loopback 0 address
        ipaddress-v6-oam | No | string |  |  | IPV6 OAM address
        ipv4-oam-address | No | string |  |  | Used to configure device, also used for troubleshooting and is IP used for traps generated by device.
        number-of-cpus | No | integer | int32 |  | Number of cpus
        pserver-id | No | string |  |  | ID of Pserver
        pserver-selflink | No | string |  |  | URL to endpoint where AAI can get more details
        ptnii-equip-name | No | string |  |  | PTNII name
        ram-in-megabytes | No | integer | int32 |  | RAM size, in MBs
        serial-number | No | string |  |  | Serial number, may be queried

.. code-block:: javascript

    {
        "disk-in-gigabytes": 1,
        "equip-model": "somestring",
        "equip-type": "somestring",
        "equip-vendor": "somestring",
        "fqdn": "somestring",
        "hostname": "somestring",
        "in-maint": true,
        "internet-topology": "somestring",
        "inv-status": "somestring",
        "ipaddress-v4-aim": "somestring",
        "ipaddress-v4-loopback-0": "somestring",
        "ipaddress-v6-aim": "somestring",
        "ipaddress-v6-loopback-0": "somestring",
        "ipaddress-v6-oam": "somestring",
        "ipv4-oam-address": "somestring",
        "number-of-cpus": 1,
        "pserver-id": "somestring",
        "pserver-selflink": "somestring",
        "ptnii-equip-name": "somestring",
        "ram-in-megabytes": 1,
        "serial-number": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}``
---------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
---------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/lag-interfaces/lag-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}``
-----------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}``
-----------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/sriov-vfs/sriov-vf/{pci-id}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}``
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv4-address-list/{l3-interface-ipv4-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/l-interfaces/l-interface/{interface-name}/vlans/vlan/{vlan-interface}/l3-interface-ipv6-address-list/{l3-interface-ipv6-address}``
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/pservers/pserver/{hostname}/p-interfaces/p-interface/{interface-name}/sriov-pfs/sriov-pf/{pf-pci-id}``
------------------------------------------------------------------------------------------------------------------------------------


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

        hostname | path | Yes | string |  |  | Value from executing hostname on the compute node.
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






PATCH ``/cloud-infrastructure/virtual-data-centers/virtual-data-center/{vdc-id}``
---------------------------------------------------------------------------------


Summary
+++++++

update an existing virtual-data-center

Description
+++++++++++

.. raw:: html

    Update an existing virtual-data-center
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

        vdc-id | path | Yes | string |  |  | Unique ID of the vdc


Request
+++++++



.. _d_900e867e1614a172a0ad5db22d72d666:

Body
^^^^

Virtual organization of cloud infrastructure elements in a data center context
###### Related Nodes
- FROM connector( connector LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- FROM logical-link( logical-link LocatedIn virtual-data-center, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vdc-id | Yes | string |  |  | Unique ID of the vdc
        vdc-name | Yes | string |  |  | Name of the virtual data center

.. code-block:: javascript

    {
        "vdc-id": "somestring",
        "vdc-name": "somestring"
    }

Responses
+++++++++

**default**
^^^^^^^^^^^

Response codes found in [response codes](https://wiki.web.att.com/pages/viewpage.action?pageId=607391054).




  
Data Structures
~~~~~~~~~~~~~~~

.. _d_58c3064a1cecac5d4fda67e953a06fc6:

availability-zone Model Structure
---------------------------------

Availability zone, a collection of compute hosts/pservers
###### Related Nodes
- TO complex( availability-zone LocatedIn complex, MANY2ONE, will delete target node)
- TO service-capability( availability-zone AppliesTo service-capability, MANY2MANY, will delete target node)
- TO cloud-region( availability-zone BelongsTo cloud-region, MANY2ONE, will delete target node)(4)
- FROM ctag-pool( ctag-pool AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM dvs-switch( dvs-switch AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses availability-zone, MANY2MANY, will delete target node)
- FROM pserver( pserver MemberOf availability-zone, MANY2ONE, will delete target node)
- FROM vce( vce Uses availability-zone, MANY2MANY, will delete target node)

-(4) IF this TO node is deleted, this AVAILABILITY-ZONE is DELETED also
-AVAILABILITY-ZONE cannot be deleted if related to CTAG-POOL,DVS-SWITCH,GENERIC-VNF,PSERVER,VCE


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        availability-zone-name | Yes | string |  |  | Name of the availability zone.  Unique across a cloud region
        hypervisor-type | Yes | string |  |  | Type of hypervisor.  Source of truth should define valid values.
        operational-status | No | string |  |  | State that indicates whether the availability zone should be used, etc.  Source of truth should define valid values.

.. _d_5e1e0a864e5ad85e1030c7ecca537789:

availability-zones Model Structure
----------------------------------

Collection of availability zones


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        availability-zone | No | array of :ref:`availability-zone <d_58c3064a1cecac5d4fda67e953a06fc6>` |  |  | 

.. _d_f720fa2e5d0ad18f3b23972968954d2e:

cloud-infrastructure Model Structure
------------------------------------

Namespace for cloud infrastructure.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cloud-regions | No | array of :ref:`cloud-region <d_e2ebe53839e84504a61c3afbb9204b31>` |  |  | 
        complexes | No | array of :ref:`complex <d_64057b030e6d579ee56e7170f34d8a2b>` |  |  | 
        network-profiles | No | array of :ref:`network-profile <d_1476c2c383a619ffc8c0bc76c5b244c1>` |  |  | 
        operational-environments | No | array of :ref:`operational-environment <d_3256ef82888286c05345a570f5382946>` |  |  | 
        pservers | No | array of :ref:`pserver <d_7ce71d22ffe43209587502bc2ddf388d>` |  |  | 
        virtual-data-centers | No | array of :ref:`virtual-data-center <d_900e867e1614a172a0ad5db22d72d666>` |  |  | 

.. _d_e2ebe53839e84504a61c3afbb9204b31:

cloud-region Model Structure
----------------------------

cloud-region designates an installation of a cloud cluster or region or instantiation. In AT&Ts AIC cloud, this could be an LCP or DCP. Cloud regions are uniquely identified by a composite key, cloud-owner + cloud-region-id. The format of the cloud-owner is vendor-cloudname and we will use att-aic for AT&T's AIC.
###### Related Nodes
- TO complex( cloud-region LocatedIn complex, MANY2ONE, will delete target node)
- TO l3-network( cloud-region Uses l3-network, MANY2MANY, will delete target node)
- TO zone( cloud-region LocatedIn zone, MANY2ONE, will delete target node)
- FROM availability-zone( availability-zone BelongsTo cloud-region, MANY2ONE, will delete target node)(1)
- FROM dvs-switch( dvs-switch BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM flavor( flavor BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM group-assignment( group-assignment BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM image( image BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM oam-network( oam-network BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM snapshot( snapshot BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM tenant( tenant BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vip-ipv4-address-list( vip-ipv4-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vip-ipv6-address-list( vip-ipv6-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM volume-group( volume-group BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM logical-link( logical-link LocatedIn cloud-region, MANY2MANY, will delete target node)
- FROM pserver( pserver LocatedIn cloud-region, MANY2ONE, will delete target node)

-(1) IF this CLOUD-REGION node is deleted, this FROM node is DELETED also
-CLOUD-REGION cannot be deleted if related to DVS-SWITCH,FLAVOR,GROUP-ASSIGNMENT,IMAGE,OAM-NETWORK,SNAPSHOT,TENANT,VIP-IPV4-ADDRESS-LIST,VIP-IPV6-ADDRESS-LIST,VOLUME-GROUP


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cloud-owner | Yes | string |  |  | Identifies the vendor and cloud name, e.g., att-aic. First part of composite key should be formatted as vendor-cloudname
        cloud-region-id | Yes | string |  |  | Identifier used by the vendor for the region. Second part of composite key
        cloud-region-version | No | string |  |  | Software version employed at the site.  NOTE - THIS FIELD IS NOT KEPT UP TO DATE.
        cloud-type | No | string |  |  | Type of the cloud (e.g., openstack)
        cloud-zone | No | string |  |  | Zone where the cloud is homed.  NOTE - THIS FIELD IS NOT CORRECTLY POPULATED.
        complex-name | No | string |  |  | complex name for cloud-region instance.  NOTE - THIS FIELD IS NOT CORRECTLY POPULATED.
        identity-url | No | string |  |  | URL of the keystone identity service
        owner-defined-type | No | string |  |  | Cloud-owner defined type indicator (e.g., dcp, lcp)
        sriov-automation | No | string |  |  | Whether the cloud region supports (true) or does not support (false) SR-IOV automation.

.. _d_46eb1bfd4996d9bc955cb2151eea771f:

cloud-regions Model Structure
-----------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cloud-region | No | array of :ref:`cloud-region <d_e2ebe53839e84504a61c3afbb9204b31>` |  |  | 

.. _d_64057b030e6d579ee56e7170f34d8a2b:

complex Model Structure
-----------------------

Collection of physical locations that can house cloud-regions.
###### Related Nodes
- TO l3-network( complex Uses l3-network, MANY2MANY, will delete target node)
- FROM availability-zone( availability-zone LocatedIn complex, MANY2ONE, will delete target node)
- FROM cloud-region( cloud-region LocatedIn complex, MANY2ONE, will delete target node)
- FROM ctag-pool( ctag-pool BelongsTo complex, MANY2ONE, will delete target node)(1)
- FROM generic-vnf( generic-vnf LocatedIn complex, MANY2MANY, will delete target node)
- FROM oam-network( oam-network AppliesTo complex, MANY2MANY, will delete target node)
- FROM pnf( pnf LocatedIn complex, MANY2ONE, will delete target node)
- FROM pserver( pserver LocatedIn complex, MANY2ONE, will delete target node)
- FROM vce( vce LocatedIn complex, MANY2MANY, will delete target node)
- FROM volume-group( volume-group LocatedIn complex, MANY2ONE, will delete target node)
- FROM vpls-pe( vpls-pe LocatedIn complex, MANY2ONE, will delete target node)
- FROM zone( zone LocatedIn complex, MANY2ONE, will delete target node)

-(1) IF this COMPLEX node is deleted, this FROM node is DELETED also
-COMPLEX cannot be deleted if related to AVAILABILITY-ZONE,CLOUD-REGION,GENERIC-VNF,OAM-NETWORK,PNF,PSERVER,VCE,VOLUME-GROUP,VPLS-PE,ZONE


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        complex-name | No | string |  |  | Gamma complex name for LCP instance.
        data-center-code | No | string |  |  | Data center code which can be an alternate way to identify a complex
        identity-url | No | string |  |  | URL of the keystone identity service
        physical-location-id | Yes | string |  |  | Unique identifier for physical location, e.g., CLLI

.. _d_80ca4cda267d9e3107ad7f84ad9d0fa0:

complexes Model Structure
-------------------------

Collection of physical locations that can house cloud-regions.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        complex | No | array of :ref:`complex <d_64057b030e6d579ee56e7170f34d8a2b>` |  |  | 

.. _d_e31ea01485d5c9544aaf8c11735994fc:

ctag-pool Model Structure
-------------------------

A collection of C tags (vlan tags) grouped for a specific purpose.
###### Related Nodes
- TO complex( ctag-pool BelongsTo complex, MANY2ONE, will delete target node)(4)
- TO availability-zone( ctag-pool AppliesTo availability-zone, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf Uses ctag-pool, MANY2MANY, will delete target node)
- FROM vpls-pe( vpls-pe Uses ctag-pool, MANY2MANY, will delete target node)

-(4) IF this TO node is deleted, this CTAG-POOL is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        availability-zone-name | Yes | string |  |  | Name of the availability zone
        ctag-pool-purpose | Yes | string |  |  | Describes what the intended purpose of this pool is.
        ctag-values | No | string |  |  | Comma separated list of ctags
        target-pe | Yes | string |  |  | The Target provider edge router

.. _d_ac08246a9a7ef274f9429c1e51518d30:

ctag-pools Model Structure
--------------------------

.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        ctag-pool | No | array of :ref:`ctag-pool <d_e31ea01485d5c9544aaf8c11735994fc>` |  |  | 

.. _d_67d86d7cef379f11a1535418401a2a87:

dvs-switch Model Structure
--------------------------

Digital virtual switch metadata, used by SDN-C to configure VCEs.  A&AI needs to receive this data from the PO deployment team and administer it using the provisioningTool.sh into A&AI. 
###### Related Nodes
- TO cloud-region( dvs-switch BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO availability-zone( dvs-switch AppliesTo availability-zone, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        switch-name | Yes | string |  |  | DVS switch name
        vcenter-url | Yes | string |  |  | URL used to reach the vcenter

.. _d_62d1d3008d0b244edc7ea82beaf54cb5:

dvs-switches Model Structure
----------------------------

Collection of digital virtual switch metadata used for vmWare VCEs and GenericVnfs.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        dvs-switch | No | array of :ref:`dvs-switch <d_67d86d7cef379f11a1535418401a2a87>` |  |  | 

.. _d_8834036f4174d58539b63dbd7f1b4e20:

flavor Model Structure
----------------------

Openstack flavor.
###### Related Nodes
- TO cloud-region( flavor BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vserver( vserver Uses flavor, MANY2ONE, will delete target node)

-FLAVOR cannot be deleted if related to VSERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        flavor-disabled | No | boolean |  |  | Boolean as to whether this flavor is no longer enabled
        flavor-disk | No | integer | int32 |  | Disk space
        flavor-ephemeral | No | integer | int32 |  | Amount of ephemeral disk space
        flavor-id | Yes | string |  |  | Flavor id, expected to be unique across cloud-region.
        flavor-is-public | No | boolean |  |  | whether flavor is available to all users or private to the tenant it was created in.
        flavor-name | Yes | string |  |  | Flavor name
        flavor-ram | No | integer | int32 |  | Amount of memory
        flavor-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details
        flavor-swap | No | string |  |  | amount of swap space allocation
        flavor-vcpus | No | integer | int32 |  | Number of CPUs

.. _d_ea6c83f480c558a46000fe896e057915:

flavors Model Structure
-----------------------

Collection of openstack flavors.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        flavor | No | array of :ref:`flavor <d_8834036f4174d58539b63dbd7f1b4e20>` |  |  | 

.. _d_59bfd229d49ef61ec70c75e66b3d39f6:

group-assignment Model Structure
--------------------------------

Openstack group-assignment used to store exclusivity groups (EG).
###### Related Nodes
- TO cloud-region( group-assignment BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM pserver( pserver MemberOf group-assignment, MANY2ONE, will delete target node)
- FROM tenant( tenant MemberOf group-assignment, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-description | No | string |  |  | Group description - description of the group
        group-id | Yes | string |  |  | Group id, expected to be unique across cloud-region.
        group-name | Yes | string |  |  | Group name - name assigned to the group
        group-type | Yes | string |  |  | Group type - the type of group this instance refers to

.. _d_3c65c768b1d2e99c734ea1c24d62354d:

group-assignments Model Structure
---------------------------------

Collection of openstack group assignments


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        group-assignment | No | array of :ref:`group-assignment <d_59bfd229d49ef61ec70c75e66b3d39f6>` |  |  | 

.. _d_165a519a94d7796347b48e402f71559b:

image Model Structure
---------------------

Openstack image.
###### Related Nodes
- TO cloud-region( image BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM metadatum( metadatum BelongsTo image, MANY2ONE, will delete target node)(1)
- FROM vserver( vserver Uses image, MANY2ONE, will delete target node)

-(1) IF this IMAGE node is deleted, this FROM node is DELETED also
-IMAGE cannot be deleted if related to VSERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | No | string |  |  | The application that the image instantiates.
        application-vendor | No | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        image-architecture | No | string |  |  | Operating system architecture.
        image-id | Yes | string |  |  | Image id, expected to be unique across cloud region
        image-name | Yes | string |  |  | Image name
        image-os-distro | Yes | string |  |  | The common name of the operating system distribution in lowercase
        image-os-version | Yes | string |  |  | The operating system version as specified by the distributor.
        image-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. _d_52741f9f0b0ae9e62928c38af9c0f6d7:

images Model Structure
----------------------

Collectio of Openstack images.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        image | No | array of :ref:`image <d_165a519a94d7796347b48e402f71559b>` |  |  | 

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

.. _d_1476c2c383a619ffc8c0bc76c5b244c1:

network-profile Model Structure
-------------------------------

Network profile populated by SDN-GP for SNMP
###### Related Nodes
- TO generic-vnf( network-profile AppliesTo generic-vnf, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        community-string | No | string |  |  | Encrypted SNMP community string
        nm-profile-name | Yes | string |  |  | Unique name of network profile.

.. _d_4efb96d871fe1cb70154301821f4c28b:

network-profiles Model Structure
--------------------------------

Collection of network profiles


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        network-profile | No | array of :ref:`network-profile <d_1476c2c383a619ffc8c0bc76c5b244c1>` |  |  | 

.. _d_5aedd73948e3a9d09bf6392a711abfba:

nos-server Model Structure
--------------------------

nos-server is the execution environment that will have images, certain versions of VNOS, running on it.
###### Related Nodes
- TO pserver( nos-server HostedOn pserver, ONE2ONE, will delete target node)
- TO tenant( nos-server BelongsTo tenant, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn nos-server, MANY2ONE, will delete target node)(1)

-(1) IF this NOS-SERVER node is deleted, this FROM node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        nos-server-id | Yes | string |  |  | Unique identifier for this nos relative to its tenant
        nos-server-name | Yes | string |  |  | Name of nos
        nos-server-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        vendor | Yes | string |  |  | uCPE vendor

.. _d_b9b2752c74c9c6c65daca9ed0eb19eab:

nos-servers Model Structure
---------------------------

nos-server is the execution environment that will have images, certain versions of VNOS, running on it.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        nos-server | No | array of :ref:`nos-server <d_5aedd73948e3a9d09bf6392a711abfba>` |  |  | 

.. _d_c56f1e34981a49f16c93e66fd0a7ab72:

oam-network Model Structure
---------------------------

OAM network, to be deprecated shortly.  Do not use for new purposes. 
###### Related Nodes
- TO cloud-region( oam-network BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO complex( oam-network AppliesTo complex, MANY2MANY, will delete target node)
- TO service-capability( oam-network AppliesTo service-capability, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        cvlan-tag | Yes | integer | int64 |  | cvlan-id
        ipv4-oam-gateway-address | No | string |  |  | Used for VNF firewall rule so customer cannot send customer traffic over this oam network
        ipv4-oam-gateway-address-prefix-length | No | integer | int32 |  | Used for VNF firewall rule so customer cannot send customer traffic over this oam network
        network-name | Yes | string |  |  | Name of the network.
        network-uuid | Yes | string |  |  | UUID of the network. Unique across a cloud-region

.. _d_d36818c0a8e9798b4cba53d52beb4397:

oam-networks Model Structure
----------------------------

Collection of OAM networks, to be deprecated shortly.  Do not use for new purposes. 


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        oam-network | No | array of :ref:`oam-network <d_c56f1e34981a49f16c93e66fd0a7ab72>` |  |  | 

.. _d_3256ef82888286c05345a570f5382946:

operational-environment Model Structure
---------------------------------------

It is a logical partition of the cloud which allows to have multiple environments in the production AIC.
###### Related Nodes
- TO operational-environment( operational-environment Uses operational-environment, ONE2ONE, will delete target node)
- FROM operational-environment( operational-environment Uses operational-environment, ONE2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        operational-environment-id | Yes | string |  |  | UUID of an operational environment
        operational-environment-name | Yes | string |  |  | Operational Environment name
        operational-environment-status | Yes | string |  |  | Status
        operational-environment-type | Yes | string |  |  | Operational Environment Type.
        tenant-context | Yes | string |  |  | Tenant Context.
        workload-context | Yes | string |  |  | Workload Context.

.. _d_a27e924c782c4c748b902086a64688df:

operational-environments Model Structure
----------------------------------------

a logical partition of the cloud which allows to have multiple environments in the production AIC.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        operational-environment | No | array of :ref:`operational-environment <d_3256ef82888286c05345a570f5382946>` |  |  | 

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

.. _d_7ce71d22ffe43209587502bc2ddf388d:

pserver Model Structure
-----------------------

Compute host whose hostname must be unique and must exactly match what is sent as a relationship to a vserver.
###### Related Nodes
- TO group-assignment( pserver MemberOf group-assignment, MANY2ONE, will delete target node)
- TO availability-zone( pserver MemberOf availability-zone, MANY2ONE, will delete target node)
- TO cloud-region( pserver LocatedIn cloud-region, MANY2ONE, will delete target node)
- TO complex( pserver LocatedIn complex, MANY2ONE, will delete target node)
- TO zone( pserver LocatedIn zone, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf HostedOn pserver, MANY2MANY, will delete target node)
- FROM logical-link( logical-link BridgedTo pserver, MANY2MANY, will delete target node)
- FROM lag-interface( lag-interface BindsTo pserver, MANY2ONE, will delete target node)(1)
- FROM p-interface( p-interface BindsTo pserver, MANY2ONE, will delete target node)(1)
- FROM vserver( vserver HostedOn pserver, MANY2ONE, will delete target node)
- FROM nos-server( nos-server HostedOn pserver, ONE2ONE, will delete target node)

-(1) IF this PSERVER node is deleted, this FROM node is DELETED also
-PSERVER cannot be deleted if related to GENERIC-VNF,LOGICAL-LINK,VSERVER,NOS-SERVER,GROUP-ASSIGNMENT


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        disk-in-gigabytes | No | integer | int32 |  | Disk size, in GBs
        equip-model | No | string |  |  | Equipment model.  Source of truth should define valid values.
        equip-type | No | string |  |  | Equipment type.  Source of truth should define valid values.
        equip-vendor | No | string |  |  | Equipment vendor.  Source of truth should define valid values.
        fqdn | No | string |  |  | Fully-qualified domain name
        hostname | Yes | string |  |  | Value from executing hostname on the compute node.
        in-maint | Yes | boolean |  |  | used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        internet-topology | No | string |  |  | internet topology of Pserver
        inv-status | No | string |  |  | CANOPI's inventory status.  Only set with values exactly as defined by CANOPI.
        ipaddress-v4-aim | No | string |  |  | IPV4 AIM address
        ipaddress-v4-loopback-0 | No | string |  |  | IPV4 Loopback 0 address
        ipaddress-v6-aim | No | string |  |  | IPV6 AIM address
        ipaddress-v6-loopback-0 | No | string |  |  | IPV6 Loopback 0 address
        ipaddress-v6-oam | No | string |  |  | IPV6 OAM address
        ipv4-oam-address | No | string |  |  | Used to configure device, also used for troubleshooting and is IP used for traps generated by device.
        number-of-cpus | No | integer | int32 |  | Number of cpus
        pserver-id | No | string |  |  | ID of Pserver
        pserver-selflink | No | string |  |  | URL to endpoint where AAI can get more details
        ptnii-equip-name | No | string |  |  | PTNII name
        ram-in-megabytes | No | integer | int32 |  | RAM size, in MBs
        serial-number | No | string |  |  | Serial number, may be queried

.. _d_7299e46906b3ed272bd57bdea6870c97:

pservers Model Structure
------------------------

Collection of compute hosts.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        pserver | No | array of :ref:`pserver <d_7ce71d22ffe43209587502bc2ddf388d>` |  |  | 

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

.. _d_9f103a0d7b838c7b1282367c9c288469:

snapshot Model Structure
------------------------

Openstack snapshot
###### Related Nodes
- TO cloud-region( snapshot BelongsTo cloud-region, MANY2ONE, will delete target node)
- FROM vserver( vserver Uses snapshot, ONE2ONE, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        application | No | string |  |  | The application that the image instantiates.
        application-vendor | No | string |  |  | The vendor of the application.
        application-version | No | string |  |  | The version of the application.
        prev-snapshot-id | No | string |  |  | This field contains the UUID of the previous snapshot (if any).
        snapshot-architecture | No | string |  |  | Operating system architecture
        snapshot-id | Yes | string |  |  | Snapshot id, this is the key UUID assoc associated in glance with the snapshots.
        snapshot-name | No | string |  |  | Snapshot name
        snapshot-os-distro | No | string |  |  | The common name of the operating system distribution in lowercase
        snapshot-os-version | No | string |  |  | The operating system version as specified by the distributor.
        snapshot-selflink | No | string |  |  | URL to endpoint where AAI can get more details

.. _d_21ab132dfae1bca0e94ba3d91d518497:

snapshots Model Structure
-------------------------

Collection of openstack snapshots


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        snapshot | No | array of :ref:`snapshot <d_9f103a0d7b838c7b1282367c9c288469>` |  |  | 

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

.. _d_bebffb13dacd8221b192ee923e7f6d55:

tenant Model Structure
----------------------

Openstack tenant
###### Related Nodes
- TO cloud-region( tenant BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO group-assignment( tenant MemberOf group-assignment, MANY2MANY, will delete target node)
- TO l3-network( tenant Uses l3-network, MANY2MANY, will delete target node)
- TO volume-group( tenant DependsOn volume-group, ONE2MANY, will delete target node)
- TO instance-group( tenant MemberOf instance-group, ONE2MANY, will delete target node)
- FROM service-subscription( service-subscription Uses tenant, MANY2MANY, will delete target node)
- FROM vserver( vserver BelongsTo tenant, MANY2ONE, will delete target node)
- FROM nos-server( nos-server BelongsTo tenant, MANY2ONE, will delete target node)

-TENANT cannot be deleted if related to VSERVER,NOS-SERVER


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        tenant-context | No | string |  |  | This field will store the tenant context.
        tenant-id | Yes | string |  |  | Unique id relative to the cloud-region.
        tenant-name | Yes | string |  |  | Readable name of tenant

.. _d_49d600d9d382217c1dfd5f233ae1d2ee:

tenants Model Structure
-----------------------

Collection of openstack tenants.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        tenant | No | array of :ref:`tenant <d_bebffb13dacd8221b192ee923e7f6d55>` |  |  | 

.. _d_406bbae3b294e03a31d5ac0f82f60e71:

vip-ipv4-address-list Model Structure
-------------------------------------

IPv4 Address Range
###### Related Nodes
- TO cloud-region( vip-ipv4-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO instance-group( vip-ipv4-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO subnet( vip-ipv4-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vnfc( vnfc Uses vip-ipv4-address-list, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        vip-ipv4-address | Yes | string |  |  | IP address
        vip-ipv4-prefix-length | No | integer | int64 |  | Prefix length, 32 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. _d_62c16656e50309371f72cbfd41409665:

vip-ipv6-address-list Model Structure
-------------------------------------

IPv6 Address Range
###### Related Nodes
- TO cloud-region( vip-ipv6-address-list BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO instance-group( vip-ipv6-address-list MemberOf instance-group, MANY2MANY, will delete target node)
- TO subnet( vip-ipv6-address-list MemberOf subnet, MANY2MANY, will delete target node)
- FROM vnfc( vnfc Uses vip-ipv6-address-list, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        is-floating | No | boolean |  |  | Indicator of fixed or floating address
        vip-ipv6-address | Yes | string |  |  | IP address
        vip-ipv6-prefix-length | No | integer | int64 |  | Prefix length, 128 for single address
        vlan-id-inner | No | integer | int64 |  | Inner VLAN tag
        vlan-id-outer | No | integer | int64 |  | Outer VLAN tag

.. _d_900e867e1614a172a0ad5db22d72d666:

virtual-data-center Model Structure
-----------------------------------

Virtual organization of cloud infrastructure elements in a data center context
###### Related Nodes
- FROM connector( connector LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- FROM generic-vnf( generic-vnf LocatedIn virtual-data-center, MANY2MANY, will delete target node)
- FROM logical-link( logical-link LocatedIn virtual-data-center, MANY2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vdc-id | Yes | string |  |  | Unique ID of the vdc
        vdc-name | Yes | string |  |  | Name of the virtual data center

.. _d_a99c89071b421d337ce339b13911e7d9:

virtual-data-centers Model Structure
------------------------------------

Virtual organization of cloud infrastructure elements in a data center context


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        virtual-data-center | No | array of :ref:`virtual-data-center <d_900e867e1614a172a0ad5db22d72d666>` |  |  | 

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

.. _d_ad81b54f04738c58764245c7f19c8fb9:

volume Model Structure
----------------------

Ephemeral Block storage volume.
###### Related Nodes
- FROM vserver (PARENT of volume, vserver AttachesTo volume, will delete target node)(3)

-(3) IF this FROM node is deleted, this VOLUME is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        volume-id | Yes | string |  |  | Unique ID of block storage volume relative to the vserver.
        volume-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. _d_cdbfc6d4ba80ed973d1db6e8f35d8d0f:

volume-group Model Structure
----------------------------

Persistent block-level storage.
###### Related Nodes
- TO cloud-region( volume-group BelongsTo cloud-region, MANY2ONE, will delete target node)
- TO complex( volume-group LocatedIn complex, MANY2ONE, will delete target node)
- FROM generic-vnf( generic-vnf DependsOn volume-group, ONE2MANY, will delete target node)
- FROM vf-module( vf-module Uses volume-group, ONE2ONE, will delete target node)
- FROM tenant( tenant DependsOn volume-group, ONE2MANY, will delete target node)


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        heat-stack-id | No | string |  |  | Heat stack id corresponding to this volume-group
        model-customization-id | No | string |  |  | captures the id of all the configuration used to customize the resource for the service.
        orchestration-status | No | string |  |  | Orchestration status of this volume-group
        vf-module-model-customization-id | No | string |  |  | helps relate the volume group to the vf-module whose components will require the volume group
        vnf-type | Yes | string |  |  | String capturing type of vnf, that was intended to identify the ASDC resource.  This field has been overloaded in service-specific ways and clients should expect changes to occur in the future to this field as ECOMP matures.
        volume-group-id | Yes | string |  |  | Unique ID of volume-group.
        volume-group-name | Yes | string |  |  | Name of the volume group.

.. _d_7c71457d0f7bcf0ba7ea098d44b6fbdc:

volume-groups Model Structure
-----------------------------

Collection of persistent block-level storage.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        volume-group | No | array of :ref:`volume-group <d_cdbfc6d4ba80ed973d1db6e8f35d8d0f>` |  |  | 

.. _d_ab5e963a2c36efa5f27a87a5a99d2eed:

volumes Model Structure
-----------------------

Collection of ephemeral Block storage volumes.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        volume | No | array of :ref:`volume <d_ad81b54f04738c58764245c7f19c8fb9>` |  |  | 

.. _d_39120694757a865f40339bfac43ea83b:

vserver Model Structure
-----------------------

Virtual Servers, aka virtual machine or VM.
###### Related Nodes
- TO tenant( vserver BelongsTo tenant, MANY2ONE, will delete target node)
- TO flavor( vserver Uses flavor, MANY2ONE, will delete target node)
- TO image( vserver Uses image, MANY2ONE, will delete target node)
- TO pserver( vserver HostedOn pserver, MANY2ONE, will delete target node)
- TO snapshot( vserver Uses snapshot, ONE2ONE, will delete target node)
- TO volume (CHILD of vserver, vserver AttachesTo volume, ONE2MANY, will delete target node)(2)
- FROM generic-vnf( generic-vnf HostedOn vserver, ONE2MANY, will delete target node)
- FROM vce( vce HostedOn vserver, ONE2MANY, will delete target node)
- FROM l-interface( l-interface BindsTo vserver, MANY2ONE, will delete target node)(1)
- FROM vf-module( vf-module Uses vserver, ONE2MANY, will delete target node)
- FROM vnfc( vnfc HostedOn vserver, ONE2MANY, will delete target node)

-(1) IF this VSERVER node is deleted, this FROM node is DELETED also
-(2) IF this VSERVER node is deleted, this TO node is DELETED also


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        in-maint | Yes | boolean |  |  | Used to indicate whether or not this object is in maintenance mode (maintenance mode = true). This field (in conjunction with prov-status) is used to suppress alarms and vSCL on VNFs/VMs.
        is-closed-loop-disabled | Yes | boolean |  |  | Used to indicate whether closed loop function is enabled on this node
        prov-status | No | string |  |  | Trigger for operational monitoring of this resource by Service Assurance systems.
        vserver-id | Yes | string |  |  | Unique identifier for this vserver relative to its tenant
        vserver-name | Yes | string |  |  | Name of vserver
        vserver-name2 | No | string |  |  | Alternative name of vserver
        vserver-selflink | Yes | string |  |  | URL to endpoint where AAI can get more details

.. _d_4d984773f04b63e435c720fb5d6641be:

vservers Model Structure
------------------------

Collection of virtual Servers, aka virtual machines or VMs.


.. csv-table::
    :delim: |
    :header: "Name", "Required", "Type", "Format", "Properties", "Description"
    :widths: 20, 10, 15, 15, 30, 25

        vserver | No | array of :ref:`vserver <d_39120694757a865f40339bfac43ea83b>` |  |  | 

