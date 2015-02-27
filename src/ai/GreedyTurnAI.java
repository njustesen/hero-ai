package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import util.Statistics;
import action.Action;
import action.SingletonAction;
import ai.heuristic.IStateEvaluator;
import ai.movesearch.BestMoveSearch;
import ai.util.AiStatistics;

public class GreedyTurnAI implements AI {

	private final BestMoveSearch searcher = new BestMoveSearch();
	private List<Action> actions;
	private final IStateEvaluator evaluator;
	private final List<Action> lastMove;
	
	public List<Double> moves;

	public GreedyTurnAI(IStateEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		lastMove = new ArrayList<Action>();
		actions = new ArrayList<Action>();
		this.moves = new ArrayList<Double>();
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
		actions = searcher.bestMove(state, evaluator);
		moves.add((double)searcher.moves);
		
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
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "State evaluatior = " + evaluator.title() + "\n";
		return name;
	}

	@Override
	public String title() {
		return "GreedyTurn";
	}
	
}
