import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Myr {

	static final int WIN = 100;
	static final int DRAW = 0;
	static final int UNKNOWN = -10;
	static final int LOSE = -100;
	
	static final int maxThreads = 8; 
	
	ActionEngine actionEngine = new ActionEngine();
	SettingEngine settingEngine = new SettingEngine();
	
	HashMap<Setting, HashMap<Action, Integer>> learnedMoves = new HashMap<Setting, HashMap<Action, Integer>>();
	
	protected Game game;
	public int player_id;
	
	
	public Myr(){
		System.out.println("Myr AI initialized");
	}
	
	public void startGame(Game game){
		//this.movesForThisGame =  new Hashtable<Setting, Action>();
		this.game = game;
		this.player_id = game.current_player;
	}
	
	public void makeMove(){
		Action bestMove = null;
		Setting set = TTTAI.createSettingFromField(game.field);
		set = this.settingEngine.addSetting(set);
		bestMove = this.getBestMove(set);
		if(bestMove != null){
			Integer score = getLearnedMove(set, bestMove);
			if(score  != null &&  score < Myr.DRAW){
			  this.testMoves();
			  bestMove = this.getBestMove(set);
			}
		}
		while(bestMove == null ){
			this.testMoves();
			bestMove = this.getBestMove(set);
		}
		//this.movesForThisGame.put(set, bestMove);
		this.game.makeMove(bestMove.getValue());
	}
	
	public void testMoves(){
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		//System.out.println("Starting "+firstMoves.size()+" Test Moves");

		try {
			for(Move m : firstMoves){
	  			m.start();
	  			m.join();
	  		}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public synchronized void learnScore(Setting set, Action act, Integer score){
		set = this.settingEngine.addSetting(set);
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		this.learnedMoves.get(set).put(act, score);
	}
	
	public Action getBestMove(Setting set){
		set = this.settingEngine.addSetting(set);
		HashMap <Action, Integer> possibleMoves = learnedMoves.get(set);
		if(possibleMoves != null){
			//System.out.println("Learned "+possibleMoves.size()+" Moves for this Setting");
			Action bestMove = null;
			int bestScore = -101;
			Iterator<Action> it = possibleMoves.keySet().iterator();
			while(it.hasNext()) {	
				Action key = (Action) it.next();
				Integer val = possibleMoves.get(key);
				if(val > bestScore){
					bestMove = key;
					bestScore = val;
				}
			}
			//System.out.println("Best move "+bestMove.getValue()+" out of "+possibleMoves.size()+" has a score of "+bestScore);
			return bestMove;
		}else{
			System.out.println("No Moves learned for this setting");
			System.out.println("Total Moves learned: "+learnedMoves.size());
			return null;
		}
	}
	
	
	// Returns the score of a learned move
	public Integer getLearnedMove(Setting set, Action act){
		HashMap<Action, Integer> ms = this.learnedMoves.get(set);
		if(ms == null)
			return null;
		return ms.get(act);
	}
	
	
	public ArrayList<Move> calculateMoves(Game game){
		ArrayList<Move> moves = new ArrayList<Move>();
		HashMap<String, Integer> pm = game.possibleMoves();
		Setting set = this.settingEngine.addSetting(TTTAI.createSettingFromField(game.getField()));
		Object[] values =  pm.values().toArray();
		for(int i=0;(i<values.length && moves.size() <= maxThreads ) ;i++){
			Action act = this.actionEngine.addAction((Integer)values[i]);
			Move m = createMove(set, act);
			switch(values.length){
			  case 9: m.maxGeneration = 2; break;
			  case 8: m.maxGeneration = 2; break;
			  case 7: m.maxGeneration = 2; break;
			  case 6: m.maxGeneration = 3; break;
			  case 5: m.maxGeneration = 3; break;
			  case 4: m.maxGeneration = 3; break;
			  case 3: m.maxGeneration = 3; break;
			  case 2: m.maxGeneration = 2; break;
			  case 1: m.maxGeneration = 1; break;
			}
			moves.add(m);
		}
		return moves;
	}
	
	public Move createMove(Setting set, Action act){
		return new Move(this, act, set);
	}
}
