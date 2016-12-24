package org.vt.edu.trace;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.vt.edu.utils.DownloadUtil;
import static org.vt.edu.utils.Constant.*;

/**
 * Downloads the monthly trace from wikipedia statistics site in parallel.
 * 
 * @author shivam.maharshi
 */
public class DownloadMonthlyTrace {

	public static final String URL = "https://dumps.wikimedia.org/other/pagecounts-raw/2015/2015-11/";
	private static final int THREAD_COUNT = 3;
	private static final int TOTAL_FILES = 720;

	public static void download() {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
		for (int i = 0; i < TOTAL_FILES; i++) {
			executorService.execute(new DownloadTask());
		}
		executorService.shutdown();
	}

	public static void main(String[] args) {
		System.out.println("Starting montly trace download.");
		download();
		System.out.println("Successfully finished monthly trace download.");
	}

}

class DownloadTask implements Runnable {

	@Override
	public void run() {
		String file = TraceFileNameGenerator.getNextFile(GZIP_TYPE);
		if (file.equals(TraceFileNameGenerator.ALL_DOWNLOADED)) {
			return;
		}
		DownloadUtil.download(DownloadMonthlyTrace.URL, file);
	}

}