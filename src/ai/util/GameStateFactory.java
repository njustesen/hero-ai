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
		pooledObject.getObject().isTerminal = false;
		pooledObject.getObject().p1Turn = true;
		pooledObject.getObject().turn = 0;
		pooledObject.getObject().APLeft = 0;
		pooledObject.getObject().p1Hand.clear();
		pooledObject.getObject().p1Deck.clear();
		pooledObject.getObject().p2Hand.clear();
		pooledObject.getObject().p2Deck.clear();
		pooledObject.getObject().chainTargets.clear();
		for (int x = 0; x < pooledObject.getObject().map.width; x++)
			for (int y = 0; y < pooledObject.getObject().map.height; y++)
				pooledObject.getObject().squares[x][y].unit = null;
	}
}
