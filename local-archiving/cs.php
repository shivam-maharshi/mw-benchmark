<?php

error_reporting(E_ALL);
$file = '/var/www/html/archive/'.md5("http://".explode('http://', $_SERVER['REQUEST_URI'])[1]).".txt";

if(!file_exists($file)){ 
    $handle = fopen('php://input', 'rb');
    $data = '';
    if($handle) {
        while(($buffer = fgets($handle, 4096)) !== false) {
             $data .= $buffer;
        }
        if(!feof($handle)) {
            #$logger->error("Error: unexpected fgets() fail.");
        }
    }
    file_put_contents($file, $data);
    fclose($handle);
}

?>
