package com.nettleweb.ytproxy;

import com.nettleweb.client.Console;
import org.schabi.newpipe.extractor.downloader.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

final class DownloaderImpl extends Downloader {
	private final Proxy proxy;

	DownloaderImpl(String proxy) {
		Proxy p = Proxy.NO_PROXY;

		if (proxy != null && !proxy.isEmpty()) {
			try {
				URI uri = new URI(proxy);
				switch (uri.getScheme()) {
					case "socks4", "socks5" ->
							p = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(uri.getHost(), uri.getPort()));
					case "http", "https" ->
							p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(uri.getHost(), uri.getPort()));
					default -> Console.warn("Unsupported proxy protocol ignored: " + uri);
				}
			} catch (Exception e) {
				Console.warn("Failed to parse the specified proxy URL, ignoring...");
			}
		}

		this.proxy = p;
	}

	@Override
	public Response execute(Request request) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(request.url()).openConnection(this.proxy);
		connection.setDoOutput(true);
		connection.setUseCaches(true);
		connection.setRequestMethod(request.httpMethod());
		connection.setConnectTimeout(10000);

		for (Map.Entry<String, List<String>> e : request.headers().entrySet()) {
			String key = e.getKey();
			for (String v : e.getValue())
				connection.addRequestProperty(key, v);
		}

		byte[] data = request.dataToSend();
		if (data != null) {
			OutputStream out = connection.getOutputStream();
			out.write(data, 0, data.length);
			out.flush();
			out.close();
		} else connection.connect();

		int status = connection.getResponseCode();
		data = readBytes(status >= 400 ? connection.getErrorStream() : connection.getInputStream());

		return new Response(status, connection.getResponseMessage(), connection.getHeaderFields(), data.length > 0 ?
				new String(data, StandardCharsets.UTF_8) : null, connection.getURL().toString());
	}

	private static byte[] readBytes(InputStream stream) throws IOException {
		byte[] buffer = new byte[8192];
		byte[] outBuf = new byte[0];

		for (int i = stream.read(buffer, 0, 8192); i >= 0; i = stream.read(buffer, 0, 8192))
			outBuf = mergeBytes(outBuf, buffer, i);

		stream.close();
		return outBuf;
	}

	private static byte[] mergeBytes(byte[] one, byte[] two, int len) {
		int baseLength = one.length;
		byte[] merged = new byte[baseLength + len];

		System.arraycopy(one, 0, merged, 0, baseLength);
		System.arraycopy(two, 0, merged, baseLength, len);
		return merged;
	}
}
