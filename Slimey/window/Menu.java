package com.xenonplatformer.window;

import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.objects.CursorBlock;
import com.xenonplatformer.objects.StarParticle;
import com.xenonplatformer.ui.BackButton;
import com.xenonplatformer.ui.Bar;
import com.xenonplatformer.ui.BlockMoveButton;
import com.xenonplatformer.ui.EraserButton;
import com.xenonplatformer.ui.Globe;
import com.xenonplatformer.ui.GlobePlayer;
import com.xenonplatformer.ui.InventoryBlock;
import com.xenonplatformer.ui.InventoryScroll;
import com.xenonplatformer.ui.LevelGrid;
import com.xenonplatformer.ui.LevelItem;
import com.xenonplatformer.ui.MenuBar;
import com.xenonplatformer.ui.MenuButton;
import com.xenonplatformer.ui.PlayButton;
import com.xenonplatformer.ui.StopButton;
import com.xenonplatformer.window.Game.STATE;

public class Menu {
	private Game game;
	
	private boolean launched = false;
	private enum TRANSITION {
		builderPlayStart,
		builderPlayEnd
	};
	private TRANSITION transition = null;
	
	private LinkedList<UIElement> menuObjects = new LinkedList<UIElement>();
	private LinkedList<UIElement> campaignObjects = new LinkedList<UIElement>();
	
	public ID builderPut = ID.BrickBlock;
	public BLOCK_DIRECTION builderBlockDirection = BLOCK_DIRECTION.none;
	
	public LinkedList<UIElement> builderObjects = new LinkedList<UIElement>();
	public StopButton builderStopButton;
	public ScrollList inventory;
	
	public float opacity = 0;
	private int timer = 0;
	private int starTimer = 0;
	
	private ScrollList campaignLevels;
	
	public Menu(Game game) {
		this.game = game;
	}
	
