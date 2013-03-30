package jnet.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;

import jnet.core.client.Connection;
import junit.framework.TestCase;

public class TestConnection extends TestCase {
	private static String host = "www.jd.com";
	private static String encoding = "UTF-8";

	public void testConnect() throws Exception {
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(InetAddress.getByName(host), 80));
		Connection conn = new Connection(servers, 1000, 1000, 0);
		conn.connect();
		String header = "GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n";
		byte[] stream = header.getBytes(encoding);
		conn.write(stream);

		while (true) {
			System.out.print(new String(conn.read(1), encoding));
		}
	}
}
