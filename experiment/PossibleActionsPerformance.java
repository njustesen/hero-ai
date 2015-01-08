import java.util.List;

import action.Action;
import ai.RandomAI;
import ai.util.RAND_METHOD;
import game.AI;
import game.Game;
import game.GameState;

public class PossibleActionsPerformance {

	public static void main(String[] args){
		
		AI p1 = new RandomAI(true, RAND_METHOD.BRUTE);
		AI p2 = new RandomAI(false, RAND_METHOD.BRUTE);
		
		GameState state = createGameState(20, p1, p2);
		
		System.out.println("## Possible actions ##");
		possibleActions(state, 1000);
		
	}
	
	private static void possibleActions(GameState state, int n) {
		
		long start = System.nanoTime();
		
		for(int i = 0; i < n; i++){
			
			List<Action> actions = state.possibleActions();
			
		}
		
		long end = System.nanoTime();
		long ns = end - start;
		double ms = ns / 1000000.0;
		
		System.out.println("Time (ns): " + ns + ", per run: " + ns/n);
		System.out.println("Time (ms): " + ms + ", per run: " + ms/n);
		
	}

	private static GameState createGameState(int turns, AI p1, AI p2){
		Game game = new Game(null, false, p1, p2);
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