# AAI-Common

## Introduction
`AAI-Common` is a collection of common utility modules used by the other AAI components (`AAI-Resources` and `AAI-Traversal`). These utilities include `aai-schema`, which contains the schema oxm and xsd files; `aai-annotations`, which enables the annotations on the schema files; and `aai-core`, which includes various java packages used by all AAI microservices. `AAI-Resources` and `AAI-Traversal` are already configured to pull these dependencies using maven. For more information on `AAI-Resources` and `AAI-Traversal`, please see the `README.md` files in their respective repositories. This readme only covers AAI-Common.

## Getting started

### Prerequisites

The AAI services have some prerequisite requirements that must be met before being able to compile and test the services.
As with any other ONAP service, you need the [ONAP `settings.xml` file](https://git.onap.org/oparent/plain/settings.xml) in your `~/.m2/` folder.

In addition, the AAI services are still based on Java 8. As such, you can either

- globally define java 8 to be the standard jdk for the system (i.e `echo -e '\nexport JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/' >> ~/.bashrc`)
- define it locally for each command (i.e `JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/ mvn install -DskipTests`)
- configure it in your IDE

### Install
A `mvn install` will build all modules make them locally available on your system:
```sh
mvn install -Dcheckstyle.skip
```

## Test
Run all tests
```sh
mvn test -Dcheckstyle.skip
```
Run a test class
```sh
mvn test -Dcheckstyle.skip -DfailIfNoTests=false -Dtest=PserverTest
```
```sh
cd aai-core/
mvn test -Dcheckstyle.skip -Dtest=PserverTest
```

## Docker build
```sh
mvn clean install -P docker -Dcheckstyle.skip -DskipTests
```

## Debugging
If your IDE supports it, then use the built-in way to debug run a single test. Should that not be possible, you can attach your IDE to the debug port opened by maven via:
```sh
mvn test -Dcheckstyle.skip -Dtest=PserverTest -Dmaven.surefire.debug
```
This will open up a debug connection on port `5005`.T
The process of connecting to this port is IDE-specific.
For VSCode for example you have to create a `.vscode/launch.json` in the project with the following content:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug (attach)",
      "request": "attach",
      "hostName": "localhost",
      "port": 5005,
      "internalConsoleOptions": "neverOpen",
      "projectName": "aai-core"
    }
  ]
}
```