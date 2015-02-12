package ai.mcts;

import game.GameState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.AI;
import ai.heuristic.IHeuristic;
import ai.util.ActionComparator;
import ai.util.ActionPruner;
import ai.util.GameStateHasher;

public class Mcts implements AI {

	Map<String, MctsNode> transTable = new HashMap<String, MctsNode>();

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicy;
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private AbstractMctsNode root;
	private GameStateHasher hasher;

	public Mcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy) {
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicy = defaultPolicy;
		this.pruner = new ActionPruner();
		this.comparator = new ActionComparator();
		this.hasher = new GameStateHasher();
	}

	@Override
	public Action act(GameState state, long ms) {

		final long start = System.currentTimeMillis();
		transTable.clear();

		if (state.APLeft == 0){
			root = null;
			return new EndTurnAction();
		}

		if (root == null) {
			root = new MctsNode(null, null);
			state.possibleActions(root.getPossibleActions());
			pruner.prune(root.getPossibleActions(), state);
			comparator.state = state;
			Collections.sort(root.getPossibleActions(), comparator);
		}
		final GameState clone = state.copy();
		int rolls = 0;

		while (System.currentTimeMillis() < start + budget) {

			clone.imitate(state);
			final AbstractMctsNode node = treePolicy(root, clone);
			final double delta = defaultPolicy.eval(clone, state.p1Turn);
			backupNegaMultiMax(node, delta, state.p1Turn);
			rolls++;
			
			//if (rolls % 10000 == 0)
			//	System.out.println(root.toXml(0));
			
		}

		for (final AbstractMctsNode child : root.getChildren())
			System.out.println(child);

		System.out.println("Rolls=" + rolls);
		final AbstractMctsNode best = bestChild(root, state.p1Turn, false);

		//root = null;
		
		if (best == null)
			return SingletonAction.endTurnAction;
		
		return best.action;

	}

	private AbstractMctsNode treePolicy(AbstractMctsNode node, GameState clone) {
		AbstractMctsNode next;
		while (!clone.isTerminal)
			if (!node.isFullyExpanded())
				return expand(node, clone);
			else {
				next = bestChild(node, clone.p1Turn, true);
				if (next == null)
					return node;
				node = next;
				clone.update(node.action);
			}

		return node;
	}

	private AbstractMctsNode expand(AbstractMctsNode node, GameState clone) {
		final Action next = node.getPossibleActions().get(node.getChildren().size());
		final boolean p1 = clone.p1Turn;
		clone.update(next);
		final String hash = hasher.hash(clone);
		AbstractMctsNode child = null;
		if (transTable.containsKey(hash)) {
			child = new MctsTransNode(next, transTable.get(hash));
			if (!child.getParents().contains(node))
				child.getParents().add(node);
		} else {
			child = new MctsNode(next, node);
			child.setP1(p1);
			clone.possibleActions(child.getPossibleActions());
			pruner.prune(child.getPossibleActions(), clone);
			comparator.state = clone;
			Collections.sort(child.getPossibleActions(), comparator);
			transTable.put(hash, ((MctsNode)child));
		}
		node.getChildren().add(child);
		return child;
	}

	private void backupNegaMultiMax(AbstractMctsNode node, double delta, boolean p1) {
		node.setVisits(node.getVisits() + 1);
		if (node.isP1() == p1)
			node.setValue(node.getValue() + delta);
		else
			node.setValue(node.getValue() - delta);
		//System.out.println("P=" + node.parents.size());
		for (final AbstractMctsNode parent : node.getParents()){
			//System.out.println(node.hashCode() + " -> " + parent.hashCode());
			backupNegaMultiMax(parent, delta, p1);
		}
	}

	private AbstractMctsNode bestChild(AbstractMctsNode node, boolean p1, boolean urgent) {

		double bestValue = -1000000;
		AbstractMctsNode bestChild = null;
		for (final AbstractMctsNode child : node.getChildren()) {
			double value = 0;
			if (urgent)
				value = treePolicy.urgent(child, node);
			else
				value = treePolicy.best(child);
			if (value > bestValue) {
				bestValue = value;
				bestChild = child;
			}
		}

		return bestChild;

	}
	
	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
