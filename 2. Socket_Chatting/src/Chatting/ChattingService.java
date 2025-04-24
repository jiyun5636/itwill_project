package Chatting;

import java.util.Scanner;

import User.LoginUserInfo;
import User.UserDAO;

public class ChattingService {
	Scanner strSc = new Scanner(System.in);
	LoginUserInfo loginUser = LoginUserInfo.getInstance();
	UserDAO userDAO = new UserDAO();
	ChattingDAO chattingDAO = new ChattingDAO();

	public void makeOneToOneChatting() {
		System.out.println("방 이름을 설정해주세요.");
		String roomName = strSc.nextLine();

		System.out.println("초대할 유저의 닉네임을 입력해주세요.");
		String inviteUserNickName = strSc.nextLine();

		int inviteUserId = userDAO.findUserIdByName(inviteUserNickName);

		//방 생성
		OnetoOneChat onetoOneChat = new OnetoOneChat(loginUser.getKey(), inviteUserId, roomName);
	
		//DB저장
		chattingDAO.createChatRoomandList(loginUser.getKey(), inviteUserId, roomName);
		
		// 클라이언트 스레드 실행
	    Thread clientThread = new Thread(() -> {
	        Client client = new Client();
	        client.start();
	    });
	    clientThread.start();

	    try {
	        clientThread.join();  // 클라이언트 끝날 때까지 대기
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	
	public void showAndEnterChatRooms() {
	    int userId = loginUser.getKey();
	    chattingDAO.showChatRoomByUserId(userId);  // 채팅방 목록 출력

	    System.out.println("입장할 방 ID를 입력하세요 (0 입력 시 취소): ");
	    int roomId = strSc.nextInt();
	    strSc.nextLine();

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
	        clientThread.join();  // 클라이언트가 종료될 때까지 대기
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}


	public void findRoomByUserKey() {
		System.out.println("=== 현재 참여중인 방 ===");
	}
}
