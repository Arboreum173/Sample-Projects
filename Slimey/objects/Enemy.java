package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.GameObject;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;

public class Enemy extends GameObject {
	private float velX = 0, velY = 0;
	private float gravity = 0.5f;
	private final float MAX_FALL_SPEED = 15;
	
	private float opacity = 1,
		size = 1;
	
	private boolean spawning = false,
		disappearing = false,
		dying = false;
	
	private int fallTimer = 0;
	
	private int eyePos;
	
	private int directionChange = 0;
	
	private float skewCounter = 0,
		skew = 0;
	
	private BufferedImage enemy;
	private BufferedImage enemyEye;
	
	public Enemy(int posX, int posY, BLOCK_DIRECTION movement, Game game) {
		super(posX, posY, ID.Enemy, game);
		
		enemy = Game.spriteSheet.getSubimage(0, 128, 50, 55);
		enemyEye = Game.spriteSheet.getSubimage(0, 183, 11, 7);
		
		this.movement = movement;
		
		if(movement == BLOCK_DIRECTION.right) {
			velX = 1.6f;
			eyePos = 8;
		} else if(movement == BLOCK_DIRECTION.left) {
			velX = -1.6f;
			eyePos = 0;
		}
		
		setOffset(9, 23);
	}

	public void tick() {
		if(game.player.isPlaying() && !dying) {
			x += velX;
			y += velY;
			
			velY += gravity;
			
			if(velY > MAX_FALL_SPEED) {
				velY = MAX_FALL_SPEED;
			}
			
			
			
			handleCollision();
			
			game.loadAdjacentBlock(
				getPosX(),
				getPosY(),
				velX >= 0 ? BLOCK_DIRECTION.right : BLOCK_DIRECTION.left
			);
			
			handleDirectionChange();
			
			skewCounter += 0.07;
			skew = (float) (Math.abs(Math.sin(skewCounter)) / 15) * -velX;
			if(skewCounter > Math.PI) skewCounter = 0;
			
			doFallTimer();
		}
		
		checkSpawn();
		checkDisappearance();
		checkDeath();
	}
	
	private void handleDirectionChange() {
		if(directionChange == 1) {
			if(velX < 0) {
				// slow down
				velX += 0.4;
			} else {
				// speed up
				if(eyePos < 8) {
					eyePos++;
					velX += 0.2;
				} else {
					directionChange = 0;
				}
			}
		} else if(directionChange == -1) {
			if(velX > 0) {
				// slow down
				velX -= 0.4;
			} else {
				// speed up
				if(eyePos > 0) {
					eyePos--;
					velX -= 0.2;
				} else {
					directionChange = 0;
				}
			}
		}
	}
	
	private void doFallTimer() {
		// die when falling for too long
		if(velY > 0) {
			fallTimer++;
		} else {
			fallTimer = 0;
		}
		if(fallTimer > 100) die();
	}
	
	private void checkSpawn() {
		if(spawning) {
			if(size < 1) {
				size += 0.1;
				if(size > 1) size = 1;
			}
			if(opacity < 1) {
				opacity += 0.1;
				if(opacity > 1) opacity = 1;
			}
			if(size == 1 && opacity == 1) {
				spawning = false;
			}
		}
	}
	
	private void checkDisappearance() {
		if(disappearing) {
			if(size > 0) {
				size -= 0.1;
				if(size < 0) size = 0;
			}
			
			if(opacity > 0) {
				opacity -= 0.1;
				if(opacity < 0) opacity = 0;
			}
			
			if(size == 0 && opacity == 0) {
				// deleted from map
				int index = (getPosY() * (int) Game.levelData.get("width")) + getPosX();
				Game.levelData.getJSONArray("map").put(index, 0);
				game.handler.removeObject(this);
			}
		}
	}
	
	private void checkDeath() {
		if(dying) {
			opacity -= 0.1;
			if(opacity < 0) opacity = 0;
			
			if(opacity == 0) {
				game.handler.removeObject(this);
			}
		}
	}
	
