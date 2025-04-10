import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TodoList {

	private static final String FILE_NAME = "todo.txt";
	LocalDateTime localDateTime;
	List<Todo> list = new ArrayList<Todo>();
	Scanner intSc = new Scanner(System.in);
	Scanner strSc = new Scanner(System.in);

	TodoFilter filter = new TodoFilter();

	public TodoList() {
		loadFromFile();
	}

	public void selectMenu(int num) {
		switch (num) {
		case 1:
			inputTodo();
			break;

		case 2:
			updateTodo();
			break;

		case 3:
			deleteTodo();
			break;

		case 4:
			filter.search(list);
			break;
		case 5:
			checkTodo();
			break;
		case 6:
			showAllList();
			break;

		default:

		}
	}

	public void inputTodo() {
		System.out.print("제목을 입력하세요 : ");
		String title = intSc.nextLine();
		System.out.print("내용을 입력하세요 : ");
		String comment = intSc.nextLine();
		System.out.print("중요도를 입력해주세요 - * , ** , *** : ");
		String star = intSc.nextLine();

		Todo todo = new Todo(localDateTime.now(), title, comment, star);

		todo.setCheck(false); // Todo 유무 기본값은 false

		list.add(todo);

		// key값은 리스트 인덱스 값
		int key = list.indexOf(todo);
		todo.setKey(key);

		System.out.println("todo가 생성되었습니다. ");
		getOneList(key);
	}

	public void updateTodo() {
		showAllList();
		System.out.print("수정할 todo 번호를 입력하세요 : ");
		int num = intSc.nextInt();

		for (int i = 0; i < list.size(); i++) {
			if (num == i) {
				System.out.println("기존 제목 : " + list.get(i).getTitle());
				System.out.println("수정할 제목을 입력하세요 : ");
				String title = strSc.nextLine();
				list.get(i).setTitle(title);

				System.out.println("기존 내용 : " + list.get(i).getComment());
				System.out.println("수정할 내용을 입력하세요 : ");
				String comment = strSc.nextLine();
				list.get(i).setComment(comment);

				System.out.println("기존 중요도 : " + list.get(i).getStar());
				System.out.println("수정할 중요도를 입력해주세요 - * , ** , *** : ");
				String star = strSc.nextLine();
				list.get(i).setStar(star);

				System.out.println("--- 수정된 todo ---");
				getOneList(num);
				break;
			}
		}
		System.out.println();
	}

	public void deleteTodo() {
		showAllList();
		System.out.print("삭제할 todo 번호를 입력하세요 : ");
		int num = intSc.nextInt();
		for (int i = 0; i < list.size(); i++) {
			if (num == i) {
				list.remove(i);
				list.get(i).setKey(i);
				System.out.println(num + "번째 todo가 삭제되었습니다");
			}
		}
	}

	public void checkTodo() {
		showAllList();
		System.out.print("완료/미완료를 체크할 todo 번호를 입력하세요 : ");
		int num = intSc.nextInt();
		for (int i = 0; i < list.size(); i++) {
			if (num == i) {
				getOneList(i);
				System.out.println("==============");
				System.out.println("1. 완료  2. 미완료");
				int check = intSc.nextInt();
				if (check == 1) {
					list.get(i).setCheck(true);
				} else {
					list.get(i).setCheck(false);
				}
				getOneList(num);
			}
		}
	}

	public void showAllList() {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("==============");
			System.out.println(list.get(i).toString());
			System.out.println("==============");
		}
	}

	public void getOneList(int num) {
		for (int i = 0; i < list.size(); i++) {
			if (num == i) {
				System.out.println(list.get(i).toString());
			}
		}
	}

	public void loadFromFile() {
		File file = new File(FILE_NAME);
		if (!file.exists()) {
			System.out.println("[안내] 저장된 todo 파일이 없습니다.");
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split("##");
				if (parts.length < 6)
					continue;

				LocalDateTime dateTime = LocalDateTime.parse(parts[0]);
				String title = parts[1];
				String comment = parts[2];
				String star = parts[3];
				boolean check = Boolean.parseBoolean(parts[4]);
				int key = Integer.parseInt(parts[5]);

				Todo todo = new Todo(dateTime, title, comment, star);
				todo.setCheck(check);
				todo.setKey(key);

				list.add(todo);
			}
			System.out.println("[불러오기 완료] " + list.size() + "개의 항목을 불러왔습니다.");
		} catch (IOException e) {
			System.out.println("오류 발생: " + e.getMessage());
		}
	}

	public void saveToFile() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
			for (Todo todo : list) {
				String line = todo.getLocalDateTime().toString() + "##" + todo.getTitle() + "##" + todo.getComment()
						+ "##" + todo.getStar() + "##" + todo.isCheck() + "##" + todo.getKey();
				bw.write(line);
				bw.newLine();
			}
			System.out.println("[저장 완료] todo.txt에 저장되었습니다.");
		} catch (IOException e) {
			System.out.println("오류 발생: " + e.getMessage());
		}
	}
}
