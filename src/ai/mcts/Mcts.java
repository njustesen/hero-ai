package ai.mcts;

import game.GameState;
import action.Action;
import ai.AI;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;

public class Mcts implements AI {

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicy;
	private ActionPruner pruner;
	
	public Mcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy){
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicy = defaultPolicy;
		this.pruner = new ActionPruner();
	}
	
	@Override
	public Action act(GameState state, long ms) {
		
		long start = System.currentTimeMillis();
		
		MctsNode root = new MctsNode(null, null);
		state.possibleActions(root.possible);
		pruner.prune(root.possible, state);
		if (root.possible.size()==1)
			return root.possible.get(0);
		GameState clone = state.copy();
		int rolls = 0;
		
		//while(System.currentTimeMillis() < start + budget*10000){
		while(System.currentTimeMillis() < start + budget){
					
			clone.imitate(state);
			MctsNode node = treePolicy(root, clone);
			double delta = defaultPolicy.eval(clone, state.p1Turn);;
			backup(node, delta);
			rolls++;
			
		}
		
		for(MctsNode child : root.children)
			System.out.println(child);
		
		System.out.println("Rolls=" + rolls);
		return bestChild(root, state.p1Turn, false).action;
		
	}

	private MctsNode treePolicy(MctsNode node, GameState clone) {
		MctsNode next;
		while(!clone.isTerminal){
			if (!node.isFullyExpanded())
				return expand(node, clone);
			else {
				next = bestChild(node, clone.p1Turn, true);
				if (next == null)
					return node.parent;
				node = next;
				clone.update(node.action);
			}
		}
		
		return node;
	}

	private MctsNode expand(MctsNode node, GameState clone) {
		Action next = node.possible.get(node.children.size());
		clone.update(next);
		MctsNode child = new MctsNode(next, node);
		node.children.add(child);
		clone.possibleActions(child.possible);
		pruner.prune(child.possible, clone);
		return child;
	}
	
	private void backup(MctsNode node, double delta) {
		while(node != null){
			node.visits++;
			node.value += delta;
			node = node.parent;
		}
	}

	private MctsNode bestChild(MctsNode node, boolean p1, boolean urgent) {

		double bestValue = -1000000;
		MctsNode bestChild = null;
		for(MctsNode child : node.children){
			double value = 0;
			if (urgent)
				value = treePolicy.urgent(child);
			else 
				value = treePolicy.best(child);
			if (!p1)
				value = value * (-1);
			if (value > bestValue){
				bestValue = value;
				bestChild = child;
			}
		}
		
		if (bestChild == null){
			int x = 0;
			x++;
		}
		
		return bestChild;
		
	}
		
	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
