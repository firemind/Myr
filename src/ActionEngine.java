import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class ActionEngine {
	
	   private List<Action> actions = Collections.synchronizedList(new ArrayList<Action>());
	   
	   public int addAction(String val){
		   Action na = new Action(val);
		   for ( Action act : actions){
			   if(act.getValue().equals(na.getValue())){
				   //System.out.println("Action already known");
				   return this.actions.indexOf(act);
			   }
		   }
		   System.out.println("Learning new Setting");
		   this.actions.add(na);
		   return this.actions.indexOf(na);
	   }
	   
	   public Action getAction(int index){
		   return this.actions.get(index);
	   }
	   
	   public int getActionId(Action act){
		   return this.actions.indexOf(act);
	   }
}
