package massim.javaagents.vitorteresa;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Vector;

import apltk.interpreter.data.LogicBelief;
import apltk.interpreter.data.LogicGoal;
import apltk.interpreter.data.Message;
import eis.iilang.Action;
import eis.iilang.Percept;
import massim.javaagents.Agent;

public class RepairerAgent extends Agent {

	int rechargeSteps = 0;

	public RepairerAgent(String name, String team) {
		super(name, team);
	}

	@Override
	public void handlePercept(Percept p) {

	}

	@Override
	public Action step() {

		Action act = null;

		handlePercepts();

		// 1. recarregar
		act = planRecharge();
		if (act != null)
			return act;

		// 2. repara ainda precisa melhorar.

		act = planRepair();
		if (act != null)
			return act;

		// 3. Defender
		// act = planDefender();
		// if ( act != null ) return act;

		act = planBuyBattery();
		if (act != null)
			return act;

		// 4. Andar
		//
		act = planRandomWalk();
		if (act != null)
			return act;

		// 4. buying battery with a certain probability

		// 5. probing if necessary , pode ser implementado somente no explorer
		// ou no agente inspetor
		// act = planProbe();
		// if ( act != null ) return act;

		// 6. surveying if necessary
		// act = planSurvey();
		// if ( act != null ) return act;

		return Mars2011Util.skipAction();
	}

	private void handlePercepts() {

		String position = null;
		Vector<String> neighbors = new Vector<String>();

		// check percepts
		Collection<Percept> percepts = getAllPercepts();
		// if ( gatherSpecimens ) processSpecimens(percepts);
		removeBeliefs("visibleEntity");
		removeBeliefs("visibleEdge");
		for (Percept p : percepts) {
			if (p.getName().equals("step")) {
				println(p);
			} else if (p.getName().equals("visibleEntity")) {
				LogicBelief b = Mars2011Util.perceptToBelief(p);
				if (containsBelief(b) == false) {
					addBelief(b);
				} else {
				}
			} else if (p.getName().equals("visibleEdge")) {
				LogicBelief b = Mars2011Util.perceptToBelief(p);
				if (containsBelief(b) == false) {
					addBelief(b);
				} else {
				}
			} else if (p.getName().equals("probedVertex")) {
				LogicBelief b = Mars2011Util.perceptToBelief(p);
				if (containsBelief(b) == false) {
					println("I perceive the value of a vertex that I have not known before");
					addBelief(b);
					broadcastBelief(b);
				} else {
					// println("I already knew " + b);
				}
			} else if (p.getName().equals("surveyedEdge")) {
				LogicBelief b = Mars2011Util.perceptToBelief(p);
				if (containsBelief(b) == false) {
					println("I perceive the weight of an edge that I have not known before");
					addBelief(b);
					broadcastBelief(b);
				} else {
					// println("I already knew " + b);
				}
			} else if (p.getName().equals("health")) {
				Integer health = new Integer(p.getParameters().get(0)
						.toString());
				println("my health is " + health);
				if (health.intValue() == 0) {
					println("my health is zero. asking for help");
					broadcastBelief(new LogicBelief("iAmDisabled"));

				}
			} else if (p.getName().equals("position")) {
				position = p.getParameters().get(0).toString();
				removeBeliefs("position");
				addBelief(new LogicBelief("position", position));

			} else if (p.getName().equals("energy")) {
				Integer energy = new Integer(p.getParameters().get(0)
						.toString());
				removeBeliefs("energy");
				addBelief(new LogicBelief("energy", energy.toString()));
			} else if (p.getName().equals("maxEnergy")) {
				Integer maxEnergy = new Integer(p.getParameters().get(0)
						.toString());
				removeBeliefs("maxEnergy");
				addBelief(new LogicBelief("maxEnergy", maxEnergy.toString()));
			} else if (p.getName().equals("money")) {
				Integer money = new Integer(p.getParameters().get(0).toString());
				removeBeliefs("money");
				addBelief(new LogicBelief("money", money.toString()));
			} else if (p.getName().equals("achievement")) {
				println("reached achievement " + p);
			}
		}

		// again for checking neighbors
		this.removeBeliefs("neighbor");
		for (Percept p : percepts) {
			if (p.getName().equals("visibleEdge")) {
				String vertex1 = p.getParameters().get(0).toString();
				String vertex2 = p.getParameters().get(1).toString();
				if (vertex1.equals(position))
					addBelief(new LogicBelief("neighbor", vertex2));
				if (vertex2.equals(position))
					addBelief(new LogicBelief("neighbor", vertex1));
			}
		}
	}

