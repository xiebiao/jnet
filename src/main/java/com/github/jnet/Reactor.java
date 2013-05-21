package com.github.jnet;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Reactor implements Runnable {

    private final Selector            selector;
    private final ServerSocketChannel serverSocket;
    private Acceptor[]                acceptors;
    private ExecutorService           executor;
    private int                       index = 0;
    private int                       threads;
    private SessionManager            sessionManager;

    public Reactor(Selector selector, ServerSocketChannel serverSocket, SessionManager sessionManager, int threads) {
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.threads = threads;
        this.sessionManager = sessionManager;
        try {
            executor = Executors.newFixedThreadPool(threads, new JnetThreadFactory());
            for (int i = 0; i < acceptors.length; i++) {
                acceptors[i] = new Acceptor(sessionManager);
                executor.execute(acceptors[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        SocketChannel csocket = null;
        try {
            while (true) {
                selector.select();
                csocket = serverSocket.accept();
                Session session = sessionManager.getSession();
                if (session == null) {
                    csocket.close();
                    continue;
                } else {
                    session.setSocket(csocket);
                    handleNewSession(session);
                }
            }
        } catch (IOException ex) {
            if (csocket != null && csocket.isConnected()) {
                try {
                    csocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
        }

    }

    private void handleNewSession(Session session) {
        acceptors[index].addNewSession(session);
        index = (index + 1) % this.threads;
    }
}
