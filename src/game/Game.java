package game;

import java.util.Stack;

import lib.ImageLib;
import model.HAMap;

import action.Action;
import action.EndTurnAction;
import action.UndoAction;
import ai.NmSearchAI;
import ai.RAND_METHOD;
import ai.RandomAI;
import ai.ScanRandomAI;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	private static final long ANIMATION = 1000;
	private static int SLEEP = 20;
	private static boolean GFX = true;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private Stack<GameState> history;
	private int lastTurn;
	
	public static void main(String [ ] args)
	{
		
		AI[] players = new AI[2];
		int p = -1;
		for(int a = 0; a < args.length; a++){
			if (args[a].toLowerCase().equals("p1")){
				p = 0;
				continue;
			} else if (args[a].toLowerCase().equals("p2")){
				p = 1;
				continue;
			}
			if (p == 0 || p==1){
				if (args[a].toLowerCase().equals("human")){
					players[p] = null;
				} else if (args[a].toLowerCase().equals("random")){
					players[p] = new RandomAI((p==0), RAND_METHOD.TREE);
				} else if (args[a].toLowerCase().equals("scanrandom")){
					players[p] = new ScanRandomAI((p==0));
				} else if (args[a].toLowerCase().equals("nmsearch")){
					a++;
					int n = Integer.parseInt(args[a]);
					a++;
					int m = Integer.parseInt(args[a]);
					players[p] = new NmSearchAI((p==0), n, m);
				}
				p = -1;
			} else if (args[a].toLowerCase().equals("sleep")){
				a++;
				SLEEP = Integer.parseInt(args[a]);
				continue;
			} else if (args[a].toLowerCase().equals("gfx")){
				a++;
				GFX = Boolean.parseBoolean(args[a]);
				continue;
			}
		}
		
		Game game = new Game(null, GFX, players[0], players[1]);
		//Game game = new Game(null, true, new EvaAI(true,1000,1000), null);
		
		game.run();
		
		//Game game = new Game(null, false, new RandomAI(true), new RandomAI(false));
		//experiment(game, 100);
		//System.out.println(ImageLib.lib.get("crystal-1").getHeight());
		
	}
	
	private static void experiment(Game game, int runs) {
		
		int run = 1;
		int p1Won = 0;
		int p2Won = 0;
		int draw = 0;
		
		int turns = 0;
		long start = System.nanoTime();
		while (run < runs){
			
			Game newGame = new Game(null, false, game.player1, game.player2);
			newGame.run();
			
			if (newGame.state.getWinner() == 1)
				p1Won++;
			else if (newGame.state.getWinner() == 2)
				p2Won++;
			else 
				draw++;
			
			turns += newGame.state.turn;
			/*
			if (run % 1000 == 0){
				System.out.println(run + "\t" + p1Won + "\t" + p2Won + "\t" + draw);
			}
			*/
			run++;
			
		}
		long ns = System.nanoTime() - start;
		
		System.out.println("Time: " + ns);
		System.out.println("Time/game: " + ns/runs);
		System.out.println("Turn/game: " + turns/runs);
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
			
			if (state.p1Turn && player1 != null) {
				Action action = player1.act(state, TIME_LIMIT);
				if (action == null)
					action = new EndTurnAction();
				state.update(action);
				if (ui != null)
					ui.lastAction = action;
				if (player2 == null){
					try {
						Thread.sleep(ANIMATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else if (!state.p1Turn && player2 != null){
				Action action = player2.act(state, TIME_LIMIT);
				if (action == null)
					action = new EndTurnAction();
				state.update(action);
				if (ui != null)
					ui.lastAction = action;
				if (player1 == null){
					try {
						Thread.sleep(ANIMATION);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				
				if (ui.action != null){
					
					if (ui.action instanceof UndoAction){
						undoAction();
					} else {
						state.update(ui.action);
					}
					ui.lastAction = ui.action;
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
		//System.out.println("Game took " + ((end - start)/1000000d) + " ms. (" + ((end - start)/1000000d)/(double)state.turn + " per turn.");
		//System.out.println("Game had " + state.turn + " turns.");
		//System.out.println("AI spend " + (ai/1000000d) + " ms. (" + (ai/1000000d)/(double)state.turn + " per turn." );
		//System.out.println("Engine spend " + (engine/1000000d) + " ms. (" + (engine/1000000d)/(double)state.turn + " per turn." );
		//System.out.println("Missing " + (((end - start) - (engine+ai))/1000000d) + " ms.");
		if (ui != null){
			ui.state = state.copy();
			ui.repaint();
		}
		//long time = (System.nanoTime() - ns);
		//System.out.println("Time = " + time + ", " + time/state.turn);
		//System.out.println("Time = " + time/1000000.0 + ", " + time/state.turn/1000000.0);
		//System.out.println("P1Time = " + p1Time + ", " + p1Time/((5*state.turn)/2));
		//System.out.println("P2Time = " + p2Time + ", " + p2Time/((5*state.turn)/2));
		//System.out.println("Player " + state.getWinner() + " won the game!");
		//System.out.println("Turn: " + state.turn);
		
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
