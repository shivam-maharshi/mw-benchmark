package org.vt.edu.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Test for URI encoding and character set.
 * 
 * @author shivam.maharshi
 */
public class UriTesting {

  public static void main(String[] args) throws UnsupportedEncodingException {
    String url = "Î Î·Î½ÎµÎ»ÏŒÏ€Î·_Î”Î­Î»Ï„Î±";
    String urlEncoded = URLEncoder.encode(url, "UTF-8");
    System.out.println("URL Encoding : " + urlEncoded);
    /*
     * This is an open question. This should not have worked since ISO-8859-1
     * does not support Greek characters set but still this worked fine. It
     * shouldn't have.
     */
    System.out.println(URLDecoder.decode(url, "ISO-8859-1"));
    convert(url, "UTF-8", "ISO-8859-1");
    System.out.println(URLEncoder.encode("ÃŽÂ ÃŽÂ·ÃŽÂ½ÃŽÂµÃŽÂ»Ã�ÂŒÃ�Â€ÃŽÂ·_ÃŽÂ”ÃŽÂ­ÃŽÂ»Ã�Â„ÃŽÂ±", "ISO-8859-1"));
    convert("ÃŽÂ ÃŽÂ·ÃŽÂ½ÃŽÂµÃŽÂ»Ã�ÂŒÃ�Â€ÃŽÂ·_ÃŽÂ”ÃŽÂ­ÃŽÂ»Ã�Â„ÃŽÂ±", "ISO-8859-1", "UTF-8");
    convert(urlEncoded, "UTF-8", "ISO-8859-1");
  }

  private static void convert(String s, String from, String to) throws UnsupportedEncodingException {
    byte[] b = s.getBytes(from);
    System.out.println("\nInput: " + s + " From: " + from + " To: " + to + " Output: " + new String(b, to));
  }

}
