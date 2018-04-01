package file;

import core.Event;
import core.GameState;
import core.Player;

import java.io.*;
import java.util.ArrayList;

import static core.Shared.game;
import static core.Shared.player;

/**
 * Class for the save file
 */
public class Save
{

	public static ArrayList<Byte> data = new ArrayList<>();

	/**
	 * The filename for the save
	 */

	private void write(String filename, Object obj)
	{
		ObjectOutputStream file = null;
		try
		{
			file = new ObjectOutputStream(new FileOutputStream(filename));
			file.writeObject(obj);
			file.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private Object read(String filename)
	{
		try
		{
			ObjectInputStream file = new ObjectInputStream(new FileInputStream(filename));
			try
			{
				return file.readObject();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void readSave(String name)
	{
		game.gameState = (GameState) read(String.format("saves/%s/game.obj", name));
		player = (Player) read(String.format("saves/%s/player.obj", name));
		game.events = (ArrayList<Event>) read(String.format("saves/%s/events.obj", name));

		if (game.gameState == null || player == null || game.events == null)
			throw new RuntimeException("Error reading savefile.");
		game.gameState.updateIgnoreList();
	}

	public void writeSave(String name)
	{
		File folder = new File("saves/" + name);
		boolean res = folder.mkdir();

		if (!res)
			System.out.println("Folder exists. Shouldn't matter.");

		write(String.format("saves/%s/player.obj", name), player);
		write(String.format("saves/%s/game.obj", name), game.gameState);
		write(String.format("saves/%s/events.obj", name), game.events);
	}

}

