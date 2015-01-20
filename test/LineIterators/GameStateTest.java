package LineIterators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.GameState;
import lib.Card;
import model.HAMap;
import model.Unit;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import ai.util.GameStateFactory;
import ai.util.UnitFactory;

public class GameStateTest {

	public static void main(String[] args){
		
		//borrowReturn();
		borrowNReturnN(100);
		
	}

	private static void borrowReturn() {
		
		ObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
		GameState state = new GameState(HAMap.mapA);
		
		for(int i = 0; i < 100; i++){
			GameState clone = null;
			try {
				clone = pool.borrowObject();
				clone.imitate(state);
				clone.turn = (int) (Math.random() * 20);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				if (clone != null){
					clone.reset();
					System.out.println(i);
					try {
						pool.returnObject(clone);
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
	private static void borrowNReturnN(int n) {
		
		ObjectPool<GameState> pool = new GenericObjectPool<GameState>(new GameStateFactory());
		List<GameState> states = new ArrayList<GameState>();
		GameState state = new GameState(HAMap.mapA);
		
		for(int i = 0; i < n; i++){
			GameState clone = null;
			try {
				clone = pool.borrowObject();
				clone.imitate(state);
				clone.turn = (int) (Math.random() * 20);
				states.add(clone);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			System.out.println(i);
		}
		
		int i=0;
		for(GameState s : states){
			s.reset();
			try {
				pool.returnObject(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
			System.out.println(i);
		}
		
	}
	
	
}
