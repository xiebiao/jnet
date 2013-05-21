package com.github.jnet;

import com.github.jnet.demo.httpd.HttpSession;

public class Bootstrap {

    private static final String[] COMMANDS = new String[] { "httpd", "echo" };
    private static final String[] CLAZZ    = new String[] { "com.github.jnet.demo.httpd.HttpServer",
            "com.github.jnet.demo.echo.EchoServer" };

    public static final void main(String args[]) throws Exception {
        if (args == null || args.length < 4) {
            usage();
            System.exit(1);
        }
        String clazz = null;
        if (args[0].equals(COMMANDS[0])) {
            clazz = CLAZZ[0];
        } else if (args[0].equals(COMMANDS[1])) {
            clazz = CLAZZ[1];
        } else {
            usage();
            System.exit(1);
        }
        String[] _arr = args[1].split(":");
        String ip = _arr[0];
        int port = Integer.valueOf(_arr[1]);
        int threads = Integer.valueOf(args[2]);
        int connections = Integer.valueOf(args[3]);
        Server server = (Server) Class.forName(clazz).newInstance();
        server.setIp(ip);
        server.setPort(port);
        SessionManager sessionManager = new SessionManager(HttpSession.class);
        sessionManager.setReadTimeout(1000);
        sessionManager.setWriteTimeout(2000);
        server.setMaxConnection(connections);
        server.setThreads(threads);
        server.init(sessionManager);
        server.start();
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("\r <httpd|echo> <ip:port> <threads> <connections>");
    }
}
