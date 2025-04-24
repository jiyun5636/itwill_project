package Chatting;

import jdbcUtil.*;
import java.sql.*;

public class ChattingDAO {

	public boolean createChatRoomandList(int inviterId, int inviteeId, String roomName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isCreated = false;

		try {
			conn = Jdbc_Util.getConnection();
			conn.setAutoCommit(false);

			// 채팅방 생성 쿼리
			String sql = "";
			sql += "INSERT INTO CHATTINGROOM ";
			sql += "(CHATTINGROOM_ID, ROOMNAME, UNAME1, UNAME2) ";
			sql += "VALUES (CHATTINGROOM_ID.NEXTVAL, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql, new String[] { "CHATTINGROOM_ID" });
			pstmt.setString(1, roomName);
			pstmt.setInt(2, inviterId);
			pstmt.setInt(3, inviteeId);
			pstmt.executeUpdate();

			rs = pstmt.getGeneratedKeys();
			int chattingRoomId = -1;
			if (rs.next()) {
				chattingRoomId = rs.getInt(1);
			}

			rs.close();
			pstmt.close();

			// 채팅 리스트 생성

			String listSql = "";
			listSql += "INSERT INTO CHATTINGLIST ";
			listSql += "(CHATTINGLIST_ID, CHATTINGROOM_ID , UNAME1 , UNAME2) ";
			listSql += "VALUES (CHATTINGLIST_ID.NEXTVAL , ? , ? , ? )";

			pstmt = conn.prepareStatement(listSql);
			pstmt.setInt(1, chattingRoomId);
			pstmt.setInt(2, inviterId);
			pstmt.setInt(3, inviteeId);
			pstmt.executeUpdate();

			conn.commit();

			isCreated = true;

			System.out.println(":: 채팅방이 생성되었습니다. 방 번호 : " + chattingRoomId + "| 채팅방 상대 : " + inviteeId);

		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return isCreated;
	}

	// 유저 ID로 그 ID가 속해있는 모든 방 보기
	public void showChatRoomByUserId(int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = Jdbc_Util.getConnection();

			String sql = "";
			sql += "SELECT C.CHATTINGROOM_ID , C.ROOMNAME, ";
			sql += "       CASE ";
			sql += "           WHEN L.UNAME1 = ? THEN U2.NICKNAME ";
			sql += "           ELSE U1.NICKNAME ";
			sql += "       END AS OPPONENT_NICKNAME ";
			sql += "FROM CHATTINGROOM C ";
			sql += "JOIN CHATTINGLIST L ON C.CHATTINGROOM_ID = L.CHATTINGROOM_ID ";
			sql += "JOIN MEMBER U1 ON L.UNAME1 = U1.ID ";
			sql += "JOIN MEMBER U2 ON L.UNAME2 = U2.ID ";
			sql += "WHERE L.UNAME1 = ? OR L.UNAME2 = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);
			pstmt.setInt(3, userId);

			rs = pstmt.executeQuery();

			System.out.println("===== 채팅방 목록 =====");
			while (rs.next()) {
				int roomId = rs.getInt("CHATTINGROOM_ID");
				String roomName = rs.getString("ROOMNAME");
				String Nick = rs.getString("OPPONENT_NICKNAME");
				System.out.println("방 ID : " + roomId + " | 방 이름 : " + roomName + " | 대화 상대 : " + Nick);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

	}

	// 찾은 방에서 원하는 방 번호를 입력하면 그 방 안에 있는 메세지dB 다 불러와서 보여주기
	public void showMessagesByRoomId(int roomId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";

			sql += "SELECT CHATTINGMESSAGE_ID , U.NICKNAME , M.CONTENT , M.REGDATE ";
			sql += "FROM CHATMESSAGE M ";
			sql += "JOIN MEMBER U ON M.UNAME1 = U.ID ";
			sql += "WHERE M.CHATTINGROOM_ID = ? ";
			sql += "ORDER BY M.REGDATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			rs = pstmt.executeQuery();

			System.out.println("===== 채팅 메시지 (방 ID : " + roomId + ") ======");
			while (rs.next()) {
				int messageId = rs.getInt("CHATTINGMESSAGE_ID");
				String sender = rs.getString("NICKNAME");
				String content = rs.getString("CONTENT");
				Timestamp regDate = rs.getTimestamp("REGDATE");

				System.out.println("[" + regDate + "] " + sender + " : " + content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

	}

	// 채팅 메시지 보내기
	public boolean sendMessage(int roomId, int senderId, String content) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isSuccess = false;

		try {

			conn = Jdbc_Util.getConnection();

			String sql = "";
			sql += "INSERT INTO CHATMESSAGE ";
			sql += "(CHATTINGMESSAGE_ID , CHATTINGROOM_ID , UNAME1 , CONTENT , REGDATE) ";
			sql += "VALUES (CHATMESSAGE_ID.NEXTVAL, ?,?,?,SYSDATE)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.setInt(2, senderId);
			pstmt.setString(3, content);

			int result = pstmt.executeUpdate();
			if (result > 0) {
				isSuccess = true;
			} else {
				System.out.println("메시지 전송 실패");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt);
		}

		return isSuccess;
	}

	public boolean exitChatRoom(int userId, int roomId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isExitSuccess = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "DELETE FROM CHATTINGLIST ";
			sql += "WHERE CHATTINGROOM_ID ";
			sql += "AND (UNAME1 = ? OR UNAME2 = ?)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.setInt(2, userId);
			pstmt.setInt(3, userId);

			int result = pstmt.executeUpdate();
			if (result > 0) {
				isExitSuccess = true;
				System.out.println("채팅방 나가기 성공");
			} else {
				System.out.println("채팅방 나가기 실패");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt);
		}

		return isExitSuccess;

	}

}