package jnet.demo.echoserver;

import jnet.core.server.Session;
import jnet.core.util.IOBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoSession extends Session {
	private static final Logger logger = LoggerFactory
			.getLogger(EchoSession.class);
	static final int BUF_SIZE = 1024;

	@Override
	public void complateRead(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception {
		reading(readBuf, writeBuf);
	}

	@Override
	public void reading(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		logger.debug(this.toString() + " reading...");
		if (readBuf.position() > 1) {
			byte b = readBuf.getByte(readBuf.position() - 1);
			if (b == (byte) '\n') {
				int len = readBuf.position();
				writeBuf.position(0);
				writeBuf.writeBytes(readBuf.readBytes(0, len));

				writeBuf.position(0);
				writeBuf.limit(len);
				setNextState(STATE_WRITE);
				return;
			}
		}
		remainToRead(BUF_SIZE);
	}

	@Override
	public void complateWrite(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception {

		readBuf.position(0);
		remainToRead(BUF_SIZE);
	}

	@Override
	public void open(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {

		remainToRead(BUF_SIZE);
	}

	@Override
	public void close() {

	}

	public String toString() {
		return "sessson:{id=" + this.getId() + "}";
	}

	@Override
	public void writing(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		logger.debug(this.toString() + " writing...");

	}

}
