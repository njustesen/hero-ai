package gameobjects;

public abstract class GameObject {

	public abstract GameObject copy();

	public abstract int hashCode();

	public abstract boolean equals(Object obj);
	
}
