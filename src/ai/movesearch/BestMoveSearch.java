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
	private IStateEvaluator evaluator;
	private final GameStateHasher hasher = new GameStateHasher();

	public List<Action> bestMove(GameState state, IStateEvaluator evaluator) {
		this.evaluator = evaluator;

		transTable.clear();
		// FORCE GC?
		bestValue = -100000000;
		bestMove = null;
		addMoves(state, new ArrayList<Action>(), 0);
		//printStats();
		if (bestMove == null)
			return new ArrayList<Action>();
		return bestMove;

	}

	private void printStats() {
		int s = 0;
		int t = 0;
		for (final Long l : transTable.keySet())
			if (transTable.get(l) > 1)
				t += transTable.get(l);
			else
				s++;

		System.out.println(s + "\t" + t + "\t" + (double) t
				/ ((double) (t + s)));
	}

	private void addMoves(GameState state, List<Action> move, int depth) {

		// End turn
		if (state.APLeft == 0) {
			final double value = evaluator.eval(state, state.p1Turn);
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
				System.out.print("|");

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
	}

	private List<Action> clone(List<Action> move) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}