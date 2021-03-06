import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SettingEngine {
	
   public List<Setting> settings = Collections.synchronizedList(new ArrayList<Setting>());
   
   public synchronized Setting addSetting(Setting ns){
	   for ( Setting set : settings){
		   if(set.equals(ns)){
			   return set;
		   }
	   }
	   //System.out.println("Learning new Setting");
	   this.settings.add(ns);
	   return ns;
   }
   
   public Setting getSetting(int index){
	   return this.settings.get(index);
   }
   
   public int getSettingId(Setting set){
	   return this.settings.indexOf(set);
   }
   
   public synchronized Setting getMostSimilarSetting(Setting tset){
	   System.out.println("Comparing settings");
	   Setting mostSimilar = null;
	   int similarityScore = 0;
	   for ( Setting set : settings){
		   if(set != tset){
			   int score = tset.similarTo(set);
			   if(score > similarityScore){
				   mostSimilar = set;
				   similarityScore = score;
			   }
		   }
	   }
	   if(mostSimilar == null){
		   //System.out.println("No similar setting found");
	   }else{
		   System.out.println("Found similar setting with a score of "+similarityScore);
	   }
	   return mostSimilar;
   }
}
