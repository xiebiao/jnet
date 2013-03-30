package jnet.core.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import jnet.core.client.Connection;

public class EchoConnectionAb {
	private static String host = "localhost";
	private static int count = 100;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String message = "hello world\n";
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(host, 8080));
		
		for (int i = 0; i < count; i++) {
			Connection conn = new Connection(servers, 1000, 1000, 0);
			new Thread(new Ab(conn, message)).start();
		}

	}

}