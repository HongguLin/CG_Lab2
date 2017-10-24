import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class LittleWall implements ActionListener, Runnable {

	/**
	 * Little Wall Rock Climbing Copyright 2009, 2017 Eric McCreath GNU LGPL
	 */

	final static Dimension dim = new Dimension(800, 600);
	final static XYPoint wallsize = new XYPoint(8.0, 6.0);

	JFrame jframe;
	GameComponent canvas;
	Wall wall;
	//PlayerSpring player;
	PlayerSpring2 player;
	Timer timer;

	public LittleWall() {
		SwingUtilities.invokeLater(this);
	}

	public static void main(String[] args) throws InterruptedException {
		new LittleWall();
	}

	private void startRunningGame() {
		wall = new Wall(dim, wallsize);
		wall.draw(canvas.getBackgroundGraphics());
		player = new PlayerSpring2(wall);
		canvas.addMouseMotionListener(player);
		canvas.addKeyListener(player);

		timer = new Timer(1000 / 15, this);
		timer.start();
	}

	public void drawTitleScreen() {
		Graphics2D bg = canvas.getBackgroundGraphics();
		bg.setColor(Color.white);
		bg.fillRect(0, 0, dim.width, dim.height);
		canvas.clearOffscreen();

		Graphics2D os = canvas.getOffscreenGraphics();
		os.setColor(Color.red);
		os.setFont(new Font(Font.MONOSPACED,Font.BOLD,30));
		os.drawString("Little Wall Climbing", 80, 80);
		AffineTransform at = new AffineTransform();
		at.scale(0.8,0.8);
		at.rotate(Math.PI/8);

		// add your code here to make the title screen more interesting.


		os.setColor(Color.red);
		os.drawOval(200,200,100,100);
		os.drawLine(250,350,250,150);

		os.setColor(Color.orange);
		os.setFont(new Font(Font.MONOSPACED,Font.BOLD,20));
		os.drawString("play instructions:",400,400);
		os.drawString("A_left-hand",420,420);
		os.drawString("S_right-hand",420,440);
		os.drawString("Z_left-leg",420,460);
		os.drawString("X_right-leg",420,480);

		try {
			Image image = ImageIO.read(new File("src/climb1.png"));
			os.setTransform(at);
			os.drawImage(image,300,80,250,450,null);
		}catch (IOException e){
			e.printStackTrace();
		}


		canvas.drawOffscreen();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			canvas.clearOffscreen();
			Graphics2D of = canvas.getOffscreenGraphics();
			player.draw(of);
			player.update(canvas, wall);
			canvas.drawOffscreen();
		}
	}

	@Override
	public void run() {
		jframe = new JFrame("Little Wall");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas = new GameComponent(dim);
		jframe.getContentPane().add(canvas);
		jframe.pack();
		jframe.setVisible(true);
		drawTitleScreen();
		startRunningGame();
		
	}
}
