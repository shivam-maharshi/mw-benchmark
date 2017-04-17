sudo rm /home/shivam/monitor/resources.txt
sudo touch /home/shivam/monitor/resources.txt
sudo chmod -R 777 /home/shivam/monitor/resources.txt
sudo sh /home/shivam/mr.sh
sudo sh /home/shivam/logstash.sh &
sudo rm -rf /var/www/html/archive/
sudo mkdir /var/www/html/archive/
sudo chmod -R 777 /var/www/html/archive/

