package com.github.jnet.demo.httpd;

public interface Servlet {
	public void doRequest(Request request, Response response) throws Exception;
}
