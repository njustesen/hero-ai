package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.LineIterators;

import lib.Card;
import lib.CardType;
import model.AttackType;
import model.Direction;
import model.HAMap;
import model.Position;
import model.Square;
import model.SquareType;
import model.Unit;
import model.team.Council;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UnitAction;
import action.UnitActionType;
import action.SwapCardAction;

public class GameState {

	private static final int ASSAULT_BONUS = 300;

	private static final double INFERNO_DAMAGE = 300;
	
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

	public List<Position> chainTargets;
	
	public GameState(HAMap map) {
		super();
		this.isTerminal = false;
		this.map = map;
		this.p1Turn = true;
		this.turn = 1;
		this.APLeft = 5;
		this.p1Hand = new ArrayList<Card>(6);
		this.p2Hand = new ArrayList<Card>(6);
		this.chainTargets = new ArrayList<Position>();
		this.squares = new Square[map.width][map.height];
		for(int x = 0; x < map.width; x++)
			for(int y = 0; y < map.height; y++)
				squares[x][y] = map.squares[x][y].copy();
		
		dealCards(1);
		dealCards(2);
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
			List<Position> chainTargets, 
			boolean isTerminal) {
		super();
		this.map = map;
		this.p1Turn = p1Turn;
		this.turn = turn;
		this.APLeft = APLeft;
		this.squares = squares;
		this.p1Hand = p1Hand;
		this.p2Hand = p2Hand;
		this.p1Deck = p1Deck;
		this.p2Deck = p2Deck;
		this.chainTargets = chainTargets;
		this.isTerminal = isTerminal;
	}
	
	public List<Action> possibleActions(){
		
		List<Action> actions = new ArrayList<Action>();
		
		if (APLeft == 0){
			actions.add(new EndTurnAction());
			return actions;
		}
		
		for(int x = 0; x < map.width; x++){
			for(int y = 0; y < map.height; y++){
				if (squares[x][y].unit != null)
					actions.addAll(possibleActions(squares[x][y].unit, new Position(x,y)));
			}
		}
		
		Set<Card> visited = new HashSet<Card>();
		for(Card card : currentHand()){
			if (!visited.contains(card))
				actions.addAll(possibleActions(card));
		}
		
		return actions;
		
	}
	
	public List<Action> possibleActions(Card card) {
		
		List<Action> actions = new ArrayList<Action>();
		
		if (card.type == CardType.ITEM){
			for(int x = 0; x < map.width; x++){
				for(int y = 0; y < map.height; y++){
					if (squares[x][y].unit != null && squares[x][y].unit.unitClass.card != Card.CRYSTAL){
						if (squares[x][y].unit.equipment.contains(card))
							continue;
						if (squares[x][y].unit.p1Owner == p1Turn && squares[x][y].unit.hp > 0){
							if (card == Card.REVIVE_POTION && squares[x][y].unit.fullHealth())
								continue;
							actions.add(new DropAction(card, new Position(x, y)));
						}
					}
				}
			}
		} else if(card.type == CardType.SPELL){
			Set<Position> sq = new HashSet<Position>();
			for(int x = 0; x < map.width; x++){
				for(int y = 0; y < map.height; y++){
					if (squares[x][y].unit != null && squares[x][y].unit.p1Owner != p1Turn){
						for(int xx = -1; xx <=1; xx++){
							for(int yy = -1; yy <=1; yy++){
								int xxx = Math.max(0, x + xx);
								xxx = Math.min(map.width-1, xxx);
								int yyy = Math.max(0, y + yy);
								yyy = Math.min(map.height-1, yyy);
								sq.add(new Position(xxx, yyy));
							}
						}
					}
				}
			}
			
			for(Position pos : sq)
				actions.add(new DropAction(card, pos));
			
		} else if (card.type == CardType.UNIT){
			if (p1Turn){
				for(Position pos : map.p1DeploySquares){
					if (squares[pos.x][pos.y].unit == null){
						actions.add(new DropAction(card, new Position(pos.x, pos.y)));
					}
				}
			} else {
				for(Position pos : map.p2DeploySquares){
					if (squares[pos.x][pos.y].unit == null){
						actions.add(new DropAction(card, new Position(pos.x, pos.y)));
					}
				}
			}
		}
		
		if (!currentDeck().isEmpty())
			actions.add(new SwapCardAction(card));
		
		return actions;
	}

