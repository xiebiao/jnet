package jnet.protocol.http11;

import jnet.core.server.Session;
import jnet.core.utils.IOBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSession extends Session {
	private static final Logger logger = LoggerFactory
			.getLogger(HttpSession.class);

	static final int BUF_SIZE = 2048;
	static final int STATE_READ_HEAD = 0;
	static final int STATE_READ_BODY = 1;

	Request request = new Request();
	Response response = new Response();
	int state = STATE_READ_HEAD;
	int bodyLen = 0;
	int bodyStartPos = 0;

	void parseHeader(String header) throws Exception {

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
		// 解析参数
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

		// 解析cookie
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
	public void reading(IOBuffer readBuf, IOBuffer writeBuf)
			throws Exception {
		logger.debug(this.toString() + " reading...");
		if (state == STATE_READ_HEAD) {
			String buf = readBuf.getString("ASCII");
			int endPos = buf.indexOf("\r\n\r\n");
			if (endPos == -1) {
				// header不完整，继续接收
				remainToRead(BUF_SIZE);
			}
			// 解析header
			state = STATE_READ_BODY;
			String header = buf.substring(0, endPos);
			parseHeader(header);
			bodyStartPos = endPos + 4;
		}
		if (state == STATE_READ_BODY) {
			if (bodyStartPos + bodyLen > readBuf.position()) {
				// body不完整，继续接收
				remainToRead(bodyStartPos + bodyLen - readBuf.position());
				return;
			}
			// 解析body
			state = STATE_READ_HEAD;
			String body = readBuf.getString(bodyStartPos, bodyLen, "ASCII");
			parseBody(body);

			// 执行servlet
			handle(readBuf, writeBuf);
			setNextState(STATE_WRITE);
			return;
		}
		remainToRead(BUF_SIZE);

	}

	public void handle(IOBuffer readBuf, IOBuffer writeBuf) throws Exception {
		// 执行servlet
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
		logger.debug("Finished a request");
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
}
