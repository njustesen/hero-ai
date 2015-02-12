import model.HAMap;
import game.Game;
import game.GameState;
import ai.AI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.mcts.UCT;
import ai.util.RAND_METHOD;

public class AiComparison {

	public static void main(String[] args){
		
		AI p1 = new Mcts(5000, new UCT(), new RolloutEvaluation(1, 5, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		AI p2 = new GreedyTurnAI(new HeuristicEvaluation());
		compare(p1, p2, 10);
		/*
		p1 = new Mcts(250, new UCT(), new RolloutEvaluation(1, 1, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		p2 = new Mcts(250, new UCT(), new RolloutEvaluation(1, 5, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		compare(p1, p2, 10);
		
		p1 = new Mcts(500, new UCT(), new RolloutEvaluation(1, 1, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		p2 = new Mcts(500, new UCT(), new RolloutEvaluation(1, 5, new RandomAI(RAND_METHOD.TREE), new MaterialEvaluation(), true));
		compare(p1, p2, 10);
		*/
	}

	private static void compare(AI p1, AI p2, int games) {
		
		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;
		
		for(int i = 0; i < games; i++){
			boolean p1Starting = (i < games/2);
			GameState state = new GameState(HAMap.mapA);
			Game game = null;
			if (p1Starting)
				game = new Game(state, true, p1, p2);
			else 
				game = new Game(state, true, p2, p1);
			game.run();
			
			int winner = state.getWinner();
			if (winner == 1 && p1Starting || winner == 2 && !p1Starting)
				p1Wins++;
			else if (winner == 2 && p1Starting || winner == 1 && !p1Starting)
				p2Wins++;
			else
				draws++;
			System.out.println("Winner=" + winner);
		}
		
		System.out.println("P1="+p1Wins);
		System.out.println("P2="+p2Wins);
		System.out.println("draws="+draws);
		
	}
	
}
