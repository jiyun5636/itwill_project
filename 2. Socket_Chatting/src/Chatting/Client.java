package Chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

import User.LoginUserInfo;

public class Client {
    public void start() {
        Socket socket = null;

        try {
            socket = new Socket("192.168.18.8", 10000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            // 닉네임과 방 ID 전송
            out.writeUTF(LoginUserInfo.getInstance().getNickName());
            out.writeInt(LoginUserInfo.getInstance().getKey());
            out.writeInt(new ChattingService().selectRoomId);

            // 서버 응답 받기
            String serverResponse = in.readUTF();
            ServerMessageType responseType = ServerMessageType.valueOf(serverResponse);
            if (responseType == ServerMessageType.NO_ROOM) {
                System.out.println("참가한 방이 없습니다. 메인메뉴로 돌아갑니다.");
                socket.close();
                return;
            }

            // 송수신 스레드 시작
            ClientSender clientSender = new ClientSender(socket, out);
            clientSender.start();

            ClientReceiver clientReceiver = new ClientReceiver(socket, in);
            clientReceiver.start();

            clientSender.join();
            clientReceiver.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ClientSender extends Thread {
        private Socket socket;
        private DataOutputStream out;

        public ClientSender(Socket socket, DataOutputStream out) {
            this.socket = socket;
            this.out = out;
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(System.in);
            LocalDateTime date;

            try {
                while (!socket.isClosed()) {
                    String msg = sc.nextLine();

                    if (msg.equals("나가기")) {
                        out.writeUTF(ServerMessageType.EXIT.name());
                        break;
                    }
                    if (msg.equals("방나가기")) {
                        out.writeUTF(ServerMessageType.DELETE_ROOM.name());
                        break;
                    }
                    out.writeUTF(ServerMessageType.NORMAL.name() + ":" + msg);
                }

                socket.close();
                System.out.println("채팅방을 나갔습니다");

            } catch (IOException e) {
                System.out.println("서버와 연결이 종료되었습니다.");
            }
        }
    }

    private class ClientReceiver extends Thread {
        private Socket socket;
        private DataInputStream in;

        public ClientReceiver(Socket socket, DataInputStream in) {
            this.socket = socket;
            this.in = in;
        }

        @Override
        public void run() {
            try {
                while (!socket.isClosed()) {
                    String msg = in.readUTF();
                    ServerMessageType type;
                    String content = "";

                    if (msg.contains(":")) {
                        String[] parts = msg.split(":", 2);
                        type = ServerMessageType.valueOf(parts[0]);
                        content = parts[1];
                    } else {
                        type = ServerMessageType.valueOf(msg);
                    }

                    switch (type) {
                        case EXIT:
                        case DELETE_ROOM:
                            System.out.println("채팅방이 종료되었습니다. 메인메뉴로 돌아갑니다.");
                            socket.close();
                            return;
                        case NORMAL:
                            System.out.println(content);
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println("채팅창 종료");
            }
        }
    }
}
