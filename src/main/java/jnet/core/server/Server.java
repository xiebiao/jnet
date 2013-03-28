package jnet.core.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jnet.core.util.IOBuffer;
import jnet.log.Logger;
import jnet.log.LoggerFactory;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	Settings config;
	Worker[] workers;
	Selector selector;
	ServerSocketChannel socket;
	int nextWorkerIndex = 0;

	public Server(Settings config) {
		this.config = config;
		workers = new Worker[config.threadNum];
	}

	/**
	 * 启动服务
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		logger.debug("DEBUG ENTER");

		for (int i = 0; i < config.maxConnection; i++) {
			Session session = (Session) config.session.newInstance();
			session.config = config;
			session.event = Session.EVENT_READ;
			session.inuse = false;
			session.readBuf = new IOBuffer();
			session.writeBuf = new IOBuffer();
			Sessions.addSession(session);
		}

		try {
			initServerSocket();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		ExecutorService pool = Executors.newFixedThreadPool(config.threadNum);
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new Worker();
			pool.execute(workers[i]);
		}

		SocketChannel csocket = null;
		while (true) {
			selector.select();
			try {
				csocket = socket.accept();
				csocket.configureBlocking(false);
				Session session = Sessions.openSession();
				if (session == null) {
					logger.warn("too many connection");
					csocket.close();
					continue;
				} else {
					session.socket = csocket;
					handleNewSession(session);
				}
			} catch (IOException e) {
				if (csocket != null && csocket.isConnected()) {
					csocket.close();
				}
				logger.warn("", e);
			}
		}
	}

	/**
	 * 初始化server套接字
	 * 
	 * @throws IOException
	 */
	public void initServerSocket() throws IOException {
		logger.debug("DEBUG ENTER");

		selector = Selector.open();
		socket = ServerSocketChannel.open();
		socket.socket().setReuseAddress(true);
		socket.configureBlocking(false);
		socket.socket().bind(
				new InetSocketAddress(InetAddress.getByName(config.ip),
						config.port));
		socket.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 处理一个新session，为其指定一个工作线程，并加入到工作线程新session队列
	 * 
	 * @param session
	 */
	private void handleNewSession(Session session) {
		logger.debug("DEBUG ENTER");

		workers[nextWorkerIndex].addNewSession(session);
		nextWorkerIndex = (nextWorkerIndex + 1) % config.threadNum;
	}

}
