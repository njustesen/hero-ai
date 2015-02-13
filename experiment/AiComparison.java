import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.util.RAND_METHOD;

public class AiComparison {

	public static void main(String[] args) {

		AI p1 = new RandomAI(RAND_METHOD.TREE);
		AI p2 = new GreedyActionAI(new HeuristicEvaluation());
		System.out.println("P1: random");
		System.out.println("P2: greedyaction heuristc");
		compare(p1, p2, 100);

		p1 = new GreedyTurnAI(new HeuristicEvaluation());
		p2 = new GreedyActionAI(new HeuristicEvaluation());
		System.out.println("P1: greedyturn heuristic");
		System.out.println("P2: greedyaction heuristc");
		compare(p1, p2, 20);

		/*
		 * AI p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 5000, new
		 * RandomAI(RAND_METHOD.TREE), new WinLoseEvaluation(), true)); AI p2 =
		 * new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * System.out.println("P1: mcts 5000 winlose");
		 * System.out.println("P2: mcts 10 material"); compare(p1, p2, 20);
		 * 
		 * p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new HeuristicEvaluation(), true)); p2 =
		 * new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * System.out.println("P1: mcts 10 heuristic");
		 * System.out.println("P2: mcts 10 material"); compare(p1, p2, 20);
		 * 
		 * p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 50, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true)); p2 =
		 * new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * System.out.println("P1: mcts 50 material");
		 * System.out.println("P2: mcts 10 material"); compare(p1, p2, 20);
		 * 
		 * p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true)); p2 =
		 * new Mcts(3000, new UCT(), new RolloutEvaluation(1, 5, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * System.out.println("P1: mcts 10 material");
		 * System.out.println("P2: mcts 5 material"); compare(p1, p2, 20);
		 * 
		 * p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 1, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true)); p2 =
		 * new Mcts(3000, new UCT(), new RolloutEvaluation(1, 5, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * System.out.println("P1: mcts 1 material");
		 * System.out.println("P2: mcts 5 material"); compare(p1, p2, 20);
		 */
		/*
		 * p1 = new Mcts(250, new UCT(), new RolloutEvaluation(1, 1, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true)); p2 =
		 * new Mcts(250, new UCT(), new RolloutEvaluation(1, 5, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * compare(p1, p2, 10);
		 * 
		 * p1 = new Mcts(500, new UCT(), new RolloutEvaluation(1, 1, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true)); p2 =
		 * new Mcts(500, new UCT(), new RolloutEvaluation(1, 5, new
		 * RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		 * compare(p1, p2, 10);
		 */
	}

	private static void compare(AI p1, AI p2, int games) {

		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		for (int i = 0; i < games; i++) {
			final boolean p1Starting = (i < games / 2);
			final GameState state = new GameState(HAMap.mapA);
			Game game = null;
			if (p1Starting)
				game = new Game(state, true, p1, p2);
			else
				game = new Game(state, true, p2, p1);
			game.run();

			final int winner = state.getWinner();
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
