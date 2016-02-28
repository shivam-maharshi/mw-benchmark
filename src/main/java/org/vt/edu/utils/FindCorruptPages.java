package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.RestClient;

/**
 * This scripts iterates over the complete read trace bound by the read count
 * and stores any title into a CorruptPage.txt file whose response time takes
 * more than 10 seconds. This value is configurable.
 * 
 * @author shivam.maharshi
 */
public class FindCorruptPages {

	String hostAd;
	String inputFile;
	String outputFile;
	long fixCount;

	public FindCorruptPages(String hostAd, String inputFile, String outputFile, long fixCount) {
		this.hostAd = hostAd;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.fixCount = fixCount;
	}

	public void execute() {
		RestClient rc = RestClient.getClient();
		List<String> urls = FileUtil.read(inputFile, fixCount);
		List<String> invalidUrls = new ArrayList<>();
		for (String url : urls) {
			url = "http://" + hostAd + "/index.php/" + url;
			int responseCode;
			try {
				System.out.println("Verifying URL : "+url);
				responseCode = rc.httpGet(url,
						new HashMap<String, ByteIterator>());
			} catch (RestClient.TimeoutException | IOException e) {
				responseCode = 500;
			}
			if (responseCode == 500) {
				System.out.println("Found corrupt URL: "+url);
				invalidUrls.add(url);
			}
		}
		FileUtil.write(invalidUrls, outputFile);
	}

	public static void main(String[] args) {
		/*
		 * Sample Command: sudo java -Xms8096m -Xmx12086m -cp
		 * "YCSB4WebServices-0.0.jar" org.vt.edu.utils.FindCorruptPages
		 * -ad=192.168.1.51:80 -input=~/development/benchmarking/readtrace.txt
		 * -output=~/development/benchmarking/corrupturls.txt -count=10000
		 */

		String hostAd = "192.168.1.51:80";
		String input = RELATIVE_PATH + "readtrace.txt";
		String output = RELATIVE_PATH + "corrupturls.txt";
		long count = 1;
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
		FindCorruptPages f = new FindCorruptPages(hostAd, input, output, count);
		f.execute();
	}

}
