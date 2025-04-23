package User;

import java.util.Scanner;

public class UserService {
	Scanner intSc = new Scanner(System.in);
	Scanner strSc = new Scanner(System.in);
	UserDAO userDAO = new UserDAO();
	
	public void selectMenu(int num) {
		switch (num) {
		//회원가입
		case 1:
			signUp();
			break;

		//로그인
		case 2:
			break;

		//채팅방 보기
		case 3:
			break;

		//개인 채팅방 생성
		case 4:
			break;
			
		//단체 채팅방 생성
		case 5:
			break;
			
		//회원정보 수정
		case 6:
			break;

		default:

		}
	}

	public void signUp() {
		System.out.print("이름을 입력하세요: ");
		String name = strSc.nextLine();
		
		System.out.print("닉네임을 입력하세요: ");
		String nickName = strSc.nextLine();
		//중복처리 추가 필요
		
		System.out.print("비밀번호를 입력하세요: ");
		String password = strSc.nextLine();
		
		System.out.print("비밀번호를 한번 더 입력하세요: ");
		String passwordCheck = strSc.nextLine();
		
		if(!password.equals(passwordCheck)) {
			System.out.println("비밀번호를 잘못 입력하셨습니다.");
		}
		
		User user=new User(name, nickName, password);
		
		userDAO.insert(user);
		
		System.out.println("회원가입이 완료되었습니다.");
	}
	
//	public void signIn() {
//		while (true) {
//			System.out.println("닉네임을 입력하세요");
//			String nickName = strSc.nextLine();
//			
//			System.out.println("비밀번호를 입력하세요");
//			String passwordString = strSc.nextLine();
//		}
//	}
}
