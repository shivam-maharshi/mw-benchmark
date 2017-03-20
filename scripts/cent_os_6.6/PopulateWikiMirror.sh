sudo chmod 777 -R /var/www/html/mediawiki
cd /var/www/html/mediawiki
sudo echo "$wgEnableAPI=true;" >> LocalSettings.php
sudo echo "$wgEnableWriteAPI=true;" >> LocalSettings.php
sudo php maintenance/rebuildall.php
sudo /etc/init.d/apache2 restart
cd /home/shivam
sudo wget "http://archive.org/download/elwiki-20151201/elwiki-20151201-pages-meta-current.xml.bz2"
sudo wget "http://archive.org/download/elwiki-20151201/elwiki-20151201-all-titles-in-ns0.gz"
sudo bzip2 -dk elwiki-20151201-pages-meta-current.xml.bz2
cd /var/www/html/mediawiki/maintenance
# For using importDump for dumping.
# sudo php importDump.php < /home/ubuntu/elwiki-20151201-pages-meta-history.xml >& progress.log &
# For using MWImport for dumping.
java -Xmx16396m -Xms1024m -XX:+UseParallelGC -server -jar mwdumper.jar --format=sql:1.5 /home/shivam/elwiki-20151201-pages-meta-current.xml.bz2 | mysql -u root -p wikimirror --default-character-set=utf8
sudo tail -f progress.log
