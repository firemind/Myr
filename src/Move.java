import java.util.ArrayList;
import java.util.HashMap;



public class Move extends Thread{
	
  public static Integer maxGeneration = 3;
  public Game game;
  Action act;
  Myr myr;
  Setting set;
  ArrayList<Move> childMoves = new ArrayList<Move>();
  Integer current_player;
  Move parent = null;
  private Integer score = null;
  private boolean my_move;
  
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
	  this.my_move = this.game.current_player == this.myr.player_id;
  }
  
  	public Integer getResult(){
  		if(this.childMoves.size() > 0){
  			// If it's my move
  			if(this.my_move){
	  			int wins = 0;
	  			for(Move m : childMoves){
	  				if(m.getResult() == null){
	  					return null;
	  				}else if(m.getResult() == Myr.LOSE){
	  					// Opponent has a move with which it could win
	  					// => this move leads to a lost game
	  					this.score = Myr.LOSE;
	  					return this.score;
	  				}else if(m.getResult() == Myr.WIN){
	  					wins++;
	  				}else if(m.getResult() == Myr.UNKNOWN){
	  					// Outcome of child is is unknown
	  					// => Outcome of this move is unknown
	  					this.score = Myr.UNKNOWN;
	  				}else if(m.getResult() == Myr.DRAW){
	  					if(this.score == null){
	  					  this.score = Myr.DRAW;
	  					}
	  				}
	  			}
	  			// if all moves a opponent could take after this lead to a win
	  			// => the move is a guaranteed win
	  			if(wins == childMoves.size()){
	  				this.score = Myr.WIN;
	  			}
/*	  			if(this.score != null && this.score == Myr.UNKNOWN && wins > 0){
	  				Float ratio = (float) wins;
	  				ratio = ratio / childMoves.size();
	  				int result = (int) Math.round(ratio * Myr.WIN);
	  				//System.out.println("Score with wins "+wins+" ratio "+ratio+":"+result);
	  				this.score = result;
	  			}*/
  			}else{
  				// It's a move by an opponent
	  			int loses = 0;
	  			for(Move m : childMoves){
	  				if(m.getResult() == null){
	  					return null;
	  				}else if(m.getResult() == Myr.WIN){
	  					// If one of the child moves (my moves) give me a chance to win
	  					// => it's a guaranteed win
	  					this.score = Myr.WIN;
	  					return this.score;
	  				}else if(m.getResult() == Myr.LOSE){
	  					loses++;
	  				}else if(m.getResult() == Myr.UNKNOWN){
	  					// Outcome of child is is unknown
	  					// => Outcome of this move is unknown
	  					this.score = Myr.UNKNOWN;
	  				}else if(m.getResult() == Myr.DRAW){
	  					if(this.score == null){
		  				  this.score = Myr.DRAW;
		  				}
		  			}
	  			}
	  			// if all of the child moves (my moves) cause me to lose
	  			// => the move is a guaranteed lose
	  			if(loses == childMoves.size()){
	  				this.score = Myr.LOSE;
	  			}
/*	  			if(this.score != null && this.score == Myr.UNKNOWN && loses > 0){
	  				Float ratio = (float) loses;
	  				ratio = ratio / childMoves.size();
	  				int result = (int) Math.round(ratio * Myr.LOSE);
	  				//System.out.println("Score with loses "+loses+" ratio "+ratio+":"+result);
	  				this.score = result;
	  			}*/
  			}
  	  		if(this.score == null){
  	  			this.score = Myr.UNKNOWN;
  	  		}
  		}
  	/*	if(this.score != null && this.score == Myr.UNKNOWN){
  			Setting mostSimiliar = myr.settingEngine.getMostSimilarSetting(set);
  			if(mostSimiliar != null){
  				this.score = myr.getLearnedMove(mostSimiliar, act);
  			}
  		}*/
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
	  this.score = this.myr.getLearnedMove(set, act);
	  if(this.score != null && this.score != Myr.UNKNOWN){
		  //System.out.println("Move already known: "+this.score);
  	  }else if( exceededGeneration(Move.maxGeneration)){
		  this.score = Myr.UNKNOWN;
	  }else{
		  this.loadInitValues();
		  this.game.makeMove(act.getValue());
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
	  }
	  wakeParent();
	  learnResult();
	  return;
  }
  
  private void learnResult(){
	  if(this.score == null){
	    System.err.println("Wrong Score null");
	    return;
	  }
	  switch(this.getResult()){
	    case Myr.WIN:  myr.learnWin(set, act);break;
	    case Myr.DRAW:  myr.learnDraw(set, act); break;
	    case Myr.LOSE:  myr.learnLose(set, act); break;
	    case Myr.UNKNOWN: if(this.parent == null){myr.learnScore(set, act, Myr.UNKNOWN);} break;
	    default: myr.learnScore(set, act, score); break;
	  }
  }
  
  private void spawnChildren(){
		this.childMoves = myr.calculateMoves(game);
		for(Move m : childMoves){
			m.setParent(this);
			m.start();
		}
		if(this.childMoves.size() == 0){
			System.out.println("Don't have any child moves");
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
