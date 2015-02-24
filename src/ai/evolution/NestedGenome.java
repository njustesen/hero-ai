package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.heuristic.IHeuristic;

public class NestedGenome extends Genome {

	public GameState state;
	public IHeuristic heuristic;
	public double killRate;
	public int popSize;
	public double mutRate;
	
	private List<Genome> pop;
	private int nested;
	private GameState clone;
	private List<Genome> killed;
	
	public NestedGenome(int nested, int popSize, double killRate, double mutRate) {
		super();
		this.nested = nested;
		this.popSize = popSize;
		this.killRate = killRate;
		this.mutRate = mutRate;
	}
	
	@Override
	public double fitness(){
		return value;
	}

	public double run(){
		if (pop == null)
			init();
		
		if (state == null)
			System.out.println("STATE IS NULL");
		
		// FITNESS
		for (final Genome genome : pop) {
			// System.out.print("|");
			clone.imitate(state);
			clone.update(genome.actions);
			final double val = heuristic.eval(clone, state.p1Turn);
			if (genome.visits == 0 || val < genome.value)
				genome.value = val;
			if (genome.value < value)
				value = genome.value;
			genome.visits++;
		}
		
		// Kill worst genomes
		Collections.sort(pop);
		killed.clear();
		final int idx = (int) Math.floor(pop.size() * killRate);
		for (int i = idx; i < pop.size(); i++)
			killed.add(pop.get(i));
		
		// Crossover new ones
		for (final Genome genome : killed) {
			final int a = random.nextInt(idx);
			int b = random.nextInt(idx);
			while (b == a)
				b = random.nextInt(idx);

			clone.imitate(state);
			genome.crossover(pop.get(a), pop.get(b), clone);
			
			// Mutation
			if (Math.random() < mutRate) {
				clone.imitate(state);
				genome.mutate(clone);
			}

		}
		
		return value;
	}

	private void init() {
		pop = new ArrayList<Genome>(nested);
		killed = new ArrayList<Genome>();
		for(int i = 0; i < nested; i++)
			pop.add(new WeakGenome());
	}

	@Override
	public String toString() {
		return "Genome [value=" + value + ", visits="+ visits + ", fitness=" + fitness() + "]";
	}
	
}
