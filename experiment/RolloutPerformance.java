import ai.RandomAI;
import ai.ScanRandomAI;
import ai.util.RAND_METHOD;
import game.AI;
import game.Game;

public class RolloutPerformance {
	
	public static void main(String[] args){
		
		AI p1 = null;
		AI p2 = null;
		/*
		System.out.println("## Random AI GAME BRUTE ##");
		p1 = new RandomAI(true, RAND_METHOD.BRUTE);
		p2 = new RandomAI(false, RAND_METHOD.BRUTE);
		runGame(1000, p1, p2);
		System.out.println("## DONE ##");
		*/
		
		System.out.println("## Random AI GAME TREE ##");
		p1 = new RandomAI(true, RAND_METHOD.TREE);
		p2 = new RandomAI(false, RAND_METHOD.TREE);
		runGame(1000, p1, p2);
		System.out.println("## DONE ##");
		/*
		System.out.println("## Random AI GAME SCAN ##");
		p1 = new ScanRandomAI(true);
		p2 = new ScanRandomAI(false);
		runGame(1000, p1, p2);
		*/
		/*
		System.out.println("## Random AI SIM BRUTE ##");
		p1 = new RandomAI(true, RAND_METHOD.BRUTE);
		p2 = new RandomAI(false, RAND_METHOD.BRUTE);
		simulateGame(10000, p1, p2);
		System.out.println("Done");
		*/
		/*
		System.out.println("## Random AI SIM TREE ##");
		p1 = new RandomAI(true, RAND_METHOD.TREE);
		p2 = new RandomAI(false, RAND_METHOD.TREE);
		simulateGame(1000, p1, p2);
		System.out.println("Done");
		*/
		/*
		System.out.println("## Random AI SIM SCAN ##");
		p1 = new ScanRandomAI(true);
		p2 = new ScanRandomAI(false);
		simulateGame(1000, p1, p2);
		*/
	}

	private static void runGame(int n, AI p1, AI p2){
		long ns = 0;
		int turns = 0;
		for(int i = 0; i < n; i++){
			Game game = new Game(null, false, p1, p2);
			long start = System.nanoTime();
			game.run();
			long end = System.nanoTime();
			ns += end - start;
			turns += game.state.turn;
		}
		System.out.println("// Run Game //");
		System.out.println("Time (ns) avg. per game: " + (ns/n));
		System.out.println("Time (ns) avg. per turn: " + (ns/n/(turns/n)));
		double ms = ns/1000000.0;
		System.out.println("Time (ms) per game: " + (ms/n));
		System.out.println("Time (ms) per turn: " + (ms/n/(turns/n)));
		System.out.println("Turns avg. per game " + (turns/n));
		System.out.println();
	}
	
	private static void simulateGame(int n, AI p1, AI p2){
		for(int i = 0; i < n; i++){
			Game game = new Game(null, false, p1, p2);
			while(!game.state.isTerminal){
				if (game.state.p1Turn) {
					game.state.update(p1.act(game.state, -1));
				} else {
					game.state.update(p2.act(game.state, -1));
				}
			}
		}
	}
	
}
