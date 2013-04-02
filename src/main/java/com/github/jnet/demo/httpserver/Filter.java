package com.github.jnet.demo.httpserver;

import com.github.jnet.protocol.http11.HttpAttr;
import com.github.jnet.protocol.http11.Request;
import com.github.jnet.protocol.http11.Servlet;
import com.github.jnet.protocol.http11.ServletFilter;

public class Filter implements ServletFilter {

	public Servlet getServlet(Request request) {
		String url = request.header.get(HttpAttr.HEAD_URL);
		int pos = url.indexOf("?");
		if (pos >= 0) {
			url = url.substring(0,pos);
		}
		if (url.indexOf("echo") >= 0) {
			return new EchoServlet();
		}
		if (url.indexOf("time") >= 0) {
			return new TimeServlet();
		}
		if (url.indexOf("error") >= 0) {
			return new InternalServerErrorServlet();
		}
		if (url.indexOf("moved") >= 0) {
			return new MovedServlet();
		}
		return new NotFoundServlet();
	}

}
