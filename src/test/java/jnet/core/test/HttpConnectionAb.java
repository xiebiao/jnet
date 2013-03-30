package jnet.core.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import jnet.core.client.Connection;

public class HttpConnectionAb {
	private static String host = "localhost";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String header = "GET /echo.action HTTP/1.1\r\nHost: host\r\n\r\n";
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(host, 8080));
		Connection conn = new Connection(servers, 1000, 1000, 0);
		new Thread(new Ab(conn, header)).start();

	}

}
