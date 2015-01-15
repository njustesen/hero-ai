package ai.util;

import game.GameState;
import model.HAMap;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class GameStateFactory extends BasePooledObjectFactory<GameState> {
	
	@Override
	public GameState create() throws Exception {
		
		return new GameState(HAMap.mapA);
	}

	@Override
	public PooledObject<GameState> wrap(GameState gameState) {
		return new DefaultPooledObject<GameState>(gameState);
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 */
	@Override
	public void passivateObject(PooledObject<GameState> pooledObject) {
		pooledObject.getObject().reset();
	}
}
