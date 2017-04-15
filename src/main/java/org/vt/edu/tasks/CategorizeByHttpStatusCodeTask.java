package org.vt.edu.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.yahoo.ycsb.RestClient;

/**
 * This tasks iterates over the give URL trace and stores all the different HTTP
 * status codes in the given map.
 * 
 * @author shivam.maharshi
 */
public class CategorizeByHttpStatusCodeTask implements Runnable {

  private String prefix;
  private List<String> titles;
  private Map<Integer, List<String>> rsMap;
  private RestClient rc = null;

  public CategorizeByHttpStatusCodeTask(String hostAd, List<String> titles, Map<Integer, List<String>> rsMap) {
    this.prefix = "http://" + hostAd + "/index.php/";
    this.titles = titles;
    this.rsMap = rsMap;
    this.rc = RestClient.getClient();
  }

  @Override
  public void run() {
    for (String title : titles) {
      int responseCode;
      System.out.println("Categorizing URL : " + prefix + title);
      try {
        responseCode = rc.httpGet(prefix + title, null);
      } catch (RestClient.TimeoutException | IOException e) {
        responseCode = 500;
      }
      if (rsMap.get(responseCode) == null)
        rsMap.put(responseCode, new ArrayList<String>());

      rsMap.get(responseCode).add(title);
    }
  }

}
