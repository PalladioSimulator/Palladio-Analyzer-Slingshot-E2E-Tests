# Palladio-Analyzer-Slingshot-Default-Extensions

This repository contains the default simulation behavior extensions for the Slingshot project. 
The extensions are added as submodules to this repository. 
Each extension is a submodule that points to the corresponding extension repository.

All the extensions are simulation behavior extensions that enrich the simulator behaviorally, 
i.e., they are not models that can also be used outside the scope of Slingshot, e.g., SPD; they have their own evolution process.

In order for the default extensions to run successfully the dependent projects have to be fetched and installed first:

- Palladio, 5.2
- SPD, 1.0.0
- Slingshot Core, 1.0.0

In case you are using maven then the above dependencies have to be installed in your local maven repository first.

In case you are using Eclipse then the above dependencies have to be imported as projects in your workspace or already be installed.

This repository facilitates the build and test of the default extensions. 
While each of the contained extension is developed in its own repository, the default extensions are built and tested together in this repository.

The current list of default extensions is:
- PCM-Core, that realizes the interpretation of the PCM model
- SPD Interpreter, that realizes the interpretation of the SPD model
- Monitoring, that realizes the interpretation of the monitoring models
 

## How to build and test the default extensions

### Prerequisites

- Java 17
- Maven 3.9

### Build

Building the default extensions builds also the extensions that are added as submodules.
Furthermore, the build process runs the end-to-end tests for the default extensions. 

```bash
mvn clean install
```

### Adding a new extension 

All extensions that (also in the future) want to be part of the default extensions build and test pipeline have to be added as submodules and their parent in pom.xml should point to the default-extensions parent:

```<parent>
<groupId>org.palladiosimulator.analyzer.slingshot</groupId>
<artifactId>default-extensions</artifactId>
<version>0.0.2-SNAPSHOT</version>
<relativePath>../pom.xml</relativePath>
</parent>
```