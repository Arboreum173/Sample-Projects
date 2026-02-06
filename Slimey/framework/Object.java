package com.xenonplatformer.framework;

import java.awt.Graphics;

import com.xenonplatformer.window.ScrollList;

public abstract class Object {
	protected float x, y;
	protected ID id;
	protected ScrollList scrollList = null;
	
	public Object(float x, float y, ID id) {
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
	public abstract void onAdded();
	public abstract void onRemoved();
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public abstract int getWidth();
	public abstract int getHeight();
	
	public ID getId() {
		return id;
	}
	
	public boolean hasScrollList() {
		return scrollList != null;
	}
	
	public void setScrollList(ScrollList scrollList) {
		this.scrollList = scrollList;
	}
}