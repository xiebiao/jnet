package com.github.jnet.demo.echo;

import java.net.InetSocketAddress;

import com.github.jnet.AbstractServer;
import com.github.jnet.SessionManager;

public class EchoServer extends AbstractServer {

    public EchoServer(InetSocketAddress address) {
        super(address);
    }

    public static final void main(String args[]) {
        SessionManager sm = new SessionManager();
        sm.setHandler(EchoSession.class);
        EchoServer server = new EchoServer(new InetSocketAddress("127.0.0.1", 8080));
        try {
            server.init(sm);
            server.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
