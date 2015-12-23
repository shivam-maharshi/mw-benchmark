sudo chmod 777 -R /var/www/html/mediawiki

cd /var/www/html/mediawiki

sudo echo "$wgEnableAPI=true;" >> LocalSettings.php
sudo echo "$wgEnableWriteAPI=true;" >> LocalSettings.php

sudo /etc/init.d/apache2 restart

cd /home/ubuntu

sudo wget "https://dumps.wikimedia.org/elwiki/20151201/elwiki-20151201-pages-meta-history.xml.bz2"
sudo bzip2 -dk elwiki-20151201-pages-meta-history.xml.bz2

cd /var/www/html/mediawiki/maintenance

sudo php importDump.php < /home/ubuntu/elwiki-20151201-pages-meta-history.xml >& progress.log &

sudo tail -f progress.log
