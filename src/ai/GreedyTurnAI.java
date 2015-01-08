package ai;

import java.util.ArrayList;
import java.util.List;

import evaluate.GameStateEvaluator;

import action.Action;
import action.EndTurnAction;
import ai.util.MoveSearch;
import game.AI;
import game.GameState;

public class GreedyTurnAI implements AI {
	
	GameStateEvaluator evalutator = new GameStateEvaluator();
	MoveSearch searcher = new MoveSearch();
	List<Action> actions = new ArrayList<Action>();
	
	@Override
	public Action act(GameState state, long ms) {
		
		if (!actions.isEmpty()){
			Action action = actions.get(0);
			actions.remove(0);
			return action;
		}
			
		//List<List<Action>> possibleActions = searcher.possibleMoves(state);		
		System.out.println("GTAI: Searching for possible moves.");
		actions = best(state, searcher.possibleMoves(state));
		
		Action action = actions.get(0);
		actions.remove(0);
		
		return action;
		
	}

	private List<Action> best(GameState state, List<List<Action>> possibleMoves) {
		System.out.println("GTAI: Evaluation " + possibleMoves.size() + " moves.");
		double bestValue = -10000000;
		List<Action> bestMove = null;
		for(List<Action> move : possibleMoves){
			double value = evaluateMove(state, move);
			if (value > bestValue || bestMove == null){
				bestMove = move;
				bestValue = value;
			}
		}
		System.out.println("GTAI: Best move found : " + bestMove);
		System.out.println("GTAI: Value : " + bestValue);
		return bestMove;
	}


	private double evaluateMove(GameState state, List<Action> move) {
		
		int i = 0;
		int lastAP = 0;
		GameState clone = state.copy();
		while(clone.APLeft > 0 && lastAP != clone.APLeft){
			lastAP = clone.APLeft;
			clone.update(move.get(i));
			i++;
		}
		
		return evalutator.eval(clone, clone.p1Turn);
		
	}

}