	private Action planRepair() { /*
								 * if ( rechargeSteps > 0 ) { rechargeSteps --;
								 * println("recharging..."); return
								 * Mars2011Util.skipAction(); //return
								 * Mars2011Util.rechargeAction(); }
								 */

		Collection<Message> messages = getMessages();
		Vector<String> needyAgents = new Vector<String>();
		for (Message msg : messages) {
			needyAgents.add(msg.sender);
		}

		/*
		 * if ( needyAgents.size() == 0 ) { println("nothing for me to do");
		 * return Mars2011Util.skipAction(); //goals.add(new
		 * LogicGoal("beAtFullCharge")); //return Mars2011Util.rechargeAction();
		 * }
		 */
		println("some poor souls need my help " + needyAgents);

		Collection<Percept> percepts = getAllPercepts();
		String position = null;
		for (Percept p : percepts) {
			/*
			 * if ( p.getName().equals("lastActionResult") &&
			 * p.getParameters().get(0).toProlog().equals("failed") ) {
			 * println("my previous action has failed. recharging...");
			 * rechargeSteps = 2; return Mars2011Util.skipAction(); }
			 */
			if (p.getName().equals("position")) {
				position = p.getParameters().get(0).toString();
			}
		}

		// a needy one on the same vertex
		for (Percept p : percepts) {
			if (p.getName().equals("visibleEntity")) {
				String ePos = p.getParameters().get(1).toString();
				String eName = p.getParameters().get(0).toString();
				if (ePos.equals(position) && needyAgents.contains(eName)) {
					println("I am going to repair " + eName);
					return Mars2011Util.repairAction(eName);

				}
			}
		}

		// maybe on an adjacent vertex?
		Vector<String> neighbors = new Vector<String>();
		for (Percept p : percepts) {
			if (p.getName().equals("visibleEdge")) {
				String vertex1 = p.getParameters().get(0).toString();
				String vertex2 = p.getParameters().get(1).toString();
				if (vertex1.equals(position))
					neighbors.add(vertex2);
				if (vertex2.equals(position))
					neighbors.add(vertex1);
			}
		}
		for (Percept p : percepts) {
			if (p.getName().equals("visibleEntity")) {
				String ePos = p.getParameters().get(1).toString();
				String eName = p.getParameters().get(0).toString();
				if (neighbors.contains(ePos) && needyAgents.contains(eName)) {
					println("I am going to repair " + eName + ". move to "
							+ ePos + " first.");
					return Mars2011Util.gotoAction(ePos);
				}
			}
		}

		// goto neighbors
		Collections.shuffle(neighbors);
		String neighbor = neighbors.firstElement();
		println("I will go to " + neighbor);
		return Mars2011Util.gotoAction(neighbor);

	}

	private Action planRecharge() {

		LinkedList<LogicBelief> beliefs = null;

		beliefs = getAllBeliefs("energy");
		if (beliefs.size() == 0) {
			println("strangely I do not know my energy ISAAC");
			return Mars2011Util.skipAction();
		}
		int energy = new Integer(beliefs.getFirst().getParameters()
				.firstElement()).intValue();

		beliefs = getAllBeliefs("maxEnergy");
		if (beliefs.size() == 0) {
			println("strangely I do not know my maxEnergy DANN");
			return Mars2011Util.skipAction();
		}
		int maxEnergy = new Integer(beliefs.getFirst().getParameters()
				.firstElement()).intValue();

		// if has the goal of being recharged...
		if (goals.contains(new LogicGoal("beAtFullCharge"))) {
			if (maxEnergy == energy) {
				println("I can stop recharging. I am at full charge");
				removeGoals("beAtFullCharge");
				return planRandomWalk();
			} else {
				println("recharging...");
				return Mars2011Util.rechargeAction();
			}
		}
		// go to recharge mode if necessary
		else {
			if (energy < maxEnergy / 3) {
				println("I need to recharge");
				goals.add(new LogicGoal("beAtFullCharge"));
				return Mars2011Util.rechargeAction();
			}
		}

		return null;

	}

