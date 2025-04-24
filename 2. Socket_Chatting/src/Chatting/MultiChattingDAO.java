package Chatting;

import jdbcUtil.*;
import java.sql.*;

public class MultiChattingDAO {

	public boolean createMultiChatRoomandList(String roomName, int[] userIds) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isCreated = false;
		int roomId = -1;

		try {
			if (userIds.length < 2 || userIds.length > 5) {
				System.out.println("참가자는 최소 2명 , 최대 5명입니다.");
				return isCreated;
			}

			conn = Jdbc_Util.getConnection();
			conn.setAutoCommit(false);

			String sql = "";
			sql += "INSERT INTO MULTICHATTINGROOM (MULTICHATTINGROOM_ID, MULTIROOMNAME) ";
			sql += "VALUES (MULTICHATTINGROOM_SEQ.NEXTVAL, ?)";

			pstmt = conn.prepareStatement(sql, new String[] { "MULTICHATTINGROOM_ID" });
			pstmt.setString(1, roomName);
			pstmt.execute();

			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				roomId = rs.getInt(1);
			}

			rs.close();
			pstmt.close();

			// 초기 참가자
			String listSql = "";
			listSql += "INSERT INTO MULTICHATTINGLIST (MULTICHATTINGLIST_ID, MULTICHATTINGROOM_ID, ID ) ";
			listSql += "VALUES (MULTICHATTINGLIST_SEQ.NEXTVAL, ? , ?)";

			pstmt = conn.prepareStatement(listSql);

			for (int userId : userIds) {
				pstmt.setInt(1, roomId);
				pstmt.setInt(2, userId);
				pstmt.addBatch();
			}

			pstmt.executeBatch();
			conn.commit();

			System.out.println("채팅방이 생성되었습니다. | 방 이름 : " + roomName);
			isCreated = true;

		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println(ex);
			}
			System.out.println(e);
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return isCreated;
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

}
