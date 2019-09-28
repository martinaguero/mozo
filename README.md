# Mozo 0.3
Java modules dependencies management

### Table of contents  
- [Introduction](https://github.com/martinaguero/mozo/blob/master/README.md#introduction)  
- [Technology](https://github.com/martinaguero/mozo/blob/master/README.md#technology)
- [Summary](https://github.com/martinaguero/mozo/blob/master/README.md#summary)
  - [Conceptual advantages](https://github.com/martinaguero/mozo/blob/master/README.md#conceptual-advantages)
  - [Architectural advantages](https://github.com/martinaguero/mozo/blob/master/README.md#architectural-advantages)
- [Appendix](https://github.com/martinaguero/mozo/blob/master/README.md#appendix)
  - [Service](https://github.com/martinaguero/mozo/blob/master/README.md#service)
  - [Performance comparison](https://github.com/martinaguero/mozo/blob/master/README.md#performance-comparison)
  - [Use cases](https://github.com/martinaguero/mozo/blob/master/README.md#use-cases)
    - [Example 1](https://github.com/martinaguero/mozo/blob/master/README.md#example-1)
    - [Example 2](https://github.com/martinaguero/mozo/blob/master/README.md#example-2)

## Introduction
This prototype is based on a thin client and a cloud service (middleware) for solving and locating the dependencies of Java modules. The service analyzes module descriptors and dynamically locates all the dependencies (other modules) required to compile.

![Fig1](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig1.png)<br />
<sub>Fig. 1 – Basic architecture.</sub>

With this technology, the development environment (client) is decoupled from the repositories. The middleware search for the modules, not the client. It also resolves transitive dependencies.

## Technology
Since Java 9 (Java Module System), each module must have a mandatory descriptor (module-info file), so this avoids the need of adding an external descriptor, such as Maven, Ivy and Gradle requirements. Analyzing the "requires" attribute of the descriptor is enough to know the dependencies of each module:

![Fig2](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig2.png)<br />
<sub>Fig. 2 – Relation between modules in a module-info descriptor.</sub>

The service algorithm iterates the references to modules until reaching the closure or the level of depth established
in the configuration. To survey the dependencies of each module, first, the descriptor file is extracted remotely, and then, it is decompiled with the javap program (part of the JDK). See in the following diagram the dynamic interaction between the client (development environment), the middleware and the repositories:

![Fig3](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig3.png)<br />
<sub>Fig. 3 – Modules resolution workflow.</sub>

This service extracts compressed files from remote repositories with the [RemoteZip] subproject. In order to optimize the response time to locate modules, the service extracts portions of bytes from servers that implement Partial Content (RFC 2616). With this feature, only the portion of bytes that represents the module descriptor is transferred from the repositories to the middleware.

## Summary
With this technology, the service receives high-level dependency resolution requests (modules) from a thin client and, after "visiting" the repositories in search for all the required modules, it returns a list of paths to those dependencies (JSON). The middleware extracts the descriptors and analyzes its dependencies recursively, until the module tree is completed.
Finally, with the dependencies tree and its paths, the client begins the transfer from the repositories.

### Conceptual advantages
* It is not an invasive solution; it does not require adding metadata to the modules.
* The closure does not depend on the list of sources (repositories) in the descriptor file of the project/module. It is the middleware that knows these sources; the software project is decoupled from the repositories. 
* The middleware can, transparently for the client, incorporate more sophisticated algorithms, such as cognitive computing tools.

### Architectural advantages
* The client always executes the latest version.
* The paths to the modules are always verified.
* The response time is acceptable, as only the descriptor file is transferred from the module to the intermediary.
* Concentrating the solution on a cloud service allows permanent enhancements without the need to update the client.
* It would allow easily adding a web user interface for training new programmers.
    
## Appendix

### Service
The middleware is a cloud service with two main communication modules (Port and Apollo), another one with the entities (Model), common use utilities (Tools), the remote file extractor (RemoteZip) and the Mozo module, as a manager of resources:

![Fig4](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig4.png)<br />
<sub>Fig. 4 – Mozo components.</sub>

### Performance comparison
The prototype was tested by comparing its performance to Maven, Gradle and Ivy. The test case consisted of solving and downloading all the dependencies of the Quickstart class for [GeoTools], for which the corresponding descriptors were added to the 60 jars that integrate the closure, in order to have a comparable dataset.  The result measured in time is as follows:

![Fig5](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig5.png)<br />
<sub>Fig. 5 – Response time (less is better). Three attempts each tool.</sub>
  
### Use cases

#### Example 1
In this case, a statistics system requires the following modules to compile and execute: *com.stats.cli*, *com.stats.core*, *com.google.guava*, *org.apache.math* and *org.apache.rng* (Fig. 6).

![Fig6](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig6.png)<br />
<sub>Fig. 6 – Module *com.stats.cli* and it dependencies.</sub>

<br />
To download this module with all the dependencies, the user must:  


1. Download the client **Mozo.class** file from:

http://trimatek.org/mozo/Mozo.class

2. Run it from the command line interface with:

`$> java –cp . Mozo`

3. Within the Mozo command prompt, the user enters the **find-modules** command:

`mozo> find-modules com.stats.cli`

Then, it will display the result in a hierarchical tree of all the required modules and the paths.

4. Finally, enter the **download-modules** or the abbreviated version **dm** command:

`mozo> dm res1`

It will result in:

![Fig7](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig7.png)<br />
<sub>Fig. 7 – Modules download.</sub>

Now, the user has the target module (*com.stats.cli*) and all its dependencies in his local environment.

#### Example 2
To resolve and download the 60 modules required to compile and run the Quickstart class, the user must start the Mozo client (Mozo.class) and enter:

`fm org.geotools.gt_shapefile,org.geotools.gt_swing` (**fm** is the abbreviated version of **find-modules**) as shown in Figure 8:

![Fig8](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig8.png)<br />
<sub>Fig. 8 – Dependencies request of Quickstart with Mozo.</sub><br/>

The result is the dependencies tree:

![Fig9](https://github.com/martinaguero/mozo/blob/master/org.trimatek.mozo.ui/icons/fig9.png)<br />
<sub>Fig. 9 – Complete module dependencies and its paths.</sub><br/>

Finally, to download the modules from the repositories to the local environment, the user must enter `dm res1` (**dm** is the abbreviated version of **download-modules**)

<sub>Contact: trimatek.org@gmail.com</sub><br />
<sub>Last update: SEP-28-2019</sub>

[RemoteZip]:https://github.com/martinaguero/remotezip
[GeoTools]:http://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html


