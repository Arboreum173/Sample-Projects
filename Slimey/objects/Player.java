package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.xenonplatformer.framework.GameEntity;
import com.xenonplatformer.framework.GameObject;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Game.STATE;

public class Player extends GameObject {
	private float gravity = 0.5f;
	
	private boolean keyUp = false,
		keyDown = false,
		keyRight = false,
		keyLeft = false;
	
	private float velX = 0, velY = 0;
	private float moveX = 0, moveY = 0;
	private float inertiaX = 0;
	private float conveyorX = 0;
	private float friction = 0.7f;
	
	private float maxMoveX = 4;
	private float maxMoveY = 15;
	
	private boolean jumping = false;
	private boolean hanging = false;
	private boolean standing = false;
	private boolean inWater = false;
	
	private boolean facingRight = true;
	
	private boolean[] switchedButtons = {false, false, false, false};
	
	private int spawnState = -1, timer;
	
	private boolean playing = false,
		winning = false,
		dying = false;
	
	private float opacity = 0;
	
	private boolean verticalRide = false;
	private float verticalRideY = 0;
	
	private int fallTimer = 0;
	
	private float kickForceX = 5, kickForceY = 5;
	private int kickTimer = 0;
	
	private boolean jumpedIntoWater = false;
	
	private boolean climbingLadder = false,
		climbingHorizontalLadder = false,
		onZeroBlock = false;
	
	private float scaleY = 1;
	private boolean squeezeDone = false,
		puffDone = false;
	
	private float skew = 0,
		skewCounter = 0,
		skewEyesPos = 0;
	
	private float hangSqueeze = 1,
		hangPos = 0;
	
	private boolean enemyNearby = false;
	private int sweatTimer = 0;
	private float shiver = 0;
	private boolean shiverDone = false;
	
	private int directionChange = 0;
	private float pupilsX = 0, pupilsY = 0;
	
	private Random r = new Random();
	private BufferedImage player, playerEyes, playerPupils;
	private enum SIDE { Top, Bottom, Right, Left, All }
	
	public Player(int posX, int posY, Game game) {
		super(posX, posY, ID.Player, game);
		
		player = Game.spriteSheet.getSubimage(32, 0, 35, 35);
		playerEyes = Game.spriteSheet.getSubimage(67, 0, 22, 10);
		playerPupils = Game.spriteSheet.getSubimage(67, 10, 17, 5);
		
		game.addKeyListener(initKeyAdapter());
		
		setOffset(2, 3);
	}
	
