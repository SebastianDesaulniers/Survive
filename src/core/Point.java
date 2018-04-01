package core;

import java.io.Serializable;

/**
 * Created by minnow on 12/7/17
 */
public class Point implements Serializable
{
	private float x, y;
	private int nX, nY;

	@SuppressWarnings("WeakerAccess")
	public Point(float x, float y)
	{
		setX(x);
		setY(y);
	}

	public float getY()
	{
		return y;
	}

	private void adjustNearestXY()
	{
		setnX((int) getX());
		setnY((int) getY());
	}

	public void setXY(float x, float y)
	{
		setX(x);
		setY(y);
	}

	@SuppressWarnings("WeakerAccess")
	public void incX(float increment)
	{
		setX(getX() + increment);
	}

	@SuppressWarnings("WeakerAccess")
	public void decX(float increment)
	{
		setX(getX() - increment);
	}

	@SuppressWarnings("WeakerAccess")
	public void incY(float increment)
	{
		setY(getY() + increment);
	}

	@SuppressWarnings("WeakerAccess")
	public void decY(float increment)
	{
		setY(getY() - increment);
	}

	public void setY(float y)
	{
		this.y = y;
		adjustNearestXY();
	}

	public int getnY()
	{
		return nY;
	}

	public void setnY(int nY)
	{
		this.nY = nY;
	}

	public int getnX()
	{
		return nX;
	}

	@SuppressWarnings("WeakerAccess")
	public void setnX(int nX)
	{
		this.nX = nX;
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
		adjustNearestXY();
	}
}
