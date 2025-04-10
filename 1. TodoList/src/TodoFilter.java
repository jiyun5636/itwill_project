import java.util.List;
import java.util.Scanner;

public class TodoFilter {

	public void search(List<Todo> list) {

		Scanner sc = new Scanner(System.in);
		System.out.print("제목을 입력해주세요 : ");
		String keyword = sc.nextLine();

		boolean found = false;

		try {

			for (Todo todo : list) {
				if (todo.getTitle().contains(keyword)) {
					System.out.println(todo.toString());

					found = true;
				}
			}

			if (!found) {
				System.out.println("검색 결과가 없습니다.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("오류발생 : " + e);
		}

	}

}
