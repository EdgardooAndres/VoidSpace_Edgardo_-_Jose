package rbadia.voidspace.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Timer;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Boss;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyBullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;


/**
 * Handles general game logic and status.
 */
public class GameLogic {
	private GameScreen gameScreen;
	private GameStatus status;
	private SoundManager soundMan;

	private Ship ship;
	private EnemyShip enemyShip;
	private Asteroid asteroid;
	private Boss boss;
	private List<Bullet> bullets;
	private List<EnemyBullet> enemyBullets;
	private List<Asteroid> asteroids;

	/**
	 * Create a new game logic handler
	 * @param gameScreen the game screen
	 */
	public GameLogic(GameScreen gameScreen){
		this.gameScreen = gameScreen;

		// initialize game status information
		status = new GameStatus();
		// initialize the sound manager
		soundMan = new SoundManager();

		// initialize some variables
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<EnemyBullet>();
	}

	/**
	 * Returns the game status
	 * @return the game status 
	 */
	public GameStatus getStatus() {
		return status;
	}

	public SoundManager getSoundMan() {
		return soundMan;
	}
	public GameScreen getGameScreen() {
		return gameScreen;
	}

	/**
	 * Prepare for a new game.
	 */
	public void newGame(){
		status.setGameStarting(true);

		// initiate game variables
		bullets = new ArrayList<Bullet>();
		enemyBullets = new ArrayList<EnemyBullet>();
		asteroids = new ArrayList<Asteroid>();

		status.setShipsLeft(5);
		status.setGameOver(false);
		status.setAsteroidsDestroyed(0);
		status.setScore(0);
		status.setNewAsteroid(false);
		status.setNewEnemyShip(false);

		// initialize the ship and the asteroid
		newShip(gameScreen);
		newAsteroid(gameScreen);
		newAsteroids(gameScreen);
		newEnemyShip(gameScreen);
		newBoss(gameScreen);

		// prepare game screen
		gameScreen.doNewGame();

		// delay to display "Get Ready" message for 1.5 seconds
		Timer timer = new Timer(1000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameStarting(false);
				status.setGameStarted(true);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	/**
	 * Check game or level ending conditions.
	 */
	public void checkConditions(){
		// check game over conditions
		if(!status.isGameOver() && status.isGameStarted()){
			if(status.getShipsLeft() == 0){
				gameOver();
			}
		}
	}

	/**
	 * Actions to take when the game is over.
	 */
	public void gameOver(){
		status.setGameStarted(false);
		status.setGameOver(true);
		gameScreen.doGameOver();

		// delay to display "Game Over" message for 3 seconds
		Timer timer = new Timer(3000, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				status.setGameOver(false);
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	//Create Bullets
	/**
	 * Returns the list of bullets.
	 * @return the list of bullets
	 */
	public List<Bullet> getBullets() {
		return bullets;
	}
	public List<EnemyBullet> getEnemyBullets() 
	{
		return enemyBullets;
	}
	
	//Fire Bullets
	/**
	 * Fire a bullet from ship.
	 */
	public void fireBullet(){
		Bullet bulletRight = new Bullet(ship,1);
		Bullet bulletLeft = new Bullet(ship,2);
		bullets.add(bulletRight);
		bullets.add(bulletLeft);
		soundMan.playBulletSound();
	}
	public void fireEnemyBullet(Ship ship){

		EnemyBullet enemyBullet = new EnemyBullet(ship);
		enemyBullets.add(enemyBullet);
		soundMan.playBulletSound();
	}

	//Move Bullets.
	/**
	 * Move a bullet once fired.
	 * @param bullet the bullet to move
	 * @return if the bullet should be removed from screen
	 */
	public boolean moveBullet(Bullet bullet){
		if(bullet.getY() - bullet.getSpeed() >= 0){
			bullet.translate(0, -bullet.getSpeed());
			return false;
		}
		else{
			return true;
		}
	}
	public boolean moveEnemyBullet(EnemyBullet bullet)
	{
		if(bullet.getY() - bullet.getSpeed() >= 0)
		{
			bullet.translate(0, bullet.getSpeed());
			return false;
		}
		else{
			return true;
		}
	}
	public boolean moveBossBullet(EnemyBullet bullet)
	{ 
		int gen=0;
		if(gameScreen.isEnemyBullet())
			gen = new Random().nextInt(3);

		if(bullet.getY() - bullet.getSpeed() >= 0)
		{
			//			if(gameScreen.isEnemyBullet())
			gen = new Random().nextInt(3);

			switch (gen) {
			case 0:
				bullet.translate(2, bullet.getSpeed());
				return false;
			case 1:
				bullet.translate(-2, bullet.getSpeed());
				return false;
			case 2:
				bullet.translate(0, bullet.getSpeed());
				return false;
			}
		}
		return true;
	}

	//Spaceships.
	/**
	 * Create a new ship (and replace current one).
	 */
	public Ship newShip(GameScreen screen){
		this.ship = new Ship(screen);
		return ship;
	}
	/**
	 * Returns the ship.
	 * @return the ship
	 */
	public Ship getShip() {
		return ship;
	}
	public EnemyShip newEnemyShip(GameScreen screen){
		this.enemyShip = new EnemyShip(screen);
		return enemyShip;
	}
	public EnemyShip getEnemyShip() {
		return enemyShip;
	}
	
	//Asteroids.
	/**
	 * Create a new asteroid.
	 */
	public Asteroid newAsteroid(GameScreen screen){
		this.asteroid = new Asteroid(screen);
		return asteroid;
	}
	public List<Asteroid> newAsteroids(GameScreen screen){
		int i = 0;
		while(i<=Level() && asteroids.size()<=Level())
		{
			asteroids.add(new Asteroid(screen));
			i++;
		}
		return asteroids;
	}
	/**
	 * Returns the asteroid.
	 * @return the asteroid
	 */
	public Asteroid getAsteroid() {
		return asteroid;
	}
	public List<Asteroid> getAsteroids()  {
		newAsteroids(gameScreen);
		return asteroids;
	}

	//Boss.
	/**
	 * Creates a powerful enemy called 'The Boss".
	 * @param screen the game screen.
	 * @return the boss of the game.
	 */
	public Boss newBoss(GameScreen screen){
		this.boss = new Boss(screen);
		return boss;
	}
	public Boss getBoss() {
		return boss;
	}

	/**
	 * Calculates the level in which the user is playing.
	 * Starts at level 0.
	 * @return the level at which the user is playing.
	 */
	public int Level()
	{
		return status.getAsteroidsDestroyed()/5;
	}
}