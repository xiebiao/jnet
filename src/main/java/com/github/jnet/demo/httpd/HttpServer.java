package com.github.jnet.demo.httpd;

import com.github.jnet.AbstractServer;
import com.github.jnet.SessionManager;

public class HttpServer extends AbstractServer {

    public HttpServer() {

    }

    public static void main(String[] args) throws Exception {
        SessionManager sm = new SessionManager(HttpSession.class);
        HttpServer server = new HttpServer();
        server.setName("Http");
        server.setIp("127.0.0.1");
        server.setPort(9081);
        server.setThreads(50);
        server.setMaxConnection(100);
        server.init(sm);
        ServletFactory.filter = (ServletFilter) new Filter();
        server.start();
        //Thread.sleep(1000);
        //server.stop();
    }    
}
