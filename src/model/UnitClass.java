package model;

public class UnitClass {

	public GameObjectType unitType;
	public short maxHP;
	public byte speed;
	public byte physicalResistance;
	public byte magicalResistance;
	public short power;
	public Attack attack;
	public Heal heal;
	public boolean swap;
	
	public UnitClass(GameObjectType unitType, short maxHP, byte speed, byte physicalResistance, 
			byte magicalResistance, Attack attack,
			Heal heal, boolean swap) {
		super();
		
		this.unitType = unitType;
		this.maxHP = maxHP;
		this.speed = speed;
		this.attack = attack;
		this.heal = heal;
		this.physicalResistance = physicalResistance;
		this.magicalResistance = magicalResistance;
		this.swap = swap;
	}
	
}
