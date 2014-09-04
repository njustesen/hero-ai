package lib;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageLib {

	public static Map<String, BufferedImage> lib;
	static {
		lib = new HashMap<String, BufferedImage>();
		try {
			lib.put("crystal-1", 	ImageIO.read(new File("img/crystal-1.png")));
			lib.put("crystal-2", 	ImageIO.read(new File("img/crystal-2.png")));
			lib.put("door-1", 		ImageIO.read(new File("img/door-1.png")));
			lib.put("door-2", 		ImageIO.read(new File("img/door-2.png")));
			lib.put("go-active", 	ImageIO.read(new File("img/go-active.png")));
			lib.put("go-inactive", 	ImageIO.read(new File("img/go-inactive.png")));
			lib.put("ap-0", 		ImageIO.read(new File("img/ap-0.png")));
			lib.put("ap-1", 		ImageIO.read(new File("img/ap-1.png")));
			lib.put("ap-2", 		ImageIO.read(new File("img/ap-2.png")));
			lib.put("ap-3", 		ImageIO.read(new File("img/ap-3.png")));
			lib.put("ap-4", 		ImageIO.read(new File("img/ap-4.png")));
			lib.put("ap-5", 		ImageIO.read(new File("img/ap-5.png")));
			lib.put("header", 		ImageIO.read(new File("img/header.png")));
			lib.put("bar", 			ImageIO.read(new File("img/bar.png")));
			lib.put("assult", 		ImageIO.read(new File("img/assult.png")));
			lib.put("defense",		ImageIO.read(new File("img/defense.png")));
			lib.put("power", 		ImageIO.read(new File("img/power.png")));
			lib.put("deploy-1", 	ImageIO.read(new File("img/deploy-1.png")));
			lib.put("deploy-2", 	ImageIO.read(new File("img/deploy-2.png")));
			lib.put("header", 		ImageIO.read(new File("img/header.png")));
			lib.put("bar", 			ImageIO.read(new File("img/bar.png")));
			
			lib.put("knight-1", 	ImageIO.read(new File("img/knight-1.png")));
			lib.put("archer-1", 	ImageIO.read(new File("img/archer-1.png")));
			lib.put("cleric-1", 	ImageIO.read(new File("img/cleric-1.png")));
			lib.put("wizard-1", 	ImageIO.read(new File("img/wizard-1.png")));
			lib.put("ninja-1", 		ImageIO.read(new File("img/ninja-1.png")));
			
			lib.put("knight-2", 	ImageIO.read(new File("img/knight-2.png")));
			lib.put("archer-2", 	ImageIO.read(new File("img/archer-2.png")));
			lib.put("cleric-2", 	ImageIO.read(new File("img/cleric-2.png")));
			lib.put("wizard-2", 	ImageIO.read(new File("img/wizard-2.png")));
			lib.put("ninja-2", 		ImageIO.read(new File("img/ninja-2.png")));
			
			lib.put("scroll-1", 	ImageIO.read(new File("img/scroll-1.png")));
			lib.put("helmet-1", 	ImageIO.read(new File("img/helmet-1.png")));
			lib.put("scroll-2", 	ImageIO.read(new File("img/scroll-2.png")));
			lib.put("helmet-2", 	ImageIO.read(new File("img/helmet-2.png")));
			
			lib.put("sword", 		ImageIO.read(new File("img/sword.png")));
			lib.put("shield", 		ImageIO.read(new File("img/shield.png")));
			lib.put("potion", 		ImageIO.read(new File("img/potion.png")));
			lib.put("inferno", 		ImageIO.read(new File("img/inferno.png")));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
