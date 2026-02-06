package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class StopButton extends Clickable {
	private STATE state, returnState;
	private Game game;
	private BufferedImage button, buttonHovered;
	
	public StopButton(STATE state, Game game, STATE returnState) {
		super(680, 20, ID.StopButton, game);
		
		this.state = state;
		this.returnState = returnState;
		this.game = game;
		
		button = FileReader.loadImage("/stop_button.png");
		buttonHovered = ImageBrightness.change(button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != state || Game.transitioning || !isHovered()) return;
	        	
	        	AudioPlayer.getSound("click").play();
    			if(returnState == STATE.Builder) {
    				game.handler.clearObjects();
            		game.loadLevelMap();
            		game.player.show();
            		Game.state = STATE.Builder;
            		game.menu.builderPlayEnd();
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