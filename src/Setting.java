import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Setting {

	
	public HashMap<String, String> board = new HashMap<String, String>();
	private Integer score = Myr.UNKNOWN;
	
	public Game paused_game;
	public int current_player;
	public boolean terminal = false;
	private HashMap<Action, Setting> outcomes = new HashMap<Action, Setting>();

	public Setting getOutcome(Action act){
		return outcomes.get(act);
	}
	
	public Integer getScore(){
		return this.minmax();
	}
	
	public void learnOutcome(Action act, Setting set){
		this.outcomes.put(act, set);
	}
	
	public void setScore(Integer ns){
		this.score = ns;
	}
	
	public Action bestOutcome(){
		Action bestMove = null;
		int bestScore = -101;
		Random random = new Random();
		Iterator<Action> it = outcomes.keySet().iterator();
		while(it.hasNext()) {	
			Action key = (Action) it.next();
			Setting val = outcomes.get(key);
			int newscore = -val.getScore();
			if(newscore > bestScore){
				bestMove = key;
				bestScore = newscore;
			}else if(newscore == bestScore && random.nextBoolean()){
				bestMove = key;
			}
		}
		return bestMove;
	}
	
	public void startPausedGames(Myr myr){
		for(Setting outcome: outcomes.values()){
			outcome.startPausedGames(myr);
		}
		if(this.paused_game != null){
			//System.out.println("Starting paused game score is "+this.getScore());
			ArrayList<Move> firstMoves = myr.calculateMoves(this.paused_game);
			try {
				for(Move m : firstMoves){
		  			m.start();
		  		}
				for(Move m : firstMoves){
		  			m.join();
		  		}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.paused_game = null;
		}
	}
	
	public boolean equals(Setting other){
		return this.board.equals(other.board);
	}
	
	public int similarTo(Setting set){
		int matches = 0;
		for( String key : set.board.keySet()){
			if(this.board.containsKey(key)){
				if(this.board.get(key).equals(set.board.get(key))){
					matches++;
				}
			}else{
				System.out.println("missing key "+key);
			}
		}
		return ( matches / set.board.keySet().size()) * 100;
	}
	
	// Returns the Score the Field has from the perspective of the current player
	public int minmax(){
		if(this.terminal)
			return this.score;
	    if (this.outcomes.size() == 0)
	        return Myr.guess(this);
	    int v = -100000000;
	    for (Setting outcome : outcomes.values())
	        v = Math.max(v, -outcome.minmax());
	    return v;
	}
}
