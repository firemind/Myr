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
  
  public Move(Myr myr, Action act, Setting set){;
	  this.act = act;
	  this.myr = myr;
	  this.set = set;

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
  }
  
  	public Integer getResult(){
  		if(this.childMoves.size() > 0){
  			double wins = 0.0;
  			for(Move m : childMoves){
  				if(m.getResult() == null){
  					return null;
  				}else if(m.getResult() == Myr.LOSE){
  					this.score = Myr.LOSE;
  					return this.score;
  				}else if(m.getResult() == Myr.WIN){
  					wins++;
  				}
  			}
  			double ratio = wins / this.childMoves.size();
  			this.score = (int) Math.round(ratio * Myr.WIN);
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
	  if(!exceededGeneration(3)){
		  this.loadInitValues();
		  this.game.makeMove(Integer.valueOf(act.getValue()));
		  if(game.gameEnded()){
			  if(game.checkForWinner(myr.player_id)){
				  score = Myr.WIN;
			  }else if(game.checkForDraw()){
				  score = Myr.DRAW;
			  }else{
				  score = Myr.LOSE;
			  }
		  }else{
			    this.spawnChildren();
				while(this.getResult() == null){
					this.waitOnChildResults();
				}
		  }
	  }else{
		  score = Myr.DRAW;
	  }
	  wakeParent();
	  learnResult();
  }
  
  private void learnResult(){
	  if(this.score == null)
	    System.err.println("Wrong Score null");
	  switch(this.getResult()){
	    case Myr.WIN:  myr.learnWin(set, act);break;
	    case Myr.DRAW:  myr.learnDraw(set, act); break;
	    case Myr.LOSE:  myr.learnLose(set, act); break;
	    default: myr.learnScore(set, act, score); break;
	  }
  }
  
  private void spawnChildren(){
		this.childMoves = myr.calculateMoves(game);
		for(Move m : childMoves){
			m.setParent(this);
			m.start();
		}
  }
  
  public boolean exceededGeneration(Integer gen){
	  Integer count = 0;
	  Move p = this.parent;
	  while(p != null){
		  count++;
		  p = p.parent;
	  }
      return count > gen;
  }
  
  private void wakeParent(){
	  if(this.parent != null)
	    synchronized (this.parent) {
		 	  parent.notify();
		}
  }
}
