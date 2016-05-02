package rbadia.voidspace.model;

public class EnemyBullet extends Bullet
{
static int side; //for the super() constructor.
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnemyBullet(Ship ship) {
		super(ship,side);
			this.setLocation(ship.x + ((ship.width/2)+2) - bulletWidth/2,
					(ship.y+15) - bulletHeight + ship.getShipHeight());
		this.setSize(bulletWidth, bulletHeight);
	}

}
