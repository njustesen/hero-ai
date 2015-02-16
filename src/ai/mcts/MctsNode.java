package ai.mcts;

import java.util.ArrayList;
import java.util.List;

import action.Action;

public class MctsNode {

	public List<MctsEdge> in;
	public List<MctsEdge> out;
	List<Action> possible;
	public int visits;
	
	public MctsNode(List<Action> possible) {
		out = new ArrayList<MctsEdge>();
		in = new ArrayList<MctsEdge>();
		this.possible = possible;
		this.visits = 0;
	}

	public MctsEdge nextEdge(boolean p1) {
		return new MctsEdge(this, null, possible.get(out.size()), p1);
	}
	
	public boolean isFullyExpanded(){
		return out.size() >= possible.size();
	}

	public boolean isRoot() {
		return in.isEmpty();
	}
	
	public boolean isLeaf() {
		return out.size() == 0;
	}
	
	public String toXml(int depth){
		String str = "";
		String tabs = "";
		for(int i = 0; i < depth; i++)
			tabs += "\t";
		if (out.isEmpty())
			str += tabs + "<node h='"+hashCode()+"' vis='"+visits+"' />\n";
		else {
			str += tabs + "<node h='"+hashCode()+"' vis='"+visits+"' >\n";
			for (final MctsEdge edge : out)
				if (edge.to != null)
					str += edge.toXml(depth+1);
			str += tabs + "</node>\n";
		}
		return str;
	}

	@Override
	public String toString() {
		return "MctsNode [in=" + in.size() + ", out=" + out.size() + "]";
	}
	
}
