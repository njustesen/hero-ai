package ai.mcts;

import java.util.ArrayList;
import java.util.List;

import action.Action;

public class MctsNode {
	
	public Action action;
	public List<Action> possible;
	public MctsNode parent;
	public List<MctsNode> children;
	public int visits;
	public double value;
	
	public MctsNode(Action action, MctsNode parent) {
		this.action = action;
		this.parent = parent;
		this.possible = new ArrayList<Action>();
		this.children = new ArrayList<MctsNode>();
		this.visits = 0;
		this.value = 0;
	}

	public boolean isRoot(){
		return parent == null;
	}
	
	public boolean isLeaf(){
		return children.isEmpty();
	}

	public boolean isFullyExpanded() {
		return possible.size() == children.size();
	}
	
	public double avgValue() {
		if (visits == 0)
			return 0;
		return value / visits;
	}

	@Override
	public String toString() {
		return "MctsNode [action=" + action + ", visits=" + visits + ", value="
				+ value + ", avg="+avgValue() + ", children=" + children.size() + "]";
	}
	
}
