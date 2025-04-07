import java.time.LocalDateTime;

public class Todo {
	int key;
	LocalDateTime localDateTime;
	String title;
	String comment;

	boolean check;
	// * , **, ***
	String star;

	Todo(int key, LocalDateTime localDateTime, String title, String comment, String star) {
		this.key = key;
		this.localDateTime = localDateTime;
		this.title = title;
		this.comment = comment;
		this.star = star;
	}

	@Override
	public String toString() {
		return "번호 : " + getKey() + "\n날짜" + getLocalDateTime() + "\n제목 : " + getTitle() + "\n내용 : " + getComment()
				+ "\n실행 유무 : " + isCheck() + "\n중요도 : " + getStar();
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}
}
