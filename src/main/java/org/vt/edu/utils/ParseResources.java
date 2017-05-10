package org.vt.edu.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parses resources.
 * 
 * @author shivam.maharshi
 */
public class ParseResources {

  public Map<String, Map<Long, List<Resource>>> parseModes(String parentFolder) {
    Map<Long, List<Resource>> a = computeAvgResources(parentFolder + File.separator + "mode-a");
    Map<Long, List<Resource>> b = computeAvgResources(parentFolder + File.separator + "mode-b");
    Map<Long, List<Resource>> c = computeAvgResources(parentFolder + File.separator + "mode-c");
    Map<String, Map<Long, List<Resource>>> r = new TreeMap<>();
    r.put("a", a);
    r.put("b", b);
    r.put("c", c);
    return r;
  }

  private Map<Long, List<Resource>> computeAvgResources(String modesFolder) {
    File folder = new File(modesFolder);
    File[] files = folder.listFiles();
    List<Map<Long, List<Resource>>> resourcesList = new ArrayList<>();
    for (File file : files) {
      if (file.isDirectory()) {
        Map<Long, List<Resource>> rm = getResourceMap(file.getPath());
        resourcesList.add(rm);
      }
    }
    Map<Long, List<Resource>> res = aggregateAndNormalizeValues(resourcesList);
    return res;
  }

  private Map<Long, List<Resource>> aggregateAndNormalizeValues(List<Map<Long, List<Resource>>> l) {
    Map<Long, Integer> nf = new HashMap<>();
    Map<Long, Integer> listSize = new HashMap<>();
    Map<Long, List<Resource>> res = new TreeMap<>();
    for (Map<Long, List<Resource>> m : l) {
      for (Long key : m.keySet()) {
        // The factor by which map resources must be divided.
        if (nf.containsKey(key))
          nf.put(key, nf.get(key) + 1);
        else
          nf.put(key, 1);
        // The number of values from the smallest list to read.
        Integer size = listSize.get(key);
        if (size == null || size > m.get(key).size())
          listSize.put(key, m.get(key).size());
      }
    }

    Map<Long, List<Resource>> firstMap = l.get(0);

    for (Long key : firstMap.keySet()) {
      List<Resource> list = new ArrayList<>();
      int size = listSize.get(key);
      for (int i = 0; i < size; i++) {
        list.add(firstMap.get(key).get(i));
      }
      res.put(key, list);
    }

    // Add further values.
    for (int i = 1; i < l.size(); i++) {
      // Parsing a result folder.
      Map<Long, List<Resource>> rm = l.get(i);
      for (Long key : rm.keySet()) {
        // A particular bench step. Has some resources.
        List<Resource> list = res.get(key);
        List<Resource> ol = rm.get(key);
        int size = list == null ? 0 : list.size();
        for (int j = 0; j < size; j++) {
          Resource r = list.get(j);
          Resource or = ol.get(j);
          r.setCpu(r.getCpu() + or.getCpu());
          r.setDiskread(r.getDiskread() + or.getDiskread());
          r.setDiskwrite(r.getDiskwrite() + or.getDiskwrite());
          r.setNetread(r.getNetread() + or.getNetread());
          r.setNetwrite(r.getNetwrite() + or.getNetwrite());
          r.setRam(r.getRam() + or.getRam());
        }
      }
    }

    for (Long key : res.keySet()) {
      List<Resource> list = res.get(key);
      int n = nf.get(key);
      for (Resource r : list) {
        r.setCpu(r.getCpu() / n);
        r.setDiskread(r.getDiskread() / n);
        r.setDiskwrite(r.getDiskwrite() / n);
        r.setNetread(r.getNetread() / n);
        r.setNetwrite(r.getNetwrite() / n);
        r.setRam(r.getRam() / n);
      }
    }

    return res;
  }

  private Map<Long, List<Resource>> getResourceMap(String resFolder) {
    Map<Long, List<Resource>> result = new TreeMap<>();
    Map<Long, Double> res = parseResults(resFolder);
    List<Resource> resources = parseResources(resFolder);
    int counter = 0;
    for (Long key : res.keySet()) {
      List<Resource> l = new ArrayList<Resource>();
      for (int i = 0; i < res.get(key); i++)
        if (counter < resources.size())
          l.add(resources.get(counter++));
      result.put(key, l);
    }
    return result;
  }

  private Map<Long, Double> parseResults(String resultsFolder) {
    Map<Long, Double> res = new TreeMap<>();
    File fldr = new File(resultsFolder);
    File[] files = fldr.listFiles();
    for (File file : files) {
      if (file.isFile() && file.getName().startsWith("br")) {
        String fn = file.getName().split("\\.")[0];
        long concurrency = Integer.valueOf(fn.substring(2, fn.length()));
        List<String> lines = FileUtil.read(file.getPath());
        for (String l : lines)
          if (l.startsWith("[OVERALL], RunTime"))
            res.put(concurrency, Double.parseDouble(l.split(",")[2].trim()) / 10000);
      }
    }
    return res;
  }

  private List<Resource> parseResources(String resourceFolder) {
    return parseResourceFile(FileUtil.read(resourceFolder + File.separator + "resources.txt"));
  }

  private List<Resource> parseResourceFile(List<String> lines) {
    List<Resource> res = new ArrayList<>();
    for (int i = 2; i < lines.size(); i++) {
      String[] seg = lines.get(i).split(" ");
      Resource r = new Resource();
      r.setCpu(Double.parseDouble(seg[10]));
      r.setRam(Double.parseDouble(seg[24]) / 1000000);
      r.setNetread((Double.parseDouble(seg[53]) * 8) / 1000);
      r.setNetwrite((Double.parseDouble(seg[54]) * 8) / 1000);
      r.setDiskread(Double.parseDouble(seg[63]));
      r.setDiskwrite(Double.parseDouble(seg[64]));
      if (r.getCpu() > 2)
        res.add(r);
    }
    return res;
  }

}
