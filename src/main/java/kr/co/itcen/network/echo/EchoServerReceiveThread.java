package kr.co.itcen.network.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoServerReceiveThread extends Thread {
	private Socket socket;

	public EchoServerReceiveThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();

		EchoServer.log(" connected from client[" + inetRemoteSocketAddress.getAddress().getHostAddress() + ":"
				+ inetRemoteSocketAddress.getPort() + "]");//여기에 사용자명

		try {
			// 4. IOStream 생성하
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			// true : 버퍼가 찰때까지 기다리지 않고 들어올때마다 보냄

			while (true) {

				// 5. 데이터 읽기(수신)
				String data = br.readLine();
				if (data == null) {
					// 정상종료 : remote socket이 close() 메소드를 통해서 정상적으로 소켓을 닫은 경우
					EchoServer.log(" closed by client");
					break;
				}

				EchoServer.log(" received : " + data);

				// 6. 데이터 쓰기(송신)
				pw.println(data);
			}
		} catch (SocketException e) {
			EchoServer.log(" abnormal closed by client");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 7. Socket 자원정리
			if (socket != null && socket.isClosed() == false) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
