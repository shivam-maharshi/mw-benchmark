rm /home/shivam/monitor/resources.txt
touch /home/shivam/monitor/resources.txt
chmod -R 777 /home/shivam/monitor/resources.txt
sudo service redis restart
sh /home/shivam/mr.sh
sh /home/shivam/logstash.sh &
