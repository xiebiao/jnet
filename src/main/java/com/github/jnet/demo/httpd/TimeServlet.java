package com.github.jnet.demo.httpd;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServlet implements Servlet {

    public void doRequest(Request request, Response response) throws Exception {
        response.setStatusCode(200);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        response.write(sdf.format(new Date()));
    }

}
