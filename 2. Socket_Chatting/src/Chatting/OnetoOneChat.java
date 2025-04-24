package Chatting;

public class OnetoOneChat {
	private int invitedUserKey; // 초대한사람
	private int inviteUserKey; // 초대받은사람
	private String roomName;
	private static final RoomType roomType = RoomType.ONE_TO_ONE;

	OnetoOneChat() {
	}

	OnetoOneChat(int invitedUserKey, int inviteUserKey, String roomName) {
		this.invitedUserKey = invitedUserKey;
		this.inviteUserKey = inviteUserKey;
		this.roomName = roomName;
	}

	public int getInvitedUser() {
		return invitedUserKey;
	}

	public void setInvitedUser(int invitedUserKey) {
		this.invitedUserKey = invitedUserKey;
	}

	public int getInviteUserKey() {
		return inviteUserKey;
	}

	public void setInviteUserKey(int inviteUserKey) {
		this.inviteUserKey = inviteUserKey;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
