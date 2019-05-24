
.. contents::
   :depth: 3
.. _dev-setup:


AAI Developer Environment Setup
================================

For this exercise, I set up a new instance of Ubuntu in Virtualbox and gave it 16G RAM, 200GB dynamically allocated storage, and 3 processors.

1. Install openjdk 8
--------------------
 .. code-block:: bash

   1. $ sudo apt install openjdk-8-jdk

2. Install single node hadoop/janusgraph
----------------------------------------
 .. code-block:: bash

   1. $ wget http://github.com/JanusGraph/janusgraph/releases/download/v0.2.0/janusgraph-0.2.0-hadoop2.zip
   2. $ unzip janusgraph-0.2.0-hadoop2.zip
   3. $ cd janusgraph-0.2.0-hadoop2/
   4. $ ./bin/janusgraph.sh start

 Ensure you are not a root user as elasticsearch cannot be run as root.
 Response looks like:

 .. code-block:: bash

   Forking Cassandra...
   Running `nodetool statusthrift`... OK (returned exit status 0 and printed string "running").
   Forking Elasticsearch...
   Connecting to Elasticsearch (127.0.0.1:9200)...... OK (connected to 127.0.0.1:9200).
   Forking Gremlin-Server... 
   Connecting to Gremlin-Server (127.0.0.1:8182).... OK (connected to 127.0.0.1:8182).
   Run gremlin.sh to connect.

 You can verify whether everything is running by executing

 .. code-block:: bash

   ./bin/janusgraph.sh status

 And the output looks like:

 .. code-block:: bash

   Gremlin-Server (org.apache.tinkerpop.gremlin.server.GremlinServer) is running with pid 9835
   Elasticsearch (org.elasticsearch.bootstrap.Elasticsearch) is running with pid 9567
   Cassandra (org.apache.cassandra.service.CassandraDaemon) is running with pid 9207

3. Install haproxy
------------------

 .. code-block:: bash

    1. $ sudo apt-get -y install haproxy
    2. $ <path-to-haproxy>/haproxy -v

 Response should be:       

 .. code-block:: bash

   HA-Proxy version 1.6.3 2015/12/25
   Copyright 2000-2015 Willy Tarreau <willy@haproxy.org>

 Install the attached :download:`haproxy.cfg <media/haproxy.cfg>` in /etc/haproxy

 .. code-block:: bash

   $ sudo cp haproxy.cfg /etc/haproxy
   $ sudo mkdir /usr/local/etc/haproxy

 Install the attached :download:`aai.pem <media/aai.pem>` file in /etc/ssl/private

 .. code-block:: bash

   $ sudo cp aai.pem /etc/ssl/private/aai.pem
   $ sudo chmod 640 /etc/ssl/private/aai.pem
   $ sudo chown root:ssl-cert /etc/ssl/private/aai.pem 

 Add these hostnames to the loopback interface in /etc/hosts: 

 127.0.0.1 localhost aai-traversal.api.simpledemo.openecomp.org aai-resources.api.simpledemo.openecomp.org aai-traversal.api.simpledemo.onap.org aai-resources.api.simpledemo.onap.org

 .. code-block:: bash

   $ sudo service haproxy restart

4. Follow the initial setup instructions in `Setting Up Your Development Environment <https://wiki.onap.org/display/DW/Setting+Up+Your+Development+Environment>`__ e.g.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
 .. code-block:: bash

   $ sudo apt-get install git
   $ sudo apt-get install npm
   $ sudo apt-get install maven
   $ sudo apt-get install docker.io
   $ wget https://git.onap.org/oparent/plain/settings.xml
   $ mkdir ~/.m2
   $ cp settings.xml ~/.m2

 If you get an error on some of the repos saying that oparent is
 unresolvable, using the example settings.xml file should solve this
 problem: `Setting Up Your Development
 Environment#MavenExamplesettings.xml
 <https://wiki.onap.org/display/DW/Setting+Up+Your+Development+Environment#SettingUpYourDevelopmentEnvironment-MavenExamplesettings.xml>`__

5. Set Up Repos
---------------

 .. code-block:: bash

   $ mkdir -p ~/src/aai
   $ cd ~/src/aai ; for f in aai-common schema-service resources traversal graphadmin logging-service ; do git clone ssh://<username>@gerrit.onap.org:29418/aai/$f; done

