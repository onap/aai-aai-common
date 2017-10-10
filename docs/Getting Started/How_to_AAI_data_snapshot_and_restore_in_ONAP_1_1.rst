.. contents::
   :depth: 3
..

How to A&AI data snapshot and restore in ONAP 1.1
=================================================

1. Check the containers that are running and get the container name for
   resources micro service as this container contains both dataSnapshot
   and restore scripts

2. If the deployment was followed properly, then the container name
   would have been
   testconfig\_aai-resources.api.simpledemo.openecomp.org\_1

3. Run the following command to save the current snapshot of your data:

    docker exec -u aaiadmin -it
    testconfig\_aai-resources.api.simpledemo.openecomp.org\_1
    /opt/app/aai-resources/bin/dataSnapshot.sh

1. After running that command, you should see the following line in the
   standard output:

    Snapshot written to
    /opt/app/aai-resources/logs/data/dataSnapshots/dataSnapshot.graphSON.201709221713

1. This file dataSnapshot.graphSON.201709221713 can be found in
   /opt/aai/logroot/AAI-RESOURCES/data/dataSnapshots on the host vm

2. If you want to restore the data from the snapshot, then you would run
   this command:

    docker exec -u aaiadmin -it
    testconfig\_aai-resources.api.simpledemo.openecomp.org\_1
    /opt/app/aai-resources/bin/dataRestoreFromSnapshot.sh
    dataSnapshot.graphSON.201709221713

1. The argument to the dataRestoreFromSnapshot.sh is the dataSnapshot
   graphson file and it only needs the base name and expects to be found
   in the host vm /opt/aai/logroot/AAI-RESOURCES/data/dataSnapshots.

2. Once that command is run, you will be given a warning of 5 seconds to
   quit the process as it will replace whatever you have in your A&AI
   data with that snapshot and any current data thats not saved into a
   snapshot will be lost.
