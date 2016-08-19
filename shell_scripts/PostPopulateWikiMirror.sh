cd /var/www/html/mediawiki/maintenance

sudo php cleanupTitles.php
sudo php deleteSelfExternals.php
sudo php populateBacklinkNamespace.php
sudo php populateParentId.php
sudo php populateRevisionLength.php
sudo php populateRevisionSha1.php
sudo php tidyUpBug37714.php
sudo php updateRestrictions.php
sudo php updateSpecialPages.php
sudo php updateDoubleWidthSearch.php
# sudo php refreshLinks.php
sudo php rebuildtextindex.php
sudo php update.php
sudo php rebuildall.php