package org.vt.edu.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.vt.edu.trace.DownloadMonthlyTrace;

/**
 * It provide utility method to download a file from web.
 * 
 * @author shivam.maharshi
 */
public class DownloadUtil {

	public static void download(String url, String file) {
		System.out.println("Downloading.... " + DownloadMonthlyTrace.URL + file);
		URL website;
		try {
			website = new URL(url + file);
			URLConnection con = website.openConnection();
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
			InputStream in = con.getInputStream();
			ReadableByteChannel rbc = Channels.newChannel(in);
			FileOutputStream fos = new FileOutputStream(Constant.RELATIVE_PATH + file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			System.out.println("Successfully downloaded : " + DownloadMonthlyTrace.URL + file);
			fos.close();
			in.close();
			rbc.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
