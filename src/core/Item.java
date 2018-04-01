package core;


import graphics.Texture;

import java.io.Serializable;

public class Item implements Serializable
{
	private String rawName = null;

	private String name = null;
	private String description = null;

	private int hp = 0;
	private int hydration = 0;
	private int hunger = 0;
	private int sleep = 0;
	private int price = 0;

	private Texture image = null;

	public Texture getImage()
	{
		return image;
	}

	public void setImage(Texture image)
	{
		this.image = image;
	}

	public int getSleep()
	{
		return sleep;
	}

	public void setSleep(int sleep)
	{
		this.sleep = sleep;
	}

	public int getHunger()
	{
		return hunger;
	}

	public void setHunger(int hunger)
	{
		this.hunger = hunger;
	}

	public int getHydration()
	{
		return hydration;
	}

	public void setHydration(int hydration)
	{
		this.hydration = hydration;
	}

	public int getHp()
	{
		return hp;
	}

	public void setHp(int hp)
	{
		this.hp = hp;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void use(Entity entity)
	{
		entity.stats.setHp(entity.stats.getHp() + getHp());
		entity.stats.setHunger(entity.stats.getHunger() + getHunger());
		entity.stats.setHydration(entity.stats.getHydration() + getHydration());
		entity.stats.setSleep(entity.stats.getSleep() + getSleep());
	}

	public String getRawName()
	{
		return rawName;
	}

	public void setRawName(String rawName)
	{
		this.rawName = rawName;
	}

	public int getPrice()
	{
		return price;
	}

	public int getSellPrice()
	{
		return (int) Math.floor(getPrice() * 0.75f);
	}

	public void setPrice(int price)
	{
		this.price = price;
	}
}

