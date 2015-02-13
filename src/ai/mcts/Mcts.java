package ai.mcts;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import action.Action;
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
	private List<Action> move;
	private int ends;

	public Mcts(long budget, ITreePolicy treePolicy, IHeuristic defaultPolicy) {
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

		final long start = System.currentTimeMillis();

		// If move already found return next action
		if (!move.isEmpty()){
			Action action = move.get(0);
			move.remove(0);
			return action;
		}

		// Create root
		root = new MctsNode(null, null);
		addActions(root, state);
		
		// Start search
		final GameState clone = state.copy();
		int rolls = 0;
		List<AbstractMctsNode> traversal = new ArrayList<AbstractMctsNode>();
		double delta = 0;
		boolean collapsed = false;
		int startAp = state.APLeft;
		int ap = startAp;
		long time = (start + budget) - System.currentTimeMillis();
		while (time > 0) {
			/*
			if (!collapsed && ends >= 100){
				System.out.println("COLLAPSE!");
				collapse(root);
				collapsed = true;
				//System.out.println(root.toXml(0));
			}
			*/
			
			if (time < (budget/startAp)*(ap-1)){
				//System.out.println(root.toXml(0));
				cut(root, 0, startAp-ap);
				System.out.println("Cut");
				//System.out.println(root.toXml(0));
				ap--;
			}
			
			traversal.clear();
			
			clone.imitate(state);
			// SELECTION + EXPANSION
			treePolicy(traversal, root, clone);					
			// SIMULATION
			delta = defaultPolicy.eval(clone, state.p1Turn);
			//delta = defaultPolicy.normalize(delta);
			// BACKPROPAGATION
			backupNegaMax(traversal, delta, state.p1Turn);
			rolls++;
			
			//if (rolls % 100 == 0)
			//	System.out.println(root.toXml(0));
			time = (start + budget) - System.currentTimeMillis();
		}

		//System.out.println("Rolls=" + rolls);
		//System.out.println(root.toXml(0));
		
		// Save best move
		move = bestMove(state);
		Action action = move.get(0);
		move.remove(0);
		
		// Reset search
		root = null;
		transTable.clear();
		this.ends = 0;
		
		return action;
		
	}

	private void cut(AbstractMctsNode node, int depth, int cut) {
		if (depth == cut){
			AbstractMctsNode best = bestChild(node, node.isP1(), false);
			node.getChildren().clear();
			node.getChildren().add(best);
			node.getPossibleActions().clear();
		} else {	
			for(AbstractMctsNode child : node.getChildren()){
				cut(child, depth+1, cut);	
			}
		}
	}

	private boolean collapse(AbstractMctsNode node) {
		
		List<AbstractMctsNode> remove = new ArrayList<AbstractMctsNode>();
		boolean end = false;
		
		for(AbstractMctsNode child : node.getChildren()){
			if (child.action == SingletonAction.endTurnAction)
				return true;
			if (node.isLeaf())
				remove.add(child);
			else {
				boolean cEnd = collapse(child);
				if (!cEnd)
					remove.add(child);
				else
					end = true;
			}
		}
		
		node.getPossibleActions().clear();
		node.getChildren().removeAll(remove);
		
		return end;
		
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
			if (child.getAction() == SingletonAction.endTurnAction)
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
	
	private void addActions(AbstractMctsNode node, GameState state) {
		state.possibleActions(node.getPossibleActions());
		pruner.prune(node.getPossibleActions(), state);
		comparator.state = state;
		Collections.sort(node.getPossibleActions(), comparator);
	}

	private List<Action> bestMove(GameState state) {
		AbstractMctsNode node = bestChild(root, state.p1Turn, false);
		List<Action> move = new ArrayList<Action>();
		//System.out.println(node);
		//System.out.println(node.action);
		if (node == null){
			move.add(SingletonAction.endTurnAction);
			System.out.println("Turn end error");
			return move;
		}
		while(node.action != SingletonAction.endTurnAction){
			move.add(node.action);
			if (node.getChildren().isEmpty())
				break;
			node = bestChild(node, state.p1Turn, false);
		}
		move.add(SingletonAction.endTurnAction);
		return move;
	}
	
	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
