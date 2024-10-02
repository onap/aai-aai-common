.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2017 AT&T Intellectual Property.  All rights reserved.
.. _release_notes:


AAI Release Notes
==================

Abstract
========

This document provides release notes for the Active and Available Inventory Project's Kohn Release

Release Data
============

Version: 13.0.1
---------------

General
-------

Features
^^^^^^^^

- replace dmaap dependency with kafka
- make root logging level configurable via chart
- schema update to `v29`:
    * New `service-instance` attributes added in AAI schema
    * New `slice-profile` attributes added in AAI schema
    * Added New Object: `feasibility-check-job` in the schema
    * Added new Edge Rule for `feasibility-check-job`, `slice-profile` and `service-subscription`

aai-common (`1.13.6`)
---------------------

Features
^^^^^^^^

- update tinkerpop from `3.2.3` to `3.2.11`
- improve some bean loading for schema generation related objects

resources (`1.13.5`)
--------------------

Features
^^^^^^^^

- update aai-common version to `1.13.6`
- make project runnable locally via `mvn spring-boot:run`
- migrate to Junit 5

Fixes
^^^^^

- do not throw exception to communicate ok response in echo/liveness probe

traversal (`1.13.5`)
--------------------

Features
^^^^^^^^

- update aai-common version to `1.13.6`
- use newer java 8 base image

model-loader (`1.14.1`)
-----------------------

Features
^^^^^^^^

- update dependencies (in particular spring-boot `2.1` to `2.7`)
- code refactorings (model controller, less by-reference parameter updates, spring RestTemplate instead of aai rest client)
- tracing support
- add spring-boot actuator to enable liveness probes

graphadmin (`1.13.7`)
---------------------

Features
^^^^^^^^

- update aai-common to `1.13.6`

babel (`1.13.1`)
----------------

Features
^^^^^^^^

- remove AAF dependency, update dependencies
- support tracing

Fixes
^^^^^

- service was returning text/plain response with json response body

:Release Date: 2024-06-13

Version: 13.0.0
---------------

New Features
------------

- Spring Boot update in aai-common, resources and traversal from 2.1 to 2.4
- Tracing support in resources and traversal
- Optionally enable `database level caching for JanusGraph <https://docs.janusgraph.org/operations/cache/#database-level-caching>`_
- Change default logging to write to stdout instead of files
- Reduce log noise by not logging happy pass for liveness probes

Fixes
-----

- Fix metrics monitoring via Prometheus

:Release Date: 2022-11-10

Version: 11.0.0
---------------

:Release Date: 2022-11-10

New Features
------------

The R11 Kohn release of ONAP includes updates to the schema and edge rules. AAI is serving v27 as the latest version of the REST APIs, and has support for v25.

- Model updates made on the following nodes for changes to attributes or indexing, present in v27
    * User Network Interfaces (uni)
    * Route target
    * Network Route
    * Bgp neighbor
    * Vpn binding
    * Lag interface
    * Physical interface
    * Logical interface

- Edge rule changes include
    * UPDATE collection > service instance edge label and multiplicity change
    * ADD configuration > p-interface
    * ADD collection>endpoint
    * ADD collection>configuration
    * ADD collection>aggregate-route
    * ADD collection>parameter-list
    * ADD collection>policy-key-term
    * ADD collection>policy-map-member
    * ADD collection>rule
    * ADD collection>profile
    * ADD collection>policy
    * ADD collection>l3-network
    * ADD collection>vpn-binding
    * ADD collection>community-list
    * ADD collection>object-group
    * ADD p-interface>object-group

- Please note log4j is still on older versions in a transitive dependency for aaf auth for the following mS
  * onap-aai-aai-common
  * onap-aai-babel
  * onap-aai-resources
  * onap-aai-schema-service
  * onap-aai-traversal

Known Limitations, Issues, and Workarounds
==========================================

Known Issues
------------

* DMAAP Events are Not Being Published from AAI-Resources (AAI-3507)

Workarounds
-----------

* None

References
==========

Quick links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_

For more information on the ONAP Honolulu release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://lf-onap.atlassian.net
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Version: 10.0.0
---------------

:Release Date: 2022-06-02

The R10 Jakarta release of ONAP A&AI addressed security vulnerabilities and enhanced the model for the CCVPN Use Case

- Schema updated for CCVPN use case mainly enhancing and bug fixes of the Cloud Leased Line (CLL) service
- Updated versions for indy, httpclient, freemarker, activemq, commons-io, commons-compress, logback-core, commons-codec, groovy, netty-all, netty-handler, gson, and snakeyaml in various mS
- Please note log4j is still on older versions in a transitive dependency for aaf auth for the following mS
  * onap-aai-aai-common
  * onap-aai-babel
  * onap-aai-resources
  * onap-aai-schema-service
  * onap-aai-traversal

Version: 9.0.1
--------------

:Release Date: 2022-02-17

The R9 Istanbul maintenance release of ONAP A&AI addressed some security vulnerabilities mainly for the Log4J dependencies

- Updated the direct dependency log4j libraries to 2.17.2
- Please note log4j is still on older versions in a transitive dependency for aaf auth for the following mS
  * onap-aai-aai-common
  * onap-aai-babel
  * onap-aai-resources
  * onap-aai-schema-service
  * onap-aai-traversal

Version: 9.0.0
--------------

:Release Date: 2021-11-04

New Features
------------

The R9 Istanbul release of ONAP includes updates to both use cases and non-functional requirements. AAI is serving v24 as the latest version of the REST APIs, and has support for v21.

- Functional Updates
    * Model updates as part of CCVPN Transport Slicing Feature
    * Model updates as part of Smart Intent Guarantee based on IBN Feature
    * Model updates as part of CNF Orchestration Feature
- Non-functional Updates
    * Sonar & Security updates
    * Rolling upgrade functionality
    * Micrometer integration
    * Prometheus monitoring integration

Istanbul Known Limitations, Issues, and Workarounds
---------------------------------------------------

Known Issues
------------

* None

Workarounds
-----------

* None

Honolulu References
-------------------

Quick links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_

For more information on the ONAP Honolulu release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://lf-onap.atlassian.net
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Version: 8.0.0
--------------

:Release Date: 2021-04-29

New Features
------------

The R8 Honolulu release of ONAP includes updates to both use cases and non-functional requirements.

- Functional Updates
    * Model updates as part of CCVPN Transport Slicing Feature
    * Model updates as part of Network Slicing Feature
    * Model updates as part of Multi-tenancy
    * Multi-tenancy implemented allowing for control of data by owner (disabled by default)
    * GraphGraph POC enhanced for schema visualization and visual model generation
    * Sparky UI updates including Browse, Specialized Search, BYOQ, and BYOQ Builder Views
- Non-functional Updates
    * Updated eligible microservices (non-janusgraph dependent) to use Java 11
