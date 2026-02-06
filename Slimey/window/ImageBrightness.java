package com.xenonplatformer.window;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageBrightness {
	public static BufferedImage change(BufferedImage img, float factor) {
		if(factor > 2 || factor < 0 || factor == 1) return img;
		
		ColorModel cm = img.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = img.copyData(img.getRaster().createCompatibleWritableRaster());
		BufferedImage newImg = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		
		for(int y = 0; y < newImg.getHeight(); y++) {
			for(int x = 0; x < newImg.getWidth(); x++) {
				Color color = new Color(newImg.getRGB(x, y), true);
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				int alpha = color.getAlpha();
				if(factor > 1) {
					// brighten
					red = (int) (red + (255 - red) * (factor - 1));
					green = (int) (green + (255 - green) * (factor - 1));
					blue = (int) (blue + (255 - blue) * (factor - 1));
				} else {
					// darken 
					red = (int) (red * factor);
					green = (int) (green * factor);
					blue = (int) (blue * factor);
				}
				
				Color newColor = new Color(red, green, blue, alpha);
				newImg.setRGB(x, y, newColor.getRGB());
			}
		}
		
		return newImg;
	}
}