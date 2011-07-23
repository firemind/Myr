import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


public class ActionEngine {
	
	   private List<Action> actions = Collections.synchronizedList(new ArrayList<Action>());
	   
	   public synchronized Action addAction(Integer val){
		   Action na = new Action(val);
		   for ( Action act : actions){
			   if(act.getValue().equals(na.getValue())){
				   //System.out.println("Action already known");
				   return act;
			   }
		   }
		   System.out.println("Learning new Action");
		   this.actions.add(na);
		   return na;
	   }
	   
	   public Action getAction(int index){
		   return this.actions.get(index);
	   }
	   
	   public int getActionId(Action act){
		   return this.actions.indexOf(act);
	   }
}
