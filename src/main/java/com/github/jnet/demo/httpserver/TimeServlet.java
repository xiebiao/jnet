package com.github.jnet.demo.httpserver;

import java.util.Date;

import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Response;
import com.github.jnet.protocol.http11.Servlet;

public class TimeServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {
		response.setStatusCode(200);
		response.write(new Date().toString());
	}

}
