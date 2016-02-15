package com.yahoo.ycsb;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
	private static final String CON_TIMEOUT = "con.timeout";
	private static final String READ_TIMEOUT = "read.timeout";
	private static final String EXEC_TIMEOUT = "exec.timeout";
	private static final String URL_PREFIX_DEFAULT = "52.34.20.119/mediawiki";
	private static final String LOG_CALLS = "log.enable";
	private static boolean logCalls = false;
	private static final String HTTP = "http://";
	private static String urlPrefix;
	private static int conTimeout = 15;
	private static int readTimeout = 30;
	private static int execTimeout = 10;
	private static AtomicInteger opsCounter = new AtomicInteger(0);

	@Override
	public void init() throws DBException {
		props = getProperties();
		urlPrefix = props.getProperty(URL_PREFIX, URL_PREFIX_DEFAULT);
		logCalls = Boolean.valueOf(props.getProperty(LOG_CALLS).trim());
		conTimeout = Integer.valueOf(props.getProperty(CON_TIMEOUT, "15"));
		readTimeout = Integer.valueOf(props.getProperty(READ_TIMEOUT, "30"));
		execTimeout = Integer.valueOf(props.getProperty(EXEC_TIMEOUT, "10"));
		execTimeout *= 1000;
	}

	@Override
	public Status read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
		return getStatus(sendGet(HTTP + urlPrefix + "/index.php/" + key));
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
	private int sendGet(String url) {
		int responseCode = 0;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept", "*/*");
			con.setDoOutput(false);
			con.setInstanceFollowRedirects(false);
			con.connect();
			con.setConnectTimeout(conTimeout);
			con.setReadTimeout(readTimeout);
			BufferedReader in;
			responseCode = con.getResponseCode();
			if (responseCode == 200) {
				String inputLine;
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				StringBuffer response = new StringBuffer();
				long start = System.currentTimeMillis();
				while ((inputLine = in.readLine()) != null) {
					if (System.currentTimeMillis() - start > execTimeout) {
						if (logCalls)
							System.out.println(
									"GET URL : " + url + " || Request just exceeded maximum execution time of : "
											+ execTimeout + " seconds.");
						responseCode = 500;
						break;
					}
					response.append(inputLine);
				}
				in.close();
			}
		} catch (IOException e) {
			responseCode = 500;
			e.printStackTrace();
		}
		if (logCalls)
			System.out.println("GET URL : " + url + " || Response Code : " + responseCode + " || Ops Count: "
					+ opsCounter.incrementAndGet());
		return responseCode;
	}

	private String getPostParameters(String title, ByteIterator data) throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer("section=0");
		params.append("&title=").append(title).append("&appendtext=").append(data.toString()).append("&token=%2B%5C");
		return params.toString();
	}

	private int sendPost(String url, String parameters) {
		int responseCode = 200;
		try {
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
				String inputLine;
				long start = System.currentTimeMillis();
				while ((inputLine = in.readLine()) != null) {
					if (System.currentTimeMillis() - start > execTimeout) {
						if (logCalls)
							System.out.println("POST URL : " + url
									+ " || Request  exceeded maximum execution time of : " + execTimeout + " seconds.");
						responseCode = 500;
						break;
					}
				}
				// Can ignore this check.
				// if (response.toString().contains("Success")) {
				// responseCode = 200;
				// }
				in.close();
			}
		} catch (IOException e) {
			responseCode = 500;
			e.printStackTrace();
		}
		if (logCalls)
			System.out.println("POST URL : " + url + " || Response Code : " + responseCode + " || Ops Count: "
					+ opsCounter.incrementAndGet());
		return responseCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((props == null) ? 0 : props.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebClient other = (WebClient) obj;
		if (props == null) {
			if (other.props != null)
				return false;
		} else if (!props.equals(other.props))
			return false;
		return true;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String data = "Miusov, as a man man of breeding and deilcacy, could not but feel some inwrd qualms, when he reached the Father Superior's with Ivan: he felt ashamed of havin lost his temper. He felt that he ought to have disdaimed that despicable wretch, Fyodor Pavlovitch, too much to have been upset by him in Father Zossima's cell, and so to have forgotten himself. Teh monks were not to blame, in any case, he reflceted, on the steps.And if they're decent people here (and the Father Superior, I understand, is a nobleman) why not be friendly and courteous withthem? I won't argue, I'll fall in with everything, I'll win them by politness, and show them that I've nothing to do with that Aesop, thta buffoon, that Pierrot, and have merely been takken in over this affair, just as they have.";
		WebClient w = new WebClient();
		String params = "section=0";
		params += "&title=" + URLEncoder.encode("Αγορά", "UTF-8");
		params += "&appendtext=" + data;
		params += "&token=%2B%5C";
		w.sendPost("http://10.0.0.91/mediawiki2/api.php?action=edit&format=json", params);
	}

}