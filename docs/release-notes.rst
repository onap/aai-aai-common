.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. Copyright 2017 AT&T Intellectual Property.  All rights reserved.


AAI Release Notes
==================		   

Version: 1.1.1
--------------

:Release Date: 2018-01-18

**Bug Fixes**

`AAI-456 <https://jira.onap.org/browse/AAI-456>`_
AAI named-query for policy not returning extra-properties

`AAI-458 <https://jira.onap.org/browse/AAI-458>`_
[aai] ML, Search, DR, and Sparky Jenkins jobs not creating autorelease repo

`AAI-459 <https://jira.onap.org/browse/AAI-459>`_
aai-common child pom still depends on openecomp artifacts

`AAI-461 <https://jira.onap.org/browse/AAI-461>`_
AAI mS configuration files are using old openecomp params in test-config

`AAI-462 <https://jira.onap.org/browse/AAI-462>`_
Fix the resources junit tests broken in windows environment

`AAI-558 <https://jira.onap.org/browse/AAI-558>`_
aai-resources java daily jenkins job is failing

`AAI-561 <https://jira.onap.org/browse/AAI-561>`_
aai-traversal java daily jenkins job is failing

`AAI-566 <https://jira.onap.org/browse/AAI-566>`_
AAI Eclipse build failure - aai-traversal pom as hardcoded 1.8.0_101 jdk.tools version

`AAI-621 <https://jira.onap.org/browse/AAI-621>`_
Update the snapshot in test-config for v1.1.1-SNAPSHOT
	       
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
- Search Data SErvice (Abstraction layer for searchengine, supporting queries and updates)
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

- `AAI-17 <https://jira.onap.org/browse/AAI-17>`_ Seed code stabilization
- `AAI-20 <https://jira.onap.org/browse/AAI-20>`_ Champ Library
- `AAI-22 <https://jira.onap.org/browse/AAI-22>`_ Amsterdam User Case Schema Updates
- `AAI-23 <https://jira.onap.org/browse/AAI-23>`_ Model Loader Support for R1
- `AAI-58 <https://jira.onap.org/browse/AAI-58>`_ Define and build functional test cases for CSIT
- `AAI-72 <https://jira.onap.org/browse/AAI-72>`_ External System Register
- `AAI-254 <https://jira.onap.org/browse/AAI-254>`_ Documentation of REST APIs, dev guides, onboarding, etc.
- `AAI-280 <https://jira.onap.org/browse/AAI-280>`_ Confguration enhancements

**Bug Fixes**

`AAI-11 <https://jira.onap.org/browse/AAI-11>`_
robot_vm: demo.sh failing - '200' does not match '^(201|412)$' on vanilla openstack

`AAI-13 <https://jira.onap.org/browse/AAI-13>`_
VM_init is failing to get sparky

`AAI-31 <https://jira.onap.org/browse/AAI-31>`_
Compilation failure in aai-traversal

`AAI-48 <https://jira.onap.org/browse/AAI-48>`_
AAI Common REST Client returns an error on a 204 (No Content) server response

`AAI-49 <https://jira.onap.org/browse/AAI-49>`_
Health check is failing in DFW 1.1 RS. Connection refused

`AAI-62 <https://jira.onap.org/browse/AAI-62>`_
Search Data Service should not implicitly create indexes on document write

`AAI-63 <https://jira.onap.org/browse/AAI-63>`_
Data Router must handle Search Service document create failures if index does not exit

`AAI-73 <https://jira.onap.org/browse/AAI-73>`_
Sparky sync issues

`AAI-76 <https://jira.onap.org/browse/AAI-76>`_
Jenkins stage-site builds failing on resources and traversal

`AAI-94 <https://jira.onap.org/browse/AAI-94>`_
AAI Certificate will expire 30 Nov 2017 - fyi

`AAI-146 <https://jira.onap.org/browse/AAI-146>`_
Both esr-server and esr-gui Jenkins failed

`AAI-192 <https://jira.onap.org/browse/AAI-192>`_
Model Loader depends on httpclient version 4.4.1

`AAI-205 <https://jira.onap.org/browse/AAI-205>`_
Having an invalid xml namespace for v11, named-query api returns 500 error, model query return incorrect error message

`AAI-206 <https://jira.onap.org/browse/AAI-206>`_
Model based delete is failing

`AAI-217 <https://jira.onap.org/browse/AAI-217>`_
Remove internal references from A&AI seed code

`AAI-222 <https://jira.onap.org/browse/AAI-222>`_
the version property of esr-server is incorrect

