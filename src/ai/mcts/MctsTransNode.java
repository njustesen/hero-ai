package ai.mcts;


import java.util.List;

import action.Action;

public class MctsTransNode extends AbstractMctsNode {

	public MctsNode transNode;
	public int visits;
	public double value;

	public MctsTransNode(Action action, MctsNode transNode) {
		this.action = action;
		this.transNode = transNode;
		this.visits = 0;
		this.value = 0;
	}

	@Override
	public boolean isRoot() {
		return transNode.children.isEmpty();
	}

	@Override
	public boolean isLeaf() {
		return transNode.children.isEmpty();
	}

	@Override
	public boolean isFullyExpanded() {
		return transNode.isFullyExpanded();
	}
	
	@Override
	public double avgValue() {
		return value / visits;
	}
	
	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public List<AbstractMctsNode> getChildren() {
		return transNode.getChildren();
	}

	@Override
	public List<AbstractMctsNode> getParents() {
		return transNode.getParents();
	}

	@Override
	public List<Action> getPossibleActions() {
		return transNode.getPossibleActions();
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
		return transNode.isP1();
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
		transNode.setP1(p1);
	}
	

}
