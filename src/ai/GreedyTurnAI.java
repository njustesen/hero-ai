package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.movesearch.BestMoveSearch;

public class GreedyTurnAI implements AI {

	private final BestMoveSearch searcher;
	private List<Action> actions;
	private final IStateEvaluator evaluator;
	
	public List<Double> moves;

	public GreedyTurnAI(IStateEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		actions = new ArrayList<Action>();
		this.moves = new ArrayList<Double>();
		this.searcher = new BestMoveSearch();
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		
		actions = searcher.bestMove(state, evaluator);
		moves.add((double)searcher.moves);
		
		if (actions == null || actions.isEmpty())
			return SingletonAction.endTurnAction;
		
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
