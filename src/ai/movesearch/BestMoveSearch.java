package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.pool.ObjectPools;
import action.Action;
import action.SingletonAction;
import ai.heuristic.HeuristicEvaluator;
import ai.heuristic.IStateEvaluator;
import ai.util.ActionPruner;
import ai.util.GameStateHasher;

public class BestMoveSearch {

	Map<Long, Integer> transTable = new HashMap<Long, Integer>();

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IStateEvaluator heuristic;
	private final GameStateHasher hasher = new GameStateHasher();

<<<<<<< HEAD
	public List<Action> bestMove(GameState state, IStateEvaluator heuristic) {
=======
	public List<Action> bestMove(GameState state, IHeuristic heuristic,
			List<Action> lastMove) {
>>>>>>> ce224fe398055f94b94c8474c31c3dc05e2e61df

		this.heuristic = heuristic;

		transTable.clear();
		// FORCE GC?
		bestValue = -100000000;
		bestMove = null;
<<<<<<< HEAD
		addMoves(state, new ArrayList<Action>(), 0);
=======
		printStats();
		addMoves(state, new ArrayList<Action>(), 0, lastMove);

		// printStats();
>>>>>>> ce224fe398055f94b94c8474c31c3dc05e2e61df

		// printStats();
		if (bestMove == null)
			return new ArrayList<Action>();
		return bestMove;

	}

	private void printStats() {
		int s = 0;
		int t = 0;
<<<<<<< HEAD
		for (final Long l : transTable.keySet())
			if (transTable.get(l) > 1)
				t += transTable.get(l);
			else
				s++;

		System.out.println(s + "\t" + t + "\t" + (double) t
				/ ((double) (t + s)));
	}

	private void addMoves(GameState state, List<Action> move, int depth) {
=======
		int tt = 0;
		for (final String i : transTable.keySet()) {
			c += transTable.get(i);
			if (transTable.get(i) > 1) {
				t++;
				tt += transTable.get(i);
			}
		}

		System.out.println(c + ";" + transTable.keySet().size() + ";" + t + ";"
				+ tt + ";");
	}

	private void addMoves(GameState state, List<Action> move, int depth,
			List<Action> lastMove) {
>>>>>>> ce224fe398055f94b94c8474c31c3dc05e2e61df

		// End turn
		if (state.APLeft == 0) {
			final double value = heuristic.eval(state, state.p1Turn);
			if (value > bestValue) {
				final List<Action> nextMove = clone(move);
				nextMove.add(SingletonAction.endTurnAction);
				bestValue = value;
				bestMove = nextMove;
			}
			// ObjectPools.returnState(state);
			state.returnUnits();
			return;
		}

		// Possible actions
		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);

		// final GameState next = state.copy();
		final GameState next = ObjectPools.borrowState(null);
		next.imitate(state);

		int i = 0;
		for (final Action action : actions) {
			if (depth == 0)
<<<<<<< HEAD
				System.out.print("|");
=======
				System.out.print("|(" + transTable.size() + ")");
>>>>>>> ce224fe398055f94b94c8474c31c3dc05e2e61df

			if (i > 0)
				next.imitate(state);
			next.update(action);

			final long hash = next.hash();
			if (transTable.containsKey(hash))
				transTable.put(hash, transTable.get(hash) + 1);
			else {
				transTable.put(hash, 1);
				final List<Action> nextMove = clone(move);
				nextMove.add(action);
				addMoves(next, nextMove, depth + 1);
			}
			i++;
		}
<<<<<<< HEAD

=======

	}

	private boolean sameMove(List<Action> a, List<Action> b) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		if (a.size() != b.size())
			return false;
		int i = 0;
		for (final Action action : a) {
			if (!action.equals(b.get(i)))
				return false;
			i++;
		}
		return true;
>>>>>>> ce224fe398055f94b94c8474c31c3dc05e2e61df
	}

	private List<Action> clone(List<Action> move) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}