- Deprecation Notice
    * Data-router, Search-data-service, Elastic, and Router-Core are put into maintenance mode and removed from release.

Honolulu Known Limitations, Issues, and Workarounds
---------------------------------------------------

Known Issues
------------

* None

Workarounds
-----------

* None

Istanbul References
-------------------

Quick links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_

For more information on the ONAP Honolulu release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://lf-onap.atlassian.net
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Version: 7.0.1
--------------

:Release Date: 2021-05-10

The R7 Guilin maintenance release of ONAP A&AI just addressed some bug fixes and certificate updates

- Updated Certificates
- Updated Edge rule for bandwidth attribute
- AAI-EVENT notifications failing to publish was fixed

Version: 7.0.0
--------------

:Release Date: 2020-12-03

New Features
------------

The R7 Guilin release of ONAP includes updates to both use cases and non-functional requirements. The AAI platform maturity rating graduated from Incubation to Mature in Guilin.  AAI is serving v21 as the latest version of the REST APIs, and has support for v19.

- Functional Updates
    * Model updates as part of CCVPN Transport Slicing Feature
    * Model updates as part of xNF Software Upgrade feature
    * Model updates as part of Multi-tenancy
    * Updates to SDC model parsing to support Bulk PM/PM Data Control Extension & E2E Network Slicing features
    * Configurable ability to control concurrency locking
    * Configurable ability to enforce ownership of owning entity on pnf crud interactions (Multi-tenancy poc)
    * Enhancements to the model based on physical inventory
    * Support for nested json formatted responses using the as-tree=true parameter for traversal mS calls
- Non-functional Updates
    * Updated microservices to run as non-root
    * Spring boot 2 upgrades to our microservices
    * Enhanced logging
    * Added limits to aai pods
    * Update mS based on license scan findings
- Deprecation Notice
    * ESR Server is retired
    * ESR GUI is retired
    * AAI Sparky UI is not supported in Guilin nor is its supporting mS data-router, search-data-service, or elastic. Targeted for retirement in Honolulu.

Guilin Known Limitations, Issues, and Workarounds
-------------------------------------------------

Known Issues
------------

* `AAI-3219 <https://lf-onap.atlassian.net/browse/AAI-3219>`_ - AAI-EVENT notifications failed to be published to DMaap

Workarounds
-----------

The following is our workaround (i.e., replacing HTTPS with HTTP):

 .. code-block:: bash

    /** Change each of these configmaps below**/
    kubectl -n onap edit configmaps dev-aai-resources-configmap
    kubectl -n onap edit configmaps dev-aai-traversal-configmap
    kubectl -n onap edit configmaps dev-aai-graphadmin-configmap
    kubectl -n onap edit configmaps dev-aai-data-router-dynamic
    // The target attributes need to be changed are:
    // change Dmaap port from 3905 => 3904
    // change Dmaap protocol from https => http
    /** Restart related pods **/
    kubectl n onap delete pod {POD1} {POD2} {POD3} {POD4}
    //where POD1-4 are pod names of dev-aai-resources, dev-aai-traversal, dev-aai-graphadmin, and dev-aai-data-router, respectively.

Guilin References
-----------------

Quick links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_

For more information on the ONAP Guilin release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://lf-onap.atlassian.net
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Version: 6.0.0
--------------

:Release Date: 2020-06-04

New Features
------------

The R6 Frankfurt release of ONAP includes updates to both use cases and non-functional requirements.  AAI is serving v19 as the latest version of the REST APIs, and has support for v16 (Dublin and El Alto).

- Implemented new parent POM under org.onap.aai.aai-common.aai-parent for simplified management of 3rd party dependencies
- Upgrade to spring-boot 2 (partially complete)
- Model updates and edge rules changes in support of the following use cases:
  * CCVPN for SOTN NNI
  * 5G Network Slicing
  * Multi-Domain Optical Network Services
  * PNF enhancements
- Papyrus XMI UML files for run-time data model reverse engineering
- Integration with sonarcloud
- All containers run as non-root user

champ, spike, and gizmo are deprecated and removed from the helm chart.

Frankfurt Known Limitations, Issues, and Workarounds
----------------------------------------------------

Known Issues
------------

* `AAI-2766 <https://lf-onap.atlassian.net/browse/AAI-2766>`_ - AAI data-router cannot communicate with DMaaP message router service
* `AAI-2905 <https://lf-onap.atlassian.net/browse/AAI-2905>`_ - AAI sparky cannot communicate with portal due to certificate issue, might be related to https://lf-onap.atlassian.net/browse/PORTAL-875

The AAI sub-project External System Registry (ESR) is re-using elalto containers.  The integration team has helped ESR to meet security requirements for Frankfurt, and the AAI is grateful for the contribution.

Workarounds
-----------

Roles for sparky are loaded into AAF by default, so previous workaround is no longer required.  However, the pods cannot resolve portal.api.simpledemo.onap.org anymore, so it's necessary to add an entry to /etc/hosts in the sparky-be pod.  This will get around the "unknown host" issue, but then it's leads to AAI-2905, where AAI cannot get the roles from Portal due to the issue with the AAF auto-created certificate.

The community has been unable to make data-router communicate with DMaaP, we welcome contributors who can help resurrect this service, or it will be deprecated in Guilin.

Frankfurt References
--------------------

Quick links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_

For more information on the ONAP Frankfurt release, please see:

#. `ONAP Home Page`_
#. `ONAP Documentation`_
#. `ONAP Release Downloads`_
#. `ONAP Wiki Page`_

.. _`ONAP Home Page`: https://www.onap.org
.. _`ONAP Wiki Page`: https://lf-onap.atlassian.net
.. _`ONAP Documentation`: https://docs.onap.org
.. _`ONAP Release Downloads`: https://git.onap.org

Version: 5.0.2
--------------
:Release Date: 2019-10-03

**New Features**

The R5 El Alto release of ONAP is a maintenance release, focusing on
deployability, technical debt, and footprint opimization.

AAI focused on converting all of our microservices to Alpine, created
common images for users to select either Alpine or Ubuntu, and reduced
the number of microservices that is started by default for the
demo. We updated to newer versions of spring boot - we are in the
process of moving to spring-boot 2, but many of the microservices are
still running 1.5.21.  We updated to JanusGraph 0.2.3, which is a
seamless upgrade from 0.2.0 which was used in Dublin.

Users who would like to further reduce the AAI footprint can update the
aai/oom helm charts.

To re-enable the services that have been disabled by default, update
to "enabled: true" in aai/oom/values.yaml:

 .. code-block:: bash

    aai-champ:
	enabled: true
    aai-gizmo:
	enabled: true
    aai-spike:
	enabled: true

