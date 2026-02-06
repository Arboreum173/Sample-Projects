package com.xenonplatformer.framework;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Static extends Object {
	private BufferedImage img;
	
	public Static(float x, float y, String img) {
		super(x, y, ID.Static);
		this.img = FileReader.loadImage(img);
	}

	public void tick() { }

	public void render(Graphics g) {
		g.drawImage(img, (int) x, (int) y, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }

	public int getWidth() {
		return img.getWidth();
	}
	
	public int getHeight() {
		return img.getHeight();
	}
}