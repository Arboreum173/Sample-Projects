package com.xenonplatformer.objects;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.GameObject;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.Game.STATE;
import com.xenonplatformer.window.Menu.UIElement;

public class Block extends GameObject {
	private Game game;
	
	private float velX = 0, velY = 0;
	
	private float opacity = 1,
		size = 1;
	
	private boolean spawning = false,
		disappearing = false;
	
	private int bubbleTimer = 0, bubbleNext;
	
	private boolean crumbling = false;
	private int crackTimer = 0;
	private int crackState = 0;
	
	private boolean openingDoor = false,
		toggledButton = false;
	
	private float goalFloat = 0;
	private int goalShine = 64;
	private float goalDeg = 0;
	private boolean goalPuff = false;
	
	private HashMap<String, BufferedImage> blocks = new HashMap<String, BufferedImage>();
	private String waterBlockType = "waterDefault";
	
	private CursorBlock cursorBlock;
	
	private Block attachedBlockUp, attachedBlockRight, attachedBlockDown, attachedBlockLeft;
	private HashSet<Block> blockGroup = new HashSet<Block>();
	
	private Random r = new Random();
	
	public Block(int posX, int posY, ID id, BLOCK_DIRECTION movement, Game game) {
		super(posX, posY, id, game);
		
		this.movement = movement;
		this.game = game;
		
		if(id == ID.BrickBlock) this.blocks.put("brick", Game.getSprite(ID.BrickBlock));
		else if(id == ID.SpikeBlock) this.blocks.put("spike", Game.getSprite(ID.SpikeBlock));
		else if(id == ID.IceBlock) this.blocks.put("ice", Game.getSprite(ID.IceBlock));
		else if(id == ID.ZeroBlock) this.blocks.put("zero", Game.getSprite(ID.ZeroBlock));
		else if(id == ID.TrampolineBlock) this.blocks.put("trampoline", Game.getSprite(ID.TrampolineBlock));
		else if(id == ID.LadderBlock) this.blocks.put("ladder", Game.getSprite(ID.LadderBlock));
		else if(id == ID.HorizontalLadderBlock) this.blocks.put("horizontalLadder", Game.getSprite(ID.HorizontalLadderBlock));
		else if(id == ID.RightConveyorBlock) this.blocks.put("rightConveyor", Game.getSprite(ID.RightConveyorBlock));
		else if(id == ID.LeftConveyorBlock) this.blocks.put("leftConveyor", Game.getSprite(ID.LeftConveyorBlock));
		
		else if(id == ID.BlueDoor) this.blocks.put("blueDoor", Game.getSprite(ID.BlueDoor));
		else if(id == ID.RedDoor) this.blocks.put("redDoor", Game.getSprite(ID.RedDoor));
		else if(id == ID.GreenDoor) this.blocks.put("greenDoor", Game.getSprite(ID.GreenDoor));
		else if(id == ID.YellowDoor) this.blocks.put("yellowDoor", Game.getSprite(ID.YellowDoor));
		
		else if(id == ID.BlueButton) this.blocks.put("blueButton", Game.getSprite(ID.BlueButton));
		else if(id == ID.RedButton) this.blocks.put("redButton", Game.getSprite(ID.RedButton));
		else if(id == ID.GreenButton) this.blocks.put("greenButton", Game.getSprite(ID.GreenButton));
		else if(id == ID.YellowButton) this.blocks.put("yellowButton", Game.getSprite(ID.YellowButton));
		
		else if(id == ID.BreakableBrickBlock) {
			this.blocks.put("brickCrack0", Game.getSprite(ID.BreakableBrickBlock));
			this.blocks.put("brickCrack1", Game.spriteSheet.getSubimage(160, 96, 32, 32));
			this.blocks.put("brickCrack2", Game.spriteSheet.getSubimage(192, 96, 32, 32));
			this.blocks.put("brickCrumble1", Game.spriteSheet.getSubimage(96, 160, 32, 32));
			this.blocks.put("brickCrumble2", Game.spriteSheet.getSubimage(128, 160, 32, 32));
			this.blocks.put("brickCrumble3", Game.spriteSheet.getSubimage(160, 160, 32, 32));
		}
		
		else if(id == ID.BreakableIceBlock) {
			this.blocks.put("iceCrack0", Game.getSprite(ID.BreakableIceBlock));
			this.blocks.put("iceCrack1", Game.spriteSheet.getSubimage(128, 128, 32, 32));
			this.blocks.put("iceCrack2", Game.spriteSheet.getSubimage(160, 128, 32, 32));
			this.blocks.put("iceCrumble1", Game.spriteSheet.getSubimage(192, 160, 32, 32));
			this.blocks.put("iceCrumble2", Game.spriteSheet.getSubimage(0, 192, 32, 32));
			this.blocks.put("iceCrumble3", Game.spriteSheet.getSubimage(32, 192, 32, 32));
		}
		
		else if(id == ID.BreakableZeroBlock) {
			this.blocks.put("zeroCrack0", Game.getSprite(ID.BreakableZeroBlock));
			this.blocks.put("zeroCrack1", Game.spriteSheet.getSubimage(32, 96, 32, 32));
			this.blocks.put("zeroCrack2", Game.spriteSheet.getSubimage(64, 96, 32, 32));
			this.blocks.put("zeroCrumble1", Game.spriteSheet.getSubimage(64, 192, 32, 32));
			this.blocks.put("zeroCrumble2", Game.spriteSheet.getSubimage(92, 192, 32, 32));
			this.blocks.put("zeroCrumble3", Game.spriteSheet.getSubimage(128, 192, 32, 32));
		}
		
		else if(id == ID.WaterBlock) {
			this.blocks.put("waterDefault", Game.spriteSheet.getSubimage(192, 128, 32, 32));
			
			this.blocks.put("waterTop", Game.spriteSheet.getSubimage(160, 192, 32, 32));
			this.blocks.put("waterRight", Game.spriteSheet.getSubimage(192, 192, 32, 32));
			this.blocks.put("waterBottom", Game.spriteSheet.getSubimage(0, 224, 32, 32));
			this.blocks.put("waterLeft", Game.spriteSheet.getSubimage(32, 224, 32, 32));
			
			this.blocks.put("waterTopRight", Game.spriteSheet.getSubimage(64, 224, 32, 32));
			this.blocks.put("waterBottomRight", Game.spriteSheet.getSubimage(96, 224, 32, 32));
			this.blocks.put("waterBottomLeft", Game.spriteSheet.getSubimage(128, 224, 32, 32));
			this.blocks.put("waterTopLeft", Game.spriteSheet.getSubimage(160, 224, 32, 32));
			this.blocks.put("waterTopBottom", Game.spriteSheet.getSubimage(192, 224, 32, 32));
			this.blocks.put("waterRightLeft", Game.spriteSheet.getSubimage(0, 256, 32, 32));
			
			this.blocks.put("waterNotBottom", Game.spriteSheet.getSubimage(32, 256, 32, 32));
			this.blocks.put("waterNotLeft", Game.spriteSheet.getSubimage(64, 256, 32, 32));
			this.blocks.put("waterNotTop", Game.spriteSheet.getSubimage(96, 256, 32, 32));
			this.blocks.put("waterNotRight", Game.spriteSheet.getSubimage(128, 256, 32, 32));
			
			this.blocks.put("waterAll", Game.getSprite(ID.WaterBlock));
			
			bubbleNext = r.nextInt(1000) + 1;
		}
		
		else if(id == ID.StopBlock) this.blocks.put("stop", Game.getSprite(ID.StopBlock));
		
		else if(id == ID.Goal) this.blocks.put("goal", Game.getSprite(ID.Goal));
		
		velX = 0;
		velY = 0;
		
		if(movement == BLOCK_DIRECTION.up) velY = -1.5f;
		if(movement == BLOCK_DIRECTION.down) velY = 1.5f;
		if(movement == BLOCK_DIRECTION.right) velX = 1.5f;
		if(movement == BLOCK_DIRECTION.left) velX = -1.5f;
		
		this.cursorBlock = (CursorBlock) UIElement.find(game.menu.builderObjects, "cursorBlock").getObject();
	}
	
