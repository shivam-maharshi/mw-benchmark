package org.vt.edu.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Single class to be used for any interactions with Files.
 * 
 * @author shivam.maharshi
 */
public class FileUtil {

	public static List<String> read(String filepath) {
		List<String> list = new ArrayList<String>();
		try {
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			String line = null;
			while ((line = file.readLine()) != null) {
				list.add(line.trim());
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
