package ai.mcts;

import java.util.Collections;

import game.GameState;
import action.Action;
import action.EndTurnAction;
import ai.AI;
import ai.heuristic.IHeuristic;
import ai.util.ActionComparator;
import ai.util.ActionPruner;

public class Mcts implements AI {

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicy;
	private ActionPruner pruner;
	private final ActionComparator comparator;
	private MctsNode root;
	
	public Mcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy){
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicy = defaultPolicy;
		this.pruner = new ActionPruner();
		this.comparator = new ActionComparator();
	}
	
	@Override
	public Action act(GameState state, long ms) {
		
		long start = System.currentTimeMillis();
		boolean save = false;
		if (state.APLeft > 0){
			save = true;
		} else {
			root = null;
			return new EndTurnAction();
		}
		
		if (root == null){
			root = new MctsNode(null, null);
			state.possibleActions(root.possible);
			pruner.prune(root.possible, state);
			comparator.state = state;
			Collections.sort(root.possible, comparator);
		}
		GameState clone = state.copy();
		int rolls = 0;
		
		while(System.currentTimeMillis() < start + budget){
					
			clone.imitate(state);
			MctsNode node = treePolicy(root, clone);
			double delta = defaultPolicy.eval(clone, state.p1Turn);;
			backupNegamax(node, delta);
			rolls++;
			
		}
		
		for(MctsNode child : root.children)
			System.out.println(child);
		
		System.out.println("Rolls=" + rolls);
		MctsNode best = bestChild(root, state.p1Turn, false);

		//System.out.println(root.toXml(0));
		
		if (save)
			root = best;
		
		return best.action;
		
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
		boolean p1 = clone.p1Turn;
		clone.update(next);
		MctsNode child = new MctsNode(next, node);
		child.p1 = p1;
		node.children.add(child);
		clone.possibleActions(child.possible);
		pruner.prune(child.possible, clone);
		comparator.state = clone;
		Collections.sort(child.possible, comparator);
		return child;
	}
	
	private void backupNegamax(MctsNode node, double delta) {
		while(node != null){
			node.visits++;
			if (node.p1)
				node.value += delta;
			else
				node.value -= delta;
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
			//if (!p1)
			//	value = value * (-1);
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
