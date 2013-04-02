package com.github.jnet.demo.echoserver;

import com.github.jnet.Configuration;
import com.github.jnet.Server;

public class EchoServer extends Server<EchoSession> {
	public EchoServer(Configuration config, Class<EchoSession> clazz) {
		super(config, clazz);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		try {
			Configuration config = new Configuration();
			EchoServer server = new EchoServer(config, EchoSession.class);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
