import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class Enemy extends Entity{

    public static ArrayList<Enemy> list = new ArrayList<Enemy>();
    public static Queue<Enemy> deleteQueue = new LinkedList<>();


    public Enemy(){

        x = Math.random() < 0.5? -64: 1216;
        y = 512 - 128;
        direction = "right";
        speed = 2;
        health = 5;

        try {
            left1 = ImageIO.read(getClass().getResourceAsStream("/sprites/enemy/enemyLeft1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/sprites/enemy/enemyLeft2.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/sprites/enemy/enemyRight1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/sprites/enemy/enemyRight2.png"));
        }
            catch(IOException e) {
                e.printStackTrace();
        }
        list.add(this);
    }

    public void update(int playerX){

        direction = x >= playerX? "left": "right";

        if (direction == "right"){
            if (x <= playerX - 32){
                x += speed;
            }

        }
        else{
            if (x > playerX + 28){    
                x -= speed;
            }
        }

        for (LightBullet l: LightBullet.list){
            if (l.getX() >= x - 32 && l.getX() < x + 64){
                health -= l.getDamage();
                LightBullet.deleteQueue.add(l);
            }
        }
        for (HeavyBullet h: HeavyBullet.list){
            if (h.getX() >= x - 32 && h.getX() < x + 64){
                health -= h.getDamage();
                HeavyBullet.deleteQueue.add(h);
            }
        }


        if (health <= 0){
            GamePanel.score++;
            deleteQueue.add(this);
        }

        spriteCounter++;
				if (spriteCounter > 10){
					spriteNum = (spriteNum + 1) % 2;
					spriteCounter = 0;
                }
    }

    public BufferedImage getImage(){
        switch (direction){
            case "left":
                if (spriteNum == 0) return right1;
                else return right2;
            case "right":
                if (spriteNum == 0) return left1;
                else return left2;
        }
        return right1;
    }


    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    static public void cleanBullets(){
		for (Enemy e: deleteQueue){
			list.remove(e);
		}
		deleteQueue.clear();
	}

}
