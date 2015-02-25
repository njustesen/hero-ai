package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.heuristic.IHeuristic;

public class MaxGenome extends Genome {

	public GameState state;
	public IHeuristic rolloutHeuristic;
	public IHeuristic heuristic;
	public double killRate;
	public int popSize;
	public double mutRate;
	
	public List<Genome> pop;
	private GameState clone;
	private List<Genome> killed;
	private int idx;
	
	public MaxGenome(int popSize, double killRate, double mutRate, IHeuristic rolloutHeuristic, IHeuristic heuristic) {
		super();
		this.popSize = popSize;
		this.killRate = killRate;
		this.mutRate = mutRate;
		this.heuristic = heuristic;
		this.rolloutHeuristic = rolloutHeuristic;
		this.idx = popSize - (int) Math.floor(popSize * killRate);
	}
	
	@Override
	public double fitness(){
		return value;
	}

	public void run(){
		
		if (pop == null)
			setupEvo();
		
		if (clone.isTerminal){
			value = heuristic.eval(clone, !state.p1Turn);
			return;
		}

		// Crossover - reuse killed
		if (visits > 0)
			crossover(killed);
		
		for(int g = 0; g < 1; g++){
			int to = g == 0 ? pop.size() : idx;
			for (int i = 0; i < to; i++) {
				clone.imitate(state);
				clone.update(pop.get(i).actions);
				double val = rolloutHeuristic.eval(clone, !state.p1Turn);
				// MAX of own next move 
				if (pop.get(i).visits == 0 || val > pop.get(i).value)
					pop.get(i).value = val;
				pop.get(i).visits++;
			}
			Collections.sort(pop);
		}
		
		// MIN of opponent next move
		value = pop.get(0).value;
		
		// Kill worst genomes
		killed.clear();
		for (int i = idx; i < pop.size(); i++)
			killed.add(pop.get(i));
		
	}
	
	private void crossover(List<Genome> genomes) {
		for (final Genome genome : genomes) {
			final int a = random.nextInt(idx);
			int b = random.nextInt(idx);
			while (b == a)
				b = random.nextInt(idx);

			clone.imitate(state);
			genome.crossover(pop.get(a), pop.get(b), clone);
			
			if (genome.actions.isEmpty())
				continue;
				
			// Mutation
			if (Math.random() < mutRate) {
				clone.imitate(state);
				genome.mutate(clone);
			}
		}
	}
	
	public void clear() {
		setupEvo();
		visits = 0;
	}

	private void setupEvo() {
		clone = state.copy();
		pop = new ArrayList<Genome>(popSize);
		killed = new ArrayList<Genome>();
		for(int i = 0; i < popSize; i++){
			clone.imitate(state);
			Genome genome = new MinGenome();
			genome.random(clone);
			pop.add(genome);
		}
	}

	@Override
	public String toString() {
		return "Genome [value=" + value + ", visits="+ visits + ", fitness=" + fitness() + "]";
	}
	
}
