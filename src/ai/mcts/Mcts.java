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
import ai.util.ComplexActionComparator;
import ai.util.GameStateHasher;

public class Mcts implements AI {

	Map<String, MctsNode> transTable = new HashMap<String, MctsNode>();

	public double c;
	public long budget;
	public IHeuristic defaultPolicy;
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private MctsNode root;
	private GameStateHasher hasher;
	private List<Action> move;
	private int ends;

	public Mcts(long budget, double c, IHeuristic defaultPolicy) {
		this.budget = budget;
		this.defaultPolicy = defaultPolicy;
		this.pruner = new ActionPruner();
		this.comparator = new ComplexActionComparator();
		this.hasher = new GameStateHasher();
		this.move = new ArrayList<Action>();
		this.ends = 0;
		this.c = c;
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
		root = new MctsNode(actions(state));
		
		// Start search
		final GameState clone = state.copy();
		int rolls = 0;
		List<MctsEdge> traversal = new ArrayList<MctsEdge>();
		MctsNode node = null;
		double delta = 0;
		boolean collapsed = false;
		int startAp = state.APLeft;
		int ap = startAp;
		long time = (start + budget) - System.currentTimeMillis();
		while (time > 0) {
			
			// COLLAPSE
			/*
			if (!collapsed && ends >= 100){
				System.out.println("COLLAPSE!");
				collapse(root);
				collapsed = true;
				//System.out.println(root.toXml(0));
			}
			*/
			
			// CUT
			if (time < (budget/startAp)*(ap-1)){
				//System.out.println(root.toXml(0));
				cut(root, null, 0, startAp-ap);
				System.out.println("Cut");
				//System.out.println(root.toXml(0));
				ap--;
			}
			
			traversal.clear();
			//if (rolls%1000==0)
			//	System.out.println(root.toXml(0));
			clone.imitate(state);
			// SELECTION + EXPANSION
			node = treePolicy(root, clone, traversal);					
			// SIMULATION
			delta = defaultPolicy.eval(clone, state.p1Turn);
			if (delta > 0)
				delta = 1;
			else if (delta < 0)
				delta = 0;
			else if (delta == 0)
				delta = 0.5;
			
			//delta = defaultPolicy.normalize(delta);
			// BACKPROPAGATION
			backupNegaMax(traversal, delta, state.p1Turn);
			
			//if (rolls % 100 == 0)
			//	System.out.println(root.toXml(0));
			time = (start + budget) - System.currentTimeMillis();
			rolls++;
		}

		System.out.println("Rolls=" + rolls);
		//System.out.println(root.toXml(0));
		
		// Save best move

		//System.out.println(root.toXml(0));
		
		move = bestMove(state, rolls);
		Action action = move.get(0);
		move.remove(0);
		
		// Reset search
		root = null;
		transTable.clear();
		this.ends = 0;
		
		return action;
		
	}

	private void cut(MctsNode node, MctsEdge from, int depth, int cut) {
		if (node == null)
			return;
		if (depth == cut){
			MctsEdge best = best(node, false);
			node.out.clear();
			node.possible.clear();
			node.out.add(best);
			node.in.clear();
			if (from != null)
				node.in.add(from);
		} else {	
			for(MctsEdge edge : node.out)
				cut(edge.to, edge, depth+1, cut);	
		}
	}

	private MctsEdge best(MctsNode node, boolean urgent) {
		double bestVal = -100000;
		MctsEdge bestEdge = null;
		for(MctsEdge edge : node.out){
			double val = uct(edge, node, urgent);
			if (val > bestVal){
				bestVal = val;
				bestEdge = edge;
			}
		}
		
		return bestEdge;
	}

	private double uct(MctsEdge edge, MctsNode node, boolean urgent) {
		
		if (urgent){
			return edge.avg() + 
					2 * c * Math.sqrt((2 * Math.log(node.visits)) / (edge.visits));
		}
		return edge.avg();
		
	}

	private boolean collapse(MctsNode node) {
		
		List<MctsEdge> remove = new ArrayList<MctsEdge>();
		boolean end = false;
		
		for(MctsEdge edge : node.out){
			if (edge.action == SingletonAction.endTurnAction)
				return true;
			if (edge.to != null || edge.to.isLeaf())
				remove.add(edge);
			else {
				boolean resultsInEnd = collapse(edge.to);
				if (!resultsInEnd)
					remove.add(edge);
				else
					end = true;
			}
		}
		
		node.out.removeAll(remove);
		
		return end;
		
	}

	private MctsNode treePolicy(MctsNode node, GameState clone, List<MctsEdge> traversal) {
		MctsEdge edge = null;
		while (!clone.isTerminal){
			if (!node.isFullyExpanded()){
				// EXPANSION
				edge = expand(node, clone);
				traversal.add(edge);
				return edge.to;
			} else {
				edge = best(node, true);
				if (edge == null || edge.isLeaf())
					break;
				node = edge.to;
				traversal.add(edge);
				clone.update(edge.action);
			}
		}
		return node;
	}

	private MctsEdge expand(MctsNode node, GameState clone) {
		MctsEdge edge = node.nextEdge(clone.p1Turn);
		node.out.add(edge);
		clone.update(edge.action);
		final String hash = hasher.hash(clone);
		MctsNode result = null;
		if (transTable.containsKey(hash)) {
			result = transTable.get(hash);
		} else {
			result = new MctsNode(actions(clone));
			if (edge.action == SingletonAction.endTurnAction)
				ends++;
			transTable.put(hash, result);
		}
		result.in.add(edge);
		edge.to = result;
		return edge;
	}

	private void backupNegaMax(List<MctsEdge> traversal, double delta, boolean p1) {
		for(MctsEdge edge : traversal){
			edge.visits++;
			if (edge.to != null)
				edge.to.visits++;
			if (edge.from != null && edge.from.isRoot())
				edge.from.visits++;
			if (edge.p1 == p1)
				edge.value += delta;
			else
				edge.value += (1 - delta);
			/*
			if (edge.p1 == p1)
				edge.value += delta;
			else
				edge.value -= delta;
			*/
		}
	}
	
	private List<Action> actions(GameState state) {
		List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		comparator.state = state;
		Collections.sort(actions, comparator);
		return actions;
	}

	private List<Action> bestMove(GameState state, int rolls) {
		MctsEdge edge = best(root, false);
		List<Action> move = new ArrayList<Action>();
		if (edge == null){
			move.add(SingletonAction.endTurnAction);
			return move;
		}
		while(edge.action != SingletonAction.endTurnAction){
			move.add(edge.action);
			if (edge.isLeaf())
				break;
			edge = best(edge.to, false);
			if (edge == null || edge.isLeaf())
				break;
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
