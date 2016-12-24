package com.yahoo.ycsb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.vt.edu.utils.Constant;
import org.vt.edu.utils.FileUtil;

/**
 * Class responsible for making web service requests for benchmarking purpose.
 * Using Apache HttpClient over standard Java HTTP API as this is more flexible
 * and provides better functionality. For example HttpClient can automatically
 * handle Redirects and Proxy Authentication which the standard Java API don't.
 * 
 * @author shivam.maharshi
 */
public class RestClient extends DB {

	private static final String URL_PREFIX = "url.prefix";
	private static final String CON_TIMEOUT = "timeout.con";
	private static final String READ_TIMEOUT = "timeout.read";
	private static final String EXEC_TIMEOUT = "timeout.exec";
	private static final String LOG_ENABLED = "log.enable";
	private static boolean logEnabled = true;
	private String urlPrefix;
	private Properties props;
	private CloseableHttpClient client;
	private static int conTimeout = 10000;
	private static int readTimeout = 10000;
	private static int execTimeout = 10000;
	public volatile Criteria requestTimedout = new Criteria(false);

	@Override
	public void init() throws DBException {
		props = getProperties();
		urlPrefix = props.getProperty(URL_PREFIX, "");
		conTimeout = Integer.valueOf(props.getProperty(CON_TIMEOUT, "10")) * 1000;
		readTimeout = Integer.valueOf(props.getProperty(READ_TIMEOUT, "10")) * 1000;
		execTimeout = Integer.valueOf(props.getProperty(EXEC_TIMEOUT, "10")) * 1000;
		logEnabled = Boolean.valueOf(props.getProperty(LOG_ENABLED, "false").trim());
		setupClient();
	}

