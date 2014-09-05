package game;


import gameobjects.Council;
import gameobjects.Crystal;
import gameobjects.GameObject;
import gameobjects.Unit;
import gameobjects.GameObjectType;
import gameobjects.HAMap;
import gameobjects.Position;
import gameobjects.Square;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;

public class GameState {

	public HAMap map;
	public boolean p1Turn;
	public short turn;
	public byte APLeft;
	public Map<Position, GameObject> objects;
	public List<GameObjectType> p1Deck;
	public List<GameObjectType> p2Deck;
	public List<GameObjectType> p1Hand;
	public List<GameObjectType> p2Hand;
	public boolean isTerminal;
	
	public GameState(HAMap map) {
		super();
		this.isTerminal = false;
		this.map = map;
		this.p1Turn = true;
		this.turn = 1;
		APLeft = 5;
		this.objects = new HashMap<Position, GameObject>();
		setupCrystals(map);
		dealCards();
	}
	
	public GameState(HAMap map, boolean p1Turn, short turn, byte APLeft,
			Map<Position, GameObject> objects, List<GameObjectType> p1Hand,
			List<GameObjectType> p2Hand, boolean isTerminal) {
		super();
		this.map = map;
		this.p1Turn = p1Turn;
		this.turn = turn;
		this.APLeft = APLeft;
		this.objects = objects;
		this.p1Hand = p1Hand;
		this.p1Hand = p2Hand;
		this.isTerminal = isTerminal;
	}
	
	public List<Action> possibleActions(){
		
		List<Action> actions = new ArrayList<Action>();
		
		if (APLeft == 0){
			actions.add(new EndTurnAction());
			return actions;
		}
		
		for(Position pos : objects.keySet()){
			
			if (objects.get(pos) instanceof Unit)
				actions.addAll(possibleActions((Unit)objects.get(pos), pos));
			
		}
		
		Set<GameObjectType> seen = new HashSet<GameObjectType>();
		for(GameObjectType card : currentHand()){
			if (!seen.contains(card))
				actions.addAll(possibleActions(card));
		}
		
		return actions;
		
	}
	
	private List<Action> possibleActions(GameObjectType card) {
		
		List<Action> actions = new ArrayList<Action>();
		
		if (isEquipment(card)){
			for (Position pos : objects.keySet()){
				if (objects.get(pos) instanceof Unit){
					Unit unit = ((Unit)objects.get(pos));
					if (unit.equipment.contains(card))
						continue;
					if (unit.p1Owner == p1Turn && unit.hp != 0){
						if (card == GameObjectType.RevivePotion && unit.hp == maxHP(unit))
							continue;
						actions.add(new DropAction(card, pos));
					}
				}
			}
		} else if(isSpell(card)){
			Set<Position> squares = new HashSet<Position>();
			for (Position pos : objects.keySet()){
				if (objects.get(pos) instanceof Unit){
					if (((Unit)objects.get(pos)).p1Owner != p1Turn){
						for(int x = -1; x <=1; x++){
							for(int y = -1; y <=1; y++){
								int xx = Math.max(0,pos.x + x);
								xx = Math.min(map.width-1, xx);
								int yy = Math.max(0,pos.y + y);
								yy = Math.min(map.height-1, yy);
								squares.add(new Position(xx, yy));
							}
						}
					}
						actions.add(new DropAction(card, pos));
				} else if (objects.get(pos) instanceof Crystal){
					if (((Crystal)objects.get(pos)).p1Owner != p1Turn){
						for(int x = -1; x <=1; x++){
							for(int y = -1; y <=1; y++){
								int xx = Math.max(0,pos.x + x);
								xx = Math.min(map.width-1, xx);
								int yy = Math.max(0,pos.y + y);
								yy = Math.min(map.height-1, yy);
								squares.add(new Position(xx, yy));
							}
						}
					}
				}
			}
			
			for(Position pos : squares)
				actions.add(new DropAction(card, pos));
			
		} else if (isUnit(card)){
			for (Position pos : map.deploySquares(p1Turn ? 1 : 2)){
				if (!objects.containsKey(pos))					
					actions.add(new DropAction(card, pos));
			}
		}
		
		return actions;
	}

