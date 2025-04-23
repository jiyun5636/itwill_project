package User;

import java.sql.Connection;
import java.sql.PreparedStatement;

import jdbcUtil.Jdbc_Util;

//DB 서비스
public class UserDAO {
	private static final String DRIVER = "oracle.jdbc.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
	private static final String USER = "ITWILL";
	private static final String PASSWORD = "itwillpw";

	//int로 선언한 이유 : 행의 갯수를 반환하기 위해
	public int insert(User user) {
		Connection conn = null;
		//문자열로 직접 작성 할 경우 : Statement
		//파라미터를 사용할 경우 : PreparedStatement
		PreparedStatement pstmt = null;
		
		int result = 0;
		
		try {
			//DB연결
			conn = Jdbc_Util.getConnection();
			
			//실행시킬 쿼리문 작성
			StringBuilder sb=new StringBuilder();
			sb.append("INSERT INTO MEMBER ");
			sb.append("       (ID, NAME, NICKNAME, PW) ");
			sb.append("VALUES (ID.NEXTVAL, ?, ?, ?)");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			int i=1;
			pstmt.setString(i++, user.getName());
			pstmt.setString(i++, user.getNickName());
			pstmt.setString(i++, user.getPassword());
			
			//executeUpdate : INSERT, UPDATE, DELETE 등 값에 영향이 생길 때
			//executeQuery : SELECT 등 값을 실행할 때
			result=pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
			result -= 1;
		}finally {
			Jdbc_Util.close(conn, pstmt);
		}
		return result;
	}
	//아이디 중복체크
	//로그인
}
