import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Myr {

	static final int WIN = 100;
	static final int DRAW = -1;
	static final int UNKNOWN = 0;
	static final int LOSE = -100;
	
	static final int maxThreads = 8; 
	
	private ActionEngine actionEngine = new ActionEngine();
	public SettingEngine settingEngine = new SettingEngine();
	
	//HashMap<Setting, HashMap<Action, Integer>> learnedMoves = new HashMap<Setting, HashMap<Action, Integer>>();
	
	protected Game game;
	public int player_id;
	

	
	public Myr(){
		System.out.println("Myr AI initialized");
	}
	
	public void startGame(Game game){
		//this.movesForThisGame =  new Hashtable<Setting, Action>();
		this.game = game;
		this.player_id = game.current_player;
		testMoves();
	}
	
	public void makeMove(){
		Action bestMove = null;
		Setting set = TTTAI.createSettingFromField(game.field);
		set = this.settingEngine.addSetting(set);
		set.startPausedGames(this);
/*		bestMove = this.getBestMove(set);
		if(bestMove != null){
			Integer score = set.getOutcome(bestMove).getScore();
			// Not all Children have been evaluated
			if(score  != null &&  score == Myr.UNKNOWN){

			  bestMove = this.getBestMove(set);
			}
			if(score  != null &&  score == Myr.UNKNOWN){
				  testMoves();
				  bestMove = this.getBestMove(set);
			}
		}*/
		testMoves();
		while(bestMove == null ){
			//System.out.println("No best move yet");
			testMoves();
			bestMove = this.getBestMove(set);
		}
		if(set.getOutcome(bestMove).getScore() > 0)
		  System.out.println("Performing Action with a score of "+set.getOutcome(bestMove).getScore());
		//TTTAI.printSetting(set.getOutcome(bestMove).board);
		//this.movesForThisGame.put(set, bestMove);
		this.game.makeMove(bestMove.getValue());
	}
	
	public void testMoves(){
		//System.out.println("Starting test moves");
		ArrayList<Move> firstMoves = this.calculateMoves(game);
		//System.out.println("Starting "+firstMoves.size()+" Test Moves");
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
	}
	
	
	public Setting learnSetting(Game game){
		return this.settingEngine.addSetting(TTTAI.createSettingFromField(game.field));
	}
	
	public Action getBestMove(Setting set){
		set = this.settingEngine.addSetting(set);
		return set.bestOutcome();
	}
	
	
	
	public ArrayList<Move> calculateMoves(Game game){
		ArrayList<Move> moves = new ArrayList<Move>();
		HashMap<String, Integer> pm = game.possibleMoves();
		Setting set = this.settingEngine.addSetting(TTTAI.createSettingFromField(game.getField()));
		int s=set.getScore(); 
		if(s != Myr.WIN && s != Myr.DRAW ){
			Object[] values =  pm.values().toArray();
			for(int i=0;(i<values.length && moves.size() <= maxThreads ) ;i++){
				Action act = this.actionEngine.addAction((Integer)values[i]);
				Setting outcome = set.getOutcome(act);
				if(outcome == null || outcome.getScore() == Myr.UNKNOWN){
					Move m = createMove(set, act, game);
					switch(values.length){
					  case 9: m.maxGeneration = 0; break;
					  case 8: m.maxGeneration = 2; break;
					  case 7: m.maxGeneration = 2; break;
					  case 6: m.maxGeneration = 3; break;
					  case 5: m.maxGeneration = 3; break;
					  case 4: m.maxGeneration = 4; break;
					  case 3: m.maxGeneration = 3; break;
					  case 2: m.maxGeneration = 2; break;
					  case 1: m.maxGeneration = 2; break;
					}
					moves.add(m);
				}
			}
		}
		return moves;
	}
	
	public Move createMove(Setting set, Action act, Game game){
		return new Move(this, act, set, game);
	}
	
	public static int guess(Setting set){
		int score = 0;
		Integer[] fieldValues = {2,3,2,3,4,3,2,3,2};
		
		for(int i=0;i<fieldValues.length ;i++){
			Integer fieldValue = Integer.valueOf(set.board.get("FIELD "+String.valueOf(i+1)));
			if(fieldValue == set.current_player){
				score += fieldValues[i]; // The Opponent has the field
			}else if(fieldValue != Game.DRAW){
				score -= fieldValues[i]; // I have the field
			}			
		}
		return score;
	}
	
}
