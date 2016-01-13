package org.vt.edu.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class analyzes the revision table one month data to calculate the
 * minimum and maximum size of a revision length in bytes and figure out the
 * distribution followed them. Seems like scattered zipf's distribution.
 * 
 * @author shivam.maharshi
 */
public class AnalyzeRevisionDataSize {

	private static final String REVISION_FILE = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/analysis/revisionsize.txt";
	private static final String REVISION_OUTPUT = "C:/Users/Sam/Google Drive/Job/VirginiaTech/MS Thesis/YCSB4WebServices/analysis/revisionsize_output.txt";

	public static Map<Long, Integer> calculateMaxMinRevisionSize(String filepath) {
		Map<Long, Integer> sizeDistribution = new HashMap<>();
		try {
			long min = Integer.MAX_VALUE;
			long max = Integer.MIN_VALUE;
			long prevPageId = 0;
			long curPageId = 0;
			long prevSize = 0;
			long curSize = 0;
			String traceLine;
			BufferedReader file = new BufferedReader(new FileReader(filepath));
			while ((traceLine = file.readLine()) != null) {
				String[] seg = traceLine.split(",");
				curPageId = Long.valueOf(seg[0]);
				curSize = Long.valueOf(seg[1]);
				if (curPageId == prevPageId) {
					long diff = curSize - prevSize;
					if (diff > 0) {
						if (diff < min) {
							min = diff;
						} else if (diff > max) {
							max = diff;
						}
						if (sizeDistribution.containsKey(diff)) {
							sizeDistribution.put(diff, sizeDistribution.get(diff) + 1);
						} else {
							sizeDistribution.put(diff, 1);
						}
					}
				}
				prevPageId = curPageId;
				prevSize = curSize;
			}
			file.close();
			System.out.println("Maximum size: " + max + " , Minimum size: " + min);
		} catch (IOException e) {
			System.out.println("Error while opening file : " + filepath);
			System.out.println("Exception occured : " + e.getMessage());
		}
		return sizeDistribution;
	}

	private static List<RevisionSize> getList(Map<Long, Integer> map) {
		List<RevisionSize> list = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : map.entrySet()) {
			list.add(new RevisionSize(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	private static void writeToFile(List<RevisionSize> list, String output) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
			for (RevisionSize size : list) {
				writer.write(size.revisionSize + " : " + size.count + " \n");
			}
			writer.flush();
			writer.close();
			System.out.println("Revision data size written to : " + output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Map<Long, Integer> sizeDistribution = calculateMaxMinRevisionSize(REVISION_FILE);
		List<RevisionSize> list = getList(sizeDistribution);
		Collections.sort(list);
		writeToFile(list, REVISION_OUTPUT);
	}

}

class RevisionSize implements Comparable<RevisionSize> {

	public long revisionSize;
	public int count;

	public RevisionSize() {
	}

	public RevisionSize(long revisionSize, int count) {
		super();
		this.revisionSize = revisionSize;
		this.count = count;
	}

	@Override
	public int compareTo(RevisionSize o) {
		if (this.count > o.count) {
			return -1;
		} else if (this.count < o.count) {
			return 1;
		} else {
			return 0;
		}
	}

}
