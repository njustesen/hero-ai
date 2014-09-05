package gameobjects;

import java.util.HashMap;
import java.util.Map;

public class HAMap {

	static Map<Byte, Square> encoding;
	static
    {
		encoding = new HashMap<Byte, Square>();
		encoding.put((byte)0, Square.EMPTY);
		encoding.put((byte)1, Square.P1DEPLOY);
		encoding.put((byte)2, Square.P2DEPLOY);
		encoding.put((byte)10, Square.P1CRYSTAL);
		encoding.put((byte)20, Square.P2CRYSTAL);
		encoding.put((byte)3, Square.ASSAULT_BOOST);
		encoding.put((byte)4, Square.DEFENSE_BOOST);
		encoding.put((byte)5, Square.POWER_BOOST);
    }
	
	public static HAMap getMap() {
		
		byte[][] grid = new byte[9][5];
		
		// Deploy squares
		grid[0][0] = 1;
		grid[0][4] = 1;
		grid[8][0] = 2;
		grid[8][4] = 2;
		
		// Crystal squares
		grid[2][1] = 10;
		grid[1][3] = 10;
		grid[6][1] = 20;
		grid[7][3] = 20;
		
		// Assult boost squares
		grid[2][2] = 3;
		grid[6][2] = 3;
		
		// Defense boost squares
		grid[4][0] = 4;
		
		// Power boost squares
		grid[4][4] = 5;
		
		return new HAMap((byte)grid.length, (byte)grid[0].length, grid);
	
	}
	
	public byte width;
	public byte height;
	public byte[][] squares;
	
	public HAMap(byte width, byte height, byte[][] squares) {
		super();
		this.width = width;
		this.height = height;
		this.squares = squares;
	}
	
	public Square squareAt(byte x, byte y){
		
		return encoding.get(squares[x][y]);
		
	}
	
}
