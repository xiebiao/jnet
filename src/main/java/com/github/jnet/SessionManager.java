package com.github.jnet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static Session getSession() {
		synchronized (lock) {
			Iterator<Session> sessionIter = sessionList.iterator();
			while (sessionIter.hasNext()) {
				Session session = sessionIter.next();
				if (!session.isInuse()) {
					session.setInuse(true);
					logger.debug("Get Session[" + session.getId()
							+ "] from pool.");
					return session;
				}
			}
		}
		return null;
	}

	public static void closeSession(Session session) {
		logger.debug("Session[" + session.getId()
				+ "] is closed,put it back to pool.");
		synchronized (lock) {
			session.setInuse(false);
		}
	}

	public static void addSession(Session session) {
		sessionList.add(session);
	}
}
