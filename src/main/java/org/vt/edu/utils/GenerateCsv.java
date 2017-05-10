package org.vt.edu.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Creates CSV to generate graphs from the benchmarking data.
 * 
 * @author shivam.maharshi
 */
public class GenerateCsv {

  public static void main(String[] args) {
    GenerateCsv gc = new GenerateCsv();
    String in = "C:/Users/Sam/Desktop/gzip/small/100Mbps";
    String out = "C:/Users/Sam/Desktop/scsn.csv";
    gc.results(in, out);
    // gc.resources(in, out);
  }

  public void results(String in, String out) {
    ParseResults ps = new ParseResults();
    Map<String, Map<Long, Result>> res = ps.parseModes(in);
    Map<Long, Result> a = res.get("a"), b = res.get("b"), c = res.get("c");
    List<String> l = new ArrayList<>();
    l.add(",,Throughput,,,,,Average Latency,,,,,Errors,");
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
      l.add(s.toString());
    }
    FileUtil.write(l, out);
  }

  public void resources(String in, String out) {
    ParseResources ps = new ParseResources();
    Map<String, Map<Long, List<Resource>>> res = ps.parseModes(in);
    Map<Long, List<Resource>> a = res.get("a"), b = res.get("b"), c = res.get("c");
    Set<Long> keys = new TreeSet<>();

    for (Long key : a.keySet())
      keys.add(key);
    for (Long key : b.keySet())
      keys.add(key);
    for (Long key : c.keySet())
      keys.add(key);

    List<String> l = new ArrayList<>();
    l.add(",,CPU,,,,,RAM,,,,,NET-READ,,,,,NET-WRITE,,,,,DISK-READ,,,,,DISK-WRITE");
    l.add(
        ",Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,,,Mode A, Mode B, Mode C,");
    StringBuilder sb = null;
    for (Long key : keys) {
      List<Resource> ar = a.get(key), br = b.get(key), cr = c.get(key);
      int as = ar == null ? 0 : ar.size(), bs = br == null ? 0 : br.size(), cs = cr == null ? 0 : cr.size();
      int size = Math.min(Math.min(as, bs), cs);
      for (int i = 0; i < size; i++) {
        Resource ra = ar.get(i), rb = br.get(i), rc = cr.get(i);
        sb = new StringBuilder();
        sb.append(key).append(",").append(ra.getCpu()).append(",").append(rb.getCpu()).append(",").append(rc.getCpu())
            .append(",").append(",");
        sb.append(key).append(",").append(ra.getRam()).append(",").append(rb.getRam()).append(",").append(rc.getRam())
            .append(",").append(",");
        sb.append(key).append(",").append(ra.getNetread()).append(",").append(rb.getNetread()).append(",")
            .append(rc.getNetread()).append(",").append(",");
        sb.append(key).append(",").append(ra.getNetwrite()).append(",").append(rb.getNetwrite()).append(",")
            .append(rc.getNetwrite()).append(",").append(",");
        sb.append(key).append(",").append(ra.getDiskread()).append(",").append(rb.getDiskread()).append(",")
            .append(rc.getDiskread()).append(",").append(",");
        sb.append(key).append(",").append(ra.getDiskwrite()).append(",").append(rb.getDiskwrite()).append(",")
            .append(rc.getDiskwrite()).append(",").append(",");
        l.add(sb.toString());
      }
    }
    FileUtil.write(l, out);
  }

}
