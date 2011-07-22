import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Myr {

	static final int WIN = 1;
	static final int LOSE = 0;
	static final int DRAW = -1;
	
	ActionEngine actionEngine = new ActionEngine();
	SettingEngine settingEngine = new SettingEngine();
	
	// HashMap<setting_id, HashMap<Action, score>
	HashMap<Setting, HashMap<Action, Integer>> learnedMoves = new HashMap<Setting, HashMap<Action, Integer>>();
	
	protected Game game;
	public int player_id = 1;
	
	public Myr(Game game){
		System.out.println("Myr AI initialized");
		this.game = game;
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		for(Move m : firstMoves){
  			m.start();
  		}
	}
	
	public void learnWin(Setting set, Action act){
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		System.out.println("Learned move leads to win "+act.getValue());
		this.learnedMoves.get(set).put(act, WIN);
	}
	
	public void learnDraw(Setting set, Action act){
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		System.out.println("Learned move leads to draw "+act.getValue());
		this.learnedMoves.get(set).put(act, DRAW);
	}
	
	public void learnLose(Setting set, Action act){
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		System.out.println("Learned move leads to lose "+act.getValue());
		this.learnedMoves.get(set).put(act, LOSE);
	}
	
	public Action getBestMove(Setting set){
		HashMap <Action, Integer> possibleMoves = learnedMoves.get(set);
		System.out.println("Learned "+possibleMoves.size()+" Moves for this Setting");
		Action bestMove = null;
		Integer bestScore = 0;
		Iterator<Action> it = possibleMoves.keySet().iterator();
		while(it.hasNext()) {	
			Action key = (Action) it.next();
			Integer val = possibleMoves.get(key);
			if(val > bestScore){
				bestMove = key;
				bestScore = val;
			}
		}
		System.out.println("Best move has a score of "+bestScore);
		return bestMove;	
	}
	
	public ArrayList<Move> calculateMoves(Game game){
		ArrayList<Move> moves = new ArrayList<Move>();
		HashMap<String, Integer> pm = game.possibleMoves();
		for( String key : pm.keySet()){
			Action act = this.actionEngine.getAction(this.actionEngine.addAction(String.valueOf(pm.get(key))));
			Move m = createMove(act);
		    moves.add(m);
		}
		return moves;
	}
	
	public Move createMove(Action act){
		return new Move(this, act);
	}
}
