package game;


import java.util.Stack;

import model.HAMap;

import action.Action;
import ai.EvaAI;
import ai.RandomMemAI;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private Stack<GameState> history;
	
	public static void main(String [ ] args)
	{
		Game game = new Game(null, true, new EvaAI(true), null);
	}
	
	public Game(GameState state, boolean ui, AI player1, AI player2){
		this.player1 = player1;
		this.player2 = player2;
		this.history = new Stack<GameState>();
		if (state != null)
			this.state = state;
		else 
			this.state = new GameState(HAMap.getMap());
		
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
		
		this.ui = new UI(this.state, (this.player1==null), (this.player2==null));
		
		run();
	}
	
	public void run(){
		
		long start = System.nanoTime();
		long ai = 0;
		long engine = 0;
		
		int turnLimit = 50000;
		
		while(!state.isTerminal && state.turn < turnLimit){
			
			if (ui != null){
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(100);
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
				ui.lastAction = action;
				System.out.println("P1: " + action);
			} else if (!state.p1Turn && player2 != null){
				long aiStart = System.nanoTime();
				Action action = player2.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				state.update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
				ui.lastAction = action;
				System.out.println("P2: " + action);
			} else {
				
				if (ui.action != null){
					
					long engineStart = System.nanoTime();
					state.update(ui.action);
					long engineEnd = System.nanoTime();
					engine += engineEnd - engineStart;
					ui.lastAction = ui.action;
					int p = 1;
					if (!state.p1Turn)
						p = 2;
					System.out.println("P" + p + ": " + ui.action);
					ui.resetActions();
					
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
		System.out.println("Player " + state.getWinner() + " won the game!");
		
	}

}
