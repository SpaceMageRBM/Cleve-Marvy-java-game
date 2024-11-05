import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{

	public boolean upPressed, downPressed, leftPressed, rightPressed, isMoving, lightAttackDown, heavyAttackDown;
	public boolean f1Toggled = false;
	private GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		//if (code == KeyEvent.VK_W) {
		//	upPressed = true;
		//	isMoving = true;
		//}
		if (code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_F1) {
			f1Toggled = !f1Toggled;
		}
		if (code == KeyEvent.VK_Q ){
			heavyAttackDown = true;
		}
		if (code == KeyEvent.VK_E){
			lightAttackDown = true;
		}


		isMoving = !downPressed && (leftPressed ^ rightPressed);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		//if (code == KeyEvent.VK_W) {
		//	upPressed = false;
		//	isMoving = false;
		//}
		if (code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if (code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if (code == KeyEvent.VK_Q ){
			heavyAttackDown = false;
		}
		if (code == KeyEvent.VK_E){
			lightAttackDown = false;
		}
		if (code == KeyEvent.VK_ESCAPE) {
			gp.saveData();
		}
		if (code == KeyEvent.VK_F9) {
			gp.clearData();
		}
		
		
		isMoving = !downPressed && (leftPressed ^ rightPressed);
	}
}
