import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class LightBullet extends Bullet{

	public static ArrayList<LightBullet> list = new ArrayList<LightBullet>();
	public static Queue<LightBullet> deleteQueue = new LinkedList<>();

	public LightBullet(String direction, int posX, int posY) {

		this.direction = direction;
		damage = 2;
		this.posX = posX;
		this.posY = posY + 8;
		speed = 8;

		if (direction == "right"){
			this.posX += 48;
		}
		else{
			this.posX -= 16;
		}


		try {
			if (direction == "right"){
				bulletImage = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/lightBulletRight.png"));
			}
			else{
				bulletImage = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/lightBulletLeft.png"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		list.add(this);
		
	}
	
	
	public void updateBullet(){
		if (direction == "right"){
			posX += speed;
		}
		else if(direction == "left"){
			posX -= speed;
		}

		if (posX < -64 || posX > 1152){
			deleteQueue.add(this);
		}

	}

	static public void cleanBullets(){
		for (LightBullet l: deleteQueue){
			list.remove(l);
		}
		deleteQueue.clear();
	}
	
}
