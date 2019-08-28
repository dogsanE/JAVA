import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AcessDB {
	
	//데이터베이스 작업에 사용할 객체를 선언한다.
	Connection conn = null;
	PreparedStatement pstm = null;
	ResultSet rs = null;
	
	PreparedStatement pstmt = null;
	ResultSet st = null;
	Connection con = null;
	
	public AcessDB() {
		
	}
	
	String url = "jdbc:mysql://localhost:3306/tetris?serverTimezone=UTC";
	
	public AcessDB(int score) {
		
	}
	
	public void insert_db(String name, int score) {
		
		int chk = 0;
		
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			conn = DriverManager.getConnection(url, "root", "1234");
			
			String sql = "insert into tet values(?, ?);";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, name);
			pstm.setInt(2, score);
			
			chk = pstm.executeUpdate();
			
			if(chk != 0) {
				System.out.println("성공");
			}else {
				System.out.println("실패");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public void select_db(String name, int score) { int rank;
	 * 
	 * try { Class.forName("com.mysql.jdbc.Driver"); con =
	 * DriverManager.getConnection(url, "root", "1234");
	 * 
	 * String sql =
	 * "select * from tetris.tet where score = (select max(score) from tetris.tet);"
	 * ; pstm = con.prepareStatement(sql); rs=pstmt.executeQuery();
	 * 
	 * rs.next();
	 * 
	 * rs.getInt("score");
	 * 
	 * System.out.println(rs);
	 * 
	 * }catch(Exception e) { e.printStackTrace(); } }
	 */
	
	public static void main(String[] args) {
		AcessDB db = new AcessDB(); 
		// TODO Auto-generated method stub
		Scanner scanner = new Scanner(System.in);
		System.out.print("이름 : ");
		String name = scanner.nextLine();
		System.out.print("점수 : ");
		
		int score = Integer.parseInt(scanner.nextLine());
		//System.out.println("완료");
		db.insert_db(name, score);
	}
	

}
