package ai;

import java.util.ArrayList;
import java.util.List;

import evaluate.GameStateEvaluator;

import action.Action;
import action.EndTurnAction;
import game.AI;
import game.GameState;

public class GreedyTurnAI implements AI {
	
	GameStateEvaluator evalutator;
	
	@Override
	public Action act(GameState state, long ms) {
		
		evalutator = new GameStateEvaluator();
		
		List<Action> actions = best(state, state.copy(), new ArrayList<Action>());		
		
		System.out.println("actions="+actions);
		//System.out.println("value="+value);
		
		return actions.get(0);
		
	}

	private List<Action> best(GameState root, GameState state, List<Action> actions) {
		
		if (state.APLeft <= 0){
			List<Action> newActions = new ArrayList<Action>();
			for(Action a : actions)
				newActions.add(a);
			newActions.add(new EndTurnAction());
			return newActions;
		}
		
		List<Action> possible = state.possibleActions();
		if (possible.isEmpty()){
			List<Action> newActions = new ArrayList<Action>();
			for(Action a : actions)
				newActions.add(a);
			newActions.add(new EndTurnAction());
			return newActions;
		}
		
		List<Action> best = null;
		double bestValue = -100000000;
		for(Action p : possible){
			List<Action> newActions = new ArrayList<Action>();
			for(Action a : actions)
				newActions.add(a);
			newActions.add(p);
			GameState clone = state.copy();
			clone.update(p);
			List<Action> resulting = best(root, clone, newActions);
			double value = value(root.copy(), resulting, root.p1Turn);
			//System.out.println("actions="+resulting);
			//System.out.println("value="+value);
			if (value > bestValue){
				bestValue = value;
				best = resulting;
			}
		}
		
		return best;
	}

	private double value(GameState state, List<Action> actions, boolean p1) {
		
		int i = 0;
		while(state.APLeft > 0){
			state.update(actions.get(i));
			i++;
		}
		
		return evalutator.eval(state, p1);
	}


	
	
}
