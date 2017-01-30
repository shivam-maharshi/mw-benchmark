# [MediaWiki Benchmark] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark)
This project contains all the code and configurations to install, optimize and benchmark a Wikipedia server with realistic workload. Wikipedia, empowered by MediaWiki, can be used to assess and compare the performance of different:

1. Web Servers - Apache, LightHTTP, NGNIX, etc.
2. Web Server Modules  - Apache MPM, Preform, Event, etc.
3. Databases - MySQL, Postgress, TSQL, SQLLite, etc.
4. Caches - Accel, Memcached, Redis, etc.
5. Archiving - SiteStory, Cache Based Archiving, etc.

## [Contents] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark)

This project contains full-fledged web service benchmarking tool, utility tools, optimized configurations, SQL scripts, and shell scripts to allow for quick benchmarking by reproducing a realistic workload for benchmarking. A few important links with a brief explanation are given below:

1. [Analysis] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/analysis):
Analysis contains the raw input data about the read, write, delete, update & size trace required to calculate the input parameters for request distribution etc. THe parameters can be calculated using techniques like Linear Regression, Naive Bayes Model population, etc.

2. [Configurations] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/conf):
It contains the configurations required to optimize individual components of the Wikipedia setup. Since Wikipedia requires a lot of moving parts, included in this section are the optimized configurations for Apache Webserver, Java, MediaWiki, MySQL, PHP, PHP Zend Opcache, Apache Tomcat, etc. Please feel free to contribute more optimization configurations (that you've figured out) for these technologies or for the ones that don't exists in this folder. These configurations might save others some time and efforts :)

3. [Inputs] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/input):
Input folder contains the read, write, delete, update, size distribution trace files which are used by the benchmarking tool to generate realistic workload. These files should be created by a careful analysis of the trace files given by Wikipedia [here](https://dumps.wikimedia.org/backup-index.html). The tools for this analysis are also available in this project. Refer to section 9 for more details.

4. [Jar] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/jar):
Jar folders contains a precompiled fat jar (with dependency) of this project. The benchmarking and all util functions can be invoked using the jar present in this folder. Alternatively, you can also build this project using the command ```mvn clean build``` to create a jar compiled on your system from the source code. It is generally a good idea to follow the latter to avoid OS specific compilation issues.

5. [Outputs] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/output):
Output folders contains the output of the benchmarking and the utility tools. Please note that they do not contain the results but only the data used for analysis, db population, fixes, etc.

6. [Results] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/results):
Results folder contains the generated results reports after the successful completion of benchmarking. It also contains the benchmarking graphs, resource monitoring data, etc. Any data  pertaining to performance evaluation is all stored in this folder.

7. [Scripts] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/scripts):
Scripts folder contains the shell scripts required to perform any variety of actions which range from installing & populating Wikipedia with the correct data to, starting stopping the benchmarking and downloading & analyzing the traces. These scripts are available for both CentOS 6.6 and Ubuntu 13 

8. [SQL] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/sql):
SQL folder contains the SQL scripts required to populate the database, add & remove the indexes & keys, fix wikipedia schema, get analysis data, etc. These scripts speed up the process of setting up and maintaining a Wikipedia server for benchmarking purpose.

9. [YCSB For Web Service Benchmark] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src/main/java/com/yahoo/ycsb):
YCSB4WebServices is a modification of the light wieght core module of the Yahoo Cloud Service Benchmarking project to benchmark Web Services. The motivation for this modification is the need of an easily and highly configurable tool for benchmarking web services, especially for cloud applications, where synchronization between various clients is needed. At the time of creating this tool, YCSB did not provide a very flexible and easily configurable approach to benchmark web services. Hence this is a tightly coupled approach to benchmark web services using YCSB core module. To get the official support please visit the YCSB Rest module that I've contributed especially for RESTFul web services [here](https://github.com/brianfrankcooper/YCSB/tree/master/rest). This module here has updated internal core classes like CoreWorkload, Client, Generators etc. Flexibility has also been added to fetch the URL patterns for reads, writes, updates and deletes from multiple files. Adidtionally, you can configure Zipf's constant for Read, Write and Write Data Size patterns. Take a look at the sample workload [here](https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/workload).

10. [Utility Tools] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src/main/java/org/vt/edu)
It contains all the utitlity tools required to setup wikipedia, analyze inputs, download dumps, populate database, fix database schema, prepare traces, etc.

* [Analysis] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src/main/java/org/vt/edu/analysis)
  It contains the utility tools to analyze raw input data and create processed data usable by the benchmarking tool. Some of these tools can analyze a monthly trace, calculate request distribution parameters, etc.
  
* [Traces] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src/main/java/org/vt/edu/trace)
  It contains the utility tools to fetch and process trace files from remote servers and official wikipedia online archives. Some of these tools can download a monthly trace, unzip them, merge them together and stores them into a single file for a final trace.

* [Utils] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src/main/java/org/vt/edu/trace)
  It contains the utility tools to download resources from the internet, unzip different formats, interact with files on the localfile system, verify & fix a wikipedia installation and populate a wikipedia database using the MediaWiki APIs for sane data insertion.

## [Usage] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark)

Detailed usage for all tools is provided in the code. Using shell scripts are fairly straightforward. Given below are a few example usage for some important tasks:

### 1. Categorize URLs by HTTP Status Code:
```java -Xms8096m -Xmx12086m -cp "YCSB4WebServices-0.0.jar" org.vt.edu.utils.CategorizeByHttpStatus -ad=192.168.1.51:80/wiki -input=~/development/benchmarking/readtrace.txt -output=~/development/benchmarking/ -count=10000
```

### 2. Find Corrupt Pages:
```java -Xms8096m -Xmx12086m -cp "YCSB4WebServices-0.0.jar" org.vt.edu.utils.FindCorruptPages -ad=192.168.1.51:80 -input=~/development/benchmarking/readtrace.txt -output=~/development/benchmarking/corrupturls.txt -count=10000
```

### 3. Prepare Zipf's Log v/s Log Data:
```java -Xms8096m -Xmx12086m -cp "YCSB4WebServices-0.0.jar" org.vt.edu.utils.FindCorruptPages -in=/development/benchmarking/in.txt -out=/development/benchmarking/out.txt -number=10000
```

## [Build] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark)

* `mvn clean build`

## [Karma] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark)
The main intention behind the existence of this project on GitHub is to help someone in need. _**Feel free to fork, copy, suggest corrections or ask questions. Happy benchmarking :)**_
