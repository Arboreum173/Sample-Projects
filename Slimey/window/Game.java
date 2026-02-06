package com.xenonplatformer.window;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xenonplatformer.framework.FileReader;
import com.xenonplatformer.framework.GameObject;
import com.xenonplatformer.framework.GameObject.BLOCK_DIRECTION;
import com.xenonplatformer.framework.Object;
import com.xenonplatformer.framework.ID;
import com.xenonplatformer.objects.Block;
import com.xenonplatformer.objects.Enemy;
import com.xenonplatformer.objects.Player;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = -5386299653732837839L;
	
	private boolean running = false;
	private Thread thread;
	
	private ArrayList<Integer> fpsData = new ArrayList<Integer>();
	
	public static int WIDTH, HEIGHT;
	public static int x, y;
	
	public static boolean transitioning = false;
	
	public static BufferedImage spriteSheet = null;
	public static BufferedImage ui = null;
	
	public enum LEVEL_TYPE { Campaign, Draft, Online };
	public static LEVEL_TYPE levelType = LEVEL_TYPE.Draft;
	public static int levelID = 1;
	public static JSONObject levelData;
	public static int levelCount = 0;
	
	public Player player;
	public Block goal;
	
	public Handler handler;
	public Camera camera;
	public Menu menu;
	public Transition transition;
	
	ArrayList<Block> movingBlocks = new ArrayList<Block>();
	ArrayList<Block> staticBlocks = new ArrayList<Block>();
	private ArrayList<HashSet<Block>> blockGroups = new ArrayList<HashSet<Block>>();
	public HashSet<Block> processedBlocks = new HashSet<Block>();
	public ArrayList<Block> unprocessedBlocks = new ArrayList<Block>();
	
	private BufferedImage frontLayer, midLayer, backLayer;
	private float frontX = 0, midX = 0, backX = 0;
	
	public enum STATE {
		Menu,
		Help,
		Levels,
		Game,
		Builder
	};
	public static STATE state = STATE.Menu;
	
	private void init() {
		WIDTH = getWidth();
		HEIGHT = getHeight();
		x = (int) getLocationOnScreen().getX();
		y = (int) getLocationOnScreen().getY();
		
		spriteSheet = FileReader.loadImage("/sprite-sheet.png");
		ui = FileReader.loadImage("/ui.png");
		
		this.handler = new Handler();
		this.camera = new Camera(this);
		this.menu = new Menu(this);
		this.transition = new Transition();
		
		if(!FileReader.fileExists("res\\user.json")) {
			FileReader.write("res\\user.json", FileReader.read("res\\default-user.json"));
		}
		
		levelData = FileReader.readJSON("res\\level-template.json");
		levelCount = FileReader.getFilesInDirectory("res\\levels").length;
		
		menu.init();
		menu.load(state);
		
		frontLayer = FileReader.loadImage("/front_layer.png");
		midLayer = FileReader.loadImage("/mid_layer.png");
		backLayer = FileReader.loadImage("/back_layer.png");
		
		AudioPlayer.load();
		// AudioPlayer.getMusic("music").loop();
	}
	
	public synchronized void start() {
		if(running) return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		init();
		this.requestFocus();
		
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1) {
				tick();
				delta--;
			}
			if(running) render();
			frames++;
			
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				fpsData.add(frames);
				System.out.println("FPS: " + frames + " | ~" + averageFPS());
				frames = 0;
			}
		}
		
		stop();
	}
	
	private void tick() {
		transition.tick();
		menu.tick();
		handler.tick();
		
		if(state == STATE.Game || state == STATE.Builder) {
			camera.tick();
		}
		
		if(state == STATE.Game) {
			// moving background
			backX -= player.getVelX() / 40;
			midX -= player.getVelX() / 12;
			frontX -= player.getVelX() / 5;
			
			if(backX <= -800) backX = 0;
			if(midX <= -800) midX = 0;
			if(frontX <= -800) frontX = 0;
			
			if(backX >= 800) backX = 0;
			if(midX >= 800) midX = 0;
			if(frontX >= 800) frontX = 0;
			
			// do collision checks for all moving blocks
			HashMap<Block, BLOCK_DIRECTION> blockCollisions = new HashMap<Block, BLOCK_DIRECTION>();
			
			for(int i = 0; i < handler.objects.size(); i++) {
				Object obj = handler.objects.get(i);
				if(obj instanceof Block) {
					Block block = (Block) obj;
					if(
						block.getMovement() != BLOCK_DIRECTION.none &&
						player.isPlaying() &&
						block.isColliding()
					) {
						// log all collisions into map
						blockCollisions.put(block, block.getCollisionDirection());
					}
				}
			}
			
			// do collisions
			for(Map.Entry<Block, BLOCK_DIRECTION> blockCollision : blockCollisions.entrySet()) {
				Block block = blockCollision.getKey();
				BLOCK_DIRECTION direction = blockCollision.getValue();
				
				block.collide(direction);
			}
		}
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		if(state == STATE.Game || state == STATE.Builder) {
			// background
			g.setColor(new Color(150, 230, 255));
			g.fillRect(0, 0, getWidth(), getHeight());
			
			AffineTransform backPos1 = new AffineTransform(),
				backPos2 = new AffineTransform(),
				backPos3 = new AffineTransform(),
				midPos1 = new AffineTransform(),
				midPos2 = new AffineTransform(),
				midPos3 = new AffineTransform(),
				frontPos1 = new AffineTransform(),
				frontPos2 = new AffineTransform(),
				frontPos3 = new AffineTransform();
			
			// back layer
			backPos1.translate(backX, 0);
			g2d.drawImage(backLayer, backPos1, null);
			backPos2.translate(backX + 800, 0);
			g2d.drawImage(backLayer, backPos2, null);
			backPos3.translate(backX - 800, 0);
			g2d.drawImage(backLayer, backPos3, null);
			
			// mid layer
			midPos1.translate(midX, 0);
			g2d.drawImage(midLayer, midPos1, null);
			midPos2.translate(midX + 800, 0);
			g2d.drawImage(midLayer, midPos2, null);
			midPos3.translate(midX - 800, 0);
			g2d.drawImage(midLayer, midPos3, null);
			
			// front layer
			frontPos1.translate(frontX, 0);
			g2d.drawImage(frontLayer, frontPos1, null);
			frontPos2.translate(frontX + 800, 0);
			g2d.drawImage(frontLayer, frontPos2, null);
			frontPos3.translate(frontX - 800, 0);
			g2d.drawImage(frontLayer, frontPos3, null);
		}
		
		handler.render(g, camera);
		transition.render(g);
		
		g.dispose();
		bs.show();
	}
	
	private int averageFPS() {
		int sum = 0;
		for(int i = 0; i < fpsData.size(); i++) sum += fpsData.get(i);
		return sum / fpsData.size();
	}
	
	public static boolean playedLevel(LEVEL_TYPE type, int id) {
		JSONObject json = FileReader.readJSON("res\\user.json");
		JSONArray list;
		if(type == LEVEL_TYPE.Campaign) {
			list = (JSONArray) json.get("played-campaign-levels");
		} else if(type == LEVEL_TYPE.Draft) {
			list = (JSONArray) json.get("played-level-drafts");
		} else {
			list = (JSONArray) json.get("played-online-levels");
		}
		
		for(int i = 0; i < list.length(); i++) {
			if((int) list.get(i) == id) return true;
		}
		return false;
	}
	
	public static void markLevelAsWon(LEVEL_TYPE type, int id) {
		if(!Game.playedLevel(type, id)) {
			JSONObject json = FileReader.readJSON("res\\user.json");
			String key;
			if(type == LEVEL_TYPE.Campaign) {
				key = "played-campaign-levels";
			} else if(type == LEVEL_TYPE.Draft) {
				key = "played-level-drafts";
			} else {
				key = "played-online-levels";
			}
			json.put(key, ((JSONArray) json.get(key)).put(id));
			FileReader.writeJSON("res\\user.json", json);
		}
	}
	
	public void loadCampaignLevel(int level) {
		Game.levelType = LEVEL_TYPE.Campaign;
		Game.levelID = level;
		levelData = FileReader.readJSON("res\\levels\\" + level + ".json");
		loadLevelMap();
	}
	
	public void loadLevelDraft(int draft) {
		Game.levelType = LEVEL_TYPE.Draft;
		Game.levelID = draft;
		levelData = FileReader.readJSON("res\\drafts\\" + draft + ".json");
		loadLevelMap();
	}
	
	public void loadLevelTemplate() {
		Game.levelType = LEVEL_TYPE.Draft;
		Game.levelID = 1;
		levelData = FileReader.readJSON("res\\level-template.json");
		loadLevelMap();
	}
	
	public static void saveAsDraft() {
		FileReader.writeJSON("res\\drafts\\1.json", levelData);
	}
	
	public void loadLevelMap() {
		putPlayer();
		putGoal();
		putEnemies();
		camera.setCamera();
		
		getMovingBlocks();
		getStaticViewportBlocks();
		
		for(int i = 0; i < movingBlocks.size(); i++) {
			movingBlocks.get(i).setAdjacentMovingBlocks();
		}
		
		for(int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).setAdjacentMovingBlocks();
		}
		
		getBlockGroups();
	}
	
	public void putBlock(int posX, int posY, ID id, BLOCK_DIRECTION movement) {
		GameObject gameObj;
		if(id == ID.Enemy) {
			gameObj = new Enemy(posX, posY, movement, this);
		} else {
			gameObj = new Block(posX, posY, id, movement, this);
		}
		
		if(!gameObj.test()) return;
		
		if(id == ID.Enemy) {
			JSONObject enemyObj = new JSONObject();
			enemyObj.put("x", posX);
			enemyObj.put("y", posY);
			
			levelData.getJSONArray("enemies").put(enemyObj);
		} else {
			int index = getBlockIndex(posX, posY);
			levelData.getJSONArray("map").put(index, idToType(id, movement));
		}
		
		gameObj.spawn();
		
		for(int i = 0; i < handler.objects.size(); i++) {
			Object obj = handler.objects.get(i);
			
			if(obj.getId() != ID.WaterBlock) continue;
		}
	}
	
	public void eraseBlock(int posX, int posY) {
		for(int i = 0; i < handler.objects.size(); i++) {
			Object obj = handler.objects.get(i);
			
			if(!(obj instanceof GameObject)) continue;
			
			GameObject gameObj = (GameObject) obj;
			
			if(gameObj.getPosX() == posX && gameObj.getPosY() == posY) {
				eraseBlock(gameObj, true);
				break;
			}
		}
	}
	
	public void eraseBlock(GameObject gameObj, boolean disappear) {
		if(gameObj instanceof Block) {
			// delete block
			if(disappear) ((Block) gameObj).disappear();
			
			int index = getBlockIndex(gameObj.getPosX(), gameObj.getPosY());
			levelData.getJSONArray("map").put(index, 0);
		} else if(gameObj instanceof Enemy) {
			// delete enemy
			if(disappear) ((Enemy) gameObj).disappear();
			
			JSONArray enemies = levelData.getJSONArray("enemies");
			for(int j = 0; j < enemies.length(); j++) {
				// look for enemy at the position
				JSONObject enemyObj = enemies.getJSONObject(j);
				if(
					(int) enemyObj.get("x") == gameObj.getPosX() &&
					(int) enemyObj.get("y") == gameObj.getPosY()
				) {
					enemies.remove(j);
				}
			}
		}
		
		if(!disappear) handler.removeObject(gameObj);
	}
	
	public static int idToType(ID id, BLOCK_DIRECTION movement) {
		if(id == ID.Player) return 1;
		else if(id == ID.BrickBlock) {
			if(movement == BLOCK_DIRECTION.none) return 10;
			else if(movement == BLOCK_DIRECTION.up) return 101;
			else if(movement == BLOCK_DIRECTION.right) return 102;
			else if(movement == BLOCK_DIRECTION.down) return 103;
			else if(movement == BLOCK_DIRECTION.left) return 104;
		} else if(id == ID.SpikeBlock) {
			if(movement == BLOCK_DIRECTION.none) return 11;
			else if(movement == BLOCK_DIRECTION.up) return 111;
			else if(movement == BLOCK_DIRECTION.right) return 112;
			else if(movement == BLOCK_DIRECTION.down) return 113;
			else if(movement == BLOCK_DIRECTION.left) return 114;
		} else if(id == ID.IceBlock) {
			if(movement == BLOCK_DIRECTION.none) return 12;
			else if(movement == BLOCK_DIRECTION.up) return 121;
			else if(movement == BLOCK_DIRECTION.right) return 122;
			else if(movement == BLOCK_DIRECTION.down) return 123;
			else if(movement == BLOCK_DIRECTION.left) return 124;
		} else if(id == ID.ZeroBlock) {
			if(movement == BLOCK_DIRECTION.none) return 13;
			else if(movement == BLOCK_DIRECTION.up) return 131;
			else if(movement == BLOCK_DIRECTION.right) return 132;
			else if(movement == BLOCK_DIRECTION.down) return 133;
			else if(movement == BLOCK_DIRECTION.left) return 134;
		} else if(id == ID.TrampolineBlock) {
			if(movement == BLOCK_DIRECTION.none) return 14;
			else if(movement == BLOCK_DIRECTION.up) return 141;
			else if(movement == BLOCK_DIRECTION.right) return 142;
			else if(movement == BLOCK_DIRECTION.down) return 143;
			else if(movement == BLOCK_DIRECTION.left) return 144;
		} else if(id == ID.LadderBlock) {
			if(movement == BLOCK_DIRECTION.none) return 15;
			else if(movement == BLOCK_DIRECTION.up) return 151;
			else if(movement == BLOCK_DIRECTION.right) return 152;
			else if(movement == BLOCK_DIRECTION.down) return 153;
			else if(movement == BLOCK_DIRECTION.left) return 154;
		} else if(id == ID.HorizontalLadderBlock) {
			if(movement == BLOCK_DIRECTION.none) return 16;
			else if(movement == BLOCK_DIRECTION.up) return 161;
			else if(movement == BLOCK_DIRECTION.right) return 162;
			else if(movement == BLOCK_DIRECTION.down) return 163;
			else if(movement == BLOCK_DIRECTION.left) return 164;
		} else if(id == ID.RightConveyorBlock) {
			if(movement == BLOCK_DIRECTION.none) return 17;
			else if(movement == BLOCK_DIRECTION.up) return 171;
			else if(movement == BLOCK_DIRECTION.right) return 172;
			else if(movement == BLOCK_DIRECTION.down) return 173;
			else if(movement == BLOCK_DIRECTION.left) return 174;
		} else if(id == ID.LeftConveyorBlock) {
			if(movement == BLOCK_DIRECTION.none) return 18;
			else if(movement == BLOCK_DIRECTION.up) return 181;
			else if(movement == BLOCK_DIRECTION.right) return 182;
			else if(movement == BLOCK_DIRECTION.down) return 183;
			else if(movement == BLOCK_DIRECTION.left) return 184;
		} else if(id == ID.BlueDoor) return 19;
		else if(id == ID.RedDoor) return 20;
		else if(id == ID.GreenDoor) return 21;
		else if(id == ID.YellowDoor) return 22;
		else if(id == ID.BlueButton) return 23;
		else if(id == ID.RedButton) return 24;
		else if(id == ID.GreenButton) return 25;
		else if(id == ID.YellowButton) return 26;
		else if(id == ID.BreakableBrickBlock) {
			if(movement == BLOCK_DIRECTION.none) return 27;
			else if(movement == BLOCK_DIRECTION.up) return 271;
			else if(movement == BLOCK_DIRECTION.right) return 272;
			else if(movement == BLOCK_DIRECTION.down) return 273;
			else if(movement == BLOCK_DIRECTION.left) return 274;
		} else if(id == ID.BreakableIceBlock) {
			if(movement == BLOCK_DIRECTION.none) return 28;
			else if(movement == BLOCK_DIRECTION.up) return 281;
			else if(movement == BLOCK_DIRECTION.right) return 282;
			else if(movement == BLOCK_DIRECTION.down) return 283;
			else if(movement == BLOCK_DIRECTION.left) return 284;
		} else if(id == ID.BreakableZeroBlock) {
			if(movement == BLOCK_DIRECTION.none) return 29;
			else if(movement == BLOCK_DIRECTION.up) return 291;
			else if(movement == BLOCK_DIRECTION.right) return 292;
			else if(movement == BLOCK_DIRECTION.down) return 293;
			else if(movement == BLOCK_DIRECTION.left) return 294;
		} else if(id == ID.WaterBlock) {
			if(movement == BLOCK_DIRECTION.none) return 30;
			else if(movement == BLOCK_DIRECTION.up) return 301;
			else if(movement == BLOCK_DIRECTION.right) return 302;
			else if(movement == BLOCK_DIRECTION.down) return 303;
			else if(movement == BLOCK_DIRECTION.left) return 304;
		} else if(id == ID.Enemy) {
			if(movement == BLOCK_DIRECTION.right) return 31;
			else if(movement == BLOCK_DIRECTION.left) return 311;
		} else if(id == ID.StopBlock) {
			if(movement == BLOCK_DIRECTION.none) return 32;
			else if(movement == BLOCK_DIRECTION.up) return 321;
			else if(movement == BLOCK_DIRECTION.right) return 322;
			else if(movement == BLOCK_DIRECTION.down) return 323;
			else if(movement == BLOCK_DIRECTION.left) return 324;
		} else if(id == ID.Goal) return 33;
		return 0;
	}
	
	public Block getBlockAt(int posX, int posY) {
		for(int i = 0; i < handler.objects.size(); i++) {
			Object obj = handler.objects.get(i);
			
			if(!(obj instanceof Block)) continue;
			
			Block block = (Block) obj;
			
			if(
				block.getPosX() == posX &&
				block.getPosY() == posY
			) {
				return block; 
			}
		}
		
		return null;
	}
	
	public void putPlayer() {
		int posX = (int) levelData.get("playerX");
		int posY = (int) levelData.get("playerY");
		
		player = new Player(posX, posY, this);
		handler.addObject(player, 2);
		camera.setCamera();
	}
	
	public void putGoal() {
		int posX = (int) levelData.get("goalX");
		int posY = (int) levelData.get("goalY");
		
		goal = new Block(posX, posY, ID.Goal, BLOCK_DIRECTION.none, this);
		handler.addObject(goal, 2);
	}
	
	public void changePlayerPos(int posX, int posY) {
		int oldX = player.getPosX();
		int oldY = player.getPosY();
		
		// move to new position and test if valid
		player.setPosX(posX);
		player.setPosY(posY);
		
		if(player.test()) {
			// valid position
			levelData.put("playerX", posX);
			levelData.put("playerY", posY);
		} else {
			// invalid, move back to original position
			player.setPosX(oldX);
			player.setPosY(oldY);
		}
	}
	
	public void changeGoalPos(int posX, int posY) {
		int oldX = goal.getPosX();
		int oldY = goal.getPosY();
		
		// move to new position and test if valid
		goal.setPosX(posX);
		goal.setPosY(posY);
		
		if(goal.test()) {
			// valid position
			levelData.put("goalX", posX);
			levelData.put("goalY", posY);
		} else {
			// invalid, move back to original position
			goal.setPosX(oldX);
			goal.setPosY(oldY);
		}
	}
	
	public void putEnemies() {
		JSONArray enemies = levelData.getJSONArray("enemies");
		for(int i = 0; i < enemies.length(); i++) {
			JSONObject enemyObj = enemies.getJSONObject(i);
			
			int enemyX = (int) enemyObj.get("x");
			int enemyY = (int) enemyObj.get("y");
			
			handler.addObject(new Enemy(enemyX, enemyY, BLOCK_DIRECTION.right, this), 3);
		}
	}
	
	private void getStaticViewportBlocks() {
		int startX = camera.getPosX();
		int startY = camera.getPosY();
		if(startX < 0) startX = 0;
		if(startY < 0) startY = 0;
		
		int endX = camera.getEndX();
		int endY = camera.getEndY();
		
		int width = (int) levelData.get("width");
		int height = (int) levelData.get("height");
		if(endX > width - 1) endX = width - 1;
		if(endY > height - 1) endY = height - 1;
		
		for(int curY = startY; curY <= endY; curY++) {
			for(int curX = startX; curX <= endX; curX++) {
				Block block = readBlock(curX, curY);
				if(block != null) {
					handler.addObject(block, 1);
					staticBlocks.add(block);
				}
			}
		}
	}
	
	private void getMovingBlocks() {
		movingBlocks.clear();
		
		int width = (int) levelData.get("width");
		int height = (int) levelData.get("height");
		
		for(int posY = 0; posY < height; posY++) {
			for(int posX = 0; posX < width; posX++) {
				int index = getBlockIndex(posX, posY);
				int type = (int) levelData.getJSONArray("map").get(index);
				
				GameObject obj = null;
				
				if(type == 101) obj = new Block(posX, posY, ID.BrickBlock, BLOCK_DIRECTION.up, this);
				else if(type == 102) obj = new Block(posX, posY, ID.BrickBlock, BLOCK_DIRECTION.right, this);
				else if(type == 103) obj = new Block(posX, posY, ID.BrickBlock, BLOCK_DIRECTION.down, this);
				else if(type == 104) obj = new Block(posX, posY, ID.BrickBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 111) obj = new Block(posX, posY, ID.SpikeBlock, BLOCK_DIRECTION.up, this);
				else if(type == 112) obj = new Block(posX, posY, ID.SpikeBlock, BLOCK_DIRECTION.right, this);
				else if(type == 113) obj = new Block(posX, posY, ID.SpikeBlock, BLOCK_DIRECTION.down, this);
				else if(type == 114) obj = new Block(posX, posY, ID.SpikeBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 121) obj = new Block(posX, posY, ID.IceBlock, BLOCK_DIRECTION.up, this);
				else if(type == 122) obj = new Block(posX, posY, ID.IceBlock, BLOCK_DIRECTION.right, this);
				else if(type == 123) obj = new Block(posX, posY, ID.IceBlock, BLOCK_DIRECTION.down, this);
				else if(type == 124) obj = new Block(posX, posY, ID.IceBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 131) obj = new Block(posX, posY, ID.ZeroBlock, BLOCK_DIRECTION.up, this);
				else if(type == 132) obj = new Block(posX, posY, ID.ZeroBlock, BLOCK_DIRECTION.right, this);
				else if(type == 133) obj = new Block(posX, posY, ID.ZeroBlock, BLOCK_DIRECTION.down, this);
				else if(type == 134) obj = new Block(posX, posY, ID.ZeroBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 141) obj = new Block(posX, posY, ID.TrampolineBlock, BLOCK_DIRECTION.up, this);
				else if(type == 142) obj = new Block(posX, posY, ID.TrampolineBlock, BLOCK_DIRECTION.right, this);
				else if(type == 143) obj = new Block(posX, posY, ID.TrampolineBlock, BLOCK_DIRECTION.down, this);
				else if(type == 144) obj = new Block(posX, posY, ID.TrampolineBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 151) obj = new Block(posX, posY, ID.LadderBlock, BLOCK_DIRECTION.up, this);
				else if(type == 152) obj = new Block(posX, posY, ID.LadderBlock, BLOCK_DIRECTION.right, this);
				else if(type == 153) obj = new Block(posX, posY, ID.LadderBlock, BLOCK_DIRECTION.down, this);
				else if(type == 154) obj = new Block(posX, posY, ID.LadderBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 161) obj = new Block(posX, posY, ID.HorizontalLadderBlock, BLOCK_DIRECTION.up, this);
				else if(type == 162) obj = new Block(posX, posY, ID.HorizontalLadderBlock, BLOCK_DIRECTION.right, this);
				else if(type == 163) obj = new Block(posX, posY, ID.HorizontalLadderBlock, BLOCK_DIRECTION.down, this);
				else if(type == 164) obj = new Block(posX, posY, ID.HorizontalLadderBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 171) obj = new Block(posX, posY, ID.RightConveyorBlock, BLOCK_DIRECTION.up, this);
				else if(type == 172) obj = new Block(posX, posY, ID.RightConveyorBlock, BLOCK_DIRECTION.right, this);
				else if(type == 173) obj = new Block(posX, posY, ID.RightConveyorBlock, BLOCK_DIRECTION.down, this);
				else if(type == 174) obj = new Block(posX, posY, ID.RightConveyorBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 181) obj = new Block(posX, posY, ID.LeftConveyorBlock, BLOCK_DIRECTION.up, this);
				else if(type == 182) obj = new Block(posX, posY, ID.LeftConveyorBlock, BLOCK_DIRECTION.right, this);
				else if(type == 183) obj = new Block(posX, posY, ID.LeftConveyorBlock, BLOCK_DIRECTION.down, this);
				else if(type == 184) obj = new Block(posX, posY, ID.LeftConveyorBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 271) obj = new Block(posX, posY, ID.BreakableBrickBlock, BLOCK_DIRECTION.up, this);
				else if(type == 272) obj = new Block(posX, posY, ID.BreakableBrickBlock, BLOCK_DIRECTION.right, this);
				else if(type == 273) obj = new Block(posX, posY, ID.BreakableBrickBlock, BLOCK_DIRECTION.down, this);
				else if(type == 274) obj = new Block(posX, posY, ID.BreakableBrickBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 281) obj = new Block(posX, posY, ID.BreakableIceBlock, BLOCK_DIRECTION.up, this);
				else if(type == 282) obj = new Block(posX, posY, ID.BreakableIceBlock, BLOCK_DIRECTION.right, this);
				else if(type == 283) obj = new Block(posX, posY, ID.BreakableIceBlock, BLOCK_DIRECTION.down, this);
				else if(type == 284) obj = new Block(posX, posY, ID.BreakableIceBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 291) obj = new Block(posX, posY, ID.BreakableZeroBlock, BLOCK_DIRECTION.up, this);
				else if(type == 292) obj = new Block(posX, posY, ID.BreakableZeroBlock, BLOCK_DIRECTION.right, this);
				else if(type == 293) obj = new Block(posX, posY, ID.BreakableZeroBlock, BLOCK_DIRECTION.down, this);
				else if(type == 294) obj = new Block(posX, posY, ID.BreakableZeroBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 301) obj = new Block(posX, posY, ID.WaterBlock, BLOCK_DIRECTION.up, this);
				else if(type == 302) obj = new Block(posX, posY, ID.WaterBlock, BLOCK_DIRECTION.right, this);
				else if(type == 303) obj = new Block(posX, posY, ID.WaterBlock, BLOCK_DIRECTION.down, this);
				else if(type == 304) obj = new Block(posX, posY, ID.WaterBlock, BLOCK_DIRECTION.left, this);
				
				else if(type == 31) obj = new Enemy(posX, posY, BLOCK_DIRECTION.right, this);
				else if(type == 311) obj = new Enemy(posX, posY, BLOCK_DIRECTION.left, this);
				
				else if(type == 321) obj = new Block(posX, posY, ID.StopBlock, BLOCK_DIRECTION.up, this);
				else if(type == 322) obj = new Block(posX, posY, ID.StopBlock, BLOCK_DIRECTION.right, this);
				else if(type == 323) obj = new Block(posX, posY, ID.StopBlock, BLOCK_DIRECTION.down, this);
				else if(type == 324) obj = new Block(posX, posY, ID.StopBlock, BLOCK_DIRECTION.left, this);
				
				if(obj instanceof Enemy) {
					handler.addObject(obj, 2);
				} else if(obj instanceof Block) {
					handler.addObject(obj, 1);
					movingBlocks.add((Block) obj);
				}
			}
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void getBlockGroups() {
		blockGroups.clear();
		
		unprocessedBlocks = (ArrayList<Block>) movingBlocks.clone();
		
		while(!unprocessedBlocks.isEmpty()) {
			// find all moving blocks that belong together
			unprocessedBlocks.get(0).findAttachedBlocks();
			HashSet<Block> blockGroup = (HashSet<Block>) processedBlocks.clone();
			blockGroups.add(blockGroup);
			for(Block block : blockGroup) {
				block.setBlockGroup(blockGroup);
			}
			processedBlocks.clear();
		}
	}
	
	public Block readBlock(int posX, int posY) {
		Block blockAt = getBlockAt(posX, posY);
		if(blockAt != null) {
			return blockAt;
		}
		
		int width = (int) levelData.get("width");
		int height = (int) levelData.get("height");
		
		if(
			posX < 0 || posX > width - 1 ||
			posY < 0 || posY > height - 1
		) { return null; }
		
		int index = getBlockIndex(posX, posY);
		int type = (int) levelData.getJSONArray("map").get(index);
		Block obj = null;
		
		if(type == 10) obj = new Block(posX, posY, ID.BrickBlock, BLOCK_DIRECTION.none, this);
		else if(type == 11) obj = new Block(posX, posY, ID.SpikeBlock, BLOCK_DIRECTION.none, this);
		else if(type == 12) obj = new Block(posX, posY, ID.IceBlock, BLOCK_DIRECTION.none, this);
		else if(type == 13) obj = new Block(posX, posY, ID.ZeroBlock, BLOCK_DIRECTION.none, this);
		else if(type == 14) obj = new Block(posX, posY, ID.TrampolineBlock, BLOCK_DIRECTION.none, this);
		else if(type == 15) obj = new Block(posX, posY, ID.LadderBlock, BLOCK_DIRECTION.none, this);
		else if(type == 16) obj = new Block(posX, posY, ID.HorizontalLadderBlock, BLOCK_DIRECTION.none, this);
		else if(type == 17) obj = new Block(posX, posY, ID.RightConveyorBlock, BLOCK_DIRECTION.none, this);
		else if(type == 18) obj = new Block(posX, posY, ID.LeftConveyorBlock, BLOCK_DIRECTION.none, this);
		
		else if(type == 19) obj = new Block(posX, posY, ID.BlueDoor, BLOCK_DIRECTION.none, this);
		else if(type == 20) obj = new Block(posX, posY, ID.RedDoor, BLOCK_DIRECTION.none, this);
		else if(type == 21) obj = new Block(posX, posY, ID.GreenDoor, BLOCK_DIRECTION.none, this);
		else if(type == 22) obj = new Block(posX, posY, ID.YellowDoor, BLOCK_DIRECTION.none, this);
		
		else if(type == 23) obj = new Block(posX, posY, ID.BlueButton, BLOCK_DIRECTION.none, this);
		else if(type == 24) obj = new Block(posX, posY, ID.RedButton, BLOCK_DIRECTION.none, this);
		else if(type == 25) obj = new Block(posX, posY, ID.GreenButton, BLOCK_DIRECTION.none, this);
		else if(type == 26) obj = new Block(posX, posY, ID.YellowButton, BLOCK_DIRECTION.none, this);
		
		else if(type == 27) obj = new Block(posX, posY, ID.BreakableBrickBlock, BLOCK_DIRECTION.none, this);
		else if(type == 28) obj = new Block(posX, posY, ID.BreakableIceBlock, BLOCK_DIRECTION.none, this);
		else if(type == 29) obj = new Block(posX, posY, ID.BreakableZeroBlock, BLOCK_DIRECTION.none, this);
		
		else if(type == 30) obj = new Block(posX, posY, ID.WaterBlock, BLOCK_DIRECTION.none, this);
		else if(type == 32) obj = new Block(posX, posY, ID.StopBlock, BLOCK_DIRECTION.none, this);
		else if(type == 33) obj = new Block(posX, posY, ID.Goal, BLOCK_DIRECTION.none, this);
		
		return obj;
	}
	
	public Block loadAdjacentBlock(int posX, int posY, BLOCK_DIRECTION movement) {
		Block targetBlock = null;
		if(movement == BLOCK_DIRECTION.up) {
			targetBlock = readBlock(posX, posY - 1);
		} else if(movement == BLOCK_DIRECTION.right) {
			targetBlock = readBlock(posX + 1, posY);
		} else if(movement == BLOCK_DIRECTION.down) {
			targetBlock = readBlock(posX, posY + 1);
		} else if(movement == BLOCK_DIRECTION.left) {
			targetBlock = readBlock(posX - 1, posY);
		}
		
		handler.addObject(targetBlock);
		
		return targetBlock;
	}
	
	public Player makeBuilderPlayer() {
		player = new Player(24, 24, this);
		player.show();
		handler.addObject(player, 2);
		camera.setCamera();
		return player;
	}
	
	public static BufferedImage getSprite(ID id) {
		if(id == ID.Player) return Game.spriteSheet.getSubimage(160, 192, 32, 32);
		
		else if(id == ID.BrickBlock) return Game.spriteSheet.getSubimage(96, 0, 32, 32);
		else if(id == ID.SpikeBlock) return Game.spriteSheet.getSubimage(128, 0, 32, 32);
		else if(id == ID.IceBlock) return Game.spriteSheet.getSubimage(160, 0, 32, 32);
		else if(id == ID.ZeroBlock) return Game.spriteSheet.getSubimage(192, 0, 32, 32);
		else if(id == ID.TrampolineBlock) return Game.spriteSheet.getSubimage(96, 32, 32, 32);
		else if(id == ID.LadderBlock) return Game.spriteSheet.getSubimage(128, 32, 32, 32);
		else if(id == ID.HorizontalLadderBlock) return Game.spriteSheet.getSubimage(160, 32, 32, 32);
		else if(id == ID.RightConveyorBlock) return Game.spriteSheet.getSubimage(192, 32, 32, 32);
		else if(id == ID.LeftConveyorBlock) return Game.spriteSheet.getSubimage(96, 64, 32, 32);

		else if(id == ID.BlueDoor) return Game.spriteSheet.getSubimage(128, 64, 32, 32);
		else if(id == ID.RedDoor) return Game.spriteSheet.getSubimage(160, 64, 32, 32);
		else if(id == ID.GreenDoor) return Game.spriteSheet.getSubimage(192, 64, 32, 32);
		else if(id == ID.YellowDoor) return Game.spriteSheet.getSubimage(96, 96, 32, 32);

		else if(id == ID.BlueButton) return Game.spriteSheet.getSubimage(64, 32, 32, 32);
		else if(id == ID.RedButton) return Game.spriteSheet.getSubimage(0, 64, 32, 32);
		else if(id == ID.GreenButton) return Game.spriteSheet.getSubimage(32, 64, 32, 32);
		else if(id == ID.YellowButton) return Game.spriteSheet.getSubimage(64, 64, 32, 32);

		else if(id == ID.BreakableBrickBlock) return Game.spriteSheet.getSubimage(128, 96, 32, 32);
		else if(id == ID.BreakableIceBlock) return Game.spriteSheet.getSubimage(96, 128, 32, 32);
		else if(id == ID.BreakableZeroBlock) return Game.spriteSheet.getSubimage(0, 96, 32, 32);

		else if(id == ID.WaterBlock) return Game.spriteSheet.getSubimage(192, 128, 32, 32);
		
		else if(id == ID.Enemy) return Game.spriteSheet.getSubimage(64, 160, 32, 32);
		
		else if(id == ID.StopBlock) return Game.spriteSheet.getSubimage(64, 128, 32, 32);
		
		else if(id == ID.Goal) return Game.spriteSheet.getSubimage(0, 0, 32, 32);
		
		return null;
	}
	
	public int getBlockIndex(int posX, int posY) {
		return (posY * (int) levelData.get("width")) + posX;
	}
	
	public static void main(String[] args) {
		new Window(800, 600, "Xenon Platformer", new Game());
	}
}