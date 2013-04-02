package com.github.jnet.demo.httpserver;

import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Response;
import com.github.jnet.protocol.http11.Servlet;

public class InternalServerErrorServlet implements Servlet {

	@Override
	public void doRequest(Request request, Response response) throws Exception {
		response.setStatusCode(500);
		response.write("系统内部错误");
	}

}
