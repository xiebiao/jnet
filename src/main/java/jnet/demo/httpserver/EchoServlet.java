package jnet.demo.httpserver;

import jnet.protocol.http11.Request;
import jnet.protocol.http11.Response;
import jnet.protocol.http11.Servlet;

public class EchoServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {
		response.write("hello world");
	}

}
