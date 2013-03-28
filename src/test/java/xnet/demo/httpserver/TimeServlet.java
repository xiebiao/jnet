package xnet.demo.httpserver;

import jnet.protocol.http11.Request;
import jnet.protocol.http11.Response;
import jnet.protocol.http11.Servlet;

public class TimeServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {		 
		Long now = System.currentTimeMillis();
		response.write(now.toString());
	}

}
