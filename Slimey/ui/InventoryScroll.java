package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class InventoryScroll extends Clickable {
	private Game game;
	private BufferedImage button, buttonHovered;
	private boolean right;
	
	public InventoryScroll(float x, float y, boolean right, Game game) {
		super(x, y, ID.InventoryScroll, game);
		this.right = right;
		this.game = game;
		
		if(right) {
			this.button = Game.ui.getSubimage(29, 0, 22, 32);
		} else {
			this.button = Game.ui.getSubimage(51, 0, 22, 32);
		}
		buttonHovered = ImageBrightness.change(button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != STATE.Builder || Game.transitioning) return;
	        	if(isHovered()) {
	        		AudioPlayer.getSound("click").play();
	        		game.menu.inventory.scroll(right ? 10 : -10);
	    		}
	        }
	    };
	}
	
	protected void setDimensions() {
		width = button.getWidth();
		height = button.getHeight();
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		g.drawImage(isHovered() ? buttonHovered : button, (int) x, (int) y, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
}