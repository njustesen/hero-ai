package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.util.ActionComparator;
import ai.util.ActionPruner;
import ai.util.ComplexActionComparator;

public class HeuristicAI implements AI {

	private final List<Action> actions;
	private final ActionComparator comparator;
	ActionPruner pruner;

	public HeuristicAI() {
		this.actions = new ArrayList<Action>();
		this.pruner = new ActionPruner();
		this.comparator = new ComplexActionComparator();
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

		return actions.get(0);

	}

	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		return name;
	}

	@Override
	public String title() {
		return "Heuristic";
	}

}
