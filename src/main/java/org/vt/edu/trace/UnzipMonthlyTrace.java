package org.vt.edu.trace;

import org.vt.edu.utils.GzipUtil;

import static org.vt.edu.utils.Constant.*;

/**
 * Class responsible for unzipping all the downloaded wikipedia trace files.
 * 
 * @author shivam.maharshi
 */
public class UnzipMonthlyTrace {

	private static void unzip() {
		System.out.println("Unzipping the monthly trace....");
		for (int i = 0; i < 720; i++) {
			String path = RELATIVE_PATH + TraceFileNameGenerator.getNextFile(GZIP_TYPE);
			String output = path.split(GZIP_TYPE)[0] + TEXT_TYPE;
			GzipUtil.unzip(path, output);
		}
		System.out.println("Successfully unzipped monthly traced.");
	}

	public static void main(String[] args) {
		unzip();
	}

}
