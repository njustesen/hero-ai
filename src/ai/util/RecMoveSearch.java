package ai.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;

import action.Action;
import action.EndTurnAction;
import action.SingletonAction;
import game.GameState;

public class RecMoveSearch {

	HeuristicEvaluation evalutator = new HeuristicEvaluation();
	ActionPruner pruner = new ActionPruner();
	List<List<Action>> moves;
	ObjectPool<GameState> pool;
	
	
	
	public List<List<Action>> possibleMoves(GameState state, ObjectPool<GameState> pool) {

		moves = new ArrayList<List<Action>>();
		this.pool = pool;
		try {
			addMoves(state, new ArrayList<Action>(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(moves.size() + " moves found!");
		
		return moves;

	}

	private void addMoves(GameState state, List<Action> move, int depth) throws IllegalStateException, UnsupportedOperationException, Exception {

		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		int i = 1;
		for (final Action action : actions) {
			if (depth == 0)
				System.out.println(i + "/" + actions.size());
			if (depth < 5 && !(action instanceof EndTurnAction)) {
				GameState next;
				if (pool.getNumIdle() == 0)
					pool.addObject();
				next = pool.borrowObject();
				next.imitate(state);
				next.update(action);
				//if (next.APLeft == state.APLeft)
				//	continue; // Nothing happened
				List<Action> clone = clone(move);
				clone.add(action);
				addMoves(next, clone, depth + 1);
				next.reset();
				pool.returnObject(next);
			} else {
				move.add(action);
				moves.add(move);
			}
			i++;
		}
		
	}

	private List<Action> clone(List<Action> move) {
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}
