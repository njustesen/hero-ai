package ai.util;

import game.GameState;
import model.HaMap;
import model.Unit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class GameStateFactory extends BasePooledObjectFactory<GameState> {
	
	public GameStateFactory() {
		super();
	}

	@Override
	public GameState create() throws Exception {
		return new GameState(null);
	}

	@Override
	public PooledObject<GameState> wrap(GameState gameState) {
		return new DefaultPooledObject<GameState>(gameState);
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 * @throws Exception 
	 */
	@Override
	public void passivateObject(PooledObject<GameState> pooledObject) throws Exception {
		pooledObject.getObject().returnUnits();
	}
}
