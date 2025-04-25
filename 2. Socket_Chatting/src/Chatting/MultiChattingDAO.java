package Chatting;

import jdbcUtil.*;
import java.sql.*;

public class MultiChattingDAO {

	public int createMultiChatRoomandList(String roomName, int[] userIds) {
	    // userIds 배열 길이는 2~5명이어야 합니다.
	    String sql =
	        "INSERT INTO MULTICHATTINGROOM (" +
	        "  MULTICHATTINGROOM_ID, MULTIROOMNAME," +
	        "  PARTICIPANT1_ID, PARTICIPANT2_ID, PARTICIPANT3_ID," +
	        "  PARTICIPANT4_ID, PARTICIPANT5_ID" +
	        ") VALUES (" +
	        "  MULTICHATTINGROOM_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
	    int roomId = -1;

	    try (Connection conn = Jdbc_Util.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"MULTICHATTINGROOM_ID"})) {

	        conn.setAutoCommit(false);

	        // 1) 방 이름 설정
	        pstmt.setString(1, roomName);

	        // 2) PARTICIPANT 컬럼에 사용자 ID 채우기 (모자란 칸은 NULL 처리)
	        for (int i = 0; i < 5; i++) {
	            if (i < userIds.length) {
	                pstmt.setInt(2 + i, userIds[i]);
	            } else {
	                pstmt.setNull(2 + i, Types.INTEGER);
	            }
	        }

	        // 3) 실행 및 생성된 KEY 가져오기
	        pstmt.executeUpdate();
	        try (ResultSet rs = pstmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                roomId = rs.getInt(1);
	            }
	        }

	        conn.commit();
	        System.out.println("멀티채팅방 생성 완료! 방 ID: " + roomId);

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // 필요시 rollback 처리
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

	// 유저
	public boolean isUserInMultiRoom(int roomId, int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isCheck = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "SELECT COUNT(*) FROM MULTICHATTINGLIST ";
			sql += "WHERE MULTICHATTINGROOM_ID = ? AND UID = ? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, roomId);
			pstmt.setInt(2, userId);

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
			String delSql = "DELETE FROM MULTICHATTINGLIST WHERE MULTICHATTINGROOM_ID = ? AND UID = ?";
			delList = conn.prepareStatement(delSql);
			delList.setInt(1, roomId);
			delList.setInt(2, userId);
			int removed = delList.executeUpdate();
			delList.close();

			if (removed > 0) {
				// 2) 남은 참가자 수 확인
				String countSql = "SELECT COUNT(*) FROM MULTICHATTINGLIST WHERE MULTICHATTINGROOM_ID = ?";
				countStmt = conn.prepareStatement(countSql);
				countStmt.setInt(1, roomId);
				rs = countStmt.executeQuery();
				int cnt = 0;
				if (rs.next())
					cnt = rs.getInt(1);
				rs.close();
				countStmt.close();

				// 3) 0명인 경우 방 삭제
				if (cnt == 0) {
					String roomDelSql = "DELETE FROM MULTICHATTINGROOM WHERE MULTICHATTINGROOM_ID = ?";
					delRoom = conn.prepareStatement(roomDelSql);
					delRoom.setInt(1, roomId);
					delRoom.executeUpdate();
					delRoom.close();
				}

				conn.commit();
				success = true;
				System.out.println("[멀티] 방 나가기 완료" + (cnt == 0 ? " (방이 비어 삭제됨)" : ""));
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
}
