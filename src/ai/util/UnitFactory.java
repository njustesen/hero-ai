package ai.util;

import model.Unit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class UnitFactory extends BasePooledObjectFactory<Unit> {

	@Override
	public Unit create() throws Exception {
		return new Unit(null, false);
	}

	@Override
	public PooledObject<Unit> wrap(Unit unit) {
		return new DefaultPooledObject<Unit>(unit);
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 * @throws Exception 
	 */
	@Override
	public void passivateObject(PooledObject<Unit> pooledObject) throws Exception {
		//pooledObject.getObject().reset();
	}
	
}
