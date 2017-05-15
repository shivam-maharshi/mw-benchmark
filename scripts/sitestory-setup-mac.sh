brew update
brew upgrade
brew tap homebrew/apache
brew install httpd22
brew install gcc48
brew install git
sed -i '' "s/backup/$ARCHIVE_DIR/g" ~/development/apache-tomcat-6.0.41/webapps/sitestory/WEB-INF/classes/ta.properties
apt-get install apache2-dev
apt-get install gcc
apt-get install build-essential
apt-get install libcurl3 php5-curl
apt-get install libapr1 libapr1-dev
apt-get install libcurl4-openssl-dev
apt-get install openssl libssl-dev
apt-get install libtool
