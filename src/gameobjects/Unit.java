package gameobjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lib.UnitClassLib;


public class Unit extends GameObject {

	public short hp;
	public UnitClass unitClass;
	public Set<GameObjectType> equipment;
	public boolean p1Owner;
	
	public Unit(GameObjectType unit, boolean p1Owner) {
		super();
		this.unitClass = UnitClassLib.lib.get(unit);
		this.hp = unitClass.maxHP;
		this.equipment = new HashSet<GameObjectType>();
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, GameObjectType unit, boolean p1Owner, Set<GameObjectType> equipment) {
		super();
		this.unitClass = UnitClassLib.lib.get(unit);
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, UnitClass uclass, boolean p1Owner, Set<GameObjectType> equipment) {
		super();
		this.unitClass = uclass;
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}

	@Override
	public GameObject copy() {
		Set<GameObjectType> eq = new HashSet<GameObjectType>();
		for (GameObjectType type : equipment)
			eq.add(type);
		
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
