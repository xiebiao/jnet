package com.github.jnet.example.httpd;

public interface Servlet {

    public void doRequest(Request request, Response response) throws Exception;
}
