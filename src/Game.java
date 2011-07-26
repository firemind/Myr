import java.util.HashMap;


public class Game implements Cloneable{
  static final int DRAW = 0;
  static final int PLAYER_O = 1;
  static final int PLAYER_X = 2;
  

  
  public int winner = 3;
  public int current_player;
  public int[] field = new int[9];
 
  public Game(){
	  
  }
  
  public void printField(){
	  System.out.println("+-----+");
	  System.out.println("|"+field[0]+"|"+field[1]+"|"+field[2]+"|");
	  System.out.println("|"+field[3]+"|"+field[4]+"|"+field[5]+"|");
	  System.out.println("|"+field[6]+"|"+field[7]+"|"+field[8]+"|");
	  System.out.println("+-----+");
  }
  
  public void makeMove(int i){
	  if(field[i] == 0){
		  field[i] = current_player;
		  endTurn();
	  }else{
		  System.err.println("Move not allowed "+i);
	  }
  }
  
  public boolean gameEnded(){
	  return winner != 3;
  }
  
  public Game clone(){
	  try {
		Game c =  (Game) super.clone();
		c.field = this.field.clone();
		return c;
	} catch (CloneNotSupportedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
  }
  
  public HashMap<String, Integer> possibleMoves(){
	  HashMap<String, Integer> possibleMoves = new HashMap<String, Integer>();
	  for(int i = 0;i<9;i++){
		  if(field[i] == Game.DRAW){
			  possibleMoves.put("SET FIELD "+i, i);
		  }
	  }
	  return possibleMoves;
  }
  
  
  private void endTurn(){
	  if(checkForWinner(current_player)){
		  winner = current_player;
	  }else if(checkForDraw()){
		  winner = DRAW;
  	  }else if(checkForWinner(opponent())){
  		  winner = opponent();
  	  }else{
	    current_player = opponent();
  	  }
  }
  
  private int opponent(){
	  if(current_player == PLAYER_O){
		  return PLAYER_X;
	  }else{
		  return PLAYER_O;
  	  }
  }
  
	public boolean checkForWinner(int player) {
		
		// 3 in 1 row
		for (int i=0; i<=6; i += 3) {
			if(player == field[i] && 
			   player == field[i+1] &&
			   player == field[i+2])
			  return true;
		}
	
		// 3 in 1 col
		for (int i=0; i<3; i++) {
			if(player == field[i] && 
			   player == field[i+3] &&
			   player == field[i+6])
			  return true;
		}

		// 3 diagonal
		if( player == field[0] && 
			player == field[4] &&
			player == field[8])
			return true;

		if( player == field[2] && 
				player == field[4] &&
				player == field[6])
				return true;
		return false;
	}
	
	public boolean checkForDraw(){
		for(int i=0;i<9;i++){
			if(field[i] == 0)
				return false;
		}
		if(checkForWinner(PLAYER_O))
			return false;
		if(checkForWinner(PLAYER_X))
			return false;
		return true;
	}
	
	public int[] getField(){
		return this.field;		
	}


}

