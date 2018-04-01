import core.*;
import core.Event;
import file.Config;
import file.GameClock;
import file.Save;
import graphics.Texture;
import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.util.*;

import static core.Shared.game;
import static core.Shared.paint;
import static core.Shared.player;

/**
 * Created by minnow on 12/5/17
 */
public class Main
{

	private boolean doneBit = false; // TODO: migrate to GameState

	/**
	 * Initializes OpenGL primitives and addons.
	 */
	private void initOpenGL()
	{
		System.setProperty("org.lwjgl.librarypath", new File("libs/").getAbsolutePath());
		try
		{
			Display.setDisplayMode(new DisplayMode(1080, 720));
//			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
//			Display.setFullscreen(true);
			Display.create();

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			GL11.glViewport(0, 0, game.config.getWindowWidth(), game.config.getWindowHeight());
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glOrtho(0, game.config.getWindowWidth(), game.config.getWindowHeight(), 0, -1.0, 1.0);

			Display.setTitle("ISU");

		}
		catch (LWJGLException e)
		{
			System.out.println("Couldn't start OpenGL. Exiting program.");
			System.exit(1); // Exit with status code 1 since OpenGL couldn't initialize properly.
		}
	}

	private void setup()
	{
		initGame(); // Calls the initGame method to setup the game states.
		initOpenGL(); // Initializes OpenGL primitives and addons.

		game.config.readConfig();

		player.setSpriteType("player");

		game.gameState.setArea(AreaType.FARM);

		player.moves = SerializationUtils.clone(game.enemies.get("player").moves);
		player.stats = SerializationUtils.clone(game.enemies.get("player").stats);

		game.savefile = new Save();
		game.gameState.playMusic("title_screen");
//		game.config.music.get("village").playAsMusic(1, 1, true);
		game.gameState.setTitleScreen(true);
		game.gameState.updateIgnoreList();
		player.setAnimationSpeed(5);

	}

	/**
	 * Creates new class instances and initializes object states.
	 */
	private void initGame()
	{
		player = new Player(); // Creates a new instance of the player.

		game = new Game(); // Creates a new instance of Game.

		game.config = new Config(); // Creates a new instance of Config.
		game.events = new ArrayList<>(); // Creates an empty ArrayList of events.

		game.config.init(); // Initializes the config for game.
		game.config.readWindowConfig(); // Reads the window config (DONT NEED THIS, REMOVE LATER).

		game.gameState = new GameState(); // Creates a new instance of GameState.

		player.resetVelocities(); // Reset the player's velocities. Make sure they are 0.

		player.setXY(36, 83); // Sets the XY starting coordinates for the player.
		player.setName("???"); // Sets the default player name. Can be anything since it will be changed later on.

		game.gameState.clock = new GameClock(4f); // Creates a new gameclock
		// TODO: document the 24f
	}

	/**
	 * Fades the screen from black to white opacity.
	 * Used for effects like the title screen & loading screen.
	 */
	private void fade()
	{
		GL11.glColor4f(1f, 1f, 1f, game.gameState.fade);
	}

	/**
	 * Clears the screen to black
	 */
	private void clear()
	{
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear the display.
		GL11.glEnable(GL11.GL_BLEND); // Enable blending.
		GL11.glDisable(GL11.GL_DEPTH_TEST); // Disable blending.
		GL11.glClearColor(0.f, 0.f, 0.f, 0.0f); // Clear to a black color.

		/* FADING: */
		fade();
	}

	/**
	 * Updates the screen & syncs to the fps specified in the config file
	 */
	private void update()
	{
		Display.update(); // Updates the display.
		Display.sync(game.config.getFPS()); // Syncs to the desired FPS.
	}