To disable other components that are not critical to the Integration
use cases (vFw, vLB, vDNS, etc), add "enabled: false" in
aai/oom/values.yaml for each of the following services:

 .. code-block:: bash

    aai-data-router:
        enabled: false
    aai-search-data:
        enabled: false
    aai-elasticsearch:
        enabled: false
    aai-sparky-fe:
        enabled: false

*Known Vulnerabilities in Used Modules*

AAI code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain
open have been assessed for risk and determined to be false
positive. The AAI open Critical security vulnerabilities and their
risk assessment have been documented as part of the link

**Known Issues**

The AAI UI is now integrated with Portal and AAF.  However, the AAF
default boostrap does not include a role that is necessary the demo
user to access the AAI UI.

Run the following as a workaround, adjust the URL and credentials
according to your environment. The user in CRED must be able to update
the org.onap.aai namespace.  The following example has been tested from
inside the AAI resources pod.

 .. code-block:: bash

    URL='https://aaf-service.onap:8100'
    CRED='aai@aai.onap.org:demo123456!'

    curl -v -k -u "$CRED" -H "Content-Type: application/RoleRequest+json" $URL/authz/role -d '{"name":"org.onap.aai.aaiui"}'

    curl -v -k -u "$CRED" -H "Content-Type: application/UserRoleRequest+json" $URL/authz/userRole -d '{ "user":"demo@people.osaaf.org", "role":"org.onap.aai.aaiui" }'

Frankfurt will include the role and role assignment in the
default bootstrap data (being tracked under `AAI-2475 <https://lf-onap.atlassian.net/browse/AAI-2475>`__)

- `AAI-2606 <https://lf-onap.atlassian.net/browse/AAI-2606>`_ Schema-service entity description is not available

- `AAI-2457 <https://lf-onap.atlassian.net/browse/AAI-2457>`_ Inconsistent error messages when getting AAI resources

- `AAI-2457 <https://lf-onap.atlassian.net/browse/AAI-2457>`_ Inconsistent error messages when getting AAI resources

- `AAI-2092 <https://lf-onap.atlassian.net/browse/AAI-2092>`_ aai-resources does excessive amounts of logging

- `AAI-2082 <https://lf-onap.atlassian.net/browse/AAI-2082>`_ aai-resources gives incorrect output when aai-cassandra has shutdown with failure

Quick Links:

- `Active and Available Inventory project page <https://lf-onap.atlassian.net/display/DW/Active+and+Available+Inventory+Project>`_
- `R5 Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_
- `R5 Project Vulnerability Review Table for AAI <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=64003431>`_


Version: 1.4.0
--------------

:Release Date: 2019-06-08

**New Features**

The R4 Dublin release of ONAP is a balanced release, focusing on
platform maturity and deployablity while also bringing in significant
new features and use cases . AAI continued to leverage oom and
kubernetes, and added new data types in support of multiple R4 use
cases.  AAI added a new schema service which moves AAI closer to being
more model-driven and flexible.

AAI is more model driven in Casablanca, which means it dynamically
operationalize new and updated models at run-time, with minimal
downtime and coding, so that the latest service and resource models
can be delivered quickly. To do this, AAI must update its internal
model, external API and behavior to respond to change to service and
resource models, including schema changes. The schema service provides
ONAP users the ability to quickly change the AAI data model without
re-building key microservices.

AAI delivered 55%+ test coverage on all Java-based repos.

See `AAI-1779 <https://lf-onap.atlassian.net/browse/AAI-1779>`__ for details
on the schema updates in R4.

Some AAI services can be configured to leverage the ONAP Pluggable
Security Sidecar proof of concept (disabled by default, see the charts
under aai/oom for more details).

AAI now manages its own helm charts. See `aai/oom <https://gerrit.onap.org/r/admin/repos/aai/oom>`__

**Known Issues**

The AAI UI is now integrated with Portal and AAF.  However, the AAF
default boostrap does not include a role that is necessary the demo
user to access the AAI UI.

Run the following as a workaround, adjust the URL and credentials
according to your environment. The user in CRED must be able to update
the org.onap.aai namespace.  The following example has been tested from
inside the AAI resources pod.

 .. code-block:: bash

    URL='https://aaf-service.onap:8100'
    CRED='aai@aai.onap.org:demo123456!'

    curl -v -k -u "$CRED" -H "Content-Type: application/RoleRequest+json" $URL/authz/role -d '{"name":"org.onap.aai.aaiui"}'

    curl -v -k -u "$CRED" -H "Content-Type: application/UserRoleRequest+json" $URL/authz/userRole -d '{ "user":"demo@people.osaaf.org", "role":"org.onap.aai.aaiui" }'

Future releases will include the role and role assignment in the
default bootstrap data (being tracked under `AAI-2475 <https://lf-onap.atlassian.net/browse/AAI-2475>`__)


**Security Notes**

*Fixed Security Issues*

- `OJSI-114 <https://lf-onap.atlassian.net/browse/OJSI-114>`_ In default deployment AAI (aai) exposes HTTP port 30232 outside of cluster.

*Known Security Issues*

*Known Vulnerabilities in Used Modules*

AAI code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The AAI open Critical security vulnerabilities and their risk assessment have been documented as part of the `R4 project wiki <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=64003431>`_.

Quick Links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_
- `R4 Project Vulnerability Review Table for AAI <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=64003431>`_




Version: 1.3.2
--------------

:Release Date: 2019-03-31

**Updates**

AAI demo certificates were going to expire before Dublin release, so they've been refreshed to last until 2020.

- `AAI-2282 <https://lf-onap.atlassian.net/browse/AAI-2282>`_ Update certifcate for Casablanca 3.0.2

Version: 1.3.1
--------------

:Release Date: 2019-01-31

**New Features**

The Casablanca Maintenance Release provides a number of security and
bug fixes. Highlights of the issues corrected in the Casablanca
Maintenance Release:

- `AAI-2047 <https://lf-onap.atlassian.net/browse/AAI-2047>`_ Make success of createDbSchema job required to proceed in AAI startup

- `AAI-1923 <https://lf-onap.atlassian.net/browse/AAI-1923>`_ Problem deleting due to EdgeRules in CCVPN usecase Casablanca

- `AAI-1776 <https://lf-onap.atlassian.net/browse/AAI-1776>`_ Champ fails to start

- `AAI-1958 <https://lf-onap.atlassian.net/browse/AAI-1958>`_ [graphadmin] createDbSchema.sh job loses detailed logfile

- `AAI-1973 <https://lf-onap.atlassian.net/browse/AAI-1973>`_ Schema update wiki is out of data of Casablanca

- `AAI-2058 <https://lf-onap.atlassian.net/browse/AAI-2058>`_ Upgrade to latest jetty-security

- `AAI-2076 <https://lf-onap.atlassian.net/browse/AAI-2076>`_ A&AI healthcheck timeout

