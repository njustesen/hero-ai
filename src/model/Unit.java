package model;

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
	
	public Unit(byte x, byte y, short hp, GameObjectType unit, boolean p1Owner, List<GameObjectType> equipment) {
		super();
		this.unitClass = UnitClassLib.lib.get(unit);
		this.hp = hp;
		this.equipment = equipment;
		this.p1Owner = p1Owner;
	}
	
}