	public List<Action> possibleActions(Unit unit, Position from) {
		
		if (unit.unitClass.card == Card.CRYSTAL)
			return new ArrayList<Action>();
			
		List<Action> actions = new ArrayList<Action>();
		
		if (unit.hp == 0)
			return actions;
		
		if (APLeft == 0)
			return actions;
		
		if (unit.p1Owner != p1Turn)
			return actions;
		
		// Movement and attack
		int d = unit.unitClass.speed;
		if (unit.unitClass.heal != null && unit.unitClass.heal.range > d)
			d = unit.unitClass.heal.range;
		if (unit.unitClass.attack != null && unit.unitClass.attack.range > d)
			d = unit.unitClass.attack.range;
		for(int x = d*-1; x <= d; x++){
			for(int y = d*-1; y <= d; y++){
				Position to = new Position(from.x + x, from.y + y);
				if (to.x >= map.width || to.x < 0 || to.y >= map.height || to.y < 0)
					continue;
				
				if (squares[to.x][to.y].unit != null){
						
					if (squares[to.x][to.y].unit.hp == 0){
						
						if ((squares[to.x][to.y].type == SquareType.DEPLOY_1 && !p1Turn) || (squares[to.x][to.y].type == SquareType.DEPLOY_2 && p1Turn)){
							if (unit.p1Owner != squares[to.x][to.y].unit.p1Owner && distance(from, to) <= unit.unitClass.attack.range){
								actions.add(new UnitAction(from, to, UnitActionType.ATTACK));
							}else if (unit.p1Owner == squares[to.x][to.y].unit.p1Owner){
								if (unit.unitClass.heal != null)
									actions.add(new UnitAction(from, to, UnitActionType.ATTACK));
								if (unit.unitClass.swap)
									actions.add(new UnitAction(from, to, UnitActionType.SWAP));
							}
						} else {
							if (distance(from, to) <= unit.unitClass.speed)
								actions.add(new UnitAction(from, to, UnitActionType.MOVE));
							else if (unit.p1Owner == squares[to.x][to.y].unit.p1Owner && unit.unitClass.swap)
								actions.add(new UnitAction(from, to, UnitActionType.SWAP));
						}
						
					} else {
						int distance = distance(from, to);
						if (unit.p1Owner != squares[to.x][to.y].unit.p1Owner && distance <= unit.unitClass.attack.range){
							if (!(distance > 1 && losBlocked(p1Turn, from, to)))
								actions.add(new UnitAction(from, to, UnitActionType.ATTACK));
						}else if (unit.p1Owner == squares[to.x][to.y].unit.p1Owner && unit.unitClass.heal != null && distance(from, to) <= unit.unitClass.heal.range && squares[to.x][to.y].unit.fullHealth())
							actions.add(new UnitAction(from, to, UnitActionType.HEAL));
						else if (unit.p1Owner == squares[to.x][to.y].unit.p1Owner && unit.unitClass.swap)
							actions.add(new UnitAction(from, to, UnitActionType.SWAP));
					}
					
				} else {
					
					if (distance(from, to) <= unit.unitClass.speed)
						actions.add(new UnitAction(from, to, UnitActionType.MOVE));
					
				}
			}
		}
		
		return actions;
	}
	
