import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class Myr {

	static final int WIN = 100;
	static final int DRAW = 0;
	static final int LOSE = -100;
	
	static final int maxThreads = 3; 
	
	
	
	ActionEngine actionEngine = new ActionEngine();
	SettingEngine settingEngine = new SettingEngine();
	
	HashMap<Setting, HashMap<Action, Integer>> learnedMoves = new HashMap<Setting, HashMap<Action, Integer>>();
	
	protected Game game;
	public int player_id = 1;
	
	public Myr(){
		System.out.println("Myr AI initialized");
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	public void testMoves(){
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		System.out.println("Starting "+firstMoves.size()+"Test Moves");
		for(Move m : firstMoves){
  			m.start();
  		}
	}
	
	public void learnWin(Setting set, Action act){
		//System.err.println("Learned move leads to win "+act.getValue());
		/*System.out.println("+-----+");
		System.out.println("|"+set.get("FIELD 1")+"|"+set.get("FIELD 2")+"|"+set.get("FIELD 3")+"|");
		System.out.println("|"+set.get("FIELD 4")+"|"+set.get("FIELD 5")+"|"+set.get("FIELD 6")+"|");
		System.out.println("|"+set.get("FIELD 7")+"|"+set.get("FIELD 8")+"|"+set.get("FIELD 9")+"|");
		System.out.println("+-----+");*/
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
	
	public void learnScore(Setting set, Action act, Integer score){
		set = this.settingEngine.addSetting(set);
		if(!this.learnedMoves.containsKey(set)){
			HashMap<Action, Integer> moves = new HashMap<Action, Integer>();
			this.learnedMoves.put(set, moves);
		}
		//System.out.println("Learned move leads to lose "+act.getValue());
		this.learnedMoves.get(set).put(act, score);
	}
	
	public Action getBestMove(Setting set){
		set = this.settingEngine.addSetting(set);
		HashMap <Action, Integer> possibleMoves = learnedMoves.get(set);
		if(possibleMoves != null){
			System.out.println("Learned "+possibleMoves.size()+" Moves for this Setting");
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
			System.out.println("Best move has a score of "+bestScore);
			return bestMove;
		}else{
			System.out.println("No Moves learned for this setting");
			System.out.println(learnedMoves.size());
			return null;
		}
	}
	
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
		Random generator = new Random();
		Object[] values = pm.values().toArray();
		while(moves.size() < maxThreads){
			Integer am = (Integer) values[generator.nextInt(values.length)];
			Action act = this.actionEngine.addAction(am);
			Integer ms = this.getLearnedMove(set, act);
			if(	ms == null 
				|| ms == 0
			){
				Move m = createMove(set, act);
			    moves.add(m);
			}else{
				//System.out.println("Already learned moves for this Setting");
			}
		}
		return moves;
	}
	
	public Move createMove(Setting set, Action act){
		return new Move(this, act, set);
	}
}