	private List<Action> possibleActions(Unit unit, Position from) {
		
		List<Action> actions = new ArrayList<Action>();
		
		if (unit.hp == 0)
			return actions;
		
		if (APLeft == 0)
			return actions;
		
		if (unit.p1Owner != p1Turn)
			return actions;
		
		// Movement and attack
		int d = Math.max(unit.unitClass.speed, Math.max(unit.unitClass.heal != null ? unit.unitClass.heal.range : 0, unit.unitClass.attack.range));
		for(int x = d*-1; x <= d; x++){
			for(int y = d*-1; y <= d; y++){
				Position to = new Position(from.x + x, from.y + y);
				if (to.x >= map.width || to.x < 0 || to.y >= map.height || to.y < 0)
					continue;
				if (objects.containsKey(to)){
					if (objects.get(to) instanceof Unit){
						Unit unitTo = ((Unit)objects.get(to));
						
						if (unitTo.hp == 0){
							
							if (map.squareAt(to.x, to.y) == Square.P1DEPLOY && !p1Turn || map.squareAt(to.x, to.y) == Square.P2DEPLOY && p1Turn){
								if (unit.p1Owner != unitTo.p1Owner && distance(from, to) <= unit.unitClass.attack.range)
									actions.add(new UnitAction(from, to));
								else if (unit.p1Owner == unitTo.p1Owner && 
										(unit.unitClass.heal != null || unit.unitClass.swap) && 
										distance(from, to) <= unit.unitClass.attack.range)
									actions.add(new UnitAction(from, to));
							} else {
								if (distance(from, to) <= unit.unitClass.speed)
									actions.add(new UnitAction(from, to));
								else if (unit.p1Owner == unitTo.p1Owner && unit.unitClass.swap)
									actions.add(new UnitAction(from, to));
							}
							
						} else {
							
							if (unit.p1Owner != unitTo.p1Owner && distance(from, to) <= unit.unitClass.attack.range)
								actions.add(new UnitAction(from, to));
							else if (unit.p1Owner == unitTo.p1Owner && unit.unitClass.heal != null && distance(from, to) <= unit.unitClass.heal.range && unitTo.hp < maxHP(unitTo))
								actions.add(new UnitAction(from, to));
							else if (unit.p1Owner == unitTo.p1Owner && unit.unitClass.swap)
								actions.add(new UnitAction(from, to));
						}
						
					} else if (objects.get(to) instanceof Crystal){
						
						Crystal crytal = ((Crystal)objects.get(to));
						if (unit.p1Owner != crytal.p1Owner && distance(from, to) <= unit.unitClass.attack.range)
							actions.add(new UnitAction(from, to));
					}
					
				} else {
					
					if (distance(from, to) <= unit.unitClass.speed)
						actions.add(new UnitAction(from, to));
					
				}
			}
		}
		
		return actions;
	}

	private void setupCrystals(HAMap map) {
		
		for(byte x = 0; x < map.width; x++){
			for(byte y = 0; y < map.height; y++){
				if(map.squareAt(x, y) == Square.P1CRYSTAL)
					objects.put(new Position(x,y), new Crystal(true));
				else if(map.squareAt(x, y) == Square.P2CRYSTAL)
					objects.put(new Position(x,y), new Crystal(false));
			}
		}
		
	}
	
	private void dealCards() {
		
		p1Deck = new ArrayList<GameObjectType>();
		for (GameObjectType type : Council.deck)
			p1Deck.add(type);
		p2Deck = new ArrayList<GameObjectType>();
		for (GameObjectType type : Council.deck)
			p2Deck.add(type);
		
		p1Hand = drawHandFrom(p1Deck);
		p2Hand = drawHandFrom(p2Deck);
		
	}

