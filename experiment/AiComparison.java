import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyActionAI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;

public class AiComparison {

	private static final boolean GFX = false;

	public static void main(String[] args) {
		/*
		 * AI p1 = new GreedyTurnAI(new HeuristicEvaluation()); AI p2 = new
		 * GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: greedyaction heuristic");
		 * System.out.println("P2: heuristic"); compare(p1, p2, 20);
		 */
		/*
		 * AI p1 = new Mcts(25, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); AI p2 =
		 * new GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [25 ms]");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(75, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); p2 = new
		 * GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [75 ms]");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(225, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); p2 = new
		 * GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [225 ms]");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * // CUT p1 = new Mcts(25, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); ((Mcts)
		 * p1).cut = true; p2 = new GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [25 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(75, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); ((Mcts)
		 * p1).cut = true; p2 = new GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [75 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(225, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); ((Mcts)
		 * p1).cut = true; p2 = new GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [225 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 */

		final AI p1 = new Mcts(225 * 3 * 3 * 3, new RolloutEvaluation(1, 10,
				new RandomAI(RAND_METHOD.TREE),
				new MaterialBalanceEvaluation(), true));
		((Mcts) p1).collapse = true;
		final AI p2 = new GreedyActionAI(new HeuristicEvaluation());
		System.out.println("P1: mcts 10 material [225x3x3x3 ms] collapse");
		System.out.println("P2: greedyaction heuristc");
		compare(p1, p2, 100);

		/*
		 * p1 = new Mcts(225 * 3 * 3, new RolloutEvaluation(1, 10, new RandomAI(
		 * RAND_METHOD.TREE), new MaterialBalanceEvaluation(), true)); ((Mcts)
		 * p1).cut = true; p2 = new GreedyActionAI(new HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [225x3x3 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 */
		// COLLAPSE
		/*
		 * p1 = new Mcts(25, new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * ((Mcts)p1).collapse = true; p2 = new GreedyActionAI(new
		 * HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [25 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(75, new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * ((Mcts)p1).collapse = true; p2 = new GreedyActionAI(new
		 * HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [75 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 * 
		 * p1 = new Mcts(225, new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * ((Mcts)p1).collapse = true; p2 = new GreedyActionAI(new
		 * HeuristicEvaluation());
		 * System.out.println("P1: mcts 10 material [225 ms] cut");
		 * System.out.println("P2: greedyaction heuristc"); compare(p1, p2,
		 * 100);
		 */
	}

	private static void compare(AI p1, AI p2, int games) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		final GameState state = new GameState(HAMap.mapA);
		final GameState clone = new GameState(HAMap.mapA);
		final Game game = new Game(state, GFX, p1, p2);
		for (int i = 0; i < games; i++) {
			final boolean p1Starting = (i < games / 2);
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
			System.out.println("Winner=" + winner);
		}

		System.out.println("P1=" + p1Wins);
		System.out.println("P2=" + p2Wins);
		System.out.println("draws=" + draws);

	}

}