	private void handleCollision() {
		boolean collisionTop = false,
			collisionBottom = false,
			collisionRight = false,
			collisionLeft = false;
		
		boolean topMoving = false,
			bottomMoving = false,
			rightMoving = false,
			leftMoving = false;
		
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			
			if(!(obj instanceof Block)) continue;
			
			Block block = (Block) obj;
			
			if(block.isSolid()) {
				// right collision
				if(getBoundsRight().intersects(block.getBounds())) {
					collisionRight = true;
					rightMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					x = (int) block.getX() - getWidth() - 4;
				}
				
				if(getMovementBoundsRight().intersects(block.getBounds())) {
					directionChange = -1;
				}
				
				// left collision
				if(getBoundsLeft().intersects(block.getBounds())) {
					collisionLeft = true;
					leftMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					x = (int) block.getX() + 29;
				}
				
				if(getMovementBoundsLeft().intersects(block.getBounds())) {
					directionChange = 1;
				}
				
				// top collision
				if(getBoundsTop().intersects(block.getBounds())) {
					collisionTop = true;
					topMoving = block.getMovement() != BLOCK_DIRECTION.none;
				}
				
				// bottom collision
				if(getBoundsFloor().intersects(block.getBounds())) {
					collisionBottom = true;
					bottomMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					y = obj.getY() - getHeight() - 6;
					velY = 0;
				}
			}
			
			// die when hitting spike
			if(block.getId() == ID.SpikeBlock) {
				if(getBounds().intersects(block.getBounds())) {
					die();
				}
			}
			
			// squashed by moving blocks
			if(
				(topMoving && collisionBottom) ||
				(bottomMoving && collisionTop) ||
				(rightMoving && collisionLeft) ||
				(leftMoving && collisionRight)
			) {
				die();
			}
		}
	}
	
	private void die() {
		AudioPlayer.getSound("dead").play();
		
		if(!dying) {
			int angle = 0;
			for(int j = 0; j < 8; j++) {
				game.handler.addObject(new DieParticle(x + 16, y + 20, angle));
				angle += 45;
			}
		}
		
		dying = true;
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		
		int bodyX = (int) (x + ((enemy.getWidth() - (enemy.getWidth() * size)) / 2));
		int bodyY = (int) (y + ((enemy.getHeight() - (enemy.getHeight() * size)) / 2));
		
		AffineTransform tBody = new AffineTransform();
		tBody.translate(bodyX, bodyY);
		tBody.shear(skew, 0);
		tBody.scale(size, size);
		
		AffineTransform tEye = new AffineTransform();
		
		int eyeX = (int) (bodyX + (19 * size));
		int eyeY = (int) (bodyY + (27 * size));
		
		tEye.translate(eyeX, eyeY);
		tEye.scale(size, size);
		
		g2d.drawImage(enemy, tBody, null);
		g2d.drawImage(enemyEye, tEye, null);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public void spawn() {
		opacity = 0;
		size = 0.3f;
		spawning = true;
		game.handler.addObject(this, 3);
	}
	
	public void disappear() {
		disappearing = true;
	}
	
	public boolean isPermeable() {
		return false;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) x + 5, (int) y + 9, (int) 39, (int) 46);
	}
	
	public Rectangle getBoundsFloor() {
		return new Rectangle((int) x + 12, (int) y + getHeight() - 2, (int) getWidth() - 12, 8);
	}
	
	public Rectangle getBoundsTop() {
		return new Rectangle((int) x + 12, (int) y + 8, (int) getWidth() - 12, 8);
	}
	
	public Rectangle getBoundsRight() {
		return new Rectangle((int) x + getWidth(), (int) y + 18, (int) 5, (int) getHeight() - 20);
	}
	
	public Rectangle getBoundsLeft() {
		return new Rectangle((int) x + 5, (int) y + 16, (int) 5, (int) getHeight() - 20);
	}
	
	public Rectangle getMovementBoundsRight() {
		return new Rectangle((int) x + getWidth() + 20, (int) y + 8, (int) 5, (int) getHeight() - 12);
	}
	
	public Rectangle getMovementBoundsLeft() {
		return new Rectangle((int) x - 15, (int) y + 8, (int) 5, (int) getHeight() - 12);
	}
}