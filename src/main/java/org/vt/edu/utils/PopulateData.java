package org.vt.edu.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.vt.edu.tasks.ImportTask;

/**
 * This class reads a list of titles to be populated in the wiki-mirror. Then
 * for each title it performs the following action. 1. It reads the entry from
 * Text table corresponding with this title to fetch the data from first wiki-
 * mirror. 2. It uses the MediaWiki edit API to create these pages in second
 * wiki-mirror by filling them up with the data fetched in the previous step. 3.
 * Finally it verifies that these Pages are populated properly.
 * 
 * @author shivam.maharshi
 */
public class PopulateData {

  private String user;
  private String password;
  private String fromHostAd;
  private String toHostAd;
  private String toEndPoint;
  private String inputFile;
  private String fromDB = null;
  private String sp = null;
  private String wp = null;
  private String mp = null;
  private String ip = null;
  private long count;
  private long readOffset;
  private int workers;
  private static String PATH = System.getProperty("user.dir") + File.separator;
  private static volatile List<String> success = new ArrayList<>();
  private static volatile List<String> write = new ArrayList<>();
  private static volatile List<String> missing = new ArrayList<>();
  private static volatile List<String> illegal = new ArrayList<>();

  public PopulateData(String u, String p, String from, String to, String ep, String in, String db, String sp, String wp,
      String mp, String ip, long rc, long ro, int w) {
    this.user = u;
    this.password = p;
    this.fromHostAd = from;
    this.toHostAd = to;
    this.toEndPoint = ep;
    this.inputFile = in;
    this.readOffset = ro;
    this.count = rc;
    this.workers = w;
    this.fromDB = db;
    this.sp = sp;
    this.wp = wp;
    this.mp = mp;
    this.ip = ip;
  }

  private Connection getConnection() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&characterEncoding=utf-8&useUnicode=true",
          fromHostAd.split(":")[0], 3306, fromDB, user, password);
      return DriverManager.getConnection(url);
    } catch (SQLException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  public void execute() {
    long start = System.currentTimeMillis();
    System.out.println("Executing script at : " + start);
    List<String> titles = FileUtil.read(inputFile, readOffset, count);
    int size = titles.size() / workers;
    ExecutorService executor = Executors.newFixedThreadPool(workers);

    for (int i = 0; i < workers; i++)
      executor.execute(new ImportTask(titles.subList(i * size, Math.max(titles.size(), (i + 1) * size)), toHostAd,
          toEndPoint, getConnection(), success, write, missing, illegal));

    executor.shutdown();

    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      // Wait!
    }

    FileUtil.write(success, sp);
    FileUtil.write(write, wp);
    FileUtil.write(missing, mp);
    FileUtil.write(illegal, ip);

    System.out.println(
        "Successfully finished populating data in : " + ((System.currentTimeMillis() - start) / 60000) + " mins.");
  }

  /**
   * Usage: sudo java -Xms8096m -Xmx12086m -cp "mw-benchmark-0.0.jar"
   * org.vt.edu.utils.PopulateData -u=root -p=root -from=192.168.1.51 -to =
   * 192.168.1.51 -ep=sw -in=/home/shivam/readtrace_full.txt -db=wiki
   * -sp=/home/shivam/sf.txt -wp=/home/shivam/wf.txt -mp=/home/shivam/mf.txt
   * -ip=/home/shivam/if.txt -rc=100000000 -ro=0 -w=8
   */
  public static void main(String[] args) {
    System.out.println(System.getProperty("user.dir"));
    String u = "root";
    String p = "root";
    String from = "192.168.1.51";
    String to = "192.168.1.51";
    String ep = "mw";

    String in = PATH + "readtrace.txt";
    String db = "wiki";
    String sp = PATH + "success.txt";
    String wp = PATH + "write.txt";
    String mp = PATH + "missin.txt";
    String ip = PATH + "illegal.txt";
    long rc = Integer.MAX_VALUE;
    long ro = 0;
    int w = 8;
    int argLen = args.length;
    for (int i = 0; i < argLen; i++) {
      if (args[i].startsWith("-u=")) {
        u = args[i].split("=")[1];
      } else if (args[i].startsWith("-p=")) {
        p = args[i].split("=")[1];
      } else if (args[i].startsWith("-from=")) {
        from = args[i].split("=")[1];
      } else if (args[i].startsWith("-to=")) {
        to = args[i].split("=")[1];
      } else if (args[i].startsWith("-ep=")) {
        ep = args[i].split("=")[1];
      } else if (args[i].startsWith("-in=")) {
        in = args[i].split("=")[1];
      } else if (args[i].startsWith("-db=")) {
        db = args[i].split("=")[1];
      } else if (args[i].startsWith("-sp=")) {
        sp = args[i].split("=")[1];
      } else if (args[i].startsWith("-wp=")) {
        wp = args[i].split("=")[1];
      } else if (args[i].startsWith("-mp=")) {
        mp = args[i].split("=")[1];
      } else if (args[i].startsWith("-ip=")) {
        ip = args[i].split("=")[1];
      } else if (args[i].startsWith("-rc=")) {
        rc = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-ro=")) {
        ro = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-w=")) {
        w = Integer.valueOf(args[i].split("=")[1]);
      }
    }
    PopulateData pd = new PopulateData(u, p, from, to, ep, in, db, sp, wp, mp, ip, rc, ro, w);
    pd.execute();
  }

}