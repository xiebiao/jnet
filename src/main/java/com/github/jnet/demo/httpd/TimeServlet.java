package com.github.jnet.demo.httpd;

import java.util.Date;


public class TimeServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {
		response.setStatusCode(200);
		response.write(new Date().toString());
	}

}
