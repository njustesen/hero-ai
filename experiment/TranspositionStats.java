import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyTurnAI;
import ai.heuristic.HeuristicEvaluation;

public class TranspositionStats {

	public static void main(String[] args) {

		final GameState state = new GameState(HAMap.mapA);
		final AI p1 = new GreedyTurnAI(new HeuristicEvaluation());
		final Game game = new Game(state, true, p1, p1);
		game.run();
	}

}