- `AAI-2079 <https://lf-onap.atlassian.net/browse/AAI-2079>`_ aai-traversal and aai container failure to deploy issues in casablanca 3.0.0-ONAP

Dependencies were updated in multiple repos to patch security
vulnerabilities.

**Known Issues**

- `AAI-2090 <https://lf-onap.atlassian.net/browse/AAI-2090>`_ aai-data-router pod enters CrashLoopBackOff state

This issue can still present itself if you use the OOM chart which
references version 1.3.2 (which is the version specified in the
casablanca branch of oom), data-router will not start.  The workaround
is to set 1.3.3 in the values.yaml file for data-router, or use the
docker-manifest to override.  File is oom/kubernetes/aai/charts/aai-data-router/values.yaml

Users should pay special attention to `AAI-2064
<https://lf-onap.atlassian.net/browse/AAI-2064>`_ and should consult `this
page <https://www.rabbitmq.com/ssl.html>`_ for instructions on how to
properly secure it if they are concerned about the issue.

**Security Notes**

AAI code has been formally scanned during build time using NexusIQ and
all Critical vulnerabilities have been addressed, items that remain
open have been assessed for risk and determined to be false
positive. The AAI open Critical security vulnerabilities and their
risk assessment have been documented as part of the `R3 project wiki
<https://lf-onap.atlassian.net/pages/viewpage.action?pageId=45307817>`_.

Quick Links:

- `AAI main project page <https://lf-onap.atlassian.net/display/DW/Active+and+Available+Inventory+Project>`_
- `CMR Vulnerability Review Table for AAI <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=45307817>`_


Version: 1.3.0
--------------

:Release Date: 2018-11-30

**New Features**

The R3 Casablanca release of ONAP again focuses on platform maturity
and deployablity. AAI continued to leverage oom and kubernetes, and
added new data types in support of multiple R3 use cases.  AAI added a
new schema ingest library which moves AAI closer to being more
model-driven and a new microservice called "graphadmin" which provides
graph maintenance and configuration facilities.

AAI is more model driven in Casablanca, which means it dynamically
operationalize new and updated models at run-time, with minimal
downtime and coding, so that the latest service and resource models
can be delivered quickly. To do this, AAI must update its internal
model, external API and behavior to respond to change to service and
resource models, including schema changes. There are changes required
to align on implementation across different ONAP components to provide
a more strategic model-driven A&AI implementation. For this release
decomposing AAI model/schema artifacts (OXM/XSD) into a more granular
approach better enables extensibility and support logical subdivision
of models.

AAI added support fo the Cross Domain and Carrier Layer VPN use case
by adding new object types, models, and edge rules.

AAI delivered 50%+ test coverage on all Java-based repos.

Added support Support for SR-IOV.

Authentication and Authorization is performed using AAF with the CADI
framework. We use basic authentication with RBAC (Role Based Access
Control) to secure the AAI REST APIs.

AAI added automation support for orchestrating SR-IOV Provider
Networks that are compatible with the Network Cloud 1.0 infrastructure
solution based on standard SR-IOV. Allow for standard SR-IOV Provider
Networks to be defined with a set of one or more VLAN associations.

AAI added suport to allow clients to specify the format on GET
operations in the resources micoservices to output like the custom
query API does.

Added support for VLAN tagging.

**Known Issues**

Please find at this link the list of issues that will be fixed in the `Casablanca Maintenance Release <https://lf-onap.atlassian.net/issues/?jql=fixVersion%20%3D%20%22Casablanca%20Maintenance%20Release%22%20and%20type%20%3D%20Bug%20and%20project%20%3D%20%22Active%20and%20Available%20Inventory%22>`_

**Security Notes**

AAI code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The AAI open Critical security vulnerabilities and their risk assessment have been documented as part of the `R2 project wiki <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=45307817>`_.

Quick Links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_
- `R3 Project Vulnerability Review Table for AAI <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=45307817>`_


Version: 1.2.0
--------------

:Release Date: 2018-06-07

**New Features**

The R2 Beijing release of ONAP focuses on platform maturity - to that
end, AAI has switched from Titan on hbase to JanusGraph on a
multi-replica cassandra deployment.  We have added several
microservices which will be fully operational in R3 Casablanca.
Another significant change in R2 is that we converted our
Microservices from ASJC 2 to Springboot 1.5.  AAI provides
configurations for orchestration via HEAT or via OOM / kubernetes for
scaling and resiliency.

AAI added champ, a graph abstraction microservice, and Gizmo, a new
way to perform CRUD operations on the graph in a more atomic way that
exposes more of the underlying graph infrastructure.  Babel is a new
microservice that does TOSCA model translation on behalf of model
loader.  Event client provides an abstraction for dmaap events.

ONAP AAI R2 includes the following components:

