import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import util.MapLoader;
import model.DECK_SIZE;
import model.HaMap;
import game.Game;
import game.GameArguments;
import game.GameState;
import ai.AI;
import ai.RandomAI;
import ai.evaluation.HeuristicEvaluator;
import ai.evaluation.RolloutEvaluator;
import ai.evaluation.WinLoseEvaluator;
import ai.util.RAND_METHOD;


public class RolloutDeviation {

	public static void main(String[] args){
		
		AI p1 = new RandomAI(RAND_METHOD.TREE);
		GameState state = createGameState(1, p1, p1);
		
		List<RolloutEvaluator> rollers = new ArrayList<RolloutEvaluator>();
		for(int i = 10; i <= 1000000; i=i*10)
			rollers.add(new RolloutEvaluator(i, 10000, new RandomAI(RAND_METHOD.TREE), new WinLoseEvaluator(), true));
		
		for(RolloutEvaluator roller : rollers)
			roller.eval(state.copy(), true);
		
	}
	
	private static GameState createGameState(int turns, AI p1, AI p2) {
		
		GameState state;
		try {
			state = new GameState(MapLoader.get("a"));
			Game game = new Game(state, new GameArguments(false, p1, p2, "a", DECK_SIZE.STANDARD));
			state.init(game.gameArgs.deckSize);
			
			while(game.state.turn < turns){
				if (game.state.p1Turn) {
					game.state.update(p1.act(game.state, -1));
				} else {
					game.state.update(p2.act(game.state, -1));
				}
			}
			return game.state;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}


