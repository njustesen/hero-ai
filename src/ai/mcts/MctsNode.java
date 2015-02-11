package ai.mcts;

import java.util.ArrayList;
import java.util.List;

import action.Action;

public class MctsNode extends AbstractMctsNode {

	public List<Action> possible;
	public List<AbstractMctsNode> parents;
	public List<AbstractMctsNode> children;
	public int visits;
	public double value;
	public boolean p1;

	public MctsNode(Action action, AbstractMctsNode parent) {
		this.action = action;
		parents = new ArrayList<AbstractMctsNode>();
		if (parent != null)
			parents.add(parent);
		possible = new ArrayList<Action>();
		children = new ArrayList<AbstractMctsNode>();
		visits = 0;
		value = 0;
	}

	@Override
	public List<AbstractMctsNode> getChildren() {
		return children;
	}

	@Override
	public List<AbstractMctsNode> getParents() {
		return parents;
	}

	@Override
	public List<Action> getPossibleActions() {
		return possible;
	}

	@Override
	public int getVisits() {
		return visits;
	}

	@Override
	public double getValue() {
		return value;
	}

	@Override
	public boolean isP1() {
		return p1;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public void setVisits(int visits) {
		this.visits = visits;
	}

	@Override
	public void setP1(boolean p1) {
		this.p1 = p1;
	}

}
