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
  
  public Move(Myr myr, Action act, Setting set, Game game){;
	  this.act = act;
	  this.myr = myr;
	  this.set = set;
	  this.game = game.clone();
  }
  
  public void setParent( Move p){
	  this.parent = p;
  }
  
  public void loadInitValues(){
	  set.my_move = (this.game.current_player == this.myr.player_id);

  }
  
  	
  
  public void run(){
	  if( exceededGeneration(this.maxGeneration)){
  		  set.paused_game = this.game;
		  this.score = Myr.UNKNOWN;
	  }else{
		  this.loadInitValues();
		  this.game.makeMove(act.getValue());
		  Setting outcome = myr.learnSetting(game);
		  if(game.gameEnded()){
			  outcome.terminal = true;
			  if(game.winner == Game.DRAW){
				  this.score = 0;
			  }else{
				  this.score = 1;
			  }
			  /*
			  if(game.winner == myr.player_id){
				  this.score = Myr.WIN;
			  }else if(game.winner == Game.DRAW){
				  this.score = Myr.DRAW;
			  }else{
				  this.score = Myr.LOSE;
			  }*/
			  outcome.setScore(this.score);
		  }else{
			    this.spawnChildren();
		  }
		  set.learnOutcome(act, outcome);
	  }
	  return;
  }
  
  private void spawnChildren(){
		this.childMoves = myr.calculateMoves(game);
		try{
			for(Move m : childMoves){
				m.setParent(this);
				m.start();
			}
			for(Move m : childMoves){
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
