package core;

import org.lwjgl.input.Keyboard;

import java.io.Serializable;

import static core.Shared.game;

/**
 * @author Sebastian Desaulniers
 * The player class
 */
public class Player extends Entity implements Serializable
{
	private float previousX = 0;
	private float previousY = 0;

	/**
	 * Constructor for Player.
	 */
	public Player()
	{
		super(0, 0); // Calls the superclass constructor. This results in the player's x and y being initialized to 0.
	}

	/**
	 * Resets both the x and y velocity to 0.
	 */
	public void resetVelocities()
	{
		setxVel(0);
		setyVel(0);
	}

	/**
	 * Player moving method.
	 */
	@Override
	public void move()
	{
		incX(getxVel());

		if (getID(2) != -1 || getID(3) != -1 || getID(5) != -1) // Checks if there is a collision on layers 3 or 4 after increasing x.
		{
			// COLLISION OCCURRED:
			decX(getxVel()); // Reset the player's x-coordinate to what it was previously.
			setAnimationFrame(0); // Resets the animation frame. This prevents the player from having a moving animation when there is a collision..
		}

		incY(getyVel());

		if (getID(2) != -1 || getID(3) != -1 || getID(5) != -1) // Checks if there is a collision on layers 3 or 4 after increasing y.
		{
			// COLLISION OCCURRED
			decY(getyVel()); // Reset the player's y-coordinate to what it was previously.
			setAnimationFrame(0); // Resets the animation frame. This prevents the player from having a moving animation when there is a collision.
		}
	}

	public void input()
	{
		if (!isMoving()) // If the player isn't moving, don't show a walking animation.
			setAnimationFrame(0);

		// Reset the velocities whenever we move.
		resetVelocities();

		final float speed = .1f; // The player movement speed.

		if (Keyboard.isKeyDown(game.config.keys.get("up")))
		{
			setDirection("up");
			setyVel(-speed);
			updateAnimationFrame();
		}
		else if (Keyboard.isKeyDown(game.config.keys.get("down")))
		{
			setDirection("down");
			setyVel(speed);
			updateAnimationFrame();
		}
		else if (Keyboard.isKeyDown(game.config.keys.get("right")))
		{
			setDirection("right");
			setxVel(speed);
			updateAnimationFrame();
		}
		else if (Keyboard.isKeyDown(game.config.keys.get("left")))
		{
			setDirection("left");
			setxVel(-speed);
			updateAnimationFrame();
		}

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

	public boolean isMoving()
	{
		return getxVel() != 0 | getyVel() != 0;
	}

	public float getPreviousY()
	{
		return previousY;
	}

	public void setPreviousY(float previousY)
	{
		this.previousY = previousY;
	}

	public float getPreviousX()
	{
		return previousX;
	}

	public void setPreviousX(float previousX)
	{
		this.previousX = previousX;
	}
}