	private List<GameObjectType> drawHandFrom(List<GameObjectType> deck) {
		
		List<GameObjectType> hand = new ArrayList<GameObjectType>();
		
		while(deck.size() > 0 && hand.size() < 6){
			int idx = (int) (Math.random() * deck.size());
			GameObjectType card = deck.get(idx);
			deck.remove(idx);
			hand.add(card);
		}
		
		return hand;
	}
	
	public void drawCards() {
		
		while(currentHand().size() < 6 && !currentDeck().isEmpty()){
			int idx = (int) (Math.random() * currentDeck().size());
			GameObjectType card = currentDeck().get(idx);
			currentDeck().remove(idx);
			currentHand().add(card);
		}
		
	}

	public byte distance(Position from, Position to) {
		int x = from.x - to.x;
		if (x < 0)
			x = x * -1;
		int y = from.y - to.y;
		if (y < 0)
			y = y * -1;
		return (byte) (x + y);
	}
	
	public boolean isUnit(GameObjectType type) {
		if (type == GameObjectType.Archer || 
				type == GameObjectType.Cleric || 
				type == GameObjectType.Knight || 
				type == GameObjectType.Ninja || 
				type == GameObjectType.Wizard){
			return true;
		}
		return false;
	}
	
	public boolean isEquipment(GameObjectType type) {
		if (type == GameObjectType.Dragonscale || 
				type == GameObjectType.RevivePotion || 
				type == GameObjectType.Scroll || 
				type == GameObjectType.Runemetal || 
				type == GameObjectType.ShiningHelm){
			return true;
		}
		return false;
	}
	
	public boolean isSpell(GameObjectType type) {
		if (type == GameObjectType.Inferno){
			return true;
		}
		return false;
	}
	

	public int power(Unit unit, Position pos) {
		
		// Initial power
		int power = unit.unitClass.power;
		
		// Sword
		if (unit.equipment.contains(GameObjectType.Runemetal))
			power += power/2;
		
		// Power boost
		if (map.squareAt(pos.x, pos.y) == Square.POWER_BOOST)
			power += 100;
		
		// SCroll
		if (unit.equipment.contains(GameObjectType.Scroll))
			power *= 3;
		
		
		return power;
	}
	
	public int maxHP(Unit unit) {
		
		int max = unit.unitClass.maxHP;
		
		if (unit.equipment.contains(GameObjectType.Dragonscale))
			max += unit.unitClass.maxHP/10;
		if (unit.equipment.contains(GameObjectType.ShiningHelm))
			max += unit.unitClass.maxHP/10;
		
		return max;
	}

	public List<GameObjectType> currentHand() {
		if (p1Turn)
			return p1Hand;
		return p2Hand;
	}
	
	public List<GameObjectType> currentDeck() {
		if (p1Turn)
			return p1Deck;
		return p2Deck;
	}

	public GameState copy() {
		GameState copy = new GameState(map);
		copy.APLeft = APLeft;
		copy.isTerminal = isTerminal;
		copy.p1Turn = p1Turn;
		copy.turn = turn;
		copy.objects = new HashMap<Position, GameObject>();
		
		for (Position pos : objects.keySet())
			copy.objects.put(new Position(pos.x, pos.y), objects.get(pos).copy());
		
		copy.p1Deck = new ArrayList<GameObjectType>();
		for (GameObjectType obj : p1Deck)
			copy.p1Deck.add(obj);
		copy.p2Deck = new ArrayList<GameObjectType>();
		for (GameObjectType obj : p2Deck)
			copy.p2Deck.add(obj);
		
		copy.p1Hand = new ArrayList<GameObjectType>();
		for (GameObjectType obj : p1Hand)
			copy.p1Hand.add(obj);
		copy.p2Hand = new ArrayList<GameObjectType>();
		for (GameObjectType obj : p2Hand)
			copy.p2Hand.add(obj);
		
		return copy;
	}

	public void removeDying() {
		List<Position> dead = new ArrayList<Position>();
		for(Position pos : objects.keySet()){
			if (objects.get(pos) instanceof Unit)
				if (((Unit)objects.get(pos)).hp == 0)
					dead.add(pos);
		}
		for(Position pos : dead)
			objects.remove(pos);
	}
	
}
