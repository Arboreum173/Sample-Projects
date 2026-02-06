package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.ID;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.window.Game;

public class BlockImage extends Object {
	private BufferedImage block;
	
	public BlockImage(float x, float y, ID id) {
		super(x, y, ID.BlockImage);
		
		if(id == ID.BrickBlock) { this.block = Game.spriteSheet.getSubimage(96, 0, 32, 32); }
		else if(id == ID.SpikeBlock) { this.block = Game.spriteSheet.getSubimage(128, 0, 32, 32); }
		else if(id == ID.IceBlock) { this.block = Game.spriteSheet.getSubimage(160, 0, 32, 32); }
		else if(id == ID.ZeroBlock) { this.block = Game.spriteSheet.getSubimage(192, 0, 32, 32); }
		else if(id == ID.TrampolineBlock) { this.block = Game.spriteSheet.getSubimage(96, 32, 32, 32); }
		else if(id == ID.LadderBlock) { this.block = Game.spriteSheet.getSubimage(128, 32, 32, 32); }
		else if(id == ID.HorizontalLadderBlock) { this.block = Game.spriteSheet.getSubimage(160, 32, 32, 32); }
		else if(id == ID.RightConveyorBlock) { this.block = Game.spriteSheet.getSubimage(192, 32, 32, 32); }
		else if(id == ID.LeftConveyorBlock) { this.block = Game.spriteSheet.getSubimage(96, 64, 32, 32); }
		
		else if(id == ID.BlueDoor) { this.block = Game.spriteSheet.getSubimage(128, 64, 32, 32); }
		else if(id == ID.RedDoor) { this.block = Game.spriteSheet.getSubimage(160, 64, 32, 32); }
		else if(id == ID.GreenDoor) { this.block = Game.spriteSheet.getSubimage(192, 64, 32, 32); }
		else if(id == ID.YellowDoor) { this.block = Game.spriteSheet.getSubimage(96, 96, 32, 32); }
		
		else if(id == ID.BreakableBrickBlock) { this.block = Game.spriteSheet.getSubimage(128, 96, 32, 32); }
		else if(id == ID.BreakableIceBlock) { this.block = Game.spriteSheet.getSubimage(96, 128, 32, 32); }
		else if(id == ID.BreakableZeroBlock) { this.block = Game.spriteSheet.getSubimage(0, 96, 32, 32); }
		
		else if(id == ID.WaterBlock) { this.block = Game.spriteSheet.getSubimage(192, 128, 32, 32); }
		
		else if(id == ID.Goal) { this.block = Game.spriteSheet.getSubimage(0, 0, 32, 32); }
	}
	
	public void tick() { }
	
	public void render(Graphics g) {
		g.drawImage(block, (int) x, (int) y, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	public int getWidth() {
		return block.getWidth();
	}
	
	public int getHeight() {
		return block.getHeight();
	}
}