	public void update(Action action) {
		/*
		if (action instanceof UndoAction){
			if (APLeft == 5)
				return;
			else 
				undo();
		}
		*/
		chainTargets.clear();
		
		if (action instanceof EndTurnAction)
			endTurn();
		
		if (APLeft <= 0)
			endTurn();
		
		if (action instanceof DropAction){
			
			DropAction drop = (DropAction)action;
	
			// Not a type in current players hand
			if (!currentHand().contains(drop.type))
				return;
			
			// Unit
			if (drop.type.type == CardType.UNIT){
				
				// Not current players deploy square
				if (squares[drop.to.x][drop.to.y].type == SquareType.DEPLOY_1 && !p1Turn)
					return;
				if (squares[drop.to.x][drop.to.y].type == SquareType.DEPLOY_2 && p1Turn)
					return;
				
				deploy(drop.type, drop.to);
				
			}
			
			// Equipment
			if (drop.type.type == CardType.ITEM){
				
				// Not a unit square or crystal
				if (squares[drop.to.x][drop.to.y].unit == null 
						|| squares[drop.to.x][drop.to.y].unit.unitClass.card == Card.CRYSTAL)
					return;
				
				if (squares[drop.to.x][drop.to.y].unit.p1Owner != p1Turn)
					return;
					
				if (drop.type == Card.REVIVE_POTION && 
						(squares[drop.to.x][drop.to.y].unit.unitClass.card == Card.CRYSTAL 
						|| squares[drop.to.x][drop.to.y].unit.fullHealth()))
					return;
				
				
				
				if (squares[drop.to.x][drop.to.y].unit.equipment.contains(drop.type))
					return;
							
				equip(drop.type, drop.to);
				
			}

			// Spell
			if (drop.type.type == CardType.SPELL)
				dropInferno(drop.to);
			
			return;
			
		}
		
		if (action instanceof UnitAction){
			
			UnitAction ua = (UnitAction)action;
	
			if (squares[ua.from.x][ua.from.y].unit == null)
				return;
			
			Unit unit = squares[ua.from.x][ua.from.y].unit;
			
			if (unit.p1Owner != p1Turn)
				return;
			
			if (unit.hp == 0)
				return;
			
			// Move
			if (squares[ua.to.x][ua.to.y].unit == null){
				
				if (distance(ua.from, ua.to) > squares[ua.from.x][ua.from.y].unit.unitClass.speed)
					return;
				
				if (squares[ua.to.x][ua.to.y].type == SquareType.DEPLOY_1 && !unit.p1Owner)
					return;
				
				if (squares[ua.to.x][ua.to.y].type == SquareType.DEPLOY_2 && unit.p1Owner)
					return;
				
				move(unit, ua.from, ua.to);
				return;
				
			} else {
			
				Unit other = squares[ua.to.x][ua.to.y].unit;
				
				// Swap and heal
				if (unit.p1Owner == other.p1Owner){
					if (unit.unitClass.swap){
						swap(unit, ua.from, other, ua.to);
						return;
					}
					if(unit.unitClass.heal == null)
						return;
					if(distance(ua.from,ua.to) > unit.unitClass.heal.range)
						return;
					if(unit.fullHealth())
						return;
					heal(unit, ua.from, other);
					return;
				}
				
				// Attack
				int distance = distance(ua.from, ua.to);
				if (unit.unitClass.attack != null 
						&& distance > unit.unitClass.attack.range)
					return;
				
				if (distance > 1 && losBlocked(p1Turn, ua.from, ua.to))
					return;
				
				if (other.hp == 0 && distance > unit.unitClass.speed)
					return;
					
				attack(unit, ua.from, other, ua.to);
				return;
				
			}
		}
		
		if (action instanceof SwapCardAction){
			
			SwapCardAction sa = (SwapCardAction) action;
			
			if (currentHand().contains(sa.card)){
				
				currentDeck().add(sa.card);
				currentHand().remove(sa.card);
				APLeft--;
				
			}
			
		}
		
	}

	/**
	 * Using the Bresenham-based super-cover line algorithm
	 * @param from
	 * @param to
	 * @return
	 */
	public boolean losBlocked(boolean p1, Position from, Position to) {
		
		List<Position> los = LineIterators.supercoverAsList(from, to);
		
		for(Position pos : los){
			if (pos.equals(from) || pos.equals(to))
				continue;
			
			if (squares[pos.x][pos.y].unit != null && 
					squares[pos.x][pos.y].unit.p1Owner != p1){
				
				return true;
			}
		}
		
		return false;
		
	}
	
	

	private void dropInferno(Position to) {
		
		for(byte x = -1; x <= 1; x++){
			for(byte y = -1; y <= 1; y++){
				Position pos = new Position((byte)(to.x + x), (byte)(to.y + y));
				if (pos.x < 0 || pos.x >= map.width || pos.y < 0 || pos.y >= map.height)
					continue;
				if (squares[pos.x][pos.y].unit != null){
					double damage = INFERNO_DAMAGE;
					if (squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL){
						int bonus = assaultBonus();
						damage += bonus;
					}
					double resistance = squares[pos.x][pos.y].unit.resistance(this, pos, AttackType.Magical);
					damage = damage * ((100d - resistance)/100d);
					squares[pos.x][pos.y].unit.hp -= damage;
					if (squares[pos.x][pos.y].unit.hp <= 0){
						if (squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL){
							checkWinOnCrystals();
							squares[pos.x][pos.y].unit = null;
						} else {
							squares[pos.x][pos.y].unit.hp = 0;
							checkWinOnUnits();
						}
					}
				}
			}
		}
		
		currentHand().remove(Card.INFERNO);
		APLeft--;
		
	}

