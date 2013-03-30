package jnet.core.server;

public class Settings {
	/**
	 * 工作线程数
	 */
	public int threads;
	/**
	 * 端口
	 */
	public int port;
	/**
	 * 读超时（ms）
	 */
	public int readTimeout;
	/**
	 * 写超时（ms）
	 */
	public int writeTimeout;
	/**
	 * IP
	 */
	public String ip;
	/**
	 * 最大连接数
	 */
	public int maxConnection;
	/**
	 * 是否长连接
	 */
	//public boolean keepalive;

}
