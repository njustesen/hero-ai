package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.Card;

public class HAMap {
	
	public static HAMap getMap() {
		
		Square[][] grid = new Square[9][5];
		
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
		grid[2][2] = new Square(SquareType.ASSAULT, null);
		grid[6][2] = new Square(SquareType.ASSAULT, null);
		
		// Defense boost squares
		grid[4][0] = new Square(SquareType.DEFENSE, null);
		
		// Power boost squares
		grid[4][4] = new Square(SquareType.POWER, null);
		
		HAMap map = new HAMap((byte)grid.length, (byte)grid[0].length, grid);
		
		return map;
	
	}
	
	public byte width;
	public byte height;
	public Square[][] squares;
	public List<Position> p1DeploySquares;
	public List<Position> p2DeploySquares;
	public List<Position> p1Crystals;
	public List<Position> p2Crystals;
	
	public HAMap(byte width, byte height, Square[][] squares) {
		super();
		this.width = width;
		this.height = height;
		this.squares = squares;
		List<Position> deploy1 = new ArrayList<Position>();
		List<Position> deploy2 = new ArrayList<Position>();
		List<Position> crystals1 = new ArrayList<Position>();
		List<Position> crystals2 = new ArrayList<Position>();
		for(int x = 0; x < squares.length; x++){
			for(int y = 0; y < squares[0].length; y++){
				if (squares[x][y].type == SquareType.DEPLOY_1)
					p1DeploySquares.add(new Position(x,y));
				if (squares[x][y].type == SquareType.DEPLOY_2)
					p2DeploySquares.add(new Position(x,y));
				if (squares[x][y].unit != null && squares[x][y].unit.unitClass.card == Card.CRYSTAL){
					if (squares[x][y].unit.p1Owner){
						p1Crystals.add(new Position(x,y));
					} else {
						p2Crystals.add(new Position(x,y));
					}
				}
			}
		}
	}
	
	public Square squareAt(byte x, byte y){
		
		return squares[x][y];
		
	}
	
}
