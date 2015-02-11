import java.util.ArrayList;
import java.util.List;

import model.HAMap;
import game.Game;
import game.GameState;
import ai.AI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.heuristic.WinLoseEvaluation;
import ai.util.RAND_METHOD;


public class RolloutDeviation {

	public static void main(String[] args){
		
		AI p1 = new RandomAI(RAND_METHOD.TREE);
		GameState state = createGameState(1, p1, p1);
		
		List<RolloutEvaluation> rollers = new ArrayList<RolloutEvaluation>();
		for(int i = 10; i <= 1000000; i=i*10)
			rollers.add(new RolloutEvaluation(i, 10000, new RandomAI(RAND_METHOD.TREE), new WinLoseEvaluation(), true));
		
		for(RolloutEvaluation roller : rollers)
			roller.eval(state.copy(), true);
		
	}
	
	private static GameState createGameState(int turns, AI p1, AI p2) {
		
		GameState state = new GameState(HAMap.mapA);
		Game game = new Game(state, true, p1, p2);
		state.init();
		
		while(game.state.turn < turns){
			if (game.state.p1Turn) {
				game.state.update(p1.act(game.state, -1));
			} else {
				game.state.update(p2.act(game.state, -1));
			}
		}
		return game.state;
	}
	
}


