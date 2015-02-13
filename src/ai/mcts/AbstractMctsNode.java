package ai.mcts;

import java.util.List;

import action.Action;

public abstract class AbstractMctsNode {
	
	public Action action;
	
	public Action getAction(){
		return action;
	}
	public abstract List<AbstractMctsNode> getChildren();
	public abstract List<AbstractMctsNode> getParents();
	public abstract List<Action> getPossibleActions();
	public abstract int getVisits();
	public abstract void setValue(double value);
	public abstract double getValue();
	public abstract void setVisits(int visits);
	public abstract boolean isP1();
	public abstract void setP1(boolean p1);

	public boolean isRoot() {
		return getParents().isEmpty();
	}

	public boolean isLeaf(){
		return getChildren().isEmpty();
	}

	public boolean isFullyExpanded(){
		return getChildren().size() >= getPossibleActions().size();
	}

	public double avgValue(){
		return getValue() / getVisits();
	}

	public String toXml(int depth){
		String tabs = "";
		for (int i = 0; i < depth; i++)
			tabs += "\t";
		String str = tabs;
		if (getChildren().isEmpty())
			str += thisToXml(depth);
		else {
			str += thisToXml(depth);
			for (final AbstractMctsNode child : getChildren())
				str += child.toXml(depth + 1);
			str += tabs + "</node>\n";
		}

		return str;
	}

	private String thisToXml(int depth) {
		String str = "<node h=" + hashCode() + " parents=[";
		boolean b = false;
		for (AbstractMctsNode parent : getParents()){
			if (b)
				str += ", ";
			str += parent.hashCode();
			b = true;
		}
		str += "] ";
		
		str += "p=\"" + (isP1() ? 1 : 2) + "\" a=\"" + action
		+ "\" vis=\"" + getVisits() + "\" val=\"" + avgValue()
		+ "\" />\n";
		
		return str;
	}

	@Override
	public String toString() {
		return "MctsNode [action=" + action + ", visits=" + getVisits() + ", value="
				+ getValue() + ", avg=" + avgValue() + ", children="
				+ getChildren().size() + "]";
	}
	
}
