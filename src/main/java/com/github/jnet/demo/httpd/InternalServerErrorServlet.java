package com.github.jnet.demo.httpd;

import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Response;
import com.github.jnet.protocol.http11.Servlet;
/**
 * 返回HTTP 500
 * @author xiebiao
 *
 */
public class InternalServerErrorServlet implements Servlet {

	@Override
	public void doRequest(Request request, Response response) throws Exception {
		response.setStatusCode(500);
		response.write("系统内部错误");
	}

}
