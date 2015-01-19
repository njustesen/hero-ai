package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.List;

import lib.Card;
import action.Action;
import action.EndTurnAction;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.IHeuristic;
import ai.util.RAND_METHOD;

public class NmSearchAI implements AI {

	public boolean p1;
	private final AI p1Ai;
	private final AI p2Ai;
	private List<Action> foundActions;
	private final int n;
	private final int m;
	private IHeuristic heuristic;
	
	public NmSearchAI(boolean p1, int n, int m, IHeuristic heuristic) {
		this.p1 = p1;
		p1Ai = new RandomAI(RAND_METHOD.TREE);
		p2Ai = new RandomAI(RAND_METHOD.TREE);
		foundActions = new ArrayList<Action>();
		this.n = n;
		this.m = m;
		this.heuristic = heuristic;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (!foundActions.isEmpty()) {
			final Action action = foundActions.get(0);
			foundActions.remove(0);
			return action;
		}

		final List<GameState> states = new ArrayList<GameState>();
		final List<List<Action>> moves = new ArrayList<List<Action>>();

		for (int i = 0; i < n; i++) {
			final GameState clone = state.copy();
			final List<Action> actions = new ArrayList<Action>();
			while (true) {
				final Action action = p1Ai.act(clone, 0);
				actions.add(action);
				clone.update(action);
				if (action instanceof EndTurnAction)
					break;
			}
			states.add(clone);
			moves.add(actions);
		}

		foundActions = findBest(states, moves);
		// System.out.println("--- Best --- {n: " + n + "; m: " + m + "}");
		// for(Action action : foundActions)
		// System.out.println(action);
		// System.out.println("------------");
		final Action action = foundActions.get(0);
		foundActions.remove(0);

		return action;
	}

	private void randomizeHand(GameState clone, boolean player1) {

		if (player1) {
			for (final Card card : clone.p1Hand)
				clone.p1Deck.add(card);
			clone.p1Hand.clear();
			drawCards(clone.p1Hand, clone.p1Deck);
		} else {
			for (final Card card : clone.p2Hand)
				clone.p2Deck.add(card);
			clone.p2Hand.clear();
			drawCards(clone.p2Hand, clone.p2Deck);
		}
	}

	private void drawCards(List<Card> hand, List<Card> deck) {

		while (hand.size() < 6 && !deck.isEmpty()) {
			final int idx = (int) (Math.random() * deck.size());
			final Card card = deck.get(idx);
			deck.remove(idx);
			hand.add(card);
		}

	}

	private List<Action> findBest(List<GameState> states,
			List<List<Action>> moves) {

		final List<Double> values = new ArrayList<Double>();

		for (final GameState state : states) {
			final double value = evaluate(state, m);
			values.add(value);
		}

		double oppWorstVal = 1000000;
		int best = -1;
		for (int i = 0; i < states.size(); i++)
			if (values.get(i) < oppWorstVal) {
				oppWorstVal = values.get(i);
				best = i;
			}

		// System.out.println(highest);

		return moves.get(best);

	}

	private double evaluate(GameState state, int runs) {

		double oppBest = -1000000;
		for (int r = 0; r < runs; r++) {

			final GameState clone = state.copy();
			randomizeHand(clone, !p1);
			while (true) {
				final Action action = p2Ai.act(clone, 0);
				clone.update(action);
				if (action instanceof EndTurnAction)
					break;
			}

			final double value = heuristic.eval(state, !p1);
			if (value > oppBest)
				oppBest = value;
		}

		return oppBest;
	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