	/**
	 * Main game loop
	 */
	private void loop()
	{
		while (!Display.isCloseRequested() && game.gameState.isRunning())
		{
			clear(); // Clears the display.

			inputLoop(); // Gets input from the player.
			updateLoop(); // Update game states.
			renderLoop();  // Drawing to the screen.
//			debugLoop();

			update();
		}

		Display.destroy(); // Destroys the display.

		if (!game.gameState.isRunning()) // This means that the game restarted.
		{
			init();
			return;
		}

		exit(); // Calls the exit method. This finishes up the game.
	}

	/**
	 * Exit game method.
	 */
	private void exit()
	{
		System.out.println("Thanks for playing!");

		AL.destroy(); // Destroys OpenAL primitives.
		System.exit(0); // Exits the program.
	}

	private void renderLoop()
	{
		if (game.gameState.isTitleScreen())
		{
			renderTitleScreen();
			return;
		}

		if (game.gameState.isBattle())
		{
			renderBattle();
//			paint.finish();
			return;
		}

//		paint.finish();

		int nx = (int) player.getX() - game.gameState.getViewDistance();
		int ny = (int) player.getY() - game.gameState.getViewDistance();

		int maxNx = (int) player.getX() + game.gameState.getViewDistance();
		int maxNy = (int) player.getY() + game.gameState.getViewDistance();

		if (nx < 0)
			nx = 0;
		if (ny < 0)
			ny = 0;

		Texture t = game.config.tilesets.get("rpg").getTexture("rpg")[0];
		String currentArea = game.gameState.getCurrentArea().toLowerCase();
		GameMap[] gameMaps = game.map.get(currentArea);

		for (GameMap gameMap : gameMaps)
		{
			for (int i = nx; i < maxNx && i < gameMap.length - 1; i++)
			{
				for (int j = ny; j < maxNy && j < gameMap.length - 1; j++)
				{
					int id = gameMap.map[j * gameMap.length + i].getId();

					int x = t.texture.getTextureHeight() / 32;

					if (id == -1) // No need to draw an empty square.
						continue;

					float yOff = 6.2f, xOff = 8.5f;
					paint.drawImageRegion(i * 64 - ((player.getX() - xOff) * 64), (j * 64 - (player.getY() - yOff) * 64), (id % x) * 32, (id / x) * 32, 32, 32, t.texture.getImageWidth(), t.texture.getImageHeight(), t);
				}
			}
		}

		paint.scale(1.3f);
		paint.drawImage(game.config.getWindowWidth() / 2 / 1.3f, game.config.getWindowHeight() / 2 / 1.3f, 64, 64, game.config.images.get(player.getSpriteType()).getTexture(player.getDirection())[player.getAnimationFrame()]);
		paint.scaleBack();

		if (game.gameState.isMenu())
			renderMenu();

		if (game.gameState.isStatsMenu())
			renderStatsMenu();

		GameClock clock = game.gameState.clock;

		paint.drawString(930, 0, String.format("Day %d %02d:%02d %s", clock.getDay(), (clock.getHour() >= 13 ? clock.getHour() - 12 : clock.getHour()), clock.getMinute(), clock.getHour() <= 11 ? "AM" : "PM"), game.config.fonts.get("debug"), Color.BLACK);
		paint.drawString(980, 20, String.format("%.1f C", game.gameState.getTemperature()), game.config.fonts.get("debug"), Color.BLACK);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		paint.finish();

		if (game.gameState.isDebug())
			debugLoop();
	}

	private void renderSaveScreen()
	{
		int k = 1;
		for (String save : game.gameState.saveFiles)
		{
			paint.drawString(370, 200 + k++ * 20, save, game.config.fonts.get("debug"), (game.gameState.getFilePivot() == k - 1) ? new Color(0f, 0f, 0f) : (Color.GRAY));
		}
	}

	private void renderNewGame()
	{
		paint.drawString(360, 250, "Enter your name (press enter when done)", game.config.fonts.get("debug"), Color.BLACK);
		paint.drawString(400, 275, game.gameState.name, game.config.fonts.get("debug"), Color.BLACK);
	}

