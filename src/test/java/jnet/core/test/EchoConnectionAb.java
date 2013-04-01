package jnet.core.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.github.jnet.Client;


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
			Client conn = new Client(servers, 1000, 1000, 0);
			new Thread(new Ab(conn, message)).start();
		}

	}

}
