package ai;

import evaluate.GameStateEvaluator;
import action.Action;
import game.AI;
import game.GameState;

public class GreedyActionAI implements AI {

	@Override
	public Action act(GameState state, long ms) {
		
		GameStateEvaluator evaluator = new GameStateEvaluator();
		
		Action best = null;
		double bestValue = -100000000;
		if (!state.p1Turn)
			bestValue = 100000000;
		
		for(Action action : state.possibleActions()){
			
			GameState next = state.copy();
			next.update(action);
			double val = evaluator.eval(next, state.p1Turn);
			
			if (val > bestValue){
				bestValue = val;
				best = action;
			}
			
		}
		
		return best;
	}

	
	
}
