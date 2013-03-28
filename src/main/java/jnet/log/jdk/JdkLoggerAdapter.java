package jnet.log.jdk;

import jnet.log.Logger;

public class JdkLoggerAdapter implements Logger {
	private java.util.logging.Logger logger;

	public JdkLoggerAdapter(String name) {
		logger = java.util.logging.Logger.getLogger(name);
	}

	@Override
	public void info(String msg) {
		logger.info(msg);

	}

	@Override
	public void warn(String msg) {
		logger.log(java.util.logging.Level.WARNING, msg);
	}

	@Override
	public void debug(String msg) {
		logger.log(java.util.logging.Level.FINE, msg);
	}

	@Override
	public void error(String msg) {
		logger.log(java.util.logging.Level.SEVERE, msg);

	}

	@Override
	public void warn(String format, Throwable e) {
		logger.log(java.util.logging.Level.WARNING, format, e);

	}
}
