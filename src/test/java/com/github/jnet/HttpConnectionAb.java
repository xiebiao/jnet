package com.github.jnet;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.github.jnet.Client;


public class HttpConnectionAb {
	private static String host = "127.0.0.1";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String header = "GET / HTTP/1.1\r\nHost: host\r\n\r\n";
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(host, 8081));
		Client conn = new Client(servers, 1000, 1000, 0);
		new Thread(new Ab(conn, header)).start();

	}

}
