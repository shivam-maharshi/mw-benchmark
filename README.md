# MediaWiki Benchmark
This project contains all the code and configurations to install, optimize and benchmark a Wikipedia server with realistic workload. Wikipedia, empowered by MediaWiki, can be used to assess and compare the performance of different:

1. Web Servers - Apache, LightHTTP, NGNIX, etc.
2. Web Server Modules  - Apache MPM, Preform, Event, etc.
3. Databases - MySQL, Postgress, TSQL, SQLLite, etc.
4. Caches - Accel, Memcached, Redis, etc.
5. Archiving - SiteStory, Cache Based Archiving, etc.

This project contains full-fledged web service benchmarking tool, utility tools, optimized configurations, SQL scripts, and shell scripts to allow for quick benchmarking by reproducing a realistic workload for benchmarking. A few important links with a brief explanation are given below:

1. [Analysis] (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/analysis)
Analysis contains the raw input data about the read, write, delete, update & size trace required to calculate the input parameters for request distribution etc. THe parameters can be calculated using techniques like Linear Regression, Naive Bayes Model population, etc.

2. Configurations (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/conf)
It contains the configurations required to optimize individual components of the Wikipedia setup. Since Wikipedia requires a lot of moving parts, included in this section are the optimized configurations for Apache Webserver, Java, MediaWiki, MySQL, PHP, PHP Zend Opcache, Apache Tomcat, etc. Please feel free to contribute more optimization configurations (that you've figured out) for these technologies or for the ones that don't exists in this folder. These configurations might save others some time and efforts :)

3. Inputs (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/input)
Input folder contains the 

4. Jar (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/jar)

5. Outputs (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/output)

6. Results (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/results)

7. Scripts (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/scripts)

8. SQL (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/sql)

9. YCSB Modified Web Service Benchmark (https://github.com/shivam-maharshi/hckn-resrch/tree/master/mw-benchmark/src)

This is a modification of the light wieght core module of the Yahoo Cloud Service Benchmarking project to benchmark Web Services. The motivation for this project is the need of an easily and highly configurable tool for benchmarking web services, especially for cloud applications, where synchronization between various clients is needed. Since YCSB does not provide a very flexible and easily configurable way to benchmark web services, I've decided to customize it for now. I've updated the internal core classes like CoreWorkload, Client, Generators etc. Flexibility has also been added to fetch the URL patterns for reads and writes from multiple files. Adidtionally, you can configure Zipf's constant for Read, Write and Write Data Size patterns.
