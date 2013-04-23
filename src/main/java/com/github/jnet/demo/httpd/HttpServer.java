package com.github.jnet.demo.httpd;

import com.github.jnet.Configuration;
import com.github.jnet.Server;
import com.github.jnet.protocol.http11.HttpSession;
import com.github.jnet.protocol.http11.ServletFactory;
import com.github.jnet.protocol.http11.ServletFilter;

public class HttpServer extends Server<HttpSession> {

	public HttpServer(Configuration config, Class<HttpSession> clazz) {
		super(config, clazz);
	}

	public static void main(String[] args) throws Exception {

		Configuration config = new Configuration();
		config.setIp("10.28.162.84");
		HttpServer server = new HttpServer(config, HttpSession.class);
		// server.setSessionHandler(HttpSession.class);
		ServletFactory.filter = (ServletFilter) new Filter();
		server.start();
	}

}
