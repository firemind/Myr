
public class TTTAI {
	
	public int myr_wins = 0;
	public int tttai_wins = 0;
	public int draws = 0;
	
	
	public static void main(String[] args){
		Myr myr = new Myr();
		TTTAI tttai = new TTTAI();
		for(int i = 0; i < 800000; i++){
			if(i % 10 == 0){
			  System.out.println("Game Nr "+i);
			  printStats(tttai, myr);
			}
		  playGame(myr, tttai);
		}
	    printStats(tttai, myr);
	}
	
	public static void printStats(TTTAI tttai, Myr myr){
		  System.out.println("Myr Wins "+tttai.myr_wins);
		  System.out.println("TTTAI Wins "+tttai.tttai_wins);
		  System.out.println("Draws "+tttai.draws);
		  System.out.println("Total Settings learned "+myr.learnedMoves.size());
	}
	
	public static void playGame(Myr myr, TTTAI tttai){
		Game game = new Game();
		game.current_player = Game.PLAYER_O;
		myr.startGame(game);
		while(!myr.game.gameEnded()){
//			System.out.println("Myr's turn");
			myr.makeMove();
			//game.printField();
			if(!myr.game.gameEnded()){
	//			System.out.println("TTTAI's turn");
				game.makeMove(besterZug(game));
			}
		}
		if(game.winner == myr.player_id){
			tttai.myr_wins++;
		}else if(game.winner == Game.DRAW){
			tttai.draws++;
		}else if(game.winner == Game.PLAYER_X){
			tttai.tttai_wins++;
		}
	}
	
	public static Setting createSettingFromField(int[] field){
		Setting set = new Setting();
		set.put("FIELD 1", String.valueOf(field[0]));
		set.put("FIELD 2", String.valueOf(field[1]));
		set.put("FIELD 3", String.valueOf(field[2]));
		set.put("FIELD 4", String.valueOf(field[3]));
		set.put("FIELD 5", String.valueOf(field[4]));
		set.put("FIELD 6", String.valueOf(field[5]));
		set.put("FIELD 7", String.valueOf(field[6]));
		set.put("FIELD 8", String.valueOf(field[7]));
		set.put("FIELD 9", String.valueOf(field[8]));
		return set;
	}
	
	public static int siegInEinemZug(Game game, int aktuellerSpieler) {
		game = game.clone();
		// gibt Zug (Feld) zum Gewinn zurueck oder -1 falls kein Sieg in 1 Zug moeglich
		for (int i =0; i <9; i++) {	
			if(game.field[i] == 0) {
				// Feld ist leer, mache eine Zug darauf und teste, ob gewonnen 
				game.field[i] = aktuellerSpieler;
				if( game.checkForWinner(aktuellerSpieler)) {
					game.field[i] = 0; 
					return i;
				}
				game.field[i] = 0;
			}
		}
		return -1;
	}
	
	public static int besterZug(Game game) {
		int feld;
		// Teste, ob Computer in 1 Zug gewinnen kann
		if((feld = siegInEinemZug(game, game.current_player)) != -1)
			return feld;
		
		// Teste, ob Gegner in 1 Zug gewinnen kann (und ziehe ggf. dorthin)
		if((feld = siegInEinemZug(game, Game.PLAYER_O)) != -1)
			return feld;

		// Computer kann in 1 Zug nicht gewinnen
		// mach einen Zufallszug
		do	{ 
			feld = (int)(Math.random()*9); // 0.0 <= Math.random() < 1		
		}  while(game.field[feld] != 0);
		
		return feld;
	}
	
	public static void printSetting(Setting set){
		System.out.println("+-----+");
		System.out.println("|"+set.get("FIELD 1")+"|"+set.get("FIELD 2")+"|"+set.get("FIELD 3")+"|");
		System.out.println("|"+set.get("FIELD 4")+"|"+set.get("FIELD 5")+"|"+set.get("FIELD 6")+"|");
		System.out.println("|"+set.get("FIELD 7")+"|"+set.get("FIELD 8")+"|"+set.get("FIELD 9")+"|");
		System.out.println("+-----+");
	}
}
