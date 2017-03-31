sudo visudo
shivam	ALL=(ALL)	ALL
wget http://download.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
rpm -ivh epel-release-6-8.noarch.rpm
yum install -y httpd mysql-server php php-mysql
yum --enablerepo=extras install -y epel-release
yum --enablerepo=epel install -y phpmyadmin
wget https://github.com/shivam-maharshi/scripts/blob/master/install/centos_6.6/php_5.6.sh
sh php_5.6.sh
setsebool httpd_can_network_connect_db=1
service mysqld restart
mkdir /var/cache/w
chmod 777 -R /var/cache/w
mysqladmin -u root password 'root'
service mysqld restart
service httpd restart
cd /var/www/html
wget "https://releases.wikimedia.org/mediawiki/1.26/mediawiki-1.26.2.tar.gz"
tar -xvzf mediawiki-1.26.2.tar.gz
mv mediawiki-1.26.2 w
chmod 777 -R w
