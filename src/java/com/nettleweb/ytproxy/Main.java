package com.nettleweb.ytproxy;

import com.nettleweb.client.*;
import com.sun.net.httpserver.*;
import org.schabi.newpipe.extractor.*;
import org.schabi.newpipe.extractor.kiosk.*;
import org.schabi.newpipe.extractor.localization.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public final class Main {
	private static final ExecutorService defaultExecutor = Executors.newFixedThreadPool(6,
			DefaultThreadFactory.instance);

	static {
		try {
			System.loadLibrary("nettleweb");
		} catch (UnsatisfiedLinkError e) {
			// ignore
		}
		Locale.setDefault(Locale.ROOT);
	}

	private Main() {}

	public static void main(String[] args) throws Throwable {
		int parse = 0;
		int port = 80;
		String host = "0.0.0.0";
		String proxy = null;

		for (String arg : args) {
			if (arg.charAt(0) == '-') {
				arg = arg.charAt(1) == '-' ? arg.substring(2) : arg.substring(1);
				switch (arg) {
					case "host" -> parse = 1;
					case "port" -> parse = 2;
					case "proxy" -> parse = 3;
					case "help" -> {
						Console.println("Usage: yt-proxy [OPTION...]\n");
						Console.println("\t--host <name>    Start the HTTP server with the specified host.");
						Console.println("\t--port <port>    Start the HTTP server with the specified port.");
						Console.println("\t--proxy <url>    Send HTTP requests through the proxy server.");
						Console.println("\t--help           Show this help message and exit");
						Console.println("\t--version        Show version information and exit.\n");
						System.exit(0);
					}
					case "version" -> {
						Console.println("v0.1.0");
						System.exit(0);
					}
					default -> {
						Console.printErr("Error: Invalid option: --" + arg);
						Console.printErr("Try '--help' for more information.");
						System.exit(1);
					}
				}
			} else {
				switch (parse) {
					case 3 -> {
						proxy = arg;
						parse = 0;
					}
					case 1 -> {
						host = arg;
						parse = 0;
					}
					case 2 -> {
						try {
							port = Integer.parseInt(arg);
							if (port < 0 || port > 65535) {
								Console.printErr("Error: Port must be between 0 and 65535.");
								System.exit(1);
							}
						} catch (Exception e) {
							Console.printErr("Error: Invalid port value: " + arg);
							System.exit(1);
						}
						parse = 0;
					}
					default -> {
						Console.printErr("Error: Invalid arguments.");
						Console.printErr("Try '--help' for more information.");
						System.exit(1);
					}
				}
			}
		}

		Console.log("Use native: " + VM.useNative);
		Console.log("Initializing...");
		NewPipe.init(new DownloaderImpl(proxy), Localization.fromLocale(Locale.ROOT), new ContentCountry("US"));

		Console.log("Starting server...");
		try {
			// prefetch resources
			KioskInfo.getInfo(ServiceList.YouTube, "https://www.youtube.com/feed/trending");
		} catch (Exception e) {
			// ignore
		}

		HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 255);
		server.createContext("/", HTTPHandlerImpl.instance);
		server.setExecutor(defaultExecutor);
		server.start();
	}
}