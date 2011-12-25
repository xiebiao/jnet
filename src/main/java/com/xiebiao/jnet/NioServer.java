package com.xiebiao.jnet;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioServer {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private final ServerSocketChannel serverSocketChannel;
	private ByteBuffer r_buff = ByteBuffer.allocate(1024);;
	private ByteBuffer w_buff = ByteBuffer.allocate(1024);;
	private Selector selector;

	public NioServer(int port) throws Exception {
		LOG.debug("启动监听服务");
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket socket = serverSocketChannel.socket();
		//绑定端口
		socket.bind(new InetSocketAddress(8081));
		selector = Selector.open();
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		//从selector中获取I/O事件
		while (selector.select() > 0) {
			Iterator it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();
				if (key.isAcceptable()) {// 侦听端信号触发
					LOG.debug("有一个新连接");
					ServerSocketChannel server = (ServerSocketChannel) key
							.channel();
					// 接受一个新的连接
					SocketChannel sc = server.accept();
					sc.configureBlocking(false);
					// 设置该socket的异步信号OP_READ:当socket可读时，
					sc.register(selector, SelectionKey.OP_READ);
				}
				if (key.isReadable()) {// 某个socket有可读信号
					LOG.debug("某个socket有可读信号");
					int count;
					// 由key获取指定socketchannel的引用
					SocketChannel sc = (SocketChannel) key.channel();
					r_buff.clear();
					// 读取数据到r_buff
					while ((count = sc.read(r_buff)) > 0)
						;

					// System.out.println(r_buff.get);
					// 确保r_buff可读
					r_buff.flip();
					w_buff.clear();
					// 将r_buff内容拷入w_buff
					w_buff.put(r_buff);
					w_buff.flip();
					// 将数据返回给客户端
					// EchoToClient(sc);
					w_buff.clear();
					r_buff.clear();
				}
				if (key.isWritable()) {// 某个socket有可写信号
					LOG.debug("某个socket有可写信号");
				}
				it.remove();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			NioServer ns = new NioServer(8080);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
