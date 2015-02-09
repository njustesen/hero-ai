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
		actions = new ArrayList<Action>();
		value = 0;
		visits = 0;
	}

	public void random(GameState state) {
		final List<Action> possible = new ArrayList<Action>();
		actions.clear();
		visits = 0;
		value = 0;
		final boolean p1Turn = state.p1Turn;
		state.possibleActions(possible);
		while (!state.isTerminal && p1Turn == state.p1Turn) {
			state.possibleActions(possible);
			if (p1Turn == state.p1Turn && possible.isEmpty()) {
				actions.add(SingletonAction.endTurnAction);
				break;
			}
			final int idx = (int) (Math.random() * possible.size());
			actions.add(possible.get(idx));
			state.update(possible.get(idx));
		}

	}

	public void crossover(Genome a, Genome b, GameState state) {
		actions.clear();
		visits = 0;
		value = 0;
		final ArrayList<Action> possible = new ArrayList<Action>();
		for (int i = 0; i < a.actions.size(); i++) {
			state.possibleActions(possible);
			if (random.nextBoolean() && hasMove(a, possible, i))
				actions.add(a.actions.get(i));
			else if (hasMove(b, possible, i))
				actions.add(b.actions.get(i));
			else if (hasMove(a, possible, i))
				actions.add(a.actions.get(i));
			else
				actions.add(possible.get(random.nextInt(possible.size())));
			state.update(actions.get(i));
		}
		if (actions.contains(null))
			System.out.println("null");
	}

	private boolean hasMove(Genome g, ArrayList<Action> possible, int i) {
		if (g.actions.size() <= i)
			return false;

		if (possible.contains(g.actions.get(i)))
			return true;

		return false;
	}

	public void mutate(GameState state) {

		final int mutIdx = random.nextInt(actions.size());
		final List<Action> possible = new ArrayList<Action>();
		int i = 0;
		for (final Action action : actions) {
			if (i == mutIdx)
				actions.set(mutIdx, newAction(state, action));
			else if (i > mutIdx) {
				state.possibleActions(possible);
				if (!possible.contains(action))
					actions.set(i, newAction(state, action));
			}
			state.update(actions.get(i));
			i++;
		}

	}

	private Action newAction(GameState state, Action action) {

		final List<Action> possibleActions = new ArrayList<Action>();
		state.possibleActions(possibleActions);
		possibleActions.remove(action);

		if (possibleActions.isEmpty())
			return SingletonAction.endTurnAction;

		final int idx = (int) (Math.random() * possibleActions.size());

		return possibleActions.get(idx);
	}

	@Override
	public int compareTo(Genome other) {
		final double avg = avgValue() + visits * 100;
		final double otherAvg = other.avgValue() + visits * 100;
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
		final ArrayList<Action> possible = new ArrayList<Action>();
		for (final Action action : actions) {
			clone.possibleActions(possible);
			if (!possible.contains(action))
				return false;
			clone.update(action);
		}
		return true;
	}

}
