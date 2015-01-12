package ai;

import java.util.ArrayList;
import java.util.List;

import evaluate.GameStateEvaluator;
import action.Action;
import game.AI;
import game.GameState;

public class GreedyActionAI implements AI {

	private List<Action> actions;
	
	public GreedyActionAI() {
		super();
		this.actions = new ArrayList<Action>();
	}

	@Override
	public Action act(GameState state, long ms) {
		
		GameStateEvaluator evaluator = new GameStateEvaluator();
		
		Action best = null;
		double bestValue = -100000000;
		
		actions.clear();
		state.possibleActions(actions);
		for(Action action : actions){
			
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
