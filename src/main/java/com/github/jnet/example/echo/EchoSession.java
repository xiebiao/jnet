package com.github.jnet.example.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.Session;
import com.github.jnet.utils.IoBuffer;

public class EchoSession extends Session {

  private static final Logger logger = LoggerFactory.getLogger(EchoSession.class);
  private static final int BUF_SIZE = 1024;
  private static final byte[] SERVER_SAY = "Server say:".getBytes();

  // @Override
  // public void readCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
  // this.setNextState(IoState.WRITE);
  // }

  @Override
  public void read(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
    logger.debug("reading ...");
    if (readBuf.position() > 1) {
      byte b = readBuf.getByte(readBuf.position() - 1);
      if (b == (byte) '\n') {
        write(readBuf, writeBuf);
        setNextState(IoState.WRITE);
        return;
      }
    }
    remain(BUF_SIZE, IoState.READ);
  }

  // @Override
  // public void writeCompleted(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
  // /** Session未关闭，则继续读取IO,同时position复位 */
  // readBuf.position(0);
  // remain(BUF_SIZE, IoState.READ);
  // }

  @Override
  public void open(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
    logger.debug("Set  Session[" + this.getId() + "] for READ");
    remain(BUF_SIZE, IoState.READ);
  }

  @Override
  public void close() {
    logger.debug("Session closed");
  }

  public String toString() {
    return "Sessson [" + this.getId() + "]";
  }

  @Override
  public void write(IoBuffer readBuf, IoBuffer writeBuf) throws Exception {
    logger.debug("writing ...");
    int len = readBuf.position();
    writeBuf.position(0);
    writeBuf.writeBytes("Server say:".getBytes());
    writeBuf.writeBytes(readBuf.readBytes(0, len));
    writeBuf.position(0);
    // writeBuf.limit(SERVER_SAY.length + len);
  }

}
