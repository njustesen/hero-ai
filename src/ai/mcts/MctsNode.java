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
	public boolean p1;
	
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

	public String toXml(int depth){
		String tabs = "";
		for(int i = 0; i < depth; i++)
			tabs += "\t";
		String str = tabs;
		if (children.isEmpty())
			str += "<node p=\"" + (p1 ? 1 : 2) + "\" a=\"" + action + "\" vis=\"" + visits + "\" val=\"" + avgValue() + "\" />\n";
		else {
			str += "<node p=\"" + (p1 ? 1 : 2) + "\" a=\"" + action + "\" vis=\"" + visits + "\" val=\"" + avgValue() + "\">\n";
			for(MctsNode child : children)
				str += child.toXml(depth+1);
			str += tabs + "</node>\n";
		}
		
		return str;
	}
	
	@Override
	public String toString() {
		return "MctsNode [action=" + action + ", visits=" + visits + ", value="
				+ value + ", avg="+avgValue() + ", children=" + children.size() + "]";
	}
	
}
