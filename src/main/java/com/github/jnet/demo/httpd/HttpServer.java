package com.github.jnet.demo.httpd;

import com.github.jnet.Configuration;
import com.github.jnet.Server;

public class HttpServer extends Server<HttpSession> {

    public HttpServer() {

    }

    public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.setIp("127.0.0.1");
        config.setPort(8081);
        config.setReadTimeout(100);
        config.setWriteTimeout(100);

        HttpServer server = new HttpServer();
        server.setName("Http");
        server.init(config, HttpSession.class);
        ServletFactory.filter = (ServletFilter) new Filter();
        server.start();
    }
}
