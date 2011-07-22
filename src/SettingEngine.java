import java.util.ArrayList;


public class SettingEngine {
   private ArrayList<Setting> settings = new ArrayList<Setting>();
   
   public int addSetting(Setting ns){
	   for ( Setting set : settings){
		   if(set.equals(ns)){
			   System.out.println("Setting already known");
			   return this.settings.indexOf(set);
		   }
	   }
	   System.out.println("Learning new Setting");
	   this.settings.add(ns);
	   return this.settings.indexOf(ns);
   }
   
   public Setting getSetting(int index){
	   return this.settings.get(index);
   }
   
   public int getSettingId(Setting set){
	   return this.settings.indexOf(set);
   }
   
   public int getMostSimilarSetting(Setting tset){
	   Setting mostSimilar = null;
	   int similarityScore = 0;
	   for ( Setting set : settings){
		   int score = tset.similarTo(set);
		   if(score > similarityScore){
			   mostSimilar = set;
			   similarityScore = score;
		   }
	   }
	   if(mostSimilar == null){
		   System.out.println("No similar setting found");
	   }else{
		   System.out.println("Found similar setting with a score of "+similarityScore);
	   }
	   return this.settings.indexOf(mostSimilar);
   }
}
