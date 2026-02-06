package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class BackButton extends Clickable {
	private STATE state;
	private Callable<Void> action;
	private BufferedImage button, buttonHovered;
	
	public BackButton(float x, float y, STATE state, Callable<Void> action, Game game) {
		super(x, y, ID.BackButton, game);
		
		this.action = action;
		this.state = state;
		
		button = FileReader.loadImage("/back_button.png");
		buttonHovered = ImageBrightness.change(this.button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != state || Game.transitioning) return;
	        	if(isHovered()) {
	    			AudioPlayer.getSound("click").play();
	    			try {
	    				action.call();
	    			} catch(Exception e) { e.printStackTrace(); }
	    		}
	        }
	    };
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		g.drawImage(isHovered() ? buttonHovered : button, (int) x, (int) y, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = button.getWidth();
		height = button.getHeight();
	}
}