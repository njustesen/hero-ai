package ai.mcts;


import java.util.List;

import action.Action;

public class MctsTransNode extends AbstractMctsNode {

	public MctsNode transNode;

	public MctsTransNode(Action action, MctsNode transNode) {
		this.action = action;
		this.transNode = transNode;
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
		return transNode.avgValue();
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
		return transNode.getVisits();
	}

	@Override
	public double getValue() {
		return transNode.getValue();
	}

	@Override
	public boolean isP1() {
		return transNode.isP1();
	}

	@Override
	public void setValue(double value) {
		transNode.setValue(value);
	}

	@Override
	public void setVisits(int visits) {
		transNode.setVisits(visits);
	}

	@Override
	public void setP1(boolean p1) {
		transNode.setP1(p1);
	}
	

}
