package org.vt.edu.analysis;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for preparing log of URL hit counts versus log of
 * their ranks. This data is exported to a CSV file where a linear regression is
 * applied using MS Excel inbuilt tools to calculate Zipf's constant using the
 * methodology explained in this paper -
 * http://www.hpl.hp.com/research/idl/papers/ranking/ranking.html. This class is
 * used for calculating the Zipf's constant for both Read and Write operations,
 * however in general can be applied for any such similar data.
 * 
 * @author shivam.maharshi
 */
public class PrepareZipfsLogData {

	// Required for calculation of Zipf's constant for read trace.
	private static final String READ_COUNT_FILE = "readcount.txt";
	// Required for calculation of Zipf's constant for read trace.
	private static final String WRITE_COUNT_FILE = "writecount.txt";

	private static List<LogLogData> analyze(String filepath, int linesToRead) {
		Integer rank = 0;
		String countLine;
		// For Zipf's constant. Rank on X axis and Count on the Y axis.
		List<LogLogData> logLogData = new ArrayList<>();
		try {
			BufferedReader file = new BufferedReader(new FileReader(RELATIVE_PATH + filepath));
			while ((countLine = file.readLine()) != null && linesToRead > 0) {
				logLogData.add(rank, new LogLogData(Math.log10(++rank), Math.log10(Double.valueOf(countLine))));
				linesToRead--;
			}
			file.close();
		} catch (IOException e) {
			// Ignore and continue analysis for the next file.
		}
		return logLogData;
	}

	private static void writeToFile(List<LogLogData> list, String filepath) {
		try {
			Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(RELATIVE_PATH + filepath), "utf-8"));
			for (LogLogData data : list) {
				writer.write(data.rankLog + "," + data.countLog + " \n");
			}
			writer.flush();
			writer.close();
			System.out.println("Data for Zipf's constant calculation written to : " + RELATIVE_PATH + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		List<LogLogData> list = analyze(READ_COUNT_FILE, 100000);
		writeToFile(list, "ReadOpZipfsKLogData.csv");
	}

}

class LogLogData {

	double countLog;
	double rankLog;

	public LogLogData(double rankLog, double countLog) {
		this.countLog = countLog;
		this.rankLog = rankLog;
	}

}
