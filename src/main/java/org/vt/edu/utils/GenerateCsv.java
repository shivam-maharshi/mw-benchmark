package org.vt.edu.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates CSV to generate graphs from the benchmarking data.
 * 
 * @author shivam.maharshi
 */
public class GenerateCsv {

  public static void main(String[] args) {
    GenerateCsv gc = new GenerateCsv();
    String a = "C:/Users/Sam/Desktop/mode-a/big/1Gbps/set-1-375";
    String b = "C:/Users/Sam/Desktop/mode-b/big/1Gbps/set-1-375";
    String c = "C:/Users/Sam/Desktop/mode-c/big/1Gbps/set-6-375";
    String out = "C:/Users/Sam/Desktop/fcfn.csv";
    gc.execute(a, b, c, out);
  }

  public void execute(String a, String b, String c, String out) {
    Map<Integer, String> al = readFolder(a, 1), bl = readFolder(b, 1), cl = readFolder(c, 1);
    Map<Integer, String> af = readFolder(a, 2), bf = readFolder(b, 2), cf = readFolder(c, 2);
    List<String> l = new ArrayList<>();
    l.add(",Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,");
    for (Integer k : al.keySet())
      l.add(k + "," + al.get(k) + "," + bl.get(k) + "," + cl.get(k) + "," + "," + k + "," + af.get(k) + "," + bf.get(k)
          + "," + cf.get(k));
    FileUtil.write(l, out);
  }

  public Map<Integer, String> readFolder(String folder, int param) {
    Map<Integer, String> res = new TreeMap<>();
    File fldr = new File(folder);
    File[] files = fldr.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isFile() && files[i].getName().startsWith("br")) {
        String fn = files[i].getName().split("\\.")[0];
        int f = Integer.valueOf(fn.substring(2, fn.length()));
        switch (param) {
        case 1:
          res.put(f, getFailedNumber(FileUtil.read(files[i].getPath())));
          break;
        case 2:
          res.put(f, getAverageLatency(FileUtil.read(files[i].getPath())));
          break;
        default:
          break;
        }
      }
    }
    return res;
  }

  public String getFailedNumber(List<String> lines) {
    int failed = 0;
    for (String line : lines)
      if (line.startsWith("[READ], Return=ERROR") || line.startsWith("[INSERT], Return=ERROR"))
        failed += Integer.parseInt(line.split(",")[2].trim());
    return String.valueOf(failed);
  }

  public String getAverageLatency(List<String> lines) {
    for (String line : lines)
      if (line.startsWith("[READ], AverageLatency(us)"))
        return line.split(",")[2].trim();
    return null;
  }

}
