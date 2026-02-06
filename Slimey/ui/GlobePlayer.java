package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;

public class GlobePlayer extends Object {
	private BufferedImage player, playerEyes, playerPupils;
	private BufferedImage shadow;
	private Globe globe;
	
	private float skewCounter = 0,
		skew = 0,
		skewPos = 0;
	
	public GlobePlayer(Globe globe) {
		super(365, globe.getY(), ID.GlobePlayer);
		
		this.globe = globe;
		
		BufferedImage playerSheet = FileReader.loadImage("/player.png");
		
		player = playerSheet.getSubimage(0, 0, 72, 70);
		playerEyes = playerSheet.getSubimage(72, 0, 44, 20);
		playerPupils = playerSheet.getSubimage(72, 20, 33, 9);
		
		shadow = FileReader.loadImage("/player_shadow.png");
	}
	
	public void tick() {
		y = globe.getY() - 50;
		
		skewCounter += 0.07;
		skew = (float) Math.abs(Math.sin(skewCounter)) / 9;
		skewPos = 70 * skew;
		if(skewCounter > Math.PI) { skewCounter = 0; }
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform tShadow = new AffineTransform();
		tShadow.translate(x - skewPos, y + 35);
		g2d.drawImage(shadow, tShadow, null);
		
		AffineTransform tPlayer = new AffineTransform();
		tPlayer.translate(x - skewPos, y);
		tPlayer.shear(skew, 0);
		g2d.drawImage(player, tPlayer, null);
		
		AffineTransform tEyes = new AffineTransform();
		tEyes.translate(x + 11 - skewPos, y + 18);
		g2d.drawImage(playerEyes, tEyes, null);
		
		AffineTransform tPupils = new AffineTransform();
		tPupils.translate(x + 12 - skewPos, y + 23);
		g2d.drawImage(playerPupils, tPupils, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return player.getWidth();
	}
	
	public int getHeight() {
		return player.getHeight();
	}
}