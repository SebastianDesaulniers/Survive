package core;

import graphics.Tileset;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static core.Shared.game;

/**
 * Created by minnow on 12/8/17
 */
public class Entity extends Point implements Serializable
{
	private Random random = new Random();

	private int width = 64, height = 64;
	private float xVel = -2, yVel = 2;

	private String direction = "down";

	private transient org.newdawn.slick.opengl.Texture texture = null;

	private String name = null;

	private String spriteType = "man";

	private int animationFrame = 0;
	private float animationDelayFrame = 0;
	private float animationSpeed = 2.3f;

	public Stats stats = new Stats();

	private float maxxVel = 0;
	private float maxyVel = 0;

	public Move[] moves = new Move[5];
	public ArrayList<Item> items = new ArrayList<>();
	public HashMap<String, Integer[]> itemsUsed = new HashMap<>();

	public Tileset image = new Tileset(64, 64);

	public void input(String in)
	{
		if (getxVel() == 0 && getyVel() == 0)
			setAnimationFrame(0);

		direction = in;
		updateAnimationFrame();
	}

	public Move getRandomMove()
	{
		return moves[random.nextInt(moves.length)];
	}

	private void updateAnimationFrame()
	{
		setAnimationDelayFrame(getAnimationDelayFrame() + getAnimationSpeed());

		if (getAnimationDelayFrame() * getAnimationSpeed() >= getAnimationSpeed() * game.config.images.get(getSpriteType()).getTexture(getDirection()).length)
		{
			setAnimationFrame(getAnimationFrame() + 1);
			setAnimationDelayFrame(0);
		}

		if (getAnimationFrame() >= game.config.images.get(getSpriteType()).getTexture(getDirection()).length)
		{
			setAnimationFrame(1);
			setAnimationDelayFrame(0);
		}
	}

	private void updateAnimationFrame(int n)
	{
		setAnimationFrame(n);
		setAnimationDelayFrame(n);
	}

	public Entity(float x, float y)
	{
		super(x, y);

	}

	public float getxVel()
	{
		return xVel;
	}

	public float getyVel()
	{
		return yVel;
	}

	public void setxVel(float xVel)
	{
		if (xVel != 0)
			setMaxxVel(xVel);
		this.xVel = xVel;
	}

	public void setyVel(float yVel)
	{
		if (yVel != 0)
			setMaxyVel(yVel);
		this.yVel = yVel;
	}


	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public Rectangle toRect()
	{
		return new Rectangle((int) getX(), (int) getY(), getWidth(), getHeight());
	}

	private boolean collides(Entity entity)
	{
		return toRect().intersects(entity.toRect());
	}

	public int getID(int i)
	{
		if (i >= GameState.LAYER_AMOUNT || i < 0)
			return -1;
		GameMap res = game.map.get(game.gameState.getCurrentArea().toLowerCase())[i];
		Tile res2 = res.map[(Math.round(getY())) * res.length + (int) (Math.round(getX()))];
		if (res2 == null)
			return -1;
		return res2.getId();
	}

	public void move()
	{
		incX(getxVel());
		if (getID(2) != -1 || getID(3) != -1)
		{
			decX(getxVel());
			setxVel(-getxVel());
		}
		incY(getyVel());
		if (getID(2) != -1 || getID(3) != -1)
		{
			decY(getyVel());
			setyVel(-getyVel());
		}

		updateAnimationFrame();
	}

	public String getDirection()
	{
		return direction;
	}

	public void setDirection(String direction)
	{
		this.direction = direction;
	}

	public int getAnimationFrame()
	{
		return animationFrame;
	}

	public void setAnimationFrame(int animationFrame)
	{
		this.animationFrame = animationFrame;
	}

	public float getAnimationDelayFrame()
	{
		return animationDelayFrame;
	}

	public void setAnimationDelayFrame(float animationDelayFrame)
	{
		this.animationDelayFrame = animationDelayFrame;
	}

	public float getAnimationSpeed()
	{
		return animationSpeed;
	}

	public void setAnimationSpeed(float animationSpeed)
	{
		this.animationSpeed = animationSpeed;
	}

	public float getMaxyVel()
	{
		return maxyVel;
	}

	public void setMaxyVel(float maxyVel)
	{
		this.maxyVel = maxyVel;
	}

	public float getMaxxVel()
	{
		return maxxVel;
	}

	public void setMaxxVel(float maxxVel)
	{
		this.maxxVel = maxxVel;
	}

	public String getSpriteType()
	{
		return spriteType + "_" + getDirection();
	}

	public void setSpriteType(String spriteType)
	{
		this.spriteType = spriteType;
	}

	public org.newdawn.slick.opengl.Texture getTexture()
	{
		return texture;
	}

	public void setTexture(org.newdawn.slick.opengl.Texture texture)
	{
		this.texture = texture;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
