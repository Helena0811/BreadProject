package db;

public class SubCategory {
	private int subcategory_id;
	private int top_category_id;
	private String sub_name;
	public int getSubcategory_id() {
		return subcategory_id;
	}
	public void setSubcategory_id(int subcategory_id) {
		this.subcategory_id = subcategory_id;
	}
	public int getTop_category_id() {
		return top_category_id;
	}
	public void setTop_category_id(int top_category_id) {
		this.top_category_id = top_category_id;
	}
	public String getSub_name() {
		return sub_name;
	}
	public void setSub_name(String sub_name) {
		this.sub_name = sub_name;
	}
	
	
}
