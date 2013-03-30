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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 * 
 * @author xiebiao
 * 
 */
public abstract class Server<T extends Session> {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);

	private Settings config;
	private Worker[] workers;
	private Selector selector;
	private ServerSocketChannel socket;
	private int nextWorkerIndex = 0;
	private SessionManager sessionManager = SessionManager.getInstance();
	private Class<T> sessionHandler;

	public Server(Settings config, Class<T> sessionHandler) {
		this.config = config;
		this.sessionHandler = sessionHandler;
		workers = new Worker[config.threads];
	}

	/**
	 * 启动服务
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {
		logger.debug("Server start");
		for (int i = 0; i < config.maxConnection; i++) {
			Session session = this.sessionHandler.newInstance();
			session.setId(i);
			session.setConfig(config);
			session.setEvent(Session.EVENT_READ);
			session.setInuse(false);
			session.setReadBuffer(new IOBuffer());
			session.setWriteBuffer(new IOBuffer());
			sessionManager.addSession(session);
		}

		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		ExecutorService pool = Executors.newFixedThreadPool(config.threads);
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
				Session session = SessionManager.openSession();
				if (session == null) {
					logger.error("Too many connection");
					csocket.close();
					continue;
				} else {
					session.setSocket(csocket);
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

	public void init() throws IOException {

		selector = Selector.open();
		socket = ServerSocketChannel.open();
		socket.socket().setReuseAddress(true);
		socket.configureBlocking(false);
		socket.socket().bind(
				new InetSocketAddress(InetAddress.getByName(config.ip),
						config.port));
		socket.register(selector, SelectionKey.OP_ACCEPT);
		logger.debug("Server start on " + config.ip + ":" + config.port + "");
	}

	/**
	 * 处理一个新session，为其指定一个工作线程，并加入到工作线程新session队列
	 * 
	 * @param session
	 */
	private void handleNewSession(Session session) {
		workers[nextWorkerIndex].addNewSession(session);
		nextWorkerIndex = (nextWorkerIndex + 1) % config.threads;
	}

}
