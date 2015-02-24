import java.io.IOException;
import java.util.List;

import model.HaMap;
import model.Position;
import util.CachedLines;
import util.MapLoader;


public class CachedLinesPerformance {

	public static void main(String[] args){
		
		System.out.println("Loading...");
		try {
			CachedLines.load(MapLoader.get("a"));
			System.out.println("Done");
			List<Position> blocked = CachedLines.supercover(new Position(1, 1), new Position(2,3));
			System.out.println(blocked);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
