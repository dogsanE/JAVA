

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Random;
import java.awt.*;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

//JFrame �� ��ӹ����� �� ��ü�� ���ุ���ε� frame â�̶�
class Tetris extends JFrame implements Runnable, ActionListener, KeyListener{
	
	public boolean stop = true ;
	//boolean stop2 = false;
	
	public boolean over = true;
	
	int stopCount = 0;
	private int count = 0;
	private int ecount=0;
	private int bcount=0;
	
	private int x = 10;
	private int y = 18;
	private int y2 = 20;
	private int cell = 30;	
	
	private final static int MOVE = 2 ;
	private final static int STOP =  1;
	private final static int NONE = 0 ;
	private static int PREV = 3;
	
	private int Type ;			// ���� ��� type
	private int tmp , tmp2;		// ���� ��� tmp, tmp2
	private int preType;		// �̸����� ��� type
	private int pretmp, pretmp2;	//�̸����� ��� pretmp, pretmp2
	
	int Speed = 1000;
	
	private JPanel preview[][] ;
	private JPanel background[][] ;
	private JPanel center ;
	private Container ct ; //GUI
	private JLabel point = new JLabel("���� : ");
	private JLabel info = new JLabel();
	//private JLabel level = new JLabel("Level 0 : 1��");
	private JLabel runkey = new JLabel("���ӽ��� = F1");
	private JLabel stopkey = new JLabel("�Ͻ�����/���� = F2");	
	private JButton startB = new JButton("�����ϱ�");
	private JButton exitB = new JButton("������");
	
	JPanel sb;
	public JTextField namejf;
	public JLabel namelb, scorelb, scorelb2;
	
	private int Block[][];
	private int preBlock[][];
	
	Random r = new Random();
	
	public int angle =0;
	private ArrayList<Color> color;
	
	public Tetris(){
		
		this.color = new ArrayList<Color>();
		this.color.add(new Color(50,50,50));
		this.color.add(new Color(255,255,255));
		this.color.add(new Color(0,255,0));
		this.color.add(new Color(255,0,0));
		this.color.add(new Color(255,128,0));
		this.color.add(new Color(91,173,255));
		this.color.add(new Color(180,104,255));

		LineBorder line = new LineBorder(new Color(189,189,189),1);
		// �� cell ���̿� ���� �׾ ���� ȿ���� ����� �ش�.
	
		this.center = new JPanel() ;
		this.center.setSize((x*cell), (y*cell));
		this.center.setLayout(null);
		
		//setBounds�� �̿��Ͽ� �ϳ��� �����ӳ����� ��ġ�ؾ��Ұ��� �������ش�.
		this.center.add(runkey);
		this.runkey.setBounds(30+300, 30+100, 160,40);
		runkey.setFont(new Font("",1,15));
		
		this.center.add(stopkey);
		this.stopkey.setBounds(30+300,30+120,160,40);
		stopkey.setFont(new Font("",1,15));
		
		/*this.center.add(level);
		this.level.setBounds(30+300, 30+150, 150, 40);
		level.setFont(new Font("",1,15));*/
		
		this.center.add(info);
		this.info.setBounds(30+360,30+170, 170,40);
		info.setFont(new Font("",1,15));
		
		this.center.add(point);
		this.point.setBounds(30+300,30+170,50,40);
		point.setFont(new Font("",1,15));
		
		this.center.add(startB);
		this.startB.setBounds(50+300,30+400,100,40);
		startB.addActionListener(this);
		
		this.center.add(exitB);
		this.exitB.setBounds(50+300,30+450,100,40);
		exitB.addActionListener(this);
		
		this.background = new JPanel[this.x][this.y2];
		this.preview = new JPanel[5][5];
		
		// ContentPane ����� �̿��Ͽ� ����/������ ��ġ�� �����Ѵ�.
		this.ct = this.getContentPane();
		this.ct.add(center);

		
		//���� ȭ���� ����Ϳ� ��Ÿ���� ��ġ 
		this.setBounds(680, 230, (x*cell+180), (y*cell+62));

		this.Block  = new int[x][y];
		//this.preBlock = new int[4][4];
		
		// ��Ʈ���� ����ȭ�� 
		for(int i=0; i<x ; i++){
			for(int j=0; j< y; j++){
				this.background[i][j] = new JPanel();
				// 30�� ũ�⸦ ���� ���� [i],[j]�� ��ġ���� bounds
				this.background[i][j].setBounds(i*this.cell, j*this.cell, this.cell, this.cell);
				// ������ �־��ֱ� ���ؼ� line
				this.background[i][j].setBorder(line);
				// �� ����
				this.background[i][j].setBackground(new Color(50,50,50));
				
				this.center.add(background[i][j]);	
			}
		}
		
		// ���� ���� �̸����� grid
		for(int i=0; i<4 ; i++){
			for(int j=0; j<4; j++){
				this.preview[i][j] = new JPanel();
				//i*30(ũ��)+��ġ
				this.preview[i][j].setBounds(i*30+330 ,j*30, this.cell , this.cell);
				this.preview[i][j].setBorder(line);
				this.preview[i][j].setBackground(new Color(50,50,50));
				
				this.center.add(preview[i][j]);
			}
		}

		
		Thread t = new Thread(this);
		t.start();
		
		}
	
	

	
	void GameOver(){				// ���� ���� �޼���
		for(int a=0 ; a<3; a++){
			for(int b=0; b<10; b++){
				if(Block[b][a]==STOP){
					System.out.println("Ŭ���� ����"); // ������ ���� ���� �� �׷��� �������� �����.
					for(int i=0; i<10; i++){
						for(int j=0; j<18; j++){
							background[i][j].setBackground(color.get(0)); // ��� ������ ���� ������ ������ �ٲ��ش�.
							Block[i][j]=NONE;
					}
				}
			}
		}
	}
	 over = false;
	 
	 myScore dial = new myScore();
	 
	 //JOptionPane.showMessageDialog(center, " �������� ");
	 stop = true;
	 return ;
	 
	}
	
