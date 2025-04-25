package Chatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import User.LoginUserInfo;

public class Server {
    private Map<Integer, Map<String, DataOutputStream>> roomClients;

    public Server() {
        roomClients = new HashMap<>();
    }

    public void start() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(10000);
            while (true) {
                Socket socket = serverSocket.accept();
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
        ChattingDAO chattingDAO = new ChattingDAO();
        LoginUserInfo loginUser = LoginUserInfo.getInstance();

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
                nickName = in.readUTF();
                roomId = in.readInt();

                if (!chattingDAO.checkRoomInUser(roomId, loginUser.getKey())) {
                    out.writeUTF(ServerMessageType.NO_ROOM.name());
                    return;
                }

                out.writeUTF(ServerMessageType.NORMAL.name());
                roomClients.putIfAbsent(roomId, new HashMap<>());
                roomClients.get(roomId).put(nickName, out);

                sendToRoom(ServerMessageType.NORMAL.name() + ":<" + nickName + ">님이 입장했습니다", nickName);

                while (true) {
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
                            out.writeUTF(ServerMessageType.EXIT.name());
                            return;
                        case DELETE_ROOM:
                            chattingDAO.DeleteRoom(loginUser.getKey(), roomId);
                            out.writeUTF(ServerMessageType.DELETE_ROOM.name());
                            return;
                        case NORMAL:
                            sendToRoom(ServerMessageType.NORMAL.name() + ":" + nickName + " : " + content, nickName);
                            break;
                    }
                }
            } catch (IOException e) {
                // 연결 종료
            } finally {
                if (roomClients.containsKey(roomId)) {
                    roomClients.get(roomId).remove(nickName);
                    sendToRoom(ServerMessageType.NORMAL.name() + ":<" + nickName + ">님이 나갔습니다", nickName);
                }
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendToRoom(String msg, String sender) {
            Map<String, DataOutputStream> clientsInRoom = roomClients.get(roomId);

            if (clientsInRoom != null) {
                for (String key : clientsInRoom.keySet()) {
                    DataOutputStream clientOut = clientsInRoom.get(key);
                    try {
                        if (!key.equals(sender)) {
                            clientOut.writeUTF(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
