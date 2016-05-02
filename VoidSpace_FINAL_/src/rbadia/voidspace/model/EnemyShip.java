package rbadia.voidspace.model;

import java.util.Random;

import rbadia.voidspace.main.GameScreen;

public class EnemyShip extends Ship
{
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_SPEED = 4;
	
	protected int shipWidth = 34;
	protected int shipHeight = 25;
	protected int speed = DEFAULT_SPEED;
	
	private Random rand = new Random();
	
	/**
	 * Creates a new enemy ship at the default initial location. 
	 * @param screen the game screen
	 */
	public EnemyShip(GameScreen screen){
		super(screen);
		this.setLocation(rand.nextInt(screen.getWidth() - shipWidth),0);
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