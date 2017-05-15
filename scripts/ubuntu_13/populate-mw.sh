chmod 777 -R /var/www/html/mediawiki
cd /var/www/html/mediawiki
echo "$wgEnableAPI=true;" >> LocalSettings.php
echo "$wgEnableWriteAPI=true;" >> LocalSettings.php
php maintenance/rebuildall.php
/etc/init.d/apache2 restart
cd /home/ubuntu
wget "https://dumps.wikimedia.org/elwiki/20151201/elwiki-20151201-pages-meta-history.xml.bz2"
bzip2 -dk elwiki-20151201-pages-meta-history.xml.bz2
cd /var/www/html/mediawiki/maintenance
php importDump.php < /home/ubuntu/elwiki-20151201-pages-meta-history.xml >& progress.log
java -Xmx16396m -Xms1024m -XX:+UseParallelGC -server -jar mwdumper.jar --format=sql:1.5 /home/ubuntu/elwiki-20151201-pages-meta-history.xml.bz2 | mysql -u root -p wikimirror --default-character-set=utf8
tail -f progress.log
