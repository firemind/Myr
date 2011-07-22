
public class TTTAI {
	public static void main(String[] args){
		Game game = new Game();
		Myr myr = new Myr(game);
		
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
}
