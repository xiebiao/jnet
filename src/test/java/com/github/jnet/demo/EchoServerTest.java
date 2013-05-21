package com.github.jnet.demo;

import com.github.jnet.SessionManager;
import com.github.jnet.demo.echo.EchoServer;
import com.github.jnet.demo.echo.EchoSession;

public class EchoServerTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            EchoServer server = new EchoServer();
            SessionManager sm = new SessionManager(EchoSession.class);
            server.setIp("127.0.0.1");
            server.setPort(8081);
            server.init(sm);
            server.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
