.. This work is licensed under a Creative Commons Attribution 4.0 International License.

A&AI Data Restore for OOM
-------------------------

In order to restore data using the graphSON snapshot, here are the steps to follow:

Check the snapshots folder in the graphadmin pod running in your namespace:

   .. code-block:: bash

      kubectl exec -it $(kubectl get pods -lapp=aai-graphadmin -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}' | head -1) -n onap -- ls -ltr /opt/app/aai-graphadmin/logs/data/dataSnapshots/

You should see something like below:

   .. code-block:: bash

      -rw-r--r-- 1 aaiadmin aaiadmin 1353242 Nov 14 21:45 dataSnapshot.graphSON.201811142145

Choose the latest snapshot that you want to restore the database with and copy the snapshot outside of the graphadmin container for safe storage as after you do dataRestore, you will need to kill all the pods as the dataRestore drops the keyspace and creates the keyspace so resource, traversal and graphadmin cannot automatically recover

   .. code-block:: bash

      kubectl cp onap/$(kubectl get pods -lapp=aai-graphadmin -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}' | head -1):/opt/app/aai-graphadmin/logs/data/dataSnapshots/dataSnapshot.graphSON.201811142145 /tmp/dataSnapshot.graphSON.201811142145

Run the following command to connect to the cassandra cluster and do a dataRestore from an earlier graphSON format

   .. code-block:: bash

      kubectl exec -it $(kubectl get pods -lapp=aai-graphadmin -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}' | head -1) -n onap gosu aaiadmin /opt/app/aai-graphadmin/scripts/dataRestoreFromSnapshot.sh dataSnapshot.graphSON.201811142145

You should see some logs being printed and at the end it will give you info on the number of vertices in the graph when successfully restored.

Afterwards, kill the resources, traversal and graphadmin pod using the following command:

   .. code-block:: bash

      kubectl delete pod $(kubectl get pods -lapp=aai-resources -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}') -n onap
      kubectl delete pod $(kubectl get pods -lapp=aai-traversal -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}') -n onap
      kubectl delete pod $(kubectl get pods -lapp=aai-graphadmin -n onap  --template '{{range .items}}{{.metadata.name}}{{"\n"}}{{end}}') -n onap




