package ai;

import java.util.ArrayList;
import java.util.List;

import model.Unit;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import action.Action;
import ai.heuristic.IHeuristic;
import ai.movesearch.BestMoveSearch;
import ai.util.GameStateFactory;
import ai.util.UnitFactory;
import game.GameState;

public class GreedyTurnAI implements AI {

	private final BestMoveSearch searcher = new BestMoveSearch();
	private List<Action> actions = new ArrayList<Action>();
	private ObjectPool<Unit> unitPool = new GenericObjectPool<Unit>(new UnitFactory());
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

		actions = searcher.bestMove(state, pool, unitPool, heuristic);
		System.out.println(actions);
		
		final Action action = actions.get(0);
		actions.remove(0);
		return action;

	}
	
	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
