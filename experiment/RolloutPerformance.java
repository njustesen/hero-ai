import java.io.IOException;

import model.DECK_SIZE;
import model.HaMap;
import util.CachedLines;
import util.MapLoader;
import game.Game;
import game.GameArguments;
import ai.AI;
import ai.HeuristicAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;
import ai.util.SimpleActionComparator;

public class RolloutPerformance {

	public static void main(String[] args) {

		try {
			CachedLines.load(MapLoader.get("a"));
			AI p1 = null;
			AI p2 = null;
			
			System.out.println("## Random Heuristic AI SIMPLE ##");
			p1 = new RandomHeuristicAI(new SimpleActionComparator());
			simulateGame(1000, p1, p1);
			System.out.println("Done");

			System.out.println("## Random Heuristic AI Complex ##");
			p1 = new RandomHeuristicAI(new ComplexActionComparator());
			simulateGame(1000, p1, p1);
			System.out.println("Done");
			
			System.out.println("## Random AI SIM TREE ##");
			p1 = new RandomAI(RAND_METHOD.TREE);
			simulateGame(1000, p1, p1);
			System.out.println("Done");

			System.out.println("## Heuristic AI ##");
			p1 = new HeuristicAI(new ComplexActionComparator());
			p2 = new HeuristicAI(new ComplexActionComparator());
			simulateGame(1000, p1, p2);
			System.out.println("Done");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	
	private static void simulateGame(int n, AI p1, AI p2) {
		long ns = 0;
		int turns = 0;
		for (int i = 0; i < n; i++) {
			final Game game = new Game(null, new GameArguments(false, p1, p2, "a", DECK_SIZE.STANDARD));
			game.state.init(game.gameArgs.deckSize);
			final long start = System.nanoTime();
			while (!game.state.isTerminal)
				if (game.state.p1Turn)
					game.state.update(p1.act(game.state, -1));
				else
					game.state.update(p2.act(game.state, -1));
			final long end = System.nanoTime();
			ns += end - start;
			turns += game.state.turn;
		}
		System.out.println("// Sim //");
		System.out.println("Turns avg. per game: " + (turns / n));
		System.out.println("Time (ns) avg. per game: " + (ns / n));
		System.out
				.println("Time (ns) avg. per turn: " + (ns / n / (turns / n)));
		final double ms = ns / 1000000.0;
		System.out.println("Time (ms) per game: " + (ms / n));
		System.out.println("Time (ms) per turn: " + (ms / n / (turns / n)));
		System.out.println("Turns avg. per game " + (turns / n));
		System.out.println();
	}
}
