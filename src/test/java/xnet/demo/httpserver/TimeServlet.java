package xnet.demo.httpserver;

import jnet.core.http.Request;
import jnet.core.http.Response;
import jnet.core.http.Servlet;

public class TimeServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {		 
		Long now = System.currentTimeMillis();
		response.write(now.toString());
	}

}
