package model;

import game.GameState;

import java.util.HashSet;
import java.util.Set;

import lib.Card;
import lib.CardType;
import lib.UnitClass;
import lib.UnitClassLib;


public class Unit {

	public short hp;
	public UnitClass unitClass;
	public Set<Card> equipment;
	public boolean p1Owner;
	
	public Unit(Card type, boolean p1Owner) {
		super();
		this.unitClass = UnitClassLib.lib.get(type);
		this.hp = unitClass.maxHP;
		this.equipment = new HashSet<Card>();
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, Unit type, boolean p1Owner, Set<Card> equipment) {
		super();
		this.unitClass = UnitClassLib.lib.get(type);
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, UnitClass uclass, boolean p1Owner, Set<Card> equipment) {
		super();
		this.unitClass = uclass;
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}
	
	public void equip(Card card, GameState state) {
		if (card == Card.REVIVE_POTION){
			if (hp == 0)
				hp += 100;
			else
				hp += 600;
		} else {
			equipment.add(card);
			if (card == Card.DRAGONSCALE || card == Card.SHINING_HELM)
				hp += (double)hp / 10d;			
		}
		hp = (short) Math.min(hp, maxHP());
	}
	
	public int power(GameState state, Position pos) {
		
		// Initial power
		int power = unitClass.power;
		
		// Sword
		if (equipment.contains(Card.RUNEMETAL))
			power += power/2;
		
		// Power boost
		if (state.map.squareAt(pos.x, pos.y).type == SquareType.POWER)
			power += 100;
		
		// Scroll
		if (equipment.contains(Card.SCROLL))
			power *= 3;
		
		return power;
	}
	
	public int damage( GameState state, Position attPos, Unit defender, Position defPos) {
		
		double dam = power(state, attPos);
		
		if (attPos.distance(defPos) == 1)
			dam *= unitClass.attack.meleeMultiplier;
		else
			dam *= unitClass.attack.rangeMultiplier;
		
		double resistance = defender.resistance(state, defPos, unitClass.attack.attackType);
		
		return (int) (dam * ((100d - resistance)/100d));
	}
	
	public short maxHP() {
		
		short max = unitClass.maxHP;
		
		if (equipment.contains(Card.DRAGONSCALE))
			max += unitClass.maxHP/10;
		if (equipment.contains(Card.SHINING_HELM))
			max += unitClass.maxHP/10;
		
		return max;
	}
	
	public int resistance(GameState state, Position pos, AttackType attackType) {
		
		int res = 0;
		
		if (attackType == AttackType.Magical){
			res += unitClass.magicalResistance;
			if (equipment.contains(Card.SHINING_HELM))
				res += 20;
			// TODO : also +20 for defense square?
		} else if (attackType == AttackType.Physical){
			res += unitClass.physicalResistance;
			if (equipment.contains(Card.DRAGONSCALE))
				res += 20;
			if (state.map.squareAt(pos.x, pos.y).type == SquareType.DEFENSE)
				res += 20;
		}
		
		return res;
		
	}
	
	public boolean fullHealth() {
		if (hp == maxHP())
			return true;
		return false;
	}
	
	public void heal(int health) {
		
		hp = (short) Math.min(hp + health, this.maxHP());
		
	}

	public Unit copy() {
		Set<Card> eq = new HashSet<Card>();
		for (Card card : equipment)
			eq.add(card);
		
		return new Unit(hp, unitClass, p1Owner, eq);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((equipment == null) ? 0 : equipment.hashCode());
		result = prime * result + hp;
		result = prime * result + (p1Owner ? 1231 : 1237);
		result = prime * result
				+ ((unitClass == null) ? 0 : unitClass.hashCode());
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
		Unit other = (Unit) obj;
		if (equipment == null) {
			if (other.equipment != null)
				return false;
		} else if (!equipment.equals(other.equipment))
			return false;
		if (hp != other.hp)
			return false;
		if (p1Owner != other.p1Owner)
			return false;
		if (unitClass == null) {
			if (other.unitClass != null)
				return false;
		} else if (!unitClass.equals(other.unitClass))
			return false;
		return true;
	}
	
}
