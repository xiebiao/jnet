package com.github.jnet;

import java.nio.channels.SocketChannel;

import com.github.jnet.utils.IoBuffer;

/**
 * 会话
 * @author xiebiao
 */
public interface ISession {

    public enum IoState {
        READ, WRITE, CLOSE
    }

    public enum Event {
        READ, WRITE, TIMEOUT
    }

    /**
     * 打开会话
     * @param readBuf 读buffer
     * @param writeBuf 写buffer
     * @throws Exception
     */
    public abstract void open(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

    /**
     * 读buffer完成
     * @param readBuf 读buffer
     * @param writeBuf 写buffer
     * @throws Exception
     */
    public abstract void readCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

    /**
     * 读buffer中
     * @param readBuf 读buffer
     * @param writeBuf 写buffer
     * @throws Exception
     */
    public abstract void reading(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

    /**
     * 写buffer完成
     * @param readBuf 读buffer
     * @param writeBuf 写buffer
     * @throws Exception
     */
    public abstract void writeCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception;

    /**
     * 关闭会话
     * @throws Exception
     */
    public abstract void close();

    /**
     * 获取会话超时时间
     * @return
     */
    public long getNextTimeout();

    /**
     * 设置会话超时时间
     * @param timeout
     */

    public void setNextTimeout(long timeout);

    public void setCurrentEvent(Event event);

    public Event getCurrentEvent();

    public IoState getCurrentIoState();

    public void setCurrentIoState(IoState state);

    public IoBuffer getReadBuffer();

    public IoBuffer getWriteBuffer();

    public SocketChannel getSocket();

    public void setSocket(SocketChannel socket);

    /**
     * 设置空闲状态
     * @param idle true:空闲，false：繁忙
     */
    public void setIdle(boolean idle);

    public boolean isIdle();

    public void setId(int id);

    public int getId();
}