	void newCreate(){ // �� ���� �޼���
		
		tmp2 = r.nextInt(7); // 7���� ���� �������� �����ϰ� tmp2�� �־��ش�.
		tmp = tmp2; 
		Type= tmp; 
		
		switch(Type){	// switch, case�� ���� ���� �����Ѵ�.
		case 0: Square();
		break;
		
		case 1: Stick();
		break;
		
		case 2: RightPlus();
		break;
		
		case 3: LeftPlus();
		break;
		
		case 4: LongRightPlus();
		break;
		
		case 5: LongLeftPlus();
		break;
		
		case 6: School();
		break;
	}	
		
	if(Block[5][1]==STOP) // ���� [5][1] ��ġ�� ���� �� �� ���� ����.
		GameOver();
	
		stop = true ;	// GAMEOVER�� �Ǿ��� �� NEWCREATE�޼���� �ٷ� ������ ������
		over = true ;	// �ʱ⿡ �ʱⰪ�� �ް� ���� ���ϱ� ������, �ѹ� �� �ʱⰪ�� ����.
		
		//run();
	}
	
	/*
	void newpreCreate(){ // �� ���� �޼���
		
		preType = Type;
		
		switch(preType){	// switch, case�� ���� ���� �����Ѵ�.
		case 0: preSquare();
		break;
		
		case 1: preStick();
		break;
		
		case 2: preRightPlus();
		break;
		
		case 3: LeftPlus();
		break;
		
		case 4: LongRightPlus();
		break;
		
		case 5: LongLeftPlus();
		break;
		
		case 6: School();
		break;
		}
	}
	*/
		 
