package User;

public class User {
	private String name;
	private String nickName;
	private String password;
	
	User(String name, String nickName, String password){
		this.name=name;
		this.nickName=nickName;
		this.password=password;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
