package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Game.STATE;

public class CursorBlock extends GameEntity {
	private Game game;
	private HashMap<ID, BufferedImage> blocks = new HashMap<ID, BufferedImage>();
	public HashMap<BLOCK_DIRECTION, BufferedImage> blockDirectionArrows = new HashMap<BLOCK_DIRECTION, BufferedImage>();
	
	public CursorBlock(Game game) {
		super(0, 0, ID.CursorBlock);
		
		this.game = game;
		
		this.blocks.put(ID.BrickBlock, Game.getSprite(ID.BrickBlock));
		this.blocks.put(ID.SpikeBlock, Game.getSprite(ID.SpikeBlock));
		this.blocks.put(ID.IceBlock, Game.getSprite(ID.IceBlock));
		this.blocks.put(ID.ZeroBlock, Game.getSprite(ID.ZeroBlock));
		this.blocks.put(ID.TrampolineBlock, Game.getSprite(ID.TrampolineBlock));
		this.blocks.put(ID.LadderBlock, Game.getSprite(ID.LadderBlock));
		this.blocks.put(ID.HorizontalLadderBlock, Game.getSprite(ID.HorizontalLadderBlock));
		this.blocks.put(ID.RightConveyorBlock, Game.getSprite(ID.RightConveyorBlock));
		this.blocks.put(ID.LeftConveyorBlock, Game.getSprite(ID.LeftConveyorBlock));
		
		this.blocks.put(ID.BlueDoor, Game.getSprite(ID.BlueDoor));
		this.blocks.put(ID.RedDoor, Game.getSprite(ID.RedDoor));
		this.blocks.put(ID.GreenDoor, Game.getSprite(ID.GreenDoor));
		this.blocks.put(ID.YellowDoor, Game.getSprite(ID.YellowDoor));
		
		this.blocks.put(ID.BlueButton, Game.getSprite(ID.BlueButton));
		this.blocks.put(ID.RedButton, Game.getSprite(ID.RedButton));
		this.blocks.put(ID.GreenButton, Game.getSprite(ID.GreenButton));
		this.blocks.put(ID.YellowButton, Game.getSprite(ID.YellowButton));
		
		this.blocks.put(ID.BreakableBrickBlock, Game.getSprite(ID.BreakableBrickBlock));
		this.blocks.put(ID.BreakableIceBlock, Game.getSprite(ID.BreakableIceBlock));
		this.blocks.put(ID.BreakableZeroBlock, Game.getSprite(ID.BreakableZeroBlock));
		
		this.blocks.put(ID.WaterBlock, Game.getSprite(ID.WaterBlock));
		
		this.blocks.put(ID.StopBlock, Game.getSprite(ID.StopBlock));
		
		this.blocks.put(ID.Goal, Game.getSprite(ID.Goal));
		
		BufferedImage playerBody = Game.spriteSheet.getSubimage(32, 0, 35, 35);
		BufferedImage playerEyes = Game.spriteSheet.getSubimage(67, 0, 22, 10);
		BufferedImage playerPupils = Game.spriteSheet.getSubimage(67, 10, 17, 5);
		
		BufferedImage playerSprite = new BufferedImage(35, 35, BufferedImage.TYPE_INT_ARGB);
		
		Graphics playerG = playerSprite.getGraphics();
		
		playerG.drawImage(playerBody, 0, 0, null);
		playerG.drawImage(playerEyes, 7, 10, null);
		playerG.drawImage(playerPupils, 12, 12, null);
		
		playerG.dispose();
		
		this.blocks.put(ID.Player, playerSprite);
		
		BufferedImage enemyBody = Game.spriteSheet.getSubimage(1, 128, 48, 55);
		BufferedImage enemyEye = Game.spriteSheet.getSubimage(0, 183, 11, 7);
		
		BufferedImage enemySprite = new BufferedImage(48, 55, BufferedImage.TYPE_INT_ARGB);
		
		Graphics enemyG = enemySprite.getGraphics();
		
		enemyG.drawImage(enemyBody, 0, 0, null);
		enemyG.drawImage(enemyEye, 15, 27, null);
		
		enemyG.dispose();
		
		this.blocks.put(ID.Enemy, enemySprite);
		
		this.blockDirectionArrows.put(BLOCK_DIRECTION.up, Game.ui.getSubimage(0, 34, 14, 14));
		this.blockDirectionArrows.put(BLOCK_DIRECTION.right, Game.ui.getSubimage(14, 34, 14, 14));
		this.blockDirectionArrows.put(BLOCK_DIRECTION.down, Game.ui.getSubimage(28, 34, 14, 14));
		this.blockDirectionArrows.put(BLOCK_DIRECTION.left, Game.ui.getSubimage(42, 34, 14, 14));
	}
	
	public void tick() {
		if(Game.state == STATE.Builder) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			
			int mouseX = (int) mousePos.getX() - Game.x;
			int mouseY = (int) mousePos.getY() - Game.y;
			
			x = 32 * (int) Math.floor((game.camera.getX() + mouseX) / 32);
			y = 32 * (int) Math.floor((game.camera.getY() + mouseY) / 32);
			
			if(game.menu.builderPut == ID.Player) {
				x -= 1;
				y -= 2;
			} else if(game.menu.builderPut == ID.Enemy) {
				x -= 8;
				y -= 22;
			}
		}
	}
	
	public void render(Graphics g) {
		if(Game.state == STATE.Builder) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
			
			g2d.drawImage(blocks.get(game.menu.builderPut), (int) x, (int) y, null);
			
			if(game.menu.builderBlockDirection != BLOCK_DIRECTION.none) {
				g2d.drawImage(blockDirectionArrows.get(game.menu.builderBlockDirection), (int) x + 10, (int) y + 10, null);
			}
			
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
		}
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return 32;
	}
	
	public int getHeight() {
		return 32;
	}
	
	public Rectangle getBounds() {
		return null;
	}
}