package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;

import java.util.ArrayList;
import java.util.List;

import com.yahoo.ycsb.WebClient;

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
		WebClient w = new WebClient();
		List<String> urls = FileUtil.read(inputFile, fixCount);
		List<String> invalidUrls = new ArrayList<>();
		for (String url : urls) {
			int responseCode = w.sendGet("http://" + hostAd + "/mediawiki/index.php/" + url);
			if (responseCode == 500)
				invalidUrls.add(url);
		}
		FileUtil.write(invalidUrls, outputFile);
	}

	public static void main(String[] args) {
		FindCorruptPages f = new FindCorruptPages("192.168.1.51:80", RELATIVE_PATH + "readtrace.txt",
				RELATIVE_PATH + "corrupturls.txt", 10000);
		f.execute();
	}

}
