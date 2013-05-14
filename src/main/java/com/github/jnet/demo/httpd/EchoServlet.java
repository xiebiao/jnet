package com.github.jnet.demo.httpd;


public class EchoServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {
		response.write("hello world");
	}

}
