package ui;

import game.GameState;
import gameobjects.Crystal;
import gameobjects.GameObjectType;
import gameobjects.Position;
import gameobjects.Square;
import gameobjects.Unit;

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

public class UI extends JComponent {
	
	public JFrame frame;
	public int width;
	public int height;
	public int squareSize = 64;
	public GameState state;
	int bottom;
	
	public UI(GameState state){
		frame = new JFrame();
		width = state.map.width * squareSize + squareSize*2;
		height = state.map.height * squareSize + squareSize*2 + squareSize / 2;
		frame.setSize(width, height);
		frame.setTitle("Hero Academy");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);    
		frame.setVisible(true);
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
			paintHP(g);
			paintInfo(g);
			paintHand(g);
			paintAP(g);
			paintGo(g);
			paintDoors(g);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
    }

	private void paintHP(Graphics g) {
		
		for (Position pos : state.objects.keySet()){
			
			double hp = -1;
			double maxHP = -1;
			
			if (state.objects.get(pos) instanceof Crystal){
				hp = ((Crystal)state.objects.get(pos)).hp;
				maxHP = Crystal.STANDARD_HP;				
			} else if (state.objects.get(pos) instanceof Unit){
				hp = ((Unit)state.objects.get(pos)).hp;
				maxHP = state.maxHP(((Unit)state.objects.get(pos)));
			}
				
			//if (maxHP > 0){
				int w = (int) (squareSize * 0.8);
				int h = 6;
				int x = squareSize + pos.x * squareSize;
				x += (squareSize - w) / 2;
				int y = squareSize + pos.y * squareSize - 16;
				g.setColor(new Color(50,50,50));
				g.fillRect(x, y, w, h);
				g.setColor(new Color(50,255,50));
				double p = (hp/maxHP);
				g.fillRect(x+1, y+1, (int) ((w-2)*p), h-2);
			//}
			
		}
	}
	
	private void paintInfo(Graphics g) {
		
		for (Position pos : state.objects.keySet()){
			
			if (state.objects.get(pos) instanceof Unit){
				g.setColor(new Color(50,255,50));
				g.setFont(new Font("Arial", Font.PLAIN, 11));
				Unit unit = (Unit)state.objects.get(pos);
				g.drawString(unit.hp + "/" + state.maxHP(unit), 
						squareSize + squareSize * pos.x + squareSize/8, 
						squareSize + squareSize * pos.y - (int)(squareSize/3.75));
				g.setColor(new Color(255,15,25));
				g.setFont(new Font("Arial", Font.BOLD, 11));
				g.drawString(state.power(unit, pos) + "", 
						squareSize + squareSize * pos.x + squareSize/8, 
						squareSize + squareSize * pos.y);
				if (unit.hp < 0){
					g.setColor(new Color(255,50,50));
					g.fillRect(	squareSize + squareSize * pos.x, 
								squareSize + squareSize * pos.y, 
								squareSize, squareSize);
				}
			}
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
		if (image == null)
			System.out.println(state.APLeft);
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
				String name = ((Unit)state.objects.get(pos)).unitClass.unitType.name().toString().toLowerCase() + "-" + p;
				image = ImageLib.lib.get(name);
			}
			
			if (image == null)
				System.out.println(state.objects.get(pos));
			else
				g.drawImage(image, squareSize + pos.x * squareSize + squareSize/2 - image.getWidth()/2 , squareSize + pos.y * squareSize - 18, null, null);
			
		}
		
	}

	private void paintHand(Graphics g) {
		
		int start = (width / 2) - ((6 * squareSize) / 2);
		
		for(int x = 0; x < 6; x++){
			g.setColor(new Color(140, 155, 48));
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
        			
        		if (state.map.squareAt(x, y) == Square.ASSAULT_BOOST)
        			image = ImageLib.lib.get("assault");
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
        		g.setColor(new Color(155, 155, 155));
                g.drawRect(squareSize + x * squareSize, squareSize + y * squareSize, squareSize, squareSize);
            }	
        }
	}
	
}