	public void run() { // stop�� �ʱⰪ�� true
		while (true) {
			while(stop!=false){
				System.out.println();
			}
			
			try {
				while (over == true) {
//					System.out.println("�ٿ���");
					Thread.sleep(Speed); // speed�� ������ ����� ������ sleep ����
					Down();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	int TetrisPointGet(){
		return count;
	}
	
	void SetStop() { // �Ͻ� ������ �ϱ� ���� �޼ҵ� ����
		if (stopCount == 0) {
			System.out.println("�½����� ȣ���");
			this.stop = false;
			System.out.println( "�����ǰ� : " + stop );
			stopCount++;
		}
		if (stopCount == 1) {
			this.stop = true;
			stopCount = 0;
		}
	}
	
	void Down() {
		if (Moving(1)) {
			for (int i = 9; i >= 0; i--) {
				for (int j = 17; j >= 0; j--) { // 0�ǻ����� �־���(=)
					if (Block[i][j] == MOVE) {
						
						this.background[i][j + 1].setBackground(this.background[i][j].getBackground()); // ��ĭ�����鼭 �����׷���
						this.background[i][j].setBackground(new Color(50, 50,50)); // �����ִ��ڸ��� ���� ������
						
						Block[i][j + 1] = MOVE;
						Block[i][j] = NONE;
						
						PREV=3;
					}
				}
			}
		} else {
			for (int i = 9; i >= 0; i--) {
				for (int j = 17; j >= 0; j--) {
					if (Block[i][j] == MOVE){
						Block[i][j] = STOP; // �� �̵��� �������� ��ž���� �ٲ����
						angle=0; //�ޱ۰� �߰��� ���� ����� ���� ���������� 0 �����ʱ�ȭ
						PREV = 0;
					}
					}
				}
			for (int i=0 ; i<18; i++){
				int cnt =0;
				for(int j=0; j<10; j++){
					if(Block[j][i]==STOP){// ���ο� ���� ��� �Ѱ��ǰ�
						cnt++; // ���δ� 1���� ���� ����
					}
					if (cnt == 10) { // ���� �Ѷ����� ������
						ecount++;
						bcount++;
						count++;
						for (int up = i ; up > 0; up--) {
							for (int k = 0; k < 10; k++) {
								this.background[k][up].setBackground(this.background[k][up-1].getBackground());
								
								Block[k][up] = Block[k][up-1];
							}
						}
						for (int d = 0; d < 10; d++) {
							this.background[d][0].setBackground(new Color(50,50, 50)); // �������� ������ ����
							Block[d][0] = NONE; // �������� ��������� �ٲ���
						}
					}
				}
			}
			stop = false;
			newCreate();
		}
		
		// ���� ����
		
		if(ecount>=4){ // 4���� ����� ���ÿ� ���� ��
			count += ecount*2;
			ecount = 0; // ecount �� �ʱ�ȭ
		}
		else
			ecount = 0; // �ƴϸ� ������ ����
		if(bcount>=3) { // 3�� ��� ���ÿ� ���� �� 5�� �߰�
			count += bcount+2;
		}
		else
			bcount = 0;
		
		this.info.setText(""+count);
	}
	
	// ��Ÿ ������, ����, ���� Ű���� �̺�Ʈ ����
	void Right() {
		if (Moving(2)) {
			for (int i = 9; i >= 0; i--) {
				for (int j = 0; j <= 17; j++) {
					if (Block[i][j] == MOVE) {
						this.background[i + 1][j].setBackground(this.background[i][j].getBackground());
						this.background[i][j].setBackground(new Color(50, 50,50));

						Block[i + 1][j] = MOVE;
						Block[i][j] = NONE;
					}
				}
			}
		}
	}
	
	void Left() {
		if(Moving(3)){
			for(int i = 0; i<=9 ; i++){
				for(int j=0; j<=17; j++){
					if(Block[i][j] == MOVE){
						this.background[ i - 1 ][j].setBackground(this.background[i][j].getBackground());
						this.background[i][j].setBackground(new Color(50,50,50));
						
						Block[ i - 1][j] = MOVE;
						Block[i][j] = NONE;
					}
				}
			}
		}
	}
	
	void Turn(){
		int num =  Type;
		
		
		loop: for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 18; j++) {
				if(Block[i][j]==MOVE){
					if(num==6 && angle==0 && Block[i+1][j+1]!=STOP ){ //school
						background[i+1][j+1].setBackground(color.get(6));
						background[i+2][j].setBackground(color.get(0));
						
						Block[i+1][j+1] = MOVE;
						Block[i+2][j] = NONE;
						angle++;
						break loop;
					}
					
					if(num==6 && angle==1 && Block[i+2][j]!=STOP ){
						background[i+2][j].setBackground(color.get(6));
						background[i+1][j-1].setBackground(color.get(0));
						
						Block[i+2][j] = MOVE;
						Block[i+1][j-1] = NONE;
						angle++;
						break loop;
					}
					
					if(num==6 && angle==2 && Block[i+1][j-1]!=STOP ){
						background[i+1][j-1].setBackground(color.get(6));
						background[i][j].setBackground(color.get(0));
						
						Block[i+1][j-1]= MOVE;
						Block[i][j]=NONE;
						angle++;
						break loop;
					}
					if(num==6 && angle==3 && Block[i-1][j+1]!=STOP){
						background[i-1][j+1].setBackground(color.get(6));
						background[i][j+2].setBackground(color.get(0));
						
						Block[i-1][j+1] = MOVE;
						Block[i][j+2] = NONE;
						angle=0;
						break loop;
					}
					 
					if(num==5 && angle==0 && Block[i+2][j+2]!=STOP){
						background[i+1][j+2].setBackground(color.get(5));
						background[i+2][j+2].setBackground(color.get(5));
						Block[i+1][j+2] = MOVE;
						Block[i+2][j+2] = MOVE;
						
						background[i][j].setBackground(color.get(0));
						background[i+1][j].setBackground(color.get(0));
						
						Block[i][j] = NONE;
						Block[i+1][j] =NONE;

						angle++;
						break loop;
					}
					
					if(num==5 && angle==1  && Block[i+2][j-1]!=STOP){	
						background[i][j].setBackground(color.get(0));
						background[i][j+1].setBackground(color.get(0));
						Block[i][j]=NONE;
						Block[i][j+1]=NONE;
						
						background[i+2][j].setBackground(color.get(5));
						background[i+2][j-1].setBackground(color.get(5));
						Block[i+2][j]=MOVE;
						Block[i+2][j-1]=MOVE;

						angle++;
						break loop;
					}
					
					if(num==5 &&angle==2 && Block[i-1][j-2]!=STOP){
						background[i][j].setBackground(color.get(0));
						background[i+1][j].setBackground(color.get(0));
						Block[i][j] = NONE;
						Block[i+1][j] =NONE;
						
						background[i][j-2].setBackground(color.get(5));
						background[i-1][j-2].setBackground(color.get(5));
						Block[i][j-2]=MOVE;
						Block[i-1][j-2]=MOVE;

						angle++;
						break loop;
					}
					if(num==5  && angle==3 &&Block[i][j+2]!=STOP &&Block[i][j+1]!=STOP){
						background[i][j+1].setBackground(color.get(5));
						background[i][j+2].setBackground(color.get(5));
						
						Block[i][j+1] =MOVE;
						Block[i][j+2] = MOVE;
						
						background[i+2][j].setBackground(color.get(0));
						background[i+2][j+1].setBackground(color.get(0));
						
						Block[i+2][j] = NONE;
						Block[i+2][j+1] = NONE;

						angle=0;
						break loop;
					}

					
					if(num==4  && angle==0 && Block[i-1][j+2]!=STOP){ //����Ʈ�÷���
						background[i][j+2].setBackground(color.get(4));
						background[i-1][j+2].setBackground(color.get(4));
						
						Block[i][j+2] = MOVE;
						Block[i-1][j+2]=  MOVE;
						
						background[i+1][j].setBackground(color.get(0));
						background[i][j].setBackground(color.get(0));
						
						Block[i+1][j] = NONE;
						Block[i][j] =NONE;

						angle++;
						break loop;	
					}

					if(num==4  && angle==1 && Block[i][j-2]!=STOP){
						background[i+2][j].setBackground(color.get(0));
						background[i+2][j-1].setBackground(color.get(0));
						
						Block[i+2][j] = NONE;
						Block[i+2][j-1] = NONE;
						
						background[i][j-1].setBackground(color.get(4));
						background[i][j-2].setBackground(color.get(4));
						
						Block[i][j-1] = MOVE;
						Block[i][j-2] = MOVE;

						angle++;
						break loop;
					}
					
					if(num==4  &&  angle==2 && Block[i+2][j]!=STOP ){
						background[i+1][j].setBackground(color.get(4));
						background[i+2][j].setBackground(color.get(4));
						
						Block[i+1][j] = MOVE;
						Block[i+2][j] = MOVE;
						
						background[i][j+2].setBackground(color.get(0));
						background[i+1][j+2].setBackground(color.get(0));
						
						Block[i][j+2] = NONE;
						Block[i+1][j+2] = NONE;

						angle++;
						break loop;
					}
					
					if(num==4  && angle==3 && Block[i+2][j+2]!=STOP){
						background[i+2][j+1].setBackground(color.get(4));
						background[i+2][j+2].setBackground(color.get(4));
						
						Block[i+2][j+1]  = MOVE;
						Block[i+2][j+2] = MOVE;
						
						background[i][j].setBackground(color.get(0));
						background[i][j+1].setBackground(color.get(0));
						
						Block[i][j] = NONE;
						Block[i][j+1] = NONE;

						angle=0;
						break loop;	
					}
					
					 if(num==3  &&  angle==0 && Block[i-1][j+2]!=STOP){ //����Ʈ�÷���
						background[i][j].setBackground(color.get(0));
						background[i][j+2].setBackground(color.get(3));
						
						Block[i][j] =NONE;
						Block[i][j+2]= MOVE;
						
						background[i+1][j+2].setBackground(color.get(0));
						background[i-1][j+2].setBackground(color.get(3));
						
						Block[i+1][j+2] = NONE;
						Block[i-1][j+2] = MOVE;

						angle++;
						break loop;
					}
					
					if(num==3  && angle==1 && Block[i+1][j-2]!=STOP){
						background[i][j].setBackground(color.get(0));
						background[i+2][j].setBackground(color.get(3));
						
						Block[i][j] = NONE;
						Block[i+2][j] = MOVE;
						
						background[i+1][j].setBackground(color.get(0));
						background[i+1][j-2].setBackground(color.get(3));
						
						Block[i+1][j] = NONE;
						Block[i+1][j-2] = MOVE;

						angle=0;
						break loop;
					}
					
				 if(num==2 && angle==0 && Moving(2) && Moving(3) && Moving(1) && Block[i+2][j+1]!=STOP){ // RightPlus ���浹�ù���
					background[i+1][j-1].setBackground(color.get(0));
					background[i+1][j+1].setBackground(color.get(2));
					
					Block[i+1][j-1] = NONE;
					Block[i+1][j+1] = MOVE;
					
					background[i][j+1].setBackground(color.get(0));
					background[i+2][j+1].setBackground(color.get(2));
					
					Block[i][j+1] = NONE;
					Block[i+2][j+1] =MOVE;
					
					angle++;
					break loop;
				}
				
				if( num==2 && angle==1 && Moving(2) && Moving(3) && Moving(1) && Block[i+1][j-1]!=STOP && Block[i+1][j]!=STOP){
					background[i+1][j-1].setBackground(color.get(2));
					background[i+1][j+1].setBackground(color.get(0));
					
					Block[i+1][j-1] = MOVE;
					Block[i+1][j+1] = NONE;
					
					background[i][j+1].setBackground(color.get(2));
					background[i+2][j+1].setBackground(color.get(0));
					
					Block[i][j+1] =MOVE;
					Block[i+2][j+1] = NONE;
					
					angle =0;
					break loop;
				}
							
			if(num==1 && angle==0 && i-3<10 && i+2<10 && Block[i+2][j+3]!=STOP){ //��ƽ ���浹�� �����ذ�ٶ�

				background[i + 2][j + 1].setBackground(new Color(255,255,255));
				background[i + 3][j].setBackground(new Color(50,50, 50));
				Block[i + 2][j + 1] = MOVE;
				Block[i + 3][j] = NONE;

				background[i + 2][j + 2].setBackground(new Color(255,255,255));
				background[i + 1][j].setBackground(new Color(50,50, 50));
				Block[i + 2][j + 2] = MOVE;
				Block[i + 1][j] = NONE;
				
				background[i + 2][j + 3].setBackground(new Color(255,255,255));
				background[i][j].setBackground(new Color(50, 50, 50));
				Block[i + 2][j + 3] = MOVE;
				Block[i][j] = NONE;
				angle++;

				break loop;
					}
					
				if (num==1 && angle == 1 && (i-2)>=0 && (i+1)<=9 &&Block[i-1][j]!=STOP &&Block[i-2][j]!=STOP &&Block[i+1][j]!=STOP  ) {
					background[i-1][j].setBackground(color.get(1));
					background[i+1][j].setBackground(color.get(1));
					background[i-2][j].setBackground(color.get(1));
					
					Block[i-1][j] = MOVE;
					Block[i+1][j] = MOVE;
					Block[i-2][j] = MOVE;
					
					background[i][j+2].setBackground(color.get(0));
								
					Block[i][j+2] = NONE;
					background[i][j + 3].setBackground(color.get(0));
					Block[i][j+3] = NONE;
					background[i][j+1].setBackground(color.get(0));
					
					Block[i][j+1] =NONE;
				
					angle=0;
					break loop;
				}
				break loop;
				}
			}
		}
	}
	
	boolean Moving(int run) { 
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				if (Block[i][j] == MOVE) {
					if (run == 1) { 
						if ((j + 1) > 17)
							return false;
						if (Block[i][j + 1] == STOP)
							return false;
					}
					
					else if (run == 2) {	
						if ((i + 1) > 9)
							return false;
						if(Block[i+1][j]==STOP) //�� ������ �浹�� ���� 
							return false;
					}
					
					else if(run == 3){
						if((i - 1) < 0)
							return false;
						if(Block[i-1][j]==STOP)
							return false;
					
					}
				}
			}
		}
		
		return true;
	}
	
