package rbadia.voidspace.graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.Boss;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.EnemyShip;
import rbadia.voidspace.model.Ship;

/**
 * Manages and draws game graphics and images.
 */
public class GraphicsManager {
	private BufferedImage shipImg;
	private BufferedImage enemyImg;
	private BufferedImage bulletImg;
	private BufferedImage asteroidExplosionImg;
	private BufferedImage shipExplosionImg;
	private BufferedImage shipThrusterImg;
	private BufferedImage backImg;
	private Image asteroidImg, backGifImg, bossExplosion,bossImg;




	/**
	 * Creates a new graphics manager and loads the game images.
	 */
	public GraphicsManager(){
		// load images
		try {
			//static images
			this.setBackImg(ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/backImage.jpg")));
			this.enemyImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/rsz_1spo.png"));
			this.shipImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/spaceship.png"));
			this.asteroidExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/asteroidExplosion.png"));
			this.shipExplosionImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/shipExplosion.png"));
			this.shipThrusterImg=ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/shift.png"));
			this.bulletImg = ImageIO.read(getClass().getResource("/rbadia/voidspace/graphics/bullet.png"));
			//.gif format images
			this.backGifImg = new ImageIcon(getClass().getResource("/rbadia/voidspace/graphics/voidspacegig.gif")).getImage();
			this.asteroidImg = new ImageIcon(getClass().getResource("/rbadia/voidspace/graphics/asteroid.gif")).getImage();
			this.bossImg = new ImageIcon(getClass().getResource("/rbadia/voidspace/graphics/bienveBoss.gif")).getImage();
			this.bossExplosion = new ImageIcon(getClass().getResource("/rbadia/voidspace/graphics/bienveExplosion.gif")).getImage();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "The graphic files are either corrupt or missing.",
					"VoidSpace - Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	//draw ships and enemies.
	/**
	 * Draws a ship image to the specified graphics canvas.
	 * @param ship the ship to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawShip(Ship ship, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipImg, ship.x, ship.y, observer);
	}
	public void drawShipThruster(Ship ship, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipThrusterImg, ship.x, ship.y, observer);
	}	
	public void drawEnemyShip(EnemyShip enemyShip, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(enemyImg, enemyShip.x, enemyShip.y, observer);
	}
	public void drawBoss(Boss boss, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bossImg, boss.x, boss.y, observer);
	}
	/**
	 * Draws an asteroid image to the specified graphics canvas.
	 * @param asteroid the asteroid to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroid(Asteroid asteroid, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidImg, asteroid.x, asteroid.y, observer);
	}
	
	//draw backgrounds.
	/**
	 * Draws the background for the 'menu' and game over displays.
	 * @param imageIcon Dynamic background in '.gif' extension format.
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawbackGifImg(Image imageIcon,Graphics2D g2d , ImageObserver observer){
		g2d.drawImage(imageIcon, 0, 0, observer);
	}
	/**
	 * Draws the background for the 'gameplay'.
	 * @param imageIcon Dynamic background in '.gif' extension format.
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawbackImg(Image backImg,Graphics2D g2d , ImageObserver observer){
		g2d.drawImage(backImg, 0, 0, observer);
	}

	//draw bullets.
	/**
	 * Draws a bullet image to the specified graphics canvas.
	 * @param bullet the bullet to draw
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawBullet(Bullet bullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bulletImg, bullet.x, bullet.y, observer);
	}
	public void drawEnemyBullet(Bullet bullet, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bulletImg, bullet.x, bullet.y, observer);
	}

//draw explosions.
	/**
	 * Draws a ship explosion image to the specified graphics canvas.
	 * @param shipExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawShipExplosion(Rectangle shipExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipExplosionImg, shipExplosion.x, shipExplosion.y, observer);
	}
	/**
	 * Draws an asteroid explosion image to the specified graphics canvas.
	 * @param asteroidExplosion the bounding rectangle of the explosion
	 * @param g2d the graphics canvas
	 * @param observer object to be notified
	 */
	public void drawAsteroidExplosion(Rectangle asteroidExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(asteroidExplosionImg, asteroidExplosion.x, asteroidExplosion.y, observer);
	}
	public void drawEnemyShipExplosion(Rectangle enemyShipExplosion, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(shipExplosionImg, enemyShipExplosion.x, enemyShipExplosion.y, observer);
	}
	public void drawBossExplosion(Rectangle bossExplosion2, Graphics2D g2d, ImageObserver observer) {
		g2d.drawImage(bossExplosion, bossExplosion2.x, bossExplosion2.y, observer);
	}

	//image getters and setters.
	public Image getBackGifImg() {
		return backGifImg;
	}
	public void setBackGifImg(Image imageIcon) {
		this.backGifImg = imageIcon;
	}
	public BufferedImage getBackImg() {
		return backImg;
	}
	public void setBackImg(BufferedImage backImg) {
		this.backImg = backImg;
	}
	public Image getBossImg() {
		return bossImg;
	}
	public void setBossImg(BufferedImage bossImg) {
		this.bossImg = bossImg;
	}
}