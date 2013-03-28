package jnet.demo.echoserver;

import java.io.InputStream;
import java.util.Properties;

import jnet.core.server.Server;
import jnet.core.server.Settings;

import org.apache.log4j.PropertyConfigurator;

public class EchoServer {
	public static void main(String[] args) throws Exception {
		try {

			Settings config = new Settings();
			config.session = EchoSession.class;
			InputStream in = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("log4j.properties");
			Properties pro = new Properties();
			pro.load(in);
			PropertyConfigurator.configure(pro);
			config.threadNum = 5;
			config.port = 8080;
			config.rTimeout = 3000;
			config.wTimeout = 3000;
			config.ip = "0.0.0.0";
			config.keepalive = true;
			config.maxConnection = 1000;
			Server server = new Server(config);
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
