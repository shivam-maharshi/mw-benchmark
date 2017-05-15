rm -rf /var/cache/wiki/*
/usr/sbin/httpd -f /etc/httpd/conf/httpd-c.conf -k restart
service mysqld restart
