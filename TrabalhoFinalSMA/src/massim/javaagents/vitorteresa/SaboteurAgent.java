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

public class SaboteurAgent extends Agent {

	public SaboteurAgent(String name, String team) {
		super(name, team);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handlePercept(Percept p) {
	}

	@Override
	public Action step() {

		Action act = null;

		handleMessages();
		handlePercepts();

		// 1. recharging
		act = planRecharge();
		if (act != null)
			return act;

		act = planGoToInspectorNeighbors();
		if (act != null)
			return act;

		// 2. fight if possible
		act = planFight();
		if (act != null)
			return act;

		// 1. recharging
		act = planBuySabotageDevice();
		if (act != null)
			return act;

		// 3. random walking
		act = planRandomWalk();
		if (act != null)
			return act;

		return Mars2011Util.skipAction();

	}

	private Action planGoToInspectorNeighbors() {

		LinkedList<LogicBelief> inspectedEntities = getAllBeliefs("inspectedEntity");

		if (inspectedEntities.isEmpty()) {
			return null;
		}

		LinkedList<LogicBelief> neighbors = getAllBeliefs("neighbor");
		LinkedList<String> neighborNames = new LinkedList<String>();

		for (LogicBelief n : neighbors) {
			neighborNames.add(n.getParameters().firstElement());
		}

		for (LogicBelief i : inspectedEntities) {
			if (neighborNames.contains(i.getParameters().firstElement())) {
				if (Integer.valueOf(i.getParameters().get(2)) > 0) {
					System.out
							.println("Indo para um vizinho indicado por um inspetor");
					return Mars2011Util.gotoAction(i.getParameters()
							.firstElement());
				}
			}
		}
		return null;
	}

	private void handleMessages() {

		Collection<Message> messages = getMessages();
		for (Message msg : messages) {
			if (msg.value instanceof LogicBelief) {
				LogicBelief lb = (LogicBelief) msg.value;
				if (lb.getPredicate().equals("inspectedEntity")) {
					addBelief(lb);
				}
			}
		}
	}

	private Action planBuySabotageDevice() {

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

		double r = Math.random();
		if (r > 0.1) {
			println("I am not going to buy a battery");
			return null;
		}
		println("I am going to buy a battery");

		return Mars2011Util.buyAction("sabotageDevice");

	}

	private void handlePercepts() {

		String position = null;
		Vector<String> neighbors = new Vector<String>();

		// check percepts
		Collection<Percept> percepts = getAllPercepts();
		// if ( gatherSpecimens ) processSpecimens(percepts);
		removeBeliefs("visibleEntity");
		removeBeliefs("inspectedEntity");

		removeBeliefs("visibleEdge");
		for (Percept p : percepts) {
			if (p.getName().equals("step")) {
				println(p);
			} else if (p.getName().equals("visibleEntity")) {
				LogicBelief b = Mars2011Util.perceptToBelief(p);
				if (containsBelief(b) == false) {
					// println("I perceive an edge I have not known before");
					addBelief(b);
					// broadcastBelief(b);
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
			} else if (p.getName().equals("inspectedEntity")) {
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

	private Action planRecharge() {

		LinkedList<LogicBelief> beliefs = null;

		beliefs = getAllBeliefs("energy");
		if (beliefs.size() == 0) {
			println("strangely I do not know my energy");
			return Mars2011Util.skipAction();
		}
		int energy = new Integer(beliefs.getFirst().getParameters()
				.firstElement()).intValue();

		beliefs = getAllBeliefs("maxEnergy");
		if (beliefs.size() == 0) {
			println("strangely I do not know my maxEnergy");
			return Mars2011Util.skipAction();
		}
		int maxEnergy = new Integer(beliefs.getFirst().getParameters()
				.firstElement()).intValue();

		// if has the goal of being recharged...
		if (goals.contains(new LogicGoal("beAtFullCharge"))) {
			if (maxEnergy == energy) {
				println("I can stop recharging. I am at full charge");
				removeGoals("beAtFullCharge");
			} else {
				println("recharging...");
				return Mars2011Util.rechargeAction();
			}
		}
		// go to recharge mode if necessary
		else {
			if (energy <= 0) {
				println("I need to recharge");
				goals.add(new LogicGoal("beAtFullCharge"));
				return Mars2011Util.rechargeAction();
			}
		}

		return null;

	}

	private Action planFight() {

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
		Vector<LogicBelief> enemiesDatas = new Vector<LogicBelief>();

		beliefs = getAllBeliefs("visibleEntity");
		for (LogicBelief b : beliefs) {
			String name = b.getParameters().get(0);
			String pos = b.getParameters().get(1);
			String team = b.getParameters().get(2);
			String status = b.getParameters().get(3);
			if (team.equals(getTeam()))
				continue;
			if (pos.equals(position) == false)
				continue;
			if (status.equals("disabled"))
				continue;

			enemies.add(name);
			LogicBelief inspected = getInspectedEntity(name);
			if (inspected != null) {
				enemiesDatas.add(inspected);
			}
		}
		if (enemies.size() != 0) {
			println("there are " + enemies.size()
					+ " enemies at my current position");

			int lowerHhealth = Integer.MAX_VALUE;

			LogicBelief weakestEntity = null;

			if (!enemiesDatas.isEmpty()) {
				for (LogicBelief b : enemiesDatas) {
					int healthStr = Integer.valueOf(b.getParameters().get(1));
					if (healthStr < lowerHhealth) {
						weakestEntity = b;
					}
				}
				return Mars2011Util.attackAction(weakestEntity.getParameters()
						.firstElement());

			} else {
				Collections.shuffle(enemies);
				String enemy = enemies.firstElement();
				println("I will attack " + enemy);
				return Mars2011Util.attackAction(enemy);
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

	private LogicBelief getInspectedEntity(String name) {
		LinkedList<LogicBelief> beliefs = getAllBeliefs("inspectedEntity");
		for (LogicBelief b : beliefs) {
			String bname = b.getParameters().firstElement();
			if (bname.equals(name)) {
				return b;
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

}
