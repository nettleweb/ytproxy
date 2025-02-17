package com.nettleweb.client;

import com.nettleweb.runtime.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.lang.reflect.*;

@RequiresNative
public final class VM {
	private VM() {}

	public static final boolean useNative;

	static {
		boolean useLib;

		try {
			debug();
			useLib = true;
		} catch (UnsatisfiedLinkError e) {
			useLib = false;
		}

		useNative = useLib;
	}

	public static final int FD_STDIN = 0;
	public static final int FD_STDOUT = 1;
	public static final int FD_STDERR = 2;

	@RequiresNative
	public static final InputStream stdin = new Stdin();

	@RequiresNative
	public static final OutputStream stdout = new Stdout();

	@RequiresNative
	public static final OutputStream stderr = new Stderr();

	public static native void exit(int code);

	public static native void nice(int nice);

	public static native void fsync(int fd);

	public static native void close(int fd);

	public static native void abort();

	public static native void debug();

	public static native int system(@NotNull String cmd);

	@RequiresNative
	public static final class Memory {
		private Memory() {}

		public static native long objectPtr(@Nullable Object obj);

		public static native byte[] getRaw(long ptr, int len);

		public static native void setRaw(long ptr, @NotNull byte[] buf);

		public static native long malloc(long size);

		public static native void free(long ptr);
	}

	@RequiresNative
	public static final class Reflect {
		private Reflect() {}

		public static native Class<?> getClass(@NotNull String name);

		@NotNull
		public static native Object newObject(@NotNull Class<?> cls);

		public static native void setAccessible(@NotNull AccessibleObject obj, boolean flag);
	}

	private static final class Stdin extends InputStream {
		@Override
		public int read() {
			byte[] buf = new byte[1];
			if (this.read(buf, 0, 1) < 0)
				return -1;

			return buf[0] & 0xFF;
		}

		@Override
		public native int read(@NotNull byte[] buf, int off, int len);

		@Override
		public void close() {
			try {
				VM.close(FD_STDIN);
			} catch (ErrnoException e) {
				// ignore
			}
		}
	}

	private static final class Stdout extends OutputStream {
		@Override
		public void write(int b) {
			this.write(new byte[]{(byte) b}, 0, 1);
		}

		@Override
		public native void write(@NotNull byte[] buf, int off, int len);

		@Override
		public void flush() {
			try {
				VM.fsync(FD_STDOUT);
			} catch (ErrnoException e) {
				// ignore
			}
		}

		@Override
		public void close() {
			try {
				VM.close(FD_STDOUT);
			} catch (ErrnoException e) {
				// ignore
			}
		}
	}

	private static final class Stderr extends OutputStream {
		@Override
		public void write(int b) {
			this.write(new byte[]{(byte) b}, 0, 1);
		}

		@Override
		public native void write(@NotNull byte[] buf, int off, int len);

		@Override
		public void flush() {
			try {
				VM.fsync(FD_STDERR);
			} catch (ErrnoException e) {
				// ignore
			}
		}

		@Override
		public void close() {
			try {
				VM.close(FD_STDERR);
			} catch (ErrnoException e) {
				// ignore
			}
		}
	}

	public static final class ErrnoException extends RuntimeException {
		private final int errno;
		private final String command;
		private final String message;

		public ErrnoException(int errno, @Nullable String cmd) {
			this(errno, cmd, (cmd == null || cmd.isEmpty()) ? "Operation finished with error code: " + errno :
					"Operation " + cmd + " finished with error code: " + errno);
		}

		private ErrnoException(int errno, @Nullable String cmd, @Nullable String msg) {
			super(msg);
			this.errno = errno;
			this.command = cmd;
			this.message = msg;
		}

		public int getErrno() {
			return this.errno;
		}

		public String getCommand() {
			return this.command;
		}

		@Override
		public String getMessage() {
			return this.message;
		}

		@Override
		public String toString() {
			return "VMError[" + this.errno + "]: " + this.command;
		}
	}
}
