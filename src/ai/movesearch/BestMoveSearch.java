package ai.movesearch;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import model.HAMap;
import model.Unit;

import org.apache.commons.pool2.ObjectPool;

import action.Action;
import action.EndTurnAction;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;

public class BestMoveSearch {

	Map<String, Integer> transTable = new HashMap<String, Integer>();

	HeuristicEvaluation evalutator = new HeuristicEvaluation();
	ActionPruner pruner = new ActionPruner();
	ObjectPool<GameState> pool;
	ObjectPool<Unit> unitPool;
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IHeuristic heuristic;

	public List<Action> bestMove(GameState state, ObjectPool<GameState> pool,
			ObjectPool<Unit> unitPool, IHeuristic heuristic) {

		this.pool = pool;
		this.unitPool = unitPool;
		this.heuristic = heuristic;

		transTable.clear();
		bestValue = -1000000;
		bestMove = null;
		try {
			addMoves(state, new ArrayList<Action>(), 0);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
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
		/*
		 * System.out.println("States=" + c);
		 * System.out.println("Unique states=" + transTable.keySet().size());
		 * System.out.println("Unique transposition=" + t);
		 * System.out.println("Transpositions=" + tt);
		 */
		System.out.println(c + ";" + transTable.keySet().size() + ";" + t + ";"
				+ tt + ";");
	}

	private void addMoves(GameState state, List<Action> move, int depth)
			throws IllegalStateException, UnsupportedOperationException,
			Exception {

		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		final int i = 0;
		for (final Action action : actions) {
			if (depth == 0)
				System.out.print("|");
			// System.out.println(i++ + "/" + actions.size());
			final GameState next = borrowObject();
			next.unitPool = unitPool;
			next.imitate(state);
			next.update(action);

			// if (next.APLeft == state.APLeft)
			// continue; // Nothing happened
			final List<Action> nextMove = clone(move);
			nextMove.add(action);
			if (depth < 5 && !(action instanceof EndTurnAction)) {
				final String hash = next.hash();
				if (transTable.containsKey(hash))
					transTable.put(hash, transTable.get(hash) + 1);
				else {
					transTable.put(hash, 1);
					addMoves(next, nextMove, depth + 1);
				}
				// addMoves(next, nextMove, depth + 1);
			} else {
				final double value = heuristic.eval(next, state.p1Turn);
				if (value > bestValue) {
					bestValue = value;
					bestMove = nextMove;
				}
			}
			// next.reset();
			// next.returnUnits();
			if (pool != null)
				pool.returnObject(next);
		}

	}

	private GameState borrowObject() throws NoSuchElementException,
			IllegalStateException, Exception {
		if (pool == null)
			return new GameState(HAMap.mapA);

		return pool.borrowObject();
	}

	private List<Action> clone(List<Action> move) {
		final List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}
