import java.util.NoSuchElementException;

import model.DECK_SIZE;
import ai.AI;
import ai.RandomAI;
import ai.util.RAND_METHOD;
import game.Game;
import game.GameArguments;
import game.GameState;


public class ClonePerformance {

	public static void main(String[] args){
		
		GameState state = createGameState(10, 
				new RandomAI(RAND_METHOD.BRUTE), 
				new RandomAI(RAND_METHOD.BRUTE));
		
		System.out.println("## Game state cloning - 1 ##");
		cloneGameState(state, 1);
		System.out.println();
		
		System.out.println("## Game state cloning - 10 ##");
		cloneGameState(state, 10);
		System.out.println();
		
		System.out.println("## Game state cloning - 100 ##");
		cloneGameState(state, 100);
		System.out.println();
		
		System.out.println("## Game state cloning - 1000 ##");
		cloneGameState(state, 1000);
		System.out.println();
		
		System.out.println("## Game state cloning - 10000 ##");
		cloneGameState(state, 10000);
		System.out.println();
		
		System.out.println("## Game state cloning - 100000 ##");
		cloneGameState(state, 100000);
		System.out.println();
		
		System.out.println("## Game state cloning - 1000000 ##");
		cloneGameState(state, 1000000);
		System.out.println();
		
	}
	
	private static void cloneGameState(GameState state, int n){
		
		long start = System.nanoTime();
		
		for(int i = 0; i < n; i++){
			GameState clone = state.copy();
		}
		
		long end = System.nanoTime();
		long ns = (end-start);
		double ms = ns / 1000000.0;
		
		System.out.println("TIME (ns): " + ns + ", per clone: " + ns/n);
		System.out.println("TIME (ms): " + ms + ", per clone: " + ms/n);
		
	}
	
	private static GameState createGameState(int turns, AI p1, AI p2) {
		Game game = new Game(null, new GameArguments(false, p1, p2, "a", DECK_SIZE.STANDARD));
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
