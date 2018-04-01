package file;

import core.*;
import core.Event;
import core.Point;
import graphics.RenderableText;
import graphics.Texture;
import graphics.Tileset;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

import static core.Shared.game;
import static core.Shared.player;

/**
 * Used for loading config data from {@value filename}
 *
 * @author Sebastian Desaulniers
 */
public class Config implements Serializable
{
	/**
	 * Filename for the game config file.
	 * <p>
	 * <b> Note: </b> must be in the same folder as the executable.
	 * </p>
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private static final String filename = "config.ini";

	private int encounterChance = 0;

	/**
	 * Stores the re-mappable keyboard input in a HashMap.
	 *
	 * @see HashMap
	 */
	public Map<String, Integer> keys = new HashMap<>();

	public HashMap<String, Texture> backgrounds = new HashMap<>();

	/**
	 * Stores the music in a HashMap.
	 */
	@SuppressWarnings("WeakerAccess")
	public Map<String, Audio> music = new HashMap<>();

	public Map<String, Audio> sounds = new HashMap<>();
	/**
	 * Stores the fonts for text rendering in a HashMap.
	 *
	 * @see HashMap
	 */
	@SuppressWarnings("WeakerAccess")
	public Map<String, RenderableText> fonts = new HashMap<>();


	public Map<String, Tileset> images = new HashMap<>();

	public Map<String, Tileset> tilesets = new HashMap<>();
	/**
	 * Ini file reader.
	 *
	 * @see Ini
	 */
	private Ini ini = null;

	private int windowWidth = 640, windowHeight = 480;
	private int FPS = 60;

	/**
	 * Initializes the loader
	 */
	public void init()
	{
		try
		{
			ini = new Ini(new File(filename));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("Error loading config file! Game cannot start.");
			System.exit(1);
		}
	}

	/**
	 * Reads data from the config file
	 */
	public void readConfig()
	{
		readInputConfig();
		readMiscConfig();
		readMapConfig();
		readFontConfig();
		readImageConfig();

		readMusicConfig();
		readAudioConfig();


		readItemConfig();
		readMoveConfig();
		readEnemyConfig();

		readEventConfig();
	}

	private void readEnemyConfig()
	{
		String[] events = ini.get("enemies").toString().split(",");

		for (Object input : events)
			parseEnemy(input.toString());

	}

	private void parseEnemy(String input)
	{
		if (!input.matches(".*[=,].*"))
			throw new RuntimeException(String.format("Invalid input: %s\n", input));

		String statParam = input.substring(input.indexOf('[') + 2, input.indexOf(']'));

		String temp = input.substring(input.indexOf(']') + 1);

		String movesParam = temp.substring(temp.indexOf('[') + 1, temp.indexOf(']'));

		String pathParam = input.substring(input.lastIndexOf('[') + 1, input.substring(0, input.length() - 1).lastIndexOf(']'));
		if (pathParam.contains("]"))
			pathParam = pathParam.substring(0, pathParam.indexOf(']'));
		System.out.println(pathParam);

		String drops = input.substring(input.indexOf('(') + 1, input.lastIndexOf(')'));
		System.out.println("DROPS: " + drops);
		String[] dropsArray = drops.split(" ");


		int ind = input.indexOf('=');

		String name = input.substring(1, ind);

		String[] vals = statParam.split(" ");

		Entity entity = new Entity(0, 0);
		entity.stats.setHp(Integer.parseInt(vals[0]));
		entity.stats.setTotalHp(Integer.parseInt(vals[0]));
		entity.stats.setAttack(Integer.parseInt(vals[1]));
		entity.stats.setDefense(Integer.parseInt(vals[2]));
		entity.stats.setSpeed(Integer.parseInt(vals[3]));

		for (String string : dropsArray)
			entity.items.add(game.items.get(string));

		vals = movesParam.split(" ");

		for (int i = 0; i < 5; i++) // must have 5 moves
			entity.moves[i] = game.moves.get(vals[i]);

		String[] values = statParam.split(" ");

		try
		{
			entity.setTexture(Texture.getTexture("PNG", ResourceLoader.getResourceAsStream(pathParam)));
		}
		catch (IOException e)
		{
			System.out.printf("Couldn't load icon image: %s\n", pathParam);
			System.exit(0);
		}

		entity.setName(name);

		game.enemies.put(name, entity);

	}

