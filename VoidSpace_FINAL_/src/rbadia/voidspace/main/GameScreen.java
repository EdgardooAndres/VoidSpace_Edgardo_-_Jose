package rbadia.voidspace.main;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Boss;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyBullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;
import rbadia.voidspace.sounds.SoundManager;


/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private BufferedImage backBuffer;
	private Graphics2D g2d;

	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 1000;

	private long lastShipTime;
	private long lastAsteroidTime;
	private long lastEnemyShipTime;
	private long lastBulletTime;
	private long lastBossTime;

	private Rectangle asteroidExplosion;
	private Rectangle shipExplosion;
	private Rectangle bossExplosion;

	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel scoreValueLabel;

	private Random rand;
	private Random asteroid_generator = new Random();

	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;

	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;

	private int gen = 0;
	private int BOSS_LIFE = 50;
	int bossLife=20;

	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();

		initialize();

		// initialize graphics manager
		graphicsMan = new GraphicsManager();

		// initialize back buffer image
		backBuffer = new BufferedImage(711, 400, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	private void initialize() 
	{
		// set panel properties
		this.setSize(new Dimension(711, 400));
		this.setPreferredSize(new Dimension(711, 400));
		this.setBackground(Color.BLACK);
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}

	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){

		if(status.isNewAsteroid()){
			gen = (asteroid_generator.nextInt(3));}

		// set original font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}

		// erase screen
		graphicsMan.drawbackImg(graphicsMan.getBackImg(), g2d, this);

		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			drawGetReady();
			return;
		}

		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();

			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
			return;
		}

		// the game has not started yet
		if(!status.isGameStarted()){
			// draw game title screen
			initialMessage();
			return;
		}

		//draw the level on the screen.
		drawLevel();

		// draw ship
		drawShip();

		// draw bullets
		drawBullets();

		// check enemy bullet-ship collisions
		checkEnemyBullet_ShipCollisions();

		if(gameLogic.Level()%10 != 0 || gameLogic.Level()==0)
		{
			// draw asteroid
			drawAsteroid();

			// check bullet-asteroid collisions
			checkBullet_AsteroidCollisions();

			// check ship-asteroid collisions
			checkShip_AdteroidCollisions();

			// draw enemy ship
			drawEnemyShip();
			// check ship-ship collisions
			checkEnemyShip_ShipCollisions();

			// check bullet-enemy ship collisions
			checkBullet_EnemyShipCollisions();

			// draw enemy bullets
			drawEnemyBullets(); 
		}
		else 
		{

			//BOSS
			drawBoss();
			drawBossBullets();
			checkBoss_ShipCollisions();
			checkBullet_BossCollisions();
		}

		// update asteroids destroyed label
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));

		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));

		//update Score
		scoreValueLabel.setText(Integer.toString(status.getScore()));

	}

	/**
	 * Draws the "Game Over" message.
	 */
	private void drawGameOver() {
		String gameOverStr = "GAME OVER";
		graphicsMan.drawbackGifImg(graphicsMan.getBackGifImg(), g2d, this);
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);
	}

	/**
	 * Draws the initial "Get Ready!" message.
	 */
	private void drawGetReady() {
		String readyStr = "Get Ready!";
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}
	/**
	 * Draws the level on the screen.
	 */	
	private void drawLevel() {
		String levelStr = "Level "+ Integer.toString(gameLogic.Level());
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(levelStr);
		int strX = strWidth;//(this.getWidth() - strWidth)/2;
		int strY = ascent;//(this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(levelStr, strX, strY);
	}
	/**
	 * Display initial game title screen.
	 */
	private void initialMessage() {
		String gameTitleStr = "Void Space";
		graphicsMan.drawbackGifImg(graphicsMan.getBackGifImg(), g2d, this);
		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(gameTitleStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);

	}

	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
	}
	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;

		bigFont = originalFont;
		biggestFont = null;

		// set labels' text
		shipsValueLabel.setForeground(Color.BLACK);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		scoreValueLabel.setText(Long.toString(status.getScore()));
	}

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}
	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}
	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}
	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setScoreValueLabel(JLabel scoreValueLabel) {
		this.scoreValueLabel = scoreValueLabel;
	}

	//Methods used every screen update.
	/**
	 * 	Draws the asteroids in a random position and moves each asteroid
	 * with the same direction on different locations.
	 */
	public void drawAsteroid()
	{	
		for(int i=0; i<=gameLogic.Level(); i++)
		{
			List<Asteroid> asteroids = gameLogic.getAsteroids();
			Asteroid asteroid = asteroids.get(i);
			if(!status.isNewAsteroid()){
				// draw the asteroid until it reaches the bottom of the screen
				if(asteroid.getY() + asteroid.getSpeed() < this.getHeight()){
					switch (gen) {
					case 0:
						asteroid.translate(0 , asteroid.getSpeed());
						break;
					case 1:
						asteroid.translate(asteroid.getSpeed() , asteroid.getSpeed());
						break;
					case 2:
						asteroid.translate(-asteroid.getSpeed() , asteroid.getSpeed());
						break;
					}
					graphicsMan.drawAsteroid(asteroid, g2d, this);
				}
				else{
					asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
					gen = (asteroid_generator.nextInt(3));
				}
			}
			else{
				long currentTime = System.currentTimeMillis();
				if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
					// draw a new asteroid
					lastAsteroidTime = currentTime;
					status.setNewAsteroid(false);
					asteroid.setLocation(rand.nextInt(getWidth() - asteroid.width), 0);
				}
				else{
					// draw explosion
					graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
				}
			}
		}
	}
	/**
	 * 	Draws the player ship's bullets.
	 */
	public void drawBullets()
	{	
		gameLogic.getShip();
		List<Bullet> bullets = gameLogic.getBullets();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);

			boolean remove = gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
	}
	/**
	 * Draws the enemies ships' bullets.
	 */
	public void drawEnemyBullets()  
	{	
		List<EnemyBullet> enemyBullets = gameLogic.getEnemyBullets();
		EnemyShip enemyShip = gameLogic.getEnemyShip();
		for(int i=0; i<enemyBullets.size(); i++)
		{
			EnemyBullet enemyBullet = enemyBullets.get(i);
			graphicsMan.drawEnemyBullet(enemyBullet, g2d, this);

			boolean remove = gameLogic.moveEnemyBullet(enemyBullet);
			if(remove){
				enemyBullets.remove(i);
				i--;
			}
		}
		if(isEnemyBullet())
			gameLogic.fireEnemyBullet(enemyShip);
	}
	/**
	 * Draws the Boss' ship's bullets.
	 */
	public void drawBossBullets()
	{
		List<EnemyBullet> enemyBullets = gameLogic.getEnemyBullets();
		Boss boss = gameLogic.getBoss();
		for(int i=0; i<enemyBullets.size(); i++)
		{
			EnemyBullet enemyBullet = enemyBullets.get(i);
			graphicsMan.drawEnemyBullet(enemyBullet, g2d, this);

			boolean remove = gameLogic.moveBossBullet(enemyBullet);
			if(remove){
				enemyBullets.remove(i);
				i--;
			}
		}
		//		if(!isEnemyBullet())
		gameLogic.fireEnemyBullet(boss);
	}
	/**
	 * Draws the player's ship.
	 */
	public void drawShip()
	{	
		Ship ship = gameLogic.getShip();
		if(!status.isNewShip()){
			// draw it in its current location
			//draw thruster if shift is pressed
			if(InputHandler.shiftPressed())
			{
				graphicsMan.drawShipThruster(ship, g2d, this);
			}
			else{
				{graphicsMan.drawShip(ship, g2d, this);}}}
		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastShipTime) > NEW_SHIP_DELAY){
				lastShipTime = currentTime;
				status.setNewShip(false);
				ship = gameLogic.newShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			}
		}
	}
	/**
	 * Draws the enemy ship.
	 */
	public void drawEnemyShip()
	{	

		EnemyShip enemyShip = gameLogic.getEnemyShip();
		if(!status.isNewEnemyShip()){
			// draw the asteroid until it reaches the bottom of the screen
			if(enemyShip.getY() + enemyShip.getSpeed() < this.getHeight()){
				enemyShip.translate(0, enemyShip.getSpeed());
				graphicsMan.drawEnemyShip(enemyShip, g2d, this);
			}
			else{
				enemyShip.setLocation(rand.nextInt(getWidth() - enemyShip.width), 0);
			}
		}
		else{
			// draw a new one
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastEnemyShipTime) > NEW_ASTEROID_DELAY){
				lastEnemyShipTime = currentTime;
				status.setNewEnemyShip(false);
				enemyShip = gameLogic.newEnemyShip(this);
			}
			else{
				// draw explosion
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}
	}
	/**
	 * draws the Boss' ship.
	 */
	public void drawBoss()
	{	
		Boss boss = gameLogic.getBoss();
		//this.bossLife = ;
		if(bossLife!=0)
		{
			graphicsMan.drawBoss(boss, g2d, this);

			long currentTime = System.currentTimeMillis();

			if((currentTime - lastBossTime) > 2000)
			{
				lastBossTime = currentTime;
				boss.setLocation(rand.nextInt(getWidth() - 300), 0);
			}
		}
		else{
			bossExplosion = new Rectangle(
					boss.x,
					boss.y,
					boss.width,
					boss.height);
			graphicsMan.drawBossExplosion(bossExplosion, g2d, this);
			status.setScore(status.getScore() + 5000);
			status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 5);
			this.bossLife = BOSS_LIFE;
		}
	}
	/**
	 * Check whether a user bullet hits an asteroid.
	 */
	public void checkBullet_AsteroidCollisions()
	{	
		List<Asteroid> asteroids = gameLogic.getAsteroids();
		List<Bullet> bullets = gameLogic.getBullets();
		for(int i=0; i<bullets.size(); i++)
		{
			for(int j=0; j<asteroids.size(); j++)
			{
				Bullet bullet = bullets.get(i);
				Asteroid asteroid = asteroids.get(j);
				if(asteroid.intersects(bullet))
				{
					// increase asteroids destroyed count
					status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
					status.setScore(status.getScore() + 100);
					// "remove" asteroid
					asteroidExplosion = new Rectangle(
							asteroid.x,
							asteroid.y,
							asteroid.width,
							asteroid.height);
					asteroid.setLocation(-asteroid.width, -asteroid.height);
					graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);

					if (status.getAsteroidsDestroyed() < 5) {
						status.setNewAsteroid(true);
					}

					asteroids.remove(j);
					lastAsteroidTime = System.currentTimeMillis();

					// play asteroid explosion sound
					soundMan.playAsteroidExplosionSound();

					// remove bullet
					bullets.remove(i);
					break;
				}
			}
		}
	}
	/**
	 * Check whether an enemy bullet hits the user's ship.
	 */
	public void checkEnemyBullet_ShipCollisions()
	{	
		Ship ship = gameLogic.getShip();
		List<EnemyBullet> enemyBullets = gameLogic.getEnemyBullets();

		for(int i=0; i<enemyBullets.size(); i++){
			EnemyBullet enemyBullet = enemyBullets.get(i);
			if(enemyBullet.intersects(ship)){
				// decrease ships left count
				status.setShipsLeft(status.getShipsLeft() - 1);

				// "remove" enemy Ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();

				// remove bullet
				enemyBullets.remove(i);
				break;
			}
		}		
	}
	/**
	 * Check whether a user bullet hits an enemy ship.
	 */
	public void checkBullet_EnemyShipCollisions()
	{	
		EnemyShip enemyShip = gameLogic.getEnemyShip();
		List<Bullet> bullets = gameLogic.getBullets();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(enemyShip.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);
				status.setScore(status.getScore() + 500);

				// "remove" enemy Ship
				asteroidExplosion = new Rectangle(
						enemyShip.x,
						enemyShip.y,
						enemyShip.width,
						enemyShip.height);
				enemyShip.setLocation(-enemyShip.width, -enemyShip.height);
				status.setNewEnemyShip(true);
				lastEnemyShipTime = System.currentTimeMillis();

				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();

				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
	/**
	 * Check whether a user bullet hits the boss.
	 */
	public void checkBullet_BossCollisions()
	{	
		Boss boss = gameLogic.getBoss();
		List<Bullet> bullets = gameLogic.getBullets();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(boss.intersects(bullet)){
				bossLife--;

				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();

				// remove bullet
				bullets.remove(i);
				break;
			}
		}
	}
	/**
	 * Check whether a user's ship hits an asteroid.
	 */
	public void checkShip_AdteroidCollisions()
	{	
		Ship ship = gameLogic.getShip();
		List<Asteroid> asteroids = gameLogic.getAsteroids();
		for(int i=0; i<asteroids.size(); i++)
		{
			Asteroid asteroid = asteroids.get(i);
			if(asteroid.intersects(ship)){
				// decrease number of ships left
				status.setShipsLeft(status.getShipsLeft() - 1);

				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

				// "remove" asteroid
				asteroidExplosion = new Rectangle(
						asteroid.x,
						asteroid.y,
						asteroid.width,
						asteroid.height);
				asteroid.setLocation(-asteroid.width, -asteroid.height);
				status.setNewAsteroid(true);
				lastAsteroidTime = System.currentTimeMillis();

				// "remove" ship
				shipExplosion = new Rectangle(
						ship.x,
						ship.y,
						ship.width,
						ship.height);
				ship.setLocation(this.getWidth() + ship.width, -ship.height);
				status.setNewShip(true);
				lastShipTime = System.currentTimeMillis();

				// play ship explosion sound
				soundMan.playShipExplosionSound();
				// play asteroid explosion sound
				soundMan.playAsteroidExplosionSound();
			}
		}
	}
	/**
	 * Check whether a enemy ship hits the user's ship.
	 */
	public void checkEnemyShip_ShipCollisions()
	{	
		Ship ship = gameLogic.getShip();
		EnemyShip enemyShip = gameLogic.getEnemyShip();
		if(enemyShip.intersects(ship)){
			// decrease number of ships left
			status.setShipsLeft(status.getShipsLeft() - 1);
			status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 1);

			// "remove" enemy ship
			asteroidExplosion = new Rectangle(
					enemyShip.x,
					enemyShip.y,
					enemyShip.width,
					enemyShip.height);
			enemyShip.setLocation(-enemyShip.width, -enemyShip.height);
			status.setNewEnemyShip(true);
			lastEnemyShipTime = System.currentTimeMillis();

			// "remove" ship
			shipExplosion = new Rectangle(
					ship.x,
					ship.y,
					ship.width,
					ship.height);
			ship.setLocation(this.getWidth() + ship.width, -ship.height);
			status.setNewShip(true);
			lastShipTime = System.currentTimeMillis();

			// play ship explosion sound
			soundMan.playShipExplosionSound();
			// play enemy ship explosion sound
			soundMan.playAsteroidExplosionSound();
		}
	}
	/**
	 * Check whether the boss hits the user's ship.
	 */
	public void checkBoss_ShipCollisions()
	{	
		Ship ship = gameLogic.getShip();
		Boss boss = gameLogic.getBoss();
		if(boss.intersects(ship)){
			// decrease number of ships left
			status.setShipsLeft(status.getShipsLeft() - 1);

			// "remove" one life.
			bossLife--;

			// "remove" ship
			shipExplosion = new Rectangle(
					ship.x,
					ship.y,
					ship.width,
					ship.height);
			ship.setLocation(this.getWidth() + ship.width, -ship.height);
			status.setNewShip(true);
			lastShipTime = System.currentTimeMillis();

			// play ship explosion sound
			soundMan.playShipExplosionSound();
			// play enemy ship explosion sound
			soundMan.playAsteroidExplosionSound();
		}
	}

	/**
	 * If it is time for a ship to shoot.
	 * @return true if it is time to shoot.
	 */
	public boolean isEnemyBullet()
	{
		long currentTime = System.currentTimeMillis();
		if((currentTime - lastBulletTime) > 1000/3)
		{
			lastBulletTime = currentTime;
			return true ;
		}
		return false;
	}
}