package mas.agents0815.rules;

import java.util.Collection;

import mas.agents0815.SubsumptionAgent;
import massim.javaagents.agents2011.Mars2011Util;

import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import eis.iilang.Percept;

public class RuleSkip extends Rule {

	@Override
	public boolean fire(Collection<Percept> percepts,
			//better do something than smoking
			Collection<LogicBelief> beliefs, Collection<LogicGoal> goals, SubsumptionAgent agent) {
		setAction(Mars2011Util.rechargeAction());
		return true;
	}
}
