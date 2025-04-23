package jdbcUtil;

import java.sql.*;

//자바와 오라클 연동하는 클래스
public class Jdbc_Util {
//	private static final String DRIVER = "oracle.jdbc.OracleDriver";
//	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
//	private static final String USER = "ITWILL";
//	private static final String PASSWORD = "itwillpw";

	// DB연결 작업 기능
	public static Connection getConnection() {
		try {
			Class.forName(DBConfig.DRIVER);
			return DriverManager.getConnection(DBConfig.URL, DBConfig.USER, DBConfig.PASSWORD);
		} catch (ClassNotFoundException e) {
			System.out.println("[예외발생] : 드라이버 로드 실패");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("[예외발생] DB연결실패");
			// e.printStackTrace();
		}
		return null;
	}

	public static void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
		}
		close(conn, stmt);
	}

	public static void close(Connection conn, Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (SQLException e) {
		}
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
		}
	}

}
