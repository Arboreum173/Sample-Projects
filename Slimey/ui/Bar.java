package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.Static;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game;

public class Bar extends Object {
	private BufferedImage bar;
	private HashMap<Object, Item> items = new HashMap<Object, Item>();
	public enum TYPE { top, right, bottom, left, inventory };
	private Game game;
	
	public Bar(TYPE type, Game game) {
		super(0, 0, ID.Bar);
		
		if(type == TYPE.top) {
			bar = FileReader.loadImage("/top_bar.png");
		} else if(type == TYPE.right) {
			bar = FileReader.loadImage("/right_bar.png");
		} else if(type == TYPE.bottom) {
			bar = FileReader.loadImage("/bottom_bar.png");
			y = Game.HEIGHT - bar.getHeight();
		} else if(type == TYPE.left) {
			bar = FileReader.loadImage("/left_bar.png");
			x = Game.WIDTH - bar.getWidth();
		} else if(type == TYPE.inventory) {
			bar = FileReader.loadImage("/inventory_bar.png");
		}
		this.game = game;
	}
	
	public void add(Object obj, int priority) {
		items.put(obj, new Item(obj.getX(), obj.getY(), priority));
	}
	
	public void add(Object obj) {
		add(obj, 0);
	}
	
	public Set<Object> getItems() {
		return items.keySet();
	}
	
	public void tick() {
		for(Map.Entry<Object, Item> entry : items.entrySet()) {
			Object obj = entry.getKey();
			Item data = entry.getValue();
			obj.setX(x + data.getX());
			obj.setY(y + data.getY());
		}
	}
	
	public void render(Graphics g) {
		g.drawImage(bar, (int) x, (int) y, null);
	}
	
	public void onAdded() {
		for(Map.Entry<Object, Item> entry : items.entrySet()) {
			game.handler.addObject(entry.getKey(), entry.getValue().getPriority());
		}
	}
	
	public void onRemoved() {
		for(Object obj : items.keySet()) {
			game.handler.removeObject(obj);
		}
	}
	
	public int getWidth() {
		return bar.getWidth();
	}
	
	public int getHeight() {
		return bar.getHeight();
	}
	
	private static class Item {
		private float x, y;
		private int priority;
		
		public Item(float x, float y, int priority) {
			this.x = x;
			this.y = y;
			this.priority = priority;
		}
		
		public float getX() {
			return x;
		}
		
		public float getY() {
			return y;
		}
		
		@SuppressWarnings("unused")
		public void setX(float x) {
			this.x = x;
		}
		
		@SuppressWarnings("unused")
		public void setY(float y) {
			this.y = y;
		}
		
		public int getPriority() {
			return priority;
		}
	}
	
	public static class InventoryBackground extends Static {
		public InventoryBackground() {
			super(245, 20, "/inventory_bar_bg.png");
		}
	}
}