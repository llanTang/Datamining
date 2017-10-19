package demo1;


import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;  
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
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
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class ClientRequest {
	//private volatile static  ClientRequest clientRequest = null;
	static String urlPref="https://www.google.com.au/search?";
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
	private static final int RESULT_OK = 200;
	private String urlQ;
	public void setUrlQ(String urlQ) {
		if(urlQ != null) {
			this.urlQ=urlQ;
		}
	}
	public String getUrlQ() {
		return this.urlQ;
	}
	public   ClientRequest() {


	}

	/*public static ClientRequest getInstance()

	{

	       if(clientRequest == null)

	       {

	              synchronized(ClientRequest.class)

	              {

	                     if(clientRequest == null)

	                     {

	                            clientRequest = new ClientRequest();

	                     }

	              }

	       }
	       return clientRequest;

	}*/
	
	public void linkUrl(String url) {
		url = "q="+url;
		this.setUrlQ(url);
		//System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3,SSLv2Hello");
		Document document=null;
		String nextHref=urlPref+urlQ;
		for(int i =0; i < 10;i++) {
			try {
				System.setProperty(" jsse.enableSNIExtension","false");
				TimeUnit.SECONDS.sleep(1);
				document = Jsoup.connect(nextHref).
						userAgent(USER_AGENT).
						ignoreContentType(true).
						ignoreHttpErrors(true).
						timeout(30000).
						validateTLSCertificates(true).
						followRedirects(false).
						get();
				//document = response.parse();
				Elements links = document.select("h3[class=r]");
				nextHref = document.select("td.b").last().select("a[href]").attr("abs:href");
				System.out.println(nextHref);
				for(Element link : links) {
					Elements href = link.select("a[href]");
					String temp = href.attr("abs:href");
					System.out.println(temp);
					if(temp != null && temp.length()!=0) {
						UrlRequest rUrlRequest = new UrlRequest(temp);
						TimeUnit.SECONDS.sleep(1);
						rUrlRequest.saveDocument();
					}
				}
			}
			catch (SocketTimeoutException | UnknownHostException |SSLHandshakeException |SocketException e) {
				// TODO: handle exception
				System.out.println("time out");
				continue;
			}
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		//clientRequest = null;
	}
}


	
class UrlRequest{
	private String url;
	private static int name = 1;
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
			
			//System.setProperty("https.protocols", "TLSv1.2,TLSv1.1,SSLv3,SSLv2Hello");
			System.setProperty(" jsse.enableSNIExtension","false");
			org.jsoup.Connection.Response response = Jsoup.connect(url).
					ignoreHttpErrors(true).
					timeout(30000).
					validateTLSCertificates(true).
					followRedirects(false).
					execute();
			if(response.statusCode() == 200) {
				Document doc = response.parse();
				String filename = savePath+"/"+grabTitle()+".html";
				File file = new File(filename);
				FileUtils.writeStringToFile(file, doc.outerHtml(), "UTF-8");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("time out2");
			return;
		}	
	}
	private String grabTitle() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(name);
		name++;
		return stringBuilder.toString();
	}
	
	

}


