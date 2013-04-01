package com.github.jnet.protocol.http11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.Session;
import com.github.jnet.utils.IOBuffer;

public class HttpSession extends Session {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpSession.class);

	private static final int BUF_SIZE = 2048;// (2M)
	private static final int STATE_READ_HEAD = 0;
	private static final int STATE_READ_BODY = 1;

	private Request request = new Request();
	private Response response = new Response();
	private int currentState = STATE_READ_HEAD;
	private int bodyLen = 0;
	private int bodyStartPos = 0;

	public HttpSession() {
		request = new Request();
		response = new Response();
		currentState = STATE_READ_HEAD;
		bodyLen = 0;
		bodyStartPos = 0;
	}

	private void parseHeader(String header) throws Exception {
		logger.debug(this.toString() + "Parse HTTP Header");
		String[] lines = header.split("\r\n");
		if (lines.length == 0) {
			throw new Exception("invalid header");
		}

		String hline = lines[0];
		String[] row = hline.split(" ");
		if (row.length != 3) {
			throw new Exception("invalid header");
		}
		if (!row[0].equals("GET") && !row[0].equals("POST")) {
			throw new Exception("invalid header");
		}

		request.header.put(HttpAttr.HEAD_METHOD, row[0].trim());
		request.header.put(HttpAttr.HEAD_URL, row[1].trim());
		request.header.put(HttpAttr.HEAD_VERSION, row[2].trim());

		for (String line : lines) {
			row = line.split(": ");
			if (row.length != 2) {
				continue;
			}
			request.header.put(row[0].trim(), row[1].trim());
		}

		if (!request.header.containsKey(HttpAttr.HEAD_CONTENT_LEN)) {
			request.header.put(HttpAttr.HEAD_CONTENT_LEN, "0");
		}

		bodyLen = Integer.parseInt(request.header
				.get(HttpAttr.HEAD_CONTENT_LEN));
		if (bodyLen < 0) {
			throw new Exception("invalid header");
		}
		logger.debug(request.toString());
	}

	void parseBody(String body) {
		logger.debug(this.toString() + "Parse HTTP Body");
		String paramStr = body;
		String url = request.header.get(HttpAttr.HEAD_URL);
		int paramPos = url.indexOf("?");
		if (paramPos >= 0) {
			paramStr = url.substring(paramPos + 1) + paramStr;
		}

		String[] params = paramStr.split("&");
		String[] row;
		for (String line : params) {
			row = line.split("=");
			if (row.length != 2) {
				continue;
			}
			request.params.put(row[0].trim(), row[1].trim());
		}

		if (request.header.containsKey(HttpAttr.HEAD_COOKIE)) {
			String cookieStr = request.header.get(HttpAttr.HEAD_COOKIE);
			String[] cookies = cookieStr.split(";");
			for (String line : cookies) {
				row = line.split("=");
				if (row.length != 2) {
					continue;
				}
				request.cookie.put(row[0].trim(), row[1].trim());
			}
		}
	}

	@Override
	public void complateRead(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception {
		reading(readBuf, writeBuf);
	}

	@Override
	public void reading(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {

		logger.debug("Poccess the Session[" + this.getId() + "].");
		if (currentState == STATE_READ_HEAD) {
			String buf = readBuf.getString("ASCII");
			int endPos = buf.indexOf("\r\n\r\n");
			if (endPos == -1) {
				remainToRead(BUF_SIZE);
			}
			currentState = STATE_READ_BODY;
			String header = buf.substring(0, endPos);
			parseHeader(header);
			bodyStartPos = endPos + 4;
		}
		if (currentState == STATE_READ_BODY) {
			if (bodyStartPos + bodyLen > readBuf.position()) {
				remainToRead(bodyStartPos + bodyLen - readBuf.position());
				return;
			}
			currentState = STATE_READ_HEAD;
			String body = readBuf.getString(bodyStartPos, bodyLen, "ASCII");
			parseBody(body);
			handle(readBuf, writeBuf);
			setNextState(STATE_WRITE);
			return;
		}
		remainToRead(BUF_SIZE);
	}

	public void handle(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		Servlet action = ServletFactory.get(request);
		if (action == null) {
			throw new Exception("action not found");
		}
		action.doRequest(request, response);
		writeBuf.position(0);
		writeBuf.writeBytes(response.toBytes());
		writeBuf.limit(writeBuf.position());
		writeBuf.position(0);
		request.reset();
		response.reset();
		logger.debug("Write buffer to Session[" + this.getId() + "].");
	}

	@Override
	public void complateWrite(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception {
		setNextState(STATE_CLOSE);
	}

	@Override
	public void open(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		remainToRead(BUF_SIZE);
	}

	public String toString() {
		return "Session[" + this.getId() + "] ";
	}

	@Override
	public void writing(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		logger.debug(this.toString() + " writing...");

	}

	@Override
	public void close() {
		this.request.reset();
		this.response.reset();
		this.currentState = STATE_READ_HEAD;
		this.bodyLen = 0;
		this.bodyStartPos = 0;
	}
}
