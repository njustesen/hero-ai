package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lib.Card;
import lib.CardType;
import model.Position;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;
import action.UnitActionType;
import ai.util.RAND_METHOD;
import game.AI;
import game.GameState;

public class RandomAI implements AI {
	
	private static final double PROP_HAND = 0.25;
	public boolean p1;
	public RAND_METHOD randMethod;
	private List<Integer> heightOrder;
	private List<Integer> widthOrder;
	private List<Integer> handOrder;
	private List<Action> actions;
	private List<Position> positions;
	private List<Integer> idxs;

	public RandomAI(boolean p1, RAND_METHOD randMethod){
		this.p1 = p1;
		this.randMethod = randMethod;
		this.positions = new ArrayList<Position>();
		this.actions = new ArrayList<Action>();
		this.idxs = new ArrayList<Integer>();
		for(int i = 0; i < 6; i++)
			this.idxs.add(i);
		heightOrder = new ArrayList<Integer>();
		widthOrder = new ArrayList<Integer>();
		handOrder = new ArrayList<Integer>();
		for(int x = 0; x < 9; x++)
			widthOrder.add(x);
		for(int y = 0; y < 5; y++)
			heightOrder.add(y);
		for(int h = 0; h < 6; h++)
			handOrder.add(h);
	}
	
	@Override
	public Action act(GameState state, long ms) {
		Action selected = null;
		switch(randMethod){ 
			case BRUTE : selected = getActionBrute(state); break;
			case TREE : selected = getActionLazy(state); break;
			case SCAN : break;	// NOT IMPLEMENTED HERE
		}
		return selected;
	}

	private Action getActionBrute(GameState state) {
		actions.clear();
		state.possibleActions(actions);
		if (actions.isEmpty())
			return new EndTurnAction();
		
		int idx = (int) (Math.random() * actions.size());
		Action selected = actions.get(idx);
		return selected;
	}
	
	private Action getActionVeryLazy(GameState state) {
		
		if (state.APLeft <= 0)
			return new EndTurnAction();
		
		Collections.shuffle(handOrder);
		
		if (Math.random() < PROP_HAND){
			Action action = handAction(state);
			if (action != null)
				return action;
		}
		
		Collections.shuffle(heightOrder);
		Collections.shuffle(widthOrder);
		
		Action action = unitAction(state);
		if (action != null)
			return action;
		
		action = handAction(state);
		
		return action;
		
	}
	
	private Action unitAction(GameState state) {
		
		for (int xx = 0; xx < widthOrder.size(); xx++){
			for (int yy = 0; yy < heightOrder.size(); yy++){
				int x = widthOrder.get(xx);
				int y = heightOrder.get(yy);
				if (state.squares[x][y].unit != null && 
						state.squares[x][y].unit.hp > 0 && 
						state.squares[x][y].unit.unitClass.card != Card.CRYSTAL){
					List<UnitActionType> at = new ArrayList<UnitActionType>();
					if (state.squares[x][y].unit.unitClass.heal != null)
						at.add(UnitActionType.HEAL);
					at.add(UnitActionType.ATTACK);
					at.add(UnitActionType.MOVE);
					if (state.squares[x][y].unit.unitClass.swap)
						at.add(UnitActionType.SWAP);
					Action action = randomUnitAction(state, state.squares[x][y].unit, new Position(x, y), at);
					if (action != null)
						return action;
				}
			}
		}
		
		return null;
	}

