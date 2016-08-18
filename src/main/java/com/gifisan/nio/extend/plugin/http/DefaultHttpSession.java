package com.gifisan.nio.extend.plugin.http;

import java.io.IOException;

import com.gifisan.nio.common.UUIDGenerator;
import com.gifisan.nio.component.AttributesImpl;
import com.gifisan.nio.component.Session;
import com.gifisan.nio.component.protocol.future.ReadFuture;

public class DefaultHttpSession extends AttributesImpl implements HttpSession {

	private long		createTime	= System.currentTimeMillis();

	private Session	ioSession;

	private long		lastAccessTime;

	private String		sessionID;

	private HttpContext	context;

	protected DefaultHttpSession(HttpContext context, Session ioSession) {
		this.context = context;
		this.ioSession = ioSession;
		this.sessionID = UUIDGenerator.random().toString();
	}

	protected DefaultHttpSession(HttpContext context, Session ioSession, String sessionID) {
		this.context = context;
		this.ioSession = ioSession;
		this.sessionID = sessionID;
	}

	public void active(Session ioSession) {
		this.ioSession = ioSession;
		this.lastAccessTime = System.currentTimeMillis();
	}

	public void flush(ReadFuture future) throws IOException {
		ioSession.flush(future);
	}

	public long getCreateTime() {
		return createTime;
	}

	public Session getIOSession() {
		return ioSession;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public String getSessionID() {
		return sessionID;
	}

	public boolean isValidate() {
		return System.currentTimeMillis() - lastAccessTime > 1000 * 60 * 30;
	}

	public HttpContext getContext() {
		return context;
	}
}
