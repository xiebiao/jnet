package com.github.jnet.demo.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.Session;
import com.github.jnet.utils.IoBuffer;

public class EchoSession extends Session {

    private static final Logger logger   = LoggerFactory.getLogger(EchoSession.class);
    static final int            BUF_SIZE = 1024;

    @Override
    public void readCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
        this.setNextState(IoState.WRITE);
    }

    @Override
    public void reading(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
        if (readBuf.position() > 1) {
            byte b = readBuf.getByte(readBuf.position() - 1);
            if (b == (byte) '\n') {
                int len = readBuf.position();
                writeBuf.position(0);
                writeBuf.writeBytes(readBuf.readBytes(0, len));
                writeBuf.position(0);
                writeBuf.limit(len);
                setNextState(IoState.WRITE);
                return;
            }
        }
        remain(BUF_SIZE, IoState.READ);
    }

    @Override
    public void writeCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
        /** Session未关闭，则继续读取IO,同时position复位 */
        readBuf.position(0);
        remain(BUF_SIZE, IoState.READ);
    }

    @Override
    public void open(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
        logger.debug("Open session");
        remain(BUF_SIZE, IoState.READ);
    }

    @Override
    public void close() {

    }

    public String toString() {
        return "Sessson[" + this.getId() + "]";
    }

    @Override
    public void writing(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {

    }

}
