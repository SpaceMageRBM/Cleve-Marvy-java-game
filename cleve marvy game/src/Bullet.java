import java.awt.image.BufferedImage;

public abstract class Bullet {
	protected BufferedImage bulletImage;
	protected int damage;
	protected int speed;
	protected int posX, posY;
	protected String direction;
	
	public BufferedImage getTexture() {
		return bulletImage;
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getSpeed() {
		return speed;
	}
}
