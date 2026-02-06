package com.xenonplatformer.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.FileInputStream;
import java.io.IOException;

import com.xenonplatformer.framework.ID;
import com.xenonplatformer.framework.Object;

public class Label extends Object {
	private String text;
	private Color color;
	private boolean isTitle, hasShadow, isCentered;
	private Font fontTitle, fontText;
	private FontMetrics metrics = null;
	
	private boolean calcCenter = false;
	
	public Label(boolean isTitle, String text, int x, int y, Color color, boolean hasShadow, boolean isCentered) {
		super(x, y, ID.Label);
		this.isTitle = isTitle;
		this.text = text;
		this.color = color;
		this.hasShadow = hasShadow;
		this.isCentered = isCentered;
		
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("res\\zpix.ttf"));
			fontTitle = font.deriveFont(34F);
			fontText = font.deriveFont(28F);
		} catch(FontFormatException | IOException e) { e.printStackTrace(); }
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		if(metrics == null) metrics = g.getFontMetrics(isTitle ? fontTitle : fontText);
		
		g.setFont(isTitle ? fontTitle : fontText);
		if(hasShadow) {
			g.setColor(Color.BLACK);
			g.drawString(text, (int) x + 2, (int) y + 2);
		}
		
		if(isCentered && !calcCenter) center(g);
		
		g.setColor(color);
		g.drawString(text, (int) x, (int) y);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public void render(Graphics g, int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
		
		if(isCentered) center(g);
		
		render(g);
	}
	
	private void center(Graphics g) {
		x = x - metrics.stringWidth(text) / 2;
		calcCenter = true;
	}
	
	public int getWidth() {
		return metrics.stringWidth(text);
	}
	
	public int getHeight() {
		return metrics.getHeight();
	}
}