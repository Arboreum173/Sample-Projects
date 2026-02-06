package com.xenonplatformer.framework;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.xenonplatformer.window.Game;

public abstract class GameObject extends GameEntity {
	protected int posX, posY;
	protected int originalPosX, originalPosY;
	protected float offsetX = 0, offsetY = 0;
	
	public enum BLOCK_DIRECTION { none, up, right, down, left };
	protected BLOCK_DIRECTION movement;
	
	protected Game game;
	
	public GameObject(int posX, int posY, ID id, Game game) {
		super(posX * 32, posY * 32, id);
		
		this.posX = posX;
		this.posY = posY;
		
		this.originalPosX = posX;
		this.originalPosY = posY;
		
		this.game = game;
		
		posX = getPosX();
		posY = getPosY();
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
	
	public int getPosX() {
		posX = Math.round((x + offsetX) / 32);
		return posX;
	}
	
	public int getPosY() {
		posY = Math.round((y + offsetY) / 32);
		return posY;
	}
	
	public void setPosX(int posX) {
		this.posX = posX;
		x = posX * 32 - offsetX;
	}
	
	public void setPosY(int posY) {
		this.posY = posY;
		y = posY * 32 - offsetY;
	}
	
	public int getOriginalPosX() {
		return originalPosX;
	}
	
	public int getOriginalPosY() {
		return originalPosY;
	}
	
	public abstract void spawn();
	
	public abstract boolean isPermeable();
	
	public BLOCK_DIRECTION getMovement() {
		return movement;
	}
	
	public boolean test() {
		GameObject replaceObj = null;
		
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			
			if(!(obj instanceof GameObject) || obj == this) continue;
			
			GameObject gameObj = (GameObject) obj;
			
			if(
				gameObj.getPosX() == posX &&
				gameObj.getPosY() == posY
			) {
				if(gameObj.getId() == id && gameObj.getMovement() == movement) {
					// same block already at this spot
					return false;
				} else if(
					gameObj.getId() == ID.Player ||
					gameObj.getId() == ID.Enemy ||
					gameObj.getId() == ID.Goal
				) {
					// cannot place block on player/enemy/goal
					if(!isPermeable()) return false;
				} else if(
					id == ID.Player ||
					id == ID.Enemy ||
					id == ID.Goal
				) {
					// cannot put player/enemy/goal on block
					if(!gameObj.isPermeable()) return false;
				} else {
					replaceObj = gameObj;
				}
				continue;
			}
			
			if(gameObj.getBounds().intersects(getBounds())) {
				if(isPermeable()) {
					// allow e.g. water blocks around enemy
					continue;
				} else if(
					id != ID.Enemy &&
					id != ID.Goal &&
					gameObj.getId() == ID.Player
				) {
					// allow putting blocks except enemy/goal next to player
					continue;
				} else if(
					id == ID.Player &&
					gameObj.getId() != ID.Enemy &&
					gameObj.getId() != ID.Goal
				) {
					// allow putting player anywhere except next to enemy/goal
					continue;
				} else if(id == ID.Enemy && gameObj.isPermeable()) {
					// allow putting enemy in e.g. water
					continue;
				} else if(id == ID.Goal && gameObj.getId() == ID.Enemy) {
					// allow putting goal next to enemy
					continue;
				} else {
					// another block is in the way
					return false;
				}
			}
		}
		
		if(replaceObj != null) {
			game.eraseBlock(replaceObj, false);
		}
		
		return true;
	}
	
	public void setOffset(int offsetX, int offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
		x -= offsetX;
		y -= offsetY;
	}
}
