package org.vt.edu.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Import tasks responsible for importing a given list in MediaWiki database
 * using MediaWiki edit APIs.
 * 
 * @author shivam.maharshi
 */
public class ImportTask implements Runnable {
  
  private List<String> titles;
  private String toHostAd;
  private String toEndPoint;
  private Connection fromCon = null;
  private PreparedStatement fromPst = null;
  private RestClient rc = null;
  
  public ImportTask (List<String> titles, String toHostAd, String toEndPoint, Connection fromCon) {
    this.titles = titles;
    this.toHostAd = toHostAd;
    this.toEndPoint = toEndPoint;
    this.fromCon = fromCon;
    this.rc = RestClient.getClient();
  }

  @Override
  public void run() {
    for (int i = 0; i < titles.size(); i++) {
      String data, title = titles.get(i);
      try {
        data = getText(title);
        if (data == null) {
          PopulateData.missingUrls.add(title);
          continue;
        }
        // Retries once.
        int responseCode = write(title, data, i);
        if (responseCode == 500) {
          responseCode = write(title, data, i);
          if (responseCode == 500)
            PopulateData.writeFailedUrls.add(title);
        } else {
          PopulateData.successfulUrls.add(title);
        }
        // Retries once.
//        responseCode = read(title);
//        if (responseCode == 500) {
//          responseCode = read(title);
//          if (responseCode == 500)
//            PopulateData.readFailedUrls.add(title);
//        }
      } catch (SQLException | IOException e) {
        e.printStackTrace();
        PopulateData.missingUrls.add(title);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        PopulateData.illegalUrls.add(title);
      }
    }
  }
  
  public int write(String title, String data, int i) {
    System.out.println(i + " : Posting data :: URL : " + title + " || Size : " + data.length());
    int responseCode;
    try {
      data = URLEncoder.encode(data, "UTF-8");
      String postData = new StringBuilder("section=new&title=").append(title).append("&appendtext=").append(data)
          .append("&token=%2B%5C").toString();
      responseCode = rc.httpPost("http://" + toHostAd + "/" + toEndPoint + "/api.php?action=edit&format=json",
          postData);
    } catch (RestClient.TimeoutException | IOException e) {
      responseCode = 500;
    }
    System.out.println(i + " : Posted data :: URL : " + title + " || Response code : " + responseCode);
    return responseCode;
  }

//  private int read(String title) {
//    System.out.println("Reading data :: URL : " + title);
//    int responseCode;
//    try {
//      responseCode = rc.httpGet("http://" + toHostAd + "/" + toEndPoint + "/index.php/" + title);
//    } catch (RestClient.TimeoutException | IOException e) {
//      responseCode = 500;
//    }
//    return responseCode;
//  }

  private String getText(String title) throws SQLException, IOException, IllegalArgumentException {
    System.out.println("Getting text data :: URL : " + title);
    String sql = "SELECT old_text FROM text t, page p WHERE p.page_latest = t.old_id AND p.page_title = ? LIMIT 1";
    fromPst = fromCon.prepareStatement(sql);
    fromPst.setString(1, URLDecoder.decode(title, "UTF-8"));
    ResultSet rs = fromPst.executeQuery();
    if (rs.next()) {
      Blob text = rs.getBlob(1);
      InputStream is = text.getBinaryStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      br.close();
      is.close();
      return sb.toString();
    }
    return null;
  }
  
}
