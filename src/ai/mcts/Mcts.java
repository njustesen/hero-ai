package ai.mcts;

import game.GameState;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import action.Action;
import action.SingletonAction;
import ai.AI;
import ai.heuristic.IStateEvaluator;
import ai.util.ActionComparator;
import ai.util.ActionPruner;
import ai.util.ComplexActionComparator;

public class Mcts implements AI {

	Map<Long, MctsNode> transTable = new HashMap<Long, MctsNode>();

	public double c;
	public long budget;
	public IStateEvaluator defaultPolicy;
	public boolean cut;
	public boolean collapse;
	
	public List<Double> depths;
	public List<Double> rollouts;
	
	private final ActionPruner pruner;
	private final ActionComparator comparator;
	private MctsNode root;
	private List<Action> move;
	private int ends;
	

	public Mcts(long budget, IStateEvaluator defaultPolicy) {
		this.budget = budget;
		this.defaultPolicy = defaultPolicy;
		pruner = new ActionPruner();
		comparator = new ComplexActionComparator();
		move = new ArrayList<Action>();
		ends = 0;
		c = 1 / Math.sqrt(2);
		cut = false;
		collapse = false;
		depths = new ArrayList<Double>();
		rollouts = new ArrayList<Double>();
	}

	@Override
	public Action act(GameState state, long ms) {

		final long start = System.currentTimeMillis();

		// If move already found return next action
		if (!move.isEmpty()) {
			final Action action = move.get(0);
			move.remove(0);
			return action;
		}

		// Create root
		root = new MctsNode(actions(state));

		// Start search
		final GameState clone = state.copy();
		int rolls = 0;
		final List<MctsEdge> traversal = new ArrayList<MctsEdge>();
		double delta = 0;
		boolean collapsed = false;
		final int startAp = state.APLeft;
		int ap = startAp;
		long time = (start + budget) - System.currentTimeMillis();
		while (time > 0) {

			// COLLAPSE
			if (collapse && !collapsed && ends >= 20 * (budget / 1000.0)) {
				collapse(root);
				collapsed = true;
			}

			// CUT
			if (cut && time < (budget / startAp) * (ap - 1)) {
				cut(root, null, 0, startAp - ap);
				ap--;
			}

			traversal.clear();
			clone.imitate(state);

			// SELECTION + EXPANSION
			treePolicy(root, clone, traversal);

			// SIMULATION
			delta = defaultPolicy.eval(clone, state.p1Turn);

			// BACKPROPAGATION
			backupNegaMax(traversal, delta, state.p1Turn);

			time = (start + budget) - System.currentTimeMillis();
			rolls++;
		}

		root.depth(0, depths, new HashSet<MctsNode>());
		rollouts.add((double)rolls);
		
		//saveTree();
		
		move = bestMove(state, rolls);
		final Action action = move.get(0);
		move.remove(0);

		// Reset search
		root = null;
		transTable.clear();
		ends = 0;

		return action;

	}

	private void saveTree() {
		PrintWriter out = null; 
		try { 
			out = new PrintWriter("mcts.xml");
			out.print(root.toXml(0, new HashSet<MctsNode>(), 6)); 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} finally { 
			if (out!= null) 
				out.close(); 
		}
	}

	private void cut(MctsNode node, MctsEdge from, int depth, int cut) {
		if (node == null)
			return;
		if (depth == cut) {
			final MctsEdge best = best(node, false);
			node.out.clear();
			node.possible.clear();
			if (best != null)
				node.out.add(best);
			node.in.clear();
			if (from != null)
				node.in.add(from);
		} else
			for (final MctsEdge edge : node.out)
				cut(edge.to, edge, depth + 1, cut);
	}

	private MctsEdge best(MctsNode node, boolean urgent) {
		double bestVal = -100000;
		MctsEdge bestEdge = null;
		for (final MctsEdge edge : node.out) {
			final double val = uct(edge, node, urgent);
			if (val > bestVal) {
				bestVal = val;
				bestEdge = edge;
			}
		}

		return bestEdge;
	}

	private double uct(MctsEdge edge, MctsNode node, boolean urgent) {

		if (urgent)
			return edge.avg() + 2 * c
					* Math.sqrt((2 * Math.log(node.visits)) / (edge.visits));
		return edge.avg();

	}

	private boolean collapse(MctsNode node) {

		final List<MctsEdge> remove = new ArrayList<MctsEdge>();
		boolean end = false;

		for (final MctsEdge edge : node.out) {
			if (edge.action == SingletonAction.endTurnAction)
				return true;
			if (edge.to != null || edge.to.isLeaf())
				remove.add(edge);
			else {
				final boolean resultsInEnd = collapse(edge.to);
				if (!resultsInEnd)
					remove.add(edge);
				else
					end = true;
			}
		}

		node.out.removeAll(remove);

		return end;

	}

	private MctsNode treePolicy(MctsNode node, GameState clone,
			List<MctsEdge> traversal) {

		MctsEdge edge = null;
		while (!clone.isTerminal){
			if (traversal.size() > 100)
				return node;
			if (!node.isFullyExpanded()) {
				// EXPANSION
				edge = expand(node, clone);
				if (edge == null)
					return node;
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
		final MctsEdge edge = node.nextEdge(clone.p1Turn);
		node.out.add(edge);
		final int ap = clone.APLeft;
		clone.update(edge.action);
		if (ap == clone.APLeft) {
			// System.out.print("!");
			node.out.remove(edge);
			return null;
		}
		final Long hash = clone.hash();
		MctsNode result = null;
		if (transTable.containsKey(hash))
			result = transTable.get(hash);
		else {
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
		for (final MctsEdge edge : traversal) {
			edge.visits++;
			if (edge.to != null)
				edge.to.visits++;
			if (edge.from != null && edge.from.isRoot())
				edge.from.visits++;
			if (edge.p1 == p1)
				edge.value += delta;		// MAX
			else
				edge.value += (1 - delta);	// MIN
		}
	}

	private List<Action> actions(GameState state) {
		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		comparator.state = state;
		Collections.sort(actions, comparator);
		return actions;
	}

	private List<Action> bestMove(GameState state, int rolls) {
		MctsEdge edge = best(root, false);
		final List<Action> move = new ArrayList<Action>();
		if (edge == null) {
			move.add(SingletonAction.endTurnAction);
			return move;
		}
		while (edge.action != SingletonAction.endTurnAction) {
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
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}

	@Override
	public String header() {
		String name = "MCTS\n";
		name += "Time budget = " + budget + "\n";
		name += "C = " + c + "\n";
		name += "Leaf evaluation = " + defaultPolicy.title() + "\n";
		name += "Cut = " + cut + "\n";
		name += "Collapse = " + collapse + "\n";
		return name;
	}

	@Override
	public String title() {
		return "MCTS";
	}

}
