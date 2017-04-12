/opt/logstash/bin/logstash -e 'input { file { path => [ "/home/shivam/monitor/*.txt" ] type => "collectl" } } filter { if [type] == "collectl" {
    csv {
      separator => " "
    }
    mutate {
      remove_field => [column3, column4, column6, column7, column8, column9, column10, column12, column13, column14, column15, column16, column17, column18, column19, column20, column21, column22, column23, column24, column25, column28, column29, column30, column31, column32, column33, column34, column35, column38, column39, column40, column41]
      rename => ["column1", "DATE", "column2", "TIME", "column11", "CPU", "column26", "NET_RX_KB", "column27", "NET_TX_KB", "column36", "DSK_RD_KB", "column37", "DSK_WT_KB"]
      convert => {
                   "CPU" => "integer"
                   "NET_RX_KB" => "integer"
                   "NET_TX_KB" => "integer"
                   "DSK_RD_KB" => "integer"
                   "DSK_WT_KB" => "integer"
                 }
    } } } output { elasticsearch { action => "index" index => "logstash-%{+YYYY.MM.dd}" hosts => "192.168.1.52" } }'
