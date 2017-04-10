/*
 * topcategory 레코드 한 건을 담는 VO 객체
 * */
package db;

public class TopCategory {
	private int top_category_id;
	private String top_name;
	
	public int getTop_category_id() {
		return top_category_id;
	}
	public void setTop_category_id(int top_category_id) {
		this.top_category_id = top_category_id;
	}
	public String getTop_name() {
		return top_name;
	}
	public void setTop_name(String top_name) {
		this.top_name = top_name;
	}

}
