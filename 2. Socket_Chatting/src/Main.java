import java.util.Scanner;

import User.UserService;

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int num;
		UserService userService = new UserService();

		while (true) {
			System.out.println("----- 메인메뉴 -----");
			System.out.println("1. 회원가입");
			System.out.println("2. 로그인");
			System.out.println("3. 채팅방 보기");
			System.out.println("4. 개인 채팅방 생성");
			System.out.println("5. 단체 채팅방 생성");
			System.out.println("6. 회원정보 수정"); // -> 탈퇴
			System.out.println("7. 로그아웃");
			System.out.println("8. 종료");

			System.out.print(">> 원하시는 번호를 입력해주세요 : ");
			num = sc.nextInt();

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
				break;
			// 개인 채팅방 생성
			case 4:
				break;
			// 단체 채팅방 생성
			case 5:
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
				System.out.println("잘못된 번호를 입력하였습니다.");
			}
		}

	}
}