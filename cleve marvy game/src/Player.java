import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player extends Entity implements combatActions{

	GamePanel gp;
	KeyHandler keyH;
	
	BufferedImage shield1, shield2, shield3, shield4, shield5;
	int healthCharge = 0;
	int damageCooldown = 0;
	int bulletCooldown = 0;
	int shieldSpriteCounter = 0;
	int shieldSpriteNum = 0;
	public Player(GamePanel gp, KeyHandler keyH) {
		this.gp = gp;
		this.keyH = keyH;
	}
	
	public void setDefaultValues() {
		x = gp.screenWidth / 2;
		y = gp.screenHeight - (gp.tileSize * 2);
		speed = 4;
		health = 3;
		healthCharge = 0;
	}
	
	public void getPlayerImage() {
		try {
			left1 = ImageIO.read(getClass().getResourceAsStream("/sprites/left1.png"));
			left2 = ImageIO.read(getClass().getResourceAsStream("/sprites/left2.png"));
			leftpow1 = ImageIO.read(getClass().getResourceAsStream("/sprites/leftpow1.png"));
			leftpow2 = ImageIO.read(getClass().getResourceAsStream("/sprites/leftpow2.png"));
			right1 = ImageIO.read(getClass().getResourceAsStream("/sprites/right1.png"));
			right2 = ImageIO.read(getClass().getResourceAsStream("/sprites/right2.png"));
			rightpow1 = ImageIO.read(getClass().getResourceAsStream("/sprites/rightpow1.png"));
			rightpow2 = ImageIO.read(getClass().getResourceAsStream("/sprites/rightpow2.png"));
			
			shield1 = ImageIO.read(getClass().getResourceAsStream("/sprites/shield/shield1.png"));
			shield2 = ImageIO.read(getClass().getResourceAsStream("/sprites/shield/shield2.png"));
			shield3 = ImageIO.read(getClass().getResourceAsStream("/sprites/shield/shield3.png"));
			shield4 = ImageIO.read(getClass().getResourceAsStream("/sprites/shield/shield4.png"));
			shield5 = ImageIO.read(getClass().getResourceAsStream("/sprites/shield/shield5.png"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		
		if (keyH.downPressed) {
			shield();
			shieldSpriteCounter++;
			if (shieldSpriteCounter > 5){
				shieldSpriteNum = (shieldSpriteNum + 1) % 5;
				shieldSpriteCounter = 0;
			}
		}
		else{
			healthCharge = Math.abs(healthCharge - 1);
			if (keyH.isMoving) {
				if (keyH.leftPressed) {
					direction = "left";
					if (x > (gp.tileSize - 16)) x -= speed;
				}
				if (keyH.rightPressed) {
					direction = "right";
					if (x < gp.screenWidth - ((gp.tileSize * 2) - 16)) x += speed;
				}

				spriteCounter++;
				if (spriteCounter > 10){
					spriteNum = (spriteNum + 1) % 2;
					spriteCounter = 0;
				}
			}
			else{
				spriteCounter = 0;
				spriteNum = 0;
			}

			if (keyH.lightAttackDown){
				if (bulletCooldown <= 0){
					shoot(false);
					bulletCooldown += 20;
				}
			}
			else if (keyH.heavyAttackDown){
				if (bulletCooldown <= 0){
					shoot(true);
					bulletCooldown += 60;
				}

			}

		}
	
		if (bulletCooldown > 0){
			bulletCooldown--;
		}
		
		for (Enemy e: Enemy.list){
			if (e.x >= x - 32 && e.x < x + 29){
				health--;
				Enemy.deleteQueue.add(e);
			}
		}

		if (health <= 0){
			die();
		}


	}
	
	public void draw(Graphics2D g2) {
		//g2.setColor(Color.white);
		//g2.fillRect(x, y, gp.tileSize, gp.tileSize);
		
		BufferedImage image = left1;
		
		 if (keyH.downPressed) {
			switch (shieldSpriteNum) {
			case 0:
				image = shield1;
				break;
			case 1:
				image = shield2;
				break;
			case 2:
				image = shield3;
				break;
			case 3:
				image = shield4;
				break;
			case 4 :
				image = shield5;
				break;
			}
				
		} else if (keyH.lightAttackDown || keyH.heavyAttackDown){
			switch(direction) {
				case "left": 
					if (spriteNum == 0) image = leftpow1; 
					if (spriteNum == 1) image = leftpow2;
					break;
				case "right": 
					if (spriteNum == 0) image = rightpow1; 
					if (spriteNum == 1) image = rightpow2;
					break;
				}
		}else{
			switch(direction) {
				case "left": 
					if (spriteNum == 0) image = left1; 
					if (spriteNum == 1) image = left2;
					break;
				case "right": 
					if (spriteNum == 0) image = right1; 
					if (spriteNum == 1) image = right2;
					break;
				}
		}
		
		g2.drawImage(image, x, y, (int)(gp.tileSize), (int)(gp.tileSize), null);
		
		
		
	}
	
	public int getCharge(){
		return healthCharge;
	}

	public void die(){
		for (Enemy e: Enemy.list){
			Enemy.deleteQueue.add(e);
		}
		GamePanel.score = 0;
		setDefaultValues();
	}

	@Override
	public void shoot(boolean isHeavy) {
		if (isHeavy) {
			new HeavyBullet(direction, x, y);
		}
		else {
			new LightBullet(direction, x, y);
		}
		
	}

	@Override
	public void shield() {
		healthCharge = healthCharge + 6;
		if (healthCharge >= 1800){
			healthCharge %= 1800;
			if (health < 3) health++;
		}
	}
	
}
