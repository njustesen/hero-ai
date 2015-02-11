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

public class Mcts implements AI {

	Map<String, MctsNode> transTable = new HashMap<String, MctsNode>();

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicy;
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private MctsNode root;

	public Mcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy) {
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicy = defaultPolicy;
		pruner = new ActionPruner();
		comparator = new ActionComparator();
	}

	@Override
	public Action act(GameState state, long ms) {

		final long start = System.currentTimeMillis();
		transTable.clear();

		boolean save = false;
		if (state.APLeft > 0)
			save = true;
		else {
			root = null;
			return new EndTurnAction();
		}

		if (root == null) {
			root = new MctsNode(null, null);
			state.possibleActions(root.possible);
			pruner.prune(root.possible, state);
			comparator.state = state;
			Collections.sort(root.possible, comparator);
		}
		final GameState clone = state.copy();
		int rolls = 0;

		while (System.currentTimeMillis() < start + budget) {

			clone.imitate(state);
			final MctsNode node = treePolicy(root, clone);
			final double delta = defaultPolicy.eval(clone, state.p1Turn);
			backupNegaMultiMax(node, delta, state.p1Turn);
			rolls++;

		}

		for (final MctsNode child : root.children)
			System.out.println(child);

		System.out.println("Rolls=" + rolls);
		final MctsNode best = bestChild(root, state.p1Turn, false);

		// System.out.println(root.toXml(0));
		if (save)
			root = best;
		
		if (best == null)
			return SingletonAction.endTurnAction;
		
		return best.action;

	}

	private MctsNode treePolicy(MctsNode node, GameState clone) {
		MctsNode next;
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

	private MctsNode expand(MctsNode node, GameState clone) {
		final Action next = node.possible.get(node.children.size());
		final boolean p1 = clone.p1Turn;
		clone.update(next);
		final String hash = clone.hash();
		MctsNode child = null;
		if (transTable.containsKey(hash)) {
			child = transTable.get(hash);
			if (!child.parents.contains(node))
				child.parents.add(node);
		} else {
			child = new MctsNode(next, node);
			child.p1 = p1;
			clone.possibleActions(child.possible);
			pruner.prune(child.possible, clone);
			comparator.state = clone;
			Collections.sort(child.possible, comparator);
			transTable.put(hash, child);
		}
		node.children.add(child);
		return child;
	}

	/*
	private void backupNegamax(MctsNode node, double delta, boolean p1) {
		while(node != null){
			node.visits++;
			if (node.p1 == p1)
				node.value += delta;
			else
				node.value -= delta;
			node = node.parent;
		}
	}
	*/

	private void backupNegaMultiMax(MctsNode node, double delta, boolean p1) {
		node.visits++;
		if (node.p1 == p1)
			node.value += delta;
		else
			node.value -= delta;
		System.out.println("P=" + node.parents.size());
		for (final MctsNode parent : node.parents)
			backupNegaMultiMax(parent, delta, p1);
	}

	private MctsNode bestChild(MctsNode node, boolean p1, boolean urgent) {

		double bestValue = -1000000;
		MctsNode bestChild = null;
		for (final MctsNode child : node.children) {
			double value = 0;
			if (urgent)
				value = treePolicy.urgent(child, node);
			else
				value = treePolicy.best(child);
			// if (!p1)
			// value = value * (-1);
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
