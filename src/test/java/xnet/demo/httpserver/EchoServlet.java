package xnet.demo.httpserver;

import jnet.core.http.Request;
import jnet.core.http.Response;
import jnet.core.http.Servlet;

public class EchoServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {		 
		response.write("hello 中国");
	}

}
