package com.github.jnet;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IoBuffer;

/**
 * 会话管理
 * @author xiebiao
 */
public final class SessionManager {

    private static final Logger logger      = LoggerFactory.getLogger(SessionManager.class);
    private List<AbstractSession>       sessionList = new ArrayList<AbstractSession>();
    private volatile Boolean    lock        = false;

    public SessionManager() {}

    public AbstractSession getSession() {
        synchronized (lock) {
            Iterator<AbstractSession> sessionIter = sessionList.iterator();
            while (sessionIter.hasNext()) {
                AbstractSession session = sessionIter.next();
                if (!session.isIdle()) {
                    session.setIdle(true);
                    logger.info("Get Session[" + session.getId() + "] from pool.");
                    return session;
                }
            }
        }
        return null;
    }

    public void close(AbstractSession session) {
        session.setIdle(false);
        logger.info("Session[" + session.getId() + "] " + "is idle.");
    }

    public void destroy() {
        synchronized (lock) {
            for (int i = 0; i < sessionList.size(); i++) {
                AbstractSession session = sessionList.remove(i);
                session.setNextState(AbstractSession.IoState.CLOSE);
                try {
                    SocketChannel s = session.getSocket();
                    if (s != null && s.isOpen()) {
                        s.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                session = null;
            }
            sessionList = null;
            logger.debug("Session pool is destroyed.");
        }
    }

    public <E> void initialize(Class<E> clazz, int capacity) throws Exception {
        synchronized (lock) {
//            if (sessionList != null && sessionList.size() > 0) {
//                throw new java.lang.IllegalStateException("Session pool has initialized.");
//            }
            for (int i = 0; i < capacity; i++) {
                Object obj = clazz.newInstance();
                AbstractSession session = (AbstractSession) obj;
                session.setId(i);
                session.setCurrentEvent(AbstractSession.Event.READ);
                session.setIdle(false);
                session.setReadBuffer(new IoBuffer());
                session.setWriteBuffer(new IoBuffer());
                sessionList.add(session);
            }
        }
    }
}
