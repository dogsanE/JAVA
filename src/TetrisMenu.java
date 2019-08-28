

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

class TetrisMenu implements ActionListener,KeyListener{
	
	JMenuBar menuBar = new JMenuBar();
	JMenu menuName = new JMenu("�ɼ�");
	
	JMenuItem start = new JMenuItem("����");
	JMenuItem stop = new JMenuItem("�Ͻ�����");
	JMenuItem Log = new JMenuItem("�ְ���");
	JMenuItem exit = new JMenuItem("����");
	JMenuItem help = new JMenuItem("����");
	JFrame frame;
	
	int count=0;
	
	TetrisMenu(Tetris frame){
	
	menuBar.add(menuName);

	
	menuName.add(help);
	menuName.add(start);
	menuName.add(stop);
	menuName.add(Log);
	menuName.add(exit);
	
	this.frame = frame;
	((Tetris)frame).addKeyListener(this);
	start.addActionListener(this);
	help.addActionListener(this);
	Log.addActionListener(this);
	stop.addActionListener(this);
	exit.addActionListener(this);
	}
	
	TetrisMenu(){
		
		
	}


	public void actionPerformed(ActionEvent e) {
		String name = "";
		int score = 0;
		
		AcessDB db = new AcessDB();
		//db.select_db(name,score);
		
		
		if(e.getSource() == start){
			((Tetris)frame).stop = false ; //�������� ��Ʈ������ ����ȯ
			((Tetris)frame).newCreate();
			System.out.println("����");
		}
		
		if(e.getSource() == stop ){
			if(count==0){
			((Tetris)frame).over = false;
			System.out.println("����");
			count++;
			}
			else if(count==1){
				((Tetris)frame).over = true;
				System.out.println("��������");
				count=0;
			}
		}
		
		if(e.getSource() == help){
		
			JLabel help =new JLabel("<html>�������� : ����Ű<br>�������� �����̽�Ű<br><br>-----���������������-----<br>");
			
			JFrame hframe = new JFrame("����");
			Container ctp = hframe.getContentPane();
			ctp.add(help);
			hframe.setBounds(680, 200, 200, 200);
			hframe.setVisible(true);
			
			
		}
		
		if(e.getSource() == Log){
			
				Connection conn = null;
				PreparedStatement pstm = null;
				Statement sts;
				
				PreparedStatement pstmt = null;
				ResultSet st = null;
				Connection con = null;
				
				String url = "jdbc:mysql://localhost:3306/tetris?serverTimezone=UTC";
				int chk = 0;
				int maxscore=99;
				
				try {
					Class.forName("com.mysql.jdbc.Driver"); 
					conn = DriverManager.getConnection(url, "root", "1234");
					sts = conn.createStatement();
					
					
					String sql = "select * from tetris.tet where score = (select max(score) from tetris.tet);";
					ResultSet rst = sts.executeQuery(sql);
					
					rst.next();
						
						//System.out.print(maxscore);
						//System.out.println(rst);
					//JOptionPane.showMessageDialog(null, "�ֿ�� �÷��̾� :"+rst.getString(name)+"<html><br>�ְ����� :"+rst.getString("score"));
					JOptionPane.showMessageDialog(null, "�ֿ�� �÷��̾� : "+rst.getString("name")+",  ���� : "+rst.getString("score")+" ��");
					
					
				}catch(Exception es) {}
				
				/*
				 * JLabel help =new JLabel("<html>�ְ�����1 : ");
				 * //help.setText("<body>test</body></html>");
				 * help.setText(help.getText()+String.valueOf(returnINT()));
				 * 
				 * JFrame hhframe = new JFrame("����"); Container ctpp =
				 * hhframe.getContentPane(); ctpp.add(help); hhframe.setBounds(680, 200, 200,
				 * 200); hhframe.setVisible(true);
				 */
				
			}
		
		if(e.getSource() == exit ){
			System.exit(0);
		}
	}
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_F1 :
			((Tetris)frame).stop = false; //�������� ��Ʈ������ ����ȯ
			boolean over2 = ((Tetris)frame).over;
			if(over2 != false) {
				((Tetris)frame).newCreate();
			}
			else {
				((Tetris)frame).Down();
			}
			System.out.println("����");
			break;
			
		case KeyEvent.VK_F2 :
			if(count==0){
			((Tetris)frame).over = false;
			((Tetris)frame).angle = 1;
			System.out.println("����");
			count++;
			}
			else if(count==1){
				((Tetris)frame).over = true;
				System.out.println("��������");
				count=0;
			}
		case KeyEvent.VK_DOWN :
			((Tetris)frame).Down();
			break;
			
		case KeyEvent.VK_RIGHT :
			((Tetris)frame).Right();
			break;
			
		case KeyEvent.VK_LEFT :
			((Tetris)frame).Left();
			break;
			
		case KeyEvent.VK_UP :
			((Tetris)frame).Turn();
			break;
			
		case KeyEvent.VK_SPACE :
			while (((Tetris)frame).stop) {
				((Tetris)frame).Down();
			}
			((Tetris)frame).stop = true;
			break;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
	public void keyTyped(KeyEvent e) {
		
	}
}



