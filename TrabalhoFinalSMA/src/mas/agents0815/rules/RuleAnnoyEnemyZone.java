package mas.agents0815.rules;

import java.util.Collection;
import java.util.LinkedList;

import mas.agents0815.Const;
import mas.agents0815.InternalAction;
import mas.agents0815.SubsumptionAgent;
import massim.javaagents.agents2011.Mars2011Util;
import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import eis.iilang.Action;
import eis.iilang.Percept;

public class RuleAnnoyEnemyZone extends Rule{

	public boolean fire(Collection<Percept> percepts, Collection<LogicBelief> beliefs,Collection<LogicGoal> goals, SubsumptionAgent agent){
		
		if (agent.getRussianCounter()>agent.getZoneStart()){
			setAction(new InternalAction(Const.ANNOYENEMYZONE));
			return true;
		}
		return false;	
	
		}
}//class
