package com.github.jnet;

public class Configuration {

    /**
     * 工作线程数
     */
    private int    threadNumber;
    /**
     * 端口
     */
    private int    port;
    /**
     * 读超时（ms）
     */
    private int    readTimeout;
    /**
     * 写超时（ms）
     */
    private int    writeTimeout;
    /**
     * IP
     */
    private String ip;
    /**
     * 最大连接数
     */
    private int    maxConnection;

    /**
     * 是否长连接
     */
    public Configuration() {
        this.threadNumber = Runtime.getRuntime().availableProcessors() * 2;
        this.port = 8080;
        this.maxConnection = 100;
        this.readTimeout = 3000;// 3秒
        this.writeTimeout = 3000;// 3秒
        this.ip = "127.0.0.1";
    }

    /**
     * 工作线程数
     * @return
     */
    public int getThreadNumber() {
        return threadNumber;
    }

    public void setThreadNumber(int threadNumber) {
        this.threadNumber = threadNumber;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
    }

    public String toString() {
        return "{ip=" + ip + ", port=" + port + ", threadNumber=" + threadNumber + ", maxConnection=" + maxConnection
                + ",readTimeout=" + readTimeout + ", writeTimeout=" + writeTimeout + "}";
    }
}
