package com.paris.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.DBManager;
import db.SubCategory;
import db.TopCategory;

public class MainWindow extends JFrame implements ItemListener{
	JPanel p_west, p_center, p_east;
	JPanel p_up, p_down;				// Grid로 JTable을 2개 넣을 패널
	JTable table_up, table_down;
	JScrollPane scroll_up, scroll_down;
	
	// 서쪽 영역
	Choice ch_top, ch_sub;
	JTextField t_name, t_price;
	Canvas can_west;
	JButton bt_regist;
	
	// 동쪽 영역
	Canvas can_east;
	JTextField t_name2, t_price2;
	JButton bt_edit, bt_delete;

	DBManager manager;
	Connection con;
	
	// 각 DTO를 담을 collection framework
	// 상위 카테고리 list
	ArrayList<TopCategory> topList=new ArrayList<TopCategory>();
	// 하위 카테고리 list
	ArrayList<SubCategory> subList=new ArrayList<SubCategory>();
	
	BufferedImage image=null;
	
	public MainWindow() {
		
		p_west=new JPanel();
		p_center=new JPanel();
		p_east=new JPanel();
		p_up=new JPanel();
		p_down=new JPanel();
		table_up=new JTable(3,6);
		table_down=new JTable(3,4);
		scroll_up=new JScrollPane(table_up);
		scroll_down=new JScrollPane(table_down);
		
		// 서쪽 영역
		ch_top=new Choice();
		ch_sub=new Choice();
		t_name=new JTextField(10);
		t_price=new JTextField(10);
		
		// 이미지 얻기
		// 반환형 : BufferedImage -> extends Image
		
		try {
			// res folder(source folder)를 간접적으로 얻기
			// getClass()로 현재 프로젝트의 정보를 얻어올 수 있음
			URL url=this.getClass().getResource("/default.png");
			image=ImageIO.read(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		can_west=new Canvas(){
			
			public void paint(Graphics g) {
				g.drawImage((Image)image, 0, 0, 120, 120, this);
			}
		};
		
		bt_regist=new JButton("등록");
		
		// 동쪽 영역
		can_east=new Canvas(){
			public void paint(Graphics g) {
				
			}
		};
		
		t_name2=new JTextField(10);
		t_price2=new JTextField(10);
		bt_edit=new JButton("수정");
		bt_delete=new JButton("삭제");

		can_west.setPreferredSize(new Dimension(120, 120));
		can_east.setPreferredSize(new Dimension(120, 120));
		
		// 구분이 가능하도록 각 패널의 색상 설정
		p_west.setBackground(Color.WHITE);
		p_center.setBackground(Color.ORANGE);
		p_east.setBackground(Color.pink);
		p_up.setBackground(Color.GREEN);
		p_down.setBackground(Color.BLUE);
		
		// 각 패널의 크기 설정
		p_west.setPreferredSize(new Dimension(150, 700));
		p_center.setPreferredSize(new Dimension(550, 700));
		p_east.setPreferredSize(new Dimension(150, 700));
		
		// 센터에 GridLayout 지정 후 위 아래 구성
		p_center.setLayout(new GridLayout(2, 1));
		p_center.add(p_up);
		p_center.add(p_down);
		
		p_up.setLayout(new BorderLayout());
		p_down.setLayout(new BorderLayout());
		
		// 스크롤 부착
		p_up.add(scroll_up);
		p_down.add(scroll_down);
		
		ch_top.setPreferredSize(new Dimension(135, 40));
		ch_sub.setPreferredSize(new Dimension(135, 40));
		
		ch_top.add("▼상위 카테고리 선택");
		ch_sub.add("▼하위 카테고리 선택");
		
		// choice와 ItemListener 연결
		ch_top.addItemListener(this);
		
		// 서쪽 영역 부착
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can_west);
		p_west.add(bt_regist);
		
		// 동쪽 영역 부착
		p_east.add(can_east);
		p_east.add(t_name2);
		p_east.add(t_price2);
		p_east.add(bt_edit);
		p_east.add(bt_delete);

		add(p_west, BorderLayout.WEST);
		add(p_center);
		add(p_east, BorderLayout.EAST);
		
		setTitle("재고 관리 프로그램");
		setSize(850, 700);
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// DB연동
		connect();
		
		// 최상위 카테고리 얻어오기
		getTopCategory();
	}
	
	// 데이터베이스 접속
	public void connect(){
		manager=DBManager.getInstance();
		con=manager.getConnection();
		
		//System.out.println(con);
	}
	
	// 최상위 카테고리 얻기
	public void getTopCategory(){
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from topcategory order by top_category_id asc";
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				TopCategory dto=new TopCategory();
				dto.setTop_category_id(rs.getInt("top_category_id"));
				dto.setTop_name(rs.getString("top_name"));
				
				topList.add(dto);
				
				// ch_top에 카테고리 채우기
				ch_top.add(dto.getTop_name());
			}
			
			/*
			for(int i=0; i<topList.size(); i++){
				ch_top.add(topList.get(i).getTop_name());
				ch_top.add(dto.getTop_name());
			}
			*/
			
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
	
	// 하위 카테고리 얻기
	// bind 변수 사용
	public void getSubCategory(int index){
		String sql=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		// 바인드 변수 사용
		sql="select * from subcategory where top_category_id=?";	// ?뒤 변수
		
		try {
			pstmt=con.prepareStatement(sql);
			// 바인드 변수값 지정
			// pstmt.setInt(parameterIndex, x);
			// 첫번째 발견된 바인드 변수, 사용자가 선택한 top_category_id
			//int index=ch_top.getSelectedIndex();
			// if(index-1>=0){}
			
			if(index>=0){
				TopCategory dto=topList.get(index);
				pstmt.setInt(1, dto.getTop_category_id());
				// 첫번째 바인드 변수에 사용자가 선택한 상위 카테고리 id가 들어감
				rs=pstmt.executeQuery();
				
				// ch_sub choice에 담기 전에 지우기
				subList.removeAll(subList);	// 메모리 지우기
				ch_sub.removeAll();				// choice 디자인 지우기
				
				while(rs.next()){
					SubCategory vo=new SubCategory();
					
					vo.setSubcategory_id(rs.getInt("subcategory_id"));
					vo.setTop_category_id(rs.getInt("top_category_id"));
					vo.setSub_name(rs.getString("sub_name"));
					
					subList.add(vo);
					
					ch_sub.add(vo.getSub_name());
				}
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
	
	
	// 상위 카테고리를 선택했을 때
	public void itemStateChanged(ItemEvent e) {
		Object obj=e.getSource();
		Choice ch=(Choice)obj;
		int index=ch.getSelectedIndex()-1;
	
		getSubCategory(index);
		
	}

	public static void main(String[] args) {
		new MainWindow();
	}


}
