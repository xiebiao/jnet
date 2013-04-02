package com.github.jnet.demo.httpserver;

import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Response;
import com.github.jnet.protocol.http11.Servlet;

public class NotFoundServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {
		response.write("Not found.");
	}

}