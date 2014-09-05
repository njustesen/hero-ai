package gameobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
		
		HAMap map = new HAMap((byte)grid.length, (byte)grid[0].length, grid);
		map.deploySquares1.add(new Position(0, 0));
		map.deploySquares1.add(new Position(0, 4));
		map.deploySquares2.add(new Position(8, 0));
		map.deploySquares2.add(new Position(8, 4));
		
		return map;
	
	}
	
	public byte width;
	public byte height;
	public byte[][] squares;
	public List<Position> deploySquares1;
	public List<Position> deploySquares2;
	
	public HAMap(byte width, byte height, byte[][] squares) {
		super();
		this.width = width;
		this.height = height;
		this.squares = squares;
		this.deploySquares1 = new ArrayList<Position>();
		this.deploySquares2 = new ArrayList<Position>();
	}
	
	public Square squareAt(byte x, byte y){
		
		return encoding.get(squares[x][y]);
		
	}

	public List<Position> deploySquares(int player) {
		if (player == 1)
			return deploySquares1;
		return deploySquares2;
	}
	
}
