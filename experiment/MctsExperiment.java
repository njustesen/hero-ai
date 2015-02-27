import java.io.IOException;

import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.heuristic.HeuristicEvaluator;
import ai.heuristic.RolloutEvaluator;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;
import model.DECK_SIZE;
import model.HaMap;
import game.Game;
import game.GameArguments;
import game.GameState;


public class MctsExperiment {

	public static void main(String[] args) {
		
		//AI p1 = new Mcts(5000, 1 / Math.sqrt(2), new RolloutEvaluation(1, 300, random, new WinEvaluation(), false));
		AI p1 = new Mcts(10000, new RolloutEvaluator(1, 2, new RandomHeuristicAI(0.5), new HeuristicEvaluator(false), false));
		GameState state;
		try {
			state = new GameState(MapLoader.get("a"));
			Game game = new Game(state, new GameArguments(true, p1, new GreedyActionAI(new HeuristicEvaluator(false)), "a", DECK_SIZE.STANDARD));
			game.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
