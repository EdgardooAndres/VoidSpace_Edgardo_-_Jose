package rbadia.voidspace.model;
import java.awt.Rectangle;

/**
 * Represents a bullet fired by a ship.
 */
public class Bullet extends Rectangle {
	private static final long serialVersionUID = 1L;
	
	protected int bulletWidth = 8;
	protected int bulletHeight = 8;
	protected int speed = 12;

	/**
	 * Creates a new bullet above the ship, centered on it
	 * @param ship
	 */
	public Bullet(Ship ship, int side) {
		switch(side)
		{
		case 1:
		this.setLocation(ship.x + (ship.width/2+14) - bulletWidth/2,
				ship.y - bulletHeight);
		break;
		case 2:
			this.setLocation(ship.x + (ship.width/2-10) - bulletWidth/2,
					ship.y - bulletHeight);
			break;
		}
		this.setSize(bulletWidth, bulletHeight);
	}


	/**
	 * Return the bullet's speed.
	 * @return the bullet's speed.
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Set the bullet's speed
	 * @param speed the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
