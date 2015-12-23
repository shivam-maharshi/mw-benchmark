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

/**
 * Class responsible for making web service requests for benchmarking purpose.
 * 
 * @author shivam.maharshi
 */
public class WebClient extends DB {

	private Properties props;
	private static final String URL_PREFIX = "url.prefix";
	private static final String URL_PREFIX_DEFAULT = "52.34.20.119/mediawiki";
	private static final String HTTP = "http://";
	private static String urlPrefix;

	@Override
	public void init() throws DBException {
		props = getProperties();
		urlPrefix = props.getProperty(URL_PREFIX, URL_PREFIX_DEFAULT);
	}

	@Override
	public Status read(String table, String key, Set<String> fields, HashMap<String, ByteIterator> result) {
		System.out.println("Get title : " + key);
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
		System.out.println("Post title : " + key);
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
			con.setConnectTimeout(10000);
			con.connect();
			responseCode = con.getResponseCode();
			BufferedReader in;
			if (responseCode == 200) {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				in.close();
			}
			// String inputLine;
			// StringBuffer response = new StringBuffer();
			// while ((inputLine = in.readLine()) != null) {
			// response.append(inputLine);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseCode;
	}

	private String getPostParameters(String title, ByteIterator data) throws UnsupportedEncodingException {
		StringBuffer params = new StringBuffer("section=0");
		params.append("&title=").append(URLEncoder.encode(title, "UTF-8")).append("&appendtext=")
				.append(data.toString()).append("&token=%2B%5C");
		return params.toString();
	}

	private int sendPost(String url, String parameters) {
		int responseCode = 500;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			System.out.println(con.getResponseCode());
			if (response.toString().contains("Success")) {
				responseCode = 200;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		System.out.println(data.getBytes().length);

		WebClient w = new WebClient();
		String params = "section=0";
		params += "&title=%CE%95%CE%BB%CE%B5%CF%85%CE%B8%CE%B5%CF%81%CE%BF%CF%84%CE%B5%CE%BA%CF%84%CE%BF%CE%BD%CE%B9%CF%83%CE%BC%CF%8C%CF%82"; // URLEncoder.encode("Î§Ï�Î®ÏƒÏ„Î¿Ï‚_Î”Î¹Î´Î±ÏƒÎºÎ¬Î»Î¿Ï…_(Î¿Ï€Î»Î±Ï�Ï‡Î·Î³ÏŒÏ‚)",
		// "UTF-8");
		params += "&appendtext=" + data + data + data + data;
		params += "&token=%2B%5C";
		w.sendPost("http://52.34.20.119/mediawiki/api.php?action=edit&format=json", params);
	}

}
