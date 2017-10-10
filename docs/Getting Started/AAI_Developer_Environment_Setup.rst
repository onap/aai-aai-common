.. contents::
   :depth: 3
.. _dev-setup:


A&AI Developer Environment Setup
================================

This guide will illustrate setting up an A&AI development environment in
Ubuntu 16.04.  

For this exercise, I set up a new instance of Ubuntu in Virtualbox and
gave it 16G RAM, 200GB dynamically allocated storage, and 3 processors.

1. install openjdk 8

   1. sudo apt install openjdk-8-jdk

2. Install single node hadoop/titan

   1. $ wget
      http://s3.thinkaurelius.com/downloads/titan/titan-1.0.0-hadoop1.zip

   2. $ unzip titan-1.0.0-hadoop1.zip

   3. $ cd titan-1.0.0-hadoop1

   4. $ sudo ./bin/titan.sh start

3. Install haproxy

   1. $ sudo apt-get -y install haproxy

   2. | $ haproxy -v
      | HA-Proxy version 1.6.3 2015/12/25
      | Copyright 2000-2015 Willy Tarreau
        <willy@`haproxy.org <http://haproxy.org/>`__>

   3. Install this haproxy.cfg file in /etc/haproxy

    `haproxy.cfg <file:///C:\download\attachments\10782088\haproxy.cfg%3fversion=2&modificationDate=1501018863000&api=v2>`__

1. $ sudo cp aai.pem /etc/ssl/private/aai.pem

2. $ sudo chmod 640 /etc/ssl/private/aai.pem

3. | $ sudo chown root:ssl-cert /etc/ssl/private/aai.pem
   | `aai.pem <file:///C:\download\attachments\10782088\aai.pem%3fversion=1&modificationDate=1501019585000&api=v2>`__

4. sudo mkdir /usr/local/etc/haproxy

5. Add these hostnames to the loopback interface in /etc/hosts: 

   1. 127.0.0.1 localhost
      `aai-traversal.api.simpledemo.openecomp.org <http://aai-traversal.api.simpledemo.openecomp.org>`__
      aai-resour\ `ces.api.simpledemo.openecomp.or <http://ces.api.simpledemo.openecomp.org>`__\ g

6. $ sudo service haproxy restart

1. Set up repos. First, follow the initial setup instructions
   in \ `Setting Up Your Development
   Environment <file:///C:\display\DW\Setting+Up+Your+Development+Environment>`__

   1. $ mkdir -p ~/LF/AAI

   2. $ cd ~/LF/AAI

   3. $ git clone
      ssh://%3Cusername%3E@gerrit.onap.org:29418/aai/aai-common

   4. $ git
      clone \ `ssh://<username>@gerrit.onap.org:29418/aai/traversal <ssh://%253Cusername%253E@gerrit.onap.org:29418/aai/traversal>`__

   5. $ git
      clone \ `ssh://<username>@gerrit.onap.org:29418/aai/resources <ssh://%25253Cusername%25253E@gerrit.onap.org:29418/aai/resources>`__

   6. $ git clone
      ssh://%3Cusername%3E@gerrit.onap.org:29418/aai/logging-service

   7. If you did not originally create a settings.xml file when setting
      up the dev environment, you may get an error on some of the repos
      saying that oparent is unresolvable.  Using the example
      settings.xml file should solve this problem: \ `Setting Up Your
      Development
      Environment#MavenExamplesettings.xml <file:///C:\display\DW\Setting+Up+Your+Development+Environment#SettingUpYourDevelopmentEnvironment-MavenExamplesettings.xml>`__

2. Build aai-common, traversal, and resources

   1.  $ cd ~/LF/AAI/aai-common

   2.  | $ mvn clean install
       | Should result in BUILD SUCCESS

   3.  $ cd ~/LF/AAI/resources

   4.  | $ mvn clean install
       | Should result in BUILD SUCCESS

   5.  $ cd ~/LF/AAI/logging-service

   6.  | $ mvn clean install
       | Should result in BUILD SUCCESS

   7.  | $ cd ~/LF/AAI/traversal
       | I had to add the following to traversal/pom.xml to get
         traversal to build: 

   8.  <repositories>

   9.  <repository>

   10. <id>maven-restlet</id>

   11. <name>Restlet repository</name>

   12. <url>https://maven.restlet.com</url>

   13. </repository>

    </repositories>

1. | mvn clean install
   | Should result in BUILD SUCCESS

1. Titan setup

   1. | Modify both titan-cached.properties and
        titan-realtime.properties to the following (for all MS’s that
        will connect to the local Cassandra backend)
      | storage.backend=\ *cassandra*
      | storage.hostname=\ *localhost*

   2. update
      ~/LF/AAI/resources/aai-resources/bundleconfig-local/etc/appprops/titan-cached.properties

   3. update
      ~/LF/AAI/resources/aai-resources/bundleconfig-local/etc/appprops/titan-realtime.properties

   4. update
      ~/LF/AAI/traversal/aai-traversal/bundleconfig-local/etc/appprops/titan-cached.properties

   5. update
      ~/LF/AAI/traversal/aai-traversal/bundleconfig-local/etc/appprops/titan-realtime.properties

   6. | The following property can be added to specify the keyspace
        name, each time you do this step (g) should be done. If not
        specified Titan will try to create/use a defaulted keyspace
        named titan.
      | storage.cassandra.keyspace=<keyspace name>

   7. From the resources MS run the create db schema standalone program.

   8. ***NOTE***: The first thing that would need to be done is adding
      the schema to the local instance. (this will need to be done
      whenever using a new keyspace or after wiping the data).

    Runnable class com.att.aai.dbgen.GenTester with the following vm
    args.

                    -DAJSC\_HOME=~/LF/AAI/resources
    -DBUNDLECONFIG\_DIR="bundleconfig-local"

