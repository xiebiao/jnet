package jnet.demo.httpserver;

import jnet.core.server.Server;
import jnet.core.server.Configuration;
import jnet.protocol.http11.HttpSession;
import jnet.protocol.http11.ServletFactory;
import jnet.protocol.http11.ServletFilter;

public class HttpServer extends Server<HttpSession> {

	public HttpServer(Configuration config, Class<HttpSession> clazz) {
		super(config, clazz);
	}

	public static void main(String[] args) throws Exception {
		int threads = 5;
		int port = 8080;
		if (args.length != 2) {
			System.out.println(HttpServer.class + " PORT THREAD_NUM\n");
		} else {
			threads = Integer.parseInt(args[1]);
			port = Short.parseShort(args[0]);
		}
		Configuration config = new Configuration();
		config.threads = threads;
		config.port = port;
		config.readTimeout = 2000;
		config.writeTimeout = 2000;
		config.ip = "localhost";
	//	config.keepalive = true;
		config.maxConnection = 100;
		HttpServer server = new HttpServer(config, HttpSession.class);
		// server.setSessionHandler(HttpSession.class);
		ServletFactory.filter = (ServletFilter) new Filter();
		server.start();
	}

}
