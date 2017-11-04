package yang;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONValue;


public class OthelloState {
	
	 private int playerJustMoved = 1;
	 int[]board = {
			-1,-1,-1,-1,-1,-1,-1,-1,-1,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,2,1,0,0,0,
			-1,0,0,0,1,2,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,0,0,0,0,0,0,0,0,
			-1,-1,-1,-1,-1,-1,-1,-1,-1,-1
		};
	
	//1黑先下 2白后下
	static final int[]  dir_inc= {1, -1, 8, -8, 9, -9, 10, -10};
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
	

	
	public OthelloState Clone() {
		
		OthelloState o = new OthelloState();
		o.playerJustMoved =  playerJustMoved;
		for (int i = 0; i < board.length; i++) {
			o.board[i] = board[i];
		}
		
		return o ;
	}
	
	public LinkedList<Integer> GetMoves() {
		
		LinkedList<Integer> avi_mov = new LinkedList<Integer>();
		//这里寻找可做的点；
		
		for(int i = 10; i < 81; i++ ) {
			if ( (board[i] == 0) && (canFilps(i)) ) {
				//System.out.println(i + "这个点可以下棋");
				avi_mov.add(i);
			}
		}
		return avi_mov;
	}
	
	public void DoMove(int mov) {
		board[mov] = playerJustMoved;
		canFilps_Update(mov);		
		playerJustMoved = 3 - playerJustMoved; //换人

	}
	
