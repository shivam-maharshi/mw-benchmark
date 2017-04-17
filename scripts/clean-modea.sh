sudo rm -rf /var/cache/sw/*
sudo /usr/sbin/httpd -f /etc/httpd/conf/httpd_a.conf -k start
sudo service mysqld restart
