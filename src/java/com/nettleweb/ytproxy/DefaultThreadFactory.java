package com.nettleweb.ytproxy;

import java.util.concurrent.*;

final class DefaultThreadFactory implements ThreadFactory {
	public static final DefaultThreadFactory instance = new DefaultThreadFactory();

	private DefaultThreadFactory() {}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, "Worker");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.setDaemon(true);
		return thread;
	}
}