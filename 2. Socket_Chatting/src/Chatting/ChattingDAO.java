package Chatting;

import jdbcUtil.*;
import java.sql.*;

public class ChattingDAO {

	public int createChatRoomAndList(int inviterId, int inviteeId, String roomName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int chattingRoomId = -1; // 생성된 방 ID를 담을 변수

		try {
			conn = Jdbc_Util.getConnection();
			conn.setAutoCommit(false);

			// 1) 채팅방 생성
			String sql = ""
					+ "INSERT INTO CHATTINGROOM (CHATTINGROOM_ID, ROOMNAME, UNAME1, UNAME2) VALUES (CHATTINGROOM_ID.NEXTVAL, ?, ?, ?)";
			pstmt = conn.prepareStatement(sql, new String[] { "CHATTINGROOM_ID" });
			pstmt.setString(1, roomName);
			pstmt.setInt(2, inviterId);
			pstmt.setInt(3, inviteeId);
			pstmt.executeUpdate();

			// 생성된 키 조회
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				chattingRoomId = rs.getInt(1);
			}
			rs.close();
			pstmt.close();

			// 2) 채팅 리스트에 등록
			String listSql = "" + "INSERT INTO CHATTINGLIST " + "  (CHATTINGLIST_ID, CHATTINGROOM_ID, UNAME1, UNAME2) "
					+ "VALUES (CHATTINGLIST_ID.NEXTVAL, ?, ?, ?)";
			pstmt = conn.prepareStatement(listSql);
			pstmt.setInt(1, chattingRoomId);
			pstmt.setInt(2, inviterId);
			pstmt.setInt(3, inviteeId);
			pstmt.executeUpdate();

			conn.commit();
			System.out.println(":: 채팅방이 생성되었습니다. 방 번호: " + chattingRoomId + " | 채팅방 상대: " + inviteeId);

		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			chattingRoomId = -1; // 실패 시 -1 반환
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return chattingRoomId;
	}

	// 유저 ID로 그 ID가 속해있는 모든 방 보기
	public void showAllChatRoomsByUserId(int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = Jdbc_Util.getConnection();

			// 1:1 채팅방 조회
			String sqlSingle = "" + "SELECT C.CHATTINGROOM_ID, C.ROOMNAME, "
					+ "CASE WHEN L.UNAME1 = ? THEN U2.NICKNAME ELSE U1.NICKNAME END AS OPPONENT_NICKNAME "
					+ "FROM CHATTINGROOM C " + "JOIN CHATTINGLIST L ON C.CHATTINGROOM_ID = L.CHATTINGROOM_ID "
					+ "JOIN MEMBER U1 ON L.UNAME1 = U1.ID " + "JOIN MEMBER U2 ON L.UNAME2 = U2.ID "
					+ "WHERE L.UNAME1 = ? OR L.UNAME2 = ?" + "ORDER BY C.CHATTINGROOM_ID";
			pstmt = conn.prepareStatement(sqlSingle);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);
			pstmt.setInt(3, userId);
			rs = pstmt.executeQuery();

			System.out.println("===== 1:1 채팅방 목록 =====");
			while (rs.next()) {
				int roomId = rs.getInt("CHATTINGROOM_ID");
				String roomName = rs.getString("ROOMNAME");
				String opponent = rs.getString("OPPONENT_NICKNAME");
				System.out.printf("[1:1] 방 ID: %d | 방 이름: %s | 상대: %s%n", roomId, roomName, opponent);
			}
			rs.close();
			pstmt.close();

