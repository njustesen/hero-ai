package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;

public class GreedyActionAI implements AI {

	private final List<Action> actions;
	private final IHeuristic heuristic;
	private final ActionPruner pruner;

	public GreedyActionAI(IHeuristic heuristic) {
		super();
		this.heuristic = heuristic;
		actions = new ArrayList<Action>();
		this.pruner = new ActionPruner();
	}

	@Override
	public Action act(GameState state, long ms) {

		Action best = null;
		double bestValue = -100000000;

		actions.clear();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		Collections.shuffle(actions);
		for (final Action action : actions) {

			final GameState next = state.copy();
			double val = 0.0;
			next.update(action);
			val = heuristic.eval(next, state.p1Turn);
			
			if (val > bestValue) {
				bestValue = val;
				best = action;
			}

		}

		return best;
	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
