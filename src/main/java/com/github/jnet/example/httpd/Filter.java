package com.github.jnet.example.httpd;

public interface Filter {

  /**
   * 根据请求的上下文获取servlet实例 ，servlet实例为线程安全，每次请求会创建一个
   * 
   * @param request
   * @return
   */
  Servlet getServlet(Request request);
}
