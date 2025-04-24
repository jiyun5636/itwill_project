package Chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import User.LoginUserInfo;

public class Server {
	private Map<String, DataOutputStream> clients; //접속자 명단(별칭, 출력객체)
	ChattingService chattingService = new ChattingService();

	public Server() {
		clients = new HashMap<String, DataOutputStream>();
	}
	public void start() {
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(10000);
			while (true) {
				Socket socket = serverSocket.accept(); //접속 대기
				
				// 접속자가 보낸 메시지를 읽을 수 있는 읽기 전용 쓰레드 생성
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start(); //쓰레드로 동작
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		String nickName;
		ChattingDAO chattingDAO=new ChattingDAO();
		LoginUserInfo loginUser = LoginUserInfo.getInstance();
		
		public ServerReceiver(Socket socket) { //연결된 소켓 받아서 작업
			this.socket = socket;
			
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
		    try {
		        // 1. 클라이언트 첫 메시지: 닉네임 받기
		        nickName = in.readUTF();
		        // 2. 접속자 목록에 추가
		        clients.put(nickName, out);

		        // 3. 전체에게 입장 메시지 전송
		        sendToAll("<" + nickName + ">님이 입장했습니다", nickName);

		        // 4. 메시지 반복 수신
		        while (true) {
		            String msg = in.readUTF();

		            if (msg.equals("EXIT")) {
		                // "나가기" 처리 (단순히 소켓 종료)
		                break;
		            }

		            if (msg.equals("DELETE_ROOM")) {
		                chattingDAO.exitChatRoom(loginUser.getKey(),chattingService.selectRoodId);
		                System.out.println(":: " + nickName + "님이 방을 삭제했습니다");
		                break;
		            }

		            // 일반 메시지 전송 (발신자/수신자 포맷 구분)
		            sendToAll(msg, nickName);
		        }

		    } catch (IOException e) {
		        System.out.println("연결 종료 (" + nickName + ")");
		    } finally {
		        // 5. 접속 종료 처리
		        clients.remove(nickName);
		        sendToAll("<" + nickName + ">님이 나갔습니다", nickName);
		        try {
		            if (socket != null) socket.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}


		//접속자 전체에게 메시지 전달
		// Server sendToAll 수정
		private void sendToAll(String msg, String sender) {
		    for (String key : clients.keySet()) {
		        DataOutputStream out = clients.get(key);
		        try {
		            if (key.equals(sender)) {
		                //out.writeUTF(msg);  // 발신자에게는 그대로 메시지 출력
		            } else {
		                out.writeUTF(sender + " : " + msg);  // 수신자에게는 닉네임 : 메시지
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}


	}
}