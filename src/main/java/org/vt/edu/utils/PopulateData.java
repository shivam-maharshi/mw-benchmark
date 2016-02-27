package org.vt.edu.utils;

import static org.vt.edu.utils.Constant.RELATIVE_PATH;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yahoo.ycsb.WebClient;

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

	private static final String DEFAULT_DATA = "A lot of data. A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.A lot of data.";
	private String fromHostAd;
	private String toHostAd;
	private String toEndPoint;
	private String inputFile;
	private Connection fromCon = null;
	private PreparedStatement fromPst = null;
	private String fromDB = null;
	private String writeFailedUrlPath = null;
	private String readFailedUrlPath = null;
	private long count;
	private List<String> writeFailedUrls = null;
	private List<String> readFailedUrls = null;

	public PopulateData(String fromHostAd, String toHostAd, String toEndPoint, String inputFile, String fromDB,
			String writeFailedUrlPath, String readFailedUrlPath, long readCount) {
		this.fromHostAd = fromHostAd;
		this.toHostAd = toHostAd;
		this.toEndPoint = toEndPoint;
		this.inputFile = inputFile;
		this.count = readCount;
		this.fromDB = fromDB;
		this.writeFailedUrlPath = writeFailedUrlPath;
		this.readFailedUrlPath = readFailedUrlPath;
		this.writeFailedUrls = new ArrayList<>();
		this.readFailedUrls = new ArrayList<>();
	}

	private void initConnection() throws ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format(
					"jdbc:mysql://%s:%d/%s?user=%s&password=%s&characterEncoding=utf-8&useUnicode=true", fromHostAd,
					3306, fromDB, "root", "root");
			fromCon = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void write(String title, String data) {
		System.out.println("Posting data :: URL : " + title + " || Size : " + data.length());
		WebClient w = new WebClient();
		String params = new StringBuilder("section=0&title=").append(title).append("&appendtext=").append(data)
				.append("&token=%2B%5C").toString();
		int responseCode = w.sendPost("http://" + toHostAd + "/" + toEndPoint + "/api.php?action=edit&format=json",
				params);
		if (responseCode == 500)
			writeFailedUrls.add(title);
	}

	private void read(String title) {
		System.out.println("Reading data :: URL : " + title);
		WebClient w = new WebClient();
		int responseCode = w.sendGet("http://" + toHostAd + ":80/" + toEndPoint + "/index.php/" + title);
		if (responseCode == 500)
			readFailedUrls.add(title);
	}

	private String getText(String title) throws UnsupportedEncodingException, SQLException {
		System.out.println("Getting text data :: URL : " + title);
		String sql = "SELECT old_text FROM text t, page p WHERE p.page_latest = t.old_id AND p.page_title = ?";
		fromPst = fromCon.prepareStatement(sql);
		fromPst.setString(1, URLDecoder.decode(title, "UTF-8"));
		ResultSet rs = fromPst.executeQuery();
		if (rs.next())
			return rs.getString(1);
		else
			return DEFAULT_DATA;
	}

	public void execute() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		long start = System.currentTimeMillis();
		System.out.println("Executing script at : " + start);
		initConnection();
		List<String> titles = FileUtil.read(inputFile, count);
		for (String title : titles) {
			String data = getText(title);
			write(title, data);
			read(title);
		}
		FileUtil.write(writeFailedUrls, writeFailedUrlPath);
		FileUtil.write(readFailedUrls, readFailedUrlPath);
		System.out.println("Successfully finished populating data in : " + ((System.currentTimeMillis()-start)/60000) +" mins." );
	}

	public static void main(String[] args) {
		String fromHostAd = "192.168.1.51";
		String toHostAd = "192.168.1.51";
		String toEndPoint = "wiki";
		String inputFile = RELATIVE_PATH + "readtrace.txt";
		String fromDB = "wiki";
		String writeFailedUrlPath = RELATIVE_PATH + "writeFailedTitles.txt";
		String readFailedUrlPath = RELATIVE_PATH + "readFailedTitles.txt";
		long readCount = 1000;
		PopulateData pd = new PopulateData(fromHostAd, toHostAd, toEndPoint, inputFile, fromDB, writeFailedUrlPath,
				readFailedUrlPath, readCount);
		try {
			pd.execute();
		} catch (UnsupportedEncodingException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}