	private void attack(Unit attacker, Position attPos, Unit defender, Position defPos) {
		if (defender.hp == 0){
			squares[defPos.x][defPos.y].unit = null;
			move(attacker, attPos, defPos);
			checkWinOnUnits();
		} else {
			int damage = attacker.damage(this, attPos, defender, defPos);
			if (defender.unitClass.card == Card.CRYSTAL){
				int bonus = assaultBonus();
				damage += bonus;
			}
			defender.hp -= damage;
			if (defender.hp <= 0){
				defender.hp = 0;
				if (defender.unitClass.card == Card.CRYSTAL){
					checkWinOnCrystals();
					squares[defPos.x][defPos.y].unit = null;
				} else {
					squares[defPos.x][defPos.y].unit.hp = 0;
					checkWinOnUnits();
				}
			} 
			if (attacker.unitClass.attack.push)
				push(defender, attPos, defPos);
			if (attacker.unitClass.attack.chain){
				int dirX = defPos.x - attPos.x;
				int dirY = defPos.y - attPos.y;
				Direction dir = new Direction(dirX, dirY);
				chain(attacker, attPos, defPos, dir, 1);
			}
		}
		attacker.equipment.remove(Card.SCROLL);
		APLeft--;
	}

	private void chain(Unit attacker, Position attPos, Position from, Direction dir, int jump) {
		
		if (jump >= 3)
			return;
		
		Position bestPos = nextJump(from, dir);
		Direction bestDir = from.getDirection(bestPos);
		
		// Attack
		if (bestPos != null){
			Unit defender = squares[bestPos.x][bestPos.y].unit;
			chainTargets.add(bestPos);
			int damage = attacker.damage(this, attPos, defender, bestPos);
			if (jump == 1)
				damage = (int) (damage * 0.75);
			else if (jump == 2)
				damage = (int) (damage * 0.56);
			else 
				System.out.println("Illegal number of jumps!");
			
			if (defender.unitClass.card == Card.CRYSTAL){
				int bonus = assaultBonus();
				damage += bonus;
			}
			defender.hp -= damage;
			if (defender.hp <= 0){
				defender.hp = 0;
				if (defender.unitClass.card == Card.CRYSTAL){
					checkWinOnCrystals();
					defender = null;
				} else {
					defender.hp = 0;
					checkWinOnUnits();
				}
			} 
			chain(attacker, attPos, bestPos, bestDir, jump+1);
		}
		
	}

	private Position nextJump(Position from, Direction dir) {
		
		int bestValue = 0;
		Position bestPos = null;
		
		// Find best target
		for(int newDirX = -1; newDirX <= 1; newDirX++){
			for(int newDirY = -1; newDirY <= 1; newDirY++){
				if (newDirX == 0 && newDirY == 0)
					continue;
				
				Position newPos = new Position(from.x + newDirX, from.y + newDirY);
				if (newPos.x < 0 || newPos.x >= map.width || newPos.y < 0 || newPos.y >= map.height)
					continue;
				if (squares[newPos.x][newPos.y].unit != null 
						&& squares[newPos.x][newPos.y].unit.p1Owner != p1Turn
						&& squares[newPos.x][newPos.y].unit.hp > 0){
					
					Direction newDir = new Direction(newDirX, newDirY);
					
					if (newDir.opposite(dir))
						continue;
					
					int chainValue = chainValue(dir, newDir);
					
					if (chainValue > bestValue){
						bestPos = newPos;
						bestValue = chainValue;
					}
				}
			}
		}
		
		return bestPos;
	}

	private int chainValue(Direction dir, Direction newDir) {
		
		if (dir.equals(newDir))
			return 10;
		
		int value = 0;
		
		if (!newDir.isDiagonal())
			value+=4;
		
		if (newDir.isNorth())
			value+=2;
		
		if (newDir.isEast())
			value+=1;
		
		return value;
		
	}

	private int assaultBonus() {
		int bonus = 0;
		for(Position pos : map.assaultSquares){
			if (squares[pos.x][pos.y].unit != null 
					&& squares[pos.x][pos.y].unit.p1Owner == p1Turn
					&& squares[pos.x][pos.y].unit.hp != 0){
				bonus += ASSAULT_BONUS;
			}
		}
		return bonus;
	}

