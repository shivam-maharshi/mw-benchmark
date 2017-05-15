<?php
include '/var/www/html/apache-log4php-2.3.0/src/main/php/Logger.php';
Logger::configure('config.xml');
$log = Logger::getLogger("myLog");
error_reporting(E_ALL);
require "/var/www/html/predis/autoload.php";
Predis\Autoloader::register();
$key = md5(explode('http://', $_SERVER['REQUEST_URI'])[1]);
$redis = new Predis\Client(array("schema" => "tcp", "host" => "127.0.0.1", "port" => "6379"));
$data = '';
$handle = fopen('php://input', 'rb');
if($handle) {
    while(($buffer = fgets($handle, 4096)) !== false) {
        $data .= $buffer;
    }
    if(!feof($handle)) {
    }
}
$redis->set($key, $data);
$redis->disconnect();
?>
