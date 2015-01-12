package ui;

import game.GameState;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SwapCardAction;
import action.UndoAction;
import action.UnitAction;

import model.Position;
import model.Unit;

public class InputController implements MouseListener, KeyListener, MouseMotionListener {
	
	private boolean humanP1;
	private boolean humanP2;
	private int gridX;
	private int gridY;
	private int squareSize;
	public GameState state;
	public Position activeSquare;
	public List<Action> possibleActions;
	public Action action;
	public int activeCardIdx;
	public int mouseX;
	public int mouseY;
	
	public InputController(boolean humanP1, boolean humanP2, int gridX, int gridY, int squareSize) {
		this.humanP1 = humanP1;
		this.humanP2 = humanP2;
		this.squareSize = squareSize;
		this.gridX = gridX;
		this.gridY = gridY;
		this.possibleActions = new ArrayList<Action>();
		this.activeCardIdx = -1;
	}

	public void reset() {
		state = null;
		activeCardIdx = -1;
		possibleActions.clear();
		action = null;
		activeSquare = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		
		//System.out.println("clicked on x:" + arg0.getX() + ", y:" + arg0.getY());
		
		if (arg0.getX() < gridX || arg0.getY() < gridY){
			//System.out.println("Clicked out of bounds");
			return;
		}
		
		int squareX = (arg0.getX() - gridX) / squareSize;
		int squareY = (arg0.getY() - gridY) / squareSize;
		//System.out.println("clicked on square x:" + squareX + ", y:" + squareY);
		
		if (state == null){
			System.out.println("Gamestate is null");
			return;
		}
		
		if (squareX >= state.map.width || squareY >= state.map.height){
			//System.out.println("Out of grid");
			
			// Hand
			int width = state.map.width * squareSize + squareSize*2;
			int start = (width / 2) - ((6 * squareSize) / 2);
			int bottom = squareSize + state.map.height * squareSize + squareSize / 4;
			
			for(int i = 0; i < state.currentHand().size(); i++){
				int fromX = start + squareSize * i;
				int toX = fromX + squareSize;
				int fromY = bottom;
				int toY = fromY + squareSize;
				
				if (arg0.getX() >= fromX 
						&& arg0.getX() <= toX
						&& arg0.getY() >= fromY
						&& arg0.getY() <= toY){
					
					cardClicked(i);
					return;
				}
			}
			
			// End turn
			int buttonWidth = 90;
			int buttonStart = squareSize * state.map.width - 24;
			int buttonHeight = 64;
			if (arg0.getX() >= buttonStart
					&& arg0.getX() <= buttonStart + buttonWidth
					&& arg0.getY() >= bottom
					&& arg0.getY() <= bottom + buttonHeight){
				if (state.APLeft == 0){
					action = new EndTurnAction();
				}
			}
			
			// Undo turn
			int undoWidth = 90 - 24;
			int undoStart = squareSize + 24;
			int undoHeight = 64;
			if (arg0.getX() >= undoStart
					&& arg0.getX() <= undoStart + undoWidth
					&& arg0.getY() >= bottom
					&& arg0.getY() <= bottom + undoHeight){
				if (state.APLeft != 5){
					action = new UndoAction();
				}
			}
			
			// Door
			int doorWidth = 30;
			int doorStart = 20;
			if (!state.p1Turn)
				doorStart = squareSize + squareSize * state.map.width + 20;
			int doorHeight = 60;
			if (arg0.getX() >= doorStart
					&& arg0.getX() <= doorStart + doorWidth
					&& arg0.getY() >= squareSize
					&& arg0.getY() <= squareSize + doorHeight){
				if (state.APLeft > 0){
					if (activeCardIdx >= 0){
						action = new SwapCardAction(state.currentHand().get(activeCardIdx));
					}
				}
			}
			
			return;
		}
		
		//System.out.println("click registered");
		
		squareClicked(new Position(squareX, squareY));
		
	}

	private void cardClicked(int i) {
		
		if (activeCardIdx == i){
			reset();
			return;
		}
		
		activeSquare = null;
		activeCardIdx = i;
		possibleActions.clear();
		state.possibleActions(state.currentHand().get(i), possibleActions);
		
	}

	private void squareClicked(Position position) {
		
		if (activeSquare != null){
			if (activeSquare.equals(position)){
				reset();
				return;
			}
			for(Action a : possibleActions){
				if (a instanceof UnitAction){
					if (((UnitAction)a).from.equals(activeSquare) && ((UnitAction)a).to.equals(position)){
						action = a;
						//System.out.println("Returning " + a);
						return;
					}
				}
			}
			activateSquare(position);
		} else if (activeCardIdx != -1){
			for(Action a : possibleActions){
				if (a instanceof DropAction){
					if (((DropAction)a).to.equals(position)){
						action = a;
						//System.out.println("Returning " + a);
						return;
					}
				}
			}
			activateSquare(position);
		} else {
			if (state.squareAt(position).unit != null){
				activateSquare(position);
			}
		}
		
	}

	private void activateSquare(Position position) {
		
		possibleActions.clear();
		
		if (state.squareAt(position).unit != null){
			
			Unit unit = state.squareAt(position).unit;
			activeSquare = position;
			activeCardIdx = -1;
			
			if ((unit.p1Owner && humanP1 && state.p1Turn) || (!unit.p1Owner && humanP2 && !state.p1Turn)){
				
				state.possibleActions(unit, position, possibleActions);
				
			}
			
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		
		mouseX = arg0.getPoint().x;
		mouseY = arg0.getPoint().y;
		
	}

	
	
}
