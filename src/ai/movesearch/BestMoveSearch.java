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
import action.SingletonAction;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;
import ai.util.GameStateHasher;

public class BestMoveSearch {

	Map<String, Integer> transTable = new HashMap<String, Integer>();

	HeuristicEvaluation evalutator = new HeuristicEvaluation();
	ActionPruner pruner = new ActionPruner();
	ObjectPool<GameState> pool;
	ObjectPool<Unit> unitPool;
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IHeuristic heuristic;
	private GameStateHasher hasher = new GameStateHasher();
	
	public List<Action> bestMove(GameState state, ObjectPool<GameState> pool,
			ObjectPool<Unit> unitPool, IHeuristic heuristic) {

		this.heuristic = heuristic;

		transTable.clear();
		bestValue = -1000000;
		bestMove = null;
		try {
			addMoves(state, new ArrayList<Action>(), 0);
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
		
		System.out.println(c + ";" + transTable.keySet().size() + ";" + t + ";"
				+ tt + ";");
	}

	private void addMoves(GameState state, List<Action> move, int depth)
			throws IllegalStateException, UnsupportedOperationException,
			Exception {

		// End turn
		if (state.APLeft == 0){
			final double value = heuristic.eval(state, state.p1Turn);
			if (value > bestValue) {
				bestValue = value;
				final List<Action> nextMove = clone(move);
				nextMove.add(SingletonAction.endTurnAction);
				bestMove = nextMove;
			}
			return;
		}
		
		// Possible actions
		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		GameState next = state.copy();
		
		int i = 0;
		for (final Action action : actions) {
			if (depth == 0)
				System.out.print("|");
			
			if (i>0)
				next.imitate(state);
			next.update(action);
			
			final String hash = hasher.hash(next);
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
