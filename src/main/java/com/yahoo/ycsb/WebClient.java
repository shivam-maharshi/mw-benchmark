package com.yahoo.ycsb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class responsible for making web service requests for benchmarking purpose.
 * 
 * @author shivam.maharshi
 */
public class WebClient extends DB {

	private Properties props;
	private static final String URL_PREFIX = "url.prefix";
	private static final String CON_TIMEOUT = "timeout.con";
	private static final String READ_TIMEOUT = "timeout.read";
	private static final String EXEC_TIMEOUT = "timeout.exec";
	private static final String LOG_ENABLED = "log.enable";
	private static final String URL_PREFIX_DEFAULT = "192.168.1.51/mediawiki";
	private static boolean logCalls = true;
	private static final String HTTP = "http://";
	private static String urlPrefix;
	private static int conTimeout = 10000;
	private static int readTimeout = 10000;
	private static int execTimeout = 10000;
	public volatile Criteria requestTimedout = new Criteria(false);
	private static AtomicInteger opsCounter = new AtomicInteger(0);

	@Override
	public void init() throws DBException {
		props = getProperties();
		urlPrefix = props.getProperty(URL_PREFIX, URL_PREFIX_DEFAULT);
		logCalls = Boolean.valueOf(props.getProperty(LOG_ENABLED).trim());
		conTimeout = Integer.valueOf(props.getProperty(CON_TIMEOUT, "10")) * 1000;
		readTimeout = Integer.valueOf(props.getProperty(READ_TIMEOUT, "10")) * 1000;
		execTimeout = Integer.valueOf(props.getProperty(EXEC_TIMEOUT, "10")) * 1000;
	}

	@Override
	public Status read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
		//return getStatus(sendGet(HTTP + urlPrefix + "/index.php/" + key));
		return getStatus(sendGet(HTTP + urlPrefix + key));
	}

	private Status getStatus(int responseCode) {
		if (responseCode / 100 == 5) {
			return Status.ERROR;
		}
		return Status.OK;
	}

	@Override
	public Status scan(String table, String startkey, int recordcount, Set<String> fields,
			Vector<HashMap<String, ByteIterator>> result) {
		return Status.OK;
	}

	@Override
	public Status update(String table, String key, HashMap<String, ByteIterator> values) {
		// Not to be used.
		return Status.OK;
	}

	@Override
	public Status insert(String table, String key, HashMap<String, ByteIterator> values) {
		String postUrl = "/api.php?action=edit&format=json";
		String postParams;
		try {
			postParams = getPostParameters(key, values.get("field0"));
			return getStatus(sendPost(HTTP + urlPrefix + postUrl, postParams));
		} catch (UnsupportedEncodingException e) {
			return Status.ERROR;
		}
	}

	@Override
	public Status delete(String table, String key) {
		return Status.OK;
	}

	// Returns response code for verification.
	public int sendGet(String url) {
		int opsCount = opsCounter.incrementAndGet();
		int responseCode = 0;
		try {
			requestTimedout.setSatisfied(false);
			Thread timer = new Thread(new Timer(execTimeout, requestTimedout));
			timer.start();
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept", "*/*");
			con.setDoOutput(false);
			con.setInstanceFollowRedirects(false);
			con.setConnectTimeout(conTimeout);
			con.setReadTimeout(readTimeout);
			con.connect();
			BufferedReader in;
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				while (in.readLine() != null) {
					if(requestTimedout.isSatisfied())
						throw new TimeoutException();
				}
				in.close();
			}
			timer.interrupt();
		} catch (TimeoutException e) {
			responseCode = 500;
			if (logCalls)
				System.out.println("GET URL : " + url + " || Request exceeded maximum execution time of : "
						+ execTimeout + " ms.");
		} catch (IOException e) {
			e.printStackTrace();
			responseCode = 500;
		} 
		if (logCalls)
			System.out
					.println("GET URL : " + url + " || Response Code : " + responseCode + " || Ops Count: " + opsCount);
		return responseCode;
	}

	private String getPostParameters(String title, ByteIterator data) throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer("section=0");
		params.append("&title=").append(title).append("&appendtext=").append(data.toString()).append("&token=%2B%5C");
		return params.toString();
	}

	public int sendPost(String url, String parameters) {
		int responseCode = 200;
		try {
			requestTimedout.setSatisfied(false);
			Thread timer = new Thread(new Timer(execTimeout, requestTimedout));
			timer.start();
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setDoOutput(true);
			con.setConnectTimeout(conTimeout);
			con.setReadTimeout(readTimeout);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = in.readLine()) != null) {
					sb.append(line);
					if(requestTimedout.isSatisfied())
						throw new TimeoutException();
				}
				System.out.println(sb.toString());
				if (!sb.toString().contains("Success"))
					responseCode = 500;
				in.close();
			}
			timer.interrupt();
		} catch (IOException e) {
			responseCode = 500;
			e.printStackTrace();
		} catch (TimeoutException e) {
			responseCode = 500;
			if (logCalls)
				System.out.println("POST URL : " + url + " || Request exceeded maximum execution time of : "
						+ execTimeout + " seconds.");
		}
		if (logCalls)
			System.out.println("POST URL : " + url + " || Response Code : " + responseCode + " || Ops Count: "
					+ opsCounter.incrementAndGet());
		return responseCode;
	}

	private static String generateData(int size) {
		StringBuilder sb = new StringBuilder();
		while (size > 0) {
			sb.append("a");
			size--;
		}
		return sb.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		WebClient w = new WebClient();
//		String data = generateData(25000);
//		for(int i=6; i<11; i++) {
//			String params = "section=0&title=" + URLEncoder.encode(i+"", "UTF-8")+"&appendtext=" + data+"&token=%2B%5C";
//			w.sendPost("http://192.168.1.51/wiki/api.php?action=edit&format=json", params);
//		}
		w.sendGet("http://192.168.1.51/wiki/index.php/1");
	}

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
	
	public Criteria (boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}

	public boolean isSatisfied() {
		return isSatisfied;
	}

	public void setSatisfied(boolean isSatisfied) {
		this.isSatisfied = isSatisfied;
	}
	
}

class TimeoutException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
}