package ai.movesearch;

import java.util.ArrayList;
import java.util.List;

import model.Unit;

import org.apache.commons.pool2.ObjectPool;

import action.Action;
import action.EndTurnAction;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.ActionPruner;
import game.GameState;

public class BestMoveSearch {

	HeuristicEvaluation evalutator = new HeuristicEvaluation();
	ActionPruner pruner = new ActionPruner();
	ObjectPool<GameState> pool;
	ObjectPool<Unit> unitPool;
	List<Action> bestMove = new ArrayList<Action>();
	double bestValue;
	private IHeuristic heuristic;
	
	public List<Action> bestMove(GameState state, ObjectPool<GameState> pool, ObjectPool<Unit> unitPool, IHeuristic heuristic) {

		this.pool = pool;
		this.unitPool = unitPool;
		this.heuristic = heuristic;
		this.bestValue = -1000000;
		this.bestMove = null;
		try {
			addMoves(state, new ArrayList<Action>(), 0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bestMove;

	}

	private void addMoves(GameState state, List<Action> move, int depth) throws IllegalStateException, UnsupportedOperationException, Exception {

		final List<Action> actions = new ArrayList<Action>();
		state.possibleActions(actions);
		pruner.prune(actions, state);
		int i = 0;
		for (final Action action : actions) {
			if (depth == 0){
				System.out.println(i++ + "/" + actions.size());
			}
			GameState next = pool.borrowObject();
			next.unitPool = unitPool;
			next.imitate(state);
			next.update(action);
			//if (next.APLeft == state.APLeft)
			//	continue; // Nothing happened
			List<Action> nextMove = clone(move);
			nextMove.add(action);
			if (depth < 5 && !(action instanceof EndTurnAction)) {
				addMoves(next, nextMove, depth + 1);
			} else {
				double value = heuristic.eval(next, state.p1Turn);
				if (value > bestValue){
					bestValue = value;
					bestMove = nextMove;
				}
			}
			//next.reset();
			//next.returnUnits();
			pool.returnObject(next);
		}
		
	}

	private List<Action> clone(List<Action> move) {
		List<Action> actions = new ArrayList<Action>();
		actions.addAll(move);
		return actions;
	}
}
