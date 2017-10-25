.. contents::
   :depth: 3
..

How to Docker setup on Single VM
================================

Step-by-step guide
------------------

1.  You would need to have root access to the vm

2.  sudo su - root

3.  cd /opt

4.  git clone http://gerrit.onap.org/r/aai/test-config

5.  cd test-config

6.  In deploy\_vm1.sh comment out the lines 89-94 which should be an if
    statement checking for the /opt/message-router folder

7.  Create a directory called /opt/config

    1. mkdir /opt/config/

8.  Create a file called /opt/config/nexus\_username.txt containing the
    text: docker

    1. echo "docker" > /opt/config/nexus\_username.txt

9.  Create a file called /opt/config/nexus\_password.txt containing the
    text: docker

    1. echo "docker" > /opt/config/nexus\_password.txt

10. Create a file called /opt/config/dmaap\_topic.txt containing the
    text: AAI-EVENT

    1. echo " AAI-EVENT" > /opt/config/dmaap\_topic.txt

11. Create a file called /opt/config/nexus\_docker\_repo.txt containing
    text: nexus3.onap.org:10001

    echo "nexus3.onap.org:10001" > /opt/config/nexus\_docker\_repo.txt

1. Create a file called /opt/config/docker\_version.txt containing text:
   1.1-STAGING-latest

    echo "1.1-STAGING-latest" > /opt/config/docker\_version.txt

1. Please note that in the previous step, docker version is currently
   1.1-STAGING-latest and this will be changed later

    ./deploy\_vm2.sh && ./deploy\_vm1.sh
