import java.util.Scanner;

import Chatting.ChattingService;
import Chatting.ServerMain;
import User.UserService;

public class Main {
	private static Thread serverThread = null;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int num;
		UserService userService = new UserService();
		ChattingService chattingService = new ChattingService();
		while (true) {
			try {

				System.out.println("----- 메인메뉴 -----");
				System.out.println("1. 회원가입");
				System.out.println("2. 로그인");
				System.out.println("3. 채팅방 보기");
				System.out.println("4. 개인 채팅방 생성");
				System.out.println("5. 단체 채팅방 생성");
				System.out.println("6. 회원정보 수정"); // -> 탈퇴
				System.out.println("7. 로그아웃");
				System.out.println("8. 종료");

				System.out.print(">> 원하시는 번호를 입력해주세요 : \n");
				num = sc.nextInt();
				sc.nextLine();

				switch (num) {
				// 회원가입
				case 1:
					userService.signUp();
					break;
				// 로그인
				case 2:
					userService.signIn();
					break;
				// 채팅방 보기
				case 3:
					openServer();
					chattingService.showAndJoinChatRooms(); // 채팅방 목록 보기 → 입장
					break;
				// 개인 채팅방 생성
				case 4:
					openServer();
					chattingService.makeOneToOneChatting(); // 유저 초대 → 클라 실행
					break;
				// 단체 채팅방 생성
				case 5:
					openServer();
					chattingService.makeGroupChatting();
					break;
				// 회원정보 수정
				case 6:
					userService.UpdateUser();
					break;
				// 로그아웃
				case 7:
					userService.logOut();
					break;
				case 8:
					System.out.println("종료되었습니다.");
					System.exit(0);
				default:
					break;
				}
			} catch (Exception e) {
				System.out.println("잘못된 번호를 입력하였습니다.");
				sc.nextLine();
			}
		}
	}
	//서버가 열려있지 않으면 처음 실행
	//실행 후 다른 기능을 사용할 때 서버가 열려있으면 기존 서버를 그대로 사용
	//회원 관리엔 서버 필요 X, openServer에서 검사하기 때문에 3,4,5에서 호출
	public static void openServer() {
		if (serverThread == null || !serverThread.isAlive()) {
			serverThread = new Thread(() -> {
				ServerMain.startServer(); // 서버 실행
			});
			serverThread.start();
			// 서버는 join하지 않고 그냥 실행 (클라이언트가 join)
		}
	}
}