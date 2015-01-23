package ai.heuristic;

import java.util.NoSuchElementException;

import ai.AI;
import game.Game;
import game.GameState;

public class RolloutEvalutation implements IHeuristic {

	int rolls;
	int depth;	// In turns
	AI policy;
	IHeuristic heuristic;
	
	public RolloutEvalutation(int rolls, int depth, AI policy, IHeuristic heuristic) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.heuristic = heuristic;
		this.policy = policy;
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		double sum = 0;
		for(int i = 0; i < rolls; i++){
			sum += simulateGame(state.copy(), p1);
		}
		
		return sum;
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
