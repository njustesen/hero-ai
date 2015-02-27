package ai.evolution;

import java.util.ArrayList;

import action.Action;

public class StrongGenome extends Genome {

	static int g = 1000;

	public StrongGenome() {
		super();
		actions = new ArrayList<Action>();
		value = 0;
		visits = 0;
	}

	public double fitness() {
		return value + (Math.abs(value) * (Math.sqrt(visits-1)/g));
	}
	
}
