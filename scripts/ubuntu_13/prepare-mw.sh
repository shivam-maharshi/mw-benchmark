apt-get update
apt-get upgrade
apt-get update
apt-get install apache2 php5 libapache2-mod-php5 mysql-server mysql-client php5-mysql phpmyadmin
echo "Include /etc/phpmyadmin/apache.conf" >> /etc/apache2/apache2.conf
/etc/init.d/apache2 restart
cd /var/www/html
wget "https://releases.wikimedia.org/mediawiki/1.26/mediawiki-1.26.2.tar.gz"
tar -xvzf mediawiki-1.26.2.tar.gz
mv mediawiki-1.26.2 mediawiki
chmod 777 -R mediawiki
