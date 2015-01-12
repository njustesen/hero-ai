package ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import action.Action;
import action.SingletonAction;
import ai.util.GameStateFactory;
import ai.util.MoveSearch;
import evaluate.GameStateEvaluator;
import game.GameState;

public class GreedyTurnAI implements AI {

	GameStateEvaluator evalutator = new GameStateEvaluator();
	MoveSearch searcher = new MoveSearch();
	List<Action> actions = new ArrayList<Action>();
	ObjectPool<GameState> pool = new GenericObjectPool<GameState>(
			new GameStateFactory());

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
		actions.add(SingletonAction.endTurnAction);
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
		int lastAP = 0;
		final GameState clone = state.copy();
		while (clone.APLeft > 0 && lastAP != clone.APLeft) {
			lastAP = clone.APLeft;
			clone.update(move.get(i));
			i++;
		}

		return evalutator.eval(clone, clone.p1Turn);

	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
