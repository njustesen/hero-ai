import ai.AI;
import ai.GreedyActionAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.mcts.ITreePolicy;
import ai.mcts.UCT;
import ai.util.RAND_METHOD;
import model.HAMap;
import game.Game;
import game.GameState;


public class MctsExperiment {

	public static void main(String[] args) {
		
		AI random = new RandomAI(RAND_METHOD.TREE);
		ITreePolicy uct = new UCT();
		//AI p1 = new Mcts(5000, uct, new RolloutEvaluation(1, 300, random, new WinEvaluation(), false));
		AI p1 = new Mcts(200, uct, new RolloutEvaluation(1, 2, new HeuristicAI(), new HeuristicEvaluation(), false));
		GameState state = new GameState(HAMap.mapA);
		Game game = new Game(state, true, p1, new GreedyActionAI(new HeuristicEvaluation()));
		game.run();
		
	}

}
