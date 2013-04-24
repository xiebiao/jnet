package com.github.jnet.demo.httpd;

import com.github.jnet.Configuration;
import com.github.jnet.Server;
import com.github.jnet.protocol.http11.HttpSession;
import com.github.jnet.protocol.http11.ServletFactory;
import com.github.jnet.protocol.http11.ServletFilter;

public class HttpServer extends Server<HttpSession> {

	public HttpServer() {

	}
	class Stop implements Runnable {
		private Server server;

		public Stop(Server server) {
			this.server = server;
		}

		@Override
		public void run() {
			try {
				System.out.println("Stop");
				server.stop();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	class Start implements Runnable {
		private Server server;

		public Start(Server server) {
			this.server = server;
		}

		@Override
		public void run() {
			try {
				server.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration config = new Configuration();
		config.setIp("10.28.162.73");
		config.setReadTimeout(100);
		config.setWriteTimeout(100);

		// HttpServer server = new HttpServer(config, HttpSession.class);
		// server.setSessionHandler(HttpSession.class);
		ServletFactory.filter = (ServletFilter) new Filter();
		// server.start();
		Class clazz = Class.forName("com.github.jnet.demo.httpd.HttpServer");
		Object obj = clazz.newInstance();
		Server<HttpSession> server = (Server<HttpSession>) obj;
		server.init(config, HttpSession.class);
		new Thread(new HttpServer().new Start(server)).start();	
		
	}
}
