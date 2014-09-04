package model;

public class Attack {

	public byte range;
	public AttackType attackType;
	public double meleeMultiplier;
	public double rangeMultiplier;
	public boolean chain;
	public boolean push;
	
	public Attack(byte range, AttackType attackType,
			short damage, double meleeMultiplier, double rangeMultiplier,
			boolean chain, boolean push) {
		super();
		this.range = range;
		this.attackType = attackType;
		this.meleeMultiplier = meleeMultiplier;
		this.rangeMultiplier = rangeMultiplier;
		this.chain = chain;
		this.push = push;
		
	}
	
}
