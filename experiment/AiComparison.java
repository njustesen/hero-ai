import java.util.List;

import util.Statistics;
import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyActionAI;
import ai.RandomHeuristicAI;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.heuristic.WinLoseEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;

public class AiComparison {

	private static final boolean GFX = true;

	public static void main(String[] args) {
		//Rolling Horizon 50 .5 .35 50 heuristic
		AI p1 = new Mcts(2025, new RolloutEvaluation(1, 1, new RandomHeuristicAI(new ComplexActionComparator()), new MaterialBalanceEvaluation(true)));
		AI p2 = new Mcts(2025, new RolloutEvaluation(1, 1, new RandomHeuristicAI(new ComplexActionComparator()), new MaterialBalanceEvaluation(true)));
		((Mcts)p1).c = ((Mcts)p1).c / 2; 
		
		System.out.println("P1: mcts 2025 rollout 1 1 randomheuristic materialblance win c=" + ((Mcts)p1).c);
		System.out.println("P2: mcts 2025 rollout 1 1 randomheuristic materialblance win c=" + ((Mcts)p2).c);
		compare(p1, p2, 50);
		/*
		System.out.println("Avg. found in:");
		System.out.println(Statistics.avgInteger(RollingHorizonEvolution.foundIn));
		System.out.println("Avg. best genome visits:");
		System.out.println(Statistics.avgInteger(RollingHorizonEvolution.bestG));
		System.out.println();
		System.out.println("Best fitness / avg:");
		for (int d = 0; d < RollingHorizonEvolution.bestFitness.size(); d++){
			for(int i = 0; i < RollingHorizonEvolution.bestFitness.get(d).size(); i++)
				System.out.println(i+"\t"+RollingHorizonEvolution.bestHash.get(d).get(i)+"\t"+RollingHorizonEvolution.bestVals.get(d).get(i)+"\t"+RollingHorizonEvolution.bestFitness.get(d).get(i)+"\t"+RollingHorizonEvolution.bestVisits.get(d).get(i));
			System.out.println();
		}
		*/
		/*
		 * p1 = new Mcts(2025, new RolloutEvaluation(1, 1000, new
		 * RandomHeuristicAI(new ComplexActionComparator()), new
		 * WinLoseEvaluation(), false)); p2 = new Mcts(2025, new
		 * RolloutEvaluation(1, 20, new RandomHeuristicAI(new
		 * ComplexActionComparator()), new MaterialBalanceEvaluation(true),
		 * false)); System.out.println("P1: greedyaction heuristic");
		 * System.out.println("P2: heuristic"); compare(p1, p2, 100);
		 */

	}

	private static void compare(AI p1, AI p2, int games) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		final GameState state = new GameState(HAMap.mapA);
		final GameState clone = new GameState(HAMap.mapA);
		final Game game = new Game(state, GFX, p1, p2);
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
			else if (winner == 2 && p1Starting || winner == 1 && !p1Starting)
				p2Wins++;
			else
				draws++;
			System.out.print(winner);
		}
		System.out.print("\n");

		System.out.println("P1=" + p1Wins);
		System.out.println("P2=" + p2Wins);
		System.out.println("draws=" + draws);

	}

}
