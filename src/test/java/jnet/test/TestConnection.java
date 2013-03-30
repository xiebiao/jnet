package jnet.test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.github.jnet.core.client.Client;


public class TestConnection  {
	private static String host = "www.baidu.com";
	private static String encoding = "UTF-8";

	public void atestConnect() throws Exception {
		List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();
		servers.add(new InetSocketAddress(InetAddress.getByName(host), 80));
		Client conn = new Client(servers, 1000, 1000, 0);
		conn.connect();
		String header = "GET / HTTP/1.1\r\nHost: " + host + "\r\n\r\n";
		byte[] stream = header.getBytes(encoding);
		conn.write(stream);

		while (true) {
			System.out.print(new String(conn.read(1), encoding));
		}
	}
}
