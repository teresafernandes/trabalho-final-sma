package mas.agents0815.rules;

import java.util.Collection;

import mas.agents0815.Const;
import mas.agents0815.InternalAction;
import mas.agents0815.SubsumptionAgent;
import massim.javaagents.agents2011.Mars2011Util;
import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import eis.iilang.Percept;

public class RuleEvadeSaboteur extends Rule{

	@Override
	public boolean fire(Collection<Percept> percepts,
			Collection<LogicBelief> beliefs, Collection<LogicGoal> goals,
			SubsumptionAgent agent) {
		
		//fires if there is an enemy saboteur at my vertex
		
		if(attackerAtMyNode(agent)){
			//do I have enough energy?
			if (agent.getMyEnergy()<9){
				 setAction(Mars2011Util.rechargeAction());
				return true;						
			 }else{
				 setAction(new InternalAction(Const.RANDOMWALK));
				 return true;
			 }
		}
		return false;
	}
	
	private boolean attackerAtMyNode(SubsumptionAgent agent){
		boolean result = false;
		for (LogicBelief b : agent.getAllBeliefs(Const.VISIBLEENEMY)){
			//vis enemy is at my node?
			if(b.getParameters().get(1).equals(agent.getMyPos())){
			//vis enemy is an attacker?
				if(b.getParameters().get(0).contains(new Integer(5).toString())||b.getParameters().get(0).contains(new Integer(6).toString())){
					//is the enemy enabled?
					if(b.getParameters().get(2).equals("normal"))
						result = true;
				}
			}
		}
		return result;
	}

}
