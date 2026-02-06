package com.xenonplatformer.window;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class AudioPlayer {
	public static Map<String, Sound> soundMap = new HashMap<String, Sound>();
	public static Map<String, Music> musicMap = new HashMap<String, Music>();
	
	public static void load() {
		try {
			musicMap.put("music", new Music("res/audio/background-music.wav"));
			
			soundMap.put("break", new Sound("res/audio/break.wav"));
			soundMap.put("breakIce", new Sound("res/audio/break-ice.wav"));
			soundMap.put("button", new Sound("res/audio/button.wav"));
			soundMap.put("click", new Sound("res/audio/click.wav"));
			soundMap.put("climb", new Sound("res/audio/climb.wav"));
			soundMap.put("crack", new Sound("res/audio/crack.wav"));
			soundMap.put("dead", new Sound("res/audio/dead.wav"));
			soundMap.put("hang", new Sound("res/audio/hang.wav"));
			soundMap.put("jump", new Sound("res/audio/jump.wav"));
			soundMap.put("kick", new Sound("res/audio/kick.wav"));
			soundMap.put("land", new Sound("res/audio/land.wav"));
			soundMap.put("pop", new Sound("res/audio/pop.wav"));
			soundMap.put("put", new Sound("res/audio/put.wav"));
			soundMap.put("slip", new Sound("res/audio/slip.wav"));
			soundMap.put("spawn", new Sound("res/audio/spawn.wav"));
			soundMap.put("spring", new Sound("res/audio/spring.wav"));
			soundMap.put("stomp", new Sound("res/audio/stomp.wav"));
			soundMap.put("swim", new Sound("res/audio/swim.wav"));
			soundMap.put("swimJump", new Sound("res/audio/swim-jump.wav"));
			soundMap.put("win", new Sound("res/audio/win.wav"));
		} catch(SlickException e) { e.printStackTrace(); }
	}
	
	public static Sound getSound(String key) {
		return soundMap.get(key);
	}
	
	public static Music getMusic(String key) {
		return musicMap.get(key);
	}
}