rm -rf /var/cache/wiki/*
/usr/sbin/httpd -f /etc/httpd/conf/httpd-d.conf -k restart
service mysqld restart
