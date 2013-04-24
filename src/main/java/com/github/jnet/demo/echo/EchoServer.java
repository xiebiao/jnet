package com.github.jnet.demo.echo;

import com.github.jnet.Configuration;
import com.github.jnet.Server;

public class EchoServer extends Server<EchoSession> {
	public EchoServer() {
	}

	public static void main(String[] args) throws Exception {
		try {
			Configuration config = new Configuration();
			EchoServer server = new EchoServer();
			server.init(config, EchoSession.class);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
