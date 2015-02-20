package game;

import model.DECK_SIZE;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.NmSearchAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.ScanRandomAI;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;

public class GameArguments {
	
	public final AI[] players;
	public String mapName;
	public DECK_SIZE deckSize = DECK_SIZE.STANDARD;
	public int sleep;
	public boolean gfx;
	
	private int p;
	private boolean m = false;
	private boolean d = false;
	
	public GameArguments(String[] args) {
		players = new AI[2];
		mapName = "a";
		p = -1;
		sleep = 40;
		gfx = true;
		setup(args);
	}

	public GameArguments(boolean gfx, AI p1, AI p2, String mapName, DECK_SIZE deckSize) {
		players = new AI[2];
		players[0] = p1;
		players[1] = p2;
		this.mapName = mapName;
		p = -1;
		this.gfx = gfx;
		sleep = 40;
		this.deckSize = deckSize;
	}

	private void setup(String[] args) {
		for (int a = 0; a < args.length; a++) {
			if (args[a].toLowerCase().equals("map")) {
				m = true;
				continue;
			}
			if (args[a].toLowerCase().equals("deck")) {
				d = true;
				continue;
			}
			if (args[a].toLowerCase().equals("p1")) {
				p = 0;
				continue;
			} else if (args[a].toLowerCase().equals("p2")) {
				p = 1;
				continue;
			}
			if (m){
				mapName = args[a];
				m = false;
				continue;
			}
			if (d){
				if (args[a].equals("standard"))
					deckSize = DECK_SIZE.STANDARD;
				if (args[a].equals("small"))
					deckSize = DECK_SIZE.SMALL;
				if (args[a].equals("tiny"))
					deckSize = DECK_SIZE.TINY;
				d = false;
				continue;
			}
			if (p == 0 || p == 1) {
				if (args[a].toLowerCase().equals("human"))
					players[p] = null;
				else if (args[a].toLowerCase().equals("random"))
					players[p] = new RandomAI(RAND_METHOD.TREE);
				else if (args[a].toLowerCase().equals("randomheuristic"))
					players[p] = new RandomHeuristicAI(new ComplexActionComparator());
				else if (args[a].toLowerCase().equals("heuristic"))
					players[p] = new HeuristicAI(new ComplexActionComparator());
				else if (args[a].toLowerCase().equals("scanrandom"))
					players[p] = new ScanRandomAI((p == 0));
				else if (args[a].toLowerCase().equals("nmsearch")) {
					a++;
					final int n = Integer.parseInt(args[a]);
					a++;
					final int mm = Integer.parseInt(args[a]);
					a++;
					if (args[a].toLowerCase().equals("heuristic"))
						players[p] = new NmSearchAI((p == 0), n, mm,
								new HeuristicEvaluation());
					else {
						a++;
						final int rolls = Integer.parseInt(args[a]);
						a++;
						final int depth = Integer.parseInt(args[a]);
						players[p] = new NmSearchAI((p == 0), n, mm,
								new RolloutEvaluation(rolls, depth,
										new RandomAI(RAND_METHOD.TREE),
										new HeuristicEvaluation(), true));
					}

				}
				if (args[a].toLowerCase().equals("greedyaction")) {
					a++;
					if (args[a].toLowerCase().equals("heuristic"))
						players[p] = new GreedyActionAI(
								new HeuristicEvaluation());
					else if (args[a].toLowerCase().equals("rollouts")) {
						a++;
						final int rolls = Integer.parseInt(args[a]);
						a++;
						final int depth = Integer.parseInt(args[a]);
						players[p] = new GreedyActionAI(new RolloutEvaluation(
								rolls, depth, new RandomAI(RAND_METHOD.TREE),
								new HeuristicEvaluation(), true));
					}
				}
				if (args[a].toLowerCase().equals("greedyturn")) {
					a++;
					if (args[a].toLowerCase().equals("heuristic"))
						players[p] = new GreedyTurnAI(new HeuristicEvaluation());
					else if (args[a].toLowerCase().equals("rollouts")) {
						a++;
						final int rolls = Integer.parseInt(args[a]);
						a++;
						final int depth = Integer.parseInt(args[a]);
						players[p] = new GreedyTurnAI(new RolloutEvaluation(
								rolls, depth, new RandomAI(RAND_METHOD.TREE),
								new HeuristicEvaluation(), true));
					}

				}
				if (args[a].toLowerCase().equals("mcts")) {
					a++;
					final int t = Integer.parseInt(args[a]);
					players[p] = new Mcts(t, new RolloutEvaluation(
							1, 20, new RandomHeuristicAI(new ComplexActionComparator()),
							new MaterialBalanceEvaluation(true), false));
				}
				if (args[a].toLowerCase().equals("evolution"))
					players[p] = new RollingHorizonEvolution(200, .5, .35, 1200,
							new RolloutEvaluation(5, 1, new RandomHeuristicAI(new ComplexActionComparator()),
									new HeuristicEvaluation(), false, true), false);
				p = -1;
			} else if (args[a].toLowerCase().equals("sleep")) {
				a++;
				sleep = Integer.parseInt(args[a]);
				continue;
			} else if (args[a].toLowerCase().equals("gfx")) {
				a++;
				gfx = Boolean.parseBoolean(args[a]);
				continue;
			}
		}
	}

	
	
}
