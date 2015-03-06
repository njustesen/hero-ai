package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import action.Action;
import action.SingletonAction;
import ai.evaluation.IStateEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evolution.RollingHorizonEvolution;
import ai.movesearch.BestNMovesSearcher;
import ai.movesearch.ValuedMove;

public class HybridAI implements AI {

	private final BestNMovesSearcher searcher;
	private List<Action> actions;
	private List<ValuedMove> moves;
	private final IStateEvaluator evaluator;
	private int n;
	private RolloutEvaluator rolloutEvaluator;
	private int rolls;
	private int m;
	private RollingHorizonEvolution evolution;
	
	public HybridAI(IStateEvaluator evaluator, int n, RolloutEvaluator rolloutEvaluator, int rolls, int m, RollingHorizonEvolution evolution) {
		super();
		this.evaluator = evaluator;
		this.moves = new ArrayList<ValuedMove>();
		this.actions = new ArrayList<Action>();
		this.searcher = new BestNMovesSearcher(n);
		this.rolloutEvaluator = rolloutEvaluator;
		this.n = n;
		this.m = m;
		this.rolls = rolls;
		this.evolution = evolution;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
		
		// 1. GreedySearch
		moves = searcher.bestMoves(state, evaluator);
		
		// 2. Rollout phase
		rolloutPhase(state);
		
		// 3. Rolling horizon evolution
		rollingPhase(state);
		actions = moves.get(0).ations;
		
		if (actions == null || actions.isEmpty())
			return SingletonAction.endTurnAction;
		
		final Action action = actions.get(0);
		actions.remove(0);
		
		return action;

	}

	private void rollingPhase(GameState state) {
		
		GameState p1Turn = new GameState(null);
		GameState p2Turn = new GameState(null);
		
		for(ValuedMove move : moves){
			p1Turn.imitate(state);
			p1Turn.update(move.ations);
			evolution.act(p2Turn, -1);
			move.value = evaluator.eval(p2Turn, state.p1Turn);
		}
		
		Collections.sort(moves);
		moves = moves.subList(moves.size()-1, moves.size());
		
	}

	private void rolloutPhase(GameState state) {
		
		GameState p1Turn = new GameState(null);
		GameState p2Turn = new GameState(null);
		
		for(ValuedMove move : moves){
			p1Turn.imitate(state);
			p1Turn.update(move.ations);
			for(int i = 0; i < rolls; i++){
				p2Turn.imitate(p1Turn);
				move.value = rolloutEvaluator.eval(p1Turn, state.p1Turn);
			}
		}
		
		Collections.sort(moves);
		moves = moves.subList(moves.size()-m, moves.size());
		
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
		return new HybridAI(evaluator.copy(), n, (RolloutEvaluator)(rolloutEvaluator.copy()), rolls, m, (RollingHorizonEvolution)(evolution.copy()));
	}
	
}
