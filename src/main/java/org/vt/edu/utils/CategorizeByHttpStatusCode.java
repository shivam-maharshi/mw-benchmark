package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.OUTPUT_PATH;
import static org.vt.edu.utils.Constant.RELATIVE_PATH;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.vt.edu.tasks.CategorizeByHttpStatusCodeTask;

/**
 * This scripts iterates over the give URL trace bound by the read count and
 * stores all the different HTTP status codes in their respective files. It is a
 * multi-threaded script with configurable worker threads which lead to
 * drastically fast categorization.
 * 
 * @author shivam.maharshi
 */
public class CategorizeByHttpStatusCode {

  private long count;
  private String hostAd;
  private String input;
  private String output;
  private long offset;
  private int workers;
  private volatile Map<Integer, List<String>> rsMap;

  public CategorizeByHttpStatusCode(String hostAd, String input, String output, long offset, long count, int workers) {
    this.hostAd = hostAd;
    this.input = input;
    this.output = output;
    this.offset = offset;
    this.workers = workers;
    this.count = count;
    this.rsMap = new ConcurrentHashMap<>();
  }

  public void execute() {
    long start = System.currentTimeMillis();
    System.out.println("Executing script at : " + start);
    List<String> titles = FileUtil.read(input, offset, count);
    int size = titles.size() / workers;
    ExecutorService executor = Executors.newFixedThreadPool(workers);

    for (int i = 0; i < workers; i++)
      executor.execute(new CategorizeByHttpStatusCodeTask(hostAd,
          titles.subList(i * size, Math.max((i + 1) * size, titles.size())), rsMap));

    executor.shutdown();

    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      // Wait!
    }

    writeOutput(rsMap, output);
    System.out.println(
        "Successfully finished categorizing URLs in : " + ((System.currentTimeMillis() - start) / 60000) + " mins.");
  }

  private void writeOutput(Map<Integer, List<String>> map, String outputFolder) {
    Set<Entry<Integer, List<String>>> set = map.entrySet();
    for (Entry<Integer, List<String>> entry : set) {
      Integer code = entry.getKey();
      List<String> list = entry.getValue();
      FileUtil.write(list, outputFolder + code + ".txt");
    }
  }

  /*
   * Sample Command: sudo java -Xms8096m -Xmx12086m -cp "mw-benchmark-0.0.jar"
   * org.vt.edu.utils.CategorizeByHttpStatusCode -ad=192.168.1.51:80/wiki
   * -input=~/development/benchmarking/input.txt
   * -output=~/development/benchmarking/ -count=10000 -offset=0 -workers=4
   */
  public static void main(String[] args) {
    String hostAd = "192.168.1.51:80/wiki";
    String input = RELATIVE_PATH + "readtrace.txt";
    String output = OUTPUT_PATH;
    int workers = 4;
    long offset = 0;
    long count = Long.MAX_VALUE;
    int argLen = args.length;
    for (int i = 0; i < argLen; i++) {
      if (args[i].startsWith("-ad=")) {
        hostAd = args[i].split("=")[1];
      } else if (args[i].startsWith("-input=")) {
        input = args[i].split("=")[1];
      } else if (args[i].startsWith("-output=")) {
        output = args[i].split("=")[1];
      } else if (args[i].startsWith("-count=")) {
        count = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-offset=")) {
        offset = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-workers=")) {
        workers = Integer.valueOf(args[i].split("=")[1]);
      }
    }
    CategorizeByHttpStatusCode c = new CategorizeByHttpStatusCode(hostAd, input, output, offset, count, workers);
    c.execute();
  }

}
