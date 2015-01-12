package ai;

import evaluate.GameStateEvaluator;
import game.AI;
import game.GameState;

import java.util.ArrayList;
import java.util.List;

import action.Action;

public class GreedyActionAI implements AI {

	private final List<Action> actions;

	public GreedyActionAI() {
		super();
		actions = new ArrayList<Action>();
	}

	@Override
	public Action act(GameState state, long ms) {

		final GameStateEvaluator evaluator = new GameStateEvaluator();

		Action best = null;
		double bestValue = -100000000;

		actions.clear();
		state.possibleActions(actions);
		for (final Action action : actions) {

			final GameState next = state.copy();
			next.update(action);
			final double val = evaluator.eval(next, state.p1Turn);

			if (val > bestValue) {
				bestValue = val;
				best = action;
			}

		}

		return best;
	}

}
