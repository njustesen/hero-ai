package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.pool.ObjectPools;
import action.Action;
import action.SingletonAction;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.IStateEvaluator;
import ai.util.ActionPruner;

public class BestMoveSearch {

	Map<Long, Integer> transTable = new HashMap<Long, Integer>();

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IStateEvaluator evaluator;
	public int moves;

	public List<Action> bestMove(GameState state, IStateEvaluator evaluator) {
		this.evaluator = evaluator;

		bestValue = -100000000;
		bestMove = null;
		moves = 0;
		addMoves(state, new ArrayList<Action>(), 0);
		transTable.clear();
		if (bestMove == null)
			return new ArrayList<Action>();
		return bestMove;

	}

	private void addMoves(GameState state, List<Action> move, int depth) {

		// End turn
		if (state.APLeft == 0 || move.size() > 5) {
			moves++;
			final double value = evaluator.eval(state, state.p1Turn);
			if (value > bestValue) {
				final List<Action> nextMove = clone(move);
				nextMove.add(SingletonAction.endTurnAction);
				bestValue = value;
				bestMove = nextMove;
			}
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
			//if (depth == 0)
			//	System.out.print("|");

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

	private List<Action> clone(List<Action> move) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}