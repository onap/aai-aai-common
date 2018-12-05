.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Nodes Query
===========

In working with AAI's standard REST API, you may have noticed that
certain API paths have a hierarchy to them and require a client to
know multiple object keys to preform GETs. For example: the vserver
object is under tenant, which is itself under cloud-region. If you
wanted to look up a vserver by name, you would still need to know the
tenant-id and cloud-region-id (and cloud-owner) in order to
successfully perform that GET. The nodes API allows for more freedom
in querying AAI, allowing clients to circumvent the need to know
every key in the hierarchy. Using the previous example, the below is
how the API called would change for looking up a vserver by name:

.. code::

  GET /aai/v$/cloud-infrastructure/cloud-regions/cloud-region/{cloud-owner}/{cloud-region-id}/tenants/tenant/{tenant-id}/vservers?vserver-name={vserver-name}
  becomes
  GET /aai/v$/nodes/vservers?vserver-name={vserver-name}

A side effect of this is that if the same vserver name was reused between
multiple cloud-regions or tenants the client will receive multiple
vservers in the response. Vserver ID and vserver name are
intentionally non-unique outside of their cloud-region/tenant
hierarchy, as are many other keys for nested objects.

API URI
~~~~~~~

.. code::

   GET /aai/v$/nodes/{plural}?{property}={value}
   OR
   GET /aai/v$/nodes/{plural}/{singular}/{key}

Optional Query Parameters
~~~~~~~~~~~~~~~~~~~~~~~~~
The Nodes API can make use of all the optional query
parameters available on the standard REST API.

Depth
~~~~~

You can pass a depth query parameter to indicate what level of child objects you want
returned. By default the output will be depth = 0 (no "children", only
"cousin" relationships). When using depth in conjunction with the
format query parameter, it will only apply to the on the resource or
resource_and_url formats.

.. code::

   GET /aai/v$/nodes/{plural}/{singular}/{key}?depth={integer}

Nodes Only
~~~~~~~~~~

You can pass a nodes only query parameter to have the output only contain
the object properties with no relationships.

.. code::

   GET /aai/v$/nodes/{plural}/{singular}/{key}?nodes-only
   OR
   GET /aai/v$/nodes/{plural}/{singular}/{key}?format={format}&nodesOnly=true

Format
~~~~~~

You can optionally request an output format different from the default
REST API output. You can reference the list of formats on the `Custom
Query <customQueries.html>`_ wiki page for the full list of available
formats and examples.

.. code::

   GET /aai/v$/nodes/{plural}/{singular}/{key}?format={format}
   
Usage with Custom Query
~~~~~~~~~~~~~~~~~~~~~~~

The Nodes API can be called directly or as the start node for Custom
Queries. Please reference the Custom Queries wiki page for full
documentation on how to use that interface.

.. code::

   PUT /aai/v$/query?format={format} with payload like..  
   { "start": ["nodes/{plural}/{singular}/{key}"], "query": "query/{query-name}" }
