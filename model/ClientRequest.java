package demo1;


import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;  
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.print.Doc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.apache.commons.io.FileUtils;

import com.mysql.fabric.Response;
import com.mysql.jdbc.Connection;
import com.sun.accessibility.internal.resources.accessibility;
import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ClientRequest {

	static String urlPref="http://www.google.com/search?";
	private final String USER_AGENT = "Mozilla/5.0";
	private static final int RESULT_OK = 200;
	private String urlQ;
	public void setUrlQ(String urlQ) {
		this.urlQ=urlQ;
	}
	public String getUrlQ() {
		return this.urlQ;
	}
	public ClientRequest(String url) {
		url = "q="+url;
		this.setUrlQ(url);
	}
	
	public void linkUrl() {
	Document document = null;
	
		try {
			String nextHref=urlPref+urlQ;
			for(int i =0; i < 10;i++) {
				document = Jsoup.connect(nextHref).userAgent(USER_AGENT).ignoreHttpErrors(true).timeout(0).get();
				Elements links = document.select("h3[class=r]");
				Elements cnex =document.select("td.b").last().select("a[href]");
				nextHref = document.select("td.b").last().select("a[href]").attr("abs:href");
				System.out.println(nextHref);
				for(Element link : links) {
					Elements href = link.select("a[href]");
					String temp = href.attr("abs:href");
					System.out.println(temp);
					UrlRequest rUrlRequest = new UrlRequest(temp);
					rUrlRequest.saveDocument();
					
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
	
class UrlRequest{
	private String url;
	private final String USER_AGENT = "Mozilla/5.0";
	private final String  savePath= "/Users/ustctll/Desktop/demo1";
	public void setUrl(String url) {
		this.url=url;
	}
	public String getUrl() {
		return this.url;
	}
	public UrlRequest(String url) {
		this.setUrl(url);
	}
	public void saveDocument() {
		try {
			Document doc = Jsoup.connect(url).execute().parse();
			String filename = savePath+"/"+doc.select("title").first().text()+".html";
			File file = new File(filename);
			FileUtils.writeStringToFile(file, doc.outerHtml(), "UTF-8");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
		
	}
	
	

}


