package com.xenonplatformer.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Menu;

public class MenuBar extends Object {
	private Menu menu;
	private BufferedImage menuBar;
	
	public MenuBar(Menu menu) {
		super(0, 422, ID.MenuBar);
		this.menu = menu;
		menuBar = FileReader.loadImage("/menu_bar.png");
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) menu.opacity));
		g.drawImage(menuBar, 0, 422, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return menuBar.getWidth();
	}
	
	public int getHeight() {
		return menuBar.getHeight();
	}
}