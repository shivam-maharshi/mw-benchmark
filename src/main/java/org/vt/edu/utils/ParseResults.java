package org.vt.edu.utils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This class parses the benchmarking results and stores the read average
 * latency, maximum latency, minimum latency, overall throughput, number of
 * errors, total operations, successful reads, failed reads, successful writes
 * and failed writes from a folder containing all the results for all the modes.
 * 
 * The folder must be defined in this format: mode-{a,b,c} > set-{1,2,3}-*.
 * 
 * @author shivam.maharshi
 */
public class ParseResults {

  public Map<String, Map<Long, Result>> parseModes(String parentFolder) {
    Map<Long, Result> a = computeAvgResults(parentFolder + File.separator + "mode-a");
    Map<Long, Result> b = computeAvgResults(parentFolder + File.separator + "mode-b");
    Map<Long, Result> c = computeAvgResults(parentFolder + File.separator + "mode-c");
    Map<String, Map<Long, Result>> r = new TreeMap<>();
    r.put("a", a);
    r.put("b", b);
    r.put("c", c);
    return r;
  }

  private Map<Long, Result> computeAvgResults(String modesFolder) {
    int folders = 0;
    File folder = new File(modesFolder);
    File[] files = folder.listFiles();
    Map<Long, Result> res = new TreeMap<>();
    for (File file : files) {
      if (file.isDirectory()) {
        folders++;
        Map<Long, Result> results = parseResults(file.getPath());
        Iterator<Entry<Long, Result>> it = results.entrySet().iterator();
        while (it.hasNext()) {
          Entry<Long, Result> e = it.next();
          Result ir = e.getValue();
          Long key = e.getKey();
          if (res.get(key) == null)
            res.put(key, new Result());
          Result r = res.get(key);
          r.setAvgLatency(r.getAvgLatency() + ir.getAvgLatency());
          r.setDeleteErrors(r.getDeleteErrors() + ir.getDeleteErrors());
          r.setDeletes(r.getDeletes() + ir.getDeletes());
          r.setMaxLatency(r.getMaxLatency() + ir.getMaxLatency());
          r.setMinLatency(r.getMinLatency() + ir.getMinLatency());
          r.setReadErrors(r.getReadErrors() + ir.getReadErrors());
          r.setReads(r.getReads() + ir.getReads());
          r.setRuntime(r.getRuntime() + ir.getRuntime());
          r.setThroughput(r.getThroughput() + ir.getThroughput());
          r.setTotalErrors(r.getTotalErrors() + ir.getTotalErrors());
          r.setTotalOp(r.getTotalOp() + ir.getTotalOp());
          r.setUpdateErrors(r.getUpdateErrors() + ir.getUpdateErrors());
          r.setUpdates(r.getUpdates() + ir.getUpdates());
          r.setWriteErrors(r.getWriteErrors() + ir.getWriteErrors());
          r.setWrites(r.getWrites() + ir.getWrites());
        }
      }
    }
    normalizeValues(res, folders);
    return res;
  }

  private void normalizeValues(Map<Long, Result> res, int nf) {
    Iterator<Entry<Long, Result>> it = res.entrySet().iterator();
    while (it.hasNext()) {
      Result r = res.get(it.next().getKey());
      r.setAvgLatency(r.getAvgLatency() / nf);
      r.setDeleteErrors(r.getDeleteErrors() / nf);
      r.setDeletes(r.getDeletes() / nf);
      r.setMaxLatency(r.getMaxLatency() / nf);
      r.setMinLatency(r.getMinLatency() / nf);
      r.setReadErrors(r.getReadErrors() / nf);
      r.setReads(r.getReads() / nf);
      r.setRuntime(r.getRuntime() / nf);
      r.setThroughput(r.getThroughput() / nf);
      r.setTotalErrors(r.getTotalErrors() / nf);
      r.setTotalOp(r.getTotalOp() / nf);
      r.setUpdateErrors(r.getUpdateErrors() / nf);
      r.setUpdates(r.getUpdates() / nf);
      r.setWriteErrors(r.getWriteErrors() / nf);
      r.setWrites(r.getWrites() / nf);
    }
  }

  private Map<Long, Result> parseResults(String resultsFolder) {
    Map<Long, Result> res = new TreeMap<>();
    File fldr = new File(resultsFolder);
    File[] files = fldr.listFiles();
    for (File file : files) {
      if (file.isFile() && file.getName().startsWith("br")) {
        String fn = file.getName().split("\\.")[0];
        long concurrency = Integer.valueOf(fn.substring(2, fn.length()));
        Result result = parseFile(FileUtil.read(file.getPath()));
        result.setConcurrency(concurrency);
        res.put(concurrency, result);
      }
    }
    return res;
  }

  private Result parseFile(List<String> lines) {
    Result r = new Result();
    for (String l : lines) {
      if (l.startsWith("[OVERALL], RunTime"))
        r.setRuntime(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[OVERALL], Throughput"))
        r.setThroughput(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[READ], Operations"))
        r.setReads(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[READ], AverageLatency"))
        r.setAvgLatency(Double.parseDouble(l.split(",")[2].trim()) / 1000);
      else if (l.startsWith("[READ], MinLatency"))
        r.setMinLatency(Double.parseDouble(l.split(",")[2].trim()) / 1000);
      else if (l.startsWith("[READ], MaxLatency"))
        r.setMaxLatency(Double.parseDouble(l.split(",")[2].trim()) / 1000);
      else if (l.startsWith("[READ], Return=ERROR"))
        r.setReadErrors(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[INSERT], Operations"))
        r.setWrites(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[INSERT], Return=ERROR"))
        r.setWriteErrors(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[UPDATE], Operations"))
        r.setUpdates(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[UPDATE], Return=ERROR"))
        r.setUpdateErrors(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[DELETE], Operations"))
        r.setDeletes(Double.parseDouble(l.split(",")[2].trim()));
      else if (l.startsWith("[DELETE], Return=ERROR"))
        r.setDeleteErrors(Double.parseDouble(l.split(",")[2].trim()));
      r.setTotalOp(r.getDeletes() + r.getReads() + r.getUpdates() + r.getWrites());
      r.setTotalErrors(r.getDeleteErrors() + r.getReadErrors() + r.getUpdateErrors() + r.getWriteErrors());
    }
    return r;
  }

}
