package com.nettleweb.client;

import java.io.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;

final class LoggerThread implements Runnable {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS zzz", Locale.ROOT);
	private static final LoggerThread instance;

	static {
		Thread thread = new Thread(instance = new LoggerThread());
		thread.setName("Logger");
		thread.setDaemon(true);
		thread.setPriority(3);
		thread.start();
	}

	private LoggerThread() {
		if (instance != null)
			throw new IllegalStateException("Invalid constructor call");
	}

	private final List<String> info = new ArrayList<>();
	private final List<String> warn = new ArrayList<>();
	private final List<String> err = new ArrayList<>();

	static void info(String data) {
		instance.info.add(data);
	}

	static void warn(String data) {
		instance.warn.add(data);
	}

	static void err(String data) {
		instance.err.add(data);
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				break;
			}

			Date now = new Date(System.currentTimeMillis());

			while (!info.isEmpty()) {
				byte[] buf =
						("[CONSOLE LOG] " + dateFormat.format(now) + " " + info.remove(0) + "\n").getBytes(StandardCharsets.UTF_8);

				try {
					OutputStream stream = Console.out;
					stream.write(buf, 0, buf.length);
					stream.flush();
				} catch (Exception e) {
					// ignore
				}
			}

			while (!warn.isEmpty()) {
				byte[] buf =
						("[CONSOLE WARN] " + dateFormat.format(now) + " " + warn.remove(0) + "\n").getBytes(StandardCharsets.UTF_8);

				try {
					OutputStream stream = Console.out;
					stream.write(buf, 0, buf.length);
					stream.flush();
				} catch (Exception e) {
					// ignore
				}
			}

			while (!err.isEmpty()) {
				byte[] buf =
						("[CONSOLE ERROR] " + dateFormat.format(now) + " " + err.remove(0) + "\n").getBytes(StandardCharsets.UTF_8);

				try {
					OutputStream stream = Console.err;
					stream.write(buf, 0, buf.length);
					stream.flush();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
}