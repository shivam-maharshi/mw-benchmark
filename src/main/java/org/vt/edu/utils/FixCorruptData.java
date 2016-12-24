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
 * This class reads a list of titles to be fixed in the Wiki mirror database.
 * Then for each title it performs the following action. 1. It deletes the entry
 * from Text table corresponding with this title. 2. It fetches the page length
 * from the page table for this title. 3. It uses the MediaWiki edit API to
 * recreate these pages by filling them up with the garbage data of the same
 * size as before. 4. Finally it verifies that these Pages are corrupt no more.
 * 
 * @author shivam.maharshi
 */
public class FixCorruptData {

	private String hostAd;
	private String inputFile;
	private Connection con = null;
	private PreparedStatement pst = null;

	public FixCorruptData(String hostAd, String inputFile) {
		this.hostAd = hostAd;
		this.inputFile = inputFile;
	}

	private void initConnection() throws ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = String.format(
					"jdbc:mysql://%s:%d/%s?user=%s&password=%s&characterEncoding=utf-8&" + "useUnicode=true", hostAd,
					3306, "wiki", "root", "root");
			con = DriverManager.getConnection(url);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void write(List<String> titles, List<Integer> sizes) {
		for (int i = 0; i < titles.size(); i++)
			write(titles.get(i), sizes.get(i));
	}

	private void write(String title, int len) {
		System.out.println("Posting data :: URL : " + title + " || Size : " + len);
		WebClient w = new WebClient();
		String params = "section=0";
		params += "&title=" + title;
		params += "&appendtext=" + generateData(len);
		params += "&token=%2B%5C";
		w.sendPost("http://" + hostAd + "/mediawiki/api.php?action=edit&format=json", params);
	}

	private void read(List<String> titles) {
		for (String title : titles)
			read(title);
	}

	private void read(String title) {
		System.out.println("Reading data :: URL : " + title);
		WebClient w = new WebClient();
		w.sendGet("http://" + hostAd + ":80/mediawiki/index.php/" + title);
	}

	private String generateData(int size) {
		StringBuilder sb = new StringBuilder();
		while (size > 0) {
			sb.append("a");
			size--;
		}
		return sb.toString();
	}

	private List<Integer> getPageLen(List<String> titles) throws UnsupportedEncodingException, SQLException {
		List<Integer> pageSizes = new ArrayList<>();
		int count = 1;
		for (String title : titles) {
			System.out.println("URL Count : " + count++);
			pageSizes.add(getPageLen(title));
		}
		return pageSizes;
	}

	private int getPageLen(String title) throws UnsupportedEncodingException, SQLException {
		System.out.println("Getting page length :: URL : " + title);
		String sql = "SELECT page_len FROM page WHERE page_title = ?";
		pst = con.prepareStatement(sql);
		pst.setString(1, URLDecoder.decode(title, "UTF-8"));
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			return rs.getInt(1);
		else
			return 2000; // Default length.
	}

	private List<Integer> getRevisionId(List<String> titles) throws UnsupportedEncodingException, SQLException {
		List<Integer> revisions = new ArrayList<>();
		for (String title : titles) {
			revisions.add(getRevisionId(title));
		}
		return revisions;
	}

	private Integer getRevisionId(String title) throws UnsupportedEncodingException, SQLException {
		System.out.println("Getting revision id :: URL : " + title);
		String sql = "SELECT page_latest FROM page WHERE page_title = ?";
		pst = con.prepareStatement(sql);
		pst.setString(1, URLDecoder.decode(title, "UTF-8"));
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			return rs.getInt(1);
		else
			return 0;
	}

	private void deleteAll(List<String> titles, List<Integer> revisionIds)
			throws SQLException, UnsupportedEncodingException {
		for (int i = 0; i < titles.size(); i++)
			deleteAll(titles.get(i), revisionIds.get(i));
	}

	private void deleteAll(String title, Integer revisionId) throws SQLException, UnsupportedEncodingException {
		System.out.println("Deleting data :: URL : " + title + " || RevisionId : " + revisionId);
		String sql = "DELETE FROM page WHERE page_title = (?)";
		pst = con.prepareStatement(sql);
		pst.setString(1, URLDecoder.decode(title, "UTF-8"));
		pst.executeUpdate();
		sql = "DELETE FROM revision WHERE rev_text_id = (?) ";
		pst = con.prepareStatement(sql);
		pst.setInt(1, revisionId);
		pst.executeUpdate();
		sql = "DELETE FROM text WHERE old_id = (?) ";
		pst = con.prepareStatement(sql);
		pst.setInt(1, revisionId);
		pst.executeUpdate();
	}

	public void execute() throws UnsupportedEncodingException, SQLException, ClassNotFoundException {
		initConnection();
		List<String> titles = FileUtil.read(inputFile);
		List<Integer> sizes = getPageLen(titles);
		List<Integer> revIds = getRevisionId(titles);
		deleteAll(titles, revIds);
		write(titles, sizes);
		read(titles);
	}

	public static void main(String[] args) {
		FixCorruptData f = new FixCorruptData("192.168.1.51", RELATIVE_PATH + "corrupturls.txt");
		try {
			f.execute();
		} catch (UnsupportedEncodingException | SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
