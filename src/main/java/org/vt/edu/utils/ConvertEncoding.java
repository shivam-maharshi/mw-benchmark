package org.vt.edu.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to convert encoding from one format to another.
 * 
 * @author shivam.maharshi
 */
public class ConvertEncoding {

	private static final String REVISION_IN = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/benchmarking_input/writetrace.txt";
	private static final String REVISION_OUT = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/benchmarking_input/writetraceutf8.txt";

	public static void converToUTF8(String in, String out) {
		writeData(readData(in), out);
	}

	private static List<String> readData(String filepath) {
		List<String> data = new ArrayList<>();
		try {
			String traceLine = null;
			int totalCount = 0;
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			while ((traceLine = file.readLine()) != null) {
				data.add(traceLine);
				totalCount++;
			}
			file.close();
			System.out.println("Total Lines Read : " + totalCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private static void writeData(List<String> data, String filepath) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
			int totalCount = 0;
			for (String title : data) {
				writer.write(URLEncoder.encode(title, "UTF-8") + "\n");
				totalCount++;
			}
			writer.flush();
			writer.close();
			System.out.println("Revision data size written to : " + filepath);
			System.out.println("Total Lines Written : " + totalCount);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		converToUTF8(REVISION_IN, REVISION_OUT);
	}

}
