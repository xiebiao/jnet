package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;

public abstract class Session {

  private static final Logger logger = LoggerFactory.getLogger(Session.class);
  // 会话id
  protected int id = 0;
  // 下一次超时时间
  protected long nextTimeout = 0;

  public enum IoState {
    READ, WRITE, CLOSE
  }

  // 当前IO状态
  protected IoState currentState;

  public enum Event {
    READ, WRITE, TIMEOUT
  }

  // 当前会话事件
  protected Event currentEvent = Event.READ;
  // 读数据
  protected IOBuffer readBuffer = null;
  // 写数据
  protected IOBuffer writeBuffer = null;

  protected SocketChannel socket = null;

  private boolean idle = false;

  public Session() {
    id = 0;
  }

  /*------------------------------------------------------------------ change state */
  public void timeout() throws Exception {
    logger.debug("The Session " + this.getId() + " is timeout, will be closed.");
    setNextState(IoState.CLOSE);
  }

  public final void setNextState(IoState state) {
    this.currentState = state;
    switch (state) {
      case WRITE:
        logger.info("Set the Session[" + this.getId() + "] : currentState = " + "STATE_WRITE.");
        readBuffer.position(0);
        readBuffer.limit(0);
        break;
      case READ:
        logger.info("Set the Session[" + this.getId() + "] : currentState = " + "STATE_READ.");
        writeBuffer.position(0);
        writeBuffer.limit(0);
        break;
      case CLOSE:
        logger.info("Set Session[" + this.getId() + "] : currentState = " + "STATE_CLOSE.");
        this.close();
    }
  }

  public void remain(int remain, IoState state) {
    switch (state) {
      case READ:
        readBuffer.limit(readBuffer.position() + remain);
        setNextState(IoState.READ);
        break;
      case WRITE:
        writeBuffer.limit(writeBuffer.position() + remain);
        setNextState(IoState.WRITE);
        break;
    }

  }

  /*------------------------------------------------------------------ abstract methods */
  public abstract void open(IOBuffer readBuf, IOBuffer writeBuf) throws Exception;

  public abstract void read(IOBuffer readBuf, IOBuffer writeBuf) throws Exception;

  public abstract void write(IOBuffer readBuf, IOBuffer writeBuf) throws Exception;

  public abstract void close();

  /*------------------------------------------------------------------ getter/setter */
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

  public void setReadBuffer(IOBuffer readBuf) {
    this.readBuffer = readBuf;
  }

  public SocketChannel getSocketChannel() {
    return socket;
  }

  public void setSocketChannel(SocketChannel socket) {
    this.socket = socket;
  }

  public boolean isIdle() {
    return idle;
  }

  public void setIdle(boolean idle) {
    this.idle = idle;
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

  public IoState getCurrentState() {
    return currentState;
  }

}
