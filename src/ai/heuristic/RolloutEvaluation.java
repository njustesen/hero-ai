package ai.heuristic;

import ai.AI;
import game.GameState;

public class RolloutEvaluation implements IHeuristic {

	int rolls;
	int depth;	// In turns
	AI policy;
	IHeuristic heuristic;
	boolean copy;
	
	public RolloutEvaluation(int rolls, int depth, AI policy, IHeuristic heuristic, boolean copy) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.heuristic = heuristic;
		this.policy = policy;
		this.copy = copy;
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (!copy)
			return simulateGame(state, p1);
		
		double sum = 0;
		for(int i = 0; i < rolls; i++){
			sum += simulateGame(state.copy(), p1);
		}
		
		return sum / rolls;
	}
	
	private double simulateGame(GameState state, boolean p1) {
		int turns = state.turn;
		while (!state.isTerminal && state.turn < turns + depth){
			if (state.p1Turn)
				state.update(policy.act(state, -1));
			else
				state.update(policy.act(state, -1));
			
		}
		return heuristic.eval(state, p1);
	}

}