6. Checkout the 'dublin' branch
-------------------------------

 .. code-block:: bash

    $ cd ~/src/aai ; for f in aai-common schema-service resources traversal graphadmin logging-service ; do (cd $f ; git checkout dublin) done | tee checkoutlog.txt

7. Janus Setup (part 1)
-----------------------

 Modify both janus-cached.properties and janus-realtime.properties to the following (for all MS’s that will connect to the local Cassandra backend)

 .. code:: 
   
   storage.backend=cassandra
   storage.hostname=localhost
   storage.cassandra.keyspace=onap # or different keyspace name of your choosing

 Edit the following files:

 .. code::

   ~/src/aai/resources/aai-resources/src/main/resources/etc/appprops/janusgraph-cached.properties
   ~/src/aai/resources/aai-resources/src/main/resources/etc/appprops/janusgraph-realtime.properties
   ~/src/aai/traversal/aai-traversal/src/main/resources/etc/appprops/janusgraph-cached.properties
   ~/src/aai/traversal/aai-traversal/src/main/resources/etc/appprops/janusgraph-realtime.properties
   ~/src/aai/graphadmin/src/main/resources/etc/appprops/janusgraph-cached.properties
   ~/src/aai/graphadmin/src/main/resources/etc/appprops/janusgraph-realtime.properties

8. Build all the modules
------------------------

 .. code-block:: bash

   $ cd ~/src/aai ; for f in aai-common schema-service resources traversal graphadmin logging-service ; do (cd $f ; mvn versions:set -DnewVersion=0.0.1-TEST-SNAPSHOT && mvn -DskipTests clean install -Daai.schema.version=0.0.1-TEST-SNAPSHOT) done | tee log.txt 2>&1

   $ grep -e "SUCCESS" -e "FAILURE" log.txt

 And you should see:

 .. code::

   [INFO] aai-schema ......................................... SUCCESS [ 32.504 s]
   [INFO] aai-queries ........................................ SUCCESS [ 6.461 s]
   [INFO] aai-schema-service ................................. SUCCESS [02:17 min]
   [INFO] BUILD SUCCESS
   [INFO] aai-resources ...................................... SUCCESS [ 1.190 s]
   [INFO] BUILD SUCCESS
   [INFO] aai-resources ...................................... SUCCESS [ 3.210 s]
   [INFO] aai-resources ...................................... SUCCESS [ 41.213 s]
   [INFO] BUILD SUCCESS
   [INFO] aai-traversal ...................................... SUCCESS [ 1.090 s]
   [INFO] BUILD SUCCESS
   [INFO] aai-traversal ...................................... SUCCESS [ 3.181 s]
   [INFO] aai-traversal ...................................... SUCCESS [ 58.001 s]
   [INFO] BUILD SUCCESS
   [INFO] BUILD SUCCESS
   [INFO] BUILD SUCCESS
   [INFO] aai-logging-service ................................ SUCCESS [ 1.101 s]
   [INFO] BUILD SUCCESS
   [INFO] aai-logging-service ................................ SUCCESS [ 5.230 s]
   [INFO] Common Logging API ................................. SUCCESS [ 1.995 s]
   [INFO] EELF Logging Implementation ........................ SUCCESS [ 4.235 s]
   [INFO] Common Logging Distribution ........................ SUCCESS [ 0.530 s]
   [INFO] BUILD SUCCESS

9. Janus setup (part 2)
-----------------------
   
 Run this on the local instance on your first time running AAI and whenever using new keyspace or after wiping the data.

 Install the schema

 .. code-block:: bash

    $ (cd ~/src/aai/graphadmin/ && mvn -PrunAjsc -Dstart-class=org.onap.aai.schema.GenTester -Daai.schema.version=0.0.1-TEST-SNAPSHOT -Daai.schema.ingest.version=0.0.1-TEST-SNAPSHOT -DskipTests -Dcheckstyle.skip=true -DAJSC_HOME=$HOME/src/aai/graphadmin -DBUNDLECONFIG_DIR=src/main/resources)

 You should see:

 .. code:: 

   ---- NOTE --- about to open graph (takes a little while)--------;
   -- Loading new schema elements into JanusGraph --
   -- graph commit
   -- graph shutdown

