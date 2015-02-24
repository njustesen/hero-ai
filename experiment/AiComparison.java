import game.Game;
import game.GameArguments;
import game.GameState;

import java.io.IOException;

import model.DECK_SIZE;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.RandomHeuristicAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.heuristic.WinLoseEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;

public class AiComparison {

	private static final boolean GFX = true;

	public static void main(String[] args) {
		// Rolling Horizon 50 .5 .35 50 heuristic
		AI p1 = new Mcts(6075, new RolloutEvaluation(1, 1,
				new RandomHeuristicAI(new ComplexActionComparator()),
				new MaterialBalanceEvaluation(true)));
		AI p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out
				.println("P1: mcts 2025 rollout 1 1 randomheuristic materialblance win");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 32);

		p1 = new Mcts(6075, new RolloutEvaluation(1, 1, new RandomHeuristicAI(
				new ComplexActionComparator()), new HeuristicEvaluation(true)));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out
				.println("P1: mcts 2025 rollout 1 1 randomheuristic heuristic win");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 32);

		p1 = new Mcts(6075, new RolloutEvaluation(1, 5, new RandomHeuristicAI(
				new ComplexActionComparator()), new MaterialBalanceEvaluation(
				true)));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out
				.println("P1: mcts 2025 rollout 1 5 randomheuristic materialblance win");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 32);

		p1 = new Mcts(6075, new RolloutEvaluation(1, 1000,
				new RandomHeuristicAI(new ComplexActionComparator()),
				new WinLoseEvaluation()));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out
				.println("P1: mcts 2025 rollout 1 1000 randomheuristic winlose win");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 32);

		p1 = new Mcts(6075, new RolloutEvaluation(1, 1000,
				new RandomHeuristicAI(new ComplexActionComparator()),
				new WinLoseEvaluation()));
		p2 = new GreedyActionAI(new HeuristicEvaluation(false));

		System.out
				.println("P1: mcts 6075 rollout 1 1000 randomheuristic winlose win");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 32);

		/*
		 * System.out.println("Avg. found in:");
		 * System.out.println(Statistics.avgInteger
		 * (RollingHorizonEvolution.foundIn));
		 * System.out.println("Avg. best genome visits:");
		 * System.out.println(Statistics
		 * .avgInteger(RollingHorizonEvolution.bestG)); System.out.println();
		 * System.out.println("Best fitness / avg:"); for (int d = 0; d <
		 * RollingHorizonEvolution.bestFitness.size(); d++){ for(int i = 0; i <
		 * RollingHorizonEvolution.bestFitness.get(d).size(); i++)
		 * System.out.println
		 * (i+"\t"+RollingHorizonEvolution.bestHash.get(d).get(
		 * i)+"\t"+RollingHorizonEvolution
		 * .bestVals.get(d).get(i)+"\t"+RollingHorizonEvolution
		 * .bestFitness.get(d
		 * ).get(i)+"\t"+RollingHorizonEvolution.bestVisits.get(d).get(i));
		 * System.out.println(); }
		 */
		/*
		 * System.out.println("Avg. found in:"); System.out.println(Statistics
		 * .avgInteger(RollingHorizonEvolution.foundIn));
		 * System.out.println("Avg. best genome visits:"); System.out
		 * .println(Statistics.avgInteger(RollingHorizonEvolution.bestG));
		 * System.out.println(); System.out.println("Best fitness / avg:"); for
		 * (int d = 0; d < RollingHorizonEvolution.bestFitness.size(); d++) {
		 * for (int i = 0; i < RollingHorizonEvolution.bestFitness.get(d)
		 * .size(); i++) System.out.println(i + "\t" +
		 * RollingHorizonEvolution.bestHash.get(d).get(i) + "\t" +
		 * RollingHorizonEvolution.bestVals.get(d).get(i) + "\t" +
		 * RollingHorizonEvolution.bestFitness.get(d).get(i) + "\t" +
		 * RollingHorizonEvolution.bestVisits.get(d).get(i));
		 * System.out.println();
		 * 
		 * }
		 */

	}

	private static void compare(AI p1, AI p2, int games) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		GameState state;
		try {
			state = new GameState(MapLoader.get("a-small"));
			final GameState clone = state.copy();
			;
			final Game game = new Game(state, new GameArguments(GFX, p1, p2,
					"a-small", DECK_SIZE.SMALL));
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
