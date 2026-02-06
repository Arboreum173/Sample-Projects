package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class EraserButton extends Clickable {
	private Game game;
	private BufferedImage button, buttonHovered;
	private float size = 1;
	private boolean selected = false;
	
	public EraserButton(float x, float y, Game game) {
		super(x, y, ID.Eraser, game);
		
		this.game = game;
		
		this.button = Game.ui.getSubimage(0, 0, 29, 32);
		
		buttonHovered = ImageBrightness.change(this.button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != STATE.Builder || Game.transitioning || !isHovered()) return;
	    		
	        	AudioPlayer.getSound("click").play();
	    		game.menu.builderPut = ID.Eraser;
	    		game.menu.builderBlockDirection = BLOCK_DIRECTION.none;
	        }
	    };
	}
	
	public void tick() {
		selected = game.menu.builderPut == ID.Eraser;
		if(selected && size > 0.85) {
			size -= 0.03;
		} else if(!selected && size < 1) {
			size += 0.03;
			if(size > 1) size = 1;
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		t.translate(x + 5 - ((32 * size - 32) / 2), y - ((32 * size - 32) / 2));
		t.scale(size, size);
		g2d.drawImage(isHovered() || selected ? buttonHovered : button, t, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = 42;
		height = 42;
	}
	
	public ID getType() {
		return ID.Eraser;
	}
}