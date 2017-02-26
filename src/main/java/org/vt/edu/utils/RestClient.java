package org.vt.edu.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * Class responsible for making web service requests for benchmarking purpose.
 * Using Apache HttpClient over standard Java HTTP API as this is more flexible
 * and provides better functionality. For example HttpClient can automatically
 * handle Redirects and Proxy Authentication which the standard Java API don't.
 * 
 * @author shivam.maharshi
 */
public class RestClient {

  private CloseableHttpClient client;
  private int conTimeout = 10000;
  private int readTimeout = 10000;
  private int execTimeout = 10000;
  public volatile Criteria requestTimedout = new Criteria(false);

  private void init() {
    RequestConfig.Builder requestBuilder = RequestConfig.custom();
    requestBuilder = requestBuilder.setConnectTimeout(conTimeout);
    requestBuilder = requestBuilder.setConnectionRequestTimeout(readTimeout);
    requestBuilder = requestBuilder.setSocketTimeout(readTimeout);
    HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build());
    this.client = clientBuilder.setUserAgent("Mozilla/5.0").setConnectionManagerShared(true).build();
  }

  // Connection is automatically released back in case of an exception.
  public int httpGet(String endpoint) throws IOException, TimeoutException {
    requestTimedout.setSatisfied(false);
    Thread timer = new Thread(new Timer(execTimeout, requestTimedout));
    timer.start();
    int responseCode = 200;
    HttpGet request = new HttpGet(endpoint);
    request.setHeader("Accept", "*/*");
    request.setHeader("Connection", "close");
    CloseableHttpResponse response = client.execute(request);
    responseCode = response.getStatusLine().getStatusCode();
    HttpEntity responseEntity = response.getEntity();
    // If null entity don't bother about connection release.
    if (responseEntity != null) {
      InputStream stream = responseEntity.getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
      StringBuffer responseContent = new StringBuffer();
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (requestTimedout.isSatisfied()) {
          // Must avoid memory leak.
          reader.close();
          stream.close();
          EntityUtils.consumeQuietly(responseEntity);
          response.close();
          client.close();
          throw new TimeoutException();
        }
        responseContent.append(line);
      }
      timer.interrupt();
      reader.close();
      // Closing the input stream will trigger connection release.
      stream.close();
    }
    EntityUtils.consumeQuietly(responseEntity);
    response.close();
    client.close();
    return responseCode;
  }

  // Connection is automatically released back in case of an exception.
  public int httpPost(String endpoint, String postData) throws IOException, TimeoutException {
    requestTimedout.setSatisfied(false);
    Thread timer = new Thread(new Timer(execTimeout, requestTimedout));
    timer.start();
    int responseCode = 200;
    HttpPost request = new HttpPost(endpoint);
    request.setHeader("Accept", "*/*");
    // request.setHeader("Accept-Language", "en-US,en;q=0.5");
    request.setHeader("Content-Type", "application/x-www-form-urlencoded");
    request.setHeader("Connection", "close");
    InputStreamEntity reqEntity = new InputStreamEntity(new ByteArrayInputStream(postData.getBytes()),
        ContentType.APPLICATION_FORM_URLENCODED);
    reqEntity.setChunked(true);
    request.setEntity(reqEntity);
    CloseableHttpResponse response = client.execute(request);
    responseCode = response.getStatusLine().getStatusCode();
    HttpEntity responseEntity = response.getEntity();
    // If null entity don't bother about connection release.
    if (responseEntity != null) {
      InputStream stream = responseEntity.getContent();
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
      StringBuffer responseContent = new StringBuffer();
      String line = "";
      while ((line = reader.readLine()) != null) {
        if (requestTimedout.isSatisfied()) {
          // Must avoid memory leak.
          reader.close();
          stream.close();
          EntityUtils.consumeQuietly(responseEntity);
          response.close();
          client.close();
          throw new TimeoutException();
        }
        responseContent.append(line);
      }
      if (!responseContent.toString().contains("Success"))
        responseCode = 500;
      timer.interrupt();
      reader.close();
      // Closing the input stream will trigger connection release.
      stream.close();
    }
    EntityUtils.consumeQuietly(responseEntity);
    response.close();
    client.close();
    return responseCode;
  }

  public static RestClient getClient() {
    RestClient rc = new RestClient();
    try {
      rc.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return rc;
  }

//  public static void main(String[] args) {
//    String data = "Miusov, as a man man of breeding and deilcacy, could not but feel some inwrd qualms, when he reached the Father Superior's with Ivan: he felt ashamed of havin lost his temper. He felt that he ought to have disdaimed that despicable wretch, Fyodor Pavlovitch, too much to have been upset by him in Father Zossima's cell, and so to have forgotten himself. Teh monks were not to blame, in any case, he reflceted, on the steps.And if they're decent people here (and the Father Superior, I understand, is a nobleman) why not be friendly and courteous withthem? I won't argue, I'll fall in with everything, I'll win them by politness, and show them that I've nothing to do with that Aesop, thta buffoon, that Pierrot, and have merely been takken in over this affair, just as they have.";
//    RestClient rc = getClient();
//    List<String> titles = FileUtil.read(Constant.RELATIVE_PATH + "readtrace.txt", 10000);
//    int responseCode = 0;
//    for (String title : titles) {
//      String postData = new StringBuilder("section=new&title=").append("%CE%95%CE%BA%CF%84%CE%AC%CF%81%CE%B9%CE%BF")
//          .append("&appendtext=").append(data).append("&token=%2B%5C").toString();
//      try {
//        responseCode = rc.httpPost("http://192.168.1.51/sw/api.php?action=edit&format=json", postData);
//      } catch (IOException | TimeoutException e) {
//        e.printStackTrace();
//        responseCode = 500;
//      }
//      System.out.println(
//          "POST Request: " + "http://192.168.1.51:80/sw/index.php/" + title + " | Response Code: " + responseCode);
//      if (responseCode == 500) {
//      }
//    }
//  }

  class Timer implements Runnable {
    private long timeout;
    private Criteria timedout;

    public Timer(long timeout, Criteria timedout) {
      this.timedout = timedout;
      this.timeout = timeout;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(timeout);
        this.timedout.setSatisfied(true);
      } catch (InterruptedException e) {
        // Do nothing.
      }
    }

  }

  class Criteria {
    private boolean isSatisfied;

    public Criteria(boolean isSatisfied) {
      this.isSatisfied = isSatisfied;
    }

    public boolean isSatisfied() {
      return isSatisfied;
    }

    public void setSatisfied(boolean isSatisfied) {
      this.isSatisfied = isSatisfied;
    }
  }

  public class TimeoutException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

}
