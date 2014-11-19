package game;

import java.util.Stack;

import model.HAMap;

import action.Action;
import action.UndoAction;
import ai.EvaAI;
import ai.RandomAI;
import ai.RandomMemAI;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	private static final int SLEEP = 500;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private Stack<GameState> history;
	private int lastTurn;
	
	public static void main(String [ ] args)
	{
		//Game game = new Game(null, true, new EvaAI(true), null);
		Game game = new Game(null, true, new RandomAI(true), new RandomAI(false));
		
		game.run();
		
		//experiment(game, 10000);
		
	}
	
	private static void experiment(Game game, int runs) {
		
		int run = 1;
		int p1Won = 0;
		int p2Won = 0;
		int draw = 0;
		
		while (run < runs){
			
			Game newGame = new Game(null, game.ui != null, game.player1, game.player2);
			newGame.run();
			
			if (newGame.state.getWinner() == 1)
				p1Won++;
			else if (newGame.state.getWinner() == 2)
				p2Won++;
			else 
				draw++;
			
			if (run % 1000 == 0){
				System.out.println(run + "\t" + p1Won + "\t" + p2Won + "\t" + draw);
			}
			
			run++;
			
			
		}
		
		System.out.println("P1: " + p1Won + " wins " + ((double)p1Won/(double)runs * 100.0));
		System.out.println("P2: " + p2Won + " wins " + ((double)p2Won/(double)runs * 100.0));
		System.out.println("Draws: " + draw);
		
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
		if (ui)
			this.ui = new UI(this.state, (this.player1==null), (this.player2==null));
		
		history = new Stack<GameState>();
		
	}
	
	public void run(){
		
		long start = System.nanoTime();
		long ai = 0;
		long engine = 0;
		
		int turnLimit = 50000;
		
		history.add(state.copy());
		lastTurn = 5;
		
		while(!state.isTerminal && state.turn < turnLimit){
			
			if (SLEEP >= 20 && ui != null){
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(SLEEP);
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
				if (ui != null)
					ui.lastAction = action;
				//System.out.println("P1: " + action);
			} else if (!state.p1Turn && player2 != null){
				long aiStart = System.nanoTime();
				Action action = player2.act(state, TIME_LIMIT);
				long aiEnd = System.nanoTime();
				long engineStart = System.nanoTime();
				state.update(action);
				long engineEnd = System.nanoTime();
				ai += aiEnd - aiStart;
				engine += engineEnd - engineStart;
				if (ui != null)
					ui.lastAction = action;
				//System.out.println("P2: " + action);
			} else {
				
				if (ui.action != null){
					
					long engineStart = System.nanoTime();
					if (ui.action instanceof UndoAction){
						undoAction();
					} else {
						state.update(ui.action);
					}
					long engineEnd = System.nanoTime();
					engine += engineEnd - engineStart;
					ui.lastAction = ui.action;
					int p = 1;
					if (!state.p1Turn)
						p = 2;
					//System.out.println("P" + p + ": " + ui.action);
					ui.resetActions();
					
				}
				
			}
			
			if (state.APLeft != lastTurn){
				if (state.APLeft < lastTurn){
					history.add(state.copy());
				}
				lastTurn = state.APLeft;
			}
			
			if (state.APLeft == 5){
				history.clear();
				history.add(state.copy());
				lastTurn = 5;
			}
			
		}
		long end = System.nanoTime();
		//System.out.println("Game took " + ((end - start)/1000000d) + " ms. (" + ((end - start)/1000000d)/(double)state.turn + " per turn.");
		//System.out.println("Game had " + state.turn + " turns.");
		//System.out.println("AI spend " + (ai/1000000d) + " ms. (" + (ai/1000000d)/(double)state.turn + " per turn." );
		//System.out.println("Engine spend " + (engine/1000000d) + " ms. (" + (engine/1000000d)/(double)state.turn + " per turn." );
		//System.out.println("Missing " + (((end - start) - (engine+ai))/1000000d) + " ms.");
		if (ui != null){
			ui.state = state.copy();
			ui.repaint();
		}
		//System.out.println("Player " + state.getWinner() + " won the game!");
		
	}

	private void undoAction() {
		
		if (state.APLeft == 5)
			return;
		
		if (state.isTerminal)
			return;
		
		history.pop();
		
		state = history.peek().copy();
		
	}

}
