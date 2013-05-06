package com.github.jnet.demo.httpd;

import com.github.jnet.Configuration;
import com.github.jnet.Server;
import com.github.jnet.protocol.http11.HttpSession;
import com.github.jnet.protocol.http11.ServletFactory;
import com.github.jnet.protocol.http11.ServletFilter;

public class HttpServer extends Server<HttpSession> {

    public HttpServer(String name) {
        super(name);
    }

    public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.setIp("127.0.0.1");
        config.setPort(8081);
        config.setReadTimeout(100);
        config.setWriteTimeout(100);

        HttpServer server = new HttpServer("Http Server");
        server.init(config, HttpSession.class);
        ServletFactory.filter = (ServletFilter) new Filter();
        server.start();
    }
}
