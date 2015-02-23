package util.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import game.GameState;
import model.Card;
import model.HaMap;
import model.Unit;

public class ObjectPools {

	public static GenericObjectPool<GameState> statePool;
	public static GenericObjectPool<Unit> unitPool;
	public static boolean usePools = false;
	static {
		if (usePools){ 
			unitPool = new GenericObjectPool<Unit>(new UnitFactory());
			statePool = new GenericObjectPool<GameState>(new GameStateFactory());
			statePool.setBlockWhenExhausted(false);
			statePool.setMaxTotal(1000000);
			unitPool.setBlockWhenExhausted(false);
			unitPool.setMaxTotal(10000000);
		}
	}
	
	public static GameState borrowState(HaMap map){
		if (statePool == null)
			return new GameState(map);
		try {
			GameState state = statePool.borrowObject();
			state.map = map;
			if (map != null && state.units.length != map.width)
				state.units = new Unit[map.width][map.height];
			return state;
		} catch (Exception e) {
			//e.printStackTrace();
			return new GameState(null);
		}
	}
	
	public static Unit borrowUnit(Card card, boolean p1){
		if (unitPool == null)
			return new Unit(card, p1);
		try {
			Unit unit = unitPool.borrowObject();
			unit.init(card, p1);
			return unit;
		} catch (Exception e) {
			e.printStackTrace();
			return new Unit(card, p1);
		}
	}
	
	public static void returnUnit(Unit unit) {
		if (ObjectPools.unitPool == null)
			return;
		try {
			unitPool.returnObject(unit);
		} catch (Exception e){
			//e.printStackTrace();
		}
	}

	public static void returnState(GameState state) {
		if (ObjectPools.statePool == null)
			return;
		try {
			statePool.returnObject(state);
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
}
