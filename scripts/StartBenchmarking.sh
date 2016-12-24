cd ~/benchmarking/YCSB4WebServices/jar

sudo java -Xms8096m -Xmx12086m -cp "YCSB4WebServices-0.0.jar" com.yahoo.ycsb.Client -s -t -P C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/workload/workload -p threadcount=1 -p target=1 -p operationcount=10 -p exportfile=C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/BenchmarkingResults.txt  