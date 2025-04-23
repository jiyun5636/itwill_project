package User;

public class User {
	private int key;
	private String name;
	private String nickName;
	private String password;
	
	User(){
		
	}
	
	User(String name, String nickName, String password){
		this.name=name;
		this.nickName=nickName;
		this.password=password;
	}
	
	User(int key, String name, String nickName, String password){
		this.key=key;
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
	
	@Override
	public String toString() {
		return "User [key=" + key + ", name=" + name + ", nickName=" + nickName + ", password=" + password + "]";
	}
	
}
