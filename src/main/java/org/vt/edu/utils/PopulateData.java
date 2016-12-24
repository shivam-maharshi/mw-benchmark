package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;
import static org.vt.edu.utils.Constant.OUTPUT_PATH;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.RestClient;

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
public class PopulateData {

	private String fromHostAd;
	private String toHostAd;
	private String toEndPoint;
	private String inputFile;
	private Connection fromCon = null;
	private PreparedStatement fromPst = null;
	private String fromDB = null;
	private String writeFailedUrlPath = null;
	private String readFailedUrlPath = null;
	private String missingUrlsPath = null;
	private long count;
	private List<String> writeFailedUrls = null;
	private List<String> readFailedUrls = null;
	private List<String> missingUrls = null;

	public PopulateData(String fromHostAd, String toHostAd, String toEndPoint, String inputFile, String fromDB,
			String writeFailedUrlPath, String readFailedUrlPath, String missingUrlsPath, long readCount) {
		this.fromHostAd = fromHostAd;
		this.toHostAd = toHostAd;
		this.toEndPoint = toEndPoint;
		this.inputFile = inputFile;
		this.count = readCount;
		this.fromDB = fromDB;
		this.writeFailedUrlPath = writeFailedUrlPath;
		this.readFailedUrlPath = readFailedUrlPath;
		this.missingUrlsPath = missingUrlsPath;
		this.writeFailedUrls = new ArrayList<>();
		this.readFailedUrls = new ArrayList<>();
		this.missingUrls = new ArrayList<>();
	}

	private void initConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format(
					"jdbc:mysql://%s:%d/%s?user=%s&password=%s&characterEncoding=utf-8&useUnicode=true",
					fromHostAd.split(":")[0], 3306, fromDB, "root", "root");
			fromCon = DriverManager.getConnection(url);
		} catch (SQLException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	private int write(String title, String data) {
		System.out.println("Posting data :: URL : " + title + " || Size : " + data.length());
		RestClient rc = RestClient.getClient();
		int responseCode;
		try {
			data = URLEncoder.encode(data, "UTF-8");
			String postData = new StringBuilder("section=0&title=").append(title).append("&appendtext=").append(data)
					.append("&token=%2B%5C").toString();
			responseCode = rc.httpPost("http://" + toHostAd + "/" + toEndPoint + "/api.php?action=edit&format=json",
					postData);
		} catch (RestClient.TimeoutException | IOException e) {
			responseCode = 500;
		}
		return responseCode;
	}

	private int read(String title) {
		System.out.println("Reading data :: URL : " + title);
		RestClient rc = RestClient.getClient();
		int responseCode;
		try {
			responseCode = rc.httpGet("http://" + toHostAd + "/" + toEndPoint + "/index.php/" + title,
					new HashMap<String, ByteIterator>());
		} catch (RestClient.TimeoutException | IOException e) {
			responseCode = 500;
		}
		return responseCode;
	}

	private String getText(String title) throws SQLException, IOException {
		System.out.println("Getting text data :: URL : " + title);
		String sql = "SELECT old_text FROM text t, page p WHERE p.page_latest = t.old_id AND p.page_title = ? LIMIT 1";
		fromPst = fromCon.prepareStatement(sql);
		fromPst.setString(1, URLDecoder.decode("Σεβαστούπολη", "UTF-8"));
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

	public void execute() {
		long start = System.currentTimeMillis();
		System.out.println("Executing script at : " + start);
		initConnection();
		List<String> titles = FileUtil.read(inputFile, 10000, count);
		for (String title : titles) {
			String data;
			try {
				data = getText(title);
				if(data==null) {
					missingUrls.add(title);
					continue;
				}
				// Retries once.
				int responseCode = write(title, data);
				if (responseCode == 500) {
					responseCode = write(title, data);
					if (responseCode == 500)
						writeFailedUrls.add(title);
				}
				// Retries once.
				responseCode = read(title);
				if (responseCode == 500) {
					responseCode = read(title);
					if (responseCode == 500)
						readFailedUrls.add(title);
				}
			} catch (SQLException | IOException e) {
				e.printStackTrace();
				missingUrls.add(title);
			}
		}
		FileUtil.write(writeFailedUrls, writeFailedUrlPath);
		FileUtil.write(readFailedUrls, readFailedUrlPath);
		FileUtil.write(missingUrls, missingUrlsPath);
		System.out.println("Successfully finished populating data in : "
				+ ((System.currentTimeMillis() - start) / 60000) + " mins.");
	}

	public static void main(String[] args) {
		String fromHostAd = "192.168.1.51:80";
		String toHostAd = "129.114.111.33:8080";
		String toEndPoint = "wiki";
		String inputFile = RELATIVE_PATH + "readtrace.txt";
		String fromDB = "wiki";
		String writeFailedUrlPath = OUTPUT_PATH + "writeFailedTitles.txt";
		String readFailedUrlPath = OUTPUT_PATH + "readFailedTitles.txt";
		String missingUrlsPath = OUTPUT_PATH + "missingTitles.txt";
		long readCount = 10000;
		int argLen = args.length;
		for (int i = 0; i < argLen; i++) {
			if (args[i].startsWith("-from=")) {
				fromHostAd = args[i].split("=")[1];
			} else if (args[i].startsWith("-to=")) {
				toHostAd = args[i].split("=")[1];
			} else if (args[i].startsWith("-toEndPoint=")) {
				toEndPoint = args[i].split("=")[1];
			} else if (args[i].startsWith("-inputFile=")) {
				inputFile = args[i].split("=")[1];
			} else if (args[i].startsWith("-fromDB=")) {
				fromDB = args[i].split("=")[1];
			} else if (args[i].startsWith("-writeFailedFilePath=")) {
				writeFailedUrlPath = args[i].split("=")[1];
			} else if (args[i].startsWith("-readFailedFilePath=")) {
				readFailedUrlPath = args[i].split("=")[1];
			} else if (args[i].startsWith("-missingUrlsFilePath=")) {
				missingUrlsPath = args[i].split("=")[1];
			}  else if (args[i].startsWith("-readCount=")) {
				readCount = Long.valueOf(args[i].split("=")[1]);
			}
		}
		PopulateData pd = new PopulateData(fromHostAd, toHostAd, toEndPoint, inputFile, fromDB, writeFailedUrlPath,
				readFailedUrlPath, missingUrlsPath, readCount);
		pd.execute();
	}

}