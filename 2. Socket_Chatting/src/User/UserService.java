package User;

import java.util.Scanner;

public class UserService {
	Scanner intSc = new Scanner(System.in);
	Scanner strSc = new Scanner(System.in);
	UserDAO userDAO = new UserDAO();
	User user = new User();
	LoginUserInfo loginUser = LoginUserInfo.getInstance();

	public void selectMenu(int num) {
		switch (num) {
		// 회원가입
		case 1:
			signUp();
			break;

		// 로그인
		case 2:
			signIn();
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
			UpdateUser();
			break;

		// 로그아웃
		case 7:
			logOut();
			break;

		default:

		}
	}

	public void signUp() {
		System.out.print("이름을 입력하세요: ");
		String name = strSc.nextLine();

		String nickName = null;
		String password = null;
		String passwordCheck = null;

		while (true) {
			System.out.print("닉네임을 입력하세요: ");
			nickName = strSc.nextLine();
			// 중복처리 추가 필요
			if (userDAO.isNickNameDuplicate(nickName)) {
				System.out.println("이미 존재하는 닉네임입니다. 다시 입력하세요.");
			} else {
				break;
			}
		}

		while (true) {
			System.out.print("비밀번호를 입력하세요: ");
			password = strSc.nextLine();

			System.out.print("비밀번호를 한번 더 입력하세요: ");
			passwordCheck = strSc.nextLine();

			if (!password.equals(passwordCheck)) {
				System.out.println("비밀번호를 잘못 입력하셨습니다.");
			} else {
				break;
			}
		}

		user = new User(name, nickName, password);

		userDAO.insert(user);

		System.out.println("회원가입이 완료되었습니다.");
	}

	public void signIn() {
		if (loginUser.getNickName() != null) {
			System.out.println("이미 로그인되어있습니다.");
		} else {
			while (true) {
				System.out.println("닉네임을 입력하세요");
				String nickName = strSc.nextLine();

				System.out.println("비밀번호를 입력하세요");
				String password = strSc.nextLine();

				Boolean isLogin = userDAO.login(nickName, password);

				if (!isLogin) {
					System.out.println("잘못된 정보를 입력하였습니다.");
				} else {
					System.out.println("현재 로그인한 사용자: " + loginUser.getNickName());
					break; // 로그인 성공 시 반복 종료
				}
			}
		}
	}

	public void UpdateUser() {
		if (loginUser.getNickName() != null) {
			// 로그인한 사용자 정보 가져옴
			System.out.println("==== 현재 사용자 정보 ====");
			System.out.println("이름 : " + loginUser.getName());
			System.out.println("닉네임 : " + loginUser.getNickName());
			System.out.println("비밀번호 : " + loginUser.getPassword());
			System.out.println("=================");
			System.out.println("1. 수정하기");
			System.out.println("2. 탈퇴");
			int num = intSc.nextInt();

			switch (num) {
			case 1:
				System.out.println("변경할 이름 : ");
				String nameUpdate = strSc.nextLine();

				System.out.println("변경할 닉네임 : ");
				String nickNameUpdate = strSc.nextLine();

				System.out.println("변경할 비밀번호 : ");
				String passwordUpdate = strSc.nextLine();

				// 가져온 정보 update
				loginUser.updateUserInfo(nameUpdate, nickNameUpdate, passwordUpdate);
				userDAO.UpdateUser(loginUser, loginUser.getKey());
				break;
			case 2:
				System.out.println("탈퇴하시겠습니다?");
				System.out.println("1. 예	2. 아니오");
				int check = intSc.nextInt();

				if (check == 1) {
					// 추가 필요
				}
				break;
			}

			System.out.println("사용자 정보를 수정하였습니다.");
			System.out.println("==== 수정된 사용자 정보 ====");
			System.out.println("이름 : " + loginUser.getName());
			System.out.println("닉네임 : " + loginUser.getNickName());
			System.out.println("비밀번호 : " + loginUser.getPassword());
			System.out.println("=================");
		} else {
			System.out.println("로그인이 필요합니다.");
		}
	}

	public void logOut() {

	}

	public void deleteUser() {

	}
}