`AAI-224 <https://jira.onap.org/browse/AAI-224>`_
aai/esr-gui daily build failed

`AAI-225 <https://jira.onap.org/browse/AAI-225>`_
aai/esr-server daily build failed

`AAI-265 <https://jira.onap.org/browse/AAI-265>`_
EdgePropertyMap throws NullPointer if edge rule does not include property

`AAI-266 <https://jira.onap.org/browse/AAI-266>`_
auth-info edge rule does not include contains-other-v

`AAI-273 <https://jira.onap.org/browse/AAI-273>`_
Fix the esr-server setup error issue

`AAI-278 <https://jira.onap.org/browse/AAI-278>`_
AAI throws exception about mismatch keys adding esr-system-info to cloud-region

`AAI-293 <https://jira.onap.org/browse/AAI-293>`_
Jenkins job failing for aai-sparky-fe-master-release-version-java-daily

`AAI-377 <https://jira.onap.org/browse/AAI-377>`_
esr-gui docker build failed

`AAI-393 <https://jira.onap.org/browse/AAI-393>`_
The jjb defiend in a error way that cause CSIT build failed.

`AAI-398 <https://jira.onap.org/browse/AAI-398>`_
If a cloud-region didn't contain a external system info, there will be an null pointer error

`AAI-400 <https://jira.onap.org/browse/AAI-400>`_
Register ServiceTest to microservice

`AAI-401 <https://jira.onap.org/browse/AAI-401>`_
Remove DMaaP router duplication

`AAI-407 <https://jira.onap.org/browse/AAI-407>`_
There is an error to startup esr-gui docker

`AAI-412 <https://jira.onap.org/browse/AAI-412>`_
Replace the type specification in this constructor call with the diamond operator ("<>")

`AAI-417 <https://jira.onap.org/browse/AAI-417>`_
Rackspace 20170928 fails to authenticate nexus3 on 10003 during *_init.sh (sdnc for example)

`AAI-420 <https://jira.onap.org/browse/AAI-420>`_
Can not get the MSB address in esr-server

`AAI-422 <https://jira.onap.org/browse/AAI-422>`_
The esr-server csit failed

`AAI-424 <https://jira.onap.org/browse/AAI-424>`_
The integration catalog is not in use, should be removed

`AAI-425 <https://jira.onap.org/browse/AAI-425>`_
Fix the artifact of esr-gui

`AAI-426 <https://jira.onap.org/browse/AAI-426>`_
Fix the artifact of esr-server

`AAI-431 <https://jira.onap.org/browse/AAI-431>`_
esr-gui files did not contained in webapp of tomcat

`AAI-433 <https://jira.onap.org/browse/AAI-433>`_
Failed to pre-load vCPE data to AAI. No response from AAI

`AAI-434 <https://jira.onap.org/browse/AAI-434>`_
Can not visit ESR portal with demo deployment

`AAI-435 <https://jira.onap.org/browse/AAI-435>`_
default tenant need be input to A&AI while register VIM

`AAI-436 <https://jira.onap.org/browse/AAI-436>`_
Call the API from MultiCloud failed

`AAI-440 <https://jira.onap.org/browse/AAI-440>`_
The version input box should be changed in a more easy to use when register a VIM

`AAI-441 <https://jira.onap.org/browse/AAI-441>`_
Can not input the vendor and version information to EMS, but there is a default data for the two parameter

`AAI-442 <https://jira.onap.org/browse/AAI-442>`_
Can't instantiate a service

`AAI-444 <https://jira.onap.org/browse/AAI-444>`_
Cannot associate multiple service-instances to PNFs

`AAI-446 <https://jira.onap.org/browse/AAI-446>`_
vnf to esr-system-info named-query is missing vnfc

`AAI-448 <https://jira.onap.org/browse/AAI-448>`_
Remove snapshot dependencies from aai-common, data-router, and rest-client

`AAI-450 <https://jira.onap.org/browse/AAI-450>`_
Named Query needs to be updated to return VNFC Info

`AAI-453 <https://jira.onap.org/browse/AAI-453>`_
Fix stage-site jenkins job for aai-common

`AAI-454 <https://jira.onap.org/browse/AAI-454>`_
LoggingContext.requestId required NULL handling in aai/aai-common (20170607) - during demo.sh init_customer

**Known Issues**

- `AAI-61 <https://jira.onap.org/browse/AAI-61>`_ AAI cleaned up references to OpenECOMP but in order to keep the release stable for R1, the XML namespace still contains openecomp.
	  
Client systems should use http://org.openecomp.aai.inventory/v11 as the XML namespace for ONAP AAI R1.

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
