package ai;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import action.Action;
import ai.util.ActionEncoding;
import ai.util.BestMoveSearch;
import ai.util.GameStateFactory;
import ai.util.IHeuristic;
import ai.util.EncodedMoveSearch;
import game.GameState;

public class GreedyTurnAI implements AI {

	private final BestMoveSearch searcher = new BestMoveSearch();
	private List<Action> actions = new ArrayList<Action>();
	private final ObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
	private IHeuristic heuristic;
	
	public GreedyTurnAI(IHeuristic heuristic) {
		super();
		this.heuristic = heuristic;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!actions.isEmpty()) {
			final Action action = actions.get(0);
			actions.remove(0);
			return action;
		}

		// List<List<Action>> possibleActions = searcher.possibleMoves(state);
		System.out.println("GTAI: Searching for possible moves.");

		actions = searcher.bestMove(state, pool, heuristic);
		System.out.println(actions);
		
		final Action action = actions.get(0);
		actions.remove(0);
		return action;

	}
	
	private List<Action> best(GameState state, List<String> possibleMoves) {
		System.out.println("GTAI: Evaluating " + possibleMoves.size()
				+ " moves.");
		double bestValue = -10000000;
		String bestMove = null;
		GameState clone = state.copy();
		for (final String move : possibleMoves) {
			clone.imitate(state);
			final double value = evaluateMove(clone, move);
			if (value > bestValue || bestMove == null) {
				bestMove = move;
				bestValue = value;
			}
		}
		System.out.println("GTAI: Best move found : " + bestMove);
		System.out.println("GTAI: Value : " + bestValue);
		return ActionEncoding.decodeMove( bestMove );
	}

	private double evaluateMove(GameState state, String move) {

		//final GameState clone = state.copy();
		boolean p1 = state.p1Turn;
		String[] actions = move.split(" ");
		for(int i = 0; i < actions.length; i++){
			if (!actions[i].equals(""))
				state.update(ActionEncoding.decode(actions[i]));
		}

		return heuristic.eval(state, p1);

	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
