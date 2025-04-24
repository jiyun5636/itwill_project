package Chatting;

public class ServerMain{
	public static void main(String[] args) {
		startServer();
	}
	public static void startServer() {
        Server server = new Server();
        server.start();
    }
}
