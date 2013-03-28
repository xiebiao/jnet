package jnet.log;

public interface Logger {

	void info(String msg);

	void warn(String msg);

	void warn(String format, Throwable e);

	void debug(String msg);

	void error(String msg);

}
