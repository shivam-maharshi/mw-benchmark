/opt/logstash/bin/logstash -e 'input { file { path => [ "/home/shivam/monitor/*.txt" ] type => "collectl" } } filter { if [type] == "collectl" {
    csv {
      separator => " "
    }
    mutate {
      remove_field => [column3, column4, column6, column7, column8, column9, column10, column12, column13, column14, column15, column16, column17, column18, column19, column20, column21, column22, column23, column24, column26, column27, column28, column29, column30, column31, column32, column33, column34, column35, column36, column37, column38, column39, column40, column41, column42, column43,  column44, column45, column46, column47, column48, column49, column50, column51, column52, column53, column56, column57, column58, column59, column60, column61, column62, column63, column66, column67, column68, column69]
      rename => ["column1", "DATE", "column2", "TIME", "column11", "CPU", "column25", "RAM", "column54", "NET_RX_KB", "column55", "NET_TX_KB", "column64", "DSK_RD_KB", "column65", "DSK_WT_KB"]
      convert => {
                   "CPU" => "integer"
		   "RAM" => "integer"
                   "NET_RX_KB" => "integer"
                   "NET_TX_KB" => "integer"
                   "DSK_RD_KB" => "integer"
                   "DSK_WT_KB" => "integer"
                 }
    } } } output { elasticsearch { action => "index" index => "logstash-%{+YYYY.MM.dd}" hosts => "192.168.1.52" } }'
