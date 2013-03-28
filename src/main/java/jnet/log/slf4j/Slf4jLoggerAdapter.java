package jnet.log.slf4j;

import jnet.log.Logger;

public class Slf4jLoggerAdapter implements Logger {
	private org.slf4j.Logger logger;

	public Slf4jLoggerAdapter(String className) {
		logger = org.slf4j.LoggerFactory.getLogger(className);
	}

	@Override
	public void info(String msg) {
		logger.info(msg);

	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);

	}

	@Override
	public void debug(String msg) {
		logger.debug(msg);

	}

	@Override
	public void error(String msg) {
		logger.error(msg);

	}

	@Override
	public void warn(String format, Throwable e) {
		logger.warn(format, e);

	}

}
