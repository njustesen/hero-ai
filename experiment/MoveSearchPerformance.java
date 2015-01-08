import java.util.List;

import action.Action;
import ai.RandomAI;
import ai.util.MoveSearch;
import ai.util.RAND_METHOD;
import game.AI;
import game.Game;
import game.GameState;


public class MoveSearchPerformance {

	public static void main(String[] args){
		
		GameState state = createGameState(10, 
				new RandomAI(true, RAND_METHOD.BRUTE), 
				new RandomAI(false, RAND_METHOD.BRUTE));
		
		search(state);
		
	}
	
	
	private static void search(GameState state) {
		
		MoveSearch search = new MoveSearch();
		List<List<Action>> moves = search.possibleMoves(state);
		System.out.println(moves.size());
		
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
