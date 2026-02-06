package com.xenonplatformer.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Game.STATE;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Label;

public class MenuButton extends Clickable {
	private STATE state;
	private Callable<Void> action;
	private Game game;
	private Label label;
	
	private BufferedImage button, buttonHovered;
	private BufferedImage icon, iconHovered;
	
	public MenuButton(float x, float y, BufferedImage icon, String label, STATE state, Callable<Void> action, Game game) {
		super(x, y, ID.MenuButton, game);
		
		this.icon = icon;
		this.state = state;
		this.action = action;
		this.game = game;
		
		this.label = new Label(false, label, (int) x + 56, (int) y + 140, Color.WHITE, false, true);
		
		button = FileReader.loadImage("/menu_button.png");
		buttonHovered = ImageBrightness.change(button, 0.92f);
		iconHovered = ImageBrightness.change(icon, 0.92f);
		
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
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) game.menu.opacity));
		
		g.drawImage(isHovered() ? buttonHovered : button, (int) x, (int) y, null);
		g.drawImage(isHovered() ? iconHovered : icon, (int) x + ((button.getWidth() - icon.getWidth()) / 2), (int) y + ((button.getHeight() - icon.getHeight()) / 2), null);
		label.render(g);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = button.getWidth();
		height = button.getHeight();
	}
}