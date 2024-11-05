import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;

public class HeavyBullet extends Bullet{
	
	public static ArrayList<HeavyBullet> list = new ArrayList<HeavyBullet>();
	public static Queue<HeavyBullet> deleteQueue = new LinkedList<>();


	public HeavyBullet(String direction, int posX, int posY) {
		
		this.direction = direction;
		damage = 5;
		this.posX = posX;
		this.posY = posY + 16;
		speed = 6;

		if (direction == "right"){
			this.posX += 48;
		}
		else{
			this.posX -= 16;
		}


		try {
			bulletImage = ImageIO.read(getClass().getResourceAsStream("/sprites/objects/heavyBullet.png"));
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
		for (HeavyBullet h: deleteQueue){
			list.remove(h);
		}
		deleteQueue.clear();
	}
}