	private void checkWinOnUnits() {
		
		boolean p1Alive = false;
		boolean p2Alive = false;
		
		for(int x = 0; x < map.width; x++){
			for(int y = 0; y < map.height; y++){
				if (squares[x][y].unit != null && squares[x][y].unit.p1Owner)
					p1Alive = true;
				else if (squares[x][y].unit != null && !squares[x][y].unit.p1Owner)
					p2Alive = true;
			}
		}
		
		if (!p1Alive || !p2Alive){
			isTerminal = true;
			return;
		}
		
		for(Card type : p1Deck){
			if (type.type == CardType.UNIT){
				p1Alive = true;
				break;
			}
		}
		if (p1Alive){
			for(Card type : p1Hand){
				if (type.type == CardType.UNIT){
					p1Alive = true;
					break;
				}
			}
		}
		for(Card type : p2Deck){
			if (type.type == CardType.UNIT){
				p2Alive = true;
				break;
			}
		}
		if (p2Alive){
			for(Card type : p2Hand){
				if (type.type == CardType.UNIT){
					p2Alive = true;
					break;
				}
			}
		}
		
		if (!p1Alive || !p2Alive)
			isTerminal = true;
		
	}
	

	private void checkWinOnCrystals() {
		boolean p1Alive = false;
		boolean p2Alive = false;
		
		for(Position pos : map.p1Crystals){
			if (squares[pos.x][pos.y].unit != null 
					&& squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL 
					&& squares[pos.x][pos.y].unit.hp > 0){
				p1Alive = true;
				break;
			}
		}
		for(Position pos : map.p2Crystals){
			if (squares[pos.x][pos.y].unit != null 
					&& squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL
					&& squares[pos.x][pos.y].unit.hp > 0){
				p2Alive = true;
				break;
			}
		}
		
		if (!p1Alive || !p2Alive)
			isTerminal = true;
		
	}
	

	public int getWinner() {
		
		if (!isTerminal)
			return 0;
		
		boolean p1Alive = false;
		
		for(Position pos : map.p1Crystals){
			if (squares[pos.x][pos.y].unit != null && squares[pos.x][pos.y].unit.hp > 0){
				p1Alive = true;
				break;
			}
		}
		
		for(Card type : p1Deck){
			if (type.type == CardType.UNIT){
				p1Alive = true;
				break;
			}
		}
		if (p1Alive){
			for(Card type : p1Hand){
				if (type.type == CardType.UNIT){
					p1Alive = true;
					break;
				}
			}
		}
		
		if (!p1Alive)
			return 2;
		else 
			return 1;
	}	

	private void push(Unit defender, Position attPos, Position defPos) {
		
		if (defender.unitClass.card == Card.CRYSTAL)
			return;
			
		byte x = 0;
		byte y = 0;
		
		if (attPos.x > defPos.x)
			x = -1;
		if (attPos.x < defPos.x)
			x = 1;
		if (attPos.y > defPos.y)
			y = -1;
		if (attPos.y < defPos.y)
			y = 1;
		
		Position newPos = new Position((byte)(defPos.x + x), (byte)(defPos.y + y));
		if (newPos.x >= map.width || newPos.x < 0 || newPos.y >= map.height || newPos.y < 0)
			return;
		
		if (squares[newPos.x][newPos.y].unit != null)
			return;
		
		squares[defPos.x][defPos.y].unit = null;
		squares[newPos.x][newPos.y].unit = defender;
		
	}

	private void heal(Unit healer, Position pos, Unit unitTo) {
		
		int power = healer.power(this, pos);
		if (unitTo.hp == 0)
			power *= healer.unitClass.heal.revive;
		else
			power *= healer.unitClass.heal.heal;
		
		unitTo.heal(power);
			
		healer.equipment.remove(Card.SCROLL);
		APLeft--;
		
	}

	private void swap(Unit unitFrom, Position from, Unit unitTo, Position to) {
		squares[from.x][from.y].unit = unitTo;
		squares[to.x][to.y].unit = unitFrom;
		APLeft--;
	}

	private void move(Unit unit, Position from, Position to) {
		squares[from.x][from.y].unit = null;
		squares[to.x][to.y].unit = unit;
		APLeft--;
	}
	
