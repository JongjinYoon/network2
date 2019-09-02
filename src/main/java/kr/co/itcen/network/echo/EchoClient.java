package kr.co.itcen.network.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	private static String SERVER_IP = "192.168.1.70";
	private static int SERVER_PORT = 7000;

	public static void main(String[] args) {
		Socket socket = null;
		Scanner s = new Scanner(System.in);
		try {
			// 1. 소켓 생성
			socket = new Socket();

			// 2. 서버연결
			
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			log(" connected");

			// 3. IOStream생성
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			

			
			while (true) {

				// 5. 키보드 입력 받기
				System.out.print(">>");
				
				String line = s.nextLine();
				
				if ("quit".equals(line)) {
					break;
				}
				
				// 6. 데이터 쓰기(전송)
				pw.println(line);
				
				//7. 데이터 읽기(수신)
				String data = br.readLine();
				if (data == null) {
					// 정상종료 : remote socket이 close() 메소드를 통해서 정상적으로 소켓을 닫은 경우
					log(" closed by client");
					return;
				}
				//8. 콘솔출력
				System.out.println("<<" + data);

				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(s!=null) {
					s.close();
				}
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static void log(String log) {
		System.out.println("[Echo Client]" + log);
	}
}
