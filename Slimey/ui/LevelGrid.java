package com.xenonplatformer.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Game.STATE;

public class LevelGrid extends GameEntity {
	private Game game;
	private int width, height;
	private boolean putting = false;
	private int lastPosX = -1, lastPosY = -1;
	
	public LevelGrid(Game game) {
		super(0, 0, ID.LevelGrid);
		this.game = game;
		width = (int) Game.levelData.get("width");
		height = (int) Game.levelData.get("height");
		
		game.addMouseListener(initMouseAdapter());
	}
	
	public void tick() {
		if(Game.state == STATE.Builder && putting) {
			Point mousePos = MouseInfo.getPointerInfo().getLocation();
			int mouseX = (int) mousePos.getX() - Game.x;
			int mouseY = (int) mousePos.getY() - Game.y;
			
			if(mouseY > 90 && mouseY < 510) {
				int posX = (int) Math.floor((game.camera.getX() + mouseX) / 32);
				int posY = (int) Math.floor((game.camera.getY() + mouseY) / 32);
				
				boolean posChanged = posX != lastPosX || posY != lastPosY;
				
				if(
					posChanged &&
					posX >= 0 && posX < width &&
					posY >= 0 && posY < height
				) {
					if(game.menu.builderPut == ID.Eraser) {
						game.eraseBlock(posX, posY);
					} else if(game.menu.builderPut == ID.Player) {
						game.changePlayerPos(posX, posY);
					} else if(game.menu.builderPut == ID.Goal) {
						game.changeGoalPos(posX, posY);
					} else {
						game.putBlock(posX, posY, game.menu.builderPut, game.menu.builderBlockDirection);
					}
				}
				
				lastPosX = posX;
				lastPosY = posY;
			}
		}
	}
	
	public void render(Graphics g) {
		if(Game.state == STATE.Builder) {
			int startX = (game.camera.getX() > 0 ? (int) Math.floor(game.camera.getX() / 32) * 32 : 0) - 1;
			int startY = (game.camera.getY() > 0 ? (int) Math.floor(game.camera.getY() / 32) * 32 : 0) - 1;
			
			int endX = (game.camera.getX() + Game.WIDTH < width * 32 ? startX + Game.WIDTH + 32 : width * 32) - 1;
			int endY = (game.camera.getY() + Game.HEIGHT < height * 32 ? startY + Game.HEIGHT + 32 : height * 32) - 1;
			
			for(int posY = startY; posY < endY; posY += 32) {
				for(int posX = startX; posX < endX; posX += 32) {
					g.setColor(new Color(255, 255, 255, 70));
					
					g.drawRect(posX, posY, 32, 1);
					g.drawRect(posX, posY, 1, 32);
					
					if(posX + 32 == endX) g.drawRect(posX + 32, posY, 1, 32);
					if(posY + 32 == endY) g.drawRect(posX, posY + 32, 32, 1);
				}
			}
		}
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	private MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				putting = true;
			}
			
			public void mouseReleased(MouseEvent event) {
				putting = false;
				lastPosX = -1;
				lastPosY = -1;
			}
		};
	}
	
	public Rectangle getBounds() {
		return null;
	}
}