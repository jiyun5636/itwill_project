package Chatting.groupChatting;

import jdbcUtil.*;
import java.sql.*;
import java.util.Map;

public class MultiChattingDAO {

	public int createMultiChatRoomandList(String roomName, Map<Integer, Integer> userIds) {
		int roomId = -1;
		String insertRoomSql = "INSERT INTO MULTICHATTINGROOM ("
				+ "MULTICHATTINGROOM_ID, MULTIROOMNAME, PARTICIPANT1_ID, PARTICIPANT2_ID, "
				+ "PARTICIPANT3_ID, PARTICIPANT4_ID, PARTICIPANT5_ID) "
				+ "VALUES (CHATTINGROOM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";

		String insertListSql = "INSERT INTO MULTICHATTINGLIST (" + "MULTICHATTINGLIST_ID, MULTICHATTINGROOM_ID, ID) "
				+ "VALUES (MULTICHATTINGLIST_SEQ.NEXTVAL, ?, ?)";

		try (Connection conn = Jdbc_Util.getConnection();
				PreparedStatement pstmtRoom = conn.prepareStatement(insertRoomSql,
						new String[] { "MULTICHATTINGROOM_ID" });
				PreparedStatement pstmtList = conn.prepareStatement(insertListSql)) {

			conn.setAutoCommit(false);

			// 1) 방 이름 설정
			pstmtRoom.setString(1, roomName);

			// 2) PARTICIPANT 컬럼에 사용자 ID 채우기
			for (int slot = 1; slot <= 5; slot++) {
				Integer uid = userIds.get(slot);
				if (uid != null) {
					pstmtRoom.setInt(slot + 1, uid);
				} else {
					pstmtRoom.setNull(slot + 1, Types.INTEGER);
				}
			}

			// 3) 회원 존재 여부 검증
			for (Integer uid : userIds.values()) {
				if (!isMemberExist(uid)) {
					throw new IllegalArgumentException("존재하지 않는 회원 ID: " + uid);
				}
			}

			// 4) 방 INSERT 실행
			pstmtRoom.executeUpdate();
			try (ResultSet rs = pstmtRoom.getGeneratedKeys()) {
				if (rs.next()) {
					roomId = rs.getInt(1);
				}
			}

			// 5) MULTICHATTINGLIST에 참가자 추가
			for (Integer uid : userIds.values()) {
				pstmtList.setInt(1, roomId);
				pstmtList.setInt(2, uid);
				pstmtList.executeUpdate();
			}

			conn.commit();
			System.out.println("멀티채팅방 및 참가자 목록 생성 완료! 방 ID: " + roomId);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return roomId;
	}

	// 채팅 메시지 보내기
	public boolean sendMessageToMultiChatRoom(int roomId, int senderId, String content) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isSuccess = false;

		try {

			conn = Jdbc_Util.getConnection();

			String sql = "";
			sql += "INSERT INTO MULTICHATMESSAGE ";
			sql += "(MULTICHATTINGMESSAGE_ID , MULTICHATTINGROOM_ID , UNAME1 , CONTENT , REGDATE) ";
			sql += "VALUES (MULTICHATMESSAGE_ID.NEXTVAL, ?,?,?,SYSDATE)";

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

	public void showMultiMessagesByRoomId(int roomId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";

			sql += "SELECT MULTICHATTINGMESSAGE_ID , U.NICKNAME , M.CONTENT , M.REGDATE ";
			sql += "FROM MULTICHATMESSAGE M ";
			sql += "JOIN MEMBER U ON M.UNAME1 = U.ID ";
			sql += "WHERE M.MULTICHATTINGROOM_ID = ? ";
			sql += "AND M.REGDATE >= SYSDATE - 1/24 ";
			sql += "ORDER BY M.REGDATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			rs = pstmt.executeQuery();

			System.out.println("===== 채팅 메시지 (방 ID : " + roomId + ") ======");
			System.out.println("   ===== 최근 대화 내용의 1시간만 출력됩니다. =====   ");
			while (rs.next()) {
				int messageId = rs.getInt("MULTICHATTINGMESSAGE_ID");
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
	
	
	// 유저
	public boolean isUserInMultiRoom(int roomId, int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isCheck = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "SELECT COUNT(*) FROM MULTICHATTINGROOM " + "WHERE MULTICHATTINGROOM_ID = ? "
					+ "AND (PARTICIPANT1_ID = ? OR PARTICIPANT2_ID = ? "
					+ "OR PARTICIPANT3_ID = ? OR PARTICIPANT4_ID = ? OR PARTICIPANT5_ID = ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			for (int i = 2; i <= 6; i++) {
				pstmt.setInt(i, userId);
			}

			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				isCheck = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return isCheck;
	}

	// 방 나가기
	public boolean exitMultiChatRoom(int roomId, int userId) {
		Connection conn = null;
		PreparedStatement delList = null;
		PreparedStatement countStmt = null;
		PreparedStatement delRoom = null;
		ResultSet rs = null;
		boolean success = false;

		try {
			conn = Jdbc_Util.getConnection();
			conn.setAutoCommit(false);

			// 1) 참가자 삭제
			String delSql = "DELETE FROM MULTICHATTINGLIST WHERE MULTICHATTINGROOM_ID = ? AND ID = ?";
			delList = conn.prepareStatement(delSql);
			delList.setInt(1, roomId);
			delList.setInt(2, userId);
			int removed = delList.executeUpdate();
			delList.close();

			if (removed > 0) {
				String updateParticipantSql = "UPDATE MULTICHATTINGROOM "
						+ "SET PARTICIPANT1_ID = CASE WHEN PARTICIPANT1_ID = ? THEN NULL ELSE PARTICIPANT1_ID END, "
						+ "    PARTICIPANT2_ID = CASE WHEN PARTICIPANT2_ID = ? THEN NULL ELSE PARTICIPANT2_ID END, "
						+ "    PARTICIPANT3_ID = CASE WHEN PARTICIPANT3_ID = ? THEN NULL ELSE PARTICIPANT3_ID END, "
						+ "    PARTICIPANT4_ID = CASE WHEN PARTICIPANT4_ID = ? THEN NULL ELSE PARTICIPANT4_ID END, "
						+ "    PARTICIPANT5_ID = CASE WHEN PARTICIPANT5_ID = ? THEN NULL ELSE PARTICIPANT5_ID END "
						+ "WHERE MULTICHATTINGROOM_ID = ?";
				try (PreparedStatement updateParticipant = conn.prepareStatement(updateParticipantSql)) {
					for (int i = 1; i <= 5; i++) {
						updateParticipant.setInt(i, userId);
					}
					updateParticipant.setInt(6, roomId);
					updateParticipant.executeUpdate();
				}

				String countSql = "SELECT COUNT(*) FROM MULTICHATTINGLIST WHERE MULTICHATTINGROOM_ID = ?";
				countStmt = conn.prepareStatement(countSql);
				countStmt.setInt(1, roomId);
				rs = countStmt.executeQuery();
				int cnt = 0;
				if (rs.next())
					cnt = rs.getInt(1);
				rs.close();
				countStmt.close();

				if (cnt == 0) {
					String roomDelSql = "DELETE FROM MULTICHATTINGROOM WHERE MULTICHATTINGROOM_ID = ?";
					delRoom = conn.prepareStatement(roomDelSql);
					delRoom.setInt(1, roomId);
					delRoom.executeUpdate();
					delRoom.close();
				}

				conn.commit();
				success = true;
				System.out.println("[멀티] 방 나가기 완료" + (cnt == 0 ? " (방이 비어 삭제됨 - 메시지 자동 삭제)" : ""));
			} else {
				conn.rollback();
				System.out.println("[멀티] 방에 참가 중인 사용자가 아닙니다.");
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
			Jdbc_Util.close(null, delRoom);
			Jdbc_Util.close(null, countStmt);
			Jdbc_Util.close(conn, delList, rs);
		}
		return success;
	}

	private boolean isMemberExist(int userId) throws SQLException {
		String sql = "SELECT COUNT(*) FROM MEMBER WHERE ID = ?";
		try (Connection conn = Jdbc_Util.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}
}
