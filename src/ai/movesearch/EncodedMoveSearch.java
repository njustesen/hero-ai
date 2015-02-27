package ai.movesearch;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import ai.heuristic.HeuristicEvaluator;
import ai.util.ActionEncoding;
import ai.util.ActionPruner;
import game.GameState;

public class EncodedMoveSearch {

	HeuristicEvaluator evalutator = new HeuristicEvaluator(false);
	ActionPruner pruner = new ActionPruner();
	List<String> moves;
	ObjectPool<GameState> pool;
	
	public List<String> possibleMoves(GameState state, ObjectPool<GameState> pool) {

		moves = new ArrayList<String>();
		this.pool = pool;
		try {
			addMoves(state, "", 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(moves.size() + " moves found!");
		
		return moves;

	}

	private void addMoves(GameState state, String move, int depth) throws IllegalStateException, UnsupportedOperationException, Exception {

		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		int i = 1;
		for (final Action action : actions) {
			if (depth == 0)
				System.out.println(i + "/" + actions.size() + " " + action);
			if (depth < 5 && !(action instanceof EndTurnAction)) {
				GameState next;
				if (pool.getNumIdle() == 0)
					pool.addObject();
				next = pool.borrowObject();
				next.imitate(state);
				next.update(action);
				//if (next.APLeft == state.APLeft)
				//	continue; // Nothing happened
				addMoves(next, move + ActionEncoding.encode(action) + " ", depth + 1);
				pool.returnObject(next);
			} else {
				moves.add(move + ActionEncoding.encode(action));
			}
			i++;
		}
		
	}
}
