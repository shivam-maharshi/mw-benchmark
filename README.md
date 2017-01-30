# MediaWiki Benchmark
This project contains all the code and configurations to install, optimize and benchmark a Wikipedia server with realistic workload. Wikipedia, empowered by MediaWiki, can be used to assess and compare the performance of different:

1. Web Servers - Apache, LightHTTP, NGNIX, etc.
2. Web Server Modules  - Apache MPM, Preform, Event, etc.
3. Databases - MySQL, Postgress, TSQL, SQLLite, etc.
4. Caches - Accel, Memcached, Redis, etc.
5. Archiving - SiteStory, Cache Based Archiving, etc.

This project contains a wide variety of java utility tools, full-fledged web service benchmarking tool, configurations, SQL & shell scripts to allow for quick benchmarking by reproducing a realistic workload for benchmarking. A few important links with a brief explanation are given below:

1. 

This is a modification of the light wieght core module of the Yahoo Cloud Service Benchmarking project to benchmark Web Services. The motivation for this project is the need of an easily and highly configurable tool for benchmarking web services, especially for cloud applications, where synchronization between various clients is needed. Since YCSB does not provide a very flexible and easily configurable way to benchmark web services, I've decided to customize it for now. I've updated the internal core classes like CoreWorkload, Client, Generators etc. Flexibility has also been added to fetch the URL patterns for reads and writes from multiple files. Adidtionally, you can configure Zipf's constant for Read, Write and Write Data Size patterns.
