.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_

=============
 AAI Bulk API
=============

This page will serve as a reference for how to use the A&AI bulk API's available in 1810+.

Bulk Overview
=============

To execute any of the bulk api's, a client will perform a POST on the specific bulk api and include a payload indicating the nodes to be added/updated/deleted.The version (v$ ie. v14) dictates which release's REST API version the output will be based on.
 
Single transaction API
======================

API takes in transaction object which consists of an array of operations, the operations are executed against A&AI in a single transaction. All operations must succeed for the actions to be committed. If any of the operations fail these changes will be rolled back.

API URI
=======

.. code::

   URI
   POST /aai/v$/bulk/single-transaction
   Payload
   Skeleton 
   {
     "operations": [ //array of operations for this transaction
       {
         "action": "", //aai action to be taken, i.e put, patch, delete
         "uri": "", //aai uri to execute action against
         "body": {} //json object which is the body of the equivalent REST request
       }
     ]
   }

Response

.. code::

   Skeleton 
   {
     "operation-responses": [ //result of each of the request operations
       {
         "action": "", //aai action that was taken
         "uri": "", //aai uri that was executed against
         "response-status-code": , //http status code
         "response-body": null // response body of the of the equivalent REST request
       }
     ]
   }

Request-Response Examples
=========================

Example - 1 (Success)
+++++++++++++++++++++

.. code::

   Request  
   {
       "operations": [
           {
               "action": "put",
               "body": {
                   "fqdn": "pserver-key-fqdn",
                   "hostname": "pserver-1-key"
               },
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-1-key"
           },
           {
               "action": "patch",
               "body": {
                   "fqdn": "patched-fqdn"
               },
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-1-key"
           },
           {
               "action": "put",
               "body": {
                   "fqdn": "pserver-key-fqdn",
                   "hostname": "pserver-2-key"
               },
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-2-key"
           }
       ]
   }

Response
++++++++
.. code:: 

   {
       "operation-responses": [
           {
               "action": "put",
               "response-body": null,
               "response-status-code": 201,
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-1-key"
           },
           {
               "action": "patch",
               "response-body": null,
               "response-status-code": 200,
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-1-key"
           },
           {
               "action": "put",
               "response-body": null,
               "response-status-code": 201,
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-2-key"
           }
       ]
   }

Example - 2 (Success)
=====================
Request
+++++++

.. code::

   {
       "operations": [
           {
               "action": "put",
               "body": {
                   "fqdn": "pserver-key-fqdn",
                   "hostname": "pserver-key"
               },
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-key"
           },
           {
               "action": "put",
               "body": {
                   "city": "city",
                   "country": "NONE",
                   "data-center-code": "code",
                   "identity-url": "N/A",
                   "physical-location-id": "complex-key",
                   "physical-location-type": "type",
                   "postal-code": "12345",
                   "region": "Earth",
                   "state": "state",
                   "street1": "street"
               },
               "uri": "/cloud-infrastructure/complexes/complex/complex-key"
           },
           {
               "action": "put",
               "body": {
                   "related-link": "/aai/v13/cloud-infrastructure/pservers/pserver/pserver-key",
                   "related-to": "pserver"
               },
               "uri": "/cloud-infrastructure/complexes/complex/complex-key/relationship-list/relationship"
           },
           {
               "action": "delete",
               "body": {},
               "uri": "/network/generic-vnfs/generic-vnf/gvnf-key?resource-version=0"
           }
       ]
   }

Response
++++++++

.. code::

   {
       "operation-responses": [
           {
               "action": "put",
               "response-body": null,
               "response-status-code": 201,
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-key"
           },
           {
               "action": "put",
               "response-body": null,
               "response-status-code": 201,
               "uri": "/cloud-infrastructure/complexes/complex/complex-key"
           },
           {
               "action": "put",
               "response-body": null,
               "response-status-code": 200,
               "uri": "/cloud-infrastructure/complexes/complex/complex-key/relationship-list/relationship"
           },
           {
               "action": "delete",
               "response-body": null,
               "response-status-code": 204,
               "uri": "/network/generic-vnfs/generic-vnf/gvnf-key?resource-version=0"
           }
       ]
   }

Example - 3 (Failure on mismatched resource version on delete)
==============================================================
Request
+++++++

.. code::

   {
       "operations": [
           {
               "action": "put",
               "body": {
                   "fqdn": "pserver-key-fqdn",
                   "hostname": "pserver-key"
               },
               "uri": "/cloud-infrastructure/pservers/pserver/pserver-key"
           },
           {
               "action": "put",
               "body": {
                   "city": "city",
                   "country": "NONE",
                   "data-center-code": "code",
                   "identity-url": "N/A",
                   "physical-location-id": "complex-key",
                   "physical-location-type": "type",
                   "postal-code": "12345",
                   "region": "Earth",
                   "state": "state",
                   "street1": "street"
               },
               "uri": "/cloud-infrastructure/complexes/complex/complex-key"
           },
           {
               "action": "put",
               "body": {
                   "related-link": "/aai/v13/cloud-infrastructure/pservers/pserver/pserver-key",
                   "related-to": "pserver"
               },
               "uri": "/cloud-infrastructure/complexes/complex/complex-key/relationship-list/relationship"
           },
           {
               "action": "delete",
               "body": {},
               "uri": "/network/generic-vnfs/generic-vnf/gvnf-key?resource-version=1"
           }
       ]
   }

Response
++++++++

.. code:: 

   {
       "requestError": {
           "serviceException": {
               "messageId": "SVC3000",
               "text": "Invalid input performing %1 on %2 (msg=%3) (ec=%4)",
               "variables": [
                   "POST",
                   "/aai/v14/bulk-single-transaction-multi-operation",
                   "Invalid input performing %1 on %2:Operation 3 failed with status code (412) and msg ({\"requestError\":{\"serviceException\":{\"messageId\":\"SVC3000\",\"text\":\"Invalid input performing %1 on %2 (msg=%3) (ec=%4)\",\"variables\":[\"DELETE\",\"/network/generic-vnfs/generic-vnf/gvnf-key\",\"Precondition Failed:resource-version MISMATCH for delete of generic-vnf\",\"ERR.5.4.6131\"]}}})",
                   "ERR.5.2.3000"
               ]
           }
       }
   } 
 
