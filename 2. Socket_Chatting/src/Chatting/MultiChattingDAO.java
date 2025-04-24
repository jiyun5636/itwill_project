package Chatting;

import jdbcUtil.*;
import java.sql.*;

public class MultiChattingDAO {

	public int createMultiChatRoomandList(String roomName, int[] userIds) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int roomId = -1;

		try {
			if (userIds.length < 2 || userIds.length > 5) {
				System.out.println("참가자는 최소 2명 , 최대 5명입니다.");
				return roomId;
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

		return roomId;
	}

}
