package com.xenonplatformer.framework;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.json.JSONObject;

public class FileReader {
	public static String read(String path) {
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) { e.printStackTrace(); }
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public static JSONObject readJSON(String path) {
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) { e.printStackTrace(); }
		String data = new String(encoded, StandardCharsets.UTF_8);
		JSONObject json = new JSONObject(data);
		
		return json;
	}
	
	public static void write(String path, String content) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(path, "UTF-8");
			writer.println(content);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
	}
	
	public static void writeJSON(String path, JSONObject json) {
		String content = json.toString();
		write(path, content);
	}
	
	public static BufferedImage loadImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(FileReader.class.getResource(path));
		} catch (IOException e) {e.printStackTrace(); }
		return image;
	}
	
	public static boolean fileExists(String path) {
		File file = new File(path);
		return file.exists() && !file.isDirectory();
	}
	
	public static String[] getFilesInDirectory(String path) {
		File file = new File(path);
		if(file.isDirectory()) { return file.list(); }
		return new String[0];
	}
}