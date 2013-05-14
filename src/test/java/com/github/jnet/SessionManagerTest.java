package com.github.jnet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.github.jnet.demo.httpd.HttpSession;

public class SessionManagerTest extends TestCase {

    public void setUp() {

    }

    class ThreadInit implements java.lang.Runnable {

        SessionManager sm;

        public ThreadInit(SessionManager sm) {
            this.sm = sm;
        }

        @Override
        public void run() {
            try {
                sm.initialize(HttpSession.class, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void test_threads_init() {
        SessionManager sm = new SessionManager();
        for (int i = 0; i < 10; i++) {
            new Thread(new ThreadInit(sm)).start();
        }
    }

    public void test_destroy() {
        SessionManager sm = new SessionManager();
        try {
            sm.destroy();
            sm.destroy();
        } catch (Exception e) {
            Assert.assertEquals(true, true);
        }
    }
}
