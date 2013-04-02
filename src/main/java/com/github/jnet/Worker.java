package com.github.jnet;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jnet.utils.IOBuffer;
import com.github.jnet.utils.IOUtils;

public class Worker implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Worker.class);
	private Selector selector;
	private Configuration config;
	private Queue<Session> newSessionQueue = new ConcurrentLinkedQueue<Session>();
	private List<Session> eventSessionList = new ArrayList<Session>();
	private Set<Session> timeoutSessionSet = new TreeSet<Session>(
			new Comparator<Session>() {
				public int compare(Session a, Session b) {
					if (a.getNextTimeout() - b.getNextTimeout() == 0) {
						return 0;
					} else if (a.getNextTimeout() - b.getNextTimeout() > 0) {
						return 1;
					} else {
						return -1;
					}
				}
			});

	public Worker(Configuration config) throws IOException {
		this.config = config;
		selector = Selector.open();
	}

	/**
	 * 设置超时的session加入到超时队列中
	 * 
	 * @param session
	 */
	public void addTimeoutSession(Session session) {
		logger.debug("Session[" + session.getId() + "] is time out.");
		if (session.getNextTimeout() > 0) {
			timeoutSessionSet.add(session);
		}
	}

	public void run() {
		try {
			dispatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 轮询调度非阻塞请求
	 * 
	 * @throws IOException
	 */
	public void dispatch() throws IOException {

		while (true) {
			select();
		}
	}

	private void select() throws IOException {

		long timeout = 0;
		Iterator<Session> sessionIter = timeoutSessionSet.iterator();
		if (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			timeout = session.getNextTimeout() - System.currentTimeMillis();
			timeout = Math.max(timeout, 1);
		}
		selector.select(timeout);
		long now = System.currentTimeMillis();
		handleNewSession();
		checkEventSession(now);
		handleEventSession();
	}

	public void handleNewSession() {
		while (true) {
			Session session = newSessionQueue.poll();
			if (session == null) {
				break;
			}
			initNewSession(session);
		}
	}

	public void addNewSession(Session session) {
		newSessionQueue.add(session);
		selector.wakeup();
	}

	/**
	 * 初始化新会话
	 * 
	 * @param session
	 */
	public void initNewSession(Session session) {
		try {
			/** 初始化buffer */
			session.getReadBuffer().position(0);
			session.getReadBuffer().limit(0);
			session.getWriteBuffer().position(0);
			session.getWriteBuffer().limit(0);

			session.open(session.getReadBuffer(), session.getWriteBuffer());
			updateSession(session);
		} catch (Exception e) {
			close(session);
			e.printStackTrace();
		}

	}

	private void updateSession(Session session) throws ClosedChannelException {
		if (session.getCurrentState() == IOState.READ
				&& session.getReadBuffer().remaining() > 0) {
			if (this.config.getReadTimeout() > 0) {
				/** 根据配置设置读取超时 */
				if (session.getNextTimeout() > System.currentTimeMillis()) {
					/** 读取已超时 */
					session.setNextTimeout(System.currentTimeMillis()
							+ this.config.getReadTimeout());
					addTimeoutSession(session);
				}
			}
			session.getSocket().register(selector, SelectionKey.OP_READ,
					session);

		} else if (session.getCurrentState() == IOState.WRITE
				&& session.getWriteBuffer().remaining() > 0) {
			if (this.config.getWriteTimeout() > 0) {
				/** 根据配置设置写超时 */
				if (session.getNextTimeout() > System.currentTimeMillis()) {
					/** 读取已超时 */
					session.setNextTimeout(System.currentTimeMillis()
							+ this.config.getReadTimeout());
					addTimeoutSession(session);
				}
			}
			session.getSocket().register(selector, SelectionKey.OP_WRITE,
					session);
		} else {
			/** 关闭 session */
			close(session);
		}
	}

	/**
	 * 检测有IO或超时事件的session，并加入到eventSessionList中
	 * 
	 * @param time
	 */
	private void checkEventSession(long time) {
		eventSessionList.clear();
		Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
		while (keyIter.hasNext()) {
			SelectionKey key = keyIter.next();
			keyIter.remove();
			Session session = (Session) key.attachment();
			// session.setCurrentEvent(session.getCurrentState() ==
			// Session.EVENT_READ ? Session.EVENT_READ
			// : Session.EVENT_WRITE);
			eventSessionList.add(session);
			// timeoutSessionSet.remove(session);
		}

		Iterator<Session> sessionIter = timeoutSessionSet.iterator();
		while (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			if (session.getNextTimeout() <= time) {
				session.setCurrentEvent(SessionEvent.TIMEOUT);
				eventSessionList.add(session);
				sessionIter.remove();
			} else {
				break;
			}
		}

	}

	/**
	 * 处理当前有事件session 3种事件：可读、可写、超时
	 */
	private void handleEventSession() {
		Iterator<Session> eventIter = eventSessionList.iterator();
		while (eventIter.hasNext()) {
			Session session = eventIter.next();
			try {
				if (session.getCurrentEvent() == SessionEvent.TIMEOUT) {
					timeoutEvent(session);
				} else if (session.getCurrentState() == IOState.READ) {
					readEvent(session);
				} else if (session.getCurrentState() == IOState.WRITE) {
					writeEvent(session);
				}
			} catch (Exception e) {
				eventIter.remove();
				close(session);
			}
		}
	}

	/**
	 * 处理超时事件
	 * 
	 * @param session
	 * @throws Exception
	 */
	private void timeoutEvent(Session session) {
		try {
			session.timeout();
			updateSession(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ioEvent(Session session, IOBuffer buf) throws Exception {
		while (buf.remaining() > 0) {
			int len = 0;
			IOState curState = session.getCurrentState();
			if (curState == IOState.READ) {
				len = IOUtils.read(session, session.getSocket(), buf);
			} else {
				len = IOUtils.write(session, session.getSocket(), buf);
			}
			int remain = buf.remaining();

			if (curState == IOState.READ) {
				if (remain == 0) {
					session.complateRead(session.getReadBuffer(),
							session.getWriteBuffer());
					logger.debug("Session[" + session.getId()
							+ "] is readComplated.");
				} else {
					session.reading(session.getReadBuffer(),
							session.getWriteBuffer());
				}
			} else {
				if (remain == 0) {
					session.complateWrite(session.getReadBuffer(),
							session.getWriteBuffer());
					logger.debug("Session[" + session.getId()
							+ "] is writeComplated.");
				} else {
					session.writing(session.getReadBuffer(),
							session.getWriteBuffer());
				}
			}
			if (len == 0 || session.getCurrentState() != curState) {
				/** 更新session状态 */
				updateSession(session);
				break;
			}
		}
	}

	/**
	 * 处理读事件
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void readEvent(Session session) throws Exception {
		ioEvent(session, session.getReadBuffer());
	}

	/**
	 * 处理写事件
	 * 
	 * @param session
	 * @throws ClosedChannelException
	 */
	public void writeEvent(Session session) throws Exception {
		ioEvent(session, session.getWriteBuffer());
	}

	/**
	 * 关闭session 用于清理
	 * 
	 * @param session
	 */
	protected void close(Session session) {
		if (session.getSocket() != null) {
			try {
				if (session.getSocket().keyFor(selector) != null) {
					session.getSocket().keyFor(selector).cancel();
				}
				if (session.getSocket().isConnected()) {
					session.getSocket().close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			session.setSocket(null);
		}
		timeoutSessionSet.remove(session);
		SessionManager.closeSession(session);
		session.close();
	}

}
