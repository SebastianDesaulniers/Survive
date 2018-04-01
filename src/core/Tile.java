package core;

import java.io.Serializable;

/**
 * Created by minnow on 12/13/17
 */
public class Tile implements Serializable
{
	private int lat = 0, lon = 0;
	private int id = -1;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getLat()
	{
		return lat;
	}

	public void setLat(int lat)
	{
		this.lat = lat;
	}

	public int getLon()
	{
		return lon;
	}

	public void setLon(int lon)
	{
		this.lon = lon;
	}
}
