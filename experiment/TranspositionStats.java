import java.io.IOException;

import util.MapLoader;
import game.Game;
import game.GameArguments;
import game.GameState;
import model.DECK_SIZE;
import model.HaMap;
import ai.AI;
import ai.GreedyTurnAI;
import ai.heuristic.HeuristicEvaluation;

public class TranspositionStats {

	public static void main(String[] args) {

		GameState state;
		try {
			state = new GameState(MapLoader.get("a"));
			final AI p1 = new GreedyTurnAI(new HeuristicEvaluation(false));
			final Game game = new Game(state, new GameArguments(false, p1, p1, "a", DECK_SIZE.STANDARD));
			game.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
