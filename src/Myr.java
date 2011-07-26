import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
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
	
	public int learned_move_counter = 0;
	
	private Hashtable<Setting, Action> movesForThisGame;
	
	public Myr(){
		System.out.println("Myr AI initialized");
	}
	
	public void startGame(Game game){
		//this.movesForThisGame =  new Hashtable<Setting, Action>();
		this.game = game;
		this.player_id = game.current_player;
	}
	
	public Integer makeMove(){
		Action bestMove = null;
		Setting set = TTTAI.createSettingFromField(game.field);
		set = this.settingEngine.addSetting(set);
		bestMove = this.getBestMove(set);
		if(bestMove != null){
			Integer score = getLearnedMove(set, bestMove);
			if(score  != null &&  !(score > Myr.UNKNOWN)){
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
		return getLearnedMove(set, bestMove);
	}
	
	public void testMoves(){
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		//System.out.println("Starting "+firstMoves.size()+" Test Moves");

		try {
			for(Move m : firstMoves){
	  			m.start();
	  			m.join();
	  		}
			//synchronized (this){
			//  this.wait(firstMoves.size()*100);
			//}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void learnWin(Setting set, Action act){
	//	this.learnForAllMemorizedMoves(WIN);
	//	System.err.println("Move "+act.getValue());
	//	TTTAI.printSetting(set);
		this.learnScore(set, act, WIN);
	}
	
	public void learnDraw(Setting set, Action act){
		//System.out.println("Learned move leads to draw "+act.getValue());
		this.learnScore(set, act, DRAW);
	}
	
	public void learnLose(Setting set, Action act){
		//System.out.println("Learned move leads to lose "+act.getValue());
		//this.learnForAllMemorizedMoves(LOSE);
		this.learnScore(set, act, LOSE);
	}
	
	
	public synchronized void learnForAllMemorizedMoves(Integer score){
		Enumeration<Setting> keys = movesForThisGame.keys(); 
		while( keys.hasMoreElements() ) {
		  Setting key = (Setting) keys.nextElement();
		  Action val = (Action) movesForThisGame.get(key);
		  if(this.getLearnedMove(key, val) == null || this.getLearnedMove(key, val) < score ){
		    this.learnScore(key, val, score);
		  }
		}
	}
	
	public synchronized void learnScore(Setting set, Action act, Integer score){
		set = this.settingEngine.addSetting(set);
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		learned_move_counter++;
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
			if(bestScore == LOSE && possibleMoves.size() == 9){
				//TTTAI.printSetting(set);
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
			  case 9: m.maxGeneration = 9; break;
			  case 8: m.maxGeneration = 8; break;
			  case 7: m.maxGeneration = 7; break;
			  case 6: m.maxGeneration = 6; break;
			  case 5: m.maxGeneration = 5; break;
			  case 4: m.maxGeneration = 4; break;
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
