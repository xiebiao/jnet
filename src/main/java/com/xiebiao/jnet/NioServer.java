package com.xiebiao.jnet;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
    private final ServerSocketChannel serverSocketChannel;
    private ByteBuffer r_buff = ByteBuffer.allocate(1024);;
    private ByteBuffer w_buff = ByteBuffer.allocate(1024);;
    private Selector selector;

    public NioServer(int port) throws Exception {
	serverSocketChannel = ServerSocketChannel.open();
	serverSocketChannel.configureBlocking(false);
	selector = Selector.open();
	serverSocketChannel.bind(new InetSocketAddress(8080));
	serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	System.out.println("echo server has been set up ......");
	while (selector.select() > 0) {
	    // int n = selector.select();
	    // if (n == 0) {// 没有指定的I/O事件发生
	    // continue;
	    // }
	    Iterator it = selector.selectedKeys().iterator();
	    while (it.hasNext()) {
		SelectionKey key = (SelectionKey) it.next();
		if (key.isAcceptable()) {// 侦听端信号触发
		    ServerSocketChannel server = (ServerSocketChannel) key
			    .channel();
		    // 接受一个新的连接
		    SocketChannel sc = server.accept();
		    sc.configureBlocking(false);
		    // 设置该socket的异步信号OP_READ:当socket可读时，

		    // 触发函数DealwithData();
		    sc.register(selector, SelectionKey.OP_READ);
		}
		if (key.isReadable()) {// 某socket可读信号
		    System.out.println("is ready...");
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
