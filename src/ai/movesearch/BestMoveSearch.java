package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Unit;

import org.apache.commons.pool2.ObjectPool;

import action.Action;
import action.SingletonAction;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;
import ai.util.GameStateHasher;

public class BestMoveSearch {

	Map<String, Integer> transTable = new HashMap<String, Integer>();

	HeuristicEvaluation evalutator = new HeuristicEvaluation(false);
	ActionPruner pruner = new ActionPruner();
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IHeuristic heuristic;
	private final GameStateHasher hasher = new GameStateHasher();

	public List<Action> bestMove(GameState state, ObjectPool<GameState> pool,
			ObjectPool<Unit> unitPool, IHeuristic heuristic, List<Action> lastMove) {

		this.heuristic = heuristic;

		transTable.clear();
		bestValue = -1000000;
		bestMove = null;
		try {
			addMoves(state, new ArrayList<Action>(), 0, lastMove);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		printStats();

		return bestMove;

	}

	private void printStats() {
		int c = 0;
		int t = 0;
		int tt = 0;
		for (final String i : transTable.keySet()) {
			c += transTable.get(i);
			if (transTable.get(i) > 1) {
				t++;
				tt += transTable.get(i);
			}
		}

		//System.out.println(c + ";" + transTable.keySet().size() + ";" + t + ";" + tt + ";");
	}

	private void addMoves(GameState state, List<Action> move, int depth, List<Action> lastMove)
			throws IllegalStateException, UnsupportedOperationException,
			Exception {

		// End turn
		if (state.APLeft == 0) {
			final double value = heuristic.eval(state, state.p1Turn);
			if (value > bestValue){
				final List<Action> nextMove = clone(move);
				nextMove.add(SingletonAction.endTurnAction);
				if (!sameMove(nextMove, lastMove)) {
					bestValue = value;
					bestMove = nextMove;
				}
			}
			return;
		}

		// Possible actions
		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		
		final GameState next = state.copy();

		int i = 0;
		for (final Action action : actions) {
			//if (depth == 0)
			//	System.out.print("|");

			if (i > 0)
				next.imitate(state);
			next.update(action);

			final String hash = hasher.hash(next);
			if (transTable.containsKey(hash))
				transTable.put(hash, transTable.get(hash) + 1);
			else {
				transTable.put(hash, 1);
				final List<Action> nextMove = clone(move);
				nextMove.add(action);
				addMoves(next, nextMove, depth + 1, lastMove);
			}
			i++;
		}

	}

	private boolean sameMove(List<Action> a, List<Action> b) {
		if (a==b)
			return true;
		if (a == null || b == null)
			return false;
		if (a.size() != b.size())
			return false;
		int i = 0;
		for(Action action : a){
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