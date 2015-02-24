import game.Game;
import game.GameArguments;
import game.GameState;

import java.util.ArrayList;
import java.util.List;

import model.DECK_SIZE;
import action.Action;
import ai.AI;
import ai.RandomAI;
import ai.util.RAND_METHOD;

public class PossibleActionsPerformance {

	public static void main(String[] args) {

		final AI p1 = new RandomAI(RAND_METHOD.BRUTE);
		final AI p2 = new RandomAI(RAND_METHOD.BRUTE);

		final GameState state = createGameState(20, p1, p2);

		System.out.println("## Possible actions ##");
		possibleActions(state, 1000);

	}

	private static void possibleActions(GameState state, int n) {

		final long start = System.nanoTime();

		final List<Action> actions = new ArrayList<Action>();
		for (int i = 0; i < n; i++) {

			state.possibleActions(actions);

		}

		final long end = System.nanoTime();
		final long ns = end - start;
		final double ms = ns / 1000000.0;

		System.out.println("Time (ns): " + ns + ", per run: " + ns / n);
		System.out.println("Time (ms): " + ms + ", per run: " + ms / n);

	}

	private static GameState createGameState(int turns, AI p1, AI p2) {
		final Game game = new Game(null, new GameArguments(false, p1, p2, "a", DECK_SIZE.STANDARD));
		while (game.state.turn < turns) {
			if (game.state.p1Turn) {
				game.state.update(p1.act(game.state, -1));
			} else {
				game.state.update(p2.act(game.state, -1));
			}
		}
		return game.state;
	}

}
