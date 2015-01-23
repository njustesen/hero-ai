import ai.AI;
import ai.RandomAI;
import ai.heuristic.HeuristicEvaluation;
import ai.util.RAND_METHOD;
import game.Game;

public class StateEvaluationPerformance {
	
	public static void main(String[] args){
		
		System.out.println("## STATE EVALUATIONS ##");
		HeuristicEvaluation evaluator = new HeuristicEvaluation();
		evalGameStates(evaluator, 1000);
		
	}
	
	private static void evalGameStates(HeuristicEvaluation evaluator, int n){
		long ns = 0;
		int runs = 0;
		AI p1 = new RandomAI(RAND_METHOD.BRUTE);
		AI p2 = new RandomAI(RAND_METHOD.BRUTE);
		for(int i = 0; i < n; i++){
			Game game = new Game(null, false, p1, p2);
			while(!game.state.isTerminal){
				if (game.state.p1Turn) {
					game.state.update(p1.act(game.state, -1));
				} else {
					game.state.update(p2.act(game.state, -1));
				}
				long start = System.nanoTime();
				evaluator.eval(game.state, game.state.p1Turn);
				long end = System.nanoTime();
				ns += end - start;
				runs ++;
			}
		}
		System.out.println("// Game State Evaluator //");
		System.out.println("Time (ns) avg. per run: " + (ns/runs));
		double ms = ns/1000000.0;
		System.out.println("Time (ms) avg. per run: " + (ms/runs));
		System.out.println();
	}
	
}