	private Action planRandomWalk() {

		LinkedList<LogicBelief> beliefs = getAllBeliefs("neighbor");
		Vector<String> neighbors = new Vector<String>();
		for (LogicBelief b : beliefs) {
			neighbors.add(b.getParameters().firstElement());

		}

		if (neighbors.size() == 0) {
			println("strangely I do not know any neighbors");
			return Mars2011Util.skipAction();

		}

		// goto neighbors
		Collections.shuffle(neighbors);
		String neighbor = neighbors.firstElement();
		println("I will go to " + neighbor);
		return Mars2011Util.gotoAction(neighbor);

	}

	private Action planDefender() {

		// get position
		LinkedList<LogicBelief> beliefs = null;
		beliefs = getAllBeliefs("position");
		if (beliefs.size() == 0) {
			println("strangely I do not know my position");
			return Mars2011Util.skipAction();
		}
		String position = beliefs.getFirst().getParameters().firstElement();

		// if there is an enemy on the current position then attack or defend
		Vector<String> enemies = new Vector<String>();
		beliefs = getAllBeliefs("visibleEntity");
		for (LogicBelief b : beliefs) {
			String name = b.getParameters().get(0);
			String pos = b.getParameters().get(1);
			String team = b.getParameters().get(2);
			if (team.equals(getTeam()))
				continue;
			if (pos.equals(position) == false)
				continue;
			enemies.add(name);
		}
		if (enemies.size() != 0) {
			println("there are " + enemies.size()
					+ " enemies at my current position");
			if (Math.round(Math.random()) % 2 == 0) {

				Collections.shuffle(enemies);
				String enemy = enemies.firstElement();
				println("I will attack " + enemy);
				return Mars2011Util.attackAction(enemy);
				// println("I will parry");
				// return Mars2011Util.parryAction();
			} else {
				// Collections.shuffle(enemies);
				// String enemy = enemies.firstElement();
				// println("I will attack " + enemy);
				// return Mars2011Util.attackAction(enemy);
				println("I will parry");
				return Mars2011Util.parryAction();
			}
		}

		// if there is an enemy on a neighboring vertex to there
		beliefs = getAllBeliefs("neighbor");
		Vector<String> neighbors = new Vector<String>();
		for (LogicBelief b : beliefs) {
			neighbors.add(b.getParameters().firstElement());
		}

		Vector<String> vertices = new Vector<String>();
		beliefs = getAllBeliefs("visibleEntity");
		for (LogicBelief b : beliefs) {
			// String name = b.getParameters().get(0);
			String pos = b.getParameters().get(1);
			String team = b.getParameters().get(2);
			if (team.equals(getTeam()))
				continue;
			if (neighbors.contains(pos) == false)
				continue;
			vertices.add(pos);
		}
		if (vertices.size() != 0) {
			println("there are " + vertices.size()
					+ " adjacent vertices with enemies");
			Collections.shuffle(vertices);
			String vertex = vertices.firstElement();
			println("I will goto " + vertex);
			return Mars2011Util.gotoAction(vertex);
		}

		return null;
	}

