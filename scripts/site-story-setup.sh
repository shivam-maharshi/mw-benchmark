brew update
brew upgrade
brew tap homebrew/apache
brew install httpd22
brew install gcc48
brew install git
sudo sed -i '' "s/backup/$ARCHIVE_DIR/g" ~/development/apache-tomcat-6.0.41/webapps/sitestory/WEB-INF/classes/ta.properties
sudo apt-get install apache2-dev
sudo apt-get install gcc
sudo apt-get install build-essential
sudo apt-get install libcurl3 php5-curl
sudo apt-get install libapr1 libapr1-dev
sudo apt-get install libcurl4-openssl-dev
sudo apt-get install openssl libssl-dev
sudo apt-get install libtool
