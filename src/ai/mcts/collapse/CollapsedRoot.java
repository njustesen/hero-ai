package ai.mcts.collapse;

import java.util.ArrayList;
import java.util.List;

import ai.mcts.AbstractMctsNode;

public class CollapsedRoot {
	
	public List<CollapsedNode> children;
	public AbstractMctsNode node;

	public CollapsedRoot(AbstractMctsNode node) {
		this.node = node;
		children = new ArrayList<CollapsedNode>();
	}

	public String toXml(int depth){
		String tabs = "";
		for (int i = 0; i < depth; i++)
			tabs += "\t";
		String str = tabs;
		if (children.isEmpty())
			str += thisToXml(depth);
		else {
			str += thisToXml(depth);
			for(final CollapsedNode child : children)
				str += child.toXml(depth + 1);
			str += tabs + "</node>\n";
		}

		return str;
	}
	
	private String thisToXml(int depth) {
		String str = "<node h=" + node.hashCode();
		
		str += " p=\"" + (node.isP1() ? 1 : 2) + "\" a=\"" + node.action
		+ "\" vis=\"" + node.getVisits() + "\" val=\"" + node.avgValue()
		+ "\" />\n";
		
		return str;
	}

}
