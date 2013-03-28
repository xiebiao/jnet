package jnet.log;

import jnet.log.jdk.JdkLoggerAdapter;
import jnet.log.slf4j.Slf4jLoggerAdapter;

public final class LoggerFactory {
	private LoggerFactory() {
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String className) {
		try {
			Class.forName("org.slf4j.LoggerFactory");
		} catch (ClassNotFoundException e) {
			return new JdkLoggerAdapter(className);
		}
		return new Slf4jLoggerAdapter(className);
	}

	public static final void main(String args[]) {
		Logger log = LoggerFactory.getLogger(LoggerFactory.class);
		log.debug("test");
		log.info("test");
		log.error("test");
	}
}
