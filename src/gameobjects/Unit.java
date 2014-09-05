package gameobjects;

import java.util.ArrayList;
import java.util.List;

import lib.UnitClassLib;


public class Unit extends GameObject {

	public short hp;
	public UnitClass unitClass;
	public List<GameObjectType> equipment;
	public boolean p1Owner;
	
	public Unit(GameObjectType unit, boolean p1Owner) {
		super();
		this.unitClass = UnitClassLib.lib.get(unit);
		this.hp = unitClass.maxHP;
		this.equipment = new ArrayList<GameObjectType>();
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, GameObjectType unit, boolean p1Owner, List<GameObjectType> equipment) {
		super();
		this.unitClass = UnitClassLib.lib.get(unit);
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}
	
	public Unit(short hp, UnitClass uclass, boolean p1Owner, List<GameObjectType> equipment) {
		super();
		this.unitClass = uclass;
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}

	@Override
	public GameObject copy() {
		List<GameObjectType> eq = new ArrayList<GameObjectType>();
		for (GameObjectType type : equipment)
			eq.add(type);
		
		return new Unit(hp, unitClass, p1Owner, eq);
	}
	
}
