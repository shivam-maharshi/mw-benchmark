package org.vt.edu.queue.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.yahoo.ycsb.RestClient.TimeoutException;

/**
 * An HTTP Put call to SiteStory server for caching.
 * 
 * @author shivam.maharshi
 */
public class SiteStoryPut {

	private CloseableHttpClient client;

	public SiteStoryPut() {
		setupClient();
	}

	private void setupClient() {
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setConnectTimeout(10000);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(10000);
		requestBuilder = requestBuilder.setSocketTimeout(10000);
		HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(requestBuilder.build());
		this.client = clientBuilder.setUserAgent("Mozilla/5.0").setConnectionManagerShared(true).build();
	}

	// Connection is automatically released back in case of an exception.
	public int httpPut(String endpoint, String postData) throws IOException, TimeoutException {
		int responseCode = 200;
		HttpPut request = new HttpPut(endpoint);
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
				responseContent.append(line);
			}
			reader.close();
			// Closing the input stream will trigger connection release.
			stream.close();
		}
		EntityUtils.consumeQuietly(responseEntity);
		response.close();
		client.close();
		return responseCode;
	}

	public static void main(String[] args) throws HttpException, IOException {
		SiteStoryPut ssp = new SiteStoryPut();
		String endpoint = "http://192.168.1.52:8080/sitestory/put/http://192.168.1.51/wiki/index.php/Facebook1";
		ssp.httpPut(endpoint, getDummyBody());
	}

	public static String getDummyBody() {
		String bodyonly = "<html><title>Hello World!</title><body>"
				+ "<p><font size=\"14\">Hello World! Page created at  Sun, 30 Jul 2009 </font>" + "</p></body></html>";
		int size = bodyonly.length();
		String req_header = "GET /wiki/index.php/Facebook1 HTTP/1.1\r\n" + "User-Agent:Mozilla/5.0\r\n"
				+ "Referer:http://192.168.1.51/wiki/index.php/Facebook1\r\n" + "Accept:*/*\r\n"
				+ "Accept-Language:en-US,nl;q=0.5\r\n" + "Accept-Encoding:gzip, deflate\r\n" + "Connection:close\r\n"
				+ "Host:192.168.1.51\r\n";

		String res_header = "HTTP/1.1 200 OK\r\nDate: Mon, 30 Jul 2009 14:29:09 GMT\r\nServer: Apache\r\nContent-Length:"
				+ size + "\r\nConnection: close\r\nContent-Type: text/html; charset=UTF-8\r\n";
		String body = req_header + "\r\n" + res_header + "\r\n" + bodyonly;
		return body;
	}

	public void oldPut() throws HttpException, IOException {
		HttpClient mClient = new HttpClient();
		String bodyonly = "<html><title>Hello World!</title><body>"
				+ "<p><font size=\"14\">Hello World! Page created at  Sun, 30 Jul 2009 </font>" + "</p></body></html>";
		int size = bodyonly.length();
		String req_header = "GET /wiki/index.php/Facebook1 HTTP/1.1\r\n" + "User-Agent:Mozilla/5.0\r\n"
				+ "Referer:http://192.168.1.51/wiki/index.php/Facebook1\r\n" + "Accept:*/*\r\n"
				+ "Accept-Language:en-US,nl;q=0.5\r\n" + "Accept-Encoding:gzip, deflate\r\n" + "Connection:close\r\n"
				+ "Host:192.168.1.51\r\n";

		String res_header = "HTTP/1.1 200 OK\r\nDate: Mon, 30 Jul 2009 14:29:09 GMT\r\nServer: Apache\r\nContent-Length:"
				+ size + "\r\nConnection: close\r\nContent-Type: text/html; charset=UTF-8\r\n";
		String body = req_header + "\r\n" + res_header + "\r\n" + bodyonly;
		PutMethod mPut = new PutMethod(
				"http://192.168.1.52:8080/sitestory/timegate/http://192.168.1.51/wiki/index.php/Facebook1");
		mPut.setRequestBody(body);
		mClient.executeMethod(mPut);
		mPut.releaseConnection();
	}

}
