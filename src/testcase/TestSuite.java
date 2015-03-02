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
import ai.StatisticAi;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluator;
import ai.heuristic.MaterialBalanceEvaluator;
import ai.heuristic.RolloutEvaluator;
import ai.heuristic.WinLoseEvaluator;
import ai.mcts.Mcts;
import ai.util.RAND_METHOD;

public class TestSuite {

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
		else if (args[0].equals("mcts-c"))
			MctsCTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("mcts-cut"))
			MctsCutTests(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-MutRate"))
			RollingMutRate(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-killrate"))
			RollingKillRate(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("rolling-vs-greedyaction"))
			RollingVsGreedyAction(Integer.parseInt(args[1]), args[2]);
		else if (args[0].equals("greedy-action-vs-random"))
			GreedyActionVsRandom(Integer.parseInt(args[1]), args[2]);
		if (args[0].equals("greedy-action-vs-greedy-turn"))
			GreedyActionVsGreedyTurn(Integer.parseInt(args[1]), args[2]);
	}
	
	private static void RollingMutRate(int runs, String size) {
		
		List<TestCase> tests = new ArrayList<TestCase>();
		
		AI p2 = new RollingHorizonEvolution(100, .66, .5, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		AI p1 = new RollingHorizonEvolution(100, .33, .5, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		AI p3 = new RollingHorizonEvolution(100, .5, .5, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		
		tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p2), runs, "rolling-mutrate", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p3), runs, "rolling-mutrate", map(size), deck(size)));
		
		for(TestCase test : tests)
			test.run();
	}
	
	private static void RollingKillRate(int runs, String size) {
		
		List<TestCase> tests = new ArrayList<TestCase>();
		
		AI p2 = new RollingHorizonEvolution(100, .5, .66, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		AI p1 = new RollingHorizonEvolution(100, .5, .33, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		AI p3 = new RollingHorizonEvolution(100, .5, .85, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		
		//tests.add(new TestCase(new StatisticAi(p1), new StatisticAi(p2), runs, "rolling-killrate", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(p2), new StatisticAi(p3), runs, "rolling-killrate", map(size), deck(size)));
		
		for(TestCase test : tests)
			test.run();
	}

	private static void RollingVsGreedyAction(int runs, String size) {
		AI p2 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p1 = new RollingHorizonEvolution(100, .5, .5, 2000, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(false)));
		new TestCase(p1, p2, runs, "rolling-vs-greedy-action", map(size), deck(size)).run();
		
	}

	private static void GreedyActionVsGreedyTurn(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p2 = new GreedyTurnAI(new HeuristicEvaluator(false));
		
		new TestCase(new StatisticAi(p1), new StatisticAi(p2), runs, "greedy-action-vs-greedy-turn", map(size), deck(size)).run();
		
	}

	private static void GreedyActionVsRandom(int runs, String size) {
		AI p1 = new GreedyActionAI(new HeuristicEvaluator(false));
		AI p2 = new RandomAI(RAND_METHOD.TREE);
		
		new TestCase(p1, p2, runs, "greedy-action-vs-random", map(size), deck(size)).run();
	}
	
	private static void MctsCutTests(int runs, String size) {
		List<TestCase> tests = new ArrayList<TestCase>();
		
		Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		mcts2.cut = true;
		
		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs, "mcts-vs-cut", map(size), deck(size)));
		
		for(TestCase test : tests)
			test.run();
		
	}
	
	private static void MctsCTests(int runs, String size) {
		List<TestCase> tests = new ArrayList<TestCase>();
		
		Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		mcts2.c = mcts2.c / 2;
		Mcts mcts3 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		mcts3.c = mcts3.c / 4;
		
		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs, "mcts-c-vs-05c", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts2), new StatisticAi(mcts3), runs, "mcts-05c-vs-025c", map(size), deck(size)));
		
		for(TestCase test : tests)
			test.run();
		
	}

	private static void MctsRolloutTests(int runs, String size) {
		List<TestCase> tests = new ArrayList<TestCase>();
		
		Mcts mcts1 = new Mcts(6075, new RolloutEvaluator(1, 10000, new RandomHeuristicAI(0.3), new WinLoseEvaluator()));
		Mcts mcts2 = new Mcts(6075, new RolloutEvaluator(1, 5, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts3 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new MaterialBalanceEvaluator(true)));
		Mcts mcts4 = new Mcts(6075, new RolloutEvaluator(1, 1, new RandomHeuristicAI(0.3), new HeuristicEvaluator(true)));
		
		tests.add(new TestCase(new StatisticAi(mcts1), new StatisticAi(mcts2), runs, "mcts-nodepth-vs-5-depth", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts2), new StatisticAi(mcts3), runs, "mcts-5depth-vs-1depth", map(size), deck(size)));
		tests.add(new TestCase(new StatisticAi(mcts3), new StatisticAi(mcts4), runs, "mcts-1depth-heuristic-vs-1depth-material", small, DECK_SIZE.SMALL));
		
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
