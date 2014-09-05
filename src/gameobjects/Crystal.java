package gameobjects;

public class Crystal extends GameObject {
	
	public static short STANDARD_HP = 2500;
	
	public boolean p1Owner;
	public short hp;
	
	public Crystal(boolean p1Owner, short hp) {
		super();
		this.p1Owner = p1Owner;
		this.hp = hp;
	}
	
	public Crystal(boolean p1Owner) {
		super();
		this.p1Owner = p1Owner;
		this.hp = STANDARD_HP;
	}

	@Override
	public GameObject copy() {
		return new Crystal(p1Owner, hp);
	}
	
}
