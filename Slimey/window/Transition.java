package com.xenonplatformer.window;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.Callable;

import com.xenonplatformer.window.Game.STATE;

public class Transition {
	private STATE state = STATE.Menu;
	private Callable<Void> callback;
	
	private int arrowWidth = 150;
	private int x = -Game.WIDTH - (arrowWidth * 2);
	
	private boolean visible = false;
	private boolean switched = false;
	private int timeout = 0;
	
	public void tick() {
		if(!visible) return;
		
		if(x == -arrowWidth) {
			if(!switched) {
				Game.state = state;
				try {
					callback.call();
				} catch(Exception e) { e.printStackTrace(); }
				switched = true;
			}
			timeout++;
			if(timeout == 15) x += 50;
		} else {
			x += 50; 
		}
		
		if(x >= Game.WIDTH) {
			visible = false;
			Game.transitioning = false;
		}
	}
	
	public void render(Graphics g) {
		if(!visible) return;
		
		g.setColor(new Color(30, 30, 30));
		int[] coordsX = {x, x + Game.WIDTH + arrowWidth, x + Game.WIDTH + (arrowWidth * 2), x + Game.WIDTH + arrowWidth, x, x + arrowWidth, x};
		int[] coordsY = {0, 0, Game.HEIGHT / 2, Game.HEIGHT, Game.HEIGHT, Game.HEIGHT / 2, 0};
		g.fillPolygon(coordsX, coordsY, 6);
	}
	
	public void show(STATE state, Callable<Void> callback) {
		Game.transitioning = true;
		switched = false;
		timeout = 0;
		this.visible = true;
		this.state = state;
		this.callback = callback;
		x = -Game.WIDTH - (arrowWidth * 2);
	}
	
	public void show(STATE state) {
		show(state, new Callable<Void>() {
			public Void call() { return null; }
		});
	}
}