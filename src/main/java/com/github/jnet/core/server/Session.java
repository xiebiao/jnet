package com.github.jnet.core.server;

import java.nio.channels.SocketChannel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;

public abstract class Session {
	private static final Logger logger = LoggerFactory.getLogger(Session.class);
	/**
	 * session id
	 */
	private int id = 0;
	/**
	 * IO状态
	 */
	public static final int STATE_READ = 0;
	public static final int STATE_WRITE = 1;
	public static final int STATE_CLOSE = 2;

	/**
	 * 会话事件：可读，可写，超时
	 */
	public static final int EVENT_READ = 0;
	public static final int EVENT_WRITE = 1;
	public static final int EVENT_TIMEOUT = 2;

	/**
	 * 下一次超时时间点（时间戳）
	 */
	private long nextTimeout = 0;

	private int currentState;

	private int currentEvent = Session.EVENT_READ;

	private IOBuffer readBuffer = null;

	private IOBuffer writeBuffer = null;

	private SocketChannel socket = null;

	private Configuration config = null;

	private boolean inuse = false;

	public Session() {
	}

	public abstract void open(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void complateRead(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void reading(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void complateWrite(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void writing(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public void close() {
	}

	public void timeout() throws Exception {
		setNextState(STATE_CLOSE);
	}

	public void setNextState(int state) {
		this.currentState = state;
		if (state == STATE_WRITE) {
			readBuffer.position(0);
			readBuffer.limit(0);
		} else if (state == STATE_READ) {
			writeBuffer.position(0);
			writeBuffer.limit(0);
		}
		switch (state) {
		case STATE_READ:
			logger.debug(this.toString() + " STATE_READ");
			break;
		case STATE_WRITE:
			logger.debug(this.toString() + " STATE_WRITE");
			break;
		case STATE_CLOSE:
			logger.debug(this.toString() + " STATE_CLOSE");
			break;
		}
	}

	public void remainToRead(int remain) {
		readBuffer.limit(readBuffer.position() + remain);
		setNextState(STATE_READ);
	}

	public void remainToWrite(int remain) {
		writeBuffer.limit(writeBuffer.position() + remain);
		setNextState(STATE_WRITE);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(int event) {
		this.currentEvent = event;
	}

	public IOBuffer getReadBuffer() {
		return readBuffer;
	}

	public void setReadBuffer(IOBuffer readBuf) {
		this.readBuffer = readBuf;
	}

	public SocketChannel getSocket() {
		return socket;
	}

	public void setSocket(SocketChannel socket) {
		this.socket = socket;
	}

	public boolean isInuse() {
		return inuse;
	}

	public void setInuse(boolean inuse) {
		this.inuse = inuse;
	}

	public Configuration getConfig() {
		return config;
	}

	public void setConfig(Configuration config) {
		this.config = config;
	}

	public IOBuffer getWriteBuffer() {
		return writeBuffer;
	}

	public void setWriteBuffer(IOBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

	public long getNextTimeout() {
		return nextTimeout;
	}

	public void setNextTimeout(long nextTimeout) {
		this.nextTimeout = nextTimeout;
	}

	public int getCurrentState() {
		return currentState;
	}

	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

}
