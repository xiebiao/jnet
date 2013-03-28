package xnet.demo.httpserver;

import com.xiebiao.jnet.core.http.Request;
import com.xiebiao.jnet.core.http.Response;
import com.xiebiao.jnet.core.http.Servlet;

public class EchoServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {		 
		response.write("hello 中国");
	}

}
