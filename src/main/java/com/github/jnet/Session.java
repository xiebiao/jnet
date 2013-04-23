package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IoBuffer;

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

	public enum IoState {
		READ, WRITE, CLOSE
	}

	/**
	 * 当前IO状态
	 */
	protected IoState currentState;

	public enum Event {
		READ, WRITE, TIMEOUT
	}

	/**
	 * 当前会话事件
	 */
	protected Event currentEvent = Event.READ;

	protected IoBuffer readBuffer = null;

	protected IoBuffer writeBuffer = null;

	protected SocketChannel socket = null;

	private boolean inuse = false;

	public Session() {
		id = 0;
	}

	public abstract void open(IoBuffer readBuf, IoBuffer writeBuf)
			throws Exception;

	public abstract void readCompleted(IoBuffer readBuf, IoBuffer writeBuf)
			throws Exception;

	public abstract void reading(IoBuffer readBuf, IoBuffer writeBuf)
			throws Exception;

	public abstract void writeCompleted(IoBuffer readBuf, IoBuffer writeBuf)
			throws Exception;

	public abstract void writing(IoBuffer readBuf, IoBuffer writeBuf)
			throws Exception;

	public abstract void close();

	public void timeout() throws Exception {
		logger.debug("The Session " + this.getId()
				+ " is timeout, will be closed.");
		setNextState(IoState.CLOSE);
	}

	public void setNextState(IoState state) {
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
					+ " STATE_READ.");
			break;
		case WRITE:
			logger.info("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_WRITE.");
			break;
		case CLOSE:
			logger.info("Set the Session[" + this.getId() + "]'s state to"
					+ " STATE_CLOSE.");
			break;
		}
	}

	public void remainToRead(int remain) {
		readBuffer.limit(readBuffer.position() + remain);
		setNextState(IoState.READ);
	}

	public void remainToWrite(int remain) {
		writeBuffer.limit(writeBuffer.position() + remain);
		setNextState(IoState.WRITE);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Event getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(Event event) {
		this.currentEvent = event;
	}

	public void setReadBuffer(IoBuffer readBuf) {
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

	public void setWriteBuffer(IoBuffer writeBuffer) {
		this.writeBuffer = writeBuffer;
	}

	public long getNextTimeout() {
		return nextTimeout;
	}

	public void setNextTimeout(long nextTimeout) {
		this.nextTimeout = nextTimeout;
	}

	public IoState getCurrentState() {
		return currentState;
	}

}
