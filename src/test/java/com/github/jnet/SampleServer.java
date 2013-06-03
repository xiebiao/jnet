package com.github.jnet;

import java.net.InetSocketAddress;

public class SampleServer extends AbstractServer {

    public SampleServer(InetSocketAddress socketAddress) {
        super(socketAddress);
    }

    @Override
    public void setMaxConnection(int maxConnection) {
        // TODO Auto-generated method stub

    }

    public static void main(String args[]) {
        SessionManager sm = new SessionManager();
        sm.setHandler(SampleSession.class);
        SampleServer server = new SampleServer(new InetSocketAddress("127.0.0.1", 8080));
        try {
            server.init(sm);
            server.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
