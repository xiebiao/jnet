package com.github.jnet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;

public class T {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            ServerSocketChannel serverSocket = ServerSocketChannel.open();

            serverSocket.socket().bind(new InetSocketAddress(InetAddress.getByName("localhost"), 8081));
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