	public void tick() {
		if(!playing) {
			// spawn effect
			if(spawnState == 0) {
				opacity = 0;
				y -= 80;
				spawnState = 1;
				timer = 20;
			} else if(spawnState == 1) {
				if(timer > 0) {
					timer--;
				} else {
					spawnState = 2; 
				}
			} else if(spawnState == 2) {
				y += 8;
				opacity += 0.1;
				if(opacity > 1) opacity = 1;
				
				if(opacity == 1) {
					AudioPlayer.getSound("spawn").play();
					playing = true;
					spawnState = 3;
				}
			}
		} else {
			x += (velX = moveX + inertiaX + conveyorX);
			y += (velY = moveY + verticalRideY);
			
			standing = touchingSolid(SIDE.Bottom, true);
			inWater = touching(ID.WaterBlock, SIDE.All, false) && !standing;
			climbingLadder = touching(ID.LadderBlock, SIDE.All, false) && keyUp;
			climbingHorizontalLadder = touching(ID.HorizontalLadderBlock, SIDE.All, false) && keyUp;
			onZeroBlock = (
				touching(ID.ZeroBlock, SIDE.Bottom, false) ||
				touching(ID.BreakableZeroBlock, SIDE.Bottom, false)
			);
			
			if(!standing && !(climbingLadder || climbingHorizontalLadder)) {
				if(inWater) {
					moveY = 1;
				} else {
					moveY += gravity;
				}
			}
			
			if(kickTimer == 0) {
				if(keyRight) moveX += 0.3;
				if(keyLeft) moveX -= 0.3;
			}
			
			if(Math.abs(moveX) > maxMoveX) {
				moveX = moveX < 0 ? -maxMoveX : maxMoveX;
			}
			
			if(moveY > maxMoveY) {
				moveY = maxMoveY;
			}
			
			handleCollision();
			
			handleKeyControl();
			
			if(!keyLeft && !keyRight) {
				if(Math.abs(moveX) > 0.05) {
					moveX *= friction;
				} else {
					moveX = 0;
				}
			}
			
			if(kickTimer > 0) {
				kickTimer--;
				friction = 0.92f;
			}
			
			// squeeze when hanging on wall
			if(hanging || hangSqueeze < 1) {
				if(hanging) {
					if(hangSqueeze > 0.92) hangSqueeze -= 0.01f;
				} else if(hangSqueeze < 1) {
					hangSqueeze += 0.01f;
				}
				// fix position when hanging on wall on the right
				hangPos = facingRight ? getWidth() * (1 - hangSqueeze) : 0;
			}
			
			if(r.nextInt(3) == 1 && (velX != 0 || velY != 0)) {
				game.handler.addObject(new Trail(x + r.nextInt(24) + 6, y + r.nextInt(24) + 6, game.handler), 0);
			}
			
			if(directionChange == 1) {
				// move pupils to the right
				facingRight = true;
				if(pupilsX < 0) {
					pupilsX += 0.5;
				} else {
					directionChange = 0;
				}
			} else if(directionChange == -1) {
				// move pupils to the left
				facingRight = false;
				if(pupilsX > -5) {
					pupilsX -= 0.5;
				} else {
					directionChange = 0;
				}
			}
			
			pupilsY = moveY / 5;
			
			if(jumping) verticalRide = false;
			
			skewCounter += 0.12;
			skew = (float) Math.abs(Math.sin(skewCounter)) * (-moveX / 30);
			skewEyesPos = 20 * skew;
			if(skewCounter > Math.PI) skewCounter = 0;
			
			// squeeze effect
			if(jumping || moveY > 0) {
				squeezeDone = false;
				puffDone = false;
				scaleY = 1 + (Math.abs(moveY) / 80);
			} else if(!squeezeDone) {
				if(scaleY > 0.95) {
					scaleY -= 0.05;
				} else {
					squeezeDone = true; 
				}
			} else if(!puffDone) {
				if(scaleY < 1) {
					scaleY += 0.05;
				} else {
					puffDone = true; 
				}
			}
			
			if(
				!verticalRide &&
				!hanging &&
				!climbingLadder &&
				!climbingHorizontalLadder &&
				!inWater
			) {
				// die when falling for too long
				if(moveY > 0) {
					fallTimer++; 
				} else {
					fallTimer = 0; 
				}
				if(fallTimer > 100) die(false);
			}
			
			// sweat and shiver effect
			if(enemyNearby) {
				sweatTimer++;
				if(sweatTimer == 50) {
					game.handler.addObject(new SweatParticle(r.nextInt(18), this, game.handler), 3);
					sweatTimer = 0;
				}
				if(shiver < 1 && !shiverDone) {
					shiver += 0.5;
				} else if(shiver > 0) {
					shiverDone = true;
					shiver -= 0.5;
				} else {
					shiverDone = false; 
				}
			}
			
			// splash effect
			if(inWater) {
				if(!jumpedIntoWater) AudioPlayer.getSound("swimJump").play();
				jumpedIntoWater = true;
			} else {
				jumpedIntoWater = false; 
			}
		}
		
		if(dying) {
			velX = 0;
			opacity -= 0.1;
			if(opacity < 0) opacity = 0;
			if(timer > 0) {
				timer--;
			} else {
				game.handler.clearObjects();
	        	game.loadLevelMap();
	        	game.handler.addObject(game.menu.builderStopButton);
	        	game.player.spawn();
			}
		} else if(winning) {
			velX = 0;
			if(opacity > 0) {
				opacity -= 0.05;
				if(opacity < 0) opacity = 0;
				y -= 2;
			} else {
				if(timer > 0) {
					timer--;
				} else {
					Game.markLevelAsWon(Game.levelType, Game.levelID);
					
					game.handler.clearObjects();
	            	game.loadLevelMap();
	            	game.player.show();
	            	Game.state = STATE.Builder;
	            	game.menu.builderPlayEnd();
	            	
					winning = false;
				}
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
			
			if(
				obj.getId() == ID.BrickBlock ||
				obj.getId() == ID.IceBlock ||
				obj.getId() == ID.ZeroBlock ||
				obj.getId() == ID.TrampolineBlock ||
				obj.getId() == ID.RightConveyorBlock ||
				obj.getId() == ID.LeftConveyorBlock ||
				obj.getId() == ID.BreakableBrickBlock ||
				obj.getId() == ID.BreakableIceBlock ||
				obj.getId() == ID.BreakableZeroBlock ||
				(obj.getId() == ID.BlueDoor && !switchedButtons[0]) ||
				(obj.getId() == ID.RedDoor && !switchedButtons[1]) ||
				(obj.getId() == ID.GreenDoor && !switchedButtons[2]) ||
				(obj.getId() == ID.YellowDoor && !switchedButtons[3])
			) {
				Block block = (Block) obj;
				
				// top collision
				if(getBoundsTop().intersects(block.getBounds())) {
					collisionTop = true;
					topMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					y = block.getY() + 32;
					moveY = 0;
					
					if(
						block.getId() == ID.BreakableBrickBlock ||
						block.getId() == ID.BreakableIceBlock ||
						block.getId() == ID.BreakableZeroBlock
					) {
						block.crumble(true);
						if(block.getMovement() != BLOCK_DIRECTION.none) {
							y = block.getY() + getHeight() + 3;
						}
					}
				}
				
				// bottom collision
				if(getTouchBottom().intersects(block.getBounds())) {
					collisionBottom = true;
					bottomMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					y = block.getY() - getHeight();
					int intensity = moveY > 2 ? ((int) Math.abs(moveY) * 3) : 1;
					
					if(moveY > 0 && !verticalRide) {
						AudioPlayer.getSound("land").play();
					}
					
					moveY = 0;
					verticalRideY = 0;
					
					float blockVelX = block.getVelX();
					float blockVelY = block.getVelY();
					
					if(blockVelY > 0) {
						verticalRide = true;
						verticalRideY = 1.5f;
						y = block.getY() - getHeight();
					} else {
						verticalRide = false; 
					}
					
					if(blockVelY < 0) {
						y = block.getY() - getHeight();
					}
					
					if(
						block.getId() == ID.BrickBlock ||
						block.getId() == ID.BreakableBrickBlock
					) {
						friction = 0.65f;
					} else if(
						block.getId() == ID.IceBlock ||
						block.getId() == ID.BreakableIceBlock
					) {
						friction = 0.98f;
					} else if(
						block.getId() == ID.ZeroBlock ||
						block.getId() == ID.BreakableZeroBlock ||
						block.getId() == ID.TrampolineBlock
					) {
						friction = 0.55f;
					}
					
					jumping = false;
					
					if(
						block.getId() == ID.BreakableBrickBlock ||
						block.getId() == ID.BreakableIceBlock ||
						block.getId() == ID.BreakableZeroBlock
					) {
						block.crack(intensity); 
					}
					
					if(block.getId() == ID.RightConveyorBlock) {
						conveyorX = 3.5f;
					} else if(block.getId() == ID.LeftConveyorBlock) {
						conveyorX = -3.5f;
					} else {
						conveyorX = 0;
					}
					
					BLOCK_DIRECTION movement = block.getMovement();
					if(
						movement == BLOCK_DIRECTION.right ||
						movement == BLOCK_DIRECTION.left
					) {
						inertiaX = blockVelX;
					} else {
						inertiaX = 0;
					}
					
					kickTimer = 0;
				} else {
					verticalRide = false;
				}
				
				// right collision
				if(getBoundsRight().intersects(block.getBounds())) {
					collisionRight = true;
					rightMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					int stopAt = (int) block.getX() - getWidth() + 3;
					if(x > stopAt) x = stopAt;
					
					if(moveX > 0) moveX = 0;
					inertiaX = 0;
					conveyorX = 0;
				}
				
				// left collision
				if(getBoundsLeft().intersects(block.getBounds())) {
					collisionLeft = true;
					leftMoving = block.getMovement() != BLOCK_DIRECTION.none;
					
					int stopAt = (int) block.getX() + getWidth() - 3;
					if(x < stopAt) x = stopAt;
					
					if(moveX < 0) moveX = 0;
					inertiaX = 0;
					conveyorX = 0;
				}
			}
			
			if(
				obj.getId() == ID.LadderBlock ||
				obj.getId() == ID.HorizontalLadderBlock
			) {
				Block block = (Block) obj;
				if(getBoundsTop().intersects(block.getBounds())) {
					inertiaX = block.getVelX();
				}
			}
			
			if(inWater) friction = 0.93f;
			if(climbingLadder || climbingHorizontalLadder) friction = 0;
			
			if(obj.getId() == ID.Enemy) {
				float distX = Math.abs(obj.getX() - x);
				float distY = Math.abs(obj.getY() - y);
				float dist = (float) Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
				enemyNearby = dist < 200;
			}
			
			if(
				(obj.getId() == ID.SpikeBlock ||
				obj.getId() == ID.Enemy) &&
				getBounds().intersects(((GameEntity) obj).getBounds()) &&
				playing
			) {
				die(obj.getId() == ID.Enemy); 
			}
			
			if(
				obj.getId() == ID.Goal &&
				getBounds().intersects(((Block) obj).getBounds())
			) {
				AudioPlayer.getSound("win").play();
				((Block) obj).puff();
				opacity = 1;
				playing = false;
				winning = true;
			}
			
			if(
				obj.getId() == ID.BlueButton ||
				obj.getId() == ID.RedButton ||
				obj.getId() == ID.GreenButton ||
				obj.getId() == ID.YellowButton
			) {
				Block button = (Block) obj;
				if(getBounds().intersects(button.getBounds()) && !button.isButtonToggled()) {
					if(obj.getId() == ID.BlueButton) switchedButtons[0] = true;
					if(obj.getId() == ID.RedButton) switchedButtons[1] = true;
					if(obj.getId() == ID.GreenButton) switchedButtons[2] = true;
					if(obj.getId() == ID.YellowButton) switchedButtons[3] = true;
					
					AudioPlayer.getSound("button").play();
					button.toggleButton();
					
					for(int j = 0; j < game.handler.objects.size(); j++) {
						Object newObj = game.handler.objects.get(j);
						if(newObj instanceof Block) {
							Block door = (Block) newObj;
							if(button.getId() == ID.BlueButton && door.getId() == ID.BlueDoor) door.openDoor();
							if(button.getId() == ID.RedButton && door.getId() == ID.RedDoor) door.openDoor();
							if(button.getId() == ID.GreenButton && door.getId() == ID.GreenDoor) door.openDoor();
							if(button.getId() == ID.YellowButton && door.getId() == ID.YellowDoor) door.openDoor();
						}
					}
				}
			}
			
			if(obj.getId() == ID.BubbleParticle) {
				BubbleParticle bubble = (BubbleParticle) obj;
				if(getBounds().intersects(bubble.getBounds())) bubble.pop();
			}
		}
		
		// squashed by moving blocks
		if(
			(topMoving && collisionBottom) ||
			(bottomMoving && collisionTop) ||
			(rightMoving && collisionLeft) ||
			(leftMoving && collisionRight)
		) {
			die(false); 
		}
	}
	
	private void handleKeyControl() {
		if(keyRight || keyLeft) {
			if(onZeroBlock || climbingHorizontalLadder || inWater) {
				maxMoveX = 2;
			} else if(climbingLadder && keyUp) {
				maxMoveX = 1.5f;
			} else if(enemyNearby) {
				maxMoveX = 3;
			} else {
				maxMoveX = 4;
			}
			
			directionChange = keyRight ? 1 : -1;
			
			// hanging on wall
			SIDE side = keyRight ? SIDE.Right : SIDE.Left;
			if(
				touchingSolid(side, false) &&
				!touching(ID.IceBlock, side, false) &&
				!touching(ID.BreakableIceBlock, side, false) &&
				moveY > 0
			) {
				hanging = true;
				if(inWater) {
					moveY = 0.5f;
				} else {
					moveY = 1.4f;
					if(!AudioPlayer.getSound("hang").playing()) {
						AudioPlayer.getSound("hang").play();
					}
				}
			} else {
				hanging = false;
			}
			
			if(inWater && !AudioPlayer.getSound("swimJump").playing()) {
				if(r.nextInt(2) == 1) {
					AudioPlayer.getSound("swimJump").play();
				} else {
					AudioPlayer.getSound("swim").play();
				}
			}
		} else {
			hanging = false;
		}
		
		if(keyUp && !jumping) {
			if(onZeroBlock || standing) {
				jumping = true;
				
				y -= 5;
				if(!onZeroBlock) {
					moveY = enemyNearby ? -8 : -10;
				} else {
					moveY = -2; 
				}
				
				if(!AudioPlayer.getSound("jump").playing()) {
					AudioPlayer.getSound("jump").play();
				}
			}
			
			if(touching(ID.TrampolineBlock, SIDE.Bottom, false)) {
				jumping = true;
				moveY = enemyNearby ? -12 : -15;
				AudioPlayer.getSound("spring").play();
			}
		}
		
		if(keyUp) {
			if(
				climbingLadder &&
				!touchingSolid(SIDE.Top, true) &&
				!climbingHorizontalLadder
			) {
				if(!AudioPlayer.getSound("climb").playing()) {
					AudioPlayer.getSound("climb").play();
				}
				moveY = -1.5f;
			}
			
			if(climbingHorizontalLadder) {
				Block ladder = getTouchedBlock(ID.HorizontalLadderBlock, SIDE.Top);
				if(
					y < ladder.getY() + 27 &&
					y > ladder.getY() + 27 - Math.abs(moveY)
				) {
					// hang on ladder
					y = ladder.getY() + 27;
					moveY = 0;
				}
				if(
					(keyRight || keyLeft) &&
					!touchingSolid(SIDE.Left, true) &&
					!touchingSolid(SIDE.Right, true) &&
					!AudioPlayer.getSound("climb").playing()
				) {
					AudioPlayer.getSound("climb").play(); 
				}
			}
			
			if(inWater) {
				if(!AudioPlayer.getSound("swimJump").playing()) {
					if(r.nextInt(2) == 1) {
						AudioPlayer.getSound("swimJump").play();
					} else {
						AudioPlayer.getSound("swim").play();
					}
				}
				
				jumping = false;
				moveY = -2;
			}
			
			SIDE side = (keyRight ? SIDE.Right : SIDE.Left);
			
			Block breakableBlock = null;
			boolean isBreakableBlock =
				(breakableBlock = (Block) getTouchedBlock(ID.BreakableBrickBlock, side)) != null ||
				(breakableBlock = (Block) getTouchedBlock(ID.BreakableIceBlock, side)) != null ||
				(breakableBlock = (Block) getTouchedBlock(ID.BreakableZeroBlock, side)) != null;
			
			if(
				(isBreakableBlock || touchingSolid(side, false)) &&
				moveY > 0 && (keyRight || keyLeft)
			) {
				kickTimer = 15;
				
				moveX = keyRight ? -kickForceX : kickForceX;
				moveY = -kickForceY;
				
				AudioPlayer.getSound("kick").play();
				
				if(breakableBlock != null) breakableBlock.crumble(true);
			}
		}
		
		if(keyDown && inWater) {
			moveY = 1.5f;
			if(!AudioPlayer.getSound("swimJump").playing()) {
				if(r.nextInt(2) == 1) {
					AudioPlayer.getSound("swimJump").play();
				} else {
					AudioPlayer.getSound("swim").play();
				}
			}
		}
	}
	
	private KeyAdapter initKeyAdapter() {
		return new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_RIGHT) keyRight = true;
				if(key == KeyEvent.VK_LEFT) keyLeft = true;
				if(key == KeyEvent.VK_UP) keyUp = true;
				if(key == KeyEvent.VK_DOWN) keyDown = true;
			}
			
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_RIGHT) keyRight = false;
				if(key == KeyEvent.VK_LEFT) keyLeft = false;
				if(key == KeyEvent.VK_UP) keyUp = false;
				if(key == KeyEvent.VK_DOWN) keyDown = false;
			}
		};
	}
	
	public void die(boolean enemy) {
		if(!enemy) {
			AudioPlayer.getSound("dead").play();
		} else {
			AudioPlayer.getSound("stomp").play();
		}
		
		opacity = 1;
		timer = 50;
		playing = false;
		dying = true;
		
		int angle = 0;
		for(int j = 0; j < 8; j++) {
			game.handler.addObject(new DieParticle(x + 10, y + 10, angle));
			angle += 45;
		}
	}
	
	private boolean touching(Block block, SIDE side, boolean inside) {
		if(inside) {
			if(side == SIDE.Top) {
				if(getTouchTop().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Right) {
				if(getTouchRight().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Bottom) {
				if(getTouchBottom().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Left) {
				if(getTouchLeft().intersects(block.getBounds())) return true;
			} else if(side == SIDE.All){
				if(getBounds().intersects(block.getBounds())) return true;
			}
		} else {
			if(side == SIDE.Top) {
				if(getBoundsTop().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Right) {
				if(getBoundsRight().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Bottom) {
				if(getTouchBottom().intersects(block.getBounds())) return true;
			} else if(side == SIDE.Left) {
				if(getBoundsLeft().intersects(block.getBounds())) return true;
			} else if(side == SIDE.All){
				if(getTouch().intersects(block.getBounds())) return true;
			}
		}
		return false;
	}
	
	private boolean touching(ID id, SIDE side, boolean inside) {
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			if(obj instanceof Block) {
				Block block = (Block) obj;
				if(block.getId() == id && touching(block, side, inside)) return true;
			}
		}
		return false;
	}
	
	private boolean touchingSolid(SIDE side, boolean inside) {
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			if(obj instanceof Block) {
				Block block = (Block) obj;
				if(block.isSolid() && touching(block, side, inside)) return true;
			}
		}
		return false;
	}
	
	private Block getTouchedBlock(ID object, SIDE side) {
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			if(obj.getId() == object && obj instanceof Block) {
				Block block = (Block) obj;
				if(side == SIDE.Top) {
					if(getTouchTop().intersects(block.getBounds())) return block;
				} else if(side == SIDE.Right) {
					if(getTouchRight().intersects(block.getBounds())) return block;
				} else if(side == SIDE.Left) {
					if(getTouchBottom().intersects(block.getBounds())) return block;
				} else if(side == SIDE.All) {
					if(getTouchLeft().intersects(block.getBounds())) return block;
				}
			}
		}
		return null;
	}
	
	public void spawn() {
		spawnState = 0;
	}
	
	public boolean isPermeable() {
		return false;
	}
	
	public void show() {
		opacity = 1;
	}
	
	public float getVelX() {
		return velX;
	}
	
	public float getVelY() {
		return velY;
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
		
		float squeezePos = (getHeight() * scaleY) - getHeight();
		
		AffineTransform tPlayer = new AffineTransform();
		tPlayer.translate(x + hangPos, y - squeezePos + shiver);
		tPlayer.scale(hangSqueeze, scaleY);
		tPlayer.shear(skew, 0);
		g2d.drawImage(player, tPlayer, null);
		
		AffineTransform tEyes = new AffineTransform();
		tEyes.translate(x + 7 - skewEyesPos, y + 10 - squeezePos + shiver);
		g2d.drawImage(playerEyes, tEyes, null);
		
		AffineTransform tPupils = new AffineTransform();
		tPupils.translate(x + 12 + pupilsX - skewEyesPos, y + 12 - squeezePos + shiver + pupilsY);
		g2d.drawImage(playerPupils, tPupils, null);
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public Rectangle getBounds() {
		return new Rectangle((int) x + 2, (int) y + 2, 31, 31);
	}
	
	public Rectangle getBoundsBottom() {
		return new Rectangle((int) x + ((int) getWidth() / 4), (int) y + ((int) getHeight() / 2), (int) getWidth() / 2, (int) getHeight() / 2);
	}
	
	public Rectangle getBoundsTop() {
		return new Rectangle((int) x + ((int) getWidth() / 4), (int) y, (int) getWidth() / 2, (int) getHeight() / 2);
	}
	
	public Rectangle getBoundsRight() {
		return new Rectangle((int) x + getWidth() - 6, (int) y + 10, (int) 7, (int) getHeight() - 20);
	}
	
	public Rectangle getBoundsLeft() {
		return new Rectangle((int) x + 2, (int) y + 10, 7, (int) getHeight() - 20);
	}
	
	public Rectangle getTouch() {
		return new Rectangle((int) x - 2, (int) y - 2, (int) getWidth() + 4, (int) getHeight() + 4);
	}
	
	public Rectangle getTouchBottom() {
		return new Rectangle((int) x + ((int) getWidth() / 4), (int) y + ((int) getHeight() / 2) + 2, (int) getWidth() / 2, (int) getHeight() / 2);
	}
	
	public Rectangle getTouchTop() {
		return new Rectangle((int) x + ((int) getWidth() / 4), (int) y - 2, (int) getWidth() / 2, (int) getHeight() / 2);
	}
	
	public Rectangle getTouchRight() {
		return new Rectangle((int) x + ((int) getWidth() - 5) + 2, (int) y + 10, (int) 5, (int) getHeight() - 20);
	}
	
	public Rectangle getTouchLeft() {
		return new Rectangle((int) x - 2, (int) y + 10, 5, (int) getHeight() - 20);
	}
}