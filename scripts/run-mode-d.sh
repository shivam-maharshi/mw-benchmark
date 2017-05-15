rm -rf /home/shivam/archive/
mkdir /home/shivam/archive/
chmod -R 777 /home/shivam/archive/
sh /home/shivamapache-tomcat-8.5.14/bin/startup.sh
rm /home/shivam/monitor/resources.txt
touch /home/shivam/monitor/resources.txt
chmod -R 777 /home/shivam/monitor/resources.txt
sh /home/shivam/mr.sh
sh /home/shivam/logstash.sh &
