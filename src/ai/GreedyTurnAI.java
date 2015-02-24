package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.heuristic.IHeuristic;
import ai.movesearch.BestMoveSearch;

public class GreedyTurnAI implements AI {

	private final BestMoveSearch searcher = new BestMoveSearch();
	private List<Action> actions;
	private final IHeuristic heuristic;
	private final List<Action> lastMove;

	public GreedyTurnAI(IHeuristic heuristic) {
		super();
		this.heuristic = heuristic;
		lastMove = new ArrayList<Action>();
		actions = new ArrayList<Action>();
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}

		// List<List<Action>> possibleActions = searcher.possibleMoves(state);
		// System.out.println("GTAI: Searching for possible moves.");

		// actions = searcher.bestMove(state, pool, unitPool, heuristic);
		// actions = searcher.bestMove(state, pool, null, heuristic);
		// long start = System.currentTimeMillis();
		actions = searcher.bestMove(state, heuristic, lastMove);
		// System.out.println(System.currentTimeMillis() - start);
		lastMove.clear();
		if (actions == null || actions.isEmpty())
			return SingletonAction.endTurnAction;
		lastMove.addAll(actions);
		// System.out.println(actions);

		final Action action = actions.get(0);
		actions.remove(0);
		return action;

	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
