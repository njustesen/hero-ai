package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeSet;

import model.HaMap;
import action.Action;
import ai.AI;
import ai.evaluation.IStateEvaluator;

public class NestedEvolution implements AI {

	public int popSize;
	public int nestedPopSize;
	public int generations;
	public double killRate;
	public double mutRate;
	public IStateEvaluator evaluator;
	
	private final List<List<MaxGenome>> pops;
	private List<Action> actions;
	private final Random random;
	private final int idx;

	public NestedEvolution(int popSize, int nestedPopSize, double mutRate,
			double killRate, int generations, IStateEvaluator evaluator) {
		super();
		this.popSize = popSize;
		this.nestedPopSize = nestedPopSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.killRate = killRate;
		this.evaluator = evaluator;
		this.idx = popSize - (int) Math.floor(popSize * killRate);
		pops = new ArrayList<List<MaxGenome>>();
		actions = new ArrayList<Action>();
		random = new Random();
	}

	@Override
	public Action act(GameState state, long ms) {

		if (actions.isEmpty())
			search(state);

		final Action next = actions.get(0);
		actions.remove(0);
		return next;
	}

	private void search(GameState state) {

		setup(state);

		final List<MaxGenome> killed = new ArrayList<MaxGenome>();
		final GameState clone = state.copy();
		
		List<MaxGenome> base = pops.get(0);
		
		for (int g = 0; g < generations; g++) {
			
			// Crossover - reuse killed genomes
			if (g > 0){
				int pa;
				int pb;
				int a;
				int b;
				for (final MaxGenome genome : killed) {
					pa = random.nextInt(pops.size() - 1) + 1;
					a = random.nextInt(pops.get(pa).size());
					pb = random.nextInt(pops.size() - 1) + 1;
					b = random.nextInt(pops.get(pb).size());
					while (pa==pb && b == a){
						pb = random.nextInt(pops.size() - 1) + 1;
						b = random.nextInt(pops.get(pb).size());
					}
					clone.imitate(state);
					genome.visits = 0;
					genome.crossover(pops.get(pa).get(a), pops.get(pb).get(b), clone);
					base.add(genome);
					// Mutation
					if (random.nextFloat() < mutRate) {
						clone.imitate(state);
						genome.mutate(clone);
					}
	
				}
			}
			
			// Test
			for (List<MaxGenome> pop : pops){
				for (int i = 0; i < pop.size(); i++) {
					//for(int r = 0; r < pops.indexOf(pop)+1; r++){
						clone.imitate(state);
						clone.update(pop.get(i).actions);
						pop.get(i).state = clone;
						pop.get(i).run();
						pop.get(i).visits++;
					//}
				}
				Collections.sort(pop);
			}
			
			// Advance and kill
			killed.clear();
			for (int p = pops.size()-1; p >= 0; p--){
				if (pops.get(p).size() <= 2)
					continue;
				for (int i = 0; i < pops.get(p).size(); i++) {
					if (i <= (pops.get(p).size() / 2) - 1){
						if (pops.size() == p+1)
							pops.add(new ArrayList<MaxGenome>());
						pops.get(p+1).add(pops.get(p).get(i));
					} else if (p==0){
						killed.add(pops.get(p).get(i));
					} else {
						pops.get(p-1).add(pops.get(p).get(i));
					}
				}
				pops.get(p).clear();
				Collections.sort(pops.get(p));
			}
			/*
			System.out.println("---");
			for(List<MaxGenome> pop : pops){
				for(MaxGenome gen : pop)
					System.out.print(gen.value + ", ");
				System.out.print("\n");
			}
			*/
		}

		MaxGenome best = pops.get(pops.size()-1).get(0);
		actions = best.actions;
		
	}

	private void setup(GameState state) {

		pops.clear();
		pops.add(new ArrayList<MaxGenome>());
		GameState clone = state.copy();

		for (int i = 0; i < popSize; i++) {
			if (i > 0)
				clone.imitate(state);
			final MaxGenome genome = new MaxGenome(nestedPopSize, killRate, mutRate, evaluator);
			genome.random(clone);
			pops.get(0).add(genome);
		}

	}

	@Override
	public void init(GameState state, long ms) {
		// TODO
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Pop. size = " + popSize + "\n";
		name += "Nested pop. size = " + nestedPopSize + "\n";
		name += "Generations = " + generations + "\n";
		name += "Mut. rate = " + mutRate + "\n";
		name += "Kill rate = " + killRate + "\n";
		name += "State evaluator = " + evaluator.title() + "\n";
		
		return name;
	}

	@Override
	public String title() {
		return "Nested Evolution";
	}

}
