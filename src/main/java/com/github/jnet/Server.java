package com.github.jnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 * @author xiebiao
 */
public abstract class Server<T extends Session> {

    private static final Logger logger          = LoggerFactory.getLogger(Server.class);
    protected String            name;
    private Configuration       config;
    private Worker[]            workers;
    private Selector            selector;
    private ServerSocketChannel serverSocket;
    private int                 nextWorkerIndex = 0;
    private Class<T>            sessionHandler;
    private SessionManager      sessionManager  = SessionManager.getInstance();
    private ExecutorService     executor;

    public Server(String name) {
        this.name = name;
    }

    /**
     * 启动服务
     * @throws Exception
     */
    public void start() throws Exception {
        if (this.serverSocket == null) {
            throw new Exception("Server must initialize before start.");
        }
        sessionManager.initialize(this.sessionHandler, config.getMaxConnection());
        executor = Executors.newFixedThreadPool(config.getThreadNumber(), new JnetThreadFactory());
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker(sessionManager, this.config);
            executor.execute(workers[i]);
        }
        logger.info(name + " started : " + this.config.toString());
        SocketChannel csocket = null;
        while (true) {
            if (serverSocket == null) {
                logger.warn("ServerSocket is not open.");
                break;
            }
            selector.select();
            try {
                csocket = serverSocket.accept();
                csocket.configureBlocking(false);
                Session session = sessionManager.getSession();
                if (session == null) {
                    logger.error("Too many connection.");
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
                logger.error(name + " running exception:", e);
            }
        }
    }

    public void init(Configuration config, Class<T> sessionHandler) throws IOException {
        this.config = config;
        this.sessionHandler = sessionHandler;
        workers = new Worker[config.getThreadNumber()];
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().setReuseAddress(true);
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(InetAddress.getByName(config.getIp()), config.getPort()));
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void stop() throws Exception {
        if (null != serverSocket) {
            serverSocket.close();
            serverSocket = null;
        }
        this.sessionManager.destroy();
        if (null != this.executor) {
            this.executor.shutdown();
            this.executor = null;
        }

    }

    /**
     * 处理一个新session，为其指定一个工作线程，并加入到工作线程新session队列
     * @param session
     */
    private void handleNewSession(Session session) {
        workers[nextWorkerIndex].addNewSession(session);
        nextWorkerIndex = (nextWorkerIndex + 1) % config.getThreadNumber();
    }

}
