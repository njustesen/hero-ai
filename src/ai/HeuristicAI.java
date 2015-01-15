package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.util.ActionComparator;
import ai.util.ActionPruner;

public class HeuristicAI implements AI {

	private final List<Action> actions;
	private final ActionComparator comparator;
	ActionPruner pruner;

	public HeuristicAI() {
		actions = new ArrayList<Action>();
		pruner = new ActionPruner();
		comparator = new ActionComparator();
	}

	@Override
	public Action act(GameState state, long ms) {
		actions.clear();
		state.possibleActions(actions);
		pruner.prune(actions, state);

		// End turn
		if (actions.isEmpty())
			return SingletonAction.endTurnAction;

		comparator.state = state;
		Collections.sort(actions, comparator);

		comparator.state = null;

		return actions.get(0);

	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
