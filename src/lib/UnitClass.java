package lib;

import model.Attack;
import model.Heal;

public class UnitClass {

	public Card card;
	public short maxHP;
	public byte speed;
	public byte physicalResistance;
	public byte magicalResistance;
	public short power;
	public Attack attack;
	public Heal heal;
	public boolean swap;
	
	public UnitClass(Card card, short maxHP, byte speed, short power, byte physicalResistance, 
			byte magicalResistance, Attack attack,
			Heal heal, boolean swap) {
		super();
		
		this.card = card;
		this.maxHP = maxHP;
		this.speed = speed;
		this.power = power;
		this.attack = attack;
		this.heal = heal;
		this.physicalResistance = physicalResistance;
		this.magicalResistance = magicalResistance;
		this.swap = swap;
	}
	
}
