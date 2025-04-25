package Chatting;

import java.util.Scanner;

import User.LoginUserInfo;
import User.UserDAO;

public class ChattingService {
	Scanner strSc = new Scanner(System.in);
	LoginUserInfo loginUser = LoginUserInfo.getInstance();
	UserDAO userDAO = new UserDAO();
	ChattingDAO chattingDAO = new ChattingDAO();
	MultiChattingDAO multiChattingDAO = new MultiChattingDAO();
	static public int selectRoomId = 0;

	public void makeOneToOneChatting() {
		System.out.println("방 이름을 설정해주세요.");
		String roomName = strSc.nextLine();

		System.out.println("초대할 유저의 닉네임을 입력해주세요.");
		String inviteUserNickName = strSc.nextLine();

		int inviteUserId = userDAO.findUserIdByName(inviteUserNickName);

		// DB저장
		selectRoomId = chattingDAO.createChatRoomAndList(loginUser.getKey(), inviteUserId, roomName);

		// 클라이언트 스레드 실행
		Thread clientThread = new Thread(() -> {
			Client client = new Client();
			client.start();
		});
		clientThread.start();

		try {
			clientThread.join(); // 클라이언트 끝날 때까지 대기
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showAndJoinChatRooms() {
		int userId = loginUser.getKey();
		chattingDAO.showAllChatRoomsByUserId(userId); // 채팅방 목록 출력

		System.out.println("입장할 방 ID를 입력하세요 (0 입력 시 취소): ");
		int roomId = strSc.nextInt();
		strSc.nextLine();
		selectRoomId = roomId;

		if (roomId == 0) {
			System.out.println("채팅방 입장을 취소했습니다.");
			return;
		}

		// 클라이언트 실행 (채팅 시작)
		Thread clientThread = new Thread(() -> {
			Client client = new Client();
			client.start();
		});
		clientThread.start();

		try {
			clientThread.join(); // 클라이언트가 종료될 때까지 대기
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void makeGroupChatting() {
		System.out.println("방 이름을 설정해주세요.");
		String roomName = strSc.nextLine();

		System.out.println(
						"초대할 유저의 닉네임을 입력해주세요. (최대 5명)\n" + 
						"초대를 그만 하고 싶을 때 '끝'을 입력하세요)\n");
		
		//최대인원 5명
		String inviteUserNickName[] = new String[5];
		int inviteUserKey[] = new int[5];
		
		//0번 인덱스는 무조건 그룹채팅방 만든 유저정보
		inviteUserNickName[0]=loginUser.getNickName();
		inviteUserKey[0]=loginUser.getKey();
		
		for (int i = 1; i < inviteUserNickName.length; i++) {
			System.out.print("초대할 유저 닉네임 : ");
			inviteUserNickName[i] = strSc.nextLine();
			
			//초대한 사용자 key값을 뽑아서 int배열에 저장 (동일 인덱스)
			inviteUserKey[i] = userDAO.findUserIdByName(inviteUserNickName[i]);
			
			if(inviteUserNickName[i].equals("끝")) {
				if(inviteUserNickName[1]==null) {
					System.out.println("최소 1명은 초대해야합니다.");
				}
				break;
			}
		}
		multiChattingDAO.createMultiChatRoomandList(roomName, inviteUserKey);

		//int inviteUserId = userDAO.findUserIdByName(inviteUserNickName);

		// 클라이언트 스레드 실행
		Thread clientThread = new Thread(() -> {
			Client client = new Client();
			client.start();
		});
		clientThread.start();

		try {
			clientThread.join(); // 클라이언트 끝날 때까지 대기
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
