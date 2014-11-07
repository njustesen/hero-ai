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

public class EvaAI implements AI {
	
	private static final int SOLUTIONS = 200;
	private static final int TESTS = 20000;
	public boolean p1;
	private AI p1Ai;
	private AI p2Ai;
	private List<Action> foundActions;
	private int rolls;

	public EvaAI(boolean p1){
		this.p1 = p1;
		this.p1Ai = new RandomMemAI(p1);
		this.p2Ai = new RandomMemAI(!p1);
		this.foundActions = new ArrayList<Action>();
		this.rolls = 0;
	}
	
	@Override
	public Action act(GameState state, long ms) {
		
		if (!foundActions.isEmpty()){
			Action action = foundActions.get(0);
			foundActions.remove(0);
			return action;
		}
		
		this.rolls = 0;

		List<GameState> states = new ArrayList<GameState>();
		List<List<Action>> moves = new ArrayList<List<Action>>();
		
		for(int i = 0; i < SOLUTIONS; i++){
			GameState clone = state.copy();
			List<Action> actions = new ArrayList<Action>();
			while(true){
				Action action = p1Ai.act(clone, 0);
				actions.add(action);
				clone.update(action);
				//System.out.println(action);
				if (action instanceof EndTurnAction)
					break;
			}
			//System.out.println("------------");
			states.add(clone);
			moves.add(actions);
		}
		
		foundActions = findBest(states, moves);
		System.out.println("--- Best --- Rolls: " + rolls);
		for(Action action : foundActions)
			System.out.println(action);
		System.out.println("------------");
		Action action = foundActions.get(0);
		foundActions.remove(0);
		
		return action;
	}

	private List<Action> findBest(List<GameState> states, List<List<Action>> moves) {
		
		List<Double> values = new ArrayList<Double>();
		
		for(GameState state : states){
			double value = evaluate(state, TESTS);
			values.add(value);
		}
		
		double highest = -1000000;
		int best = -1;
		for(int i = 0; i < states.size(); i++){
			if (values.get(i) > highest){
				highest = values.get(i);
				best = i;
			}
		}
		
		System.out.println(highest);
		
		return moves.get(best);

	}

	private double evaluate(GameState state, int runs) {
		
		double worst = 1000000;
		long ns = System.nanoTime();
		for(int r = 0; r < runs; r++){
			
			GameState clone = state.copy();
			
			while(true){
				Action action = p2Ai.act(clone, 0);
				clone.update(action);
				if (action instanceof EndTurnAction)
					break;
			}
			
			double value = value(state, p1);
			if (value < worst)
				worst = value;
			
			rolls++;
		}
		//System.out.println(System.nanoTime() - ns);
		
		return worst;
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
		
		return p1Units - p2Units;
		
	}

}