	private void setupClient() {
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setConnectTimeout(conTimeout);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(readTimeout);
		requestBuilder = requestBuilder.setSocketTimeout(readTimeout);
		HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build());
		this.client = clientBuilder.setUserAgent("Mozilla/5.0").setConnectionManagerShared(true).build();
	}

	@Override
	public Status read(String table, String endpoint, Set<String> fields, HashMap<String, ByteIterator> result) {
		int responseCode;
		try {
			responseCode = httpGet(urlPrefix+"index.php/" + endpoint, result);
		} catch (Exception e) {
			e.printStackTrace();
			responseCode = handleExceptions(e);
			System.err.println("GET Request: " + urlPrefix +"index.php/"+ endpoint + " | Response Code: " + responseCode);
		}
		if (logEnabled)
			System.out.println("GET Request: " + urlPrefix +"index.php/"+ endpoint + " | Response Code: " + responseCode);
		return getStatus(responseCode);
	}

	@Override
	public Status insert(String table, String endpoint, HashMap<String, ByteIterator> values) {
		int responseCode;
		try {
			String postParams = getPostParameters(endpoint, values.get("field0"));
			responseCode = httpPost(urlPrefix + "api.php?action=edit&format=json", postParams);
		} catch (Exception e) {
			e.printStackTrace();
			responseCode = handleExceptions(e);
			System.err.println("POST Request: " + urlPrefix + "api.php?action=edit&format=json"+ " | Response Code: " + responseCode);
		}
		if (logEnabled)
			System.out.println("POST Request: " + urlPrefix + "api.php?action=edit&format=json" + " | Response Code: " + responseCode);
		return getStatus(responseCode);
	}

	@Override
	public Status delete(String table, String endpoint) {
		int responseCode;
		try {
			responseCode = httpDelete(urlPrefix + endpoint);
		} catch (Exception e) {
			e.printStackTrace();
			responseCode = handleExceptions(e);
			System.err.println("DELETE Request: " + urlPrefix + endpoint + " | Response Code: " + responseCode);
		}
		if (logEnabled)
			System.out.println("DELETE Request: " + urlPrefix + endpoint + " | Response Code: " + responseCode);
		return getStatus(responseCode);
	}

	@Override
	public Status update(String table, String key, HashMap<String, ByteIterator> values) {
		System.out.println("Update not implemented.");
		return Status.OK;
	}

	@Override
	public Status scan(String table, String startkey, int recordcount, Set<String> fields,
			Vector<HashMap<String, ByteIterator>> result) {
		System.out.println("Scan operation is not supported for RESTFul Web Services client.");
		return Status.OK;
	}

	private Status getStatus(int responseCode) {
		if (responseCode / 100 == 5) {
			return Status.ERROR;
		}
		return Status.OK;
	}

	private int handleExceptions(Exception e) {
		e.printStackTrace();
		if (e instanceof ClientProtocolException)
			return 400;
		return 500;
	}

	// Connection is automatically released back in case of an exception.
	public int httpGet(String endpoint, HashMap<String, ByteIterator> result) throws IOException, TimeoutException {
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
			result.put("response", new StringByteIterator(responseContent.toString()));
			reader.close();
			// Closing the input stream will trigger connection release.
			stream.close();
		}
		EntityUtils.consumeQuietly(responseEntity);
		response.close();
		client.close();
		return responseCode;
	}
	
	private String getPostParameters(String title, ByteIterator data) throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer("section=0");
		params.append("&title=").append(title).append("&appendtext=").append(data.toString()).append("&token=%2B%5C");
		return params.toString();
	}

	// Connection is automatically released back in case of an exception.
	public int httpPost(String endpoint, String postData) throws IOException, TimeoutException {
		requestTimedout.setSatisfied(false);
		Thread timer = new Thread(new Timer(execTimeout, requestTimedout));
		timer.start();
		int responseCode = 200;
		HttpPost request = new HttpPost(endpoint);
		request.setHeader("Accept", "*/*");
		request.setHeader("Accept-Language", "en-US,en;q=0.5");
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

	// Connection is automatically released back in case of an exception.
	private int httpDelete(String endpoint) throws IOException {
		int responseCode = 200;
		HttpDelete request = new HttpDelete(endpoint);
		CloseableHttpResponse response = client.execute(request);
		responseCode = response.getStatusLine().getStatusCode();
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

	public static void main(String[] args) {
//		String data = "Miusov, as a man man of breeding and deilcacy, could not but feel some inwrd qualms, when he reached the Father Superior's with Ivan: he felt ashamed of havin lost his temper. He felt that he ought to have disdaimed that despicable wretch, Fyodor Pavlovitch, too much to have been upset by him in Father Zossima's cell, and so to have forgotten himself. Teh monks were not to blame, in any case, he reflceted, on the steps.And if they're decent people here (and the Father Superior, I understand, is a nobleman) why not be friendly and courteous withthem? I won't argue, I'll fall in with everything, I'll win them by politness, and show them that I've nothing to do with that Aesop, thta buffoon, that Pierrot, and have merely been takken in over this affair, just as they have.";
//		String postData = new StringBuilder("section=new&title=")
//				.append("%CE%95%CE%BE%CE%AD%CE%B3%CE%B5%CF%81%CF%83%CE%B7_%CF%84%CE%BF%CF%85_%CE%A0%CE%BF%CE%BB%CF%85%CF%84%CE%B5%CF%87%CE%BD%CE%B5%CE%AF%CE%BF%CF%85")
//				.append("&appendtext=").append(data).append("&token=%2B%5C").toString();
		RestClient rc = getClient();
		HashMap<String, ByteIterator> result = new HashMap<String, ByteIterator>();
		List<String> titles = FileUtil.read(Constant.RELATIVE_PATH+"readtrace.txt",10000);
		int responseCode = 0;
		for(String title: titles) {
			try {
				responseCode = rc.httpGet("http://192.168.1.51/wiki/index.php/",result);
			} catch(IOException | TimeoutException e) {
				e.printStackTrace();
				responseCode = 500;
			}
			System.out.println("GET Request: " + "http://192.168.1.51:80/wiki/index.php/"+title + " | Response Code: " + responseCode);
			if(responseCode==500) {
			}
		}
		// rc.httpPost("http://192.168.1.51:8080/wiki/api.php?action=edit&format=json", postData);
	}

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
