import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class Myr {

	static final int WIN = 100;
	static final int DRAW = 0;
	static final int UNKNOWN = 10;
	static final int LOSE = -100;
	
	static final int maxThreads = 8; 
	
	ActionEngine actionEngine = new ActionEngine();
	SettingEngine settingEngine = new SettingEngine();
	
	HashMap<Setting, HashMap<Action, Integer>> learnedMoves = new HashMap<Setting, HashMap<Action, Integer>>();
	
	protected Game game;
	public int player_id = Game.PLAYER_O;
	
	private Hashtable<Setting, Action> movesForThisGame;
	
	public Myr(){
		System.out.println("Myr AI initialized");
	}
	
	public void startGame(Game game){
		this.movesForThisGame =  new Hashtable<Setting, Action>();
		this.game = game;
	}
	
	public Integer makeMove(){
		Action bestMove = null;
		Setting set = TTTAI.createSettingFromField(game.field);
		set = this.settingEngine.addSetting(set);
		bestMove = this.getBestMove(set);
		if(bestMove != null && bestMove.getValue() == Myr.UNKNOWN){
			//System.out.println("Thinking of something better...");
			this.testMoves();
			bestMove = this.getBestMove(set);
		}
		while(bestMove == null){
			this.testMoves();
			bestMove = this.getBestMove(set);
		}
		this.movesForThisGame.put(set, bestMove);
		this.game.makeMove(bestMove.getValue());
		return getLearnedMove(set, bestMove);
	}
	
	public void testMoves(){
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		//System.out.println("Starting "+firstMoves.size()+" Test Moves");
		for(Move m : firstMoves){
  			m.start();
  		}
		try {
			synchronized (this){
			  this.wait(100);
			}
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
		this.learnedMoves.get(set).put(act, score);
	}
	
	public Action getBestMove(Setting set){
		set = this.settingEngine.addSetting(set);
		HashMap <Action, Integer> possibleMoves = learnedMoves.get(set);
		if(possibleMoves != null){
			//System.out.println("Learned "+possibleMoves.size()+" Moves for this Setting");
			Action bestMove = null;
			Integer bestScore = -101;
			Iterator<Action> it = possibleMoves.keySet().iterator();
			while(it.hasNext()) {	
				Action key = (Action) it.next();
				Integer val = possibleMoves.get(key);
				if(val > bestScore){
					bestMove = key;
					bestScore = val;
				}
			}
			return bestMove;
		}else{
			System.out.println("No Moves learned for this setting");
			System.out.println("Total Moves learned: "+learnedMoves.size());
			return null;
		}
	}
	
	public Integer getLearnedMove(Setting set, Action act){
		HashMap<Action, Integer> ms = this.learnedMoves.get(set);
		if(ms == null)
			return null;
		if(ms.get(act) == null || ms.get(act) == UNKNOWN)
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
			moves.add(m);
		}
		return moves;
	}
	
	public Move createMove(Setting set, Action act){
		return new Move(this, act, set);
	}
}
