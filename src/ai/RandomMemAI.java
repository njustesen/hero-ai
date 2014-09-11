package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.Card;
import lib.CardType;
import model.Position;
import model.Square;
import model.SquareType;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;
import game.AI;
import game.GameState;

public class RandomMemAI implements AI {
	
	private static final double PROP_HAND = 0.25;
	private static final double SWAP_PROP = 0.20;
	private static final double HEAL_PROP = 0.60;
	public boolean p1;
	private List<Position> myUnits;
	private List<Position> enemyUnits;
	private List<Position> emptySpaces;
	private List<Integer> handOrder;
	
	public RandomMemAI(boolean p1){
		this.p1 = p1;
		this.handOrder = new ArrayList<Integer>();
		this.handOrder.add(0);
		this.handOrder.add(1);
		this.handOrder.add(2);
		this.handOrder.add(3);
		this.handOrder.add(4);
		this.handOrder.add(5);
		this.myUnits = new ArrayList<Position>();
		this.enemyUnits = new ArrayList<Position>();
		this.emptySpaces = new ArrayList<Position>();
	}
	
	@Override
	public Action act(GameState state, long ms) {
		//long aiStart = System.nanoTime();
		
		if (state.APLeft <= 0)
			return new EndTurnAction();
		
		locateUnits(state);
		
		Collections.shuffle(myUnits);
		Collections.shuffle(enemyUnits);
		Collections.shuffle(emptySpaces);
		
		Action action = null;
		if (Math.random() < PROP_HAND){
			action = handAction(state);
			if (action == null)
				action = unitAction(state);
		}
		if (action == null){
			action = unitAction(state);
			if (action == null)
				action = handAction(state);
		}
		
		//long aiEnd = System.nanoTime();
		//System.out.println("Move took " + ((aiEnd - aiStart)/1000000d) + " " + selected);
		
		//System.out.println(selected);
		
		//System.out.println(action);
		
		return action;
	}

	private Action unitAction(GameState state) {
		
		Action action = null;
		
		for(Position pos : myUnits){
			
			if (state.squares[pos.x][pos.y].unit.hp <= 0)
				continue;
			
			List<UnitActionType> types = new ArrayList<UnitActionType>();
			
			if (state.squares[pos.x][pos.y].unit.unitClass.swap)
				types.add(UnitActionType.SWAP);
			
			if (state.squares[pos.x][pos.y].unit.unitClass.heal != null)
				types.add(UnitActionType.HEAL);
			
			if (state.squares[pos.x][pos.y].unit.unitClass.speed > 0)
				types.add(UnitActionType.MOVE);
			
			if (state.squares[pos.x][pos.y].unit.unitClass.attack != null)
				types.add(UnitActionType.ATTACK);
			
			Collections.shuffle(types);
			
			for(UnitActionType type : types){
				switch (type) {
				case MOVE: action = moveAction(state, pos); break;
				case ATTACK: action = attackAction(state, pos); break;
				case HEAL: action = healAction(state, pos); break;
				case SWAP: action = swapAction(state, pos); break;
				}
				if (action != null)
					break;
			}
				
			if (action != null)
				return action;
			
		}
		
		return action;
	}

	private Action swapAction(GameState state, Position pos) {
		
		for(Position other : myUnits){
			
			if (other.equals(pos) || state.squares[other.x][other.y].unit.hp <= 0)
				continue;
			
			return new UnitAction(pos, other);
			
		}
		
		return null;
	}

	private Action healAction(GameState state, Position pos) {
		
		for(Position other : myUnits){
			
			if (other.equals(pos) || state.squares[other.x][other.y].unit.fullHealth())
				continue;
			
			if (state.squares[other.x][other.y].unit.unitClass.card == Card.CRYSTAL)
				continue;
			
			if (state.distance(pos, other) > state.squares[pos.x][pos.y].unit.unitClass.heal.range)
				continue;
			
			return new UnitAction(pos, other);
			
		}
		
		return null;
	}

	private Action attackAction(GameState state, Position pos) {
		
		if (state.squares[pos.x][pos.y].unit.unitClass.attack == null)
			return null;
		
		for(Position other : enemyUnits){
			
			if (state.squares[other.x][other.y].unit.hp <= 0 
					&& state.distance(pos, other) > state.squares[pos.x][pos.y].unit.unitClass.speed)
				continue;
			
			if (state.squares[other.x][other.y].unit.hp > 0 
					&& state.distance(pos, other) > state.squares[pos.x][pos.y].unit.unitClass.attack.range)
				continue;
			
			return new UnitAction(pos, other);
			
		}
		
		return null;
	}

	private Action moveAction(GameState state, Position pos) {
		
		for(Position empty : emptySpaces){
			
			if (state.distance(pos, empty) > state.squares[pos.x][pos.y].unit.unitClass.speed)
				continue;
			
			if (!state.squares[pos.x][pos.y].unit.p1Owner && state.squares[empty.x][empty.y].type == SquareType.DEPLOY_1)
				continue;
			
			if (state.squares[pos.x][pos.y].unit.p1Owner && state.squares[empty.x][empty.y].type == SquareType.DEPLOY_2)
				continue;
				
			return new UnitAction(pos, empty);
			
		}
		
		return null;
	}

	private Action handAction(GameState state) {
		
		Collections.shuffle(handOrder);
		Action action = null;
		
		for(Integer i : handOrder){
			if (i >= state.currentHand().size())
				continue;
			Card card = state.currentHand().get(i);
			
			if (card.type == CardType.ITEM)
				action = dropItemAction(state, card);
			if (card.type == CardType.UNIT)
				action = dropUnitAction(state, card);
			if (card.type == CardType.SPELL)
				action = dropSpellAction(state, card);
			
			if (action != null)
				return action;
		}
		
		return null;
	}

	private Action dropSpellAction(GameState state, Card card) {
		
		for(Position pos : enemyUnits){
			return new DropAction(card, pos);
			// TODO: also around unit
		}
		
		return null;
	}

	private Action dropUnitAction(GameState state, Card card) {
		
		if (state.p1Turn)
			for(Position pos : state.map.p1DeploySquares)
				if (state.squares[pos.x][pos.y].unit == null || state.squares[pos.x][pos.y].unit.hp <= 0)
					return new DropAction(card, pos);
		if (!state.p1Turn)
			for(Position pos : state.map.p2DeploySquares)
				if (state.squares[pos.x][pos.y].unit == null || state.squares[pos.x][pos.y].unit.hp <= 0)
					return new DropAction(card, pos);
		
		return null;
	}

	private Action dropItemAction(GameState state, Card card) {
		
		for(Position pos : myUnits){
			
			if (state.squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL)
				continue;
			
			if (card == Card.REVIVE_POTION && state.squares[pos.x][pos.y].unit.fullHealth())
				continue;
			
			if (card != Card.REVIVE_POTION && state.squares[pos.x][pos.y].unit.hp <= 0 || state.squares[pos.x][pos.y].unit.equipment.contains(card))
				continue;
			
			return new DropAction(card, pos);
			
		}
		
		return null;
	}

	private void locateUnits(GameState state) {
		
		this.emptySpaces.clear();
		this.myUnits.clear();
		this.enemyUnits.clear();

		for(int x = 0; x < state.map.width; x++){
			for(int y = 0; y < state.map.height; y++){
				if (state.squares[x][y].unit != null){
					if (state.squares[x][y].unit.p1Owner == p1)
						myUnits.add(new Position(x,y));
					else
						enemyUnits.add(new Position(x,y));
				} else {
					emptySpaces.add(new Position(x,y));
				}
			}
		}
	}

}
