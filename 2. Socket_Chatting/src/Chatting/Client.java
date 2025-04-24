package Chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import User.LoginUserInfo;

public class Client {
	public void start() {
		Socket socket = null;
		
		try {
			//socket = new Socket("localhost", 10000);
			socket = new Socket("192.168.18.8", 10000);
			
			//메시지 전송 쓰레드 만들기
			ClientSender clientSender = new ClientSender(socket);
			clientSender.start();
			
			//메시지 수신 쓰레드 만들기
			ClientReceiver clientReceiver = new ClientReceiver(socket);
			clientReceiver.start();
			
			clientSender.join();
	        clientReceiver.join();
			
		} catch (IOException | InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	private class ClientSender extends Thread {
		private Socket socket;
		private DataOutputStream out;
		LoginUserInfo loginUser = LoginUserInfo.getInstance();
		String nickname=loginUser.getNickName();

		public ClientSender(Socket socket) {
			this.socket = socket;
			try {
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("[예외발생] ClientSender 생성자 "
						+ "out 객체 생성 실패");
			}
		}
		
		@Override
		public void run() {
			if (out == null) {
				System.out.println("[예외발생] out 객체가 null 입니다");
				return;
			}
			
			//메시지 작성하고, 작성된 메시지 서버로 전송
			Scanner sc = new Scanner(System.in);
			
			try {
				
				out.writeUTF(nickname); //첫번째 메시지 전송 : 이름(별칭)
				
				// ClientSender run() 부분
				while (true) {
				    System.out.print("메시지 작성> ");
				    String msg = sc.nextLine();

				    if (msg.equals("나가기")) {
				        out.writeUTF("EXIT");  // 서버에도 알림
				        break;  // 소켓 종료 준비
				    }
				    if (msg.equals("방나가기")) {
				        out.writeUTF("DELETE_ROOM");  // 서버에 방 삭제 요청
				        break;  // 소켓 종료 준비
				    }
				    out.writeUTF(msg);
				}
				socket.close();  // 소켓 종료
				System.out.println("채팅방을 나갔습니다");

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("전송종료");
		}
		
	}
	
	//메시지 수신 쓰레드
	private class ClientReceiver extends Thread {
		private Socket socket;
		DataInputStream in;
		
		public ClientReceiver(Socket socket) {
			this.socket = socket;
			
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// 메시지를 받아서 화면 출력(반복)
			try {
				// ClientReceiver run() 부분
				while (true) {
				    String msg = in.readUTF();
					System.out.println(msg);
				}

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(":: 더이상 읽을 수 없습니다");
			}
		}
		
	}
}
