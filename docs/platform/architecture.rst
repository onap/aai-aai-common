.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. _architecture:

Architecture
------------

AAI Architecture in ONAP
^^^^^^^^^^^^^^^^^^^^^^^^

AAI provides ONAP with its logically centralized view of inventory data, taking in updates from orchestrators, controllers, and assurance systems.  With that responsibility, it takes on a key data management role, providing the ONAP components not only the current "as-built" view of the services, but also the view into the data of the system itself; it's integrity, it's chronology, and where the source of the information can be found.

In addition to inventory and topology management, AAI provides the ability to do inventory administration.  Data in AAI is continually updated in real-time as changes are made within the cloud. Because AAI is metadata-driven, new resources and services can be added quickly with Service Design and Creation (SDC) catalog definitions, using the AAI model loader, thus eliminating the need for lengthy development cycles. In addition, new inventory item types can be added quickly through schema configuration files.

.. image:: images/aai-architecture.PNG

AAI's Role in ONAP
^^^^^^^^^^^^^^^^^^

AAI is where the data converges, where the pictures come together, and where the ONAP actor systems ask questions so they can make their decisions.  As new network or data center resources become available, AAI is updated with their specifics via REST APIs.  When new service types are designed, or new services instantiated, systems keep AAI up to date of their deployment at each step of the way.  As telemetry is gathered for the services as well as their underlying infrastructure, the health and analytics conclusions reached are stored in AAI as state information.  When assurance systems detect a failure, AAI is queried to determine the extent of the impact.

With the high volume and variety of data, AAI must be prepared to answer many types of queries; real-time search to quickly retrieve specific items from an ocean of data, relationships to determine impacts and consequences, aggregations and counts to explore availability and consumption, validation and integrity to establish whether systems are acting on good information, history and provenance to reconstruct the current view and its context, and enrichment out to legacy systems to examine the low-level details of the network and virtual assets.

.. image:: images/aai_in_onap.png