			// 멀티채팅방 조회
			String sqlMulti = "" + "SELECT M.MULTICHATTINGROOM_ID, M.MULTIROOMNAME, " + "COUNT(L.ID) AS USER_COUNT, "
					+ "LISTAGG(U.NICKNAME, ', ') WITHIN GROUP (ORDER BY U.NICKNAME) AS PARTICIPANTS "
					+ "FROM MULTICHATTINGROOM M "
					+ "JOIN MULTICHATTINGLIST L ON M.MULTICHATTINGROOM_ID = L.MULTICHATTINGROOM_ID "
					+ "JOIN MEMBER U ON L.ID = U.ID " + "WHERE M.MULTICHATTINGROOM_ID IN ("
					+ "    SELECT MULTICHATTINGROOM_ID FROM MULTICHATTINGLIST WHERE ID = ?" + ") "
					+ "GROUP BY M.MULTICHATTINGROOM_ID, M.MULTIROOMNAME ORDER BY M.MULTICHATTINGROOM_ID";
			pstmt = conn.prepareStatement(sqlMulti);
			pstmt.setInt(1, userId);
			rs = pstmt.executeQuery();

			System.out.println("===== 멀티채팅방 목록 =====");
			while (rs.next()) {
				int roomId = rs.getInt("MULTICHATTINGROOM_ID");
				String roomName = rs.getString("MULTIROOMNAME");
				int userCount = rs.getInt("USER_COUNT");
				String participants = rs.getString("PARTICIPANTS");
				System.out.printf("[멀티] 방 ID: %d | 방 이름: %s | 인원: %d명 | 참가자: %s%n", roomId, roomName, userCount,
						participants);
			}

		} catch (SQLException e) {
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
			sql += "AND M.REGDATE >= SYSDATE - 1/24 ";
			sql += "ORDER BY M.REGDATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			rs = pstmt.executeQuery();

			System.out.println("===== 채팅 메시지 (방 ID : " + roomId + ") ======");
			System.out.println("   ===== 최근 대화 내용의 1시간만 출력됩니다. =====   ");
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

	public boolean DeleteRoom(int userId, int roomId) {
		Connection conn = null;
		PreparedStatement delListStmt = null;
		PreparedStatement delRoomStmt = null;
		boolean isSuccess = false;

		try {
			conn = Jdbc_Util.getConnection();
			conn.setAutoCommit(false);
			String delListSql = "DELETE FROM CHATTINGLIST " + "WHERE CHATTINGROOM_ID = ? "
					+ "  AND (UNAME1 = ? OR UNAME2 = ?)";
			delListStmt = conn.prepareStatement(delListSql);
			delListStmt.setInt(1, roomId);
			delListStmt.setInt(2, userId);
			delListStmt.setInt(3, userId);
			int listDeleted = delListStmt.executeUpdate();

			String delRoomSql = "DELETE FROM CHATTINGROOM " + "WHERE CHATTINGROOM_ID = ?";
			delRoomStmt = conn.prepareStatement(delRoomSql);
			delRoomStmt.setInt(1, roomId);
			int roomDeleted = delRoomStmt.executeUpdate();

			conn.commit();
			isSuccess = (listDeleted > 0 && roomDeleted > 0);

			if (isSuccess == true) {
				System.out.println("채팅방 삭제 완료");
			} else {
				System.out.println("채팅방 삭제 실패");
			}

		} catch (SQLException e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, delListStmt);
			Jdbc_Util.close(null, delRoomStmt);
		}

		return isSuccess;
	}

	// 유저 아이디
	public boolean checkRoomInUser(int roomId, int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean ischeck = false;

		try {

			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "SELECT COUNT(*) AS CNT FROM CHATTINGROOM ";
			sql += "WHERE CHATTINGROOM_ID = ? ";
			sql += "AND (UNAME1 = ? OR UNAME2 = ?)";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.setInt(2, userId);
			pstmt.setInt(3, userId);

			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt("CNT") > 0) {
				ischeck = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return ischeck;
	}

	public boolean isRoomExistById(int roomId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isExist = false;
		
		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "SELECT COUNT(*) AS CNT FROM CHATTINGROOM ";
			sql += "WHERE CHATTINGROOM_ID = ?";
			
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
		
			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt("CNT") > 0 ) {
				isExist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}
		
		return false;
	}
}