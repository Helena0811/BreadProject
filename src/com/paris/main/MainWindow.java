/*
 * Join문
 * 정규화에 의해 물리적으로 분리된 테이블을 마치 하나의 테이블처럼 보여줄 수 있는 쿼리 기법
 * 
 * 1. inner join
 * 조인 대상이 되는 테이블 간 공통적인 레코드만 가져옴
 * 주의할 특징) 공통적인 레코드가 아닌 경우 누락시킴
 * 
 * 2. outer join
 * 조인 대상이 되는 테이블 간 공통된 레코드 뿐만 아니라, 지정한 테이블의 레코드는 무조건 가져옴
 * 
 * */
package com.paris.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import db.DBManager;
import db.DownModel;
import db.SubCategory;
import db.TopCategory;
import db.UpModel;

public class MainWindow extends JFrame implements ItemListener, ActionListener{
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
	JTextField t_id, t_name2, t_price2;
	JButton bt_edit, bt_delete;

	DBManager manager;
	Connection con;
	
	// 각 DTO를 담을 collection framework
	// 상위 카테고리 list
	ArrayList<TopCategory> topList=new ArrayList<TopCategory>();
	// 하위 카테고리 list
	ArrayList<SubCategory> subList=new ArrayList<SubCategory>();
	
	BufferedImage image=null;
	
	// 테이블 모델
	UpModel upModel;
	DownModel downModel;
	
	// 이미지 관련
	JFileChooser chooser;
	File file;
	
