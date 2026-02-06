package com.xenonplatformer.ui;

import java.awt.Color;
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
import com.xenonplatformer.window.Label;

public class LevelItem extends Clickable {
	private int levelNum;
	private STATE state;
	private Game game;
	private Label label;
	private BufferedImage button, buttonHovered;
	
	public LevelItem(float x, float y, String text, int levelNum, STATE state, Game game) {
		super(x, y, ID.LevelItem, game);
		
		this.levelNum = levelNum;
		this.state = state;
		this.game = game;
		
		label = new Label(true, "Level " + levelNum, (int) x + 40, (int) y + 55, Color.WHITE, true, false);
		
		button = FileReader.loadImage("/level_item.png");
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
	    				game.transition.show(STATE.Game, new Callable<Void>() {
	    					public Void call() {
	    						game.handler.clearObjects();
	    						game.loadCampaignLevel(levelNum);
	    						return null;
	    					}
	    				});
	    			} catch(Exception e2) { e2.printStackTrace(); }
	    		}
	        }
	    };
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		g.drawImage(isHovered() ? buttonHovered : button, (int) x, (int) y, null);
		label.render(g, (int) x + 40, (int) y + 55, Color.WHITE);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = button.getWidth();
		height = button.getHeight();
	}
}