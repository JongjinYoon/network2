package kr.co.itcen.network.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
	private Socket socket;
	private static String documentRoot = "";
	static {
		documentRoot = RequestHandler.class.getClass().getResource("/webapp").getPath();
	}

	public RequestHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// get IOStream
			OutputStream outputStream = socket.getOutputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
			consoleLog("connected from " + inetSocketAddress.getAddress().getHostAddress() + ":"
					+ inetSocketAddress.getPort());
			String request = null;
			while (true) {
				String line = br.readLine();

				// 브라우저가 연결을 끊으면
				if (line == null) {
					break;
				}

				// header만 읽음
				if ("".equals(line)) {
					break;
				}

				if (request == null) {
					request = line;
					break;
				}
			}

			String[] tokens = request.split(" ");
			if ("GET".equals(tokens[0])) {
				consoleLog("Request:" + request);
				responesStaticResource(outputStream, tokens[1], tokens[2]);
			} else {// POST,PUT,DELETE명령은 무시
				consoleLog("Bad Request:" + request);
				respones400Error(outputStream, tokens[2]);
			}
			// 예제 응답입니다.
			// 서버 시작과 테스트를 마친 후, 주석 처리 합니다.
//			outputStream.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
//			outputStream.write("Content-Type:text/html; charset=utf-8\r\n".getBytes("UTF-8"));
//			outputStream.write("\r\n".getBytes());
//			outputStream.write("<h1>이 페이지가 잘 보이면 실습과제 SimpleHttpServer를 시작할 준비가 된 것입니다.</h1>".getBytes("UTF-8"));

		} catch (Exception ex) {
			consoleLog("error:" + ex);
		} finally {
			// clean-up
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}

			} catch (IOException ex) {
				consoleLog("error:" + ex);
			}
		}
	}

	private void responesStaticResource(OutputStream outputStream, String url, String protocol) throws IOException {
		if ("/".equals(url)) {
			url = "/index.html";
		}
		File file = new File(documentRoot + url);
		if (file.exists() == false) {
			consoleLog("File Not Found:" + url);
			respones404Error(outputStream, protocol);
			return;
		}
		int num = 200;
		request(outputStream, protocol, num, file);
	}

	private void respones400Error(OutputStream outputStream, String protocol) throws IOException {
		int num = 400;
		File file = new File(documentRoot + "error/400.html");
		request(outputStream, protocol, num, file);
	}

	private void respones404Error(OutputStream outputStream, String protocol) throws IOException {
		int num = 404;
		File file = new File(documentRoot + "error/404.html");
		request(outputStream, protocol, num, file);
	}

	private void request(OutputStream outputStream, String protocol, int num, File file) throws IOException {
		String msg = null;
		if (num == 404) {
			msg = " 404 File Not Found\r\n";
		}
		if (num == 200) {
			msg = " 200 OK\r\n";
		} else {
			msg = " 400 Bad Request\r\n";
		}
		// nio
		byte[] body = Files.readAllBytes(file.toPath());
		String contentType = Files.probeContentType(file.toPath());

		// 응답
		outputStream.write((protocol + msg).getBytes("UTF-8"));
		outputStream.write(("Content-Type:" + contentType + "; charset=utf-8\r\n").getBytes("UTF-8"));
		outputStream.write("\r\n".getBytes());
		outputStream.write(body);
	}

	public void consoleLog(String message) {
		System.out.println("[RequestHandler#" + getId() + "] " + message);
	}

}
