package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;

public abstract class Session {
	private static final Logger logger = LoggerFactory.getLogger(Session.class);
	/**
	 * 会话id
	 */
	protected int id = 0;

	/**
	 * 下一次超时时间点（时间戳）
	 */
	protected long nextTimeout = 0;
	/**
	 * 当前IO状态
	 */
	protected IOState currentState;
	/**
	 * 当前会话事件
	 */
	protected SessionEvent currentEvent = SessionEvent.READ;

	protected IOBuffer readBuffer = null;

	protected IOBuffer writeBuffer = null;

	protected SocketChannel socket = null;

	private boolean inuse = false;

	public Session() {
	}

	public abstract void open(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void readCompleted(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void reading(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void writeCompleted(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void writing(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception;

	public abstract void close();

	public void timeout() throws Exception {
		logger.debug("The Session " + this.getId()
				+ " is timeout, will be closed.");
		setNextState(IOState.CLOSE);
	}

	public void setNextState(IOState state) {
		this.currentState = state;
		switch (state) {
		case WRITE:
			readBuffer.position(0);
			readBuffer.limit(0);
			break;
		case READ:
			writeBuffer.position(0);
			writeBuffer.limit(0);
			break;
		case CLOSE:
			this.close();
		}

		switch (state) {
		case READ:
			logger.info("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_READ");
			break;
		case WRITE:
			logger.info("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_WRITE");
			break;
		case CLOSE:
			logger.info("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_CLOSE");
			break;
		}
	}

	public void remainToRead(int remain) {
		readBuffer.limit(readBuffer.position() + remain);
		setNextState(IOState.READ);
	}

	public void remainToWrite(int remain) {
		writeBuffer.limit(writeBuffer.position() + remain);
		setNextState(IOState.WRITE);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SessionEvent getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(SessionEvent event) {
		this.currentEvent = event;
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

	public void setWriteBuffer(IOBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

	public long getNextTimeout() {
		return nextTimeout;
	}

	public void setNextTimeout(long nextTimeout) {
		this.nextTimeout = nextTimeout;
	}

	public IOState getCurrentState() {
		return currentState;
	}

}
