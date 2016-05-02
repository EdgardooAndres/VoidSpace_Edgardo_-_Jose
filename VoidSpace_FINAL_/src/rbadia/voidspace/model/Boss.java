package rbadia.voidspace.model;

import rbadia.voidspace.main.GameScreen;

public class Boss extends EnemyShip 
{
	private static final long serialVersionUID = 1L;
	protected int shipWidth = 300;
	protected int shipHeight = 190;
	
	public Boss(GameScreen screen) {
		super(screen);
		this.setSize(shipWidth, shipHeight);
	}
	@Override
	public int getShipWidth() {
		return shipWidth;
	}
	
	@Override
	public int getShipHeight() {
		return shipHeight;
	}
	
	@Override
	public int getSpeed() {
		return speed;
	}
	
	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	@Override
	public int getDefaultSpeed(){
		return DEFAULT_SPEED;
	}

}
