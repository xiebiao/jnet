package com.github.jnet;

import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.ISession.Event;
import com.github.jnet.ISession.IoState;
import com.github.jnet.utils.IoBuffer;

public abstract class AbstractSession implements ISession {

    private static final Logger LOG         = LoggerFactory.getLogger(AbstractSession.class);
    protected long              nextTimeout = 0;

    protected IoState           currentIoState;                                              // 当前IO状态
    protected Event             currentEvent;                                                // 当前会话事件
    protected IoBuffer          readBuffer  = null;                                          // 读buffer
    protected IoBuffer          writeBuffer = null;                                          // 写buffer
    protected SocketChannel     socket      = null;                                          // 开启socket
    protected boolean           idle;                                                        // 是否空闲状态
    protected int               id;                                                          // 会话Id

    public AbstractSession() {
        this.currentEvent = Event.READ;
        this.currentIoState = IoState.READ;
        this.idle = false;
        this.writeBuffer = new IoBuffer();
        this.readBuffer = new IoBuffer();
    }

    public long getNextTimeout() {
        return nextTimeout;
    }

    public void setNextTimeout(long nextTimeout) {
        this.nextTimeout = nextTimeout;
    }

    public void setNextState(IoState state) {
        this.currentIoState = state;
        switch (state) {
            case WRITE:
                readBuffer.position(0);
                readBuffer.limit(0);
                LOG.info("Set the Session[" + this.id + "] : currentState = " + "STATE_WRITE.");
                break;
            case READ:
                writeBuffer.position(0);
                writeBuffer.limit(0);
                LOG.info("Set the Session[" + this.id + "] : currentState = " + "STATE_READ.");
                break;
            case CLOSE:
                LOG.info("Set the Session[" + this.id + "] : currentState = " + "STATE_CLOSE.");
                this.close();
        }
    }

    /**
     * 会话超时
     * @throws Exception
     */
    public void timeout() throws Exception {
        LOG.debug("The Session " + this.id + " is timeout, will be closed.");
        setNextState(IoState.CLOSE);
    }

    /**
     * 会话中
     * @param remain
     * @param state
     */
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

    public void init() {
        readBuffer.position(0);
        readBuffer.limit(0);
        writeBuffer.position(0);
        writeBuffer.limit(0);
        try {
            open(readBuffer, writeBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IoState getCurrentIoState() {
        return this.currentIoState;
    }

    public IoBuffer getReadBuffer() {
        return this.readBuffer;
    }

    public IoBuffer getWriteBuffer() {
        return this.writeBuffer;
    }

    public SocketChannel getSocket() {
        return this.socket;
    }

    public boolean isIdle() {
        return this.idle;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setSocket(SocketChannel socket) {
        this.socket = socket;
    }

    @Override
    public void setCurrentEvent(Event event) {
        this.currentEvent = event;

    }

    @Override
    public Event getCurrentEvent() {
        return this.currentEvent;
    }

    @Override
    public void setCurrentIoState(IoState state) {
        this.currentIoState = state;

    }

    @Override
    public void setIdle(boolean idle) {
        this.idle = idle;
    }
}
