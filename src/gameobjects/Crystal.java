package gameobjects;

public class Crystal extends GameObject {
	
	public static short STANDARD_HP = 4500;
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + hp;
		result = prime * result + (p1Owner ? 1231 : 1237);
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
		Crystal other = (Crystal) obj;
		if (hp != other.hp)
			return false;
		if (p1Owner != other.p1Owner)
			return false;
		return true;
	}
	
	
	
}
