import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{
	
	final int originalTileSize = 16;
	final int scale = 4;
	
	public final int tileSize = originalTileSize * scale;
	public final int smallTileSize = 8 * scale;
	final int maxScreenCol = 18; // 1152 pixels
	final int maxScreenRow = 8;	// 512 pixels
	
	final int screenWidth = maxScreenCol * tileSize; 
	final int screenHeight = maxScreenRow * tileSize; 
	
	final int targetFPS = 60;
	
	
	BufferedImage groundTile, skyArt, barrierTileLeft, barrierTileRight, UI_emptyHeart, UI_fullHeart;
	
	KeyHandler keyH = new KeyHandler(this);
	Thread gameThread;
	public Player player = new Player(this, keyH);
	public static int score = 0;
	public static int highScore = 0;

	@FunctionalInterface
	public interface HealthCheck {
		public boolean shouldHighlight(int minimumHealth);
	}
	HealthCheck h1 = (minimum) -> {return player.getHealth() >= minimum;}; //Lambda expression
	
	
	
	public GamePanel() {	// ---------------------------------------------------------------------------------------------GAME PANEL
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.blue);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
	}
	
	public void startGameThread() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override				// ----------------------------------------------------------------------------------------------RUN
	public void run() {
		double drawInterval = 1000 / targetFPS;
		double nextDrawTime = System.currentTimeMillis() + drawInterval;
		player.setDefaultValues();
		player.getPlayerImage();
		
		try {
			groundTile = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/groundDark.png"));
			skyArt = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/sky01.png"));
			barrierTileLeft = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/barrierLeft.png"));
			barrierTileRight = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/barrierRight.png"));
			UI_emptyHeart = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/emptyHeart.png"));
			UI_fullHeart = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/fullHeart.png"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		loadData();
		
		while (gameThread != null) {
			
			update();
			repaint();
			
			try {
				double remainingTime = nextDrawTime - System.currentTimeMillis();	
				
				if (remainingTime < 0) {
					remainingTime = 0;
				}
				
				Thread.sleep((long)remainingTime);
				
				nextDrawTime += drawInterval;
			}
			catch (InterruptedException e) {
			}
			
		}
		
		
		
	}
	
	public void update() {	// ------------------------------------------------------------------------------------- UPDATE
		
		highScore = Math.max(score, highScore);

		if (Math.random() < 0.01){
			new Enemy();
		}
		
		player.update();
		
		for (Enemy e: Enemy.list){
			e.update(player.x);
		}

		for (LightBullet l: LightBullet.list){
			l.updateBullet();
		}
		for (HeavyBullet h: HeavyBullet.list){
			h.updateBullet();
		}

		LightBullet.cleanBullets();
		HeavyBullet.cleanBullets();
		Enemy.cleanBullets();

	}
	public void paintComponent(Graphics g) {	// --------------------------------------------------------------------PAINT COMPONENT
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)(g);
		
		g2.drawImage(skyArt, 0, 0, maxScreenCol * tileSize, 7 * tileSize, null);

		player.draw(g2);
		
		if (keyH.f1Toggled) {
			g2.setColor(Color.green);
			g2.drawRect(player.x, player.y, tileSize, tileSize);
		}
		
		
		for (Enemy e: Enemy.list){
			g2.drawImage(e.getImage(), e.getX(), e.getY(), tileSize, tileSize, null);
			if (keyH.f1Toggled) {
				g2.setColor(Color.red);
				g2.drawRect(e.getX(), e.getY(), tileSize, tileSize);
			}
		}


		try{
			for (LightBullet l: LightBullet.list){
				g2.drawImage(l.bulletImage, l.posX, l.posY, smallTileSize, smallTileSize, null);
			}
			for (HeavyBullet h: HeavyBullet.list){
				g2.drawImage(h.bulletImage, h.posX, h.posY, smallTileSize, smallTileSize, null);
			}
		}
		catch(java.util.ConcurrentModificationException e){

		}


		for (int i = 0; i < maxScreenCol; i++) {
			g2.drawImage(groundTile, i * tileSize, screenHeight - tileSize, tileSize, tileSize, null);
		}
		
		if (keyH.f1Toggled) {
			g2.setColor(Color.white);
			for (int a = 0; a < maxScreenCol; a++) {
				for (int b = 0; b < maxScreenRow; b++) {
					g2.drawRect(a * tileSize, b * tileSize, tileSize, tileSize);
				}
			}
		}
		
		// ----------------- BARRIERS -------------------
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, player.x > 240? 0f: (float)((240 - player.x) / 240.0)));
		
		for (int leftY = 0; leftY < maxScreenRow - 1; leftY++) {
			g2.drawImage(barrierTileLeft, 0, leftY * tileSize, tileSize, tileSize, null);
		}
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, player.x < 848? 0f: (float)((player.x - 848) / 240.0)));
		
		for (int leftY = 0; leftY < maxScreenRow - 1; leftY++) {
			g2.drawImage(barrierTileRight, screenWidth - tileSize, leftY * tileSize, tileSize, tileSize, null);
		}
		
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		
		
		// -------------- UI -------------
		
		g2.drawImage(h1.shouldHighlight(1)? UI_fullHeart: UI_emptyHeart, tileSize / 2, tileSize / 2, tileSize, tileSize, null);
		g2.drawImage(h1.shouldHighlight(2)? UI_fullHeart: UI_emptyHeart, (int)(tileSize * 1.75), tileSize / 2, tileSize, tileSize, null);
		g2.drawImage(h1.shouldHighlight(3)? UI_fullHeart: UI_emptyHeart, (int)(tileSize * 3), tileSize / 2, tileSize, tileSize, null);
		
		g2.setColor(Color.getHSBColor(0.65f, 1f, 0.2f));
		g2.fillRect(32, 16, (int)(64 * 3.5), 16);
		g2.setColor(Color.getHSBColor(0.4f, 1f, 0.75f));
		g2.drawRect(36, 20, 215, 7);

		g2.fillRect(36, 20, (int)((player.getCharge() / 1800.0) * 215), 7);

		g2.drawString("Score: " + String.valueOf(score), screenWidth / 2, 48);
		g2.drawString("HiScore: " + String.valueOf(highScore), screenWidth / 2, 64);
		g2.drawString("ESC to Save", 1024, 48);
		g2.drawString("F1 to Toggle Grid", 1024, 64);
		g2.drawString("F9 to Reset Stats", 1024, 80);
		

		g2.dispose();
	}	
	
	
	public void saveData() {	// -------------------------------------------------------------------------------------------------------------- SAVE DATA
		try (BufferedWriter bw = new BufferedWriter(new FileWriter("save.txt"))){
			bw.write("Health: " + player.health);
			bw.newLine();
			bw.write("Score: " + score);
			bw.newLine();
			bw.write("High Score: " + highScore);
			System.out.println("Game Data Saved!!!");
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void clearData() {	// -------------------------------------------------------------------------------------------------------------- CLEAR DATA
		player.die();
		highScore = 0;
	}
	
	
	
	public void loadData() {
		try (BufferedReader br = new BufferedReader(new FileReader("save.txt"))){
			String health = br.readLine().replace("Health: ", "");
			String score = br.readLine().replace("Score: ", "");
			String hiScore = br.readLine().replace("High Score: ", "");
			
			player.health = Integer.parseInt(health);
			this.score = Integer.parseInt(score);
			this.highScore = Integer.parseInt(hiScore);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	
}
