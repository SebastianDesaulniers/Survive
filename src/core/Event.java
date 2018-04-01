package core;

import graphics.Draw;
import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.input.Keyboard;

import java.io.Serializable;

import static core.Shared.game;
import static core.Shared.player;

public class Event implements Serializable
{
	private Point coords = null;
	private Point newCoords = null;

	public EventType type = EventType.DOOR;
	private String facingDirection = null;
	public AreaType area = AreaType.FARM;
	public Item item = null;

	private String name = null;

	private String areaNeeded = null;
	private String newArea = null;

	public Event(float x, float y, EventType type)
	{
		coords = new Point(x, y);
		this.type = type;
	}

	public boolean usable()
	{
		boolean res = false;

		switch (type)
		{
			case ITEM:
				if (item == null)
					return false;
			case TALK:
				if (Keyboard.isKeyDown(game.config.keys.get("select")) && Math.round(player.getX()) == Math.round(coords.getX()) && Math.round(player.getY()) == Math.round(coords.getY()))
					res = true;
				break;
			case DOOR:
				if (Math.round(player.getX()) == Math.round(coords.getX()) && Math.round(player.getY()) == Math.round(coords.getY()))
					res = true;
				break;
		}
		return res;
	}

	private boolean isVowel(char a)
	{
		return a == 'a' | a == 'e' | a == 'o' | a == 'u' | a == 'i';
	}

	public void trigger(Draw draw)
	{
//		if (!player.getDirection().equals(getFacingDirection()))
//			return;
		if (!game.gameState.getCurrentArea().equals(areaNeeded))
			return;

		switch (type)
		{

			case DOOR:
			{
				player.setPreviousX(player.getX());
				player.setPreviousY(player.getY());
				player.setXY(newCoords.getX(), newCoords.getY());
				System.out.println(newArea);
				game.gameState.setCurrentArea(newArea.toLowerCase());
				System.out.println("OK WE GOOD");
				break;
			}
			case ITEM:
			{
				if (item != null)
				{
					int pivot = (facingDirection.toLowerCase().equals("up") ? -1 : 1);
					game.gameState.playSound("pickup");
					player.items.add(SerializationUtils.clone(item));
					if (isVowel(item.getName().toLowerCase().charAt(0)))
						draw.drawTextbox(String.format("You found an %s!     ", item.getName().toLowerCase()), 0.37f);
					else
						draw.drawTextbox(String.format("You found a %s!      ", item.getName().toLowerCase()), 0.37f);

					item = null;
					game.gameState.setEvent(false);
					game.gameState.ignoredTiles.add(new int[]{(int) coords.getX(), (int) coords.getY() + pivot, 5});
					game.gameState.ignoredTilesString.add(game.gameState.getCurrentArea().toLowerCase());
					game.gameState.updateIgnoreList();
					System.out.println(player.getX());
					System.out.println(player.getY());
					game.gameState.updateStore();
				}
				break;
			}
			case TALK:
			{
				draw.drawTextbox(name, 0.17f);
				System.out.println(name);
				game.gameState.setEvent(false);
				break;
			}
		}
	}

	public String getFacingDirection()
	{
		return facingDirection;
	}

	public void setFacingDirection(String facingDirection)
	{
		this.facingDirection = facingDirection;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAreaNeeded()
	{
		return areaNeeded;
	}

	public void setAreaNeeded(String areaNeeded)
	{
		this.areaNeeded = areaNeeded;
	}

	public void setNewCoords(Point newCoords)
	{
		this.newCoords = newCoords;
	}

	public void setNewArea(String newArea)
	{
		this.newArea = newArea;
	}
}
