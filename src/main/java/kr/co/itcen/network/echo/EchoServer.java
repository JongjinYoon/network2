package kr.co.itcen.network.echo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	private static final int PORT = 7000;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;

		try {
			// 1. 서버 소켓 생성
			serverSocket = new ServerSocket();

			// 2. Binding : Socket에 SocketAddress(IPAddress + Port) 바인딩
			InetAddress inetAddress = InetAddress.getLocalHost();
			// InetAddress 객체에 의해 표현되는 IP 주소에 해당하는 호스트네임을 포함한 String을 반환한다.

			// 로컬호스트 : 이 프로그램이 돌고있는 호스트
			InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, PORT);
			// 호스트 번호와 포트 번호로 부터 소켓 주소를 작성
			serverSocket.bind(inetSocketAddress);
			log("binding " + inetAddress.getHostAddress() + ":" + PORT);

			// 3. accept : 클라이언트로 부터 연결요청(Connect)을 기다린다
			while (true) {
				Socket socket = serverSocket.accept();// blocking
				new EchoServerReceiveThread(socket).start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 8. ServerSocket 자원정리
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public static void log(String log) {
		System.out.println("[Echo Server#" + Thread.currentThread().getId() + "]" + log);
	}
}
