package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.Card;
import lib.CardType;
import lib.UnitClassLib;
import model.Position;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;
import action.UnitActionType;
import game.AI;
import game.GameState;

public class NmSearchAI implements AI {
	
	public boolean p1;
	private AI p1Ai;
	private AI p2Ai;
	private List<Action> foundActions;
	private int n;
	private int m;

	public NmSearchAI(boolean p1, int n, int m){
		this.p1 = p1;
		this.p1Ai = new RandomAI(p1, RAND_METHOD.TREE);
		this.p2Ai = new RandomAI(!p1, RAND_METHOD.TREE);
		this.foundActions = new ArrayList<Action>();
		this.n = n;
		this.m = m;
	}
	
	@Override
	public Action act(GameState state, long ms) {
		
		if (!foundActions.isEmpty()){
			Action action = foundActions.get(0);
			foundActions.remove(0);
			return action;
		}
		
		List<GameState> states = new ArrayList<GameState>();
		List<List<Action>> moves = new ArrayList<List<Action>>();
		
		for(int i = 0; i < n; i++){
			GameState clone = state.copy();
			List<Action> actions = new ArrayList<Action>();
			while(true){
				Action action = p1Ai.act(clone, 0);
				actions.add(action);
				clone.update(action);
				if (action instanceof EndTurnAction)
					break;
			}
			states.add(clone);
			moves.add(actions);
		}
		
		foundActions = findBest(states, moves);
		//System.out.println("--- Best --- {n: " + n + "; m: " + m + "}");
		//for(Action action : foundActions)
		//	System.out.println(action);
		//System.out.println("------------");
		Action action = foundActions.get(0);
		foundActions.remove(0);
		
		return action;
	}

	private void randomizeHand(GameState clone, boolean player1) {
		
		if (player1){
			for(Card card : clone.p1Hand){
				clone.p1Deck.add(card);
			}
			clone.p1Hand.clear();
			drawCards(clone.p1Hand, clone.p1Deck);
		} else {
			for(Card card : clone.p2Hand){
				clone.p2Deck.add(card);
			}
			clone.p2Hand.clear();
			drawCards(clone.p2Hand, clone.p2Deck);
		}
	}
	
	private void drawCards(List<Card> hand, List<Card> deck) {
		
		while(hand.size() < 6 && !deck.isEmpty()){
			int idx = (int) (Math.random() * deck.size());
			Card card = deck.get(idx);
			deck.remove(idx);
			hand.add(card);
		}
		
	}

	private List<Action> findBest(List<GameState> states, List<List<Action>> moves) {
		
		List<Double> values = new ArrayList<Double>();
		
		for(GameState state : states){
			double value = evaluate(state, m);
			values.add(value);
		}
		
		double oppWorstVal = 1000000;
		int best = -1;
		for(int i = 0; i < states.size(); i++){
			if (values.get(i) < oppWorstVal){
				oppWorstVal = values.get(i);
				best = i;
			}
		}
		
		//System.out.println(highest);
		
		return moves.get(best);

	}

	private double evaluate(GameState state, int runs) {
		
		double oppBest = -1000000;
		for(int r = 0; r < runs; r++){
			
			GameState clone = state.copy();
			randomizeHand(clone, !p1);
			while(true){
				Action action = p2Ai.act(clone, 0);
				clone.update(action);
				if (action instanceof EndTurnAction)
					break;
			}
			
			double value = value(state, !p1);
			if (value > oppBest)
				oppBest = value;
		}
		
		return oppBest;
	}

	private double value(GameState state, boolean p1) {
		/*
		int p1CrystalHP = 0;
		int p2CrystalHP = 0;
		for(Position pos : state.map.p1Crystals){
			if (state.squares[pos.x][pos.y].unit != null 
					&& state.squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL){
				p1CrystalHP += state.squares[pos.x][pos.y].unit.hp;
			}
		}
		for(Position pos : state.map.p2Crystals){
			if (state.squares[pos.x][pos.y].unit != null 
					&& state.squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL){
				p2CrystalHP += state.squares[pos.x][pos.y].unit.hp;
			}
		}
		*/
		
		int p1Units = 0;
		int p2Units = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.squares[x][y].unit != null){
					int m = 0;
					if (state.squares[x][y].unit.hp != 0)
						m = state.squares[x][y].unit.unitClass.maxHP;
					if ( state.squares[x][y].unit.p1Owner)
						p1Units += state.squares[x][y].unit.hp + m;
					else 
						p2Units += state.squares[x][y].unit.hp + m;
				}
			}
		}
		for(Card card : state.p1Deck){
			if (card.type == CardType.UNIT){
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for(Card card : state.p1Hand){
			if (card.type == CardType.UNIT){
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for(Card card : state.p2Hand){
			if (card.type == CardType.UNIT){
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for(Card card : state.p2Deck){
			if (card.type == CardType.UNIT){
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		
		if (p1)
			return p1Units - p2Units;
		return p2Units - p1Units;
	}

}
