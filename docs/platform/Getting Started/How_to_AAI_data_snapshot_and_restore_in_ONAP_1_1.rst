.. contents::
   :depth: 3
..

How to create a snapshot and restore AAI data in ONAP 1.1
==========================================================

1. Check the containers that are running and get the container name for the resources microservice, as this container contains both the data snapshot and restore scripts.

2. If the deployment was followed properly, then the container name will be:

   .. code-block:: bash

    testconfig\_aai-resources.api.simpledemo.openecomp.org\_1

3. Run the following command to save the current snapshot of your data:

   .. code-block:: bash
   
    docker exec -u aaiadmin -it
    testconfig\_aai-resources.api.simpledemo.openecomp.org\_1
    /opt/app/aai-resources/bin/dataSnapshot.sh

4. After running that command, you should see the following line in the standard output:

   .. code:: bash

    Snapshot written to
    /opt/app/aai-resources/logs/data/dataSnapshots/dataSnapshot.graphSON.201709221713

The snapshot location
---------------------

The file dataSnapshot.graphSON.201709221713 can be found in **/opt/aai/logroot/AAI-RESOURCES/data/dataSnapshots** on the host vm.

Restoring data form the snapshot
--------------------------------

1. To restore the data from the snapshot, run this command:

   .. code-block:: bash

    docker exec -u aaiadmin -it
    testconfig\_aai-resources.api.simpledemo.openecomp.org\_1
    /opt/app/aai-resources/bin/dataRestoreFromSnapshot.sh
    dataSnapshot.graphSON.201709221713

   The argument to the dataRestoreFromSnapshot.sh is the dataSnapshot graphson file and it only needs the base name and expects to be found in the host vm **/opt/aai/logroot/AAI-RESOURCES/data/dataSnapshots**.

2. Once the command has run, you will be given a warning of 5 seconds to quit the process, as it will replace whatever you have in your AAI data with the snapshot and **any current data that is not saved into a snapshot will be lost**.
