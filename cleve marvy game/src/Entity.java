import java.awt.image.BufferedImage;

public class Entity{
	
	protected int health;
	public int x, y;
	public int speed;
	
	public BufferedImage left1, left2, leftpow1, leftpow2, right1, right2, rightpow1, rightpow2;
	public String direction = "right";
	
	public int spriteCounter = 0;
	public int spriteNum = 1;
	
	public int getHealth() {
		return health;
	}

	public void changeHealth(int damage){
		health += damage;
	}
}