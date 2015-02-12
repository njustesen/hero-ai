package ui;

import game.GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import lib.ImageLib;
import lib.UnitClassLib;
import model.AttackType;
import model.Card;
import model.CardType;
import model.Position;
import model.SquareType;
import model.Unit;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class UI extends JComponent {

	public JFrame frame;
	public int width;
	public int height;
	public int squareSize = 64;
	public GameState state;
	public Action lastAction;
	private final int bottom;
	private final InputController inputController;
	public Action action;
	private final boolean humanP1;
	private final boolean humanP2;
	private int turn = 0;

	public UI(GameState state, boolean humanP1, boolean humanP2) {
		frame = new JFrame();
		width = state.map.width * squareSize + squareSize * 2;
		height = state.map.height * squareSize + squareSize * 2 + squareSize;
		frame.setSize(width, height);
		frame.setTitle("Hero AI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setVisible(true);
		this.humanP1 = humanP1;
		this.humanP2 = humanP2;
		inputController = new InputController(humanP1, humanP2, squareSize,
				squareSize, squareSize);
		this.addMouseListener(inputController);
		this.addMouseMotionListener(inputController);
		this.state = state;
		bottom = squareSize + state.map.height * squareSize + squareSize / 4;
	}

	public void resetActions() {
		inputController.reset();
		action = null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (state == null)
			return;
		
		if (state.turn != turn){
			turn = state.turn;
			frame.setTitle("Hero AI - Turn " + turn);
		}
		
		if (state == null) {
			System.out.println("State is NULL");
			return;
		}

		if (inputController != null) {
			inputController.state = state.copy();
			action = inputController.action;
		}

		try {
			paintBoard(g);
			paintHeader(g);
			paintLastUnitAction(g);
			paintGameObjects(g);
			paintHP(g);
			paintInfo(g);
			paintHand(g);
			paintAP(g);
			paintGo(g);
			paintDoors(g);
			paintLastSwapCardAction(g);
			paintLastDropAction(g);
			paintWinScreen(g);
			paintUnitDetails(g);
			paintDeck(g);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	private void paintDeck(Graphics g) {

		// Player 1
		if (inputController.mouseX >= (squareSize / 8)
				&& inputController.mouseX <= (squareSize / 8) + 45
				&& inputController.mouseY >= (squareSize)
				&& inputController.mouseY <= (squareSize) + 60) {

			g.setColor(new Color(0, 0, 0, 150));
			int yyy = 0;
			if (state.cardsLeft(1) > 21)
				yyy = 1;
			if (state.cardsLeft(1) <= 14)
				yyy = -1;
			if (state.cardsLeft(1) <= 7)
				yyy = -2;
			if (state.cardsLeft(1) > 0)
				g.fillRect(squareSize + squareSize / 2, squareSize + squareSize
						/ 2 - squareSize * yyy / 2, width - squareSize * 3,
						(int) (height - squareSize * 3.5 + squareSize * yyy));

			showDeckAndHand(g, 1, yyy);

		}

		// Player 2
		if (inputController.mouseX >= (width - squareSize + (squareSize / 8))
				&& inputController.mouseX <= (width - squareSize + (squareSize / 8)) + 45
				&& inputController.mouseY >= (squareSize)
				&& inputController.mouseY <= (squareSize) + 60) {

			g.setColor(new Color(0, 0, 0, 150));
			int yyy = 0;
			if (state.cardsLeft(2) > 21)
				yyy = 1;
			if (state.cardsLeft(2) <= 14)
				yyy = -1;
			if (state.cardsLeft(2) <= 7)
				yyy = -2;
			if (state.cardsLeft(2) > 0)
				g.fillRect(squareSize + squareSize / 2, squareSize + squareSize
						/ 2 - squareSize * yyy / 2, width - squareSize * 3,
						(int) (height - squareSize * 3.5 + squareSize * yyy));

			showDeckAndHand(g, 2, yyy);

		}

	}

	private void showDeckAndHand(Graphics g, int p, int yless) {
		final int x = squareSize * 2;
		final int y = squareSize * 2 - yless * squareSize / 2;
		final int space = squareSize;
		int col = 0;
		int row = 0;
		final List<Card> cards = new ArrayList<Card>();
		if (p == 1) {
			cards.addAll(state.p1Deck);
			cards.addAll(state.p1Hand);
		} else if (p == 2) {
			cards.addAll(state.p2Deck);
			cards.addAll(state.p2Hand);
		} else {
			return;
		}
		Collections.sort(cards);
		for (final Card card : cards) {
			final BufferedImage image = getImage(card, p);
			int xx = x + (space * col);
			int yy = y + (space * row);
			if (xx > (width - x) - space) {
				row++;
				col = 0;
				xx = x + (space * col);
				yy = y + (space * row);
			}
			g.drawImage(image, xx, yy, null);
			col++;
		}
	}

	private void paintUnitDetails(Graphics g) {

		if (inputController.activeSquare != null
				&& state.unitAt(inputController.activeSquare) != null) {

			final Position pos = inputController.activeSquare;
			Unit selected = state.unitAt(pos);
			if (selected.unitClass.card == Card.CRYSTAL)
				return;

			int startX = 2;
			int startY = 154;
			final int stepY = 13;

			if (!selected.p1Owner)
				startX += (state.map.width + 1) * squareSize;

			g.setColor(new Color(0, 0, 0, 60));
			g.fillRect(startX, startY, 60, 230);

			startX += 2;
			startY += 2 + stepY;

			g.setFont(new Font("Arial", Font.BOLD, 12));

			g.setColor(Color.black);
			g.drawString("HP", startX, startY);
			startY += stepY;

			g.setColor(Color.green);
			g.drawString(selected.hp + "/" + selected.maxHP(), startX, startY);
			startY += stepY * 2;

			g.setColor(Color.black);
			g.drawString("Phy. res.", startX, startY);
			startY += stepY;

			g.setColor(Color.white);
			g.drawString(
					"" + selected.resistance(state, pos, AttackType.Physical),
					startX, startY);
			startY += stepY * 2;

			g.setColor(Color.black);
			g.drawString("Mag. res.", startX, startY);
			startY += stepY;

			g.setColor(Color.pink);
			g.drawString(
					"" + selected.resistance(state, pos, AttackType.Magical),
					startX, startY);
			startY += stepY * 2;

			g.setColor(Color.black);
			g.drawString("Power", startX, startY);
			startY += stepY;

			g.setColor(Color.red);
			g.drawString("" + selected.power(state, pos), startX, startY);
			startY += stepY * 2;

			g.setColor(Color.black);
			g.drawString("Speed", startX, startY);
			startY += stepY;

			g.setColor(Color.blue);
			g.drawString("" + selected.unitClass.speed, startX, startY);
			startY += stepY * 2;

			g.setColor(Color.black);
			g.drawString("Range", startX, startY);
			startY += stepY;

			g.setColor(Color.yellow);
			g.drawString("" + selected.unitClass.attack.range, startX, startY);
			startY += stepY * 2;

		}

	}

	private void paintWinScreen(Graphics g) {
		boolean draw = false;
		if (state.isTerminal) {
			if (state.getWinner() == 1) {
				g.setColor(Color.red);
			} else if (state.getWinner() == 2) {
				g.setColor(Color.blue);
			} else {
				g.setColor(Color.white);
				draw = true;
			}
			g.setFont(new Font("Arial", Font.BOLD, 50));
		
			if (!draw)
				g.drawString("PLAYER " + state.getWinner() + " WON!", 155, 200);
			else 
				g.drawString("DRAW!", 265, 200);
		}

	}

	private void paintLastDropAction(Graphics g) {

		if (!(lastAction instanceof DropAction))
			return;

		final DropAction da = ((DropAction) lastAction);

		if (da.type == Card.INFERNO) {

			for (int y = -1; y <= 1; y++) {
				for (int x = -1; x <= 1; x++) {
					paintInferno(g, da.to.x + x, da.to.y + y);
				}
			}

		} else {

			if (da.type.type == CardType.UNIT)
				return;

			BufferedImage image = null;
			int p = 1;
			if (!state.p1Turn)
				p = 2;

			switch (da.type) {
			case DRAGONSCALE:
				image = ImageLib.lib.get("shield");
				break;
			case REVIVE_POTION:
				image = ImageLib.lib.get("potion");
				break;
			case RUNEMETAL:
				image = ImageLib.lib.get("sword");
				break;
			case SCROLL:
				image = ImageLib.lib.get("scroll-" + p);
				break;
			case SHINING_HELM:
				image = ImageLib.lib.get("helmet-" + p);
				break;
			default:
				break;
			}

			if (image != null) {
				g.drawImage(image, squareSize + da.to.x * squareSize
						+ squareSize / 2 - image.getWidth() / 2, squareSize
						+ da.to.y * squareSize - 18, null, null);
			} else {
				System.out.println("DROP: Could not find " + da.type.name());
			}

		}

	}

	private void paintInferno(Graphics g, int x, int y) {

		if (x < 0 || x >= state.map.width || y < 0 || y >= state.map.height)
			return;

		final BufferedImage image = ImageLib.lib.get("inferno");

		g.drawImage(image,
				squareSize + x * squareSize + squareSize / 2 - image.getWidth()
						/ 2, squareSize + y * squareSize - 18, null, null);

	}

	private void paintLastSwapCardAction(Graphics g) {

		if (!(lastAction instanceof SwapCardAction))
			return;

		g.setColor(Color.GREEN);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		int p = 1;
		if (!state.p1Turn)
			p += 1;

		BufferedImage imageUnit = null;
		switch (((SwapCardAction) lastAction).card) {
		case ARCHER:
			imageUnit = ImageLib.lib.get("archer-" + p);
			break;
		case CLERIC:
			imageUnit = ImageLib.lib.get("cleric-" + p);
			break;
		case DRAGONSCALE:
			imageUnit = ImageLib.lib.get("shield");
			break;
		case INFERNO:
			imageUnit = ImageLib.lib.get("inferno");
			break;
		case KNIGHT:
			imageUnit = ImageLib.lib.get("knight-" + p);
			break;
		case NINJA:
			imageUnit = ImageLib.lib.get("ninja-" + p);
			break;
		case REVIVE_POTION:
			imageUnit = ImageLib.lib.get("potion");
			break;
		case RUNEMETAL:
			imageUnit = ImageLib.lib.get("sword");
			break;
		case SCROLL:
			imageUnit = ImageLib.lib.get("scroll-" + p);
			break;
		case SHINING_HELM:
			imageUnit = ImageLib.lib.get("helmet-" + p);
			break;
		case WIZARD:
			imageUnit = ImageLib.lib.get("wizard-" + p);
			break;
		default:
			break;
		}

		if (imageUnit == null)
			System.out.println("SWAP: could not find image "
					+ ((SwapCardAction) lastAction).card.name());
		else if (p == 2)
			g.drawImage(imageUnit, width - imageUnit.getWidth() - squareSize
					/ 8, squareSize, null, null);
		else
			g.drawImage(imageUnit, squareSize / 8, squareSize, null, null);
	}

	private void paintLastUnitAction(Graphics g) {

		if (lastAction == null || lastAction instanceof EndTurnAction)
			return;

		if (lastAction instanceof UnitAction) {
			final Position from = ((UnitAction) lastAction).from;
			final Position to = ((UnitAction) lastAction).to;
			final int ovalW = 48;
			final int ovalH = 48;
			final int rectW = 48;
			final int rectH = 48;
			if (((UnitAction) lastAction).type == UnitActionType.HEAL)
				g.setColor(new Color(0, 255, 0, 100));
			if (((UnitAction) lastAction).type == UnitActionType.ATTACK)
				g.setColor(new Color(255, 0, 0, 100));
			if (((UnitAction) lastAction).type == UnitActionType.MOVE)
				g.setColor(new Color(0, 0, 255, 100));
			if (((UnitAction) lastAction).type == UnitActionType.SWAP)
				g.setColor(new Color(255, 0, 255, 100));
			((Graphics2D) g).setStroke(new BasicStroke(4));
			g.drawLine(squareSize + squareSize * from.x + squareSize / 2,
					squareSize + squareSize * from.y + squareSize / 2,
					squareSize + squareSize * to.x + squareSize / 2, squareSize
							+ squareSize * to.y + squareSize / 2);
			g.fillOval(squareSize + squareSize * from.x + squareSize / 2
					- ovalW / 2, squareSize + squareSize * from.y + squareSize
					/ 2 - ovalH / 2, ovalW, ovalH);
			g.fillRect(squareSize + squareSize * to.x + squareSize / 2 - rectW
					/ 2, squareSize + squareSize * to.y + squareSize / 2
					- rectH / 2, rectW, rectH);

			Position lastPos = to;
			for (final Position pos : state.chainTargets) {
				g.drawLine(
						squareSize + squareSize * lastPos.x + squareSize / 2,
						squareSize + squareSize * lastPos.y + squareSize / 2,
						squareSize + squareSize * pos.x + squareSize / 2,
						squareSize + squareSize * pos.y + squareSize / 2);
				g.fillRect(squareSize + squareSize * pos.x + squareSize / 2
						- rectW / 2, squareSize + squareSize * pos.y
						+ squareSize / 2 - rectH / 2, rectW, rectH);
				lastPos = pos;
			}

		}

	}

	private void paintHP(Graphics g) {

		// Crystal hp bars
		for (int p = 1; p <= 2; p++) {
			double hp = 0;
			final double maxHP = state.map.p1Crystals.size()
					* UnitClassLib.lib.get(Card.CRYSTAL).maxHP;
			if (p == 1) {
				for (final Position pos : state.map.p1Crystals) {
					if (state.units[pos.x][pos.y] == null
							|| state.units[pos.x][pos.y].unitClass.card != Card.CRYSTAL)
						continue;
					hp += state.units[pos.x][pos.y].hp;
				}
			} else if (p == 2) {
				for (final Position pos : state.map.p2Crystals) {
					if (state.units[pos.x][pos.y] == null
							|| state.units[pos.x][pos.y].unitClass.card != Card.CRYSTAL)
						continue;
					hp += state.units[pos.x][pos.y].hp;
				}
			}
			final int w = 158;
			final int h = 15;
			int xx = 132;
			final int border = 2;
			final double per = (hp / maxHP);
			if (p == 2)
				xx = 411 + (int) ((w - border * 2) - ((w - border * 2) * per));
			final int yy = 8;
			// g.setColor(new Color(10,10,10));
			// g.fillRect(xx, yy, w, h);
			g.setColor(new Color(50, 225, 50));
			g.fillRect(xx + border, yy + border,
					(int) ((w - border * 2) * per), h - border * 2);

			g.setColor(new Color(150, 255, 150));
			g.fillRect(xx + border, yy + border,
					(int) ((w - border * 2) * per), (h - border * 2) / 4);
			g.setColor(new Color(20, 155, 20));
			g.fillRect(xx + border, yy + h - border * 2,
					(int) ((w - border * 2) * per), (h - border * 2) / 4);

		}

		// Units
		for (int x = 0; x < state.map.width; x++) {
			for (int y = 0; y < state.map.height; y++) {
				if (state.units[x][y] == null)
					continue;
				final double hp = state.units[x][y].hp;
				final double maxHP = state.units[x][y].maxHP();

				// if (maxHP > 0){
				final int w = (int) (squareSize * 0.8);
				final int h = 6;
				int xx = squareSize + x * squareSize;
				xx += (squareSize - w) / 2;
				final int yy = squareSize + y * squareSize - 16;
				g.setColor(new Color(50, 50, 50));
				g.fillRect(xx, yy, w, h);
				g.setColor(new Color(50, 225, 50));
				final double p = (hp / maxHP);
				g.fillRect(xx + 1, yy + 1, (int) ((w - 2) * p), h - 2);
				g.setColor(new Color(20, 155, 20));
				g.fillRect(xx + 1, yy + 4, (int) ((w - 2) * p), 1);
				g.setColor(new Color(150, 255, 150));
				g.fillRect(xx + 1, yy + 1, (int) ((w - 2) * p), 1);
				// }

			}
		}
	}

	private void paintInfo(Graphics g) {

		for (int x = 0; x < state.map.width; x++) {
			for (int y = 0; y < state.map.height; y++) {
				if (state.units[x][y] == null)
					continue;

				g.setFont(new Font("Arial", Font.PLAIN, 11));

				g.setColor(new Color(50, 255, 50));
				g.drawString(state.units[x][y].hp + "/"
						+ state.units[x][y].maxHP(), squareSize
						+ squareSize * x + squareSize / 8, squareSize
						+ squareSize * y - (int) (squareSize / 3.75));

				if (state.units[x][y] == null)
					continue;

				g.setColor(new Color(50, 100, 50));
				g.drawString(state.units[x][y].hp + "/"
						+ state.units[x][y].maxHP(), squareSize
						+ squareSize * x + squareSize / 8 - 1, squareSize
						+ squareSize * y - (int) (squareSize / 3.75));

				if (state.units[x][y] == null)
					continue;

				if (state.units[x][y].power(state, new Position(x, y)) > 0) {
					g.setFont(new Font("Arial", Font.BOLD, 11));
					g.setColor(new Color(50, 100, 50));
					g.drawString(
							state.units[x][y].power(state, new Position(
									x, y))
									+ "", squareSize + squareSize * x
									+ squareSize / 3, squareSize + squareSize
									* y - 1);
					g.setColor(new Color(255, 25, 25));
					g.drawString(
							state.units[x][y].power(state, new Position(
									x, y))
									+ "", squareSize + squareSize * x
									+ squareSize / 3, squareSize + squareSize
									* y);
				}

			}
		}
	}

	private void paintDoors(Graphics g) throws IOException {

		BufferedImage image = ImageLib.lib.get("door-1");
		g.setColor(Color.black);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawImage(image, squareSize / 8, squareSize, null, null);
		g.drawString("" + state.cardsLeft(1), (int) (squareSize / 2.75),
				squareSize + image.getHeight() - 6);

		image = ImageLib.lib.get("door-2");
		g.drawImage(image, width - image.getWidth() - squareSize / 8,
				squareSize, null, null);

		g.drawString("" + state.cardsLeft(2), width - image.getWidth()
				+ squareSize / 8, squareSize + image.getHeight() - 6);

	}

	private void paintGo(Graphics g) throws IOException {

		BufferedImage image = ImageLib.lib.get("go-active");
		if (state.APLeft != 0)
			image = ImageLib.lib.get("go-inactive");

		g.drawImage(image, squareSize * state.map.width - 24, bottom, null);

	}

	private void paintAP(Graphics g) throws IOException {

		final BufferedImage image = ImageLib.lib.get("ap-" + state.APLeft + "");
		if (image == null)
			System.out.println(state.APLeft);
		g.drawImage(image, squareSize + squareSize / 4, bottom
				+ (squareSize - image.getHeight()) / 2, null, null);

	}

	private void paintHeader(Graphics g) throws IOException {

		final BufferedImage image = ImageLib.lib.get("header");
		final int x = width / 2 - image.getWidth() / 2;
		g.drawImage(image, x, 0, null, null);
		/*
		 * BufferedImage bar = ImageLib.lib.get("bar"); int toBarA = 102;
		 * g.drawImage(bar, x + toBarA, 9, null, null);
		 * 
		 * int toBarB = 382; g.drawImage(bar, x + toBarB, 9, null, null);
		 */
	}

	private void paintGameObjects(Graphics g) throws IOException {

		for (int x = 0; x < state.map.squares.length; x++) {
			for (int y = 0; y < state.map.squares[0].length; y++) {
				if (state.units[x][y] == null)
					continue;

				if (state.units[x][y].hp <= 0
						&& state.units[x][y].unitClass.card == Card.CRYSTAL)
					continue;

				BufferedImage image = null;

				String red = "";
				if (state.units[x][y].hp <= 0)
					red = "-red";

				int p = 1;
				if (!state.units[x][y].p1Owner)
					p += 1;
				final String name = state.units[x][y].unitClass.card
						.name().toString().toLowerCase()
						+ red + "-" + p;
				image = ImageLib.lib.get(name);

				if (image == null)
					System.out.println(state.units[x][y].unitClass.card
							.name());
				else
					g.drawImage(image, squareSize + x * squareSize + squareSize
							/ 2 - image.getWidth() / 2, squareSize + y
							* squareSize - 18, null, null);

				image = null;
				int i = 0;
				for (final Card card : state.units[x][y].equipment) {
					i++;
					switch (card) {
					case DRAGONSCALE:
						image = ImageLib.lib.get("shield-small");
						break;
					case RUNEMETAL:
						image = ImageLib.lib.get("sword-small");
						break;
					case SHINING_HELM:
						image = ImageLib.lib.get("helmet-small-" + p);
						break;
					case SCROLL:
						image = ImageLib.lib.get("scroll-small-" + p);
						break;
					default:
						break;
					}
					if (image == null)
						System.out.println(card.name());
					else if (p == 1)
						g.drawImage(
								image,
								squareSize + x * squareSize + squareSize
										- image.getWidth() / 4 * 3,
								squareSize + y * squareSize
										+ (image.getHeight() / 3 * 2) * (i - 2),
								null, null);
					else
						g.drawImage(
								image,
								squareSize + x * squareSize - image.getWidth()
										/ 5,
								squareSize + y * squareSize
										+ (image.getHeight() / 3 * 2) * (i - 2),
								null, null);

				}
			}
		}
	}

	private void paintHand(Graphics g) {

		final int start = (width / 2) - ((6 * squareSize) / 2);

		for (int x = 0; x < 6; x++) {
			g.setColor(new Color(140, 155, 48));
			g.fillRect(start + x * squareSize, bottom, squareSize, squareSize);
		}
		/*
		 * for(int x = 0; x < 6; x++){ g.setColor(new Color(255, 255, 255, 30));
		 * g.drawRect(start + x * squareSize, bottom, squareSize, squareSize); }
		 */
		if (state.p1Turn && (humanP1 || (!humanP1 && !humanP2)))
			paintHand(g, start, state.p1Hand, 1);
		else if (!state.p1Turn && (humanP2 || (!humanP1 && !humanP2)))
			paintHand(g, start, state.p2Hand, 2);

	}

	private void paintHand(Graphics g, int from, List<Card> hand, int p) {

		for (int i = 0; i < hand.size(); i++) {

			final int x = from + i * squareSize;

			final BufferedImage image = getImage(hand.get(i), p);

			if (image != null) {
				int b = bottom;
				if (inputController.activeCardIdx == i)
					b -= squareSize / 4;
				g.drawImage(image, x + squareSize / 2 - image.getWidth() / 2,
						b, null, null);
			} else {
				System.out.println("HAND: Could not find "
						+ hand.get(i).toString());
			}

		}

	}

	private BufferedImage getImage(Card card, int p) {

		switch (card) {
		case ARCHER:
			return ImageLib.lib.get("archer-" + p);
		case CLERIC:
			return ImageLib.lib.get("cleric-" + p);
		case DRAGONSCALE:
			return ImageLib.lib.get("shield");
		case INFERNO:
			return ImageLib.lib.get("inferno");
		case KNIGHT:
			return ImageLib.lib.get("knight-" + p);
		case NINJA:
			return ImageLib.lib.get("ninja-" + p);
		case REVIVE_POTION:
			return ImageLib.lib.get("potion");
		case RUNEMETAL:
			return ImageLib.lib.get("sword");
		case SCROLL:
			return ImageLib.lib.get("scroll-" + p);
		case SHINING_HELM:
			return ImageLib.lib.get("helmet-" + p);
		case WIZARD:
			return ImageLib.lib.get("wizard-" + p);
		default:
			break;
		}

		return null;
	}

	private void paintBoard(Graphics g) throws IOException {
		g.setColor(new Color(170, 185, 68));
		g.fillRect(0, 0, width, height);

		for (int x = 0; x < state.map.width; x++) {
			for (int y = 0; y < state.map.height; y++) {

				final Position pos = new Position(x, y);

				if (inputController.activeSquare != null
						&& inputController.activeSquare.equals(pos))
					g.setColor(new Color(215, 215, 215));
				else {
					boolean found = false;
					for (final Action action : inputController.possibleActions) {
						if (action instanceof UnitAction) {
							if (((UnitAction) action).to.equals(pos)) {
								if (((UnitAction) action).type == UnitActionType.ATTACK) {
									g.setColor(new Color(255, 50, 50));
								} else if (((UnitAction) action).type == UnitActionType.HEAL) {
									g.setColor(new Color(50, 255, 50));
								} else if (((UnitAction) action).type == UnitActionType.MOVE) {
									g.setColor(new Color(50, 50, 255));
								} else if (((UnitAction) action).type == UnitActionType.SWAP) {
									g.setColor(new Color(240, 240, 240));
								}
								found = true;
								break;
							}
						} else if (action instanceof DropAction) {
							if (((DropAction) action).to.equals(pos)) {
								if (((DropAction) action).type.type == CardType.UNIT) {
									g.setColor(new Color(50, 50, 255));
								} else if (((DropAction) action).type.type == CardType.SPELL) {
									g.setColor(new Color(255, 50, 50));
								} else if (((DropAction) action).type.type == CardType.ITEM) {
									g.setColor(new Color(50, 255, 50));
								}
								found = true;
								break;
							}
						}
					}
					if (!found) {
						if ((x + y) % 2 == 1)
							g.setColor(new Color(182, 187, 147));
						else
							g.setColor(new Color(194, 197, 153));
					}
				}

				g.fillRect(squareSize + x * squareSize, squareSize + y
						* squareSize, squareSize, squareSize);

				BufferedImage image = null;

				if (state.map.squareAt(x, y) == SquareType.ASSAULT)
					image = ImageLib.lib.get("assault");
				else if (state.map.squareAt(x, y) == SquareType.DEFENSE)
					image = ImageLib.lib.get("defense");
				else if (state.map.squareAt(x, y) == SquareType.POWER)
					image = ImageLib.lib.get("power");
				else if (state.map.squareAt(x, y) == SquareType.DEPLOY_1)
					image = ImageLib.lib.get("deploy-1");
				else if (state.map.squareAt(x, y) == SquareType.DEPLOY_2)
					image = ImageLib.lib.get("deploy-2");

				if (image != null)
					g.drawImage(image, squareSize + x * squareSize, squareSize
							+ y * squareSize, null, null);
			}
		}

		for (int x = 0; x < state.map.width; x++) {
			for (int y = 0; y < state.map.height; y++) {
				g.setColor(new Color(155, 155, 155));
				g.drawRect(squareSize + x * squareSize, squareSize + y
						* squareSize, squareSize, squareSize);
			}
		}
	}

}
