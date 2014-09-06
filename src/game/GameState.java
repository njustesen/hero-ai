package game;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lib.Card;
import lib.CardType;
import model.Council;
import model.HAMap;
import model.Position;
import model.Square;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;

public class GameState {

	public HAMap map;
	public boolean p1Turn;
	public short turn;
	public byte APLeft;
	public Square[][] squares;
	public List<Card> p1Deck;
	public List<Card> p2Deck;
	public List<Card> p1Hand;
	public List<Card> p2Hand;
	public boolean isTerminal;
	
	public GameState(HAMap map) {
		super();
		this.isTerminal = false;
		this.map = map;
		this.p1Turn = true;
		this.turn = 1;
		APLeft = 5;
		p1Hand = new ArrayList<Card>(6);
		p2Hand = new ArrayList<Card>(6);
		this.squares = new Square[map.width][map.height];
		for(int x = 0; x < map.width; x++)
			for(int y = 0; y < map.height; y++)
				squares[x][y] = map.squares[x][y].copy();
		dealCards();
	}
	
	public GameState(
			HAMap map, 
			boolean p1Turn, 
			short turn, 
			byte APLeft,
			Square[][] squares, 
			List<Card> p1Hand,
			List<Card> p2Hand, 
			List<Card> p1Deck,
			List<Card> p2Deck, 
			boolean isTerminal) {
		super();
		this.map = map;
		this.p1Turn = p1Turn;
		this.turn = turn;
		this.APLeft = APLeft;
		this.squares = squares;
		this.p1Hand = p1Hand;
		this.p1Hand = p2Hand;
		this.p1Hand = p1Deck;
		this.p1Hand = p2Deck;
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
		
		Set<CardType> seen = new HashSet<CardType>();
		for(CardType card : currentHand()){
			if (!seen.contains(card))
				actions.addAll(possibleActions(card));
		}
		
		return actions;
		
	}
	
	private List<Action> possibleActions(CardType card) {
		
		List<Action> actions = new ArrayList<Action>();
		
		if (isEquipment(card)){
			for (Position pos : objects.keySet()){
				if (objects.get(pos) instanceof Unit){
					Unit unit = ((Unit)objects.get(pos));
					if (unit.equipment.contains(card))
						continue;
					if (unit.p1Owner == p1Turn && unit.hp != 0){
						if (card == CardType.RevivePotion && unit.hp == maxHP(unit))
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
	
	private void dealCards() {
		
		p1Deck = new ArrayList<Card>();
		for (Card type : Council.deck)
			p1Deck.add(type);
		p2Deck = new ArrayList<Card>();
		for (Card type : Council.deck)
			p2Deck.add(type);
		
		p1Hand = drawHandFrom(p1Deck);
		p2Hand = drawHandFrom(p2Deck);
		
	}

	private List<Card> drawHandFrom(List<Card> deck) {
		
		List<Card> hand = new ArrayList<Card>();
		
		while(deck.size() > 0 && hand.size() < 6){
			int idx = (int) (Math.random() * deck.size());
			Card card = deck.get(idx);
			deck.remove(idx);
			hand.add(card);
		}
		
		return hand;
	}
	
	private void drawCards() {
		
		while(currentHand().size() < 6 && !currentDeck().isEmpty()){
			int idx = (int) (Math.random() * currentDeck().size());
			Card card = currentDeck().get(idx);
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
	
	
	public List<Card> currentHand() {
		if (p1Turn)
			return p1Hand;
		return p2Hand;
	}
	
	public List<Card> currentDeck() {
		if (p1Turn)
			return p1Deck;
		return p2Deck;
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
	
	public GameState copy() {
		Square[][] sq = new Square[map.width][map.height];
		List<Card> p1h = new ArrayList<Card>(p1Hand.size());
		List<Card> p2h = new ArrayList<Card>(p2Hand.size());
		List<Card> p1d = new ArrayList<Card>(p1Deck.size());
		List<Card> p2d = new ArrayList<Card>(p2Deck.size());
		return new GameState(map, p1Turn, turn, APLeft, sq, p1h, p2h, p1d, p2d, isTerminal);
	}

	
}
