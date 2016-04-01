package org.vt.edu.analysis;

import java.util.ArrayList;
import java.util.List;

import org.vt.edu.utils.FileUtil;

/**
 * Reads the CPU and RAM data and stores the value in a csv to prepare graphs.
 * 
 * @author shivam.maharshi
 */
public class ReadCPUAndRAMData {

	public static void exportToCsv(String input, String output) {
		List<String> lines = FileUtil.read(input);
		List<String> outputLines = parseFile(lines);
		FileUtil.write(outputLines, output);
	}

	private static List<String> parseFile(List<String> lines) {
		List<String> res = new ArrayList<>();
		int min = 5;
		boolean cpuRamCouple = false;
		String temp = "";
		for (String line : lines) {
			if (line.startsWith("CPU")) {
				temp += min + "," + line.split(",")[0].split(":")[1].split("%us")[0].trim();
			} else if (line.startsWith("Mem:")) {
				temp += "," + Double.valueOf(line.split(",")[1].split("k used")[0].trim()) / 1000000;
				cpuRamCouple = true;
			}
			if (cpuRamCouple) {
				res.add(temp);
				min += 5;
				temp = "";
				cpuRamCouple = false;
			}
		}
		return res;
	}

	public static void main(String[] args) {
		String relPath = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/";
		exportToCsv(relPath + "ModeB_20.txt", relPath + "result_20.csv");
	}

}
