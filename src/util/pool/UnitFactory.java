package util.pool;

import model.Unit;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class UnitFactory extends BasePooledObjectFactory<Unit> {

	int count = 0;
	
	@Override
	public Unit create() throws Exception {
		//count++;
		//if (count % 10000 == 0)
		//	System.out.println(count);
		return new Unit(null, false);
	}

	@Override
	public PooledObject<Unit> wrap(Unit unit) {
		return new DefaultPooledObject<Unit>(unit);
	}
	
	@Override
	public boolean validateObject(PooledObject<Unit> pooledObject){
		return true;
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 * @throws Exception 
	 */
	@Override
	public void passivateObject(PooledObject<Unit> pooledObject) throws Exception {
		pooledObject.getObject().equipment.clear();
	}
	
}