	private void equip(Card card, Position pos) {
		if (card == Card.REVIVE_POTION){
			if (squares[pos.x][pos.y].unit.hp == 0){
				squares[pos.x][pos.y].unit.hp += 100;
			} else {
				squares[pos.x][pos.y].unit.heal(1000);
			}
		} else {
			squares[pos.x][pos.y].unit.equip(card, this);
		}
		currentHand().remove(card);
		APLeft--;
	}

	private void deploy(Card card, Position pos) {
		squares[pos.x][pos.y].unit = new Unit(card, p1Turn);
		currentHand().remove(card);
		APLeft--;
	}
/*
	private void undo() {
		if (history.size() > 1){
			history.pop();
			state = history.peek();
		}
	}
*/
	private void endTurn() {
		checkWinOnUnits();
		drawCards();
		removeDying(p1Turn);
		p1Turn = !p1Turn;
		APLeft = 5;
		turn++;
		//history.clear();
		//history.push(state.copy());
	}
	
	private void dealCards(int player) {
		if (player == 1){
			p1Deck = new ArrayList<Card>();
			for (Card type : Council.deck)
				p1Deck.add(type);
			
			p1Hand = drawHandFrom(p1Deck);
			
			boolean p1HandGood = false;
			for(Card card : p1Hand){
				if (card.type == CardType.UNIT){
					p1HandGood = true;
					break;
				}
			}
			if (!p1HandGood)
				dealCards(1);
		} else if (player == 2){
			p2Deck = new ArrayList<Card>();
			for (Card type : Council.deck)
				p2Deck.add(type);
			
			p2Hand = drawHandFrom(p2Deck);
			
			boolean p2HandGood = false;
			for(Card card : p2Hand){
				if (card.type == CardType.UNIT){
					p2HandGood = true;
					break;
				}
			}
			if (!p2HandGood)
				dealCards(2);
		}
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

	public void removeDying(boolean p1) {
		for(int x = 0; x < map.width; x++)
			for(int y = 0; y < map.height; y++)
				if(squares[x][y].unit != null && squares[x][y].unit.p1Owner == p1 && squares[x][y].unit.hp == 0)
						squares[x][y].unit = null;
	}
	
	public GameState copy() {
		Square[][] sq = new Square[map.width][map.height];
		for(int x = 0; x < map.width; x++){
			for(int y = 0; y < map.height; y++){
				sq[x][y] = squares[x][y].copy();
			}
		}
		List<Card> p1h = new ArrayList<Card>(p1Hand.size());
		for(Card card : p1Hand)
			p1h.add(card);
		List<Card> p2h = new ArrayList<Card>(p2Hand.size());
		for(Card card : p2Hand)
			p2h.add(card);
		List<Card> p1d = new ArrayList<Card>(p1Deck.size());
		for(Card card : p1Deck)
			p1d.add(card);
		List<Card> p2d = new ArrayList<Card>(p2Deck.size());
		for(Card card : p2Deck)
			p2d.add(card);
		return new GameState(map, p1Turn, turn, APLeft, sq, p1h, p2h, p1d, p2d, chainTargets, isTerminal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + APLeft;
		result = prime * result + (isTerminal ? 1231 : 1237);
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		result = prime * result + ((p1Deck == null) ? 0 : p1Deck.hashCode());
		result = prime * result + ((p1Hand == null) ? 0 : p1Hand.hashCode());
		result = prime * result + (p1Turn ? 1231 : 1237);
		result = prime * result + ((p2Deck == null) ? 0 : p2Deck.hashCode());
		result = prime * result + ((p2Hand == null) ? 0 : p2Hand.hashCode());
		result = prime * result + Arrays.hashCode(squares);
		result = prime * result + turn;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (APLeft != other.APLeft)
			return false;
		if (isTerminal != other.isTerminal)
			return false;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		if (p1Deck == null) {
			if (other.p1Deck != null)
				return false;
		} else if (!p1Deck.equals(other.p1Deck))
			return false;
		if (p1Hand == null) {
			if (other.p1Hand != null)
				return false;
		} else if (!p1Hand.equals(other.p1Hand))
			return false;
		if (p1Turn != other.p1Turn)
			return false;
		if (p2Deck == null) {
			if (other.p2Deck != null)
				return false;
		} else if (!p2Deck.equals(other.p2Deck))
			return false;
		if (p2Hand == null) {
			if (other.p2Hand != null)
				return false;
		} else if (!p2Hand.equals(other.p2Hand))
			return false;
		if (!Arrays.deepEquals(squares, other.squares))
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}
	
}
