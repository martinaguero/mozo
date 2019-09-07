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
