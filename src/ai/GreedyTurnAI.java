package ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import action.Action;
import ai.util.GameStateFactory;
import ai.util.IHeuristic;
import ai.util.RecMoveSearch;
import game.GameState;

public class GreedyTurnAI implements AI {

	private final RecMoveSearch searcher = new RecMoveSearch();
	private List<Action> actions = new ArrayList<Action>();
	private final ObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
	private IHeuristic heuristic;
	
	public GreedyTurnAI(IHeuristic heuristic) {
		super();
		this.heuristic = heuristic;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}

		// List<List<Action>> possibleActions = searcher.possibleMoves(state);
		System.out.println("GTAI: Searching for possible moves.");

		actions = best(state, searcher.possibleMoves(state, pool));
		System.out.println(actions);
		
		final Action action = actions.get(0);
		actions.remove(0);
		return action;

	}

	private List<Action> best(GameState state, List<List<Action>> possibleMoves) {
		System.out.println("GTAI: Evaluation " + possibleMoves.size()
				+ " moves.");
		double bestValue = -10000000;
		List<Action> bestMove = null;
		for (final List<Action> move : possibleMoves) {
			final double value = evaluateMove(state, move);
			if (value > bestValue || bestMove == null) {
				bestMove = move;
				bestValue = value;
			}
		}
		System.out.println("GTAI: Best move found : " + bestMove);
		System.out.println("GTAI: Value : " + bestValue);
		return bestMove;
	}

	private double evaluateMove(GameState state, List<Action> move) {

		int i = 0;
		final GameState clone = state.copy();
		while (i < move.size()) {
			clone.update(move.get(i));
			i++;
		}

		return heuristic.eval(clone, state.p1Turn);

	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