	private void readMoveConfig()
	{
		String[] events = ini.get("moves").toString().split(",");

		for (Object input : events)
			parseMove(input.toString());

	}

	private void parseMove(@NotNull String input)
	{
		MoveType type = input.contains("ATK") ? MoveType.ATTACK : MoveType.STATUS;
		Move move = null;

		String[] stats = input.substring(input.indexOf(" [") + 2, input.lastIndexOf("]") - 1).split(" ");
		move = new Move(type, Integer.parseInt(stats[0]), Float.parseFloat(stats[1]), Integer.parseInt(stats[2]), Float.parseFloat(stats[3]));

		// TODO: fix the constructor to take status

		int ind = input.indexOf('=');
		String name = input.substring(1, ind);

		move.setName(name);

		game.moves.put(name, move);
	}

	private void readItemConfig()
	{
		String[] events = ini.get("items").toString().split(",");

		for (Object input : events)
			parseItem(input.toString());
	}

	private void parseItem(String input)
	{
		if (!input.matches(".*[=,].*"))
			throw new RuntimeException(String.format("Invalid input: %s\n", input));

		String descParam = input.substring(input.indexOf('[') + 2, input.indexOf(']'));

		String temp = input.substring(input.indexOf(']') + 1);

		String statParam = temp.substring(temp.indexOf('[') + 1, temp.indexOf(']'));

		String pathParam = input.substring(input.lastIndexOf('[') + 1, input.lastIndexOf(']') - 1);

		String name = descParam.split(":")[0];
		String description = descParam.split(":")[1];

		Item item = new Item();
		item.setName(name);
		item.setDescription(description);
		String[] values = statParam.split(" ");

		item.setHp(Integer.parseInt(values[0]));
		item.setHunger(Integer.parseInt(values[1]));
		item.setHydration(Integer.parseInt(values[2]));
		item.setSleep(Integer.parseInt(values[3]));
		item.setPrice(Integer.parseInt(values[4]));

		Texture t = new Texture();
		System.out.println(pathParam);
		t.loadTexture(pathParam);
		item.setImage(t);

		String value = input.substring(0, input.indexOf('=')).trim().replace("{", "");
		System.out.println(value);
		item.setRawName(value);

		game.items.put(value, item);
	}

