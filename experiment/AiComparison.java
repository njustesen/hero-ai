import game.Game;
import game.GameState;
import model.HAMap;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.heuristic.WinLoseEvaluation;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;

public class AiComparison {

	public static void main(String[] args) {

		AI p1 = new GreedyTurnAI(new HeuristicEvaluation());
		AI p2 = new GreedyActionAI(new HeuristicEvaluation());
		System.out.println("P1: greedyaction heuristic");
		System.out.println("P2: heuristic");
		compare(p1, p2, 20);
		/*
		p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new RandomAI(RAND_METHOD.TREE), new HeuristicEvaluation(), true));
		p2 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		System.out.println("P1: mcts 10 heuristic");
		System.out.println("P2: mcts 10 material");
		compare(p1, p2, 20);

		p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 50, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		p2 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		System.out.println("P1: mcts 50 material");
		System.out.println("P2: mcts 10 material");
		compare(p1, p2, 20);

		p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 10, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		p2 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 5, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		System.out.println("P1: mcts 10 material");
		System.out.println("P2: mcts 5 material");
		compare(p1, p2, 20);

		p1 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 1, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		p2 = new Mcts(3000, new UCT(), new RolloutEvaluation(1, 5, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		System.out.println("P1: mcts 1 material");
		System.out.println("P2: mcts 5 material");
		compare(p1, p2, 20);
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
			System.out.println("Winner=" + winner);
		}

		System.out.println("P1=" + p1Wins);
		System.out.println("P2=" + p2Wins);
		System.out.println("draws=" + draws);

	}

}
