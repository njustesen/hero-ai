package LineIterators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.GameState;
import lib.Card;
import model.Unit;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import ai.util.UnitFactory;

public class UnitPoolTest {

	public static void main(String[] args){
		
		borrowNReturnN(100);
		
	}

	private static void borrowReturn() {
		
		ObjectPool<Unit> unitPool = new GenericObjectPool<Unit>(new UnitFactory());
		Unit unit = new Unit(Card.ARCHER, true);
		for(int i = 0; i < 100; i++){
			Unit clone = null;
			try {
				clone = unitPool.borrowObject();
				clone.imitate(unit);
				clone.hp = (int) (Math.random() * 20);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			} finally {
				if (clone != null){
					clone.reset();
					System.out.println(i);
					try {
						unitPool.returnObject(clone);
					} catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
	
	private static void borrowNReturnN(int n) {
		
		ObjectPool<Unit> unitPool = new GenericObjectPool<Unit>(new UnitFactory());
		List<Unit> units = new ArrayList<Unit>();
		Unit unit = new Unit(Card.ARCHER, true);
		
		for(int i = 0; i < n; i++){
			Unit clone = null;
			try {
				clone = unitPool.borrowObject();
				clone.imitate(unit);
				clone.hp = (int) (Math.random() * 20);
				units.add(clone);
			} catch (Exception e) {
				System.out.println(e);
				e.printStackTrace();
			}
			System.out.println(i);
		}
		
		int i=0;
		for(Unit u : units){
			u.reset();
			try {
				unitPool.returnObject(u);
			} catch (Exception e) {
				e.printStackTrace();
			}
			i++;
			System.out.println(i);
		}
		
	}
	
	
}
