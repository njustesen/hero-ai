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
	private int m;
	private RolloutEvaluator rolloutEvaluator;
	private RollingHorizonEvolution evolution;
	private int searchBudget;
	
	public HybridAI(IStateEvaluator evaluator, int searchBudget, int n, RolloutEvaluator rolloutEvaluator, int m, RollingHorizonEvolution evolution) {
		super();
		this.evaluator = evaluator;
		this.moves = new ArrayList<ValuedMove>();
		this.actions = new ArrayList<Action>();
		this.searcher = new BestNMovesSearcher(n);
		this.rolloutEvaluator = rolloutEvaluator;
		this.n = n;
		this.m = m;
		this.searchBudget = searchBudget;
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
		moves = searcher.bestMoves(state, evaluator, searchBudget);
		
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
		
		GameState clone = new GameState(state.map);
		
		for(ValuedMove move : moves){
			clone.imitate(state);
			clone.update(move.ations);
			if (!clone.isTerminal){
				evolution.search(clone);
				if (evolution.actions.isEmpty())
					System.out.println("EMPTY");
				clone.update(evolution.actions);
			}
			move.value = evaluator.eval(clone, state.p1Turn);
		}
		
		Collections.sort(moves);
		List<ValuedMove> newMoves = new ArrayList<ValuedMove>();
		newMoves.addAll(moves.subList(moves.size()-1, moves.size()));
		moves = newMoves;
		
	}

	private void rolloutPhase(GameState state) {
		
		GameState clone = new GameState(null);
		
		for(ValuedMove move : moves){
			clone.imitate(state);
			clone.update(move.ations);
			move.value = rolloutEvaluator.eval(clone, state.p1Turn);
		}
		
		Collections.sort(moves);
		List<ValuedMove> newMoves = new ArrayList<ValuedMove>();
		newMoves.addAll(moves.subList(moves.size()-m, moves.size()));
		moves = newMoves;
		
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
		return "Hybrid";
	}

	@Override
	public AI copy() {
		return new HybridAI(evaluator.copy(), searchBudget, n, (RolloutEvaluator)(rolloutEvaluator.copy()), m, (RollingHorizonEvolution)(evolution.copy()));
	}
	
}
