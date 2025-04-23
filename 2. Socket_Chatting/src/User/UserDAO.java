package User;

import java.sql.*;
import jdbcUtil.Jdbc_Util;

//DB 서비스
public class UserDAO {
//	private static final String DRIVER = "oracle.jdbc.OracleDriver";
//	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
//	private static final String USER = "ITWILL";
//	private static final String PASSWORD = "itwillpw";

	// int로 선언한 이유 : 행의 갯수를 반환하기 위해
	public int insert(User user) {
		Connection conn = null;
		// 문자열로 직접 작성 할 경우 : Statement
		// 파라미터를 사용할 경우 : PreparedStatement
		PreparedStatement pstmt = null;

		int result = 0;

		try {
			// DB연결
			conn = Jdbc_Util.getConnection();

			// 실행시킬 쿼리문 작성
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO MEMBER ");
			sb.append("       (ID, NAME, NICKNAME, PW) ");
			sb.append("VALUES (ID.NEXTVAL, ?, ?, ?)");

			pstmt = conn.prepareStatement(sb.toString());

			int i = 1;
			pstmt.setString(i++, user.getName());
			pstmt.setString(i++, user.getNickName());
			pstmt.setString(i++, user.getPassword());

			// executeUpdate : INSERT, UPDATE, DELETE 등 값에 영향이 생길 때
			// executeQuery : SELECT 등 값을 실행할 때
			result = pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			result -= 1;
		} finally {
			Jdbc_Util.close(conn, pstmt);
		}
		return result;
	}
	// 아이디 중복체크

	public boolean isNickNameDuplicate(String nickName) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isNNDuplicate = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "SELECT * FROM MEMBER WHERE NICKNAME = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, nickName);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				isNNDuplicate = rs.getInt(1) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}
		return isNNDuplicate;
	}

	// 로그인

	public boolean login(String NickName, String password) {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isLoginSuccess = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "SELECT ID , NAME, NICKNAME , PW ";
			sql += "FROM MEMBER ";
			sql += "WHERE NICKNAME = ? AND PW = ?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, NickName);
			pstmt.setString(2, password);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("ID");
				String name = rs.getString("NAME");
				String dbNickName = rs.getString("NICKNAME");
				String dbPassword = rs.getString("PW");

				LoginUserInfo.getInstance().setUserInfo(id, name, dbNickName, dbPassword);
				isLoginSuccess = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return isLoginSuccess;

	}

	// 유저삭제
	public boolean deleteUser(int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean isDelete = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "DELETE FROM MEMBER WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);

			int result = pstmt.executeUpdate();
			if (result > 0) {
				isDelete = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt, rs);
		}

		return isDelete;
	}

	public boolean UpdateUser(LoginUserInfo loginUserInfo, int userId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isUpdate = false;

		try {
			conn = Jdbc_Util.getConnection();
			String sql = "";
			sql += "UPDATE MEMBER ";
			sql += "SET NAME = ?, ";
			sql += "NICKNAME = ?, ";
			sql += "PW = ? ";
			sql += "WHERE ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, loginUserInfo.getName());
			pstmt.setString(2, loginUserInfo.getNickName());
			pstmt.setString(3, loginUserInfo.getPassword());
			pstmt.setInt(4, loginUserInfo.getKey());

			System.out.println(loginUserInfo.toString());

			int result = pstmt.executeUpdate();
			if (result > 0) {
				System.out.println(result);
				isUpdate = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Jdbc_Util.close(conn, pstmt);
		}

		return isUpdate;
	}
}
