package core;

import file.Config;
import file.Save;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by minnow on 12/7/17
 */
public class Game implements Serializable
{
	public Config config;
	public GameState gameState;

	public ArrayList<Event> events;
	public HashMap<String, GameMap[]> map = new HashMap<>();

	public Save savefile;

	public HashMap<String, Item> items = new HashMap<>();
	public HashMap<String, Entity> enemies = new HashMap<>();
	public HashMap<String, Move> moves = new HashMap<>();
}
