package Chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private Map<Integer, Map<String, DataOutputStream>> roomClients; // 방 번호별 (닉네임, 출력스트림)

    public Server() {
        roomClients = new HashMap<>();
    }

    public void start() {
        System.out.println("서버 시작");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(10000);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 접속 됨");

                // 쓰레드 생성 및 실행
                ServerReceiver thread = new ServerReceiver(socket);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ServerReceiver extends Thread {
        Socket socket;
        DataInputStream in;
        DataOutputStream out;
        String nickName;
        int roomId;

        public ServerReceiver(Socket socket) {
            this.socket = socket;
            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // 닉네임과 방 번호 수신
                nickName = in.readUTF();
                roomId = in.readInt();

                // 방 목록에 클라이언트 등록
                roomClients.putIfAbsent(roomId, new HashMap<>());
                roomClients.get(roomId).put(nickName, out);

                sendToRoom("<" + nickName + ">님이 입장했습니다");

                // 메시지 수신 및 송신
                while (true) {
                    String msg = in.readUTF();

                    if (msg.equalsIgnoreCase("EXIT")) {
                        break;
                    }

                    if (msg.equalsIgnoreCase("DELETE_ROOM")) {
                        // 방 삭제 로직 추가 필요 (예: DB 처리)
                        sendToRoom(":: " + nickName + "님이 방을 삭제했습니다");
                        roomClients.remove(roomId);
                        break;
                    }

                    sendToRoom(nickName + " : " + msg);
                }
            } catch (IOException e) {
                System.out.println("연결 종료: " + nickName);
            } finally {
                // 퇴장 처리
                if (roomClients.containsKey(roomId)) {
                    roomClients.get(roomId).remove(nickName);
                    sendToRoom("<" + nickName + ">님이 나갔습니다");
                }
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendToRoom(String msg) {
            Map<String, DataOutputStream> clientsInRoom = roomClients.get(roomId);
            if (clientsInRoom != null) {
                for (DataOutputStream clientOut : clientsInRoom.values()) {
                    try {
                        clientOut.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
