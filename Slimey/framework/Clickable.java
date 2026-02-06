package com.xenonplatformer.framework;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;

import com.xenonplatformer.window.Game;

public abstract class Clickable extends Object {
	protected int width, height;
	
	public Clickable(float x, float y, ID id, Game game) {
		super(x, y, id);
		
		game.addMouseListener(initMouseAdapter());
		game.addMouseWheelListener(initMouseAdapter());
	}
	
	protected abstract MouseAdapter initMouseAdapter();
	
	public boolean isHovered() {
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		
		int mouseX = (int) mousePos.getX() - Game.x;
		int mouseY = (int) mousePos.getY() - Game.y;
		
		if(
			mouseX > x && mouseX < x + width &&
			(hasScrollList() ?
				mouseX > scrollList.getX() &&
				mouseX < scrollList.getX() + scrollList.getWidth() : true
			)
		) {
			return (
				mouseY > y && mouseY < y + height &&
				(hasScrollList() ?
					mouseY > scrollList.getY() &&
					mouseY < scrollList.getY() + scrollList.getHeight() : true
				)
			);
		} else {
			return false;
		}
	}
	
	public boolean isAccessible() {
		if(!hasScrollList()) return true;
		
		Point mousePos = MouseInfo.getPointerInfo().getLocation();
		
		int mouseX = (int) mousePos.getX() - Game.x;
		int mouseY = (int) mousePos.getY() - Game.y;
		
		if(mouseX > scrollList.getX() && mouseX < scrollList.getX() + scrollList.getWidth()) {
			return (
				mouseY > scrollList.getY() &&
				mouseY < scrollList.getY() + scrollList.getHeight()
			);
		} else {
			return false;
		}
	}
	
	protected abstract void setDimensions();
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public abstract void tick();
	public abstract void render(Graphics g);
}