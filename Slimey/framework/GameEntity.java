package com.xenonplatformer.framework;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameEntity extends Object {
	public GameEntity(float x, float y, ID id) {
		super(x, y, id);
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract Rectangle getBounds();
	
	public int getWidth() {
		return (int) getBounds().getWidth();
	}
	
	public int getHeight() {
		return (int) getBounds().getHeight();
	}
}