package com.github.jnet.demo.echo;

import com.github.jnet.Configuration;
import com.github.jnet.Server;

public class EchoServer extends Server<EchoSession> {
	public EchoServer(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		try {
			Configuration config = new Configuration();
			config.setReadTimeout(100);
			config.setWriteTimeout(100);
			EchoServer server = new EchoServer("Echo Server");
			server.init(config, EchoSession.class);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