1. | Here's the command I used, and it worked:
   | $ cd ~/LF/AAI; java
     -DAJSC\_HOME=/home/jimmy/LF/AAI/resources/aai-resources
     -DBUNDLECONFIG\_DIR="bundleconfig-local" -cp
     aai-common/aai-core/target/aai-core-1.1.0-SNAPSHOT.jar:resources/aai-resources/target/aai-resources.jar:resources/aai-resources/target/userjars/\*
     org.openecomp.aai.dbgen.GenTester

1. Start the "resources" microservice

   1. | Resources runs on port 8446.  Go to the resources directory
      | $ cd ~/LF/AAI/resources

   2. | Set the debug port to 9446
      | $ export MAVEN\_OPTS="-Xms1024m -Xmx5120m -XX:PermSize=2024m
        -Xdebug -Xnoagent -Djava.compiler=NONE
        -Xrunjdwp:transport=dt\_socket,address=9446,server=y,suspend=n"

   3. | Start the microservice
      | $ mvn -P runAjsc

2. Verify the resources microservice (this example uses Postman utility
   for Google Chrome)

   1. Use basic auth, user = AAI, pw = AAI

   2. Set the X-TransactionId header (in the example below, the value is
      9999)

   3. Set the X-FromAppId header (in the example below, the value is
      jimmy-postman)

   4. Perform a GET of https://127.0.0.1:8443/aai/v11/network/vces

   5. You should see an error as below, 404 Not Found, ERR.5.4.6114. 
      This indicates that the service is functioning normally:

+------------------------------------------+
| |C:\\9cb03b5a507d917b3f460df1c1d95eea|   |
+------------------------------------------+

1. 
2. Start the "traversal" microservice

   1. | Traversal runs on port 8447.  Go to the traversal directory
      | $ cd ~/LF/AAI/traversal

   2. | Set the debug port to 9447
      | $ export MAVEN\_OPTS="-Xms1024m -Xmx5120m -XX:PermSize=2024m
        -Xdebug -Xnoagent -Djava.compiler=NONE
        -Xrunjdwp:transport=dt\_socket,address=9447,server=y,suspend=n"

   3. | Start the microservice
      | $ mvn -P runAjsc 
      | Should see something like this: 2017-07-26
        12:46:35.524:INFO:oejs.Server:com.att.ajsc.runner.Runner.main():
        Started @25827ms

3. Verify the traversal microservice

   1. | Set up the widget models
      | This will set up the postman to add widget models: \ `Add Widget
        Models.postman\_collection.json <file:///C:\download\attachments\10782088\Add%20Widget%20Models.postman_collection.json%3fversion=2&modificationDate=1501102559000&api=v2>`__\ `NamedQuery.postman\_collection.json <file:///C:\download\attachments\10782088\NamedQuery.postman_collection.json%3fversion=2&modificationDate=1501102582000&api=v2>`__

   2. Create a runner using this
      file: \ `models.csv <file:///C:\download\attachments\10782088\models.csv%3fversion=1&modificationDate=1501100140000&api=v2>`__

   3. | Run the test runner
      | |C:\\de01805e8408f48478705feb59a27e02|

   4. | Add a named query called "getComponentList" (this named query is
        used by
        VID): \ `NamedQuery.postman\_collection.json <file:///C:\download\attachments\10782088\NamedQuery.postman_collection.json%3fversion=2&modificationDate=1501102582000&api=v2>`__
      | |C:\\5cdb29c4d0655cf5ede2011736938e58|

   5. Add objects: \ `Add Instances for Named
      Query.postman\_collection.json <file:///C:\download\attachments\10782088\Add%20Instances%20for%20Named%20Query.postman_collection.json%3fversion=1&modificationDate=1501102617000&api=v2>`__ (replacing
      the xmlns "http://org.openecomp.aai.inventory/v11" with
      "http://org.onap.aai.inventory/v11" in the Body of the PUT
      request)

   6. | Execute named-query: \ `Execute Named
        Query.postman\_collection.json <file:///C:\download\attachments\10782088\Execute%20Named%20Query.postman_collection.json%3fversion=1&modificationDate=1501102658000&api=v2>`__
      | You should see something like the following:
      | |C:\\a67954cfbcfebb8d7a7f48bba2a26195|

4. Your A&AI instance is now running, both the resources and traversal
   microservices are working properly with a local titan graph. 

5. Next: \ `Tutorial: Making and Testing a Schema Change in
   A&AI <file:///C:\pages\viewpage.action%3fpageId=10783023>`__

.. |C:\\9cb03b5a507d917b3f460df1c1d95eea| image:: media/image1.png
   :width: 4.87500in
   :height: 2.87500in
.. |C:\\de01805e8408f48478705feb59a27e02| image:: media/image2.tmp
   :width: 4.87500in
   :height: 3.75000in
.. |C:\\5cdb29c4d0655cf5ede2011736938e58| image:: media/image3.png
   :width: 4.87500in
   :height: 4.15000in
.. |C:\\a67954cfbcfebb8d7a7f48bba2a26195| image:: media/image4.png
   :width: 4.87500in
   :height: 4.15000in
