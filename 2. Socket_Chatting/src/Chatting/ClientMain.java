package Chatting;

public class ClientMain {
	private static boolean isClient = false;
	public static void main(String[] args) {
		startClient();
	}
	public static void startClient() {
		isClient=true;
		Client client= new Client();
		client.start();
		isClient=false;
	}
	public static boolean isClientOn() {
		return isClient;
	}
}
