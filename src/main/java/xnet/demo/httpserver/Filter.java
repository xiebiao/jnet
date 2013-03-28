package xnet.demo.httpserver;

import com.xiebiao.jnet.core.http.HttpAttr;
import com.xiebiao.jnet.core.http.Request;
import com.xiebiao.jnet.core.http.Servlet;
import com.xiebiao.jnet.core.http.ServletFilter;

public class Filter implements ServletFilter {

	public Servlet getServlet(Request request) {
		String url = request.header.get(HttpAttr.HEAD_URL);
		int pos = url.indexOf("?");
		if (pos >= 0) {
			url = url.substring(0,pos);
		}
		if (url.indexOf("echo.action") >= 0) {
			return new EchoServlet();
		}
		if (url.indexOf("time.action") >= 0) {
			return new TimeServlet();
		}
		return new ErrorServlet();
	}

}