- AAI Data Management
- Resources (CRUD operations)
- Traversal (Advanced search operations)
- Data Router (Route/persist AAI event data for consumption by the UI)
- Model Loader (Obtains SDC artifacts and loads them into the A&AI Resources service for storage)
- Search Data Service (Abstraction layer for searchengine, supporting queries and updates)
- Babel (TOSCA translation for model-loader)
- Event-client (DMaaP abstraction
- Champ (Graph abstraction microservice)
- Applications
- Sparky (AAI User Interface)

Source code of AAI is released under the following repositories at https://gerrit.onap.org

- aai/aai-common
- aai/event-client
- aai/babel
- aai/champ
- aai/data-router
- aai/esr-gui
- aai/esr-server
- aai/gizmo
- aai/logging-service
- aai/model-loader
- aai/resources
- aai/rest-client
- aai/router-core
- aai/search-data-service
- aai/sparky-be
- aai/sparky-fe
- aai/test-config
- aai/traversal

**Epic**

- `AAI-16 <https://lf-onap.atlassian.net/browse/AAI-16>`_ A&AI Platform Deployment

- `AAI-17 <https://lf-onap.atlassian.net/browse/AAI-17>`_ Seed code stabilization

- `AAI-21 <https://lf-onap.atlassian.net/browse/AAI-21>`_ Gizmo

- `AAI-24 <https://lf-onap.atlassian.net/browse/AAI-24>`_ Move to Active Open Source Graph Database

- `AAI-38 <https://lf-onap.atlassian.net/browse/AAI-38>`_ AAI Microservice to generate AAI model XML

- `AAI-280 <https://lf-onap.atlassian.net/browse/AAI-280>`_ This epic groups together the various requests for making AAI more configurable

- `AAI-466 <https://lf-onap.atlassian.net/browse/AAI-466>`_ Beijing R2 AAI Schema Updates

- `AAI-680 <https://lf-onap.atlassian.net/browse/AAI-680>`_ HPA Use Case Support in AAI

- `AAI-681 <https://lf-onap.atlassian.net/browse/AAI-681>`_ Change Management Use Case Support in AAI

- `AAI-682 <https://lf-onap.atlassian.net/browse/AAI-682>`_ Scale Out Use Case Support in AAI

- `AAI-769 <https://lf-onap.atlassian.net/browse/AAI-769>`_ Required updates to the v13 REST API

**Bug Fixes**

- `AAI-129 <https://lf-onap.atlassian.net/browse/AAI-129>`_ RestClientBuilder SSL protocol should be configurable

- `AAI-131 <https://lf-onap.atlassian.net/browse/AAI-131>`_ Model-Loader service of A&AI has it's Log Provider Configuration File sealed inside the WAR

- `AAI-175 <https://lf-onap.atlassian.net/browse/AAI-175>`_ aai core service of A&AI has it's Log Provider Configuration File configurable from startup.sh

- `AAI-295 <https://lf-onap.atlassian.net/browse/AAI-295>`_ ChampDAO tests failing in gizmo

- `AAI-460 <https://lf-onap.atlassian.net/browse/AAI-460>`_ vm1-aai-inst1 aai-resources fails to start

- `AAI-463 <https://lf-onap.atlassian.net/browse/AAI-463>`_ Wrong Error message when we use PUT instead of POST to create the relationship

- `AAI-521 <https://lf-onap.atlassian.net/browse/AAI-521>`_ A&AI resources container sporadically hangs on startup

- `AAI-523 <https://lf-onap.atlassian.net/browse/AAI-523>`_ Sparky UI does not display RelationshipList nodes

- `AAI-558 <https://lf-onap.atlassian.net/browse/AAI-558>`_ aai-resources java daily jenkins job is failing

- `AAI-559 <https://lf-onap.atlassian.net/browse/AAI-559>`_ CSIT jobs should use a set of streams, not a list of branches

- `AAI-561 <https://lf-onap.atlassian.net/browse/AAI-561>`_ aai-traversal java daily jenkins job is failing

- `AAI-568 <https://lf-onap.atlassian.net/browse/AAI-568>`_ aai/logging-api build fails on license.txt not found when run outside of aai/logging-service dir - for root CI builds

- `AAI-601 <https://lf-onap.atlassian.net/browse/AAI-601>`_ AAI search-data-service build failing on 1.1 JAX-RS instead of required 2.0 library only on clean Ubuntu 16.04/JDK1.8.0_151

- `AAI-603 <https://lf-onap.atlassian.net/browse/AAI-603>`_ Sonar only push to master

- `AAI-666 <https://lf-onap.atlassian.net/browse/AAI-666>`_ aai/datarouter startup fails to find logback.xml

- `AAI-679 <https://lf-onap.atlassian.net/browse/AAI-679>`_ A&AI UI failed to search service-instance based on service-instance-id

- `AAI-699 <https://lf-onap.atlassian.net/browse/AAI-699>`_ SDC Tosca does not generate Groups from resource yaml

- `AAI-738 <https://lf-onap.atlassian.net/browse/AAI-738>`_ When register service to MSB, esr-server still will register to MSB automaticly

- `AAI-788 <https://lf-onap.atlassian.net/browse/AAI-788>`_ fix the cookie decryption algorithm

- `AAI-796 <https://lf-onap.atlassian.net/browse/AAI-796>`_ AAI is logging %PARSER_ERROR instead of REMOTE_USER

- `AAI-833 <https://lf-onap.atlassian.net/browse/AAI-833>`_ The url of query vim type from multiCloud is incorrect

- `AAI-838 <https://lf-onap.atlassian.net/browse/AAI-838>`_ Add back the properties that got removed

- `AAI-874 <https://lf-onap.atlassian.net/browse/AAI-874>`_ Fix the test-config traversal aaiconfig to use proper timeout keys

- `AAI-948 <https://lf-onap.atlassian.net/browse/AAI-948>`_ aai-rest-client build fails with non-resolvable parent POM

- `AAI-961 <https://lf-onap.atlassian.net/browse/AAI-961>`_ Fix aai-sparky-be-master-aai-docker-java-daily

- `AAI-985 <https://lf-onap.atlassian.net/browse/AAI-985>`_ Sparky-be: Change dependency to make use of sparky-fe war file from Beijing version

- `AAI-987 <https://lf-onap.atlassian.net/browse/AAI-987>`_ Update ML with the latest changes

- `AAI-993 <https://lf-onap.atlassian.net/browse/AAI-993>`_ Champ docker image name incorrect

- `AAI-994 <https://lf-onap.atlassian.net/browse/AAI-994>`_ Crud-service (Gizmo) docker tag version is incorrect

- `AAI-995 <https://lf-onap.atlassian.net/browse/AAI-995>`_ Gizmo docker image name incorrect

- `AAI-996 <https://lf-onap.atlassian.net/browse/AAI-996>`_ Change ML pom file to address build failure problems

- `AAI-1005 <https://lf-onap.atlassian.net/browse/AAI-1005>`_ Fix docker-compose-db.yml in test-config

- `AAI-1006 <https://lf-onap.atlassian.net/browse/AAI-1006>`_ Babel start script does not set all required properties

- `AAI-1007 <https://lf-onap.atlassian.net/browse/AAI-1007>`_ Babel: java.lang.NoClassDefFoundError: com/att/aft/dme2/internal/gson/JsonSyntaxException

- `AAI-1016 <https://lf-onap.atlassian.net/browse/AAI-1016>`_ Model-loader: properties files are incorrectly named and have errors

- `AAI-1017 <https://lf-onap.atlassian.net/browse/AAI-1017>`_ Fix Champ build - incorrect definition of Java system path

- `AAI-1018 <https://lf-onap.atlassian.net/browse/AAI-1018>`_ Model-loader: CONF_INVALID_MSG_BUS_ADDRESS

- `AAI-1019 <https://lf-onap.atlassian.net/browse/AAI-1019>`_ aai-resources: does not require username/password after springboot upgrade

- `AAI-1020 <https://lf-onap.atlassian.net/browse/AAI-1020>`_ aai-traversal: does not require username/password after springboot upgrade

- `AAI-1024 <https://lf-onap.atlassian.net/browse/AAI-1024>`_ Test-config: model-loader MSG_BUS_ADDRESSES not set

- `AAI-1025 <https://lf-onap.atlassian.net/browse/AAI-1025>`_ Test-config: traversal updateQueryData.sh fails to update models and queries

- `AAI-1026 <https://lf-onap.atlassian.net/browse/AAI-1026>`_ test-config: model-loader is attempting 2-way TLS with AAI

- `AAI-1027 <https://lf-onap.atlassian.net/browse/AAI-1027>`_ ModelLoader basic auth failure with aai-resources

- `AAI-1029 <https://lf-onap.atlassian.net/browse/AAI-1029>`_ The DOC about ESR installation should be update

- `AAI-1034 <https://lf-onap.atlassian.net/browse/AAI-1034>`_ [sparky-be] Portal API Proxy missing from Spring Boot Sparky

- `AAI-1035 <https://lf-onap.atlassian.net/browse/AAI-1035>`_ Security: Springboot 1.5.10 has new nexusIQ critical exceptions

- `AAI-1038 <https://lf-onap.atlassian.net/browse/AAI-1038>`_ Babel missing .gitreview file

- `AAI-1049 <https://lf-onap.atlassian.net/browse/AAI-1049>`_ [Model Loader] - Remove dependency on PowerMockito

- `AAI-1051 <https://lf-onap.atlassian.net/browse/AAI-1051>`_ API Spec is specifying v12 in v13 file

- `AAI-1052 <https://lf-onap.atlassian.net/browse/AAI-1052>`_ AAI is using -SNAPSHOT artifacts; remove -SNAPSHOT dependencies

- `AAI-1077 <https://lf-onap.atlassian.net/browse/AAI-1077>`_ [Babel] master daily build job is not creating an autorelease staging repo

- `AAI-1082 <https://lf-onap.atlassian.net/browse/AAI-1082>`_ Champ janus version incompatible with Resources janus version

- `AAI-1084 <https://lf-onap.atlassian.net/browse/AAI-1084>`_ POST with PATCH override call is returning 405

- `AAI-1086 <https://lf-onap.atlassian.net/browse/AAI-1086>`_ Babel: Compressed files contain proprietary markings

- `AAI-1088 <https://lf-onap.atlassian.net/browse/AAI-1088>`_ aai-common: version.properties refers to previous patch release

- `AAI-1089 <https://lf-onap.atlassian.net/browse/AAI-1089>`_ haproxy, aai-resources, and aai-traversal using outdated certificate in HEAT config

- `AAI-1090 <https://lf-onap.atlassian.net/browse/AAI-1090>`_ v13 does not support External System under cloud region

- `AAI-1091 <https://lf-onap.atlassian.net/browse/AAI-1091>`_ ESR fails to register EMS

- `AAI-1094 <https://lf-onap.atlassian.net/browse/AAI-1094>`_ Model-loader: failure to negotiate with message router in OOM

- `AAI-1096 <https://lf-onap.atlassian.net/browse/AAI-1096>`_ Increase length for field:password in ESR-GUI VIM registration page

- `AAI-1100 <https://lf-onap.atlassian.net/browse/AAI-1100>`_ OOM Resources and Traversal Config map missing release

- `AAI-1101 <https://lf-onap.atlassian.net/browse/AAI-1101>`_ haproxy, aai-resources, and aai-traversal using outdated certificate in OOM config

- `AAI-1105 <https://lf-onap.atlassian.net/browse/AAI-1105>`_ aai-traversal job is failing when trying to start OOM

- `AAI-1106 <https://lf-onap.atlassian.net/browse/AAI-1106>`_ aai-resources: scripts do not work properly with spring-boot

- `AAI-1107 <https://lf-onap.atlassian.net/browse/AAI-1107>`_ Security: babel and m-l brings in springboot jersey starter, which includes logback 1.1.11

- `AAI-1108 <https://lf-onap.atlassian.net/browse/AAI-1108>`_ [Babel] Remove license violations in latest commit.

- `AAI-1110 <https://lf-onap.atlassian.net/browse/AAI-1110>`_ Model Loader logback.xml errors

- `AAI-1111 <https://lf-onap.atlassian.net/browse/AAI-1111>`_ Update test-config project for Babel

- `AAI-1113 <https://lf-onap.atlassian.net/browse/AAI-1113>`_ ESR VIM registration portal: Physical Location Id does not populate any data

- `AAI-1114 <https://lf-onap.atlassian.net/browse/AAI-1114>`_ Security: [Champ] add Dockerfile and remove additional AJSC files

- `AAI-1116 <https://lf-onap.atlassian.net/browse/AAI-1116>`_ [Gizmo] addressing Security vulnerabilities (Nexus IQ)

- `AAI-1117 <https://lf-onap.atlassian.net/browse/AAI-1117>`_ [Champ] addressing Security vulnerabilities (Nexus IQ)

- `AAI-1118 <https://lf-onap.atlassian.net/browse/AAI-1118>`_ [Gizmo] upgrade artefacts from aai-common to 1.2.4

- `AAI-1119 <https://lf-onap.atlassian.net/browse/AAI-1119>`_ [Champ] Prevent deployment of Champ service jar

- `AAI-1120 <https://lf-onap.atlassian.net/browse/AAI-1120>`_ [Gizmo] Fix Jacoco configuration

- `AAI-1121 <https://lf-onap.atlassian.net/browse/AAI-1121>`_ Add the default realtime clients

- `AAI-1123 <https://lf-onap.atlassian.net/browse/AAI-1123>`_ Babel logback.xml errors

- `AAI-1124 <https://lf-onap.atlassian.net/browse/AAI-1124>`_ [router-core] NexusIQ reporting httpclient 4.5 vulnerability

- `AAI-1125 <https://lf-onap.atlassian.net/browse/AAI-1125>`_ [data-router] NexusIQ reporting httpclient 4.5 vulnerability

- `AAI-1126 <https://lf-onap.atlassian.net/browse/AAI-1126>`_ [Babel] Authorisation mechanism is not functioning

- `AAI-1127 <https://lf-onap.atlassian.net/browse/AAI-1127>`_ [sparky-be] doesn't release artifacts because it is missing the staging plugin

- `AAI-1132 <https://lf-onap.atlassian.net/browse/AAI-1132>`_ AAI's OOM server certificate doesn't include all k8 names

- `AAI-1133 <https://lf-onap.atlassian.net/browse/AAI-1133>`_ AAI's haproxy server config doesn't include all k8 names

- `AAI-1134 <https://lf-onap.atlassian.net/browse/AAI-1134>`_ OOF not defined in AAI realm properties files

- `AAI-1135 <https://lf-onap.atlassian.net/browse/AAI-1135>`_ [traversal] closed loop named-query is missing property-collect-list

- `AAI-1136 <https://lf-onap.atlassian.net/browse/AAI-1136>`_ Babel doesnt start in HEAT due to log directory permissions

- `AAI-1138 <https://lf-onap.atlassian.net/browse/AAI-1138>`_ [Champ] Bump to 1.2.1-SNAPSHOT and 1.2.1 in version.properties

- `AAI-1139 <https://lf-onap.atlassian.net/browse/AAI-1139>`_ [resources and traversal] do not release artifacts properly

- `AAI-1141 <https://lf-onap.atlassian.net/browse/AAI-1141>`_ [champ] duplicate dependency in pom.xml

- `AAI-1142 <https://lf-onap.atlassian.net/browse/AAI-1142>`_ [champ] doesn't create release artifacts

- `AAI-1143 <https://lf-onap.atlassian.net/browse/AAI-1143>`_ [resources] createDbSchema.sh tries to add -SNAPSHOT version to classpath

- `AAI-1144 <https://lf-onap.atlassian.net/browse/AAI-1144>`_ [oom and test-config] robot-ete is missing from realtime clients list

- `AAI-1146 <https://lf-onap.atlassian.net/browse/AAI-1146>`_ [champ] daily build job is failing

- `AAI-1148 <https://lf-onap.atlassian.net/browse/AAI-1148>`_ [Model-Loader] Rollback of VNF Images fails

- `AAI-1151 <https://lf-onap.atlassian.net/browse/AAI-1151>`_ [Champ & Gizmo] Fix JJB jenkins jobs

- `AAI-1153 <https://lf-onap.atlassian.net/browse/AAI-1153>`_ [Champ] Bump to 1.2.2-SNAPSHOT and 1.2.2 in version.properties

**Known Issues**

If the either the aai-resources or aai-traversal pod is deleted, haproxy will not automatically detect when the pod is re-instantiated.  As a temporary workaround, you can delete the haproxy pod (the one named "aai", for example, "dev-aai-8794fbff5-clx7d") and when the aai pod restarts the service should operate normally. A proposed fix is `here <https://gerrit.onap.org/r/c/oom/+/51075/1>`_ if you want to see how to configure the haproxy service to auto-recover when the IP address of either the aai-resources or aai-traversal pod changes.

**Security Notes**

AAI code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The AAI open Critical security vulnerabilities and their risk assessment have been documented as part of the `project <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=25441383>`_.

Quick Links:

- `AAI project page <https://lf-onap.atlassian.net/wiki/spaces/DW/pages/16230663/Active+and+Available+Inventory+Project>`_
- `Passing Badge information for AAI <https://bestpractices.coreinfrastructure.org/en/projects/1591>`_
- `R2 Project Vulnerability Review Table for AAI <https://lf-onap.atlassian.net/pages/viewpage.action?pageId=25441383>`_

Version: 1.1.1
--------------

:Release Date: 2018-01-18

**Bug Fixes**

- `AAI-456 <https://lf-onap.atlassian.net/browse/AAI-456>`_ AAI named-query for policy not returning extra-properties

- `AAI-458 <https://lf-onap.atlassian.net/browse/AAI-458>`_ [aai] ML, Search, DR, and Sparky Jenkins jobs not creating autorelease repo

- `AAI-459 <https://lf-onap.atlassian.net/browse/AAI-459>`_ aai-common child pom still depends on openecomp artifacts

- `AAI-461 <https://lf-onap.atlassian.net/browse/AAI-461>`_ AAI mS configuration files are using old openecomp params in test-config

- `AAI-462 <https://lf-onap.atlassian.net/browse/AAI-462>`_ Fix the resources junit tests broken in windows environment

- `AAI-558 <https://lf-onap.atlassian.net/browse/AAI-558>`_ aai-resources java daily jenkins job is failing

- `AAI-561 <https://lf-onap.atlassian.net/browse/AAI-561>`_ aai-traversal java daily jenkins job is failing

- `AAI-566 <https://lf-onap.atlassian.net/browse/AAI-566>`_ AAI Eclipse build failure - aai-traversal pom as hardcoded 1.8.0_101 jdk.tools version

- `AAI-621 <https://lf-onap.atlassian.net/browse/AAI-621>`_ Update the snapshot in test-config for v1.1.1-SNAPSHOT

Version: 1.1.0
--------------

:Release Date: 2017-11-16

**New Features**

Initial release of Active and Available Inventory (AAI) for Open Network Automation Platform (ONAP).  AAI provides ONAP with its logically centralized view of inventory data, taking in updates from orchestrators, controllers, and assurance systems.  AAI provides core REST services.

ONAP AAI R1 includes the following components:

- AAI Data Management
- Resources (CRUD operations)
- Traversal (Advanced search operations)
- Data Router (Route/persist AAI event data for consumption by the UI)
- Model Loader (Obtains SDC artifacts and loads them into the A&AI Resources service for storage)
- Search Data Service (Abstraction layer for searchengine, supporting queries and updates)
- Applications
- Sparky (AAI User Interface)

Source code of AAI is released under the following repositories at https://gerrit.onap.org .

- aai/aai-common
- aai/aai-config
- aai/aai-data
- aai/aai-service
- aai/babel
- aai/champ
- aai/data-router
- aai/esr-gui
- aai/esr-server
- aai/gizmo
- aai/logging-service
- aai/model-loader
- aai/resources
- aai/rest-client
- aai/router-core
- aai/search-data-service
- aai/sparky-be
- aai/sparky-fe
- aai/test-config
- aai/traversal

**Epic**

- `AAI-17 <https://lf-onap.atlassian.net/browse/AAI-17>`_ Seed code stabilization
- `AAI-20 <https://lf-onap.atlassian.net/browse/AAI-20>`_ Champ Library
- `AAI-22 <https://lf-onap.atlassian.net/browse/AAI-22>`_ Amsterdam User Case Schema Updates
- `AAI-23 <https://lf-onap.atlassian.net/browse/AAI-23>`_ Model Loader Support for R1
- `AAI-58 <https://lf-onap.atlassian.net/browse/AAI-58>`_ Define and build functional test cases for CSIT
- `AAI-72 <https://lf-onap.atlassian.net/browse/AAI-72>`_ External System Register
- `AAI-254 <https://lf-onap.atlassian.net/browse/AAI-254>`_ Documentation of REST APIs, dev guides, onboarding, etc.
- `AAI-280 <https://lf-onap.atlassian.net/browse/AAI-280>`_ Confguration enhancements

**Bug Fixes**

- `AAI-11 <https://lf-onap.atlassian.net/browse/AAI-11>`_ robot_vm: demo.sh failing - '200' does not match '^(201|412)$' on vanilla openstack

- `AAI-13 <https://lf-onap.atlassian.net/browse/AAI-13>`_ VM_init is failing to get sparky

- `AAI-31 <https://lf-onap.atlassian.net/browse/AAI-31>`_ Compilation failure in aai-traversal

- `AAI-48 <https://lf-onap.atlassian.net/browse/AAI-48>`_ AAI Common REST Client returns an error on a 204 (No Content) server response

- `AAI-49 <https://lf-onap.atlassian.net/browse/AAI-49>`_ Health check is failing in DFW 1.1 RS. Connection refused

- `AAI-62 <https://lf-onap.atlassian.net/browse/AAI-62>`_ Search Data Service should not implicitly create indexes on document write

- `AAI-63 <https://lf-onap.atlassian.net/browse/AAI-63>`_ Data Router must handle Search Service document create failures if index does not exit

- `AAI-73 <https://lf-onap.atlassian.net/browse/AAI-73>`_ Sparky sync issues

- `AAI-76 <https://lf-onap.atlassian.net/browse/AAI-76>`_ Jenkins stage-site builds failing on resources and traversal

- `AAI-94 <https://lf-onap.atlassian.net/browse/AAI-94>`_ AAI Certificate will expire 30 Nov 2017 - fyi

- `AAI-146 <https://lf-onap.atlassian.net/browse/AAI-146>`_ Both esr-server and esr-gui Jenkins failed

- `AAI-192 <https://lf-onap.atlassian.net/browse/AAI-192>`_ Model Loader depends on httpclient version 4.4.1

- `AAI-205 <https://lf-onap.atlassian.net/browse/AAI-205>`_ Having an invalid xml namespace for v11, named-query api returns 500 error, model query return incorrect error message

- `AAI-206 <https://lf-onap.atlassian.net/browse/AAI-206>`_ Model based delete is failing

- `AAI-217 <https://lf-onap.atlassian.net/browse/AAI-217>`_ Remove internal references from A&AI seed code

- `AAI-222 <https://lf-onap.atlassian.net/browse/AAI-222>`_ the version property of esr-server is incorrect

- `AAI-224 <https://lf-onap.atlassian.net/browse/AAI-224>`_ aai/esr-gui daily build failed

- `AAI-225 <https://lf-onap.atlassian.net/browse/AAI-225>`_ aai/esr-server daily build failed

- `AAI-265 <https://lf-onap.atlassian.net/browse/AAI-265>`_ EdgePropertyMap throws NullPointer if edge rule does not include property

- `AAI-266 <https://lf-onap.atlassian.net/browse/AAI-266>`_ auth-info edge rule does not include contains-other-v

- `AAI-273 <https://lf-onap.atlassian.net/browse/AAI-273>`_ Fix the esr-server setup error issue

- `AAI-278 <https://lf-onap.atlassian.net/browse/AAI-278>`_ AAI throws exception about mismatch keys adding esr-system-info to cloud-region

- `AAI-293 <https://lf-onap.atlassian.net/browse/AAI-293>`_ Jenkins job failing for aai-sparky-fe-master-release-version-java-daily

- `AAI-377 <https://lf-onap.atlassian.net/browse/AAI-377>`_ esr-gui docker build failed

- `AAI-393 <https://lf-onap.atlassian.net/browse/AAI-393>`_ The jjb defiend in a error way that cause CSIT build failed.

- `AAI-398 <https://lf-onap.atlassian.net/browse/AAI-398>`_ If a cloud-region didn't contain a external system info, there will be an null pointer error

- `AAI-400 <https://lf-onap.atlassian.net/browse/AAI-400>`_ Register ServiceTest to microservice

- `AAI-401 <https://lf-onap.atlassian.net/browse/AAI-401>`_ Remove DMaaP router duplication

- `AAI-407 <https://lf-onap.atlassian.net/browse/AAI-407>`_ There is an error to startup esr-gui docker

- `AAI-412 <https://lf-onap.atlassian.net/browse/AAI-412>`_ Replace the type specification in this constructor call with the diamond operator ("<>")

- `AAI-417 <https://lf-onap.atlassian.net/browse/AAI-417>`_ Rackspace 20170928 fails to authenticate nexus3 on 10003 during *_init.sh* (sdnc for example)

- `AAI-420 <https://lf-onap.atlassian.net/browse/AAI-420>`_ Can not get the MSB address in esr-server

- `AAI-422 <https://lf-onap.atlassian.net/browse/AAI-422>`_ The esr-server csit failed

- `AAI-424 <https://lf-onap.atlassian.net/browse/AAI-424>`_ The integration catalog is not in use, should be removed

- `AAI-425 <https://lf-onap.atlassian.net/browse/AAI-425>`_ Fix the artifact of esr-gui

- `AAI-426 <https://lf-onap.atlassian.net/browse/AAI-426>`_ Fix the artifact of esr-server

- `AAI-431 <https://lf-onap.atlassian.net/browse/AAI-431>`_ esr-gui files did not contained in webapp of tomcat

- `AAI-433 <https://lf-onap.atlassian.net/browse/AAI-433>`_ Failed to pre-load vCPE data to AAI. No response from AAI

- `AAI-434 <https://lf-onap.atlassian.net/browse/AAI-434>`_ Can not visit ESR portal with demo deployment

- `AAI-435 <https://lf-onap.atlassian.net/browse/AAI-435>`_ default tenant need be input to A&AI while register VIM

- `AAI-436 <https://lf-onap.atlassian.net/browse/AAI-436>`_ Call the API from MultiCloud failed

- `AAI-440 <https://lf-onap.atlassian.net/browse/AAI-440>`_ The version input box should be changed in a more easy to use when register a VIM

- `AAI-441 <https://lf-onap.atlassian.net/browse/AAI-441>`_ Can not input the vendor and version information to EMS, but there is a default data for the two parameter

- `AAI-442 <https://lf-onap.atlassian.net/browse/AAI-442>`_ Can't instantiate a service

- `AAI-444 <https://lf-onap.atlassian.net/browse/AAI-444>`_ Cannot associate multiple service-instances to PNFs

- `AAI-446 <https://lf-onap.atlassian.net/browse/AAI-446>`_ vnf to esr-system-info named-query is missing vnfc

- `AAI-448 <https://lf-onap.atlassian.net/browse/AAI-448>`_ Remove snapshot dependencies from aai-common, data-router, and rest-client

- `AAI-450 <https://lf-onap.atlassian.net/browse/AAI-450>`_ Named Query needs to be updated to return VNFC Info

- `AAI-453 <https://lf-onap.atlassian.net/browse/AAI-453>`_ Fix stage-site jenkins job for aai-common

- `AAI-454 <https://lf-onap.atlassian.net/browse/AAI-454>`_ LoggingContext.requestId required NULL handling in aai/aai-common (20170607) - during demo.sh init_customer

**Known Issues**

- `AAI-61 <https://lf-onap.atlassian.net/browse/AAI-61>`_ AAI cleaned up references to OpenECOMP but in order to keep the release stable for R1, the XML namespace still contains openecomp.

**Security Issues**

See Common Vulnerabilities and Exposures `CVE <https://cve.mitre.org>`

ONAP docker images and repos include demo TLS server certificates that are signed by a demo Certificate Authority. DO NOT use the demo certificates in a production environment.

AAI uses HTTPS Basic Authentication.

**Upgrade Notes**

This is an initial release

**Deprecation Notes**

AAI Amsterdam provides support for legacy versions of the API, v8 and v11 in this release.  v11 is the latest and preferred version.

**Other**

===========

End of Release Notes
