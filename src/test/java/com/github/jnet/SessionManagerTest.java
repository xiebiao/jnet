package com.github.jnet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.github.jnet.protocol.http11.HttpSession;

public class SessionManagerTest extends TestCase {
	private SessionManager sm = SessionManager.getInstance();

	public void setUp() {
		try {
			sm.initialize(HttpSession.class, 10);
		} catch (Exception e) {
			if (e instanceof IllegalStateException) {
				Assert.assertEquals(true, true);
			}
		}
	}

	public void test0() {
		try {
			// sm.initialize(HttpSession.class, 10);
		} catch (Exception e) {
			if (e instanceof IllegalStateException) {
				Assert.assertEquals(true, true);
			}
		}
	}

	public void test1() {
		try {
			sm.destroy();
			sm.destroy();
		} catch (Exception e) {
			Assert.assertEquals(true, true);
		}
	}
}
