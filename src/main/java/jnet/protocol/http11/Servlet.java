package jnet.protocol.http11;

public interface Servlet {
	public void doRequest(Request request, Response response) throws Exception;
}
