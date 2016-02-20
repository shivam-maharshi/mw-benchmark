package org.vt.edu.utils;

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
 * Single class to be used for any interactions with Files.
 * 
 * @author shivam.maharshi
 */
public class FileUtil {

	public static List<String> read(String filepath, long readLimit) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			String line = null;
			long count = 0;
			while ((line = file.readLine()) != null) {
				list.add(line.trim());
				count++;
				if (count >= readLimit)
					break;
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<String> read(String filepath) {
		return read(filepath, Long.MAX_VALUE);
	}

	public static void write(List<String> list, String filepath) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "utf-8"));
			for (String line : list) {
				writer.write(line + "\n");
			}
			writer.flush();
			writer.close();
			System.out.println("Invalid Urls written to : " + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
