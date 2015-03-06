package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.movesearch.BestMoveSearcher;

public class GreedyTurnAI implements AI {

	private final BestMoveSearcher searcher;
	private List<Action> actions;
	private final IStateEvaluator evaluator;
	
	public List<Double> moves;
	private boolean anytime;
	public int budget;

	public GreedyTurnAI(IStateEvaluator evaluator) {
		super();
		this.evaluator = evaluator;
		this.actions = new ArrayList<Action>();
		this.moves = new ArrayList<Double>();
		this.searcher = new BestMoveSearcher();
		this.anytime = false;
		this.budget = -1;
	}
	
	public GreedyTurnAI(IStateEvaluator evaluator, int budget) {
		super();
		this.evaluator = evaluator;
		this.actions = new ArrayList<Action>();
		this.moves = new ArrayList<Double>();
		this.searcher = new BestMoveSearcher();
		this.anytime = false;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		
		if(!anytime)
			actions = searcher.bestMove(state, evaluator, budget);
		
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

	@Override
	public AI copy() {
		return new GreedyTurnAI(evaluator.copy());
	}
	
}
