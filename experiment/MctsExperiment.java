import ai.AI;
import ai.GreedyActionAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;
import model.HAMap;
import game.Game;
import game.GameState;


public class MctsExperiment {

	public static void main(String[] args) {
		
		AI random = new RandomAI(RAND_METHOD.TREE);
		//AI p1 = new Mcts(5000, 1 / Math.sqrt(2), new RolloutEvaluation(1, 300, random, new WinEvaluation(), false));
		AI p1 = new Mcts(10000, 1 / Math.sqrt(2), new RolloutEvaluation(1, 2, new RandomHeuristicAI(new ComplexActionComparator()), new HeuristicEvaluation(), false));
		GameState state = new GameState(HAMap.mapA);
		Game game = new Game(state, true, p1, new GreedyActionAI(new HeuristicEvaluation()));
		game.run();
		
	}

}