	private void renderHelpScreen()
	{
		paint.drawString(60, 190, "Your objective is to survive", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 210, "Make sure to maintain your hunger, hydration and sleep", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 230, "Don't overeat, overdrink, or oversleep, either", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 250, "Sell items, cook meat, and drink water", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 270, "Don't forget you can save your game", game.config.fonts.get("debug"), Color.WHITE);

		paint.drawString(60, 310, "SPACE = SELECT/MENU", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 330, "Z = CANCEL", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 350, "ARROW KEYS = MOVE", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(60, 370, "You can change controls in options or in the .ini file.", game.config.fonts.get("debug"), Color.WHITE);
	}

	private void renderTitleScreen()
	{
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glScalef(1f, 1.2f, 1f);
		paint.drawImageFull(0, 0, 1080, 720, 1920, 1080, game.config.backgrounds.get("title"));
		GL11.glScalef(1f, 1f / 1.2f, 1f);
		paint.drawString(370, 100, "SURVIVE", game.config.fonts.get("wood"), Color.WHITE);

		if (game.gameState.isCreatingNewGame())
		{
			renderNewGame();
		}
		else if (game.gameState.isSelectingFile())
		{
			renderSaveScreen();
		}
		else if (game.gameState.isHelpScreen())
		{
			renderHelpScreen();
		}
		else
		{
			int k = 1;
			for (String option : GameState.titleScreenOptions)
				paint.drawString(438, 40 * k++ + 200, option, game.config.fonts.get("osd_mono"), (game.gameState.getTitleScreenPivot() == k - 1) ? new Color(1f, 1f, 1f) : (Color.BLACK));
		}

	}

	private void renderBattle()
	{
		Entity opponent = game.gameState.battle.getOpponent();
		GL11.glScalef(2.5f, 2.5f, 1f);
		paint.drawImageFull(0, 0, 1080, 720, 1080, 580, game.config.backgrounds.get("field"));
		GL11.glScalef(1f / 2.5f, 1f / 2.5f, 1f);

		paint.scale(2);
		paint.drawImageRegionRaw(1080 / 4f, 480 / 2f, 192, 0, 48, 48, 2048, 2048, opponent.getTexture());
		paint.scaleBack();

		paint.drawRectangle(5, 195, 220, 190, new Color(1f, 1f, 1f, 1f));
		paint.drawRectangle(10, 200, 210, 180, new Color(0f, 0f, 0f, 1f));

		int k = 1;

		if (game.gameState.isChoosingMove())
		{
			for (Move move : player.moves)
				paint.drawString(20, 25 * k++ + 220, move.getName(), game.config.fonts.get("debug"), (game.gameState.getMovePivot() == k - 1) ? new Color(1f, 1f, 1f) : (Color.GRAY));
		}
		else if (game.gameState.isItemMenu())
		{
			renderItems();
		}
		else
		{
			for (String option : GameState.battleOptions)
				paint.drawString(70, 50 * k++ + 220, option, game.config.fonts.get("debug"), (game.gameState.getBattlePivot() == k - 1) ? new Color(1f, 1f, 1f) : (Color.GRAY));
		}
		renderStatsBattle();
	}


	private void renderStore()
	{
		paint.drawRectangle(225, 25, 570, 170, new Color(1f, 1f, 1f, 1f)); // TODO: use ratio dependent on window dimensions.
		paint.drawRectangle(230, 30, 560, 160, new Color(0f, 0f, 0f, 1f));

		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				paint.drawRectangle(225 + j * 64 - 2, 250 + i * 64 - 2, 64 + 2, 64 + 2, Color.WHITE);

				if (game.gameState.getStorePivot() == i * 12 + j)
					paint.drawRectangle(225 + j * 64, 250 + i * 64, 62, 62, Color.GRAY);
				else
					paint.drawRectangle(225 + j * 64, 250 + i * 64, 62, 62, Color.BLACK);

				if (i * 12 + j < game.gameState.storeItems.size())
				{
					Item t = game.gameState.storeItems.get(i * 12 + j);
					if (t == null || t.getImage().texture == null)
					{
						t.setImage(game.items.get(t.getRawName()).getImage());
					}
					paint.drawImageRegion(225 + j * 64, 250 + i * 64, 0, 0, 32, 32, 32, 32, t.getImage());
				}
			}
		}
		paint.setColor(new Color(1f, 1f, 1f, 0.8f));