10. Start the "resources" microservice
--------------------------------------

 Resources runs on port 8447.  Go to the resources directory

 .. code-block:: bash

    $ cd ~/src/aai/resources

 Set the debug port to 9447

  .. code-block:: bash

     $ export MAVEN_OPTS="-Xms1024m -Xmx5120m -XX:PermSize=2024m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9447,server=y,suspend=n"

 Start the microservice - adjust your build version accordingly

 .. code-block::

     $ mvn -pl aai-resources -PrunAjsc -Daai.schema.version=0.0.1-TEST-SNAPSHOT -Daai.schema.ingest.version=0.0.1-TEST-SNAPSHOT -DskipTests -Dcheckstyle.skip=true

 Should see something like this: Resources Microservice Started


11. Verify the resources microservice
-------------------------------------

 This example uses curl from commandline

 .. code-block:: 

   $ sudo apt-get install jq  # for pretty output

 Download :download:`script - test-complex <media/test-complex>`
 Download :download:`data - data-complex.json <media/data-complex.json>`

 .. code-block:: 

   $ sh ./test-complex 2>&1 | tee log.txt

 Confirm log.txt contains:

 .. code-block:: 

    > GET /aai/v16/cloud-infrastructure/complexes HTTP/1.1

 .. code-block:: json

    {
     "requestError": {
       "serviceException": {
         "messageId": "SVC3001",
         "text": "Resource not found for %1 using id %2 (msg=%3) (ec=%4)",
         "variables": [
           "GET",
           "cloud-infrastructure/complexes",
           "Node Not Found:No Node of type complex found at: cloud-infrastructure/complexes",
           "ERR.5.4.6114"
         ]
       }
     }
    }

 Then followed by:

 .. code-block::

    > PUT /aai/v16/cloud-infrastructure/complexes/complex/clli2 HTTP/1.1
    > GET /aai/v16/cloud-infrastructure/complexes/complex/clli2 HTTP/1.1

 With payload: 

 .. code-block:: json
  
   {
     "physical-location-id": "clli2",
     "data-center-code": "example-data-center-code-val-6667",
     "complex-name": "clli2",
     "identity-url": "example-identity-url-val-28399",
     "resource-version": "1543408364646",
     "physical-location-type": "example-physical-location-type-val-28399",
     "street1": "example-street1-val-28399",
     "street2": "example-street2-val-28399",
     "city": "example-city-val-28399",
     "state": "example-state-val-28399",
     "postal-code": "example-postal-code-val-28399",
     "country": "example-country-val-28399",
     "region": "example-region-val-28399",
     "latitude": "1111",
     "longitude": "2222",
     "elevation": "example-elevation-val-28399",
     "lata": "example-lata-val-28399"
   }
   
 And finishes with:

 .. code-block::

    > DELETE /aai/v16/cloud-infrastructure/complexes/complex/clli2?resource-version=1543408364646 HTTP/1.1
    > GET /aai/v16/cloud-infrastructure/complexes HTTP/1.1

 With the following:
  
 .. code-block:: json

     {
     "requestError": {
       "serviceException": {
         "messageId": "SVC3001",
         "text": "Resource not found for %1 using id %2 (msg=%3) (ec=%4)",
         "variables": [
           "GET",
           "cloud-infrastructure/complexes",
           "Node Not Found:No Node of type complex found at: cloud-infrastructure/complexes",
           "ERR.5.4.6114"
         ]
       }
     }
   }	  

12. Start the "traversal" microservice
--------------------------------------    

 Traversal runs on port 8446.  Go to the traversal directory

 .. code-block:: bash

    $ cd ~/src/aai/traversal

 Set the debug port to 9446
 
    $ export MAVEN_OPTS="-Xms1024m -Xmx5120m -XX:PermSize=2024m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9446,server=y,suspend=n"

  Start the microservice - adjust your build version accordingly

  .. code-block:: bash

     $ mvn -pl aai-traversal -PrunAjsc -Daai.schema.version=0.0.1-TEST-SNAPSHOT -Daai.schema.ingest.version=0.0.1-TEST-SNAPSHOT -DskipTests -Dcheckstyle.skip=true

  Should see something like this: Traversal Microservice Started

