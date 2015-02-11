package model;

import java.util.ArrayList;
import java.util.List;

public class HAMap {

	public static HAMap mapA = HAMap.getMap();

	public static HAMap getMap() {

		final SquareType[][] grid = new SquareType[9][5];

		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid[0].length; y++)
				grid[x][y] = SquareType.NONE;

		// Deploy squares
		grid[0][0] = SquareType.DEPLOY_1;
		grid[0][4] = SquareType.DEPLOY_1;
		grid[8][0] = SquareType.DEPLOY_2;
		grid[8][4] = SquareType.DEPLOY_2;

		// Assault boost squares
		grid[2][2] = SquareType.POWER;
		grid[6][2] = SquareType.POWER;

		// Defense boost squares
		grid[4][0] = SquareType.DEFENSE;

		// Power boost squares
		grid[4][4] = SquareType.ASSAULT;

		final HAMap map = new HAMap(grid.length, grid[0].length, grid);

		map.p1Crystals.add(new Position(2, 1));
		map.p1Crystals.add(new Position(1, 3));
		map.p2Crystals.add(new Position(6, 1));
		map.p2Crystals.add(new Position(7, 3));
		
		return map;

	}

	public int width;
	public int height;
	public SquareType[][] squares;
	public List<Position> assaultSquares;
	public List<Position> p1DeploySquares;
	public List<Position> p2DeploySquares;
	public List<Position> p1Crystals;
	public List<Position> p2Crystals;

	public HAMap(int width, int height, SquareType[][] squares) {
		super();
		this.width = width;
		this.height = height;
		this.squares = squares;
		assaultSquares = new ArrayList<Position>();
		p1DeploySquares = new ArrayList<Position>();
		p2DeploySquares = new ArrayList<Position>();
		p1Crystals = new ArrayList<Position>();
		p2Crystals = new ArrayList<Position>();
		for (int x = 0; x < squares.length; x++)
			for (int y = 0; y < squares[0].length; y++) {
				if (squares[x][y] == SquareType.DEPLOY_1)
					p1DeploySquares.add(new Position(x, y));
				if (squares[x][y] == SquareType.DEPLOY_2)
					p2DeploySquares.add(new Position(x, y));
				if (squares[x][y] == SquareType.ASSAULT)
					assaultSquares.add(new Position(x, y));
			}
	}

	public SquareType squareAt(int x, int y) {

		return squares[x][y];

	}

	public SquareType squareAt(Position position) {
		return squareAt(position.x, position.y);
	}

}
