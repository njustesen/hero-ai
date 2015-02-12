package ai.mcts.collapse;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import ai.mcts.AbstractMctsNode;

public class CollapsedNode {

	AbstractMctsNode node;
	List<Action> move;
	GameState state;
	
	public CollapsedNode(AbstractMctsNode root, AbstractMctsNode node, GameState state) {
		super();
		this.node = node;
		this.move = new ArrayList<Action>();
		setMove(node);
		node.getParents().clear();
		node.getParents().add(root);
		setState(state);
	}

	private void setState(GameState start) {
		for(Action action : move)
			start.update(action);
		this.state = start;
	}

	private  void setMove(AbstractMctsNode node) {
		while(node != null){
			if (node.action == null)
				break;
			move.add(node.action);
			if (node.getParents().isEmpty())
				break;
			node = node.getParents().get(0);
		}
		Collections.reverse(move);
	}

	public String toXml(int depth){
		String tabs = "";
		for (int i = 0; i < depth; i++)
			tabs += "\t";
		String str = tabs;
		if (node.getChildren().isEmpty())
			str += thisToXml(depth);
		else {
			str += thisToXml(depth);
			for (final AbstractMctsNode child : node.getChildren())
				str += child.toXml(depth + 1);
			str += tabs + "</node>\n";
		}

		return str;
	}

	private String thisToXml(int depth) {
		String str = "<node h=" + node.hashCode() + " parents=[";
		boolean b = false;
		for (AbstractMctsNode parent : node.getParents()){
			if (b)
				str += ", ";
			str += parent.hashCode();
			b = true;
		}
		str += "] ";
		
		str += "p=\"" + (node.isP1() ? 1 : 2) + "\" a=\"" + node.action
		+ "\" vis=\"" + node.getVisits() + "\" val=\"" + node.avgValue()
		+ "\" />\n";
		
		return str;
	}
	
	
	
}
