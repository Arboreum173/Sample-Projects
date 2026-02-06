package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;

public class Globe extends Object {
	private BufferedImage globe;
	private float globeDeg = 0;
	
	public Globe() {
		super(100, 600, ID.Globe);
		globe = FileReader.loadImage("/globe.png");
	}
	
	public void tick() {
		globeDeg += 0.005;
		if(globeDeg > Math.PI * 2) {
			globeDeg -= Math.PI * 2;
		}
		if(y > 300) {
			y -= 3;
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.rotate(globeDeg, 300, 300);
		g2d.drawImage(globe, t, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return globe.getWidth();
	}
	
	public int getHeight() {
		return globe.getHeight();
	}
}