package com.github.jnet.example.httpd;

public interface Servlet {

  void doRequest(Request request, Response response) throws Exception;
}