	void Square() { 			// �簢�� ������ �⺻�� ����
		 Block[4][0] = MOVE;
		 Block[5][0] = MOVE;
		 Block[4][1] = MOVE;
		 Block[5][1] = MOVE;
		 
		 this.background[4][0].setBackground(new Color(255,255,0));
		 this.background[5][0].setBackground(new Color(255,255,0));
		 this.background[4][1].setBackground(new Color(255,255,0));
		 this.background[5][1].setBackground(new Color(255,255,0));
	}
	
	void preSquare() { 			// �̸� ����
		 Block[4][0] = MOVE;
		 Block[5][0] = MOVE;
		 Block[4][1] = MOVE;
		 Block[5][1] = MOVE;
		 
		 this.background[4][0].setBackground(new Color(255,255,0));
		 this.background[5][0].setBackground(new Color(255,255,0));
		 this.background[4][1].setBackground(new Color(255,255,0));
		 this.background[5][1].setBackground(new Color(255,255,0));
	}
	
	void Stick() {				// ��ƽ�� ������ �⺻�� ����
		 Block[3][0] = MOVE;
		 Block[4][0] = MOVE;
		 Block[5][0] = MOVE;
		 Block[6][0] = MOVE;
		 
		 this.background[3][0].setBackground(new Color(255,255,255));
		 this.background[4][0].setBackground(new Color(255,255,255));
		 this.background[5][0].setBackground(new Color(255,255,255));
		 this.background[6][0].setBackground(new Color(255,255,255));
	}
	