	private Action randomUnitAction(GameState state, Unit unit, Position pos, List<UnitActionType> at) {
		
		Collections.shuffle(at);
		
		while(!at.isEmpty()){
			
			if (at.get(0) == UnitActionType.ATTACK){
				
				int d = unit.unitClass.attack.range;
				for(int x = d*-1; x <= d; x++){
					for(int y = d*-1; y <= d; y++){
						Position to = new Position(pos.x + x, pos.y + y);
						if (to.x >= state.map.width || to.x < 0 || to.y >= state.map.height || to.y < 0)
							continue;
						if (state.squares[to.x][to.y].unit != null){
							Unit other = state.squares[to.x][to.y].unit;
							if (other.hp < 0)
								continue;
							if (other.p1Owner == state.p1Turn)
								continue;
							return new UnitAction(pos, to, UnitActionType.ATTACK);
						}
					}
				}
			} else if (at.get(0) == UnitActionType.HEAL){
				
				int d = unit.unitClass.heal.range;
				for(int x = d*-1; x <= d; x++){
					for(int y = d*-1; y <= d; y++){
						Position to = new Position(pos.x + x, pos.y + y);
						if (to.x >= state.map.width || to.x < 0 || to.y >= state.map.height || to.y < 0)
							continue;
						if (state.squares[to.x][to.y].unit != null){
							Unit other = state.squares[to.x][to.y].unit;
							if (other.p1Owner != state.p1Turn)
								continue;
							return new UnitAction(pos, to, UnitActionType.HEAL);
						}
					}
				}
			} else if (at.get(0) == UnitActionType.MOVE){
				
				int d = unit.unitClass.speed;
				for(int x = d*-1; x <= d; x++){
					for(int y = d*-1; y <= d; y++){
						Position to = new Position(pos.x + x, pos.y + y);
						if (to.x >= state.map.width || to.x < 0 || to.y >= state.map.height || to.y < 0)
							continue;
						if (state.squares[to.x][to.y].unit != null && state.squares[to.x][to.y].unit.hp != 0)
							continue;
						
						return new UnitAction(pos, to, UnitActionType.MOVE);
					}
				}
				
			} else if (at.get(0) == UnitActionType.SWAP){
				
				for(int x = 0; x <= state.map.width; x++){
					for(int y = 0; y <= state.map.height; y++){
						Position to = new Position(x, y);
						if (to.x >= state.map.width || to.x < 0 || to.y >= state.map.height || to.y < 0)
							continue;
						if (state.squares[to.x][to.y].unit != null && state.squares[to.x][to.y].unit.hp != 0 && state.squares[to.x][to.y].unit.p1Owner == state.p1Turn)
							return new UnitAction(pos, to, UnitActionType.SWAP);
					}
				}
			}
			
			at.remove(0);
			
		}
		
		return null;
	}

	private Action handAction(GameState state) {
		for (int cc = 0; cc < handOrder.size(); cc++){
			int c = handOrder.get(cc);
			if (c >= state.currentHand().size())
				continue;
			actions.clear();
			state.possibleActions(state.currentHand().get(c), actions);
			if (!actions.isEmpty())
				return actions.get((int)(actions.size() * Math.random()));
		}
		return null;
	}

	public Action getActionLazy(GameState state) {
		
		if (state.APLeft == 0)
			return new EndTurnAction();
		
		if (Math.random() < PROP_HAND){
			List<Integer> idxs = new ArrayList<Integer>(state.currentHand().size());
			for(int i = 0; i < state.currentHand().size(); i++)
				idxs.add(i);
			Collections.shuffle(idxs);
			for(Integer i : idxs){
				actions.clear();
				state.possibleActions(state.currentHand().get(i), actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
			positions.clear();
			for(int x = 0; x < state.map.width; x++)
				for(int y = 0; y < state.map.height; y++)
					if (state.squares[x][y].unit != null && state.squares[x][y].unit.p1Owner == state.p1Turn && state.squares[x][y].unit.hp > 0 && state.squares[x][y].unit.unitClass.card != Card.CRYSTAL)
						positions.add(new Position(x,y));
			Collections.shuffle(positions);
			for(Position pos : positions){
				actions.clear();
				state.possibleActions(state.squares[pos.x][pos.y].unit, pos, actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
		} else {
			positions.clear();
			for(int x = 0; x < state.map.width; x++)
				for(int y = 0; y < state.map.height; y++)
					if (state.squares[x][y].unit != null && state.squares[x][y].unit.p1Owner == state.p1Turn && state.squares[x][y].unit.hp > 0 && state.squares[x][y].unit.unitClass.card != Card.CRYSTAL)
						positions.add(new Position(x,y));
			Collections.shuffle(positions);
			for(Position pos : positions){
				actions.clear();
				state.possibleActions(state.squares[pos.x][pos.y].unit, pos, actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
			Collections.shuffle(idxs);
			for(Integer i : idxs){
				if (i >= state.currentHand().size())
					continue;
				actions.clear();
				state.possibleActions(state.currentHand().get(i), actions);
				if (!actions.isEmpty())
					return actions.get((int) (Math.random() * actions.size()));
			}
		}
		
		return new EndTurnAction();
	}
	
}
