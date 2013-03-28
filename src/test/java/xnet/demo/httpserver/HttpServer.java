package xnet.demo.httpserver;

import jnet.core.http.HttpSession;
import jnet.core.http.ServletFactory;
import jnet.core.http.ServletFilter;
import jnet.core.server.*;

import org.apache.log4j.PropertyConfigurator;



public class HttpServer {
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			System.out.println(HttpServer.class + " PORT THREAD_NUM\n");
			return;
		}

		Settings config = new Settings();
		config.session = HttpSession.class;
		PropertyConfigurator.configure("./conf/log4j.properties");
		config.threadNum = Integer.parseInt(args[1]);
		config.port = Short.parseShort(args[0]);
		config.rTimeout = 1000;
		config.wTimeout = 1000;
		config.ip = "0.0.0.0";
		config.keepalive = true;
		config.maxConnection = 1000;
		Server server = new Server(config);
		ServletFactory.filter = (ServletFilter) new Filter();
		server.start();
	}
}
