package ai.evolution;

import java.util.ArrayList;

import action.Action;

public class StrongGenome extends Genome {

	public int g;

	public StrongGenome(int g) {
		super();
		actions = new ArrayList<Action>();
		value = 0;
		visits = 0;
		this.g = g;
	}

	public double fitness() {
		return value + (Math.abs(value) * (Math.sqrt(visits-1)/g));
	}
	
}
