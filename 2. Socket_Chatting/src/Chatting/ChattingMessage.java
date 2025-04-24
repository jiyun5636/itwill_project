package Chatting;

import java.time.LocalDateTime;

public class ChattingMessage {
	private int userKey;
	private int chattingRoomKey;
	private String content;
	private LocalDateTime date;

	public int getChattingRoomKey() {
		return chattingRoomKey;
	}
	public void setChattingRoomKey(int chattingRoomKey) {
		this.chattingRoomKey = chattingRoomKey;
	}
	public int getUserKey() {
		return userKey;
	}
	public void setUserKey(int userKey) {
		this.userKey = userKey;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
}
