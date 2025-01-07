package com.nettleweb.client;

import java.io.*;
import java.lang.annotation.*;
import java.nio.charset.*;
import java.text.*;
import java.util.*;

public final class Console {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS zzz",
			Locale.ROOT);

	private Console() {}

	@Native
	public static final InputStream in;

	@Native
	public static final OutputStream out;

	@Native
	public static final OutputStream err;

	static {
		if (VM.useNative) {
			in = new ConsoleIn(VM.stdin);
			out = new ConsoleOut(VM.stdout);
			err = new ConsoleOut(VM.stderr);
		} else {
			in = new ConsoleIn(System.in);
			out = new ConsoleOut(System.out);
			err = new ConsoleOut(System.err);
		}

		try {
			System.setIn(Console.in);
			System.setOut((PrintStream) out);
			System.setErr((PrintStream) err);
		} catch (Exception e) {
			// ignore
		}
	}

	private static char[] getChars(CharSequence seq) {
		final int length = seq.length();

		char[] data = new char[length];
		for (int i = 0; i < length; i++)
			data[i] = seq.charAt(i);

		return data;
	}

	private static String objectToString(Object obj) {
		if (obj == null)
			return "null";
		if (obj instanceof String)
			return (String) obj;
		if (obj instanceof CharSequence)
			return new String(getChars((CharSequence) obj));

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
		((PrintStream) out).println("[CONSOLE LOG] " + dateFormat.format(new Date(System.currentTimeMillis())) + " " + data);
	}

	public static void warn(Object... data) {
		StringBuilder builder = new StringBuilder();
		for (Object obj : data)
			builder.append(objectToString(obj).trim()).append(" ");

		warn(builder.substring(0, builder.length() - 1));
	}

	public static void warn(String data) {
		((PrintStream) out).println("[CONSOLE WARN] " + dateFormat.format(new Date(System.currentTimeMillis())) + " " + data);
	}

	public static void error(Object... data) {
		StringBuilder builder = new StringBuilder();
		for (Object obj : data)
			builder.append(objectToString(obj).trim()).append(" ");

		error(builder.substring(0, builder.length() - 1));
	}

	public static void error(String data) {
		((PrintStream) err).println("[CONSOLE ERROR] " + dateFormat.format(new Date(System.currentTimeMillis())) + " " + data);
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

	private static final class ConsoleIn extends InputStream {
		private final InputStream base;

		private ConsoleIn(InputStream base) {
			this.base = base;
		}

		@Override
		public int read() {
			byte[] buf = new byte[1];
			if (this.read(buf) < 0)
				return -1;

			return buf[0] & 0xFF;
		}

		@Override
		public int read(byte[] b) {
			return this.read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) {
			try {
				return this.base.read(b, off, len);
			} catch (Exception e) {
				// ignore
				return 0;
			}
		}

		@Override
		public void close() {
		}
	}

	private static final class ConsoleOut extends PrintStream {
		private static final byte[] falseBytes = new byte[]{102, 97, 108, 115, 101};
		private static final byte[] trueBytes = new byte[]{116, 114, 117, 101};
		private static final byte[] nullBytes = new byte[]{110, 117, 108, 108};
		private static final byte[] newLine = new byte[]{(byte) '\n'};

		private final OutputStream base;

		private ConsoleOut(OutputStream base) {
			super(base, false, StandardCharsets.UTF_8);
			this.base = base;
		}

		@Override
		public void write(int b) {
			this.write(new byte[]{(byte) b}, 0, 1);
		}

		@Override
		public void write(byte[] b) {
			this.write(b, 0, b.length);
		}

		@Override
		public void write(byte[] b, int off, int len) {
			try {
				OutputStream stream = this.base;
				stream.write(b, off, len);
				stream.flush();
			} catch (Exception e) {
				// ignore
			}
		}

		@Override
		public void writeBytes(byte[] buf) {
			this.write(buf, 0, buf.length);
		}

		@Override
		public void print(String s) {
			this.write(s == null ? nullBytes : s.getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public void print(Object o) {
			this.print(o == null ? null : o.toString());
		}

		@Override
		public void print(char[] s) {
			this.write(new String(s).getBytes(StandardCharsets.UTF_8));
		}

		@Override
		public void println(String x) {
			this.write(x == null ? nullBytes : x.getBytes(StandardCharsets.UTF_8));
			this.write(newLine);
		}

		@Override
		public void println(Object x) {
			this.print(x == null ? null : x.toString());
		}

		@Override
		public void println(char[] x) {
			this.write(new String(x).getBytes(StandardCharsets.UTF_8));
			this.write(newLine);
		}

		@Override
		public void print(int i) {
			this.print(Integer.toString(i));
		}

		@Override
		public void print(char c) {
			this.print(Character.toString(c));
		}

		@Override
		public void print(long l) {
			this.print(Long.toString(l));
		}

		@Override
		public void print(float f) {
			this.print(Float.toString(f));
		}

		@Override
		public void print(double d) {
			this.print(Double.toString(d));
		}

		@Override
		public void print(boolean b) {
			this.write(b ? trueBytes : falseBytes);
		}

		@Override
		public void println() {
			this.write(newLine);
		}

		@Override
		public void println(int x) {
			this.print(Integer.toString(x));
			this.write(newLine);
		}

		@Override
		public void println(char x) {
			this.print(Character.toString(x));
			this.write(newLine);
		}

		@Override
		public void println(long x) {
			this.print(Long.toString(x));
			this.write(newLine);
		}

		@Override
		public void println(float x) {
			this.print(Float.toString(x));
			this.write(newLine);
		}

		@Override
		public void println(double x) {
			this.print(Double.toString(x));
			this.write(newLine);
		}

		@Override
		public void println(boolean x) {
			this.write(x ? trueBytes : falseBytes);
			this.write(newLine);
		}

		@Override
		public PrintStream append(char c) {
			this.print(Character.toString(c));
			return this;
		}

		@Override
		public PrintStream append(CharSequence s) {
			this.print(getChars(s));
			return this;
		}

		@Override
		public boolean checkError() {
			return false;
		}

		@Override
		public void clearError() {
		}

		@Override
		public void setError() {
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}
	}
}