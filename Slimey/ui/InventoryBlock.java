package com.xenonplatformer.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.xenonplatformer.framework.Clickable;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.window.AudioPlayer;
import com.xenonplatformer.window.Game;
import com.xenonplatformer.window.ImageBrightness;
import com.xenonplatformer.window.Game.STATE;

public class InventoryBlock extends Clickable {
	private ID type;
	private Game game;
	private BufferedImage block, blockHovered;
	private BufferedImage counter;
	private float size = 1;
	private boolean selected = false;
	
	public InventoryBlock(float x, float y, ID type, Game game) {
		super(x, y, ID.InventoryBlock, game);
		
		this.type = type;
		this.game = game;
		
		if(type == ID.Player) this.block = Game.getSprite(ID.Player); 
		
		else if(type == ID.BrickBlock) this.block = Game.getSprite(ID.BrickBlock);
		else if(type == ID.SpikeBlock) this.block = Game.getSprite(ID.SpikeBlock);
		else if(type == ID.IceBlock) this.block = Game.getSprite(ID.IceBlock);
		else if(type == ID.ZeroBlock) this.block = Game.getSprite(ID.ZeroBlock);
		else if(type == ID.TrampolineBlock) this.block = Game.getSprite(ID.TrampolineBlock);
		else if(type == ID.LadderBlock) this.block = Game.getSprite(ID.LadderBlock);
		else if(type == ID.HorizontalLadderBlock) this.block = Game.getSprite(ID.HorizontalLadderBlock);
		else if(type == ID.RightConveyorBlock) this.block = Game.getSprite(ID.RightConveyorBlock);
		else if(type == ID.LeftConveyorBlock) this.block = Game.getSprite(ID.LeftConveyorBlock);
		
		else if(type == ID.BlueDoor) this.block = Game.getSprite(ID.BlueDoor);
		else if(type == ID.RedDoor) this.block = Game.getSprite(ID.RedDoor);
		else if(type == ID.GreenDoor) this.block = Game.getSprite(ID.GreenDoor);
		else if(type == ID.YellowDoor) this.block = Game.getSprite(ID.YellowDoor);
		
		else if(type == ID.BlueButton) this.block = Game.getSprite(ID.BlueButton);
		else if(type == ID.RedButton) this.block = Game.getSprite(ID.RedButton);
		else if(type == ID.GreenButton) this.block = Game.getSprite(ID.GreenButton);
		else if(type == ID.YellowButton) this.block = Game.getSprite(ID.YellowButton);
		
		else if(type == ID.BreakableBrickBlock) this.block = Game.getSprite(ID.BreakableBrickBlock);
		else if(type == ID.BreakableIceBlock) this.block = Game.getSprite(ID.BreakableIceBlock);
		else if(type == ID.BreakableZeroBlock) this.block = Game.getSprite(ID.BreakableZeroBlock);
		
		else if(type == ID.WaterBlock) this.block = Game.getSprite(ID.WaterBlock);
		
		else if(type == ID.Enemy) this.block = Game.getSprite(ID.Enemy);
		
		else if(type == ID.StopBlock) this.block = Game.getSprite(ID.StopBlock);
		
		else if(type == ID.Goal) this.block = Game.getSprite(ID.Goal);
		
		blockHovered = ImageBrightness.change(this.block, 0.92f);
		
		counter = Game.ui.getSubimage(56, 33, 42, 14);
		
		setDimensions();
	}
	
	protected MouseAdapter initMouseAdapter() {
		return new MouseAdapter() {
	        public void mouseClicked(MouseEvent event) {
	        	if(Game.state != STATE.Builder || Game.transitioning || !isHovered()) return;
	    		
	        	AudioPlayer.getSound("click").play();
	    		game.menu.builderPut = type;
	        }
	    };
	}
	
	public void tick() {
		selected = game.menu.builderPut == type;
		if(selected && size > 0.85) {
			size -= 0.03;
		} else if(!selected && size < 1) {
			size += 0.03;
			if(size > 1) size = 1;
		}
	}
	
	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform t = new AffineTransform();
		t.translate(x + 5 - ((32 * size - 32) / 2), y - ((32 * size - 32) / 2));
		t.scale(size, size);
		g2d.drawImage(isHovered() || selected ? blockHovered : block, t, null);
		g.drawImage(counter, (int) x, (int) y + 36, null);
	}
	
	public void onAdded() { }
	public void onRemoved() { }
	
	protected void setDimensions() {
		width = 42;
		height = 42;
	}
	
	public ID getType() {
		return type;
	}
}