	void RightPlus(){			// �� �� ����
		 Block[5][0] = MOVE;
		 Block[5][1] = MOVE;
		 Block[4][1] = MOVE;
		 Block[4][2] = MOVE;

		 this.background[5][0].setBackground(new Color(0,255,0));
		 this.background[5][1].setBackground(new Color(0,255,0));
		 this.background[4][1].setBackground(new Color(0,255,0));
		 this.background[4][2].setBackground(new Color(0,255,0));
	 }
	
	 void LeftPlus(){			// �� �� ����
		 Block[4][0] = MOVE;
		 Block[4][1] = MOVE;
		 Block[5][1] = MOVE;
		 Block[5][2] = MOVE;
		 
		 this.background[4][0].setBackground(new Color(255,0,0));
		 this.background[4][1].setBackground(new Color(255,0,0));
		 this.background[5][1].setBackground(new Color(255,0,0));
		 this.background[5][2].setBackground(new Color(255,0,0));
	 }
	 
	 void LongRightPlus(){		// ����
		 Block[4][0] = MOVE;
		 Block[5][0] = MOVE;
		 Block[5][1] = MOVE;
		 Block[5][2] = MOVE;
		 

		 this.background[4][0].setBackground(new Color(255,128,0));
		 this.background[5][0].setBackground(new Color(255,128,0));
		 this.background[5][1].setBackground(new Color(255,128,0));
		 this.background[5][2].setBackground(new Color(255,128,0));
	}
	 
