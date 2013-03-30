package jnet.core.server;

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

import jnet.core.util.IOBuffer;
import jnet.core.util.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(Worker.class);
	Selector selector;
	Boolean lock = false;
	Queue<Session> newSessionQueue = new ConcurrentLinkedQueue<Session>();
	/**
	 * 超时的session集合， TreeSet保证集合中的session按超时时间升序排列
	 */
	Set<Session> timeoutSessionSet = new TreeSet<Session>(
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
	/**
	 * 有IO事件或超时的session队列
	 */
	List<Session> eventSessionList = new ArrayList<Session>();

	public Worker() throws IOException {
		selector = Selector.open();
	}

	/**
	 * 设置超时的session加入到超时队列中
	 * 
	 * @param session
	 */
	public void addTimeoutSession(Session session) {
		logger.debug("Add time out " + session);
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

	public void select() throws IOException {

		long timeout = 0;
		Iterator<Session> sessionIter = timeoutSessionSet.iterator();
		if (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			timeout = session.getNextTimeout() - System.currentTimeMillis();
			timeout = Math.max(timeout, 1);
		}
		selector.select(timeout);
		long now = System.currentTimeMillis();

		// 处理新来的session
		handleNewSession();
		// 检查有IO或超时session
		checkEventSession(now);
		// 处理session事件
		handleEventSession();
	}

	/**
	 * 处理新增连接
	 */
	public void handleNewSession() {
		while (true) {
			Session session = newSessionQueue.poll();
			if (session == null) {
				break;
			}
			logger.debug("Handle a new " + session);
			initNewSession(session);
		}
	}

	/**
	 * 为工作线程增加一个session，server线程中调用
	 * 
	 * @param session
	 */
	public void addNewSession(Session session) {
		newSessionQueue.add(session);
		// 唤醒阻塞中的select
		selector.wakeup();
	}

	/**
	 * 初始化新增连接
	 * 
	 * @param session
	 */
	public void initNewSession(Session session) {
		try {
			// 初始化buffer
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

	/**
	 * 当执行回调函数后更新session
	 * 
	 * @param session
	 * @throws ClosedChannelException
	 */
	private void updateSession(Session session) throws ClosedChannelException {
		// 根据session状态注册读或写事件
		if (session.getCurrentState() == Session.STATE_READ
				&& session.getReadBuffer().remaining() > 0) {
			if (session.getConfig().readTimeout > 0) {
				session.setNextTimeout(System.currentTimeMillis()
						+ session.getConfig().readTimeout);
			}
			session.getSocket().register(selector, SelectionKey.OP_READ,
					session);
			// 加入到超时session队列
			addTimeoutSession(session);
		} else if (session.getCurrentState() == Session.STATE_WRITE
				&& session.getWriteBuffer().remaining() > 0) {
			if (session.getConfig().writeTimeout > 0) {
				session.setNextTimeout(System.currentTimeMillis()
						+ session.getConfig().writeTimeout);
			}
			session.getSocket().register(selector, SelectionKey.OP_WRITE,
					session);
			// 加入到超时session队列
			addTimeoutSession(session);
		} else {
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
			session.setEvent(session.getCurrentState() == Session.EVENT_READ ? Session.EVENT_READ
					: Session.EVENT_WRITE);
			eventSessionList.add(session);
			timeoutSessionSet.remove(session);
		}

		Iterator<Session> sessionIter = timeoutSessionSet.iterator();
		while (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			if (session.getNextTimeout() <= time) {
				session.setEvent(Session.EVENT_TIMEOUT);
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
		// 处理事件
		Iterator<Session> eventIter = eventSessionList.iterator();
		while (eventIter.hasNext()) {
			Session session = eventIter.next();
			try {
				if (session.getEvent() == Session.EVENT_TIMEOUT) {
					timeoutEvent(session);
				} else if (session.getCurrentState() == Session.STATE_READ) {
					readEvent(session);
				} else if (session.getCurrentState() == Session.STATE_WRITE) {
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
			session.timeout(session.getReadBuffer(), session.getWriteBuffer());
			updateSession(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ioEvent(Session session, IOBuffer buf) throws Exception {
		while (buf.remaining() > 0) {
			int len = 0;
			int curState = session.getCurrentState();
			if (curState == Session.STATE_READ) {
				len = IOUtils.read(session.getSocket(), buf);// 读数据
			} else {
				len = IOUtils.write(session.getSocket(), buf);// 读数据
			}
			int remain = buf.remaining();// 剩余字节数

			if (curState == Session.STATE_READ) {
				if (remain == 0) {
					// 当前数据IO完成
					session.complateRead(session.getReadBuffer(),
							session.getWriteBuffer());
				} else {
					// 当前数据IO未完成
					session.complateReadOnce(session.getReadBuffer(),
							session.getWriteBuffer());
				}
			} else {
				if (remain == 0) {
					// 当前数据IO完成
					session.complateWrite(session.getReadBuffer(),
							session.getWriteBuffer());
				} else {
					// 当前数据IO未完成
					session.complateWriteOnce(session.getReadBuffer(),
							session.getWriteBuffer());
				}
			}
			if (len == 0 || session.getCurrentState() != curState) {
				// session状态切换
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
		logger.debug(session + " read");
		ioEvent(session, session.getReadBuffer());
	}

	/**
	 * 处理写事件
	 * 
	 * @param session
	 * @throws ClosedChannelException
	 */
	public void writeEvent(Session session) throws Exception {
		logger.debug(session + ", write");
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
		// 执行close回调函数
		session.close();
	}

}
