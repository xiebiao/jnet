package com.github.jnet.demo.echo;

import com.github.jnet.AbstractServer;
import com.github.jnet.SessionManager;
import com.github.jnet.demo.httpd.HttpSession;

public class EchoServer extends AbstractServer {

    public EchoServer() {

    }

    public static void main(String[] args) throws Exception {
        try {
            EchoServer server = new EchoServer();
            SessionManager sm = new SessionManager(HttpSession.class);
            server.init(sm);
            server.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
