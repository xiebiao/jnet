package com.github.jnet.utils;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class IOUtils {
	private static Logger logger = LoggerFactory.getLogger(IOUtils.class);

	/**
	 * 网络读操作
	 * 
	 * @param socket
	 * @param buf
	 * @return
	 * @throws Exception
	 */
	public static int read(SocketChannel socket, IOBuffer buf) throws Exception {
		int len = socket.read(buf.getBuffer());
		logger.debug("Read " + len + " bytes");
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
	public static int write(SocketChannel socket, IOBuffer buf)
			throws Exception {
		int len = socket.write(buf.getBuffer());
		logger.debug("Write " + len + " bytes");
		if (len < 0) {
			throw new Exception("IO Error");
		}
		return len;
	}
}
