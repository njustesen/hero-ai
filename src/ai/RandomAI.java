package ai;

import java.util.List;

import action.Action;
import action.EndTurnAction;
import game.AI;
import game.GameState;

public class RandomAI implements AI {

	@Override
	public Action act(GameState state, long ms) {
		
		List<Action> actions = state.possibleActions();
		System.out.println(actions.size());
		/*
		System.out.println("-- Posible actions --");
		for(Action action : actions){
			System.out.println(action.toString());
		}
		*/
		if (actions.isEmpty()){
			//System.out.println("No actions!");
			return new EndTurnAction();
		}
		//System.out.println("---------------------");
		int idx = (int) (Math.random() * actions.size());
		Action selected = actions.get(idx);
		//System.out.println(selected);
		//System.out.println("---------------------");
		
		return selected;
	}

}
