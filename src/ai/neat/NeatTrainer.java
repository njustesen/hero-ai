package ai.neat;

import game.Game;
import game.GameArguments;
import game.GameState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import ai.neat.jneat.Neat;
import ai.neat.jneat.Network;
import ai.neat.jneat.Organism;
import ai.neat.jneat.Population;
import ai.neat.jneat.Species;

public class NeatTrainer {

	private static final int POP_SIZE = 100;
	private static final double PROP_LINK = 0.5;
	private static final boolean RECURRENT = true;
	private static final int GENERATIONS = 100000;
	private static final int MATCHES = 50;
	private static final int CHECKS = 1000;
	private static Random random;
	private static GameArguments gameArgs;

	public static void main(String[] args) throws Exception{
		
		Population pop = null;
		random = new Random();
		
		Neat.p_num_trait_params = 10;
		//Neat.p_dropoff_age = 50;
		Neat.p_age_significance = 1.1;
		Neat.p_survival_thresh = 0.9;
		Neat.p_compat_threshold = 0.2;
		Neat.readParam("parameters.ne");
		
		pop = new Population(POP_SIZE,  10, 1, 50, RECURRENT, PROP_LINK);
		
		pop.verify();
		
		Network bestNet = null;
		double bestFitness = -1;
		
		for (int gen = 1; gen <= GENERATIONS; gen++) {
			System.out.print("\n---------------- Generation ----------------------" + gen);
			bestFitness = -1;
			
			Iterator itr_organism = pop.getOrganisms().iterator();
			while (itr_organism.hasNext()) {
				Organism organism = ((Organism) itr_organism.next());
				//double fitness = fitness(organism, pop.getOrganisms());
				double fitness = fitness(organism.getNet(), pop.getOrganisms(), MATCHES);
				organism.setFitness(fitness);
				if (fitness > bestFitness){
					bestFitness = fitness;
					bestNet = organism.net;
				}
			}
			
			//compute average and max fitness for each species
			// Necessary? OR does it happen in epoch?
			Iterator itr_specie;
			itr_specie = pop.species.iterator();
			while (itr_specie.hasNext()) {
				Species _specie = ((Species) itr_specie.next());
				_specie.compute_average_fitness();
				_specie.compute_max_fitness();
			}
			
			// EPOCH
			pop.epoch(gen);
			
			// Play best against random
			double objFitness = fitness(bestNet, pop.getOrganisms(), CHECKS);
			
			System.out.println(" - Best network fitness: " + objFitness + ", nodes: " + bestNet.getAllnodes().size());
			if (objFitness == 1.0){
				System.out.println("Evolution done");
				break;
			}
			
			//System.out.print("\n  Population : innov num   = " + pop.getCur_innov_num());
			//System.out.print("\n             : cur_node_id = " + pop.getCur_node_id());
			//System.out.print("\n   result    : " + pop.);
		}
		
		System.out.println("");
		
	}

	private static double fitness(Network net, Vector organisms, int runs) {
		
		List<Organism> played = new ArrayList<Organism>();
		
		double sum = 0;
		Game game = new Game(new GameState(null), gameArgs);
		for(int i = 0; i < runs; i++){
			if (i != 0)
				game.state = new GameState(game.state.map);
			Organism other = null;
			while(other == null || other.getNet().equals(net))
				other = (Organism) organisms.get(random.nextInt(organisms.size()));
			game.player1 = new NaiveNeatAI(net);
			game.player2 = new NaiveNeatAI(other.getNet());
			game.run();
			double val = game.state.getWinner();
			sum += score(1, val);
			played.add(other);
		}
		
		return sum;
	}

	private static double score(int p, double winner) {
		if (p == winner)
			return 1;
		if (p == 0)
			return 0.5;
		return 0;
	}
	
}
