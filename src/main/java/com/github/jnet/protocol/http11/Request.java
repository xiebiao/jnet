package com.github.jnet.protocol.http11;

import java.util.HashMap;
import java.util.Map;

public class Request {

	public Map<String, String> header = new HashMap<String, String>();
	public Map<String, String> cookie = new HashMap<String, String>();
	public Map<String, String> params = new HashMap<String, String>();

	public void reset() {
		header.clear();
		cookie.clear();
		params.clear();
	}

	public String toString() {
		return header.toString() + "\n" + params.toString();
	}
}
