# Parking Lot 
The codebase for Parking Lot assignment from Coditas.

## About the tech stack
The solution is written in Java & leverages some features of Java SE 8
wherever possible. Here are the brief details about the tools used:
* Dev platform: `Ubuntu 18.04 LTS`
* The code is developed, deployed & tested using `OpenJDK version "1.8.0_252"` 
* Build system is `Apache Maven 3.6.0`; maven wrapper `mvnw` is also provided in the project
to alienate the need to install Apache Maven on the machine.
* `JUnit 5` for unit testing
* Uses `exec-maven-plugin` for convenience of running the program through Maven
* The compliance level for both - Java compiler & generated class files is Java 8
* The IDE used is `IntelliJ IDEA 2020.1.2 CE`

## The project structure
Entire codebase resides in the directory from where this README file is being read.
The layout as inline:
```shell script
$ ls -l
bin
file_inputs.txt
logs
parking-lot
site
```
`bin` directory contains the executable bash scripts.  
 Usage: assuming you are in the root directory:  
 ```shell script
# to run the unit tests and package the executable JAR.
parking_lot$ bin/setup.sh

# to run the parking lot code with the supplied commands file. The filename can be absolute
# or relative
parking_lot$ bin/parking_lot.sh <filename>
# e.g. bin/parking_lot.sh file_inputs.txt
```
* `parking-lot` is a maven project containing the `pom.xml` and all the Java source code.  
* `logs` directory will be generated on launching the program and the logs will be written to
the `app.log` file within it.
* `file_inputs.txt` is provided for convenience in the root directory to quickly run the code
using `bin/parking_lot.sh file_inputs.txt`
* `site` directory contains the Javadoc/Doclet for the project. 

## Running unit tests
Unit tests can be run in multiple ways:
* Through the bash script `bin/setup.sh`
* By navigating to `parking-lot` directory and running `./mvnw [clean] test`
* Through IDE

### Integration tests
Apart from unit tests, the codebase also has fare amount of integration tests.  

Additionally, `parking-lot/src/test/resources` has multiple files starting with
`file_inputs` prefix that capture the various scenarios for the parking lot problem.
These files can be used as inputs to the program to check the behaviour. Many of these
scenarios are already verified in the `DefaultParserImplTest` unit tests.  

## Running the code
It's possible through multiple ways:
* Through the bash script `bin/parking_lot.sh <filename>`
* By navigating to `parking-lot` directory and running `./mvnw exec:java -Dexec.args=<filename>`
 with maven java exec plugin
* Directly using the generated JAR: `java -jar parking-lot-1.0-SNAPSHOT-shaded.jar <filename>`

## About the solution and design
In order to make the solution extensible and robust, the codebase has adopted a generic
approach for the way commands are processed, interpreted and their output rendered.  

The design separates out 3 main parts:
- the IO processor that keeps on reading the supplied commands one by one & hands it over
to the Command infrastructure for the further processing. It also renders the command output
to the `STDOUT` or a supplied `PrintStream`.
- the generic Command processing infrastructure that deals with parsing and validating
the textual commands being read, and bridging those to the domain classes that actually end up
implementing the domain specific commands - for e.g parking lot domain. It includes classes liike
`Parser`, `Command` and relevant exception classes. `Command` class particularly encapsulates 
all the information & metadata that enables bridging from the text command to the 
domain specific target class which implements the relevant commands. 
- the other part is the domain specific implementation of the `Parser` & target class. 
In case of parking lot domain, it's the `DefaultParserImpl` class that implements the parsing
whereas `ParkingLot` is the target that implements the relevant commands.

The java docs provide a comprehensive documentation about the different classes and interfaces.  
Please refer: _**site/apidocs/index.html**_   

Overall the classes interact in the following way:


![Alt text](Design.png?raw=true "Design")

 