	public MainWindow() {
		chooser=new JFileChooser("C:/Users/sist110/Pictures/images");
		
		p_west=new JPanel();
		p_center=new JPanel();
		p_east=new JPanel();
		p_up=new JPanel();
		p_down=new JPanel();
		table_up=new JTable();
		table_down=new JTable();
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
				g.drawImage((Image)image, 0, 0, 120, 120, this);
			}
		};
		
		// canvas의 MouseListener 연결
		can_west.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				preview();
			}
		});
		
		can_east.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		
		t_id=new JTextField(10);
		t_id.setEnabled(false);
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
		
		// 버튼과 ActionListener 연결
		bt_regist.addActionListener(this);
		
		// 위쪽 테이블인 table_up과 MouseListener 연결
		table_up.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// 현재 선택한 레코드의 subcategory_id 구하기
				int row=table_up.getSelectedRow();
				int col=0;
				//System.out.println("현재 선택된 좌표는"+row);
				String subcategory_id=(String)(table_up.getValueAt(row, col));
				System.out.println("현재 선택한 레코드의 subcategory_id : "+subcategory_id);
				// 구한 id를 아래의 모델에 적용
				// -> 현재 마우스로 선택한 레코드의 subcategory_id를 넘겨 상품 목록 출력
				
				// 현재 tableModel이 new로 생성될 때 아무 값도 들어있지 않으므로 컬럼이 0이라 판단
				// 원래 column은 고정적이기 때문에 tableModel이 new로 생성될 때 column을 고정시켜놓기
				downModel.getList(Integer.parseInt(subcategory_id));
				// 아래쪽 JTable에 출력
				table_down.updateUI();
				
				// getList()는 정상적으로 되고 있는데ㅠㅠ
				// System.out.println(downModel.getRowCount());
			}
		});
		
		// 아래쪽 테이블인 table_down과 MouseListener 연결
		table_down.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row=table_down.getSelectedRow();
				
				// downModel이 보유하고 있는 2차원 벡터 data의 row번째 가져오기
				Vector vec=downModel.data.get(row);
				getDetail(vec);
			}
		});
		
		// 서쪽 영역 부착
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can_west);
		p_west.add(bt_regist);
		
		// 동쪽 영역 부착
		p_east.add(can_east);
		p_east.add(t_id);
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
		
		// 위쪽 JTable 가져오기
		getUpList();
		
		// 아래쪽 JTable 가져오기
		getDownList();
	}
	
	// canvas에 이미지 반영
	public void preview(){
		int result=chooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION){
			file=chooser.getSelectedFile();
			// 캔버스에 이미지 그리기
			// 선택한 파일을 기존의 이미지로 대체
			try {
				image=ImageIO.read(file);
				can_west.repaint();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 이미지 복사
	public void copy(){
		FileInputStream fis=null;
		FileOutputStream fos=null;
		
		try {
			fis=new FileInputStream(file);
			fos=new FileOutputStream("C:/java_workspace2/BreadProject/data/"+file.getName());
			
			byte[] b=new byte[1024];
			
			int flag;		// -1인지 여부 판단
			while(true){
				flag=fis.read(b);
				if(flag==-1){
					break;
				}
				fos.write(b);		// 실제 데이터는 b에 들어있음
				System.out.println("이미지 복사 완료");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
	
	// 위쪽 테이블 처리
	public void getUpList(){
		table_up.setModel(upModel=new UpModel(con));
		table_up.updateUI();
	}
	
	// 아래쪽 테이블 처리
	public void getDownList(){
		table_down.setModel(downModel=new DownModel(con));
		table_down.updateUI();
	}
	
	// 상위 카테고리를 선택했을 때
	public void itemStateChanged(ItemEvent e) {
		Object obj=e.getSource();
		Choice ch=(Choice)obj;
		int index=ch.getSelectedIndex()-1;
	
		getSubCategory(index);
	}
	
	/*---------------------------------------------
	 상품 등록
	---------------------------------------------*/
	public void regist(){
		PreparedStatement pstmt=null;
		
		String sql="insert into product(product_id, subcategory_id, product_name, price, img)";
		sql+=" values(seq_product.nextval, ?, ?, ?, ?)";

		try {
			pstmt=con.prepareStatement(sql);
			
			// 바인드 변수에 들어갈 값 결정
			// subcategory_id 구하는 방법
			// ArrayList 안에 들어있는 SubCategory DTO를 추출하여 PK값을 넣기
			int sub_id=subList.get(ch_sub.getSelectedIndex()).getSubcategory_id();
			pstmt.setInt(1, sub_id);
			pstmt.setString(2, t_name.getText());
			pstmt.setInt(3, Integer.parseInt(t_price.getText()));
			pstmt.setString(4, file.getName());
			
			// 여기서 바로 디버깅 불가, 바인드 변수 값을 확인할 수 없다!
			// -> 디버깅은 풀어서 하는 수 밖에 없음ㅜㅜ
			//System.out.println(sql);
			
			// executeUpdate 메소드는 쿼리문 수행 후 반영된 레코드의 갯수 반환
			// 따라서, insert문의 경우 성공한 경우 항상 1, update는 1건 이상, delete는 1건 이상
			// 결론 : insert문 수행 시 반환값이 0이라면 실패!
			int result=pstmt.executeUpdate();
			
			if(result!=0){
				JOptionPane.showMessageDialog(this, "등록 성공");
				// 등록했으므로 DB를 새롭게 가져와 2차원 Vector가 변경됨
				upModel.getList();
				table_up.updateUI();
				
				// 이미지 파일 복사 후 저장
				copy();
			}
			else{
				JOptionPane.showMessageDialog(this, "등록 실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// 오른쪽 패널에 아래 테이블에서 선택한 제품의 상세 정보 보여주기
	public void getDetail(Vector vec){
		t_id.setText(vec.get(0).toString());
		t_name2.setText(vec.get(2).toString());
		t_price2.setText(vec.get(3).toString());
		
		// 이미지 교체
		try {
			image=ImageIO.read(new File("C:/java_workspace2/BreadProject/data/"+vec.get(4).toString()));
			can_east.repaint();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// 등록 버튼을 눌렀을 때
	public void actionPerformed(ActionEvent e) {
		regist();
	}

	public static void main(String[] args) {
		new MainWindow();
	}

}
