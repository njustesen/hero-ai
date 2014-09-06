package model;

public class Square {

	public SquareType type;
	public Unit unit;
	
	public Square(SquareType type, Unit unit) {
		super();
		this.type = type;
		this.unit = unit;
	}
	
	public Square copy(){
		return new Square(type, unit.copy());
	}

}