	private Action planProbe() {

		LinkedList<LogicBelief> beliefs = null;

		beliefs = getAllBeliefs("position");
		if (beliefs.size() == 0) {
			println("strangely I do not know my position");
			return Mars2011Util.skipAction();
		}
		String position = beliefs.getFirst().getParameters().firstElement();

		// probe current position if not known
		boolean probed = false;
		LinkedList<LogicBelief> vertices = getAllBeliefs("probedVertex");
		for (LogicBelief v : vertices) {
			if (v.getParameters().get(0).equals(position)) {
				probed = true;
				break;
			}
		}
		if (probed == false) {
			println("I do not know the value of my position. I will probe.");
			return Mars2011Util.probeAction();
		} else {
			println("I know the value of my position");
		}

		beliefs = getAllBeliefs("neighbor");

		// get unprobed neighbors
		Vector<String> unprobed = new Vector<String>();
		for (LogicBelief n : beliefs) {
			probed = false;
			String name = n.getParameters().firstElement();
			for (LogicBelief v : vertices) {
				if (v.getParameters().get(0).equals(name)) {
					probed = true;
					break;
				}
			}
			if (probed == false)
				unprobed.add(name);
		}
		if (unprobed.size() != 0) {
			println("some of my neighbors are unprobed.");
			Collections.shuffle(unprobed);
			String neighbor = unprobed.firstElement(); // escolhe o vizinho a
														// ser sondado, seria
														// interessante escolher
														// alguem do time oposto
														// j�.
			println("I will go to " + neighbor);
			return Mars2011Util.gotoAction(neighbor);
		} else {
			println("all of my neighbors are probed");
		}

		return null;

	}

	private Action planSurvey() {

		println("I know " + getAllBeliefs("visibleEdge").size()
				+ " visible edges");
		println("I know " + getAllBeliefs("surveyedEdge").size()
				+ " surveyed edges");

		// get all neighbors
		LinkedList<LogicBelief> visible = getAllBeliefs("visibleEdge");
		LinkedList<LogicBelief> surveyed = getAllBeliefs("surveyedEdge");

		String position = getAllBeliefs("position").get(0).getParameters()
				.firstElement();

		int unsurveyedNum = 0;
		int adjacentNum = 0;

		for (LogicBelief v : visible) {

			String vVertex0 = v.getParameters().elementAt(0);
			String vVertex1 = v.getParameters().elementAt(1);

			boolean adjacent = false;
			if (vVertex0.equals(position) || vVertex1.equals(position))
				adjacent = true;

			if (adjacent == false)
				continue;
			adjacentNum++;

			boolean isSurveyed = false;
			for (LogicBelief s : surveyed) {
				String sVertex0 = s.getParameters().elementAt(0);
				String sVertex1 = s.getParameters().elementAt(1);
				if (sVertex0.equals(vVertex0) && sVertex1.equals(vVertex1)) {
					isSurveyed = true;
					break;
				}
				if (sVertex0.equals(vVertex1) && sVertex1.equals(vVertex0)) {
					isSurveyed = true;
					break;
				}
			}
			if (isSurveyed == false)
				unsurveyedNum++;

		}

		println("" + unsurveyedNum + " out of " + adjacentNum
				+ " adjacent edges are unsurveyed");

		if (unsurveyedNum > 0) {
			println("I will survey");
			return Mars2011Util.surveyAction();
		}

		return null;

	}

	/**
	 * Buy a battery with a given probability
	 * 
	 * @return
	 */
	private Action planBuyBattery() {

		LinkedList<LogicBelief> beliefs = this.getAllBeliefs("money");
		if (beliefs.size() == 0) {
			println("strangely I do not know our money.");
			return null;
		}

		LogicBelief moneyBelief = beliefs.get(0);
		int money = new Integer(moneyBelief.getParameters().get(0)).intValue();

		if (money < 10) {
			println("we do not have enough money.");
			return null;
		}
		println("we do have enough money.");

		// double r = Math.random();
		// if ( r < 1.0 ) {
		// println("I am not going to buy a battery");
		// return null;
		// }
		int flag = ((int) Math.random() % 10);
		println("flag reparador" + flag);
		switch (flag) {

		case 0:
			println("I am going to buy a battery");
			return Mars2011Util.buyAction("battery");
		case 2:
			println("I am going to buy a shield");
			return Mars2011Util.buyAction("shield");
		default:
			break;
		}
		return null;
	}

}