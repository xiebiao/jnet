package xnet.demo.httpserver;

import com.xiebiao.jnet.core.http.Request;
import com.xiebiao.jnet.core.http.Response;
import com.xiebiao.jnet.core.http.Servlet;

public class TimeServlet implements Servlet {

	public void doRequest(Request request, Response response) throws Exception {		 
		Long now = System.currentTimeMillis();
		response.write(now.toString());
	}

}
