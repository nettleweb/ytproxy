package com.nettleweb.client;

import java.io.*;
import java.lang.annotation.*;
import java.nio.charset.*;

public final class Console {
	private Console() {}

	@Native
	public static final InputStream in;

	@Native
	public static final OutputStream out;

	@Native
	public static final OutputStream err;

	static {
		if (VM.useNative) {
			in = VM.stdin;
			out = VM.stdout;
			err = VM.stderr;
		} else {
			in = System.in;
			out = System.out;
			err = System.err;
		}
	}

	private static String objectToString(Object obj) {
		if (obj == null)
			return "null";
		if (obj instanceof String)
			return (String) obj;

		if (obj instanceof CharSequence seq) {
			int length = seq.length();
			char[] data = new char[length];

			for (int i = 0; i < length; i++)
				data[i] = seq.charAt(i);

			return new String(data);
		}

		if (obj instanceof Throwable) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			((Throwable) obj).printStackTrace(new PrintStream(stream));
			return stream.toString(StandardCharsets.UTF_8);
		}

		return obj.toString();
	}

	public static void log(Object... data) {
		StringBuilder builder = new StringBuilder();
		for (Object obj : data)
			builder.append(objectToString(obj).trim()).append(" ");

		log(builder.substring(0, builder.length() - 1));
	}

	public static void log(String data) {
		LoggerThread.info(data);
	}

	public static void warn(Object... data) {
		StringBuilder builder = new StringBuilder();
		for (Object obj : data)
			builder.append(objectToString(obj).trim()).append(" ");

		warn(builder.substring(0, builder.length() - 1));
	}

	public static void warn(String data) {
		LoggerThread.warn(data);
	}

	public static void error(Object... data) {
		StringBuilder builder = new StringBuilder();
		for (Object obj : data)
			builder.append(objectToString(obj).trim()).append(" ");

		error(builder.substring(0, builder.length() - 1));
	}

	public static void error(String data) {
		LoggerThread.err(data);
	}

	public static void println(String data) {
		try {
			OutputStream out = Console.out;
			out.write(data.getBytes(StandardCharsets.UTF_8));
			out.write((byte) '\n');
			out.flush();
		} catch (IOException e) {
			// ignore
		}
	}

	public static void printErr(String data) {
		try {
			OutputStream err = Console.err;
			err.write(data.getBytes(StandardCharsets.UTF_8));
			err.write((byte) '\n');
			err.flush();
		} catch (IOException e) {
			// ignore
		}
	}
}