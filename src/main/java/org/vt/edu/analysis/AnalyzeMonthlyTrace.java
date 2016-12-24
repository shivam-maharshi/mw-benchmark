package org.vt.edu.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.vt.edu.trace.TraceFileNameGenerator;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.vt.edu.utils.Constant.*;

/**
 * This class is responsible for analyzing the visit trace dumps from Wikipedia
 * and storing them in a data structure for reference by {@link LoadGenerator}
 * to replicate realistic life scenarios. Right now it just maintains the counts
 * of the page visits.
 * 
 * @author shivam.maharshi
 */
public class AnalyzeMonthlyTrace {

	private static String LANGUAGE_PREFIX = "el";
	private static long hits = 0L;
	private static SortedMap<TraceData, Void> urlMap = new TreeMap<>();
	private static Map<String, Long> countMap = new HashMap<>();
	// Required for creating close to realistic load while benchmarking.
	private static final String READ_TRACE_FILE = "readtrace.txt";
	// Required for calculation of Zipfs constant for read trace.
	private static final String READ_COUNT_FILE = "readcount.txt";

	private static void analyze(String filepath) {
		String traceLine;
		String[] traceSeg;
		String url;
		try {
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			while ((traceLine = file.readLine()) != null) {
				traceSeg = traceLine.split(" ");
				if (traceSeg[0].equals(LANGUAGE_PREFIX)) {
					url = traceSeg[1].trim();
					if (url != null && !url.isEmpty()) {
						Long urlCount = Long.valueOf(traceSeg[2]);
						if (countMap.containsKey(url)) {
							urlCount += countMap.get(url);
						}
						countMap.put(url, urlCount);
						hits += urlCount;
					}
				}
			}
			file.close();
		} catch (IOException e) {
			// Ignore and continue analysis for the next file.
			System.out.println("");
		}
	}

	private static void populateTreeMap() {
		for (Entry<String, Long> entry : countMap.entrySet()) {
			urlMap.put(new TraceData(entry.getKey(), entry.getValue()), null);
		}
	}

	private static void writeToFile() {
		try {
			Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(RELATIVE_PATH + READ_TRACE_FILE), "utf-8"));
			Writer countWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(RELATIVE_PATH + READ_COUNT_FILE), "utf-8"));
			for (Map.Entry<TraceData, Void> entry : urlMap.entrySet()) {
				writer.write(entry.getKey().url + " \n");
				countWriter.write(entry.getKey().count + " \n");
			}
			writer.flush();
			countWriter.flush();
			writer.close();
			countWriter.close();
			System.out.println("Monthly trace analysis written to : " + RELATIVE_PATH + READ_TRACE_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void analyze() {
		System.out.println("Starting monthly trace analysis.");
		for (int i = 0; i < 720; i++) {
			String path = RELATIVE_PATH + TraceFileNameGenerator.getNextFile(TEXT_TYPE);
			System.out.println("Analyzing file .... : " + path);
			AnalyzeMonthlyTrace.analyze(path);
			System.out.println("Successfully analyzed : " + path);
		}
		populateTreeMap();
		writeToFile();
		System.out.println("Total Hits : " + AnalyzeMonthlyTrace.hits);
		System.out.println("Successfully finisehd monthly trace analysis.");
	}

	public static void main(String[] args) {
		analyze();
	}

}

class TraceData implements Comparable<TraceData> {

	public String url;
	public long count;

	public TraceData(String url, long count) {
		super();
		this.url = url;
		this.count = count;
	}

	@Override
	public int compareTo(TraceData o) {
		return this.count < o.count ? 1 : -1;
	}

}
