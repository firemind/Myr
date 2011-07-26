import java.util.ArrayList;



public class Move extends Thread{
	
  public Integer maxGeneration = null;
  public Game game;
  Action act;
  Myr myr;
  Setting set;
  ArrayList<Move> childMoves = new ArrayList<Move>();
  Move parent = null;
  private Integer score = null;
  private boolean my_move;
  private boolean has_children = false;
  
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
	  this.my_move = (this.game.current_player == this.myr.player_id);

  }
  
  	public Integer getResult(){
  		if(has_children){
  			// If it's my move
  			boolean has_draw_child = false;
  			if(this.my_move){
	  			int wins = 0;
	  			for(Move m : childMoves){
	  				Integer res = m.getResult();
	  				if(res == Myr.LOSE){
	  					// Opponent has a move with which it could win
	  					// => this move leads to a lost game
	  					this.score = Myr.LOSE;
	  					return this.score;
	  				}else if(res == Myr.WIN){
	  					wins++;
	  				}else if(res == Myr.UNKNOWN){
	  					// Outcome of child is is unknown
	  					// => Outcome of this move is unknown
	  					this.score = Myr.UNKNOWN;
	  				}else if(res == Myr.DRAW){
	  					has_draw_child = true;
	  				}
	  			}
	  			if(this.score == null && has_draw_child){
	  				this.score = Myr.DRAW;
	  			}
	  			// if all moves a opponent could take after this lead to a win
	  			// => the move is a guaranteed win
	  			if(wins == childMoves.size()){
	  				this.score = Myr.WIN;
	  				return this.score;
	  			}
  			}else{
  				// It's a move by an opponent
	  			int loses = 0;
	  			for(Move m : childMoves){
	  				Integer res = m.getResult();
	  				if(res == Myr.WIN){
	  					// If one of the child moves (my moves) give me a chance to win
	  					// => it's a guaranteed win
	  					this.score = Myr.WIN;
	  					return this.score;
	  				}else if(res == Myr.LOSE){
	  					loses++;
	  				}else if(res == Myr.UNKNOWN){
	  					// Outcome of child is is unknown
	  					// => Outcome of this move is unknown
	  					this.score = Myr.UNKNOWN;
	  				}else if(res == Myr.DRAW){
	  					has_draw_child = true;
		  			}
	  			}
	  			if(this.score == null && has_draw_child){
	  				this.score = Myr.DRAW;
	  			}
	  			// if all of the child moves (my moves) cause me to lose
	  			// => the move is a guaranteed lose
	  			if(loses == childMoves.size()){
	  				this.score = Myr.LOSE;
	  				return this.score;
	  			}
  			}
  	  		if(this.score == null){
  	  			this.score = Myr.UNKNOWN;
  	  		}
  		}
  		return this.score;
  	}
  
  public void run(){
	  Integer s = this.myr.getLearnedMove(set, act);
	  if(s != null && s != Myr.UNKNOWN){
		  //System.out.println("Move already known: "+this.score);
		  this.score = s;
  	  }else if( exceededGeneration(this.maxGeneration)){
		  this.score = Myr.UNKNOWN;
	  }else{
		  this.loadInitValues();
		  this.game.makeMove(act.getValue());
		  if(game.gameEnded()){
			  if(game.winner == myr.player_id){
				  this.score = Myr.WIN;
			  }else if(game.winner == Game.DRAW){
				  this.score = Myr.DRAW;
			  }else{
				  this.score = Myr.LOSE;
			  }
		  }else{
			    this.has_children = true;
			    this.spawnChildren();

		  }
		  learnResult();
	  }
	  return;
  }
  
  private void learnResult(){
	  Integer res = this.getResult();
	  if(res == null){
	    System.err.println("Wrong Score null");
	    return;
	  }
	  if(!(res == Myr.UNKNOWN && this.parent != null)){
		  myr.learnScore(set, act, this.score);
	  }
  }
  
  private void spawnChildren(){
		this.childMoves = myr.calculateMoves(game);
		try{
			for(Move m : childMoves){
				m.setParent(this);
				m.start();
				m.join();
			}
		}catch (InterruptedException e){
				System.out.println("Interrupted Move Thread");
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
  
}
