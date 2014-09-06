package game;


import java.util.List;
import java.util.Stack;

import model.AttackType;
import model.HAMap;
import model.Position;
import model.Square;
import model.Unit;

import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.UndoAction;
import action.UnitAction;
import ai.RandomAI;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private boolean p1Winner;
	private Stack<GameState> history;
	
	public static void main(String [ ] args)
	{
		Game game = new Game(null, true, new RandomAI(), new RandomAI());
	}
	
	public Game(GameState state, boolean ui, AI player1, AI player2){
		this.player1 = player1;
		this.player2 = player2;
		this.history = new Stack<GameState>();
		if (state != null)
			this.state = state;
		else 
			this.state = new GameState(HAMap.getMap());
		
		p1Winner = false;
		/*
		// Add units
		this.state.objects.put(new Position((byte)0,(byte)0), new Unit(GameObjectType.Knight, true));
		this.state.objects.put(new Position((byte)0,(byte)1), new Unit(GameObjectType.Archer, true));
		this.state.objects.put(new Position((byte)0,(byte)2), new Unit(GameObjectType.Wizard, true));
		this.state.objects.put(new Position((byte)0,(byte)3), new Unit(GameObjectType.Cleric, true));
		this.state.objects.put(new Position((byte)0,(byte)4), new Unit(GameObjectType.Ninja, true));
		
		// Add units
		this.state.objects.put(new Position((byte)8,(byte)0), new Unit(GameObjectType.Knight, false));
		this.state.objects.put(new Position((byte)8,(byte)1), new Unit(GameObjectType.Archer, false));
		this.state.objects.put(new Position((byte)8,(byte)2), new Unit(GameObjectType.Wizard, false));
		this.state.objects.put(new Position((byte)8,(byte)3), new Unit(GameObjectType.Cleric, false));
		this.state.objects.put(new Position((byte)8,(byte)4), new Unit(GameObjectType.Ninja, false));
		*/
		//history.push(this.state.copy());
		
		//this.ui = new UI(this.state);
		
		run();
	}
	
	public void run(){
		
		long start = System.nanoTime();
		long ai = 0;
		long engine = 0;
		
		while(!state.isTerminal){
			
			if (ui != null){
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//int ap = state.APLeft;
			
			if (state.p1Turn && player1 != null) {
				long aiStart = System.nanoTime();
				Action action = player1.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				state.update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
			} else if (!state.p1Turn && player2 != null){
				long aiStart = System.nanoTime();
				Action action = player2.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				state.update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
			} else {
				try {
					// Wait for human input
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/*
			if (state.APLeft < ap)
				history.push(state.copy());
				*/
		}
		long end = System.nanoTime();
		System.out.println("Game took " + ((end - start)/1000000d) + " ms. (" + ((end - start)/1000000d)/(double)state.turn + " per turn.");
		System.out.println("Game had " + state.turn + " turns.");
		System.out.println("AI spend " + (ai/1000000d) + " ms. (" + (ai/1000000d)/(double)state.turn + " per turn." );
		System.out.println("Engine spend " + (engine/1000000d) + " ms. (" + (engine/1000000d)/(double)state.turn + " per turn." );
		System.out.println("Missing " + (((end - start) - (engine+ai))/1000000d) + " ms.");
		if (ui != null){
			ui.state = state.copy();
			ui.repaint();
		}
		System.out.println("Player " + (p1Winner ? 1 : 2) + " won the game!");
		
	}

}
