package com.xiebiao.jnet;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {

    /**
     * @param args
     */
    public static void main(String[] args) {
	try {
	    ServerSocket serverSocket = new ServerSocket(8080);
	    Socket socket = serverSocket.accept();
	    while (socket.isConnected()) {
		InputStream input = socket.getInputStream();
		byte[] bytes = new byte[1024];
		input.read(bytes);
		System.out.println(new String(bytes).toString());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

}
