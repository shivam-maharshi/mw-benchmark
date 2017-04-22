package org.vt.edu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates CSV to generate graphs from the benchmarking data.
 * 
 * @author shivam.maharshi
 */
public class GenerateCsv {

  public static void main(String[] args) {
    GenerateCsv gc = new GenerateCsv();
    String in = "C:/Users/Sam/Desktop/big/1Gbps/";
    String out = "C:/Users/Sam/Desktop/fcfn.csv";
    gc.execute(in, out);
  }

  public void execute(String in, String out) {
    ParseResults ps = new ParseResults();
    Map<String, Map<Long, Result>> res = ps.parseModes(in);
    Map<Long, Result> a = res.get("a"), b = res.get("b"), c = res.get("c");
    List<String> l = new ArrayList<>();
    l.add(",Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,");
    StringBuilder s = null;
    for (Long k : a.keySet()) {
      s = new StringBuilder();
      s.append(k).append(",").append(a.get(k).getThroughput()).append(",").append(b.get(k).getThroughput()).append(",")
          .append(c.get(k).getThroughput()).append(",").append(",");
      s.append(k).append(",").append(a.get(k).getAvgLatency()).append(",").append(b.get(k).getAvgLatency()).append(",")
          .append(c.get(k).getAvgLatency()).append(",").append(",");
      s.append(k).append(",").append(a.get(k).getTotalErrors()).append(",").append(b.get(k).getTotalErrors())
          .append(",").append(c.get(k).getTotalErrors()).append(",").append(",");
    }
    l.add(s.toString());
    FileUtil.write(l, out);
  }

}
