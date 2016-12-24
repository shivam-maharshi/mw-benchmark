package org.vt.edu.analysis;

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
 * This class uses the revision size data to generate a size distribution file
 * that closely resembles the real life insert pattern. This is used by the file
 * generator to calculate the data size to be inserted while the YCSB workload
 * runs. This was the best way to simulate the workload, since the data size
 * does not follow any given distributions like Zipfian, Exponential or Normal.
 * 
 * @author shivam.maharshi
 */
public class PrepareInsertSizeDistribution {

	private static final String REVISION_IN = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/analysis/revisionsize_output.txt";
	private static final String REVISION_OUT = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/analysis/revisionsize_distribution.txt";

	private static List<Integer> value = new ArrayList<>();
	private static List<Integer> count = new ArrayList<>();
	private static int SIZE = 0;

	private static void populateData(String filepath) {
		try {
			String traceLine = null;
			int totalCount = 0;
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			while ((traceLine = file.readLine()) != null) {
				value.add(Integer.valueOf(traceLine.split(":")[0].trim()));
				totalCount += Integer.valueOf(traceLine.split(":")[1].trim());
				count.add(totalCount);
			}
			SIZE = totalCount;
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateInsertSizeDistribution(int size, String filepath) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
			while (size > 0) {
				int count = (int) (Math.random() * SIZE);
				writer.write(getValue(count) + "\n");
				size--; // Number of samples to be build.
			}
			writer.flush();
			writer.close();
			System.out.println("Total Count : " + SIZE);
			System.out.println("Revision data size written to : " + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Returns the value of the interval to which this count belongs.
	private static int getValue(int c) {
		for (int i = 0; i < count.size() - 1; i++) {
			if (c < count.get(i))
				return value.get(i);
		}
		// Should never reach here if c < SIZE.
		return value.get(value.size() - 1);
	}

	public static void generate(String input, String output) {
		populateData(input);
		generateInsertSizeDistribution(SIZE, output);
	}

	public static void main(String[] args) {
		generate(REVISION_IN, REVISION_OUT);
	}

}
