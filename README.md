# Mozo
Dependencies management

## Introduction
This prototype is based on a thin client and a cloud service (middleware) for resolving and locating all the dependencies of Java modules. The service analyze modules descriptors and, from that information, dynamically locates all the dependencies (other modules) required to compile.



Fig. 1 – Basic architecture.

With this technology, the development environment (client) is decoupled from the repositories. The middleware search for the modules, not the client. It also resolves transitives dependencies and their locations.

## Technology
Since Java 9 (Java Module System), each module must have a mandatory descriptor (module-info file), so, this avoids the need of adding an external descriptor, such as Maven, Ivy y Gradle requires. Analizing the "requires" attribute of the descriptor, is enough to know the dependencies of each module:



Fig. 2 - Relation between modules in a module-info descriptor.

The service algorithm iterates the references to modules until reaching the closure or the level of depth established
in the configuration. To survey the dependencies of each module, first, the descriptor file is extracted remotely, and then, it is decompiled with the javap program (part of the JDK). See in the following diagram the dynamic interaction between the client (development environment), the middleware and the repositories:



Fig. 3 - Dynamic representation.

This service extracts compressed files from remote repositories with the [RemoteZip] subproject. In order to optimize the response time to locate modules, the service extracts portions of bytes from servers that implements RFC 2616. With this feature, only the portion of bytes that represents the module descriptor is transferred from the repositories to the middleware.

## Summary
With this technology, the service receives high-level dependency resolution requests (modules) from a thin client and, after "visiting" the repositories in search for all the required modules, it returns a list of paths to those dependencies (JSON). The middleware extracts the descriptors, analyzes its dependencies recursively, until the module tree is completed.
Finally, with the dependencies tree and its paths, the client begins the transfer from the repositories.

### Conceptual advantages
* It is not an invasive solution, that is to say, it does not require adding metadata to the modules.
* The resolution of the closure does not depend on the list of sources (repositories) defined in the descriptor file of the project/module. It is the intermediary who knows these sources. In this way, the software project is decoupled from the repositories. 
* The intermediary can, transparently for the client, incorporate more sophisticated algorithms, such as cognitive computing tools.

### Architecture advantages
* The client always executes the latest version.
* The paths to the modules are always verified.
* The response time is acceptable, as only the descriptor file is transferred from the module to the intermediary.
* Concentrating the solution on a cloud service allows permanent extensions and corrections without the need to update the client.
* It would allow the user to easily create a web interface to download dependencies.


## Appendix

### Components
The middleware is a cloud service with 2 client communication modules (Port and Apollo), another one that communicates with the entities (Model), common use utilities (Tools), the remote file extractor (RemoteZip) and the Mozo module, as manager or resources.


Fig. 6 – Mozo components.

### Performance comparison
The prototype was tested by comparing its performance to Maven, Gradle and Ivy. The test case consisted of solving and downloading all the dependencies of the Quickstart class for [GeoTools], for which the corresponding descriptors were added to the 60 jars that integrate the closure, in order to have comparable dataset.  The result measured in time is as follows:



Fig. 7 – Response time (less is better).


### Use cases

#### Example 1
In this case, a statistics system requires the following modules to compile and execute: *com.stats.cli*, *com.stats.core*, *com.google.guava*, *org.apache.math* and *org.apache.rng* (Fig. 4).


Fig. 4 - Module *com.stats.cli* and it dependencies.

To download this module with all the dependencies, the actions that the user must execute are:

1. Download the client **Mozo.class** file from:

http://trimatek.org/mozo/Mozo.class

2. Run it from the command interface with:

`$> java –cp . Mozo`

3. Within the Mozo command prompt, the user enters the **find-modules** command:

`mozo> find-modules com.stats.cli`

Then, it will display the hierarchical tree of the complete closing of dependencies by console.

4. Lastly, the user enters the **download-modules** command:

`mozo> download-modules res0`

Resulting in:


Fig. 5 - Console output during module download.

Now, the user has the target module (com.stats.cli) and all its dependencies in his local environment.


#### Example 2
To resolve and download the 60 modules required to compile and run Quickstart, the user must start the Mozo client (Mozo.class) and enter:

`fm org.geotools.gt_shapefile,org.geotools.gt_swing` (fm is the abbreviated version of find-modules) as shown in Fig. 8:

[RemoteZip]:https://github.com/martinaguero/remotezip
[GeoTools]:http://docs.geotools.org/latest/userguide/build/install/jdk.html


