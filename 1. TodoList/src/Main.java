import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		/*
		 * 메인메뉴 1. todo 만들기 2. todo 수정하기 3. todo 삭제하기 4. todo 검색하기 5. todo 완료/미완료 체크하기
		 */
		Scanner sc = new Scanner(System.in);
		int num;
		TodoList todoList = new TodoList();

		while (true) {
			System.out.println("----- 메인메뉴 -----");
			System.out.println("1. todo 만들기");
			System.out.println("2. todo 수정하기");
			System.out.println("3. todo 삭제하기");
			System.out.println("4. todo 검색하기");
			System.out.println("5. todo 완료/미완료 체크하기");
			System.out.println("6. todo 전체보기");
			System.out.println("7. 종료하기");

			System.out.print(">> 원하시는 번호를 입력해주세요 : ");
			num = sc.nextInt();

			if (num == 7) {
				System.out.println("종료되었습니다.");
				break;
			}else {
				todoList.selectMenu(num);
			}
		}
		
	}
}
