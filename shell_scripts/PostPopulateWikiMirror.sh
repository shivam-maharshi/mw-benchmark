cd /var/www/html/mediawiki

sudo php maintenance/populateParentId.php
sudo php maintenance/populateRevisionLength.php
sudo php maintenance/populateRevisionSha1.php
sudo php maintenance/update.php
sudo php maintenance/rebuildall.php