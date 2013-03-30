package com.github.jnet.demo.echoserver;

import com.github.jnet.core.server.Configuration;
import com.github.jnet.core.server.Server;

public class EchoServer extends Server<EchoSession> {
	public EchoServer(Configuration config, Class<EchoSession> clazz) {
		super(config, clazz);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		try {

			Configuration config = new Configuration();
			config.threads = 5;
			config.port = 8080;
			config.readTimeout = 3000;
			config.writeTimeout = 3000;
			config.ip = "localhost";
			//config.keepalive = true;
			config.maxConnection = 100;
			EchoServer server = new EchoServer(config, EchoSession.class);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
