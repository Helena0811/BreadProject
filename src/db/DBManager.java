/*
 * 1. 데이터베이스 접속 관련 정보를 데이터베이스 관련 코드 여러군데 두지 않게 위함
 * 2. 싱글톤으로 관리함으로써, 인스턴스를 불필요하게 많이 만들지 않아도 됨
 * 3. package book;
 * 싱글톤 안에 Connection 멤버로 보유하고 있으므로 connection을 한번에 연결 가능
 * public class DBManager{}
 * */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

	static private DBManager instance;
	private Connection con;	
	
	private String driver="oracle.jdbc.driver.OracleDriver";
	private String url="jdbc:oracle:thin:@localhost:1521:XE";
	private String user="bread";
	private String password="bread";
	
	private DBManager() {
		try {
			Class.forName(driver);
			con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
	public static DBManager getInstance() {
		if(instance==null){
			instance=new DBManager();
		}
		return instance;
	}
	
	public Connection getConnection() {
		return con;
	}

	// 다 사용했으면 연결 끊기
	public void disConnect(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
