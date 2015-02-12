package ai.mcts.collapse;

import game.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.AI;
import ai.heuristic.IHeuristic;
import ai.mcts.AbstractMctsNode;
import ai.mcts.ITreePolicy;
import ai.mcts.MctsNode;
import ai.mcts.MctsTransNode;
import ai.util.ActionComparator;
import ai.util.ActionPruner;
import ai.util.GameStateHasher;

public class CollapsedMcts implements AI {

	private static final int MOVES = 30;

	Map<String, MctsNode> transTable = new HashMap<String, MctsNode>();

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicy;
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private AbstractMctsNode root;
	private GameStateHasher hasher;
	
	private List<Action> move;
	private int ends;

	private int rec;
	
	public CollapsedMcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy) {
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicy = defaultPolicy;
		this.pruner = new ActionPruner();
		this.comparator = new ActionComparator();
		this.hasher = new GameStateHasher();
		this.move = new ArrayList<Action>();
		this.ends = 0;
	}

	@Override
	public Action act(GameState state, long ms) {

		// If move already found return next action
		if (!move.isEmpty()){
			Action action = move.get(0);
			move.remove(0);
			return action;
		}
		
		// Search
		mcts(state, budget/3, budget, MOVES/2);
		
		// If mcts did find enough moves return as normal
		if (ends == 0){
			move = findBestMove(state);
			Action action = move.get(0);
			move.remove(0);
			return action;
		}
		
		// Collapse
		CollapsedRoot croot = collapseTree(state);

		//System.out.println(root.toXml(0));
		
		CollapsedNode best = continuedSearch(state, croot, (ms/3)*2);
		
		// Save best move and return first action
		move = best.move;
		Action action = move.get(0);
		move.remove(0);
		root = null;
		croot = null;
		ends = 0;
		transTable.clear();
		
		return action;
	}
	
	private List<Action> findBestMove(GameState state) {
		AbstractMctsNode node = bestChild(root, state.p1Turn, false);
		List<Action> move = new ArrayList<Action>();
		while(node.action != SingletonAction.endTurnAction){
			move.add(node.action);
			if (node.getChildren().isEmpty())
				break;
			node = bestChild(node, state.p1Turn, false);
		}
		move.add(SingletonAction.endTurnAction);
		return move;
	}

	private CollapsedRoot collapseTree(GameState state) {
		
		// Find end nodes
		Set<AbstractMctsNode> endNodes = new HashSet<AbstractMctsNode>();
		findEndNodes(root, endNodes);
		System.out.println(endNodes.size() + " end nodes found");
		List<AbstractMctsNode> endNodesList = new ArrayList<AbstractMctsNode>();
		endNodesList.addAll(endNodes);
		
		// Take only most visited end nodes
		Collections.sort(endNodesList, new Comparator<AbstractMctsNode>() {
		    public int compare(AbstractMctsNode o1, AbstractMctsNode o2) {
		    	return o2.getVisits() - o1.getVisits();
		    }
		});
		endNodesList = endNodesList.subList(0, Math.min(MOVES, endNodes.size()));
		
		// Create collapsed nodes
		CollapsedRoot croot = new CollapsedRoot(root);
		for(AbstractMctsNode endNode : endNodesList)
			croot.children.add(new CollapsedNode(croot.node, endNode, state.copy()));
		
		// Reset visits
		int v = 0;
		for(CollapsedNode child : croot.children)
			v += child.node.getVisits();
		croot.node.setVisits(v);
		
		return croot;
		
	}

	private CollapsedNode continuedSearch(GameState state, CollapsedRoot croot, long budget) {
		
		final long start = System.currentTimeMillis();
		
		GameState clone = null;
		CollapsedNode cnode = null;
		AbstractMctsNode node = null;
		double delta = 0;
		int rolls = 0;
		
		//System.out.println(croot.toXml(0));
		
		//while (System.currentTimeMillis() < start + budget) {
		while (System.currentTimeMillis() < start + budget) {

			cnode = bestChild(croot, state.p1Turn, true);
			if (clone == null){
				clone = cnode.state.copy();
			}else 
				clone.imitate(cnode.state);
			node = treePolicy(cnode.node, clone);
			delta = defaultPolicy.eval(clone, state.p1Turn);
			backupNegaMultiMax(node, delta, state.p1Turn);
			rolls++;
			//System.out.println(croot.toXml(0));
			
			//if (rolls % 10000 == 0)
			//	System.out.println(root.toXml(0));
			
		}
		
		System.out.println("Rolls="+rolls);
		
		return bestChild(croot, state.p1Turn, false);
	}


	private void findEndNodes(AbstractMctsNode root, Set<AbstractMctsNode> endNodes) {
		for(AbstractMctsNode child : root.getChildren()){
			if (child.action == SingletonAction.endTurnAction)
				endNodes.add(child);
			else
				findEndNodes(child, endNodes);
		}
	}


	public void mcts(GameState state, long min, long max, int minEnds) {

		final long start = System.currentTimeMillis();

		if (root == null) {
			root = new MctsNode(null, null);
			state.possibleActions(root.getPossibleActions());
			pruner.prune(root.getPossibleActions(), state);
			comparator.state = state;
			Collections.sort(root.getPossibleActions(), comparator);
		}
		final GameState clone = state.copy();
		int rolls = 0;

		while (timeLeft(start, min, max, minEnds)) {
			rec = 0;
			clone.imitate(state);
			final AbstractMctsNode node = treePolicy(root, clone);
			final double delta = defaultPolicy.eval(clone, state.p1Turn);
			backupNegaMultiMax(node, delta, state.p1Turn);
			rolls++;
			System.out.println(rec);
			//if (rolls % 10000 == 0)
			//	System.out.println(root.toXml(0));
			
		}

		System.out.println("Rolls=" + rolls);

	}

	private boolean timeLeft(long start, long min, long max, int minEnds) {
		
		if (System.currentTimeMillis() > start + max)
			return false;
		
		if (System.currentTimeMillis() < start + min || ends < minEnds)
			return true;
		
		return false;
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
			if (child.getAction() == SingletonAction.endTurnAction)
				ends++;
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
		rec++;
		if (node.isP1() == p1)
			node.setValue(node.getValue() + delta);
		else
			node.setValue(node.getValue() - delta);
		//System.out.println("P=" + node.parents.size());
		for (final AbstractMctsNode parent : node.getParents()){
			//System.out.println(node.hashCode() + " -> " + parent.hashCode());
			if (rec == 20){
				System.out.println(node.hashCode());
				System.out.println(root.toXml(0));
			}
			backupNegaMultiMax(parent, delta, p1);
		}
	}
	
	private CollapsedNode bestChild(CollapsedRoot root, boolean p1, boolean urgent) {

		double bestValue = -1000000;
		CollapsedNode bestChild = null;
		for (final CollapsedNode child : root.children) {
			double value = 0;
			if (urgent)
				value = treePolicy.urgent(child.node, root.node);
			else
				value = treePolicy.best(child.node);
			if (value > bestValue) {
				bestValue = value;
				bestChild = child;
			}
		}

		return bestChild;

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
