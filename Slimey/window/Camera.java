package com.xenonplatformer.window;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.objects.Block;
import com.xenonplatformer.window.Game.STATE;

public class Camera {
	private Game game;
	
	private int x, y;
	
	private int posX = 0, posY = 0;
	private int endX = 0, endY = 0;
	private int diffPosX = 0, diffPosY = 0;
	private int diffEndX = 0, diffEndY = 0;
	
	private float moveX = 0, moveY = 0;
	private boolean keyUp = false,
		keyDown = false,
		keyRight = false,
		keyLeft = false;
	
	public Camera(Game game) {
		this.x = 0;
		this.y = 0;
		
		this.game = game;
		game.addKeyListener(initKeyAdapter());
	}
	
	public void tick() {
		if(game.player.isPlaying()) setCamera();
		if(Game.state == STATE.Builder) {
			if(keyUp) moveY = -5;
			if(keyDown) moveY = 5;
			if(keyRight) moveX = 5;
			if(keyLeft) moveX = -5;
			
			x += moveX;
			y += moveY;
			
			moveX *= 0.85;
			moveY *= 0.85;
			if(Math.abs(moveX) < 1) moveX = 0;
			if(Math.abs(moveY) < 1) moveY = 0;
			
			loadScene();
		}
	}
	
	public void setCamera() {
		x = (int) game.player.getX() - ((Game.WIDTH / 2) - 15);
		y = (int) game.player.getY() - (Game.HEIGHT - 250);
		loadScene();
	}
	
	private void loadScene() {
		// handle level screen movement while playing and in editor
		int newPosX = (int) Math.floor(x / 32);
		int newPosY = (int) Math.floor(y / 32);
		
		int newEndX = (int) Math.ceil((x + Game.WIDTH) / 32);
		int newEndY = (int) Math.ceil((y + Game.HEIGHT) / 32);
		
		// do nothing if no movement
		if(
			newPosX == posX &
			newPosY == posY &&
			newEndX == endX &&
			newEndY == endY
		) {
			return;
		}
		
		diffPosX = newPosX - posX;
		diffPosY = newPosY - posY;
		diffEndX = newEndX - endX;
		diffEndY = newEndY - endY;
		
		posX = newPosX;
		posY = newPosY;
		endX = newEndX;
		endY = newEndY;
		
		unloadBlocks();
		loadViewportBlocks();
	}
	
	private void unloadBlocks() {
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			if(obj instanceof Block) {
				Block block = (Block) obj;
				// do not unload moving blocks
				if(block.getMovement() == BLOCK_DIRECTION.none) {
					if(block.getX() == (posX - 1) * 32 && diffPosX > 0) {
						// unload left when moved right
						game.handler.removeObject(block);
						i--;
					}
					if(block.getY() == (posY - 1) * 32 && diffPosY > 0) {
						// unload top when moved down
						game.handler.removeObject(block);
						i--;
					}
					if(block.getX() == (endX + 1) * 32 && diffEndX < 0) {
						// unload right when moved left
						game.handler.removeObject(block);
						i--;
					}
					if(block.getY() == (endY + 1) * 32 && diffEndY < 0) {
						// unload bottom when moved up
						game.handler.removeObject(block);
						i--;
					}
				}
			}
		}
	}
	
	private void loadViewportBlocks() {
		if(diffPosX < 0 || diffEndX > 0) {
			for(int curY = posY; curY <= endY; curY++) {
				Block block;
				if(diffPosX < 0) {
					// moved left
					block = game.readBlock(posX, curY);
				} else {
					// moved right
					block = game.readBlock(endX, curY);
				}
				if(block != null) game.handler.addObject(block);
			}
		}
		
		if(diffPosY < 0 || diffEndY > 0) {
			for(int curX = posX; curX <= endX; curX++) {
				Block block;
				if(diffPosY < 0) {
					// moved up
					block = game.readBlock(curX, posY);
				} else {
					// moved down
					block = game.readBlock(curX, endY);
				}
				if(block != null) game.handler.addObject(block);
			}
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public int getEndX() {
		return endX;
	}
	
	public int getEndY() {
		return endY;
	}
	
	private KeyAdapter initKeyAdapter() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(Game.state != STATE.Builder) return;
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_UP) keyUp = true;
				if(key == KeyEvent.VK_DOWN) keyDown = true;
				if(key == KeyEvent.VK_LEFT) keyLeft = true;
				if(key == KeyEvent.VK_RIGHT) keyRight = true;
			}
			
			public void keyReleased(KeyEvent e) {
				if(Game.state != STATE.Builder) return;
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_UP) keyUp = false;
				if(key == KeyEvent.VK_DOWN) keyDown = false;
				if(key == KeyEvent.VK_LEFT) keyLeft = false;
				if(key == KeyEvent.VK_RIGHT) keyRight = false;
			}
		};
	}
}