.. contents::
   :depth: 3
..
.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

AAI Recents API
===============

Recents API Overview
--------------------

The Recents API will allow a client to get the list of objects that
has been created or updated recently, up to a maximum of 1 week back.
This API is accessed through the AAI Rest Interface, utilizing the
same certificates and headers.

Request
-------

The API can be accessed by using sending a GET request to the Recents
API and sending a single node-type and a parameter to specify either
timestamp to start the search or the number of hours to look back.
 
Querying with number of hours

.. code::

   GET /aai/recents/v$/{node-type}?hours={hours}

Querying with an epoch timestamp

.. code::

   GET /aai/recents/v$/{node-type}?date-time={timestamp}

   Example:
   GET /aai/recents/v$/pnf?date-time=1531413113815

Response
--------

The Recents API sends a response in a new format, which includes the
object type, URI, and resource-version.

Sample response:

.. code-block:: json

  {
   "results": [
     {
       "resource-type": "pnf",
       "resource-link": "/aai/v16/network/pnfs/pnf/lab20105v"
       "resource-version": "1531413113815"
     },
     {
       "resource-type": "pnf",
       "resource-link": "/aai/v16/network/pnfs/pnf/stack01"
       "resource-version": "1531413113612"
     },...]
  
 }
