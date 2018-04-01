package graphics;

import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by minnow on 12/7/17
 */
public class Texture extends org.newdawn.slick.opengl.TextureLoader implements Serializable
{
	private int offsetX = 0, offsetY = 0;
	public transient org.newdawn.slick.opengl.Texture texture;
	private int width = 64, height = 64;

	public void loadTexture(String filename)
	{
		try
		{
			texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(filename));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("error loading image");
		}
	}

	public Texture clipArea(Rectangle area, org.newdawn.slick.opengl.Texture originalTexture)
	{
		Texture t = new Texture();
		t.texture = originalTexture;
		t.setWidth(area.width);
		t.setHeight(area.height);
		t.setOffsetX(area.x);
		t.setOffsetY(area.y);

		return t;
	}

	public int getHeight()
	{
		return height;
	}

	public boolean isTransparent(int x)
	{
		return x > -1 && this.texture.getTextureData()[x] >> 24 == 0;
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

	public int getOffsetX()
	{
		return offsetX;
	}

	public void setOffsetX(int offsetX)
	{
		this.offsetX = offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}
}
