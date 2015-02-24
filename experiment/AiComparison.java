import game.Game;
import game.GameArguments;
import game.GameState;

import java.io.IOException;

import model.DECK_SIZE;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.heuristic.HeuristicEvaluation;

public class AiComparison {

	private static final boolean GFX = true;

	public static void main(String[] args) {

		/*
		 * //Rolling Horizon 50 .5 .35 50 heuristic AI p1 = new Mcts(6075, new
		 * RolloutEvaluation(1, 1, new RandomHeuristicAI(new
		 * ComplexActionComparator()), new MaterialBalanceEvaluation(true))); AI
		 * p2 = new GreedyActionAI(new HeuristicEvaluation(false));
		 * 
		 * System.out.println(
		 * "P1: mcts 2025 rollout 1 1 randomheuristic materialblance win");
		 * System.out.println("P2: greedyaction heuristic"); compare(p1, p2,
		 * 32);
		 * 
		 * p1 = new Mcts(6075, new RolloutEvaluation(1, 1, new
		 * RandomHeuristicAI(new ComplexActionComparator()), new
		 * HeuristicEvaluation(true))); p2 = new GreedyActionAI(new
		 * HeuristicEvaluation(false));
		 * 
		 * System.out.println(
		 * "P1: mcts 2025 rollout 1 1 randomheuristic heuristic win");
		 * System.out.println("P2: greedyaction heuristic"); compare(p1, p2,
		 * 32);
		 * 
		 * p1 = new Mcts(6075, new RolloutEvaluation(1, 5, new
		 * RandomHeuristicAI(new ComplexActionComparator()), new
		 * MaterialBalanceEvaluation(true))); p2 = new GreedyActionAI(new
		 * HeuristicEvaluation(false));
		 * 
		 * System.out.println(
		 * "P1: mcts 2025 rollout 1 5 randomheuristic materialblance win");
		 * System.out.println("P2: greedyaction heuristic"); compare(p1, p2,
		 * 32);
		 * 
		 * p1 = new Mcts(6075, new RolloutEvaluation(1, 1000, new
		 * RandomHeuristicAI(new ComplexActionComparator()), new
		 * WinLoseEvaluation())); p2 = new GreedyActionAI(new
		 * HeuristicEvaluation(false));
		 * 
		 * System.out.println(
		 * "P1: mcts 2025 rollout 1 1000 randomheuristic winlose win");
		 * System.out.println("P2: greedyaction heuristic"); compare(p1, p2,
		 * 32);
		 * 
		 * p1 = new Mcts(6075, new RolloutEvaluation(1, 1000, new
		 * RandomHeuristicAI(new ComplexActionComparator()), new
		 * WinLoseEvaluation())); p2 = new GreedyActionAI(new
		 * HeuristicEvaluation(false));
		 * 
		 * System.out.println(
		 * "P1: mcts 6075 rollout 1 1000 randomheuristic winlose win");
		 * System.out.println("P2: greedyaction heuristic"); compare(p1, p2,
		 * 32);
		 */
		AI p1 = new GreedyTurnAI(new HeuristicEvaluation(false));
		AI p2 = new GreedyActionAI(new HeuristicEvaluation(false));
		System.out.println("P1: greedyturn heuristic");
		System.out.println("Pedyaction heuristic");
		System.out.println("TINY");
		compare(p1, p2, 32, "a-tiny", DECK_SIZE.TINY);

		p1 = new GreedyTurnAI(new HeuristicEvaluation(false));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out.println("P1: greedyturn heuristic");
		System.out.println("P2: greedyaction heuristic");
		System.out.println("SMALL");
		compare(p1, p2, 32, "a-small", DECK_SIZE.SMALL);

		p1 = new GreedyTurnAI(new HeuristicEvaluation(false));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out.println("P1: greedyturn heuristic");
		System.out.println("P2: greedyaction heuristic");
		System.out.println("STANDARD");
		compare(p1, p2, 32, "a", DECK_SIZE.STANDARD);

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
