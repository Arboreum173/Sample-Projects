package com.xenonplatformer.window;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class Window {
	public Window(int width, int height, String title, Game game) {
		game.setPreferredSize(new Dimension(width, height));
		game.setMinimumSize(new Dimension(width, height));
		game.setMaximumSize(new Dimension(width, height));
		
		JFrame frame = new JFrame(title);
		frame.add(game);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent event) {
				Game.x = (int) game.getLocationOnScreen().getX();
				Game.y = (int) game.getLocationOnScreen().getY();
			}
		});
		
		game.start();
	}
}