	 void LongLeftPlus(){		// �� �ݴ���
		 Block[4][0] = MOVE;
		 Block[5][0] = MOVE;
		 Block[4][1] = MOVE;
		 Block[4][2] = MOVE;
		 
		 this.background[4][0].setBackground(new Color(91,173,255));
		 this.background[5][0].setBackground(new Color(91,173,255));
		 this.background[4][1].setBackground(new Color(91,173,255));
		 this.background[4][2].setBackground(new Color(91,173,255));
	 }
	 
	 void School (){			// �� ���
		 Block[4][1] = MOVE;
		 Block[5][0] = MOVE;
		 Block[5][1] = MOVE;
		 Block[6][1] = MOVE;
		 
		 this.background[4][1].setBackground(new Color(180,104,255));
		 this.background[5][0].setBackground(new Color(180,104,255));
		 this.background[5][1].setBackground(new Color(180,104,255));
		 this.background[6][1].setBackground(new Color(180,104,255));
	 }

	

	public static void main(String args[]){

		Tetris frame = new Tetris();
		TetrisMenu menu = new TetrisMenu(frame);
		frame.setJMenuBar(menu.menuBar);
		//frame.setLayout(new FlowLayout());
		frame.setTitle("��Ʈ����");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == exitB ){
			System.exit(0);
		}
		if(e.getSource() == startB) {
				
				stop = false ; //�������� ��Ʈ������ ����ȯ
				newCreate();
		}
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class myScore extends JFrame implements ActionListener  {
		
		JFrame jf;
		JPanel sb,sb2;
		JTextField namejf;
		JButton confirm;
		JLabel namelb, scorelb, scorelb2;
		
		PreparedStatement pstmtt = null;
		ResultSet sts = null;
		Connection cons = null;
		
		myScore(){
			
			jf = new JFrame();
			sb = new JPanel();
			sb2 = new JPanel();
			
			namejf = new JTextField(10);
			
			namelb = new JLabel("�̸� :");
			scorelb = new JLabel("���� :");
			scorelb2 = new JLabel(""+count);
			
			sb.add(namelb);
			sb.add(namejf);
			sb2.add(scorelb);
			sb2.add(scorelb2);
			
			JButton confirm = new JButton("�߰�");
			confirm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					accessDB(namejf.getText(),count);
					jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
			});
			
			jf.add(sb, BorderLayout.NORTH);
			jf.add(sb2, BorderLayout.CENTER);
			jf.add(confirm, BorderLayout.SOUTH);
			
			
			jf.setTitle("Game Over");
			jf.setVisible(true);
			jf.setSize(220,140);
			jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			/*
			 * if(e.getSource()==confirm) { String name = namejf.getText(); int score =
			 * count; accessDB(name, score); }
			 */
		}
	}
	
	public void accessDB(String name, int score) {
		System.out.println("����");
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		PreparedStatement pstmt = null;
		ResultSet st = null;
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/tetris?serverTimezone=UTC";
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
				System.out.println("����");
			}else {
				System.out.println("����");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