		if (game.gameState.getStorePivot() < game.gameState.storeItems.size() && game.gameState.getStorePivot() >= 0)
		{
			Item t = game.gameState.storeItems.get(game.gameState.getStorePivot());
			paint.drawString(235, 35, String.format("%s: %s", t.getName(), t.getDescription()), game.config.fonts.get("debug"));
			paint.drawImageRegion(450, 85, 0, 0, 32, 32, 32, 32, t.getImage());
			paint.drawString(238, 65, String.format("Buy for %d $", t.getPrice()), game.config.fonts.get("debug"));
		}
	}


	private void renderItems()
	{
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		paint.drawRectangle(225, 25, 570, 170, new Color(1f, 1f, 1f, 1f)); // TODO: use ratio dependent on window dimensions.
		paint.drawRectangle(230, 30, 560, 160, new Color(0f, 0f, 0f, 1f));

		for (int i = 0; i < 6; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				paint.drawRectangle(225 + j * 64 - 2, 250 + i * 64 - 2, 64 + 2, 64 + 2, Color.WHITE);

				if (game.gameState.getItemPivot() == i * 12 + j)
					paint.drawRectangle(225 + j * 64, 250 + i * 64, 62, 62, Color.GRAY);
				else
					paint.drawRectangle(225 + j * 64, 250 + i * 64, 62, 62, Color.BLACK);

				if (i * 12 + j < player.items.size())
				{
					Item t = player.items.get(i * 12 + j);
					if (t.getImage().texture == null)
					{
						t.setImage(game.items.get(t.getRawName()).getImage());
					}
					paint.drawImageRegion(225 + j * 64, 250 + i * 64, 0, 0, 32, 32, 32, 32, t.getImage());
				}
			}
		}
		paint.setColor(new Color(1f, 1f, 1f, 0.8f));

		if (game.gameState.getItemPivot() < player.items.size() && game.gameState.getItemPivot() >= 0)
		{
			Item t = player.items.get(game.gameState.getItemPivot());
			paint.drawString(235, 35, String.format("%s: %s", t.getName(), t.getDescription()), game.config.fonts.get("debug"));
			paint.drawImageRegion(450, 85, 0, 0, 32, 32, 32, 32, t.getImage());
			paint.drawString(238, 65, String.format("%+d hp", t.getHp()), game.config.fonts.get("debug"));
			paint.drawString(238, 85, String.format("%+d hunger", t.getHunger()), game.config.fonts.get("debug"));
			paint.drawString(238, 105, String.format("%+d hydration", t.getHydration()), game.config.fonts.get("debug"));
			paint.drawString(238, 125, String.format("%+d$ to sell", t.getSellPrice()), game.config.fonts.get("debug"));

			if (!game.gameState.isItemMenuMore())
				paint.drawString(750, 65, "...", game.config.fonts.get("debug"));
		}
		if (game.gameState.isItemMenuMore())
		{
			paint.drawRectangle(725, 25, 90, 170, new Color(1f, 1f, 1f, 1f));
			paint.drawRectangle(730, 30, 80, 160, new Color(0f, 0f, 0f, 1f));
			int k = 1;
			for (String option : GameState.optionsItems)
				paint.drawString(738, 40 * k++ + 25, option, game.config.fonts.get("dialog"), (game.gameState.getItemOptionsPivot() == k - 1) ? new Color(1f, 1f, 1f) : (Color.GRAY));
		}
		if (game.gameState.isInfoItemMenu())
		{
			if (game.gameState.getItemPivot() < player.items.size() - 1 && game.gameState.getItemPivot() >= 0)
			{
				String rawName = player.items.get(game.gameState.getItemPivot()).getRawName();
				Integer[] values = player.itemsUsed.get(rawName);
				if (values == null)
					values = new Integer[]{0, 0};
				paint.drawString(520, 85, String.format("Amount used: %d", values[0]), game.config.fonts.get("debug"));
				paint.drawString(520, 105, String.format("Amount trashed: %d", values[1]), game.config.fonts.get("debug"));
			}
		}

	}

	private void renderStatsBattle()
	{
		paint.drawRectangle(25 - 20, 90, 220, 140, new Color(1f, 1f, 1f, 1f));
		paint.drawRectangle(30 - 20, 95, 210, 130, new Color(0f, 0f, 0f, 1f));

		paint.drawString(20, 100, player.getName(), game.config.fonts.get("normal_text"), Color.WHITE);
		paint.drawString(153 - 20, 100, String.format("%d/%d", player.stats.getHp(), player.stats.getTotalHp()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 125, ((float) player.stats.getHp() / player.stats.getTotalHp()) * 180f, 20, new Color(0xff5050));

		paint.drawString(20, 150, String.format("%s", game.gameState.battle.getOpponent().getName()), game.config.fonts.get("normal_text"), Color.WHITE);
		Stats e = game.gameState.battle.getOpponent().stats;
		paint.drawString(153 - 20, 150, String.format("%d/%d", e.getHp(), e.getTotalHp()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 175, ((float) e.getHp() / e.getTotalHp()) * 180f, 20, new Color(0xffcc66));

	}

	private void renderStats()
	{
		paint.drawRectangle(25 - 20, 290, 220, 270, new Color(1f, 1f, 1f, 1f));
		paint.drawRectangle(30 - 20, 295, 210, 260, new Color(0f, 0f, 0f, 1f));

		paint.drawString(20, 300, "HP", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(153 - 20, 300, String.format("%d/%d", player.stats.getHp(), player.stats.getTotalHp()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 325, ((float) player.stats.getHp() / player.stats.getTotalHp()) * 180f, 20, new Color(0xff5050));

		paint.drawString(20, 350, "Hunger", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(153 - 20, 350, String.format("%d/%d", player.stats.getHunger(), player.stats.getTotalHunger()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 375, ((float) player.stats.getHunger() / player.stats.getTotalHunger()) * 180f, 20, new Color(0xffcc66));

		paint.drawString(20, 400, "Hydration", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(153 - 20, 400, String.format("%d/%d", player.stats.getHydration(), player.stats.getTotalHydration()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 375 + 50, ((float) player.stats.getHydration() / player.stats.getTotalHydration()) * 180f, 20, new Color(0x00ccff));

		paint.drawString(20, 450, "Sleep", game.config.fonts.get("debug"), Color.WHITE);
		paint.drawString(153 - 20, 450, String.format("%d/%d", player.stats.getSleep(), player.stats.getTotalSleep()), game.config.fonts.get("debug"), Color.WHITE);
		paint.drawRectangle(20, 475, ((float) player.stats.getSleep() / player.stats.getTotalSleep()) * 180f, 20, new Color(0xdddddd));

	}

	private void renderStatsMenu()
	{
		paint.drawRectangle(225, 25, 570, 170, new Color(1f, 1f, 1f, 1f));
		paint.drawRectangle(230, 30, 560, 160, new Color(0f, 0f, 0f, 1f));

		Entity t = player;

		paint.drawString(235, 35, String.format("Name: %-18s  Days alive: %d", t.getName(), game.gameState.clock.getDay()), game.config.fonts.get("debug"));
		paint.drawString(238, 65, String.format("Level: %-18d Next level: %d xp", t.stats.getLevel(), Math.round(t.stats.getNextExp() - t.stats.getExp())), game.config.fonts.get("debug"));
		paint.drawString(238, 85, String.format("Attack: %d", t.stats.getAttack()), game.config.fonts.get("debug"));
		paint.drawString(238, 105, String.format("Defense: %d", t.stats.getDefense()), game.config.fonts.get("debug"));
		paint.drawString(238, 135, String.format("Money: %d", t.stats.getMoney()), game.config.fonts.get("debug"));


	}

	/**
	 * Renders the game menu seen when the user presses "select".
	 */
	private void renderMenu()
	{
		player.resetVelocities();
		player.setAnimationFrame(0);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		paint.drawRectangle(25 - 20, 25, 120, 260, new Color(1f, 1f, 1f, 1f));
		paint.drawRectangle(30 - 20, 30, 110, 250, new Color(0f, 0f, 0f, 1f));

		int k = 1;
		for (String option : GameState.options)
			paint.drawString(28, 40 * k++ + 10, option, game.config.fonts.get("dialog"), (game.gameState.menuPivot == k - 1) ? new Color(1f, 1f, 1f) : (Color.GRAY));

		if (game.gameState.isItemMenu())
			renderItems();
		if (game.gameState.isStore())
			renderStore();

		renderStats();

		paint.setColor(Color.WHITE);
	}

	private void debugLoop()
	{
		paint.drawString(5, 0, "Debug Info:", game.config.fonts.get("debug"), new Color(0x000000));
		paint.drawString(15, 20, String.format("x: %.3f, y: %.3f", player.getX(), player.getY()), game.config.fonts.get("debug"), new Color(0x000000));
		paint.drawString(15, 40, String.format("xVel: %.3f, yVel: %.3f", player.getxVel(), player.getyVel()), game.config.fonts.get("debug"), new Color(0x000000));
		for (int j = 0; j < 5; j++)
			paint.drawString(15, 60 + (j + 1) * 30, String.format("ID[%d]: %d", j, player.getID(j)), game.config.fonts.get("debug"), new Color(0x000000));
	}

	private void updateLoop()
	{
		if (!game.gameState.isBattle() && !game.gameState.isTitleScreen())
			player.move();
		if (player.isMoving() && game.gameState.isWaitingPrompt())
		{
			game.gameState.setWaitingPrompt(false);
			game.gameState.setDoneWaitingPrompt(true);
		}

		if (!player.stats.checkAlive() && paint.isDoneTextbox())
		{
			if (!doneBit)
			{
				paint.drawTextbox(String.format("%s died...                                ", player.getName()), 0.2f);
				doneBit = true;
			}
			else if (paint.isDoneTextbox())
			{
				game.gameState.setRunning(false);
				doneBit = false;
			}
		}


		if (!game.gameState.isTitleScreen())
			game.gameState.clock.update();

		if (game.gameState.clock.getTime() % (3600 * 8) == 0 && game.gameState.clock.prev != game.gameState.clock.getTime())
		{
			player.stats.incHunger(2);
			player.stats.setSleep(player.stats.getSleep() + 2);
			player.stats.incHydration(4);
		}

		if (game.gameState.fade < 1)
			game.gameState.fade += 0.01f;

		if (game.gameState.isBattleMessage())
		{
			paint.drawTextbox(game.gameState.getBattleMessageString(), 0.48f);
//			game.gameState.setBattleMessage(false);
		}

		if (paint.isDoneTextbox() && game.gameState.isBattle() && !game.gameState.isDoneDrawing())
		{
			paint.resetDrawing();
			game.gameState.setBattleMessage(false);
			game.gameState.setEvent(false);
			game.gameState.setDoneDrawing(true);
			if (!game.gameState.isOpponentTurn() && !game.gameState.isAfterMove())
			{
				game.gameState.setOpponentTurn(true);
			}
		}
		else if (game.gameState.isBattle() && paint.isDoneTextbox() && game.gameState.battle != null)
			game.gameState.battle.advance();

		if (game.gameState.isDoneWaitingPrompt())
		{
			paint.resetDrawing();
			game.gameState.setDoneWaitingPrompt(false);
		}

		if (game.gameState.isLevelUp())
		{
			if (!doneBit)
			{
				paint.drawTextbox(String.format("%s leveled up to level %d!     ", player.getName(), player.stats.getLevel()), 0.31f);
				if (game.gameState.currentMusic != null)
					game.gameState.currentMusic.stop();
				game.gameState.playSound("level_up");
				doneBit = true;
			}
			else if (paint.isDoneTextbox() && !game.config.sounds.get("level_up").isPlaying())
			{
				game.gameState.setLevelUp(false);
				if (game.gameState.currentMusic != null)
					game.gameState.currentMusic.playAsMusic(1f, 1f, true);
				doneBit = false;
			}
		}

		if (game.gameState.isEndingBattle() && game.gameState.isBattle())
		{
			if (paint.isDoneTextbox())
			{
				Entity opponent = game.gameState.battle.getOpponent();
				int xp = GameState.random.nextInt(opponent.stats.getTotalHp() + opponent.stats.getAttack()) + 10;
				int money = Math.round(10 * opponent.stats.getAttack() * 0.5f + 10);
				player.stats.gainExp(xp);

				player.stats.incHunger(1);
				player.stats.incHydration(1);
				player.stats.incSleep(2);
				player.stats.incMoney(money);
				Item item = opponent.items.get(GameState.random.nextInt(opponent.items.size()));
				game.gameState.setEndingBattle(false);

				game.gameState.setBattle(false);
				game.gameState.setOpponentTurn(false);

				player.items.add(item);
				if (game.gameState.isLevelUp())
					return;

				String string = String.format("You won! Gained %d xp, %d$, and a %s.               ", xp, money, item.getName().toLowerCase());
				paint.drawTextbox(string, 0.35f);
				game.gameState.setBattleMessageString(string);
				game.gameState.battle.reset();
//				paint.resetDrawing();
			}
		}


		game.gameState.setEvent(false);
		for (Event e : game.events)
		{
			if (e.usable())
			{
				game.gameState.setEvent(true);
				e.trigger(paint);
			}
		}

		if ((player.getID(1) == 1235 || player.getID(1) == 1363) && player.isMoving() && !game.gameState.isBattle() && paint.isDoneTextbox()) // wild grass
		{
			if (GameState.random.nextInt(game.config.getEncounterChance()) == 1)
			{
				player.resetVelocities();
				game.gameState.createBattle(game.gameState.generateRandomEnemy(player.getID(1), player.stats.getLevel()));
				game.gameState.setBattle(true);
				game.gameState.setDoneDrawing(true);
//				paint.setDoneTextbox(false);
			}
		}
		game.gameState.setTemperature((18 - (game.gameState.clock.getHour() - 12)) - 10);
	}

	/**
	 * The input loop.
	 */
	private void inputLoop()
	{
		if (!game.gameState.isTitleScreen() && !game.gameState.isMenu() && !game.gameState.isBattle()) // Make sure the player can't move during: the title screen, menu, store, and battle.
			player.input(); // Get input from the player for movement.

		while (Keyboard.next()) // This while loop is used to look through each key pressed on the event queue.
			game.gameState.input(); // Handle each key in the input loop.
	}

	/**
	 * The initialization method. Calls other methods to simplify code readability.
	 */
	private void init()
	{
		setup(); // Sets up the game. This should only be called once.
		loop(); // Calls the main game loop method. This is where the game starts.
	}

	/**
	 * The main method.
	 *
	 * @param args Can be anything. Irrelevant here since they aren't used.
	 */
	public static void main(String[] args)
	{
		Main main = new Main(); // Creates a new instance of Main.

		main.init(); // Starts the game by calling the init method.
	}
}