	public void tick() {
		if(Game.state == STATE.Game) {
			if(movement != BLOCK_DIRECTION.none && game.player.isPlaying()) {
				if(movement == BLOCK_DIRECTION.up || movement == BLOCK_DIRECTION.down) {
					y += velY;
				} else if(movement == BLOCK_DIRECTION.right || movement == BLOCK_DIRECTION.left) {
					x += velX;
				}
				
				game.loadAdjacentBlock(posX, posY, movement);
			}
			
			if(crumbling) {
				crumble(false);
			} else if(openingDoor) {
				if(opacity > 0) {
					opacity -= 0.1;
					if(opacity < 0) opacity = 0;
					size -= 0.05;
				} else {
					game.handler.removeObject(this);
				}
			} else if(id == ID.WaterBlock) {
				bubbleTimer++;
				if(bubbleTimer == bubbleNext) {
					bubbleTimer = 0;
					bubbleNext = r.nextInt(1000) + 1;
					game.handler.addObject(new BubbleParticle(x + 12, y + 12, game.handler));
				}
			} else if(id == ID.Goal) {
				goalFloat += 0.1;
				goalShine = (int) (64 * (1 + (Math.abs(Math.sin(goalFloat)) / 5)));
				goalDeg = 50 * (float) (1 + (Math.sin(goalFloat) / 5)) - 50;
				if(goalFloat > 6.2) goalFloat = 0;
				if(goalPuff) {
					if(opacity > 0) {
						size += 0.05;
						opacity -= 0.1;
						if(opacity < 0) opacity = 0;
					} else {
						goalPuff = false;
					}
				}
			}
		}
		
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
				int index = (getPosY() * (int) Game.levelData.get("width")) + getPosX();
				Game.levelData.getJSONArray("map").put(index, 0);
				game.handler.removeObject(this);
			}
		}
	}
	
	public void setAdjacentMovingBlocks() {
		Block blockUp = game.getBlockAt(posX, posY - 1);
		if(blockUp != null && blockUp.getMovement() == movement) attachedBlockUp = blockUp;
		
		Block blockRight = game.getBlockAt(posX + 1, posY);
		if(blockRight != null && blockRight.getMovement() == movement) attachedBlockRight = blockRight;
		
		Block blockDown = game.getBlockAt(posX, posY + 1);
		if(blockDown != null && blockDown.getMovement() == movement) attachedBlockDown = blockDown;
		
		Block blockLeft = game.getBlockAt(posX - 1, posY);
		if(blockLeft != null && blockLeft.getMovement() == movement) attachedBlockLeft = blockLeft;
	}
	
	public boolean isColliding() {
		for(int i = 0; i < game.handler.objects.size(); i++) {
			Object obj = game.handler.objects.get(i);
			if(obj != this && obj instanceof Block) {
				Block block = (Block) obj;
				if(
					getBounds().intersects(block.getBounds()) &&
					block.getMovement() != movement
				) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void collide(BLOCK_DIRECTION direction) {
		for(Block block : blockGroup) {
			block.setDirection(direction);
		}
	}
	
	public BLOCK_DIRECTION getCollisionDirection() {
		if(movement == BLOCK_DIRECTION.up) {
			return BLOCK_DIRECTION.down;
		} else if(movement == BLOCK_DIRECTION.right) {
			return BLOCK_DIRECTION.left;
		} else if(movement == BLOCK_DIRECTION.down) {
			return BLOCK_DIRECTION.up;
		} else if(movement == BLOCK_DIRECTION.left) {
			return BLOCK_DIRECTION.right;
		}
		return BLOCK_DIRECTION.none;
	}
	
	private void setDirection(BLOCK_DIRECTION direction) {
		if(direction == BLOCK_DIRECTION.up) {
			velY = -Math.abs(velY);
		} else if(direction == BLOCK_DIRECTION.right) {
			velX = Math.abs(velX);
		} else if(direction == BLOCK_DIRECTION.down) {
			velY = Math.abs(velY);
		} else if(direction == BLOCK_DIRECTION.left) {
			velX = -Math.abs(velX);
		}
		
		movement = direction;
	}
	
	public void findAttachedBlocks() {
		game.unprocessedBlocks.remove(this);
		game.processedBlocks.add(this);
		
		if(attachedBlockUp != null && game.unprocessedBlocks.contains(attachedBlockUp)) {
			attachedBlockUp.findAttachedBlocks();
		}
		if(attachedBlockRight != null && game.unprocessedBlocks.contains(attachedBlockRight)) {
			attachedBlockRight.findAttachedBlocks();
		}
		if(attachedBlockDown != null && game.unprocessedBlocks.contains(attachedBlockDown)) {
			attachedBlockDown.findAttachedBlocks();
		}
		if(attachedBlockLeft != null && game.unprocessedBlocks.contains(attachedBlockLeft)) {
			attachedBlockLeft.findAttachedBlocks();
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		AffineTransform t = new AffineTransform();
		t.translate(x - ((32 * size - 32) / 2), y - ((32 * size - 32) / 2));
		t.scale(size, size);
		
		if(id == ID.BrickBlock) { g2d.drawImage(blocks.get("brick"), t, null); }
		else if(id == ID.SpikeBlock) { g2d.drawImage(blocks.get("spike"), t, null); }
		else if(id == ID.IceBlock) { g2d.drawImage(blocks.get("ice"), t, null); }
		else if(id == ID.ZeroBlock) { g2d.drawImage(blocks.get("zero"), t, null); }
		else if(id == ID.TrampolineBlock) { g2d.drawImage(blocks.get("trampoline"), t, null); }
		else if(id == ID.LadderBlock) { g2d.drawImage(blocks.get("ladder"), t, null); }
		else if(id == ID.HorizontalLadderBlock) { g2d.drawImage(blocks.get("horizontalLadder"), t, null); }
		else if(id == ID.RightConveyorBlock) { g2d.drawImage(blocks.get("rightConveyor"), t, null); }
		else if(id == ID.LeftConveyorBlock) { g2d.drawImage(blocks.get("leftConveyor"), t, null); }
		
		else if(id == ID.BlueDoor) { g2d.drawImage(blocks.get("blueDoor"), t, null); }
		else if(id == ID.RedDoor) { g2d.drawImage(blocks.get("redDoor"), t, null); }
		else if(id == ID.GreenDoor) { g2d.drawImage(blocks.get("greenDoor"), t, null); }
		else if(id == ID.YellowDoor) { g2d.drawImage(blocks.get("yellowDoor"), t, null); }
		
		else if(id == ID.BlueButton) {
			if(toggledButton) {
				g.setColor(new Color(0, 140, 255, 40));
				g.fillOval((int) x - 9, (int) y - 9, 50, 50);
			}
			g.drawImage(blocks.get("blueButton"), (int) x, (int) y, null);
		} else if(id == ID.RedButton) {
			if(toggledButton) {
				g.setColor(new Color(255, 0, 0, 40));
				g.fillOval((int) x - 9, (int) y - 9, 50, 50);
			}
			g.drawImage(blocks.get("redButton"), (int) x, (int) y, null);
		} else if(id == ID.GreenButton) {
			if(toggledButton) {
				g.setColor(new Color(0, 191, 22, 40));
				g.fillOval((int) x - 9, (int) y - 9, 50, 50);
			}
			g.drawImage(blocks.get("greenButton"), (int) x, (int) y, null);
		} else if(id == ID.YellowButton) {
			if(toggledButton) {
				g.setColor(new Color(255, 204, 0, 40));
				g.fillOval((int) x - 9, (int) y - 9, 50, 50);
			}
			g.drawImage(blocks.get("yellowButton"), (int) x, (int) y, null);
		}
		
		else if(id == ID.BreakableBrickBlock) {
			if(crackState == 0) {
				g2d.drawImage(blocks.get("brickCrack0"), t, null);
			} else if(crackState == 1) {
				g2d.drawImage(blocks.get("brickCrack1"), t, null);
			} else if(crackState == 2) {
				g2d.drawImage(blocks.get("brickCrack2"), t, null);
			} else if(crackState == 3) {
				g2d.drawImage(blocks.get("brickCrumble1"), t, null);
			} else if(crackState == 4) {
				g2d.drawImage(blocks.get("brickCrumble2"), t, null);
			} else if(crackState == 5) {
				g2d.drawImage(blocks.get("brickCrumble3"), t, null);
			} 
		} else if(id == ID.BreakableIceBlock) {
			if(crackState == 0) {
				g2d.drawImage(blocks.get("iceCrack0"), t, null);
			} else if(crackState == 1) {
				g2d.drawImage(blocks.get("iceCrack1"), t, null);
			} else if(crackState == 2) {
				g2d.drawImage(blocks.get("iceCrack2"), t, null);
			} else if(crackState == 3) {
				g2d.drawImage(blocks.get("iceCrumble1"), t, null);
			} else if(crackState == 4) {
				g2d.drawImage(blocks.get("iceCrumble2"), t, null);
			} else if(crackState == 5) {
				g2d.drawImage(blocks.get("iceCrumble3"), t, null);
			} 
		} else if(id == ID.BreakableZeroBlock) {
			if(crackState == 0) {
				g2d.drawImage(blocks.get("zeroCrack0"), t, null);
			} else if(crackState == 1) {
				g2d.drawImage(blocks.get("zeroCrack1"), t, null);
			} else if(crackState == 2) {
				g2d.drawImage(blocks.get("zeroCrack2"), t, null);
			} else if(crackState == 3) {
				g2d.drawImage(blocks.get("zeroCrumble1"), t, null);
			} else if(crackState == 4) {
				g2d.drawImage(blocks.get("zeroCrumble2"), t, null);
			} else if(crackState == 5) {
				g2d.drawImage(blocks.get("zeroCrumble3"), t, null);
			} 
		}
		
		else if(id == ID.WaterBlock) {
			g2d.drawImage(this.blocks.get(waterBlockType), t, null);
		}
		
		else if(id == ID.StopBlock) {
			g2d.drawImage(blocks.get("stop"), t, null); 
		}
		
		else if(id == ID.Goal) {
			g.setColor(new Color(255, 204, 0, 40));
			g.fillOval((int) x - ((goalShine - 32) / 2), (int) y - ((goalShine - 32) / 2), goalShine, goalShine);
			t.rotate(Math.toRadians(goalDeg), 17, 17);
			g2d.drawImage(blocks.get("goal"), t, null);
		}
		
		if(movement != BLOCK_DIRECTION.none && Game.state == STATE.Builder) {
			g2d.drawImage(cursorBlock.blockDirectionArrows.get(movement), (int) x + 10, (int) y + 10, null);
		}
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public void crack(int intensity) {
		if(
			(id == ID.BreakableBrickBlock ||
			id == ID.BreakableIceBlock ||
			id == ID.BreakableZeroBlock) &&
			!crumbling
		) {
			crackTimer += intensity;
			
			int oldState = crackState;
			if(crackTimer < 30) {
				crackState = 0;
			} else if(crackTimer >= 30 && crackTimer < 60) {
				crackState = 1;
				if(oldState != 1) AudioPlayer.getSound("crack").play(); 
			} else if(crackTimer >= 60 && crackTimer < 90) {
				crackState = 2;
				if(oldState != 2) AudioPlayer.getSound("crack").play(); 
			} else if(crackTimer >= 90) {
				startCrumble();
				crackTimer = 90;
				crumbling = true;
			}
		}
	}
	
	public void crumble(boolean start) {
		if(
			id == ID.BreakableBrickBlock ||
			id == ID.BreakableIceBlock ||
			id == ID.BreakableZeroBlock
		) {
			if(start && !crumbling) startCrumble();
			
			crackTimer++;
			if(crackTimer >= 90 && crackTimer < 93) {
				crackState = 3;
			} else if(crackTimer >= 93 && crackTimer < 96) {
				crackState = 4;
			} else if(crackTimer >= 96 && crackTimer < 99) {
				crackState = 5;
			} else if(crackTimer == 99) {
				game.handler.removeObject(this);
			}
		}
	}
	
	private void startCrumble() {
		if(
			id == ID.BreakableBrickBlock ||
			id == ID.BreakableIceBlock ||
			id == ID.BreakableZeroBlock
		) {
			if(id == ID.BreakableIceBlock) {
				AudioPlayer.getSound("breakIce").play();
			} else {
				AudioPlayer.getSound("break").play();
			}
			crackTimer = 90;
			crumbling = true;
		}
	}
	
	public void puff() {
		if(id == ID.Goal) goalPuff = true;
	}
	
	public void toggleButton() {
		if(
			id == ID.BlueButton ||
			id == ID.RedButton ||
			id == ID.GreenButton ||
			id == ID.YellowButton
		) {
			toggledButton = true;
		}
	}
	
	public boolean isButtonToggled() {
		if(
			id == ID.BlueButton ||
			id == ID.RedButton ||
			id == ID.GreenButton ||
			id == ID.YellowButton
		) {
			return toggledButton;
		}
		return false;
	}
	
	public void openDoor() {
		if(
			id == ID.BlueDoor ||
			id == ID.RedDoor ||
			id == ID.GreenDoor ||
			id == ID.YellowDoor
		) {
			openingDoor = true;
		}
	}
	
	public boolean isSolid() {
		return (
			id == ID.BrickBlock ||
			id == ID.IceBlock ||
			id == ID.ZeroBlock ||
			id == ID.TrampolineBlock ||
			id == ID.RightConveyorBlock ||
			id == ID.LeftConveyorBlock ||
			id == ID.BlueDoor ||
			id == ID.RedDoor ||
			id == ID.GreenDoor ||
			id == ID.YellowDoor ||
			id == ID.BreakableBrickBlock ||
			id == ID.BreakableIceBlock ||
			id == ID.BreakableZeroBlock
		);
	}
	
	public boolean isPermeable() {
		return id == ID.WaterBlock || id == ID.StopBlock;
	}
	
	public float getVelX() {
		return velX;
	}
	
	public float getVelY() {
		return velY;
	}
	
	public void spawn() {
		opacity = 0;
		size = 0.3f;
		spawning = true;
		game.handler.addObject(this, 1);
	}
	
	public void disappear() {
		disappearing = true;
	}
	
	public void setBlockGroup(HashSet<Block> blockGroup) {
		this.blockGroup = blockGroup;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, 32, 32);
	}
}