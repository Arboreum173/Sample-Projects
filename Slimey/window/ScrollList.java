package com.xenonplatformer.window;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.Graphics;
import java.util.LinkedList;

import com.xenonplatformer.framework.ID;
import com.xenonplatformer.framework.Object;

public class ScrollList extends Object {
	private int width, height;
	
	private float pos = 0;
	private float speed = 0;
	private float space = 0;
	private int priority;
	private float maxScroll;
	
	public enum DIRECTION { horizontal, vertical };
	private DIRECTION direction;
	private int directionSize;
	
	private LinkedList<Object> elements = new LinkedList<Object>();
	
	private Game game;
	
	public ScrollList(DIRECTION direction, float x, float y, int width, int height, int space, int priority, Game game) {
		super(x, y, ID.ScrollList);
		this.width = width;
		this.height = height;
		this.direction = direction;
		this.directionSize = (direction == DIRECTION.vertical ? height : width);
		this.space = space;
		this.priority = priority;
		this.game = game;
		game.addMouseWheelListener(mouseAdapter());
	}
	
	public void tick() {
		if(Math.abs(speed) > 1) {
			pos += (speed *= 0.85);
			update();
		} else {
			speed = 0;
		}
		
		if(pos > maxScroll - directionSize) {
			pos = maxScroll - directionSize;
		} else if(pos < 0) {
			pos = 0;
		}
	}
	
	public void render(Graphics g) { }
	
	MouseAdapter mouseAdapter() {
		return new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent event) {
				scroll(event.getWheelRotation() < 0 ? -15 : 15);
			}
		};
	}
	
	public void onAdded() {
		update();
	}
	
	public void onRemoved() {
		for(Object element : elements) {
			game.handler.removeObject(element);
		}
	}
	
	public void update() {
		float cur = 0;
		for(Object element : elements) {
			int size = (direction == DIRECTION.vertical ? element.getHeight() : element.getWidth());
			if(cur < pos + directionSize + 20) {
				if(cur + size + 20 > pos) {
					if(direction == DIRECTION.vertical) {
						element.setY(y + cur - pos);
						element.setX(x);
					} else {
						element.setX(x + cur - pos);
						element.setY(y);
					}
					game.handler.addObject(element, priority);
				} else {
					// hidden on the left/top
					game.handler.removeObject(element); 
				}
				cur += size + space;
			} else {
				// hidden on the right/bottom
				game.handler.removeObject(element); 
			}
		}
		maxScroll = cur;
	}
	
	public void add(Object element) {
		elements.add(element);
		element.setScrollList(this);
	}
	
	public void scroll(float speed) {
		this.speed = speed;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}