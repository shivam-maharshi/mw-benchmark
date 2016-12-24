package org.vt.edu.trace;

import java.util.concurrent.atomic.AtomicInteger;

import org.vt.edu.utils.Constant;

/**
 * Class responsible for generating the file name.
 * 
 * @author shivam.maharshi
 */
public class TraceFileNameGenerator {

	private static volatile AtomicInteger hourCounter = new AtomicInteger(0);
	private static volatile AtomicInteger dateCounter = new AtomicInteger(1);
	public static final String ALL_DOWNLOADED = "AD";
	private static final String FILE_NAME_TEMPLATE = "pagecounts-201511date-hour0000";

	public static synchronized String getNextFile(String extension) {
		int date = dateCounter.intValue();
		int hour = hourCounter.intValue();
		if (date > 30) {
			return ALL_DOWNLOADED;
		}
		if (hour > 23) {
			if (date >= 30) {
				return ALL_DOWNLOADED;
			} else {
				date = dateCounter.incrementAndGet();
				hourCounter.set(0);
				hour = 0;
			}
		}

		hourCounter.getAndIncrement();

		String hourVal = "";
		if (hour < 10) {
			hourVal = "0";
		}
		hourVal += hour;
		String file = FILE_NAME_TEMPLATE.replace("hour", hourVal);

		String dateVal = "";
		if (date < 10) {
			dateVal = "0";
		}
		dateVal += date;
		file = file.replace("date", dateVal);

		return file + extension;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 720; i++) {
			System.out.println(" sudo rm " + TraceFileNameGenerator.getNextFile(Constant.GZIP_TYPE));
		}
	}

}