	public double GetResult(int playerjm) {
		
		int counterPlayer = 3 - playerjm;
		int cJm = 0;
		int cOc = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == playerjm) {
				cJm++;
			} else if(board[i] == counterPlayer) {
				cOc++;
			}
		}
		
		if (cJm > cOc) {
			return 1.0;
		} else if (cJm < cOc) {
			return 0.0;
		} else {
			return 0.5;
		} 
		
	}

	public boolean canFlips_SingleDir(int pos, int dir) {  
		
		int opp_player  = 3 - playerJustMoved ;		
		int pt = pos + dir;
		if(board[pt] == opp_player) {
			while (board[pt] == opp_player) {
				pt += dir;
			}
			return ((board[pt] == playerJustMoved) ? true : false);
		}
		return false;
	}
	
	public boolean canFilps(int pos) {
		
		for (int i = 0; i < 8; i++) {
			int mask = 0b00000001 << i;
			if( (dir_mask[pos] & mask) != 0 ) {   
				if( canFlips_SingleDir(pos, dir_inc[i])) {				
					return true;					
				}
			}
		}
		return false;
	}
	
	public void canFlips_SingleDir_Update(int pos, int dir) {  
		
		int opp_player_id = ((playerJustMoved == 1) ? 2 : 1);
		
		int pt = pos + dir;
		LinkedList<Integer> update_Cell = new LinkedList<Integer>();
		
		if(board[pt] == opp_player_id) {
			update_Cell.add(pt);
			while (board[pt] == opp_player_id) {
				update_Cell.add(pt);
				pt += dir;
			}
			
			if(board[pt] == playerJustMoved) {				 
				 for(Iterator<Integer> iterator = update_Cell.iterator(); iterator.hasNext();) {
					 board[iterator.next()] = playerJustMoved;
				 }
			}
		}
		
		
		 
	}
	
	public void canFilps_Update(int pos) {
		
		for (int i = 0; i < 8; i++) {
			int mask = 0b00000001 << i;
			if( (dir_mask[pos] & mask) != 0 ) {    
				 canFlips_SingleDir_Update(pos, dir_inc[i]);		
			}
		}
	}
	
	public void show_Panel() {
		
		int lo = 10;
		int hi = 17;		
		for(int i = 0; i < 81; i++ ) {			
			if( (i >= lo) && (i <= hi)   ) {			
				if (board[i] == 1) {
					System.out.print('x');
				}else if (board[i] == 2) {
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
	
	public static int UCT(OthelloState rootstate, int itermax) {
		
		Node rootnode = new Node(rootstate);
		
		for(int i = 0; i < itermax; i++ ) {
			Random r = new Random();
			Node node = rootnode ;
			OthelloState state = rootstate.Clone(); 
		//select
			while ( (node.untriedMoves.size() == 0) && (node.childNodes.size() != 0)) {
				node = node.UCTSelectChild();
				state.DoMove(node.mov);
			}
			
		// expand
			if (node.untriedMoves.size() != 0) {
				int m = node.untriedMoves.get(r.nextInt(node.untriedMoves.size()));
				state.DoMove(m);
				node = node.AddChild(m,state);
			} 
	   //RollOut
			while (state.GetMoves().size() != 0) {           
				state.DoMove( state.GetMoves().get( r.nextInt(state.GetMoves().size()) ));
			} 
	  //backprogation	
			while (node != null ) {
				node.Update(state.GetResult(node.playerJustMoved));
				node = node.parentNode;
			}
		}
		
		for(Node node : rootnode.childNodes ) {			
			System.out.println("[ M : ("+ ((node.mov%9)-1)+","+((node.mov/9)-1)+")"+ " w/v:"+node.wins+ "/" +node.visits + "]" );		
		}
		
		double tmpVisit = rootnode.childNodes.getFirst().visits;
		int tmpMove  = rootnode.childNodes.getFirst().mov;
		for( Node node : rootnode.childNodes ) {
			if( node.visits > tmpVisit ) {
				tmpVisit = node.visits;
				tmpMove = node.mov;
			}
		}
			
		return tmpMove;
	}
	
	public static void UCTPlayGame(OthelloState state) {
		

		int m = 0 ;
		
		while (state.GetMoves().size() != 0  ) {
			 state.show_Panel();			 
			 if (state.playerJustMoved == 1) {
				m = UCT(state, 1000);
			}else {
				m = UCT(state,1000);
			}
			
			System.out.println("best move is x: " + ((m % 9) -1) + " y: " + ((m/9) - 1));
			state.DoMove(m);
		}
		
		if (state.GetResult(state.playerJustMoved) == 1) {
			System.out.println("Player" + state.playerJustMoved + "wins");
		}else if (state.GetResult(state.playerJustMoved) == 2) {
			System.out.println("Player" + (3 - state.playerJustMoved) + "wins");
		}else {
			System.out.println("Nobody win!");
		}
		
	}
	
	static class Node{
		
		double wins = 0;
		double visits = 0;
		int mov;
		Node parentNode;
		LinkedList<Node> childNodes;		
		LinkedList<Integer> untriedMoves;
		public int playerJustMoved;
		
		Node( OthelloState state){
			parentNode = null;
			childNodes = new LinkedList<Node>();
			untriedMoves = state.GetMoves();
			playerJustMoved = state.playerJustMoved;			
		}
		Node( int move, Node parent,OthelloState state){
			mov = move;
			parentNode = parent;
			childNodes = new LinkedList<Node>();
			wins = 0;
			visits = 0;	
			untriedMoves = state.GetMoves();			
			playerJustMoved = state.playerJustMoved;
		}
		
		
		public Node UCTSelectChild() {
			
			Random random = new Random();
			Node s = childNodes.get(random.nextInt(childNodes.size()));
			double UCB = s.wins / s.visits + Math.sqrt(2 * Math.log(this.visits)/s.visits);
			
			Node node;
			for (Iterator<Node> iterator = childNodes.iterator(); iterator.hasNext();) {	
				node = iterator.next();
				if (UCB < (node.wins / node.visits + Math.sqrt(2 * Math.log(this.visits)/node.visits))) {
					s = node;
					UCB = node.wins / node.visits + Math.sqrt(2 * Math.log(this.visits)/node.visits) ;
				}
			}
			return s;
		}

		public Node AddChild(int mov, OthelloState state) {
			// TODO Auto-generated method stub
			Node node = new Node(mov,this,state);
			this.untriedMoves.remove((Integer)mov);			
			this.childNodes.add(node);
			return node;
		}

		public void Update(double result) {
			// TODO Auto-generated method stub
			this.visits +=1;
			this.wins += result;
		}		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String input = new Scanner(System.in).nextLine();
		Map<String, List> inputJSON = (Map) JSONValue.parse(input);
/*			// 下面的 TYPE 为单条 response / request 类型，有较大可能为 Map<String, Long> 或 String
		List<TYPE> requests = inputJSON.get("requests");
		List<TYPE> responses = inputJSON.get("responses");
		//要先还原
*/		
		
		
		OthelloState state = new OthelloState();
		UCTPlayGame(state);
		
		
		
		
/*		// 这边运算得出一个 output，注意类型也为 TYPE
		Map<String, TYPE> outputJSON = new HashMap();
		outputJSON.put("response", output);
		System.out.print(JSONValue.toJSONString(outputJSON));
*/		
	}

}
