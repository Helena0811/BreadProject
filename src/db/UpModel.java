/*
 * 하위 카테고리와 그 카테고리에 등록된 상품의 수 정보를 제공하는 모델
 * */
package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class UpModel extends AbstractTableModel{
	Vector<String> columnName=new Vector<String>();
	Vector<Vector> data=new Vector<Vector>();					// 2차원 벡터
	Connection con;
	
	public UpModel(Connection con) {
		this.con=con;
		getList();		// 생성자에 선언, 메소드라서 필요할 때 언제든 다시 호출 가능
	}
	
	// 목록 가져오기
	public void getList(){
		/*
		 * select s.subcategory_id, sub_name, count(product_name) from subcategory s left outer join product p 
			on s.subcategory_id=p.subcategory_id
			group by s.subcategory_id, sub_name;
			-> s.subcategory_id 앞에 소유권을 붙인 이유 : 중복된 컬럼이기 때문에 구분하기 위해서 사용
			-> sub_name인 경우 중복되지 않은 컬럼이기 때문에 소유권을 붙여도, 붙이지 않아도 상관없음
		 * */
		StringBuffer sql=new StringBuffer();
		sql.append("select s.subcategory_id as subcategory_id, sub_name as 카테고리명, count(product_name) as 개수");
		sql.append(" from subcategory s left outer join product p");
		sql.append(" on s.subcategory_id=p.subcategory_id");
		sql.append(" group by s.subcategory_id, sub_name");
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql.toString());
			rs=pstmt.executeQuery();
			
			// 테이블 갱신 -> 계속 누적되어 출력되는 것을 방지
			// columnName과 data Vector 초기화
			columnName.removeAll(columnName);
			data.removeAll(data);
			
			// 컬럼명 추출
			ResultSetMetaData meta=rs.getMetaData();
			for(int i=1; i<=meta.getColumnCount(); i++){
				// 컬럼명들을 vector에 저장
				columnName.add(meta.getColumnName(i));
			}
			
			while(rs.next()){
				// 레코드 1건을 vector에 저장 -> 여기서의 벡터는 DTO 역할
				Vector vec=new Vector();
				// JTable은 숫자, 문자 구분할 필요 없이 모두 String으로 가면 됨(Generic Type을 명시할 경우 String으로)
				vec.add(rs.getString("subcategory_id"));
				vec.add(rs.getString("카테고리명"));
				vec.add(rs.getString("개수"));
				
				data.add(vec);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return columnName.size();
	}

	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}
	
}