	private void parseEvent(String input)
	{
		if (!input.matches(".*[=,].*"))
			throw new RuntimeException(String.format("Invalid input: %s\n", input));

		String params = input.substring(input.indexOf('[') + 2, input.indexOf(']'));

		String[] values = params.split(" ");

		float nx = 0, ny = 0;
		float x = Float.parseFloat(values[0]);
		float y = Float.parseFloat(values[1]);
		String facingDirection = values[2].toLowerCase();
		String areaNeeded = values[3].toLowerCase();
		if (values.length > 4)
		{
			nx = Float.parseFloat(values[4]);
			ny = Float.parseFloat(values[5]);
		}
		EventType eventType = EventType.DOOR;
		AreaType areaType = null;

		String type = input.substring(input.lastIndexOf('=') + 2, input.indexOf(':')).toUpperCase().trim();

		if (!type.equals("item"))
		{
			try
			{
				eventType = (EventType) EventType.class.getField(type).get(EventType.class);
			}
			catch (IllegalAccessException | NoSuchFieldException e)
			{
				System.out.printf("Invalid EventType: %s\n", type);
				System.exit(0);
			}
		}
		String name = null;
		Event event = new Event(x, y, eventType);
		if (input.contains("TALK"))
		{
			String area = input.substring(input.indexOf(":") + 1).trim();
			name = area.substring(0, area.length() - 2);
		}

		else if (input.contains(":") && !input.contains("ITEM") && !input.contains("TALK"))
		{
			String area = input.substring(input.indexOf(":") + 1).trim();
			if (area.contains("}"))
				area = area.substring(0, area.length() - 2);
			else
				area = area.substring(0, area.length() - 1);
			System.out.println(area);
			event.setNewArea(area);
		}
		String area = input.substring(input.indexOf(":") + 1).trim();
		if (area.contains("}"))
			area = area.substring(0, area.length() - 2).trim();
		else
			area = area.substring(0, area.length() - 1).trim();

		if (name != null)
			event.setName(name.replace("*", ","));

		System.out.println("TYPE: " + event.type.toString());
		System.out.println("AREA: " + area);

		event.item = game.items.get(area);
		event.setFacingDirection(facingDirection);
		event.area = areaType;
		event.setAreaNeeded(areaNeeded);
		if (nx != 0 && ny != 0)
			event.setNewCoords(new Point(nx, ny));

		game.events.add(event);
	}

	private void readEventConfig()
	{
		String[] events = ini.get("events").toString().split(",");

		for (Object input : events)
		{
			parseEvent(input.toString());
		}
	}

