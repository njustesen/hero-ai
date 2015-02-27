import game.Game;
import game.GameArguments;
import game.GameState;

import java.io.IOException;

import model.DECK_SIZE;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.evolution.NestedEvolution;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluator;
import ai.heuristic.RolloutEvaluatior;
import ai.heuristic.WinLoseEvaluator;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;

public class AiComparison {

	private static final boolean GFX = true;

	public static void main(String[] args) {
		
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		//AI p2 = new Mcts(225, new RolloutEvaluation(1, 10000, new RandomHeuristicAI(0.5, new ComplexActionComparator()), new WinLoseEvaluation()));
		//AI p2 = new Mcts(675, new RolloutEvaluation(1, 10000, new RandomHeuristicAI(0.5, new ComplexActionComparator()), new WinLoseEvaluation()));
		//AI p2 = new Mcts(2025, new RolloutEvaluation(1, 10000, new RandomHeuristicAI(0.5, new ComplexActionComparator()), new WinLoseEvaluation()));
		AI p2 = new Mcts(6075, new RolloutEvaluatior(1, 10000, new RandomHeuristicAI(0.5), new WinLoseEvaluator()));
		
		System.out.println("P1: greedyturn heuristic");
		System.out.println("P2: rollinghorizon 100 .5 .5 1000 rollout 1 1 randomheuristic heuristic");
		System.out.println("STANDARD");
		compare(p1, p2, 1, "a", DECK_SIZE.STANDARD);
		
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
