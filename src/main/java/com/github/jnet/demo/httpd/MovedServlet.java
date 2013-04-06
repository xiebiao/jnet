package com.github.jnet.demo.httpd;

import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Response;
import com.github.jnet.protocol.http11.Servlet;

public class MovedServlet implements Servlet {

	@Override
	public void doRequest(Request request, Response response) throws Exception {
		response.setStatusCode(302);
		response.getHeader().put("location",
				"http://localhost:8082/hello_world");
		response.write("跳转");
	}
}
