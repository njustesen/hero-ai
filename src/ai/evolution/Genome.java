package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import action.Action;
import action.SingletonAction;

public class Genome implements Comparable<Genome> {

	public static Random random = new Random();
	public List<Action> actions;
	public double value;
	public int visits;
	
	public Genome() {
		super();
		this.actions = new ArrayList<Action>();
		this.value = 0;
		this.visits = 0;
	}
	
	public void random(GameState state) {
		List<Action> possible = new ArrayList<Action>();
		actions.clear();
		visits = 0;
		value = 0;
		boolean p1Turn = state.p1Turn;
		state.possibleActions(possible);
		while (!state.isTerminal && p1Turn == state.p1Turn){
			state.possibleActions(possible);
			if (p1Turn == state.p1Turn && possible.isEmpty()){
				actions.add(SingletonAction.endTurnAction);
				break;
			}
			int idx = (int) (Math.random() * possible.size());
			actions.add(possible.get(idx));
			state.update(possible.get(idx));
		}
			
	}

	public void crossover(Genome a, Genome b, GameState state) {
		actions.clear();
		visits = 0;
		value = 0;
		ArrayList<Action> possible = new ArrayList<Action>();
		for(int i = 0; i < a.actions.size(); i++){
			state.possibleActions(possible);
			if (random.nextBoolean() && possible.contains(a.actions.get(i))){
				actions.add(a.actions.get(i));
			} else if (possible.contains(b.actions.get(i))){
				actions.add(b.actions.get(i));
			} else if (possible.contains(a.actions.get(i))){
				actions.add(a.actions.get(i));
			} else {
				System.out.println("Should not happen!");
			}
			state.update(actions.get(i));
		}
		if (actions.contains(null))
			System.out.println("null");
	}
	
	public void mutate(GameState state){
		
		int mutIdx = random.nextInt(actions.size());
		List<Action> possible = new ArrayList<Action>();
		
		int i = 0;
		for(Action action : actions){
			if (i==mutIdx){
				actions.set(mutIdx, newAction(state, action));
			} else if (i > mutIdx){
				state.possibleActions(possible);
				if (!possible.contains(action))
					actions.set(i, newAction(state, action));
			}
			if (i < actions.size() - 1){
				state.update(action);
				i++;
			}
		}
		
	}

	private Action newAction(GameState state, Action action) {
		
		List<Action> possibleActions = new ArrayList<Action>();
		state.possibleActions(possibleActions);
		possibleActions.remove(action);
		
		if (possibleActions.isEmpty())
			return action;
		
		int idx = (int) (Math.random() * possibleActions.size());
		
		return possibleActions.get(idx);
	}

	@Override
	public int compareTo(Genome other) {
		double avg = avgValue();
		double otherAvg = other.avgValue();
		if (avg == otherAvg)
			return 0;
		if (avg > otherAvg)
			return -1;
		return 1;
	}

	public double avgValue() {
		if (visits == 0)
			return 0;
		return value / visits;
	}

	public boolean isLegal(GameState clone) {
		// TODO : create
		return false;
	}
	
}
