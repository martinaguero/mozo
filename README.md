# Mozo
Dependencies management

## Summary
This solution is based on a thin client and a cloud service for resolving and locating all the dependencies required by the devepment environment. Basically, its functionality consists in analyzing the descriptors of Java modules and, from that information, solving and locating all the dependencies required to compile.



Fig. 1 – General architecture of the system.

With this technology, the development environment is decoupled from the repositories, being the middleware the entity that determines the location of the modules. This solution proposes that the client only sends the requests of direct dependencies to the cloud service to resolve closing and location.

## Technology
Since Java 9 version (Java Module System) each module will be embedded by a mandatory (module-info), thus avoiding the need of adding an external descriptor, such as the ones used Maven, Ivy y Gradle. To analyze the ‘requires’ attribute is enough to know the dependencies of each module:



Fig. 2 - Relation between modules defined in the module-info descriptor.

A system is presented where an algorithm iterates the references to modules until reaching the closing or the level of depth defined in configuration. To know the dependencies of each module, first the descriptor file is extracted and then it is compiled with the javap program (that is part of the JDK). See in the following diagram the dynamic interaction between the client (development environment), the intermediary and the repositories where the intermediary locates the jar files of each module:



Fig. 3 - Dynamic representation of the system.

The JSON file has the paths to all the modules. This prototype uses a software component to extract compressed files from remote repositories. In order to optimize the response time to locate the modules, an algorithm that extracts portions of bytes from servers that implement RFC2616 was adapted to Java: https://www.codeproject.com/Articles/8688/Extracting-files-from-a-remote-ZIP-archive. In this way, only the portion of bytes contained in the module descriptor is transferred from the repositories to the intermediary. 

## Conclusions
With this technology, the intermediary receives dependency resolution requests (modules) from a command-line client (CLI) and, after "visiting" the repositories in search for the required modules, it returns a list of paths to those dependencies in JSON format. In this version, there is no local repository of modules or classes as the intermediary only extracts the descriptors, analyzes its dependencies and recursively until the construction of the module tree is completed.
Finally, with the dependencies tree and its routes, the client requests all the required modules to the respective repositories.

### Conceptual advantages
* It is not an invasive solution, that is to say, it does not require adding metadata to the modules.
* The resolution of the closing does not depend on the list of sources (repositories) defined in the descriptor file of the project/module. It is the intermediary who knows these sources. In this way, the software project is decoupled from the repositories. 
* The intermediary can, transparently for the client, incorporate more sophisticated algorithms, such as cognitive computing tools.

### Architecture advantages
* The client always executes the latest version.
* The paths to the modules are always verified.
* The response time is acceptable, as only the descriptor file is transferred from the module to the intermediary.
* Concentrating the solution on a cloud service allows permanent extensions and corrections without the need to update the client.
* It would allow the user to easily create a web interface to download dependencies.


# Appendix

## Example 1
In this case, a statistics system requires the following modules to compile and execute: *com.stats.cli*, *com.stats.core*, *com.google.guava*, *org.apache.math* and *org.apache.rng* (Fig. 4).


Fig. 4 - Module *com.stats.cli* and dependencies.

To download this module with all it dependencies, the actions that the user must execute are:

1. Download the client Mozo.class file from:

http://trimatek.org/mozo/Mozo.class

2. Run it from the command interface with:

`$>java –cp . Mozo`

3. Within the command console (prompt), the user enters:

`mozo>find-modules com.stats.cli`

Then, it will display the hierarchical tree of the complete closing of dependencies by console.

4. Lastly, the user enters the command:

`mozo>download-modules res0`

Resulting in:


Fig. 5 - Console output during module download.

Now, the user has the target module (com.stats.cli) and all its dependencies in his local environment.

## Architecture of the Middleware
The middleware is a cloud service consisting of 2 client communication modules (Port and Apollo), another one that communicates with the entities (Model), one that concentrates common use utilities (Tools), the remote file extractor (RemoteZip) and the Mozo module, serving as the coordinator.


Fig. 6 – Mozo service components.

## Performance Comparison
The prototype was evaluated by comparing its performance to Maven, Gradle y Ivy. The test case consisted of solving and downloading the class Quickstart dependencies of GeoTools: http://docs.geotools.org/latest/userguide/build/install/jdk.html for which the corresponding descriptors were added to the 60 jars that integrate the closure, so that this proposal can be compared with current technologies.  The result measured in time is as follows:



Fig. 7 – Response time (less is better).


## Example 2
To resolve and download the 60 modules necessary to run Quickstart, the user must start the Mozo client (Mozo.class) and enter:
fm org.geotools.gt_shapefile,org.geotools.gt_swing (fm is the abbreviated version of find-modules) as shown in Fig. 8:





