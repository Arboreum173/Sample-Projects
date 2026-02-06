package com.xenonplatformer.window;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.LinkedList;

import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game.STATE;

public class Handler {
	public LinkedList<Object> objects = new LinkedList<Object>();
	public HashMap<Object, Integer> priorities = new HashMap<Object, Integer>();
	private int tickI = 0;
	
	public void tick() {
		for(tickI = 0; tickI < objects.size(); tickI++) {
			try {
				Object obj = objects.get(tickI);
				obj.tick();
			} catch(NullPointerException e) { }
		}
	}
	
	public void render(Graphics g, Camera camera) {
		Graphics2D g2d = (Graphics2D) g;
		for(int i = 0; i < objects.size(); i++) {
			try {
				Object obj = objects.get(i);
				if(
					obj instanceof GameEntity &&
					(Game.state == STATE.Game || Game.state == STATE.Builder)
				) {
					g2d.translate(-camera.getX(), -camera.getY());
					obj.render(g);
					g2d.translate(camera.getX(), camera.getY());
				} else {
					obj.render(g);
				}
			} catch(NullPointerException e) { }
		}
	}
	
	public void addObject(Object object, int priority) {
		if(object == null || hasObject(object)) return;
		
		int higher = 0;
		for(int i : priorities.values()) {
			if(i > priority) higher++;
		}
		
		priorities.put(object, priority);
		
		int index = objects.size() - higher;
		objects.add(index, object);
		
		object.onAdded();
		
		if(tickI >= index) tickI++;
	}
	
	public void addObject(Object object) {
		addObject(object, 0);
	}
	
	public void removeObject(Object object) {
		if(!hasObject(object)) return;
		
		int index = objects.indexOf(object);
		objects.remove(object);
		priorities.remove(object);
		object.onRemoved();
		if(tickI >= index) tickI--;
	}
	
	public boolean hasObject(Object object) {
		return objects.contains(object);
	}
	
	public void clearObjects() {
		objects.clear();
		priorities.clear();
	}
	
	public void clearGameObjects() {
		for(int i = 0; i < objects.size(); i++) {
			Object obj = objects.get(i);
			if(obj instanceof GameEntity) {
				int index = objects.indexOf(obj);
				objects.remove(obj);
				priorities.remove(obj);
				if(i >= index) i--;
			}
		}
	}
	
	public void clearObjectsWithId(ID id) {
		for(int i = 0; i < objects.size(); i++) {
			Object obj = objects.get(i);
			if(obj.getId() == id) {
				int index = objects.indexOf(obj);
				objects.remove(obj);
				priorities.remove(obj);
				if(i >= index) i--;
			}
		}
	}
}