package User;

public class LoginUserInfo {
	private static LoginUserInfo instance = new LoginUserInfo();

	private int key;
	private String name;
	private String nickName;
	private String password;

	private LoginUserInfo() {
	} // 생성자 private

	public static LoginUserInfo getInstance() {
		return instance;
	}

	public void setUserInfo(int key, String name, String nickName, String password) {
		this.key=key;
		this.name = name;
		this.nickName = nickName;
		this.password = password;
	}
	public void updateUserInfo(String name, String nickName, String password) {
		this.name = name;
		this.nickName = nickName;
		this.password = password;
	}

	public void clearUserInfo() {
		this.key = 0;
		this.name = null;
		this.nickName = null;
		this.password = null;
	}

	public int getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public String getNickName() {
		return nickName;
	}

	public String getPassword() {
		return password;
	}
}
