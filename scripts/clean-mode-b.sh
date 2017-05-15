sudo rm -rf /var/cache/wiki/*
sudo /usr/sbin/httpd -f /etc/httpd/conf/httpd-b.conf -k start
sudo service mysqld restart
