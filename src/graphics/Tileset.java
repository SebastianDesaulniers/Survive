package graphics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by minnow on 12/9/17
 */
public class Tileset implements Serializable
{
	private int width = 64, height = 64;
	private int tileWidth = 32, tileHeight = 32;
	private int spacing = 0;

	private Map<String, Texture[]> textures;

	public Tileset(int w, int h)
	{
		setWidth(w);
		setHeight(h);
		textures = new HashMap<>();
	}

	public Tileset(int w, int h, int tileWidth, int tileHeight)
	{
		setWidth(w);
		setHeight(h);

		setTileWidth(tileWidth);
		setTileHeight(tileHeight);

		textures = new HashMap<>();
	}

	public void load(String name)
	{

	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getTileHeight()
	{
		return tileHeight;
	}

	public void setTileHeight(int tileHeight)
	{
		this.tileHeight = tileHeight;
	}

	public int getTileWidth()
	{
		return tileWidth;
	}

	public void setTileWidth(int tileWidth)
	{
		this.tileWidth = tileWidth;
	}

	public int getSpacing()
	{
		return spacing;
	}

	public void setSpacing(int spacing)
	{
		this.spacing = spacing;
	}

	public Texture[] getTexture(String string)
	{
		return textures.get(string);
	}

	public void setTexture(String string,  Texture[] textures)
	{
		this.textures.put(string, textures);
	}
}
