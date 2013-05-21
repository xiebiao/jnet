package com.github.jnet.demo;

import com.github.jnet.SessionManager;
import com.github.jnet.demo.httpd.HttpServer;
import com.github.jnet.demo.httpd.HttpSession;

public class HttpServerTest {

    /**
     * @param args
     */
    public static void main(String[] args)throws Exception {
        SessionManager sm = new SessionManager(HttpSession.class);
        sm.setReadTimeout(1000);
        sm.setWriteTimeout(2000);
        HttpServer server = new HttpServer();
        server.setName("Http");
        server.setIp("127.0.0.1");
        server.setPort(8081);
        server.setThreads(50);
        server.setMaxConnection(100);
        server.init(sm);
        server.start();
        // Thread.sleep(1000);
        // server.stop();
    }

}
