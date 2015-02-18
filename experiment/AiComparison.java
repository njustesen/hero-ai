import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.MaterialEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.heuristic.WinLoseEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;
import ai.util.SimpleActionComparator;

public class AiComparison {

	public static void main(String[] args) {
		//Rolling Horizon 50 .5 .35 50 heuristic
		AI p1 = new RollingHorizonEvolution(100, .5, .35, 100, new HeuristicEvaluation());
		AI p2 = new GreedyActionAI(new HeuristicEvaluation());
		System.out.println("P1: rollinghorizon 100 .5 .35 100 heuristic");
		System.out.println("P2: greedyaction heuristic");
		compare(p1, p2, 100);
		/*
		p1 = new Mcts(2025, new RolloutEvaluation(1, 1000, new RandomHeuristicAI(new ComplexActionComparator()), new WinLoseEvaluation(), false));
		p2 = new Mcts(2025, new RolloutEvaluation(1, 20, new RandomHeuristicAI(new ComplexActionComparator()), new MaterialBalanceEvaluation(true), false));
		System.out.println("P1: greedyaction heuristic");
		System.out.println("P2: heuristic");
		compare(p1, p2, 100);
		*/
	}

	private static void compare(AI p1, AI p2, int games) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		final GameState state = new GameState(HAMap.mapA);
		final GameState clone = new GameState(HAMap.mapA);
		Game game = new Game(state, true, p1, p2);
		for (int i = 0; i < games; i++) {
			final boolean p1Starting = (i < games / 2);
			clone.imitate(state);
			game.state = clone;
			if (p1Starting){
				game.player1 = p1;
				game.player2 = p2;
			}else{
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
