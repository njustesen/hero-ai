package ai.mcts.collapse;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.Action;
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

	private static final int MOVES = 32;

	Map<String, MctsNode> transTable = new HashMap<String, MctsNode>();

	public long budget;
	public ITreePolicy treePolicy;
	public IHeuristic defaultPolicyA;
	public IHeuristic defaultPolicyB;
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private AbstractMctsNode root;
	private GameStateHasher hasher;
	
	private List<Action> move;
	private int ends;
	
	public CollapsedMcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicyA, IHeuristic defaultPolicyB) {
		this.budget = budget;
		this.treePolicy = treePolicy;
		this.defaultPolicyA = defaultPolicyA;
		this.defaultPolicyB = defaultPolicyB;
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
		mcts(state, budget/3, budget, MOVES);
		
		// If mcts did find enough moves return as normal
		if (ends == 0){
			move = bestMove(state);
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
	
	private List<Action> bestMove(GameState state) {
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
	
	private void findEndNodes(AbstractMctsNode root, Set<AbstractMctsNode> endNodes) {
		for(AbstractMctsNode child : root.getChildren()){
			if (child.action == SingletonAction.endTurnAction)
				endNodes.add(child);
			else
				findEndNodes(child, endNodes);
		}
	}

	private CollapsedNode continuedSearch(GameState state, CollapsedRoot croot, long budget) {
		
		final long start = System.currentTimeMillis();
		
		GameState clone = null;
		CollapsedNode cnode = null;
		//AbstractMctsNode node = null;
		double delta = 0;
		int rolls = 0;
		List<AbstractMctsNode> traversal = new ArrayList<AbstractMctsNode>();
		
		while (System.currentTimeMillis() < start + budget) {
			traversal.clear();
			traversal.add(croot.node);
			
			cnode = bestChild(croot, state.p1Turn, true);
			if (clone == null)
				clone = cnode.state.copy();
			else 
				clone.imitate(cnode.state);
			
			treePolicy(traversal, cnode.node, clone);
			delta = defaultPolicyB.eval(clone, state.p1Turn);
			//delta = defaultPolicyB.normalize(delta);
			backupNegaMax(traversal, delta, state.p1Turn);
			rolls++;
			
		}
		
		System.out.println("Collapsed rolls="+rolls);
		
		return bestChild(croot, state.p1Turn, false);
	}

	public void mcts(GameState state, long min, long max, int minEnds) {

		final long start = System.currentTimeMillis();

		// Create root
		root = new MctsNode(null, null);
		addActions(root, state);
		
		// Start search
		final GameState clone = state.copy();
		int rolls = 0;
		List<AbstractMctsNode> traversal = new ArrayList<AbstractMctsNode>();
		double delta = 0;
		while (timeLeft(start, min, max, minEnds)) {
			traversal.clear();
			clone.imitate(state);
			// SELECTION + EXPANSION
			treePolicy(traversal, root, clone);					
			// SIMULATION
			delta = defaultPolicyA.eval(clone, state.p1Turn);
			//delta = defaultPolicyA.normalize(delta);
			// BACKPROPAGATION
			backupNegaMax(traversal, delta, state.p1Turn);
			rolls++;
			
		}

		System.out.println("MCTS rolls=" + rolls);

	}

	private boolean timeLeft(long start, long min, long max, int minEnds) {
		
		if (System.currentTimeMillis() > start + max)
			return false;
		
		if (System.currentTimeMillis() < start + min || ends < minEnds)
			return true;
		
		return false;
	}

	private void treePolicy(List<AbstractMctsNode> traversal, AbstractMctsNode node, GameState clone) {
		AbstractMctsNode next;
		traversal.add(node);
		while (!clone.isTerminal){
			if (!node.isFullyExpanded()){
				// EXPANSION
				traversal.add(expand(node, clone));
				break;
			} else {
				next = bestChild(node, clone.p1Turn, true);
				if (next == null)
					break;
				node = next;
				traversal.add(node);
				clone.update(node.action);
			}
		}
		return;
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
			if (child.action == SingletonAction.endTurnAction)
				ends++;
			child.setP1(p1);
			addActions(child, clone);
			transTable.put(hash, ((MctsNode)child));
		}
		node.getChildren().add(child);
		return child;
	}

	private void backupNegaMax(List<AbstractMctsNode> traversal, double delta, boolean p1) {
		for(AbstractMctsNode node : traversal){
			node.setVisits(node.getVisits() + 1);
			if (node.isP1() == p1)
				node.setValue(node.getValue() + (delta));
			else
				node.setValue(node.getValue() + (1 - delta));
		}
	}
	
	private void addActions(AbstractMctsNode node, GameState state) {
		state.possibleActions(node.getPossibleActions());
		pruner.prune(node.getPossibleActions(), state);
		comparator.state = state;
		Collections.sort(node.getPossibleActions(), comparator);
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
