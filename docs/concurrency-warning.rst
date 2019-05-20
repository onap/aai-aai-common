.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Warning!
========

AAI REST API Not Safe With Concurrent Access (i.e. more than 1 REST client)

Problem Overview
++++++++++++++++

The REST API of the CRUD webservice A&AI Resources is not consistent under concurrent access to the same entity (calls using the same ID).

If multiple requests modify the same entity the result is undetermined. The worst case behaviour observed is when concurrent access produces duplicates of that entity - the result is that no further REST queries (GET/POST/DELETE ...) to this entity are possible and the entity becomes inaccessible. Afterwards only a manual intervention in the database can restore proper service behaviour. Access the data concurrently at your own risk.

It is recommended that at all times there is only 1 client calling the A&AI REST services.

Technical aspects of the problem
++++++++++++++++++++++++++++++++

It is unclear what causes this problem. The most probable cause is that the code calling `JanusGraph <http://janusgraph.org/>`__ library is buggy. In order to guarantee data consistency special precautions have to made described here `Chapter 31. Eventually-Consistent Storage Backends. <https://docs.janusgraph.org/latest/eventual-consistency.html>`__ Unfortunately the JanusGraph features that ensure data consistency are not used in the current A&AI code.

Path forward
++++++++++++

There are 4 solutions possible (subjectively sorted from worst to best)

1. Recommend using max. 1 A&AI REST client.

   - PROS: the current state - no effort needed, works "most of the time".
   - CONS: not what would be expected of a carrier-grade system by any rational measure

2. Having batch jobs scan the database and remove duplicates and corrupted data created due to inconsistencies (this is being used now as problem mitigation)

   - PROS: you don't have to deal with the real problem, it is easy because you only target symptoms
   - CONS: solution does not work because if data gets corrupted between batch sweeps then the data is unavailable until the batch job runs again. Also adds accidental complexity with batch jobs and their timing.

3. Not write data in A&AI Resources directly but use the `Champ <https://gerrit.onap.org/r/#/admin/projects/aai/champ>`__ service (Note: in the future architecturally the Champ project should be the only one accessing the JanusGraph database and A&AI Resources would only forward entity change requests to Champ)

   - PROS: Access to JanusGraph database can be properly implemented in Champ without of fear of breaking existing functionality
   - CONS: Champ seems like a dead initiative and is not going to be finished in the next few years. Correct me if this assessment of Champ is wrong, for example with a concrete finish date.

4. Correct the root cause of the inconsistency

   - PROS: Webservice would work as expected
   - CONS: Changes to core A&AI libraries are needed, potential to break functionality or trigger software regressions. Deep A&AI expertize around data handling needed


