package org.vt.edu.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Provides a utility method to unzip a zipped file and store it.
 * 
 * @author shivam.maharshi
 */
public class GzipUtil {

	public static void unzip(String compressedFile, String decompressedFile) {
		System.out.println("Unzipping file : " + compressedFile);
		byte[] buffer = new byte[1024];
		try {
			FileInputStream fileIn = new FileInputStream(compressedFile);
			GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
			FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);
			int bytes_read;
			while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
				fileOutputStream.write(buffer, 0, bytes_read);
			}
			System.out.println("File unzipped successfully : " + compressedFile);
			fileIn.close();
			gZIPInputStream.close();
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error while unzipping. File not found : " + compressedFile);
		} catch (IOException ex) {
			// Ignore and continue un-zipping the next file.
		}
	}

	public static void main(String[] args) {
		GzipUtil.unzip("pagecounts-20151101-000000.gz", "wikitrace/pagecounts-20151101-000000.txt");
	}

}
