package com.xiebiao.jnet.core.util;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetIo {
	private static final Logger logger = LoggerFactory.getLogger(NetIo.class);
	/**
	 * 网络读操作
	 * 
	 * @param socket
	 * @param buf
	 * @return
	 * @throws Exception
	 */
	public static int read(SocketChannel socket, IOBuffer buf) throws Exception {
		logger.debug("DEBUG ENTER");
		int len = socket.read(buf.getBuf());
		logger.debug("nread " + len + " bytes");
		if (len < 0) {
			throw new Exception("IO Error");
		}
		return len;
	}

	/**
	 * 网络写操作
	 * 
	 * @param socket
	 * @param buf
	 * @return
	 * @throws Exception
	 */
	public static int write(SocketChannel socket, IOBuffer buf) throws Exception {
		logger.debug("DEBUG ENTER");

		int len = socket.write(buf.getBuf());
		logger.debug("nwrite " + len + " bytes");
		if (len < 0) {
			throw new Exception("IO Error");
		}
		return len;
	}
}
