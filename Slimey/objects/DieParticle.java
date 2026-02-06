package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game;

public class DieParticle extends GameEntity {
	private float opacity = 1;
	private float angle;
	
	private BufferedImage particle;
	
	public DieParticle(float x, float y, float angle) {
		super(x, y, ID.DieParticle);
		this.angle = angle;
		
		particle = Game.spriteSheet.getSubimage(16, 32, 10, 10);
	}

	public void tick() {
		x += 2 * (float) Math.cos(Math.toRadians(angle - 90));
        y += 2 * (float) Math.sin(Math.toRadians(angle - 90));
        if(opacity > 0) {
        	opacity -= 0.02f;
        	if(opacity < 0) opacity = 0;
        }
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		AffineTransform t = new AffineTransform();
		t.translate(x + 3, y + 3);
		g2d.drawImage(particle, t, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }

	public Rectangle getBounds() {
		return null;
	}
}