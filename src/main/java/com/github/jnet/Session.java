package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;

public abstract class Session {
	private static final Logger logger = LoggerFactory.getLogger(Session.class);
	/**
	 *会话id
	 */
	private int id = 0;

	/**
	 * 下一次超时时间点（时间戳）
	 */
	private long nextTimeout = 0;
	/**
	 * 当前IO状态
	 */
	private IOState currentState;
	/**
	 * 当前会话事件
	 */
	private SessionEvent currentEvent = SessionEvent.READ;

	private IOBuffer readBuffer = null;

	private IOBuffer writeBuffer = null;

	private SocketChannel socket = null;

	//private Configuration config = null;

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
		if (state == IOState.WRITE) {
			readBuffer.position(0);
			readBuffer.limit(0);
		} else if (state == IOState.READ) {
			writeBuffer.position(0);
			writeBuffer.limit(0);
		}
		switch (state) {
		case READ:
			logger.debug("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_READ");
			break;
		case WRITE:
			logger.debug("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_WRITE");
			break;
		case CLOSE:
			logger.debug("Set the Session[" + this.getId() + "]'s state to"
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

	public IOState getCurrentState() {
		return currentState;
	}

}
