rm /home/shivam/monitor/resources.txt
touch /home/shivam/monitor/resources.txt
chmod -R 777 /home/shivam/monitor/resources.txt
sh /home/shivam/mr.sh
sh /home/shivam/logstash.sh &
rm -rf /var/www/html/archive/
mkdir /var/www/html/archive/
chmod -R 777 /var/www/html/archive/
