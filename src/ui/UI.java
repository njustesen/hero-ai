package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;

import lib.ImageLib;
import model.Crystal;
import model.GameObjectType;
import model.GameState;
import model.Position;
import model.Square;
import model.Unit;

public class UI extends JComponent {
	
	JFrame frame;
	Image background;
	private int width;
	private int height;
	private int squareSize = 64;
	private GameState state;
	int bottom;
	
	public UI(GameState state){
		JFrame f = new JFrame();
		width = state.map.width * squareSize + squareSize*2;
		height = state.map.height * squareSize + squareSize*2 + squareSize / 2;
        f.setSize(width, height);
        f.setTitle("Hero Academy");
        f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(this);    
        f.setVisible(true);
        this.state = state;
        this.bottom = squareSize + state.map.height * squareSize + squareSize / 4;
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        try {
			paintBoard(g);
			paintHeader(g);
			paintGameObjects(g);
			paintHand(g);
			paintAP(g);
			paintGo(g);
			paintDoors(g);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

	private void paintDoors(Graphics g) throws IOException {
		
		BufferedImage image = ImageLib.lib.get("door-1");
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		((Graphics2D)g).setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawImage(image, squareSize / 8 , squareSize, null, null);
		g.drawString("" + state.p1Deck.size(), (int) (squareSize / 2.75), squareSize + image.getHeight() - 6);
		
		image = ImageLib.lib.get("door-2");
		g.drawImage(image, (int) (width - image.getWidth() - squareSize / 8), squareSize, null, null);
		g.drawString("" + state.p2Deck.size(), width - image.getWidth() + squareSize / 8, squareSize + image.getHeight() - 6);
		
	}

	private void paintGo(Graphics g) throws IOException {
		
		BufferedImage image = ImageLib.lib.get("go-active");
		if (state.APLeft!=0)
			image = ImageLib.lib.get("go-inactive"); 
		
		g.drawImage(image, squareSize * state.map.width - 24, bottom, null);
		
	}

	private void paintAP(Graphics g) throws IOException {
		
		BufferedImage image = ImageLib.lib.get("ap-" + state.APLeft + "");
		g.drawImage(image, squareSize + squareSize / 4, bottom + (squareSize - image.getHeight()) / 2, null, null);
		
	}

	private void paintHeader(Graphics g) throws IOException {
		
		BufferedImage image = ImageLib.lib.get("header");
		int x = width / 2 - image.getWidth() / 2;
		g.drawImage(image, x, 0, null, null);
		
		BufferedImage bar = ImageLib.lib.get("bar");
		int toBarA = 102;
		g.drawImage(bar, x + toBarA, 9, null, null);
		
		int toBarB = 382;
		g.drawImage(bar, x + toBarB, 9, null, null);
		
	}

	private void paintGameObjects(Graphics g) throws IOException {
		
		for (Position pos : state.objects.keySet()){
			
			BufferedImage image = null;
			
			if (state.objects.get(pos) instanceof Crystal){
				int p = 1;
				if (!((Crystal)state.objects.get(pos)).p1Owner)
					p+=1;
				image = ImageLib.lib.get("crystal-" + p + "");
			} else if (state.objects.get(pos) instanceof Unit){
				int p = 1;
				if (!((Unit)state.objects.get(pos)).p1Owner)
					p+=1;
				image = ImageLib.lib.get(((Unit)state.objects.get(pos)).unitClass.unitType.name().toString().toLowerCase() + "-" + p);
			}
				
			g.drawImage(image, squareSize + pos.x * squareSize + squareSize/2 - image.getWidth()/2 , squareSize + pos.y * squareSize - 18, null, null);
			
		}
		
	}

	private void paintHand(Graphics g) {
		
		int start = (width / 2) - ((6 * squareSize) / 2);
		
		for(int x = 0; x < 6; x++){
			g.setColor(new Color(0, 0, 0, 50));
			g.fillRect(start + x * squareSize, bottom, squareSize, squareSize);
		}
		/*
		for(int x = 0; x < 6; x++){
			g.setColor(new Color(255, 255, 255, 30));
			g.drawRect(start + x * squareSize, bottom, squareSize, squareSize);
		}
		*/
		if (state.p1Turn)
			paintHand(g, start, state.p1Hand, 1);
		else
			paintHand(g, start, state.p2Hand, 2);
		
	}

	private void paintHand(Graphics g, int from, List<GameObjectType> hand, int p) {
		
		for(int i = 0; i < hand.size(); i++){
			
			int x = from + i * squareSize;
			
			BufferedImage image = null;
			switch (hand.get(i)) {
			case Archer: image = ImageLib.lib.get("archer-" + p);break;
			case Cleric: image = ImageLib.lib.get("cleric-" + p);break;
			case Dragonscale: image = ImageLib.lib.get("shield");break;
			case Inferno: image = ImageLib.lib.get("inferno");break;
			case Knight: image = ImageLib.lib.get("knight-" + p);break;
			case Ninja: image = ImageLib.lib.get("ninja-" + p);break;
			case RevivePotion: image = ImageLib.lib.get("potion");break;
			case Runemetal: image = ImageLib.lib.get("sword");break;
			case Scroll: image = ImageLib.lib.get("scroll-" + p);break;
			case ShiningHelm: image = ImageLib.lib.get("helmet-" + p);break;
			case Wizard: image = ImageLib.lib.get("wizard-" + p);break;
			default:
				break;
			}
			
			if (image != null){
				System.out.println(image.toString() + "found.");
				g.drawImage(image, x + squareSize/2 - image.getWidth()/2, bottom, null, null);
			}else{
				System.out.println("Could not find " + hand.get(i).toString());
			}
			
		}
		
	}

	private void paintBoard(Graphics g) throws IOException {
		g.setColor(new Color(170, 185, 68));
        g.fillRect(0, 0, width, height);
        
        for(byte x = 0; x < state.map.width; x++){
        	for(byte y = 0; y < state.map.height; y++){
        		
        		g.setColor(new Color(194, 197, 153));
        		if ((x+y)%2==1)
        			g.setColor(new Color(182, 187, 147));
        		g.fillRect(squareSize + x * squareSize, squareSize + y * squareSize, squareSize, squareSize);
        		
        		BufferedImage image = null;
        			
        		if (state.map.squareAt(x, y) == Square.ASSULT_BOOST)
        			image = ImageLib.lib.get("assult");
        		else if (state.map.squareAt(x, y) == Square.DEFENSE_BOOST)
        			image = ImageLib.lib.get("defense");
        		else if (state.map.squareAt(x, y) == Square.POWER_BOOST)
        			image = ImageLib.lib.get("power");
        		else if (state.map.squareAt(x, y) == Square.P1DEPLOY)
        			image = ImageLib.lib.get("deploy-1");
        		else if (state.map.squareAt(x, y) == Square.P2DEPLOY)
        			image = ImageLib.lib.get("deploy-2");
        		
        		if (image != null)
                	g.drawImage(image, squareSize + x * squareSize, squareSize + y * squareSize, null, null);
            }	
        }
        
        for(int x = 0; x < state.map.width; x++){
        	for(int y = 0; y < state.map.height; y++){
        		g.setColor(new Color(255, 255, 255, 30));
                g.drawRect(squareSize + x * squareSize, squareSize + y * squareSize, squareSize, squareSize);
            }	
        }
	}
	
}
