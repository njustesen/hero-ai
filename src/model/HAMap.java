package model;

import java.util.ArrayList;
import java.util.List;

import lib.Card;

public class HAMap {

	public static HAMap mapA = HAMap.getMap();

	public static HAMap getMap() {

		final Square[][] grid = new Square[9][5];

		for (int x = 0; x < grid.length; x++)
			for (int y = 0; y < grid[0].length; y++)
				grid[x][y] = new Square(SquareType.NONE, null);

		// Deploy squares
		grid[0][0] = new Square(SquareType.DEPLOY_1, null);
		grid[0][4] = new Square(SquareType.DEPLOY_1, null);
		grid[8][0] = new Square(SquareType.DEPLOY_2, null);
		grid[8][4] = new Square(SquareType.DEPLOY_2, null);

		// Crystal squares
		grid[2][1] = new Square(SquareType.NONE, new Unit(Card.CRYSTAL, true));
		grid[1][3] = new Square(SquareType.NONE, new Unit(Card.CRYSTAL, true));
		grid[6][1] = new Square(SquareType.NONE, new Unit(Card.CRYSTAL, false));
		grid[7][3] = new Square(SquareType.NONE, new Unit(Card.CRYSTAL, false));

		// Assault boost squares
		grid[2][2] = new Square(SquareType.POWER, null);
		grid[6][2] = new Square(SquareType.POWER, null);

		// Defense boost squares
		grid[4][0] = new Square(SquareType.DEFENSE, null);

		// Power boost squares
		grid[4][4] = new Square(SquareType.ASSAULT, null);

		final HAMap map = new HAMap(grid.length, grid[0].length, grid);

		return map;

	}

	public int width;
	public int height;
	public Square[][] squares;
	public List<Position> assaultSquares;
	public List<Position> p1DeploySquares;
	public List<Position> p2DeploySquares;
	public List<Position> p1Crystals;
	public List<Position> p2Crystals;

	public HAMap(int width, int height, Square[][] squares) {
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
				if (squares[x][y].type == SquareType.DEPLOY_1)
					p1DeploySquares.add(new Position(x, y));
				if (squares[x][y].type == SquareType.DEPLOY_2)
					p2DeploySquares.add(new Position(x, y));
				if (squares[x][y].type == SquareType.ASSAULT)
					assaultSquares.add(new Position(x, y));
				if (squares[x][y].unit != null
						&& squares[x][y].unit.unitClass.card == Card.CRYSTAL)
					if (squares[x][y].unit.p1Owner)
						p1Crystals.add(new Position(x, y));
					else
						p2Crystals.add(new Position(x, y));
			}
	}

	public Square squareAt(int x, int y) {

		return squares[x][y];

	}

	public Square squareAt(Position position) {
		return squareAt(position.x, position.y);
	}

}
