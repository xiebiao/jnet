package com.github.jnet;

import java.io.UnsupportedEncodingException;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.Client;

public class Ab implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Ab.class);
	private Client con;
	private String message;

	Ab(Client con, String message) {
		this.con = con;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			con.connect();
			byte[] stream = message.getBytes("UTF-8");
			con.write(stream);
			StringBuffer sb = new StringBuffer();
			while (true) {
				byte[] b = con.read(1);
				//if(b[0]=='\n')break;
				if (b != null && b.length == 1) {
					sb.append(new String(b, "UTF-8"));
					
				} else {
					break;
				}
			}
			logger.info(sb.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}