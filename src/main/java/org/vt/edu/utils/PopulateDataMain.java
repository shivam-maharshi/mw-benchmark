package org.vt.edu.utils;

import static org.vt.edu.Constant.OUTPUT_PATH;
import static org.vt.edu.Constant.RELATIVE_PATH;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class reads a list of titles to be populated in the Wiki mirror. Then
 * for each title it performs the following action. 1. It reads the entry from
 * Text table corresponding with this title to fetch the data from first wiki
 * mirror. 2. It uses the MediaWiki edit API to create these pages in second
 * wiki mirror by filling them up with the data fetched in the previous step. 3.
 * Finally it verifies that these Pages are populated properly.
 * 
 * @author shivam.maharshi
 */
public class PopulateDataMain {

  private String user;
  private String password;
  private String fromHostAd;
  private String toHostAd;
  private String toEndPoint;
  private String inputFile;
  private String fromDB = null;
  private String successfulUrlsPath = null;
  private String writeFailedUrlPath = null;
  private String readFailedUrlPath = null;
  private String missingUrlsPath = null;
  private String illegalUrlsPath = null;
  private long count;
  private long readOffset;
  private int workers;
  static volatile List<String> successfulUrls = new ArrayList<>();
  static volatile List<String> writeFailedUrls = new ArrayList<>();
  static volatile List<String> readFailedUrls = new ArrayList<>();
  static volatile List<String> missingUrls = new ArrayList<>();
  static volatile List<String> illegalUrls = new ArrayList<>();

  public PopulateDataMain(String user, String password, String fromHostAd, String toHostAd, String toEndPoint,
      String inputFile, String fromDB, String successfulUrlsPath, String writeFailedUrlPath, String readFailedUrlPath, String missingUrlsPath,
      String illegalUrlsPath, long readCount, long readOffset, int workers) {
    this.user = user;
    this.password = password;
    this.fromHostAd = fromHostAd;
    this.toHostAd = toHostAd;
    this.toEndPoint = toEndPoint;
    this.inputFile = inputFile;
    this.readOffset = readOffset;
    this.count = readCount;
    this.workers = workers;
    this.fromDB = fromDB;
    this.successfulUrlsPath = successfulUrlsPath;
    this.writeFailedUrlPath = writeFailedUrlPath;
    this.readFailedUrlPath = readFailedUrlPath;
    this.missingUrlsPath = missingUrlsPath;
    this.illegalUrlsPath = illegalUrlsPath;
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
      executor.execute(new ImportTask(titles.subList(i * size, (i + 1) * size), toHostAd, toEndPoint, getConnection()));
    
    executor.shutdown();
    
    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      // Wait!
    }
    
    FileUtil.write(successfulUrls, successfulUrlsPath);
    FileUtil.write(writeFailedUrls, writeFailedUrlPath);
    FileUtil.write(readFailedUrls, readFailedUrlPath);
    FileUtil.write(missingUrls, missingUrlsPath);
    FileUtil.write(illegalUrls, illegalUrlsPath);
    
    System.out.println(
        "Successfully finished populating data in : " + ((System.currentTimeMillis() - start) / 60000) + " mins.");
  }

  /**
   * Usage: sudo java -Xms8096m -Xmx12086m -cp "wiki-import.jar"
   * org.vt.edu.PopulateData -user=root -password=root -fromHostAd=192.168.1.51
   * -toHostAd = 192.168.1.51 -toEndPoint=sw
   * -inputFile=/home/shivam/readtrace_full.txt -fromDB=wiki
   * -writeFailedFilePath=/home/shivam/wf.txt
   * -readFailedFilePath=/home/shivam/rf.txt
   * -missingUrlsFilePath=/home/shivam/mf.txt
   * -illegalUrlsFilePath=/home/shivam/if.txt -readCount=100000000 -readOffset=0
   */
  public static void main(String[] args) {
    // PopulateData pd = new PopulateData(null, "192.168.1.51:80", "testwiki",
    // null, null, null, null, null, 0);
    // int rc = pd.write("Google", "Google is the biggest software company.");
    String user = "root";
    String password = "root";
    String fromHostAd = "192.168.1.51";
    String toHostAd = "192.168.1.51";
    String toEndPoint = "mw";
    String inputFile = RELATIVE_PATH + "readtrace.txt";
    String fromDB = "wiki";
    String successfulUrlsPath = OUTPUT_PATH + "successfulTitles.txt";
    String writeFailedUrlPath = OUTPUT_PATH + "writeFailedTitles.txt";
    String readFailedUrlPath = OUTPUT_PATH + "readFailedTitles.txt";
    String missingUrlsPath = OUTPUT_PATH + "missingTitles.txt";
    String illegalUrlsPath = OUTPUT_PATH + "illegalTitles.txt";
    long readCount = Integer.MAX_VALUE;
    long readOffset = 0;
    int workers = 8;
    int argLen = args.length;
    for (int i = 0; i < argLen; i++) {
      if (args[i].startsWith("-user=")) {
        user = args[i].split("=")[1];
      } else if (args[i].startsWith("-password=")) {
        password = args[i].split("=")[1];
      } else if (args[i].startsWith("-from=")) {
        fromHostAd = args[i].split("=")[1];
      } else if (args[i].startsWith("-to=")) {
        toHostAd = args[i].split("=")[1];
      } else if (args[i].startsWith("-toEndPoint=")) {
        toEndPoint = args[i].split("=")[1];
      } else if (args[i].startsWith("-inputFile=")) {
        inputFile = args[i].split("=")[1];
      } else if (args[i].startsWith("-fromDB=")) {
        fromDB = args[i].split("=")[1];
      } else if (args[i].startsWith("-successfulUrlsFilePath=")) {
        successfulUrlsPath = args[i].split("=")[1];
      } else if (args[i].startsWith("-writeFailedFilePath=")) {
        writeFailedUrlPath = args[i].split("=")[1];
      } else if (args[i].startsWith("-readFailedFilePath=")) {
        readFailedUrlPath = args[i].split("=")[1];
      } else if (args[i].startsWith("-missingUrlsFilePath=")) {
        missingUrlsPath = args[i].split("=")[1];
      } else if (args[i].startsWith("-illegalUrlsFilePath=")) {
        illegalUrlsPath = args[i].split("=")[1];
      } else if (args[i].startsWith("-readCount=")) {
        readCount = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-readOffset=")) {
        readOffset = Long.valueOf(args[i].split("=")[1]);
      } else if (args[i].startsWith("-workers=")) {
        workers = Integer.valueOf(args[i].split("=")[1]);
      }
    }
    PopulateDataMain pd = new PopulateDataMain(user, password, fromHostAd, toHostAd, toEndPoint, inputFile, fromDB,
        successfulUrlsPath, writeFailedUrlPath, readFailedUrlPath, missingUrlsPath, illegalUrlsPath, readCount,
        readOffset, workers);
    pd.execute();
  }

}