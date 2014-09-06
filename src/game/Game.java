package game;


import java.util.List;
import java.util.Stack;

import model.AttackType;
import model.HAMap;
import model.Position;
import model.Square;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UndoAction;
import action.UnitAction;
import ai.RandomAI;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private boolean p1Winner;
	private Stack<GameState> history;
	
	public static void main(String [ ] args)
	{
		Game game = new Game(null, true, new RandomAI(), new RandomAI());
	}
	
	public Game(GameState state, boolean ui, AI player1, AI player2){
		this.player1 = player1;
		this.player2 = player2;
		this.history = new Stack<GameState>();
		if (state != null)
			this.state = state;
		else 
			this.state = new GameState(HAMap.getMap());
		
		p1Winner = false;
		/*
		// Add units
		this.state.objects.put(new Position((byte)0,(byte)0), new Unit(GameObjectType.Knight, true));
		this.state.objects.put(new Position((byte)0,(byte)1), new Unit(GameObjectType.Archer, true));
		this.state.objects.put(new Position((byte)0,(byte)2), new Unit(GameObjectType.Wizard, true));
		this.state.objects.put(new Position((byte)0,(byte)3), new Unit(GameObjectType.Cleric, true));
		this.state.objects.put(new Position((byte)0,(byte)4), new Unit(GameObjectType.Ninja, true));
		
		// Add units
		this.state.objects.put(new Position((byte)8,(byte)0), new Unit(GameObjectType.Knight, false));
		this.state.objects.put(new Position((byte)8,(byte)1), new Unit(GameObjectType.Archer, false));
		this.state.objects.put(new Position((byte)8,(byte)2), new Unit(GameObjectType.Wizard, false));
		this.state.objects.put(new Position((byte)8,(byte)3), new Unit(GameObjectType.Cleric, false));
		this.state.objects.put(new Position((byte)8,(byte)4), new Unit(GameObjectType.Ninja, false));
		*/
		//history.push(this.state.copy());
		
		this.ui = new UI(this.state);
		
		run();
	}
	
	public void run(){
		
		long start = System.nanoTime();
		long ai = 0;
		long engine = 0;
		
		while(!state.isTerminal){
			/*
			if (ui != null){
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			*/
			//int ap = state.APLeft;
			
			if (state.p1Turn && player1 != null) {
				long aiStart = System.nanoTime();
				Action action = player1.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
			} else if (!state.p1Turn && player2 != null){
				long aiStart = System.nanoTime();
				Action action = player2.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
			} else {
				try {
					// Wait for human input
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/*
			if (state.APLeft < ap)
				history.push(state.copy());
				*/
		}
		long end = System.nanoTime();
		System.out.println("Game took " + ((end - start)/1000000d) + " ms. (" + ((end - start)/1000000d)/(double)state.turn + " per turn.");
		System.out.println("Game had " + state.turn + " turns.");
		System.out.println("AI spend " + (ai/1000000d) + " ms. (" + (ai/1000000d)/(double)state.turn + " per turn." );
		System.out.println("Engine spend " + (engine/1000000d) + " ms. (" + (engine/1000000d)/(double)state.turn + " per turn." );
		System.out.println("Missing " + (((end - start) - (engine+ai))/1000000d) + " ms.");
		if (ui != null){
			ui.state = state.copy();
			ui.repaint();
		}
		System.out.println("Player " + (p1Winner ? 1 : 2) + " won the game!");
		
	}

	private void update(Action action) {
		
		if (action instanceof UndoAction){
			if (state.APLeft == 5)
				return;
			else 
				undo();
		}
		
		if (action instanceof EndTurnAction){
			if (state.APLeft == 0)
				endTurn();
			else 
				return;
		}
		
		if (state.APLeft == 0)
			return;
		
		if (action instanceof DropAction){
			
			DropAction drop = (DropAction)action;
	
			// Not a type in current players hand
			if (!state.currentHand().contains(drop.type))
				return;
			
			// Unit
			if (state.isUnit(drop.type)){
				
				// Not a deploy square
				if (!(state.map.squareAt(drop.to.x, drop.to.y) == Square.P1DEPLOY) && !(state.map.squareAt(drop.to.x, drop.to.y) == Square.P2DEPLOY))
					return;
				
				// Not current players deploy square
				if (state.map.squareAt(drop.to.x, drop.to.y) == Square.P1DEPLOY && !state.p1Turn)
					return;
				if (state.map.squareAt(drop.to.x, drop.to.y) == Square.P2DEPLOY && state.p1Turn)
					return;
				
				deploy(drop.type, drop.to);
				
			}
			
			// Equipment
			if (state.isEquipment(drop.type)){
				
				// Not a unit square
				if (!(state.objects.get(drop.to) instanceof Unit))
					return;
				
				Unit unit = ((Unit)state.objects.get(drop.to));
				
				if (unit.p1Owner != state.p1Turn)
					return;
					
				if (unit.hp == 0)
					return;
				
				if (unit.equipment.contains(drop.type))
					return;
							
				equip(drop.type, unit);
				
			}
			
			// Spell
			if (state.isSpell(drop.type)){
				
				dropInferno(drop.to);
				
			}
			
			return;
			
		}
		
		if (action instanceof UnitAction){
			
			UnitAction unitAction = (UnitAction)action;
			GameObject objFrom = state.objects.get(unitAction.from);
	
			if (objFrom == null || !(objFrom instanceof Unit))
				return;
			
			Unit unitFrom = (Unit)objFrom;
			
			if (unitFrom.p1Owner != state.p1Turn)
				return;
			
			if (unitFrom.hp == 0)
				return;
			
			// Move
			GameObject objTo = state.objects.get(unitAction.to);
			if (objTo == null){
				
				if (state.distance(unitAction.from, unitAction.to) > unitFrom.unitClass.speed)
					return;
				
				if ((state.map.squareAt(unitAction.to.x, unitAction.to.y) == Square.P1DEPLOY) && !unitFrom.p1Owner)
					return;
				
				if ((state.map.squareAt(unitAction.to.x, unitAction.to.y) == Square.P2DEPLOY) && unitFrom.p1Owner)
					return;
				
				move(unitFrom, unitAction.from, unitAction.to);
				return;
			}
			
			if (objTo instanceof Unit){
				
				Unit unitTo = (Unit)objTo;
				
				// Swap and heal
				if (unitFrom.p1Owner == unitTo.p1Owner){
					if (unitFrom.unitClass.swap){
						swap(unitFrom, unitAction.from, unitTo, unitAction.to);
						return;
					}
					if(unitFrom.unitClass.heal == null)
						return;
					if(unitFrom.unitClass.heal.range > state.distance(unitAction.from,unitAction.to))
						return;
					if(unitTo.hp == unitTo.unitClass.maxHP)
						return;
					heal(unitFrom, unitAction.from, unitTo);
					return;
				}
				
				// Attack unit
				if (state.distance(unitAction.from, unitAction.to) > unitFrom.unitClass.attack.range)
					return;
				
				attackUnit(unitFrom, unitAction.from, unitTo, unitAction.to);
				return;
				
			}
			
			if (objTo instanceof Crystal){
				
				// Attack crystal
				if (state.distance(unitAction.from, unitAction.to) > unitFrom.unitClass.attack.range)
					return;
				
				attackCrystal(unitFrom, unitAction.from, (Crystal)objTo, unitAction.to);
				return;
				
			}
		}
		
		
	}

	private void dropInferno(Position to) {
		
		for(byte x = -1; x <= 1; x++){
			for(byte y = -1; y <= 1; y++){
				Position pos = new Position((byte)(to.x + x), (byte)(to.y + y));
				GameObject obj = state.objects.get(pos);
				if (obj != null && obj instanceof Unit){
					Unit def = ((Unit)obj);
					double dam = 300;
					double resistance = resistance(def, pos, AttackType.Magical);
					dam = dam * ((100d - resistance)/100d);
					def.hp -= Math.min(dam, def.hp);
				} else if (obj != null && obj instanceof Crystal){
					Crystal cry = ((Crystal)obj);
					int dam = 300;
					cry.hp -= Math.min(dam, cry.hp);
				}
			}
		}
		
		state.currentHand().remove(GameObjectType.Inferno);
		state.APLeft--;
		
	}

	private void attackCrystal(Unit attacker, Position attPos, Crystal crystal, Position cryPos) {
		crystal.hp -= damage(attacker, attPos, crystal, cryPos);
		if (crystal.hp < 0){
			crystal.hp = 0;
			state.objects.remove(cryPos);
			checkWinOnCrystals();
		}
		attacker.equipment.remove(GameObjectType.Scroll);
		state.APLeft--;
	}

	private void attackUnit(Unit attacker, Position attPos, Unit defender, Position defPos) {
		if (defender.hp == 0){
			state.objects.remove(defPos);
			move(attacker, attPos, defPos);
			checkWinOnUnits();
		} else {
			defender.hp -= damage(attacker, attPos, defender, defPos);
			if (defender.hp < 0)
				defender.hp = 0;
			if (attacker.unitClass.attack.push)
				push(defender, attPos, defPos);
			if (attacker.unitClass.attack.chain){
				// TODO
			}
		}
		attacker.equipment.remove(GameObjectType.Scroll);
		state.APLeft--;
	}

	private void checkWinOnUnits() {
		
		boolean p1Alive = false;
		boolean p2Alive = false;
		
		for(Position pos : state.objects.keySet()){
			if (state.objects.get(pos) instanceof Unit){
				if (((Unit)state.objects.get(pos)).p1Owner)
					p1Alive = true;
				if (!((Unit)state.objects.get(pos)).p1Owner)
					p2Alive = true;
			}
		}
		
		if (p1Alive && !p2Alive){
			state.isTerminal = true;
			p1Winner = true;
			return;
		}
		
		if (!p1Alive && p2Alive){
			state.isTerminal = true;
			p1Winner = false;
			return;
		}
		
		for(GameObjectType type : state.p1Deck){
			if (state.isUnit(type)){
				p1Alive = true;
			}
		}
		for(GameObjectType type : state.p1Hand){
			if (state.isUnit(type)){
				p1Alive = true;
			}
		}
		for(GameObjectType type : state.p2Deck){
			if (state.isUnit(type)){
				p2Alive = true;
			}
		}
		for(GameObjectType type : state.p2Hand){
			if (state.isUnit(type)){
				p2Alive = true;
			}
		}
		
		if (p1Alive && !p2Alive){
			state.isTerminal = true;
			p1Winner = true;
			return;
		}
		
		if (!p1Alive && p2Alive){
			state.isTerminal = true;
			p1Winner = false;
			return;
		}
		
	}
	

	private void checkWinOnCrystals() {
		boolean p1Alive = false;
		boolean p2Alive = false;
		
		for(GameObject obj : state.objects.values()){
			if (obj instanceof Crystal){
				if (((Crystal)obj).p1Owner)
					p1Alive = true;
				else if (!((Crystal)obj).p1Owner)
					p2Alive = true;
			}
		}
		
		if (p1Alive && !p2Alive){
			state.isTerminal = true;
			p1Winner = true;
			return;
		}
		
		if (!p1Alive && p2Alive){
			state.isTerminal = true;
			p1Winner = false;
			return;
		}
		
	}

	private void push(Unit defender, Position attPos, Position defPos) {
		
		byte x = 0;
		byte y = 0;
		
		if (attPos.x > defPos.x)
			x = -1;
		if (attPos.x < defPos.x)
			x = 1;
		if (attPos.y > defPos.y)
			x = -1;
		if (attPos.y < defPos.y)
			x = 1;
		
		Position newPos = new Position((byte)(defPos.x + x), (byte)(defPos.y + y));
		if (newPos.x >= state.map.width || newPos.x < 0 || newPos.y >= state.map.height || newPos.y < 0)
			return;
		
		if (state.objects.get(newPos) != null)
			return;
		
		state.objects.remove(defPos);
		state.objects.put(newPos, defender);
		
	}

	private int damage(Unit attacker, Position attPos, Unit defender, Position defPos) {
		
		int dam = state.power(attacker, attPos);
		
		if (state.distance(attPos, defPos) == 1)
			dam *= attacker.unitClass.attack.meleeMultiplier;
		else
			dam *= attacker.unitClass.attack.rangeMultiplier;
		
		int resistance = resistance(defender, defPos, attacker.unitClass.attack.attackType);
		
		return dam * ((100 - resistance)/100);
	}
	

	private short damage(Unit attacker, Position attPos, Crystal crystal, Position cryPos) {
		int dam = state.power(attacker, attPos);
		
		if (state.distance(attPos, cryPos) == 1)
			dam *= attacker.unitClass.attack.meleeMultiplier;
		else
			dam *= attacker.unitClass.attack.rangeMultiplier;
		
		for(Position pos : state.objects.keySet()){
			if (state.map.squareAt(pos.x, pos.y) == Square.ASSAULT_BOOST){
				if (state.objects.get(pos) instanceof Unit){
					if (attacker.p1Owner == ((Unit)state.objects.get(pos)).p1Owner){
						dam *= 2;
						break;
					}
				}
			}
		}
		
		return (short) dam;
	}

	private int resistance(Unit unit, Position pos, AttackType attackType) {
		
		int res = 0;
		
		if (attackType == AttackType.Magical){
			res += unit.unitClass.magicalResistance;
			if (unit.equipment.contains(GameObjectType.ShiningHelm))
				res += 20;
		} else {
			if (attackType == AttackType.Physical){
				res += unit.unitClass.physicalResistance;
				if (unit.equipment.contains(GameObjectType.Dragonscale) || state.map.squareAt(pos.x, pos.y) == Square.DEFENSE_BOOST)
					res += 20;
			}
		}
		
		return res;
		
	}

	private void heal(Unit healer, Position pos, Unit unitTo) {
		
		int power = state.power(healer, pos);
		if (unitTo.hp == 0)
			power *= healer.unitClass.heal.revive;
		else
			power *= healer.unitClass.heal.heal;
		
		unitTo.hp += power;
		
		int maxHP = state.maxHP(unitTo);
		if (unitTo.hp > maxHP)
			unitTo.hp = (short) maxHP;
			
		healer.equipment.remove(GameObjectType.Scroll);
		state.APLeft--;
		
	}

	private void swap(Unit unitFrom, Position from, Unit unitTo, Position to) {
		state.objects.remove(from);
		state.objects.remove(to);
		state.objects.put(to, unitFrom);
		state.objects.put(to, unitTo);
		state.APLeft--;
	}

	private void move(Unit unit, Position from, Position to) {
		state.objects.remove(from);
		state.objects.put(to, unit);
		state.APLeft--;
	}

	private void deploy(GameObjectType unitType, Position pos) {
		state.objects.put(pos, new Unit(unitType, state.p1Turn));
		state.currentHand().remove(unitType);
		state.APLeft--;
	}

	private void undo() {
		if (history.size() > 1){
			history.pop();
			state = history.peek();
		}
	}

	private void endTurn() {
		state.removeDying();
		state.drawCards();
		state.p1Turn = !state.p1Turn;
		state.APLeft = 5;
		state.turn++;
		//history.clear();
		//history.push(state.copy());
	}

	
}
