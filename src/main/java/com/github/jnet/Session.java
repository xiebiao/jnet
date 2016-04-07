package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IoBuffer;

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
  protected IoBuffer readBuffer = null;
  // 写数据
  protected IoBuffer writeBuffer = null;

  protected SocketChannel socket = null;

  // 是否空闲
  private boolean idle = true;

  public Session() {
    id = 0;
  }

  public void timeout() throws Exception {
    logger.debug("The Session " + this.getId() + " is timeout, will be closed.");
    setNextState(IoState.CLOSE);
  }

  public final void setNextState(IoState state) {
    this.currentState = state;
    switch (state) {
      case WRITE:
        logger.debug("Session[" + this.getId() + "]  ready for WRITE");
        readBuffer.position(0);
        readBuffer.limit(0);
        break;
      case READ:
        logger.debug("Session[" + this.getId() + "]  ready for READ");
        writeBuffer.position(0);
        writeBuffer.limit(0);
        break;
      case CLOSE:
        logger.debug("Session[" + this.getId() + "]  ready for CLOSE");
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
  public abstract void open(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

  public abstract void read(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

  public abstract void write(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

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

  public void setReadBuffer(IoBuffer readBuf) {
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
