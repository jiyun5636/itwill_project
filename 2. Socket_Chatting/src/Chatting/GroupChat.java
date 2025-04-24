package Chatting;

public class GroupChat {
	private int invitedUserKey; //초대한사람
	private int[] inviteUserKey; //초대받은사람
	private String roomName;
	private static final RoomType roomType = RoomType.GROUP;
	
	
	GroupChat(int invitedUserKey,int inviteUserKey[], String roomName){
		this.invitedUserKey=invitedUserKey;
		this.inviteUserKey=inviteUserKey;
		this.roomName=roomName;
	}
	
	public int getInvitedUserKey() {
		return invitedUserKey;
	}
	public void setInvitedUserKey(int invitedUserKey) {
		this.invitedUserKey = invitedUserKey;
	}
	public int[] getInviteUserKey() {
		return inviteUserKey;
	}
	public void setInviteUserKey(int[] inviteUserKey) {
		this.inviteUserKey = inviteUserKey;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}
