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
import com.xenonplatformer.window.Handler;

public class SweatParticle extends GameEntity {
	private Player player;
	private Handler handler;
	private BufferedImage sweat;
	
	private float posX = 0;
	private float posY = 0;
	private float opacity = 0;
	
	public SweatParticle(int posX, Player player, Handler handler) {
		super(player.getX() + 7 + posX, player.getY() + 6, ID.SweatParticle);
		this.posX = posX;
		this.player = player;
		this.handler = handler;
		sweat = Game.spriteSheet.getSubimage(89, 0, 4, 5);
	}
	
	public void tick() {
		if(opacity < 1 && posY == 0) {
			opacity += 0.05;
			if(opacity > 1) {
				opacity = 1;
				posY = 1;
			}
		} else {
			if(posY < 14) {
				posY *= 1.1;
			} else { opacity = 0; }
			if(posY > 8) {
				opacity -= 0.1;
				if(opacity < 0) {
					opacity = 0;
					handler.removeObject(this);
				}
			}
		}
		x = player.getX() + 7 + posX;
		y = player.getY() + 6;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		AffineTransform t = new AffineTransform();
		t.translate(x, y + posY);
		g2d.drawImage(sweat, t, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public Rectangle getBounds() {
		return null;
	}
}