	public void init() {
		/* --- Menu --- */
		menuObjects.add(new UIElement("globe", new Globe(), 1));
		menuObjects.add(new UIElement("globePlayer", new GlobePlayer((Globe) UIElement.find(menuObjects, "globe").getObject()), 2));
		menuObjects.add(new UIElement("menuBar", new MenuBar(this), 2));
		menuObjects.add(new UIElement("campaignButton", new MenuButton(130, 420, FileReader.loadImage("/campaign_icon.png"), "Campaign", STATE.Menu, new Callable<Void>() {
			public Void call() {
				game.transition.show(STATE.Levels, new Callable<Void>() {
					public Void call() {
						unload(STATE.Menu);
						load(STATE.Levels);
						return null;
					}
				});
				return null;
			}
		}, game), 3));
		menuObjects.add(new UIElement("builderButton", new MenuButton(300, 420, FileReader.loadImage("/builder_icon.png"), "Builder", STATE.Menu, new Callable<Void>() {
			public Void call() {
				game.transition.show(STATE.Builder, new Callable<Void>() {
					public Void call() {
						unload(STATE.Menu);
						game.handler.clearObjectsWithId(ID.StarParticle);
						
						load(STATE.Builder);
						game.loadLevelDraft(1);
						game.player.show();
						return null;
					}
				});
				return null;
			}
		}, game), 3));
		
		/* --- Campaign --- */
		Bar campaignBar = new Bar(Bar.TYPE.top, game);
		campaignBar.add(new Label(true, "Campaign", 350, 50, Color.WHITE, true, true), 3);
		campaignBar.add(new BackButton(35, 12, STATE.Levels, new Callable<Void>() {
			public Void call() {
				game.transition.show(STATE.Menu, new Callable<Void>() {
					public Void call() {
						unload(STATE.Levels);
						load(STATE.Menu);
						return null;
					}
				});
				return null;
			}
		}, game), 3);
		campaignObjects.add(new UIElement("campaignBar", campaignBar, 2));
		
		campaignLevels = new ScrollList(ScrollList.DIRECTION.vertical, 34, 110, 732, 490, 16, 1, game);
		
		/* --- Builder --- */
		Bar builderInventoryBar = new Bar(Bar.TYPE.inventory, game);
		
		builderInventoryBar.add(new BackButton(10, 12, STATE.Builder, new Callable<Void>() {
			public Void call() {
				game.transition.show(STATE.Menu, new Callable<Void>() {
					public Void call() {
						Game.saveAsDraft();
						game.handler.clearObjects();
						unload(STATE.Builder);
						load(STATE.Menu);
						return null;
					}
				});
				return null;
			}
		}, game), 6);
		
		inventory = new ScrollList(ScrollList.DIRECTION.horizontal, 257, 20, 314, 50, 3, 4, game);
		
		inventory.add(new InventoryBlock(0, 0, ID.Player, game));
		inventory.add(new InventoryBlock(0, 0, ID.Goal, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.BrickBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.SpikeBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.IceBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.ZeroBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.TrampolineBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.LadderBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.HorizontalLadderBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.RightConveyorBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.LeftConveyorBlock, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.BlueDoor, game));
		inventory.add(new InventoryBlock(0, 0, ID.RedDoor, game));
		inventory.add(new InventoryBlock(0, 0, ID.GreenDoor, game));
		inventory.add(new InventoryBlock(0, 0, ID.YellowDoor, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.BlueButton, game));
		inventory.add(new InventoryBlock(0, 0, ID.RedButton, game));
		inventory.add(new InventoryBlock(0, 0, ID.GreenButton, game));
		inventory.add(new InventoryBlock(0, 0, ID.YellowButton, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.BreakableBrickBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.BreakableIceBlock, game));
		inventory.add(new InventoryBlock(0, 0, ID.BreakableZeroBlock, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.WaterBlock, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.Enemy, game));
		
		inventory.add(new InventoryBlock(0, 0, ID.StopBlock, game));
		
		builderInventoryBar.add(inventory);
		builderInventoryBar.add(new Bar.InventoryBackground(), 3);
		builderInventoryBar.add(new EraserButton(147, 28, game), 7);
		builderInventoryBar.add(new InventoryScroll(219, 28, false, game), 7);
		builderInventoryBar.add(new InventoryScroll(587, 28, true, game), 7);
		
		builderInventoryBar.add(new BlockMoveButton(628, 28, BLOCK_DIRECTION.up, game), 5);
		builderInventoryBar.add(new BlockMoveButton(662, 28, BLOCK_DIRECTION.right, game), 5);
		builderInventoryBar.add(new BlockMoveButton(697, 28, BLOCK_DIRECTION.down, game), 5);
		builderInventoryBar.add(new BlockMoveButton(732, 28, BLOCK_DIRECTION.left, game), 5);
		
		builderStopButton = new StopButton(STATE.Game, game, STATE.Builder);
		
		Bar builderBottomBar = new Bar(Bar.TYPE.bottom, game);
		builderBottomBar.add(new PlayButton(STATE.Builder, game), 5);
		
		builderObjects.add(new UIElement("inventoryBar", builderInventoryBar, 5));
		builderObjects.add(new UIElement("bottomBar", builderBottomBar, 5));
		builderObjects.add(new UIElement("levelGrid", new LevelGrid(game)));
		builderObjects.add(new UIElement("cursorBlock", new CursorBlock(game), 2));
	}
	
	public void load(STATE state) {
		if(state == STATE.Menu) {
			for(int i = 0; i < menuObjects.size(); i++) {
				UIElement element = menuObjects.get(i);
				game.handler.addObject(element.getObject(), element.getPriority());
			}
		} else if(state == STATE.Levels) {
			for(int i = 0; i < campaignObjects.size(); i++) {
				UIElement element = campaignObjects.get(i);
				game.handler.addObject(element.getObject(), element.getPriority());
			}
			
			for(int i = 0; i < Game.levelCount; i++) {
				campaignLevels.add(new LevelItem(0, 0, "Level " + (i + 1), i + 1, STATE.Levels, game));
			}
			
			game.handler.addObject(campaignLevels);
		} else if(state == STATE.Builder) {
			for(int i = 0; i < builderObjects.size(); i++) {
				UIElement element = builderObjects.get(i);
				game.handler.addObject(element.getObject(), element.getPriority());
			}
		}
	}
	
