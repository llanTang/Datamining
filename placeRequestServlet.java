package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo1.ClientRequest;

public class placeRequestServlet extends HttpServlet {
	public void service(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
		request.setCharacterEncoding("UTF-8");
		String placeName = request.getParameter("placeName");
		if(placeName != null &&!placeName.equals("")) {
			//link the google to get the relvant pages
			placeName = placeName.trim().replace(' ', '+');
			//ClientRequest clientReq = ClientRequest.getInstance();;
			//clientReq.linkUrl(placeName);
			System.out.println(placeName);
			ClientRequest clientReq = new ClientRequest();
			clientReq.linkUrl(placeName);
		}
		response.setContentType("text/html;charset=GBK");
		PrintWriter out = response.getWriter();
		out.println("2232343");
		
	}
}


