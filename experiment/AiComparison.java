import game.Game;
import game.GameArguments;
import game.GameState;

import java.io.IOException;

import model.DECK_SIZE;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.RandomHeuristicAI;
import ai.evolution.NestedEvolution;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;

public class AiComparison {

	private static final boolean GFX = true;

	public static void main(String[] args) {
		
		AI p1 = new GreedyTurnAI(new HeuristicEvaluation(false));
		//AI p2 = new NestedEvolution(100, 20, 0.5, .50, 400, new HeuristicEvaluation(false));
		AI p2 = new NestedEvolution(32, 16, 0.5, .35, 200, new RolloutEvaluation(1, 1, new RandomHeuristicAI(new ComplexActionComparator()), new HeuristicEvaluation(false)),new HeuristicEvaluation(false));
		
		System.out.println("P1: greedyaction heuristic");
		System.out.println("P2: Nested Evolution 100 20 .5 .35 100 rollout 5 1 randomheuristic heuristic");
		System.out.println("TINY");
		compare(p1, p2, 10, "a-tiny", DECK_SIZE.TINY);
		
	}

	private static void compare(AI p1, AI p2, int games, String mapName,
			DECK_SIZE deckSize) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		GameState state;
		try {
			state = new GameState(MapLoader.get(mapName));
			final GameState clone = state.copy();
			final Game game = new Game(state, new GameArguments(GFX, p1, p2,
					mapName, deckSize));
			boolean p1Starting;
			for (int i = 0; i < games; i++) {
				if (games == 1)
					p1Starting = true;
				else
					p1Starting = (i < games / 2);
				clone.imitate(state);
				game.state = clone;
				if (p1Starting) {
					game.player1 = p1;
					game.player2 = p2;
				} else {
					game.player1 = p2;
					game.player2 = p1;
				}
				game.run();

				final int winner = clone.getWinner();
				if (winner == 1 && p1Starting || winner == 2 && !p1Starting)
					p1Wins++;
				else if (winner == 2 && p1Starting || winner == 1
						&& !p1Starting)
					p2Wins++;
				else
					draws++;
				System.out.print(winner);
			}
			System.out.print("\n");

			System.out.println("P1=" + p1Wins);
			System.out.println("P2=" + p2Wins);
			System.out.println("draws=" + draws);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

}
