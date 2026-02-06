package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Handler;

public class Trail extends GameEntity {
	private float alpha = 1;
	private Handler handler;
	
	public Trail(float x, float y, Handler handler) {
		super(x, y, ID.Trail);
		this.handler = handler;
	}
	
	public void tick() {
		if(alpha > 0.2) {
			alpha -= 0.04;
		} else {
			handler.removeObject(this);
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		
		g.setColor(Color.WHITE);
		g.fillOval((int) x, (int) y, 3, 3);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public Rectangle getBounds() {
		return null;
	}
}