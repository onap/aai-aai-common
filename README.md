# AAI-Common

## Introduction
`AAI-Common` is a collection of common utility modules used by the other AAI components (`AAI-Resources` and `AAI-Traversal`). These utilities include `aai-schema`, which contains the schema oxm and xsd files; `aai-annotations`, which enables the annotations on the schema files; and `aai-core`, which includes various java packages used by all AAI microservices. `AAI-Resources` and `AAI-Traversal` are already configured to pull these dependencies using maven. For more information on `AAI-Resources` and `AAI-Traversal`, please see the `README.md` files in their respective repositories. This readme only covers AAI-Common.

## Compiling AAI-Common
Each module of AAI-Common can be compiled using
``` bash
mvn clean install -DskipTests
```
To compile all of them at once, run this command at the top level of `aai-common`; to do so for a specific module, run it in that module's subdirectory. Integration tests are started by omitting the skipTests flag `mvn clean install`. Again, this can be done for all the submodules at once or for any one individually. 

## Logging
EELF framework is used for **specific logs** (audit, metric and error logs). They are tracking inter component logs (request and response) and allow to follow a complete flow through the AAI subsystem

Each microservice (AAI-Resources and AAI-Traversal) keeps its own logging directories. Please see their specific readmes for more information.

## Testing AAI-Common Functionalities
There are JUnit tests for aai-core and aai-annotations. Changes to the schema must be tested in the context of the AAI-Resources microservice via the REST interface. Please see the AAI-Resources readme for details on how to test via the REST API.