	public void unload(STATE state) {
		if(state == STATE.Menu) {
			for(int i = 0; i < menuObjects.size(); i++) {
				UIElement element = menuObjects.get(i);
				game.handler.removeObject(element.getObject());
			}
		} else if(state == STATE.Levels) {
			for(int i = 0; i < campaignObjects.size(); i++) {
				UIElement element = campaignObjects.get(i);
				game.handler.removeObject(element.getObject());
			}
			
			game.handler.removeObject(campaignLevels);
		} else if(state == STATE.Builder) {
			for(int i = 0; i < builderObjects.size(); i++) {
				UIElement element = builderObjects.get(i);
				game.handler.removeObject(element.getObject());
			}
		}
	}
	
	public void tick() {
		if(Game.state == STATE.Menu && !launched) {
			// launch game
			if(timer < 100) {
				timer++;
			} else if(opacity < 1) {
				opacity += 0.1;
				if(opacity > 1) opacity = 1;
			} else {
				launched = true; 
			}
		}
		
		if(Game.state == STATE.Menu || Game.state == STATE.Levels) {
			// spawn background stars
			starTimer++;
			if(starTimer == 3) {
				starTimer = 0;
				game.handler.addObject(new StarParticle(game.handler));
			}
		}
		
		if(
			transition == TRANSITION.builderPlayStart ||
			transition == TRANSITION.builderPlayEnd
		) {
			int speed = 9;
			boolean collapse = (transition == TRANSITION.builderPlayStart);
			
			Bar inventoryBar = (Bar) UIElement.find(builderObjects, "inventoryBar").getObject();
			Bar bottomBar = (Bar) UIElement.find(builderObjects, "bottomBar").getObject();
			
			boolean topBarToggled;
			if(collapse) {
				topBarToggled = inventoryBar.getY() <= -inventoryBar.getHeight();
			} else {
				topBarToggled = inventoryBar.getY() >= 0;
			}
			
			boolean bottomBarToggled;
			if(collapse) {
				bottomBarToggled = bottomBar.getY() >= Game.HEIGHT;
			} else {
				bottomBarToggled = bottomBar.getY() <= Game.HEIGHT - bottomBar.getHeight();
			}
			
			if(!topBarToggled) {
				inventoryBar.setY(inventoryBar.getY() + (collapse ? -speed : speed));
				for(Object obj : inventoryBar.getItems()) {
					obj.setY(obj.getY() + (collapse ? -speed : speed));
				}
			}
			
			if(!bottomBarToggled) {
				bottomBar.setY(bottomBar.getY() + (collapse ? speed : -speed));
				for(Object obj : bottomBar.getItems()) {
					obj.setY(obj.getY() + (collapse ? speed : -speed));
				}
			}
			
			inventory.update();
			
			if(topBarToggled && bottomBarToggled) {
				if(collapse) {
					game.handler.removeObject(inventoryBar);
					game.handler.removeObject(bottomBar);
				}
				transition = null;
			}
		}
	}
	
	public void builderPlayStart() {
		transition = TRANSITION.builderPlayStart;
		game.handler.addObject(builderStopButton, 4);
	}
	
	public void builderPlayEnd() {
		transition = TRANSITION.builderPlayEnd;
		game.handler.removeObject(builderStopButton);
		load(STATE.Builder);
	}
	
	public static class UIElement {
		private String name;
		private Object obj;
		private int priority;
		
		public UIElement(String name, Object obj, int priority) {
			this.name = name;
			this.obj = obj;
			this.priority = priority;
		}
		
		public UIElement(String name, Object obj) {
			this.name = name;
			this.obj = obj;
			this.priority = 0;
		}
		
		public String getName() {
			return name;
		}
		
		public Object getObject() {
			return obj;
		}
		
		public int getPriority() {
			return priority;
		}
		
		public static UIElement find(LinkedList<UIElement> list, String name) {
			for(int i = 0; i < list.size(); i++) {
				if(list.get(i).getName().equals(name)) return list.get(i);
			}
			return null;
		}
	}
}