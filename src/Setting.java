import java.util.HashMap;


public class Setting extends HashMap<String, String>{


	public int similarTo(Setting set){
		int matches = 0;
		for( String key : set.keySet()){
			if(this.containsKey(key)){
				if(this.get(key).equals(set.get(key))){
					matches++;
				}
			}else{
				System.out.println("missing key "+key);
			}
		}
		return ( matches / set.keySet().size()) * 100;
	}
}
