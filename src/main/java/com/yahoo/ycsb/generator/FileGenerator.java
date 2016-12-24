/**                                                                                                                                                                                
 * Licensed under the Apache License, Version 2.0 (the "License"); you                                                                                                             
 * may not use this file except in compliance with the License. You                                                                                                                
 * may obtain a copy of the License at                                                                                                                                             
 *                                                                                                                                                                                 
 * http://www.apache.org/licenses/LICENSE-2.0                                                                                                                                      
 *                                                                                                                                                                                 
 * Unless required by applicable law or agreed to in writing, software                                                                                                             
 * distributed under the License is distributed on an "AS IS" BASIS,                                                                                                               
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or                                                                                                                 
 * implied. See the License for the specific language governing                                                                                                                    
 * permissions and limitations under the License. See accompanying                                                                                                                 
 * LICENSE file.                                                                                                                                                                   
 */

package com.yahoo.ycsb.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * A generator, whose sequence is the lines of a file.
 */
public class FileGenerator extends IntegerGenerator {
	String filename;
	String current;
	BufferedReader reader;

	/**
	 * Create a FileGenerator with the given file.
	 * 
	 * @param _filename
	 *            The file to read lines from.
	 */
	public FileGenerator(String _filename) {
		try {
			filename = _filename;
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			System.err.println("Exception: " + e);
		}
	}

	/**
	 * Return the next string of the sequence, ie the next line of the file.
	 */
	public synchronized String nextString() {
		try {
			return current = reader.readLine();
		} catch (NullPointerException e) {
			System.err.println("NullPointerException: " + filename + ':' + current);
			throw e;
		} catch (IOException e) {
			System.err.println("Exception: " + e);
			return null;
		}
	}

	/**
	 * Return the previous read line.
	 */
	public String lastString() {
		return current;
	}

	/**
	 * Reopen the file to reuse values.
	 */
	public synchronized void reloadFile() {
		try {
			System.err.println("Reload " + filename);
			reader.close();
			File file = new File(filename);
			FileInputStream in = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(in));
		} catch (IOException e) {
			System.err.println("Exception: " + e);
		}
	}

	@Override
	public int nextInt() {
		return Integer.valueOf(nextString().trim());
	}

	@Override
	public double mean() {
		// Not needed
		return 0;
	}

	public static void main(String[] args) {
		FileGenerator fg = new FileGenerator(
				"C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/analysis/revisionsize_distribution.txt");
		for (int i = 0; i < 10000; i++) {
			System.out.println(fg.nextInt());
		}
	}
}
