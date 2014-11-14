package model;

public class Direction {

	int x;
	int y;
	
	public Direction(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		if (this.x > 1)
			this.x = 1;
		if (this.x < -1)
			this.x = -1;
		if (this.y > 1)
			this.y = 1;
		if (this.y < -1)
			this.y = -1;
	}
	
	public boolean isDiagonal(){
		return (x != 0 && y != 0);
	}
	
	public boolean isNorth(){
		return (y == -1);
	}
	
	public boolean isEast(){
		return (x == 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		Direction other = (Direction) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public boolean opposite(Direction dir) {
		if (dir.x*(-1) == x && dir.y*(-1) == y){
			return true;
		}
		return false;
	}
	
}