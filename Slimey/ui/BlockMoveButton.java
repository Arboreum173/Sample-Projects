package com.xenonplatformer.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class BlockMoveButton extends Clickable {
	private Game game;
	private BLOCK_DIRECTION blockDirection;
	private BufferedImage button, buttonActive;
	
	private float size = 1;
	private boolean selected = false;
	private boolean selectable = false;
	private float opacity = 1;
	
	public BlockMoveButton(float x, float y, BLOCK_DIRECTION blockDirection, Game game) {
		super(x, y, ID.BlockMoveButton, game);
		
		this.game = game;
		
		this.blockDirection = blockDirection;
		
		if(blockDirection == BLOCK_DIRECTION.up) { 
			button = Game.ui.getSubimage(73, 0, 32, 32);
		} else if(blockDirection == BLOCK_DIRECTION.right) { 
			button = Game.ui.getSubimage(105, 0, 32, 32);
		} else if(blockDirection == BLOCK_DIRECTION.down) { 
			button = Game.ui.getSubimage(137, 0, 32, 32);
		} else if(blockDirection == BLOCK_DIRECTION.left) { 
			button = Game.ui.getSubimage(169, 0, 32, 32);
		}
		buttonActive = ImageBrightness.change(button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(
	        		Game.state != STATE.Builder ||
	        		Game.transitioning ||
	        		!isHovered() ||
	        		!selectable
	        	) {
	        		return;
	        	}
	        	
	        	AudioPlayer.getSound("click").play();
	        	if(game.menu.builderBlockDirection == blockDirection) {
	        		game.menu.builderBlockDirection = BLOCK_DIRECTION.none;
	        	} else {
	        		game.menu.builderBlockDirection = blockDirection;
	        	}
	        }
	    };
	}
	
	public void tick() {
		selected = game.menu.builderBlockDirection == blockDirection && selectable;
		if(selected && size > 0.85) {
			size -= 0.03;
		} else if(!selected && size < 1) {
			size += 0.03;
			if(size > 1) size = 1;
		}
		
		boolean isStaticBlock = (
			game.menu.builderPut == ID.Player ||
    		game.menu.builderPut == ID.Goal ||
    		game.menu.builderPut == ID.BlueDoor ||
    		game.menu.builderPut == ID.RedDoor ||
    		game.menu.builderPut == ID.GreenDoor ||
    		game.menu.builderPut == ID.YellowDoor ||
    		game.menu.builderPut == ID.BlueButton ||
    		game.menu.builderPut == ID.RedButton ||
    		game.menu.builderPut == ID.GreenButton ||
    		game.menu.builderPut == ID.YellowButton
    	);
		
		boolean isVerticalEnemy = (
			game.menu.builderPut == ID.Enemy &&
			(blockDirection == BLOCK_DIRECTION.up ||
			blockDirection == BLOCK_DIRECTION.down)
		);
		
		selectable = (
			!isStaticBlock &&
			!isVerticalEnemy &&
			game.menu.builderPut != ID.Eraser
		);
		
		if(selectable) {
			if(opacity < 1) {
				opacity += 0.05;
				if(opacity > 1) opacity = 1;
			}
		} else {
			if(opacity > 0.5) {
				opacity -= 0.05;
				if(opacity < 0.5) opacity = 0.5f;
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		
		t.translate(x + 5 - ((32 * size - 32) / 2), y - ((32 * size - 32) / 2));
		t.scale(size, size);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		boolean isActive = selectable && (isHovered() || selected);
		g2d.drawImage(isActive ? buttonActive : button, t, null);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = button.getWidth();
		height = button.getHeight();
	}
}