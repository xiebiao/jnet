package com.github.jnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;

/**
 * <p>
 * 会话管理
 * </p>
 * 
 * @author xiebiao
 * 
 */
public final class SessionManager {
	private static final Logger logger = LoggerFactory
			.getLogger(SessionManager.class);
	private static List<Session> sessionList = new ArrayList<Session>();
	private static Boolean lock = false;
	private static SessionManager sessionManager = new SessionManager();

	private SessionManager() {
	}

	public static SessionManager getInstance() {
		return sessionManager;
	}

	public Session getSession() {
		synchronized (lock) {
			Iterator<Session> sessionIter = sessionList.iterator();
			while (sessionIter.hasNext()) {
				Session session = sessionIter.next();
				if (!session.isInuse()) {
					session.setInuse(true);
					logger.info("Get Session[" + session.getId()
							+ "] from pool.");
					return session;
				}
			}
		}
		return null;
	}

	public void close(Session session) {
		logger.info("Session[" + session.getId()
				+ "] is closed,put it back to pool.");
		synchronized (lock) {
			session.setInuse(false);
		}
	}

	public void destroy() {
		synchronized (lock) {
			for (int i = 0; i < sessionList.size(); i++) {
				Session session = sessionList.get(i);
				session.setNextState(IOState.CLOSE);
				session = null;
			}
			sessionList = null;
		}
	}

	public <T> void initialize(Class<T> clazz, int capacity) throws Exception {
		if (sessionList.size() > 0) {
			throw new java.lang.IllegalStateException(
					"Session pool has initialized");
		}
		for (int i = 0; i < capacity; i++) {
			Object obj = clazz.newInstance();
			Session session = (Session) obj;
			session.setId(i);
			session.setCurrentEvent(SessionEvent.READ);
			session.setInuse(false);
			session.setReadBuffer(new IOBuffer());
			session.setWriteBuffer(new IOBuffer());
			sessionList.add(session);
		}
	}

	public static void addSession(Session session) {
		sessionList.add(session);
	}
}
