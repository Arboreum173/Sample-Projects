package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Handler;

public class StarParticle extends Object {
	private Handler handler;
	private BufferedImage star;
	
	private float deltaX, deltaY;
	private float rotationAngle;
	private float size;
	private float alpha, alphaPerc;

	public StarParticle(Handler handler) {
		super(400, 300, ID.StarParticle);
		this.handler = handler;
		star = Game.spriteSheet.getSubimage(0, 32, 16, 16);
		
		float angle = new Random().nextInt(360);
		angle = (float) Math.toRadians(angle - 90);
		
		deltaX = 5 * (float) Math.cos(angle);
		deltaY = 5 * (float) Math.sin(angle);
		
		size = (float) (new Random().nextInt(5) + 4) / 10;
		
		alpha = (float) (new Random().nextInt(50) + 25) / 100;
		
		int spawnOffset = new Random().nextInt(150) + 100;
		
		x += spawnOffset * Math.cos(angle);
		y += spawnOffset * Math.sin(angle);
	}
	
	public void tick() {
		x += deltaX;
        y += deltaY;
        
        rotationAngle += 0.01;
        
        if(x < -20 || x > 820 || y < -20 || y > 620) {
        	handler.removeObject(this); 
        }
        
        if(alphaPerc < 1) {
        	alphaPerc += 0.05;
        }
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * alphaPerc));
		t.translate(x, y);
		t.scale(size, size);
		t.rotate(rotationAngle, 8, 8);
		g2d.drawImage(star, t, null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return star.getWidth();
	}
	
	public int getHeight() {
		return star.getHeight();
	}
}