	@SuppressWarnings("Duplicates")
	private void readAudioConfig()
	{
		String[] values = ini.get("sound_effects").toString().split(",");

		for (String value : values)
		{
			Audio a = null;

			int ind = value.indexOf('=');

			String sound = value.substring(1, ind);
			String file = value.substring(ind + 2, value.lastIndexOf(']'));
			try
			{
				a = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(file));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				sounds.put(sound, a);
			}
		}

	}

	@SuppressWarnings("Duplicates")
	private void readMusicConfig()
	{
		String[] values = ini.get("music").toString().split(",");

		for (String value : values)
		{
			Audio a = null;
			int ind = value.indexOf('=');

			String sound = value.substring(1, ind);
			String file = value.substring(ind + 2, value.lastIndexOf(']'));
			try
			{
				a = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(file));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				music.put(sound, a);
			}
		}

	}

	private void readMiscConfig()
	{
		setFPS(Integer.parseInt(ini.get("misc", "fps")));
		game.gameState.setDebug(Boolean.parseBoolean(ini.get("misc", "debug")));
		game.gameState.setCurrentArea(ini.get("misc", "starting_area"));

		this.encounterChance = Integer.parseInt(ini.get("misc", "encounter_chance"));
	}

	private void readMapConfig()
	{
		String[] maps = ini.get("maps").toString().split(",");
		for (String res : maps)
		{
			int ind = res.indexOf('=');

			String area = res.substring(1, ind);
			int piv = res.contains("}") ? 2 : 1;
			String file = res.substring(ind + 2, res.length() - piv);


			System.out.printf("%s %s\n", area, file);

			game.gameState.maps.put(area, file);
			GameMap[] gameMap = new GameMap[GameState.LAYER_AMOUNT];
			for (int i = 1; i <= GameState.LAYER_AMOUNT; i++)
			{
				gameMap[i - 1] = new GameMap();
				gameMap[i - 1].load(String.format("%s_%d.csv", file, i));
			}
			game.map.put(area, gameMap);
		}
	}

	private void readImageConfig()
	{
		readTilesetConfig();
		readSpriteConfig();
		readBackgroundConfig();
	}

	private void readBackgroundConfig()
	{
		String[] values = ini.get("backgrounds").toString().split(",");

		for (String value : values)
		{
			Texture a = null;
			int ind = value.indexOf('=');

			String name = value.substring(1, ind);
			String file = value.substring(ind + 2, value.lastIndexOf(']'));

			a = new Texture();
			a.loadTexture(file);
			backgrounds.put(name, a);
		}

	}

	private void readSpriteConfig()
	{
		player.image = new Tileset(178, 1, 64, 64);
		String[] maps = ini.get("sprites").toString().split(",");
		final String[] positions = {"up", "left", "down", "right"};
		for (String res : maps)
		{
			int ind = res.indexOf('=');

			String value = res.substring(1, ind);
			String file = res.substring(ind + 2, res.length() - 2);

			Texture texture = new Texture();
			ArrayList<Tileset> tilesets = new ArrayList<>();
			texture.loadTexture(ini.get("sprites", value));
			for (int i = 0; i < 4; i++)
			{
				Texture[] t = new Texture[9];
				for (int j = 0; j < 9; j++)
					t[j] = texture.clipArea(new Rectangle(j * 64, i * 64 + 512, 64, 64), texture.texture);

				Tileset tileset = new Tileset(64, 64);
				tileset.setTexture(positions[i], t);
				tilesets.add(tileset);
			}
			int k = 0;
			for (Tileset t : tilesets)
			{
				images.put(value + "_" + positions[k], t);
				k++;
			}
		}
	}

	/**
	 * Reads tileset config from the config file.
	 */
	private void readTilesetConfig()
	{
		tilesets = new HashMap<>();
		final String[] values = {"rpg"};

		int k = 0;
		ArrayList<Tileset> tilesets = new ArrayList<>();
		for (String value : values)
		{
			Texture texture = new Texture();
			texture.loadTexture(ini.get("tilesets", value));
			Texture[] t = new Texture[64 * 64];
			for (int j = 0; j < 64 * 64; j++)
				t[j] = texture;

			Tileset tileset = new Tileset(64, 64);
			tileset.setTexture(value, t);
			tilesets.add(tileset);
			this.tilesets.put(value, tilesets.get(k));
			k++;
		}
	}


	/**
	 * Reads font config from the config file.
	 */

	private void readFontConfig()
	{
		final String[] values = {"title_screen", "dialog", "normal_text", "debug", "osd_mono", "wood"};
		for (String value : values)
		{
			int size = Integer.parseInt(ini.get("fonts", String.format("%s_size", value)));
			Font tempFont = null;
			try
			{
				tempFont = Font.createFont(Font.TRUETYPE_FONT, new File(ini.get("fonts", value)));
			}
			catch (FontFormatException | IOException e)
			{
				e.printStackTrace();
			}
			assert tempFont != null;
			tempFont = tempFont.deriveFont((float) size);
			fonts.put(value, new RenderableText(tempFont, true));
		}
	}

	/**
	 * Reads input data from the config file.
	 */
	private void readInputConfig()
	{
		final String[] values = {"up", "down", "left", "right", "menu", "menu_up", "menu_down", "menu_left", "menu_right", "select", "more", "cancel"};
		try
		{
			for (String value : values)
			{
				Field field = Keyboard.class.getField(ini.get("input", value));
				keys.put(value, (Integer) field.get(Keyboard.class));
			}
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * Reads window data from the config file.
	 */
	public void readWindowConfig()
	{
		setWindowWidth(1080);
		setWindowHeight(720);
		System.out.printf("Window width: %d, window height: %d\n", getWindowWidth(), getWindowHeight());
	}

	public int getWindowWidth()
	{
		return windowWidth;
	}

	private void setWindowWidth(int windowWidth)
	{
		this.windowWidth = windowWidth;
	}

	public int getWindowHeight()
	{
		return windowHeight;
	}

	private void setWindowHeight(int windowHeight)
	{
		this.windowHeight = windowHeight;
	}

	public int getFPS()
	{
		return FPS;
	}

	private void setFPS(int fps)
	{
		FPS = fps;
	}

	public int getEncounterChance()
	{
		return encounterChance;
	}

}

