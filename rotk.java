package yong;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;


// ���һ�� x y  �Լ�ģ������״̬   ���һ�� x y



public class rotk {
	
	static final int DUMMY = -1;
	static final int LESS = 0;
	static final int BLACK = 1;  //x
	static final int WHITE = 2;  // o
	static final int[]  dir_inc= {1, -1, 8, -8, 9, -9, 10, -10};  //
	 //  ����λ���� �ж��ĸ�λ��Ҫ�Ǹ�λ�õ��ж�
	static final int dir_mask[] =      
		{
		0,0,0,0,0,0,0,0,0,
		0,81,81,87,87,87,87,22,22,
		0,81,81,87,87,87,87,22,22,
		0,121,121,255,255,255,255,182,182,
		0,121,121,255,255,255,255,182,182,
		0,121,121,255,255,255,255,182,182,
		0,121,121,255,255,255,255,182,182,
		0,41,41,171,171,171,171,162,162,
		0,41,41,171,171,171,171,162,162,
		0,0,0,0,0,0,0,0,0,0
		};
	
	
	static final int ori_state[] = 
		{
			-1,-1,-1,-1,-1,-1,-1,-1,-1,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
		};

	
	
	public static boolean isGameOver(int[] state ) {
		
		boolean isGamen = true;

			for(int y = 10; y < 81; y++ ) {
				if(state[y] == LESS) {                   // ��ʵ�������ж�ʤ��ʱ��˳���ж�˭ʤ��
					return !isGamen;
				}
		}
			System.out.println("��Ϸ������");
			
		return isGamen;   //  ������������� ���洦��һ��
	}
	
	//  square(row, col) = board[10+col+row*9]   0��ʼ��
	//  ʹ����������¼���õĿ�λ��
	
	
	public static LinkedList<Integer> avi_node( int[] state, int player_id){  //����һ�������µĵ�ĺϼ�
		
		
		LinkedList<Integer> avi_node = new LinkedList<Integer>();
		
		for(int i = 10; i < 81; i++ ) {
			
			if ( (state[i] == LESS) && (canFilps(state, i, player_id)) ) {
				System.out.println(i + "������������");
				avi_node.add(i);
			}
		}
			
		
		return avi_node;
	}
	
	public static boolean canFlips_SingleDir(int[] state, int pos, int dir, int player_id) {  
		
		int opp_player_id = ( (player_id == BLACK) ? WHITE : BLACK);
		
		int pt = pos + dir;
		if(state[pt] == opp_player_id) {
			
			while (state[pt] == opp_player_id) {
				pt += dir;
			}
			
			return ((state[pt] == player_id) ? true : false);
		}
		
		return false;
	}
	
	public static void canFlips_SingleDir_Update(int[] state, int pos, int dir, int player_id) {  
		
		int opp_player_id = ((player_id == BLACK) ? WHITE : BLACK);
		
		int pt = pos + dir;
		LinkedList<Integer> update_Cell = new LinkedList<Integer>();
		
		if(state[pt] == opp_player_id) {
			update_Cell.add(pt);
			while (state[pt] == opp_player_id) {
				update_Cell.add(pt);
				pt += dir;
			}
			
			if(state[pt] == player_id) {
				 
				 for(Iterator<Integer> iterator = update_Cell.iterator(); iterator.hasNext();) {
					 state[iterator.next()] = player_id;
				 }
			}
		}
		
		
		 
	}
	
	public static boolean canFilps(int[] state, int pos, int player_id) {
		
		for (int i = 0; i < 8; i++) {
			
			int mask = 0b00000001 << i;
			if( (dir_mask[pos] & mask) != 0 ) {   
				//System.out.println(i + "�����ǿ��Ե�");
				if( canFlips_SingleDir(state, pos, dir_inc[i], player_id)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void canFilps_Update(int[] state, int pos, int player_id) {
		
		for (int i = 0; i < 8; i++) {
			
			int mask = 0b00000001 << i;
			if( (dir_mask[pos] & mask) != 0 ) {    
				
				 canFlips_SingleDir_Update(state, pos, dir_inc[i], player_id);		
			}
		}

	}
	
	public static void update_State(int[] state, int pos, int player_id) {  // ���µ�ǰ���̾���
		
		state[pos] = player_id;
		canFilps_Update(state, pos, player_id);
	}
	
	public static void show_Panel(int[] state) {
		
		int lo = 10;
		int hi = 17;
		
		for(int i = 0; i < 81; i++ ) {
			
			if( (i >= lo) && (i <= hi)   ) {
			
				//System.out.print( (state[i] == BLACK) ? 'o' : 'x');
				
				if (state[i] == BLACK) {
					System.out.print('x');
				}else if (state[i] == WHITE) {
					System.out.print('o');
				}else {
					System.out.print('~');
				}
				
				if (i == hi) {
					System.out.println(" ");
					lo = lo + 9;
					hi = hi + 9;
				}
			}
		}
		
	}
	
	
	
	
	
	public static void main(String[] args) {
	
		
		
		int[] state = ori_state;
		state[40] = WHITE;
		state[50] = WHITE;
		state[41] = BLACK;
		state[49] = BLACK;
		
		Boolean currentPlayer = true;  
		int player_id = BLACK;
		
		LinkedList<Integer> avi_node = new LinkedList<Integer>();
		
		System.out.println("��ʼ�����˰�");
		//show_Panel(state);
		
		
		while ( !isGameOver(state) ) {
			
			
			if (currentPlayer) {
				player_id = BLACK;
			}else {
				player_id = WHITE;
			}
			
			avi_node = avi_node(state, player_id );
			
			if (avi_node == null) {  // ���µ㼯Ϊ�� 
				currentPlayer = !currentPlayer;    //�������ӵĻغ�Ҫ�����µ�
				System.out.println("����غ�û���µ�λ�����ֵ�");
				continue;                          // ��������غ� 
			}
			
			
			do {
				
				
				System.out.println(" Gamer" + player_id + " �µĻغϿ�ʼ�˰���");
				show_Panel(state);
				
				System.out.println("��������λ�� x y ");
				Scanner in = new Scanner(System.in);
				int x = in.nextInt();
				int y = in.nextInt();
				int pos  = 10 + x + y * 9;
				
				if (avi_node.contains(pos)) {   
					// �����������ɵ�λ��
					System.out.println("�������� x y"+ x + y + "λ��");
					
					update_State(state, pos, player_id);     // ��������״̬
					
					show_Panel(state);
					
					currentPlayer = !currentPlayer; //��ȷ����֮����������ת��  
					break;
				}else {
					System.out.println("���µ�λ�ò������ӵģ���ѡ��λ�ð�");
					
					for (Iterator iterator = avi_node.iterator(); iterator.hasNext();) {
						int integer = (int) iterator.next();
						
						int col = integer ;
						int row = integer ;
						
						//System.out.println("�����µ�λ�����⼸�� x" + col + " y " + y );
						
						System.out.println("�����µ�λ�����⼸�� x" + integer );
					}
					
					
				}
				
			} while (true);
		
		
		}
	
	}
	
	
	
	
	
	
}

