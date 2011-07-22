import java.util.ArrayList;
import java.util.HashMap;



public class Move extends Thread{
	
  public Game game;
  Action act;
  Myr myr;
  Setting set;
  ArrayList<Move> childMoves = new ArrayList<Move>();
  Integer current_player;
  Move parent = null;
  
  private Integer score = null;
  public Move(Myr myr, Action act){;
	  this.act = act;
	  this.myr = myr;
	  this.game = myr.game.clone();
  }
  
  public void setParent( Move p){
	  this.parent = p;
  }
  
  public void loadInitValues(){
	  if(this.parent != null){
		  this.game = parent.game.clone();
	  }else{
		  this.game = myr.game.clone();
	  }
	  this.set = TTTAI.createSettingFromField(game.getField());
  }
  
  	public Integer getResult(){
  		if(this.childMoves.size() > 0){
  			Integer wins = 0;
  			for(Move m : childMoves){
  				if(m.getResult() == null){
  					return null;
  				}else if(m.getResult() == Myr.LOSE){
  					return Myr.LOSE;
  				}else if(m.getResult() == Myr.WIN){
  					wins++;
  				}
  			}
  			if(wins == this.childMoves.size()){
  				return Myr.WIN;
  			}
  		}
  		return this.score;
  	}
  
	public void waitOnChildResults(){
		try{
			synchronized (this) {
			     this.wait(10000);
			}
		}catch (InterruptedException e){
				System.out.println("Interrupted Move Thread");
		}
	}
  
  public void run(){
	  this.loadInitValues();
	  this.game.makeMove(Integer.valueOf(act.getValue()));
	  if(game.gameEnded()){
		  if(game.checkForWinner(myr.player_id)){
			  System.out.println("Thread leads to win");
			  game.printField();
			  score = Myr.WIN;
			  myr.learnWin(set, act);
		  }else if(game.checkForDraw()){
			  System.out.println("Thread leads to draw");
			  score = Myr.DRAW;
			  myr.learnDraw(set, act);
		  }else{
			  System.out.println("Thread leads to lose");
			  score = Myr.LOSE;
			  myr.learnLose(set, act);
		  }
		  wakeParent();
	  }else{
			this.childMoves = myr.calculateMoves(game);
  			for(Move m : childMoves){
  				m.setParent(this);
  				m.start();
  			}
			while(this.getResult() == null){
				this.waitOnChildResults();
			}
			wakeParent();
	  }
  }
  
  private void wakeParent(){
	  if(this.parent != null)
	    synchronized (this.parent) {
		 	  parent.notify();
		}
  }
}
