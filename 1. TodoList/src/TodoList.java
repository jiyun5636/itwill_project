import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoList {

	LocalDateTime localDateTime;
	List<Todo> list = new ArrayList<Todo>();
	Scanner sc = new Scanner(System.in);

	public void selectMenu(int num) {
		switch (num) {
		case 1:
			inputTodo();
			break;

		case 2:
		default:

		}
	}

	public void inputTodo() {
		System.out.print("제목을 입력하세요 : ");
		String title = sc.nextLine();
		System.out.print("내용을 입력하세요 : ");
		String comment = sc.nextLine();
		System.out.print("중요도를 입력해주세요 - * , ** , *** : ");
		String star = sc.nextLine();

		int key = list.size();
		System.out.println(key);

		Todo todo = new Todo(key, localDateTime.now(), title, comment, star);
		todo.setCheck(false); // Todo 유무 기본값은 false

		list.add(todo);

		System.out.println("todo가 생성되었습니다. ");
		System.out.println(list.get(key).toString());
	}

	public void updateTodo() {

	}

	public void deleteTodo() {

	}
}
