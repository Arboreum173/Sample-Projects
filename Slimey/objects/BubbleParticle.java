package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Handler;

public class BubbleParticle extends GameEntity {
	private float opacity = 0;
	private float size = 1;
	private Handler handler;
	
	private boolean popping = false;
	private BufferedImage bubble;
	
	public BubbleParticle(float x, float y, Handler handler) {
		super(x, y, ID.BubbleParticle);
		this.handler = handler;
		
		bubble = Game.spriteSheet.getSubimage(16, 42, 10, 10);
	}
	
	public void tick() {
		if(!popping) {
			if(inWater()) {
				y--;
				if(opacity < 1) {
					opacity += 0.15f;
					if(opacity > 1) opacity = 1;
				}
			} else {
				popping = true;
			}
		} else {
			if(opacity > 0) {
				opacity -= 0.075f;
				if(opacity < 0) opacity = 0;
				size += 0.075f;
			} else {
				handler.removeObject(this);
			}
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		t.translate(x - ((10 * size - 10) / 2), y - ((10 * size - 10) / 2));
		t.scale(size, size);
		g2d.drawImage(bubble, t, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public boolean inWater() {
		for(int i = 0; i < handler.objects.size(); i++) {
			Object tempObject = handler.objects.get(i);
			if(tempObject.getId() == ID.WaterBlock) {
				Block waterBlock = (Block) tempObject;
				if(getBounds().intersects(waterBlock.getBounds())) {
					BLOCK_DIRECTION movement = waterBlock.getMovement();
					if(movement == BLOCK_DIRECTION.up) y -= 1.5f;
					if(movement == BLOCK_DIRECTION.down) y += 1.5f;
					return true;
				}
			}
		}
		return false;
	}
	
	public void pop() {
		if(!popping) AudioPlayer.getSound("pop").play();
		popping = true;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 10, 1);
	}
}