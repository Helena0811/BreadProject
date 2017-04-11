/*
 * 하위 카테고리에 등록된 상품 정보를 제공하는 모델
 * */
package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class DownModel extends AbstractTableModel{
	Vector<String> columnName=new Vector<String>();
	public Vector<Vector> data=new Vector<Vector>();
	
	Connection con;
	
	public DownModel(Connection con) {
		this.con=con;
		
		// 원래 column은 고정적이기 때문에 tableModel이 new로 생성될 때 column을 고정시켜놓기
		columnName.add("product_id");
		columnName.add("subcategory_id");
		columnName.add("product_name");
		columnName.add("price");
		columnName.add("img");
	}
	
	// 마우스로 사용자가 클릭할 때 마다 id값이 바뀌므로, 아래의 메소드를 클릭 시마다 호출
	public void getList(int subcategory_id){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		// 하위 카테고리별 개수에 따른 상품 목록 출력
		// select * from product where subcategory_id=선택된 것;
		String sql="select * from product where subcategory_id=?";
		
		try {
			pstmt=con.prepareStatement(sql);
			pstmt.setInt(1, subcategory_id);
			rs=pstmt.executeQuery();
			
			// Vector 초기화
			columnName.removeAll(columnName);
			data.removeAll(data);
			
			/*
			 * 애초에 생성자에서 고정시키고 있으므로 주석 처리
			// columnName 채우기
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1; i<=meta.getColumnCount(); i++){
				columnName.add(meta.getColumnName(i));
			}
			*/
			//System.out.println("getList 컬럼의 크기는 "+columnName.size());
			
			while(rs.next()){
				Vector vec=new Vector();
				// boxing : 기본자료형으로 들어감
				vec.add(rs.getString("product_id"));
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("product_name"));
				vec.add(rs.getString("price"));
				vec.add(rs.getString("img"));
				
				data.add(vec);
			}
			//System.out.println("getList 레코드의 크기는 "+data.size());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	public int getRowCount() {
		//System.out.println("레코드의 갯수는 "+data.size());
		return data.size();
	}

	public int getColumnCount() {
		// updateUI()를 수행해도 이 메소드는 tableModel이 new로 생성될 때 컬럼이 0이라고 판단하기 때문에 출력되지 않음
		// System.out.println("컬럼의 갯수는 "+data.size());
		return columnName.size();
	}

	public Object getValueAt(int row, int col) {
		Object value=data.get(row).get(col);
		// updateUI()를 수행해도 이 메소드는 tableModel이 new로 생성될 때 컬럼이 0이라고 판단하기 때문에 출력되지 않음
		// System.out.println("각 값은 "+value);
		return data.get(row).get(col);
	}
	
}
