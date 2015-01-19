import java.util.List;

import model.HAMap;
import model.Position;
import util.CachedLines;


public class CachedLinesPerformance {

	public static void main(String[] args){
		
		System.out.println("Loading...");
		CachedLines.load(HAMap.mapA);
		System.out.println("Done");
		List<Position> blocked = CachedLines.supercover(new Position(1, 1), new Position(2,3));
		System.out.println(blocked);
	}
	
}
