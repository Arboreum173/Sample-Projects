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

public class PlayButton extends Clickable {
	private STATE state;
	private Game game;
	private BufferedImage button, buttonHovered;
	
	public PlayButton(STATE state, Game game) {
		super(710, 16, ID.PlayButton, game);
		this.state = state;
		this.game = game;
		
		button = FileReader.loadImage("/play_button.png");
		buttonHovered = ImageBrightness.change(button, 0.92f);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != state || Game.transitioning) return;
	        	if(isHovered()) {
	        		AudioPlayer.getSound("click").play();
	        		game.handler.clearGameObjects();
	        		game.loadLevelMap();
	        		game.menu.builderPlayStart();
	    			Game.state = STATE.Game;
	    			game.player.spawn();
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