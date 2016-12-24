package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;
import static org.vt.edu.utils.Constant.OUTPUT_PATH;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.RestClient;

/**
 * This scripts iterates over the complete read trace bound by the read count
 * and stores any title into a CorruptPage.txt file whose response time takes
 * more than 10 seconds. This value is configurable.
 * 
 * @author shivam.maharshi
 */
public class CategorizeByHttpStatus {

	String hostAd;
	String inputFile;
	String outputFolder;
	long readCount;

	public CategorizeByHttpStatus(String hostAd, String inputFile, String outputFolder, long readCount) {
		this.hostAd = hostAd;
		this.inputFile = inputFile;
		this.outputFolder = outputFolder;
		this.readCount = readCount;
	}

	public void execute() {
		long start = System.currentTimeMillis();
		RestClient rc = RestClient.getClient();
		List<String> titles = FileUtil.read(inputFile, readCount);
		Map<Integer, List<String>> rsMap = new HashMap<>();
		for (String title : titles) {
			String url = "http://" + hostAd + "/index.php/" + title;
			int responseCode;
			try {
				System.out.println("Categorizing URL : " + url);
				responseCode = rc.httpGet(url, new HashMap<String, ByteIterator>());
			} catch (RestClient.TimeoutException | IOException e) {
				responseCode = 500;
			}
			List<String> list = rsMap.get(responseCode);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(title);
			rsMap.put(responseCode, list);
		}
		writeOutput(rsMap, outputFolder);
		System.out.println("Successfully finished categorizing URLs in : "
				+ ((System.currentTimeMillis() - start) / 60000) + " mins.");
	}

	private void writeOutput(Map<Integer, List<String>> map, String outputFolder) {
		Set<Entry<Integer, List<String>>> set = map.entrySet();
		for (Entry<Integer, List<String>> entry : set) {
			Integer code = entry.getKey();
			List<String> list = entry.getValue();
			FileUtil.write(list, outputFolder + code + ".txt");
		}
	}

	public static void main(String[] args) {
		/*
		 * Sample Command: sudo java -Xms8096m -Xmx12086m -cp
		 * "YCSB4WebServices-0.0.jar" org.vt.edu.utils.CategorizeByHttpStatus
		 * -ad=192.168.1.51:80/wiki -input=~/development/benchmarking/readtrace.txt
		 * -output=~/development/benchmarking/ -count=10000
		 */

		String hostAd = "192.168.1.51:80/wiki";
		String input = RELATIVE_PATH + "readtrace.txt";
		String output = OUTPUT_PATH;
		long count = 10000;
		int argLen = args.length;
		for (int i = 0; i < argLen; i++) {
			if (args[i].startsWith("-ad=")) {
				hostAd = args[i].split("=")[1];
			} else if (args[i].startsWith("-input=")) {
				input = args[i].split("=")[1];
			} else if (args[i].startsWith("-output=")) {
				output = args[i].split("=")[1];
			} else if (args[i].startsWith("-count=")) {
				count = Long.valueOf(args[i].split("=")[1]);
			}
		}
		CategorizeByHttpStatus c = new CategorizeByHttpStatus(hostAd, input, output, count);
		c.execute();
	}

}
