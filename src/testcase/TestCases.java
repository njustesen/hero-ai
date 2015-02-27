package testcase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.DECK_SIZE;
import model.HaMap;
import util.MapLoader;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.heuristic.HeuristicEvaluator;
import ai.heuristic.MaterialBalanceEvaluator;
import ai.heuristic.RolloutEvaluator;
import ai.heuristic.WinLoseEvaluator;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;

public class TestCases {

	private static HaMap tiny;
	private static HaMap small;
	private static HaMap standard;

	public static void main(String[] args) {
		
		try {
			tiny = MapLoader.get("a-tiny");
			small = MapLoader.get("a-small");
			standard = MapLoader.get("a");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (args[0].equals("mcts-rollouts-1"))
			MctsRolloutTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("greedy-action-vs-random"))
			GreedyActionVsRandom(Integer.parseInt(args[1]), args[2]);
		if (args[0].equals("greedy-action-vs-greedy-turn"))
			GreedyActionVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
	}

	private static void GreedyActionVsGreedyTurn(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));
		
		new TestCase(p1, p2, runs, "greedy-action-vs-greedy-turn", map(size), deck(size)).run();
		
	}

	private static void GreedyActionVsRandom(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p2 = new RandomAI(RAND_METHOD.TREE);
		
		new TestCase(p1, p2, runs, "greedy-action-vs-random", map(size), deck(size)).run();
	}

	private static void MctsRolloutTests(int runs, String size) {
		List<TestCase> tests = new ArrayList<TestCase>();
		
		Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 10000, new RandomHeuristicAI(0.3), new WinLoseEvaluator()));
		Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 5, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts3 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts4 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(true)));
		
		tests.add(new TestCase(mcts1, mcts2, runs, "mcts-nodepth-vs-5-depth", map(size), deck(size)));
		tests.add(new TestCase(mcts2, mcts3, runs, "mcts-5depth-vs-1depth", map(size), deck(size)));
		tests.add(new TestCase(mcts3, mcts4, runs, "mcts-1depth-heuristic-vs-1depth-material", small, DECK_SIZE.SMALL));
		
		for(TestCase test : tests)
			test.run();
		
	}

	private static DECK_SIZE deck(String size) {
		if (size.equals("tiny"))
			return DECK_SIZE.TINY;
		if (size.equals("small"))
			return DECK_SIZE.SMALL;
		
		return DECK_SIZE.STANDARD;
	}

	private static HaMap map(String size) {
		if (size.equals("tiny"))
			return tiny;
		if (size.equals("small"))
			return small;
		return standard;
	}

}
