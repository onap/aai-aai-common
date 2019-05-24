.. This work is licensed under a Creative Commons Attribution 4.0 International License.

AAI Documentation Repository
----------------------------
Active and Available Inventory (AAI) is the “one-stop-shop” where all the network information comes together.  Modern networks are complex, dynamic, and difficult to manage, and AAI delivers live insight into hybrid services and virtual resources, in support of closed loop processes.  We hold the references to the network services and infrastructure, data center resources, connectivity components, and service overlays; and we know how they’re all related.

The key AAI repos for running the AAI REST APIs:

- aai/aai-common: This holds the model, annotations and common modules used across the Resources and Traversal micro services
- aai/traversal: AAI Traversal Micro Service providing REST APIs for traversal/search of inventory resources
- aai/resources: AAI Resources Micro Service providing CRUD REST APIs for inventory resources

.. toctree::
   :maxdepth: 2

   platform/index.rst
   release-notes.rst
   AAI REST API Documentation/AAIRESTAPI_DUBLIN.rst

