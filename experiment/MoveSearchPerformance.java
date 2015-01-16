import game.Game;
import game.GameState;

import java.util.List;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import action.Action;
import ai.AI;
import ai.RandomAI;
import ai.util.GameStateFactory;
import ai.util.TreeMoveSearch;
import ai.util.RAND_METHOD;

public class MoveSearchPerformance {

	public static void main(String[] args) {

		final GameState state = createGameState(10, new RandomAI(RAND_METHOD.BRUTE), new RandomAI(RAND_METHOD.BRUTE));

		search(state);

	}

	private static void search(GameState state) {

		final ObjectPool<GameState> pool = new GenericObjectPool<GameState>(
				new GameStateFactory());

		final TreeMoveSearch search = new TreeMoveSearch();
		final List<List<Action>> moves = search.possibleMoves(state, pool);
		System.out.println(moves.size());

	}

	private static GameState createGameState(int turns, AI p1, AI p2) {
		final Game game = new Game(null, false, p1, p2);
		while (game.state.turn < turns)
			if (game.state.p1Turn)
				game.state.update(p1.act(game.state, -1));
			else
				game.state.update(p2.act(game.state, -1));
		return game.state;
	}

}
