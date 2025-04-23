package Chatting;

import java.time.LocalDateTime;

public class ChattingMessage {
	private String content;
	private LocalDateTime date;
	
	
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
