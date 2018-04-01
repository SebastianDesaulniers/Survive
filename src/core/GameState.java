package core;

import file.GameClock;
import org.apache.commons.lang3.SerializationUtils;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.Audio;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.security.Key;
import java.util.*;

import static core.Shared.game;
import static core.Shared.paint;
import static core.Shared.player;

/**
 * Created by minnow on 12/8/17
 */
public class GameState implements Serializable
{
	public static Random random = new Random();

	public static final String[] options = {"ITEMS", "STATS", "STORE", "SAVE", "OPTIONS", "HELP"};
	public static final String[] optionsItems = {"USE", "TRASH", "SELL"};
	public static final String[] battleOptions = {"ATTACK", "ITEM"};
	public static final String[] titleScreenOptions = {"CONTINUE", "LOAD", "NEW GAME", "OPTIONS", "HELP"};

//	public Stack<Event> events = null;

	public Map<String, Integer> customKeys = null;

	public ArrayList<Item> storeItems = new ArrayList<>();

	public List<String> saveFiles = new ArrayList<>();
	private int viewDistance = 24;

	public ArrayList<int[]> ignoredTiles = new ArrayList<>();
	public ArrayList<String> ignoredTilesString = new ArrayList<>();

	public String name = "";

	public transient Audio currentMusic = null;
	private transient Audio currentSound = null;

	private boolean hasGainedHunger = false;

	private boolean isHelpScreen = false;

	public GameClock clock = null;

	private float temperature = 32;
	private boolean isRunning = true;

	public static final int LAYER_AMOUNT = 6;
	public Battle battle = null;

	public HashMap<String, String> maps = new HashMap<>();

	private boolean isSelectingFile = false;

	private boolean creatingNewGame = false;
	private boolean creatingNewGameInput = false;

	private boolean isDoneWaitingPrompt = false;

	private boolean isStatsMenu = false;

	private boolean isWaitingPrompt = false;

	/**
	 * If the game is drawing a textbox.
	 */
	private boolean isDrawingTextbox = false;

	/**
	 * If the user is playing the game.
	 */
	private boolean isPlayingGame = true;

	/**
	 * Loading screen transition.
	 */
	private boolean isLoadingScreen = false;
	private boolean isLoadingScreenStart = false;
	private boolean isLoadingScreenEnd = false;

	/**
	 * The logo on boot, before the title screen.
	 */
	private boolean isLogo = false;

	/**
	 * Debug mode.
	 */
	private boolean isDebug = false;

	/**
	 * The in-game menu.
	 */
	private boolean isMenu = false;

	/**
	 * If the player is in a battle.
	 */
	private boolean isBattle = false;

	/**
	 * The title screen.
	 */
	private boolean isTitleScreen = false;

	/**
	 * The in-game item menu.
	 */
	private boolean isItemMenu = false;

	/**
	 * The "..." to the item menu.
	 */
	private boolean isItemMenuMore = false;

	/**
	 * The "info" in the item menu.
	 */
	private boolean isInfoItemMenu = false;

	private boolean isEvent = false;
	private boolean battleMessage = false;


	/**
	 * If there's a message to read.
	 */
	private boolean isMessage = false;

	/**
	 * If the player is choosing a move.
	 */
	private boolean isChoosingMove = false;

	/**
	 * If it's the end of the battle (experience & items).
	 */
	private boolean isEndingBattle = false;

	private boolean isOpponentTurn = false;

	private boolean isLevelUp = false;

	private String battleMessageString = null;
	private String currentArea = null;

	private AreaType previousArea = null;

	private AreaType area;

	public int menuPivot = 1;
	private int itemPivot = 1;
	private int itemOptionsPivot = 1;
	private int battlePivot = 1;
	private int movePivot = 1;
	private int filePivot = 1;
	private int storePivot = 1;
	private int titleScreenPivot = 1;

	public float fade = -0.32f;

	private boolean doneDrawing = false;
	private boolean isStore = false;
	private boolean afterMove = false;

	public void updateStore()
	{
		Item item = player.items.get(player.items.size() - 1);
		if (storeItems.contains(item) || item.getRawName().contains("gold") || storeItems.size() >= 12 * 6)
			return;

		int stock = random.nextInt(2) + 1; // How many of that item to add.
		for (int i = 0; i < stock; i++)
			storeItems.add(SerializationUtils.clone(item));
	}

	public int getFilePivot()
	{
		return filePivot;
	}

	private boolean isFolder(File file)
	{
		return file.isDirectory();
	}

	private boolean scanDirectory(String path)
	{
		File directory = new File(path);

		File[] list = directory.listFiles(this::isFolder);

		if (list == null)
			return false;

		saveFiles = new ArrayList<>();

		for (File string : list)
			saveFiles.add(string.getName());

		saveFiles.sort(null); // Sort the array of Strings.

		return true;
	}

	public void updateIgnoreList()
	{
		int i = 0;
		for (int[] array : ignoredTiles)
		{
			GameMap n = game.map.get(ignoredTilesString.get(i++).toLowerCase())[array[2]];
			int location = array[1] * n.length + array[0];
			n.map[location].setId(-1);
			System.out.println(location);
		}
	}

	public int getViewDistance()
	{
		return viewDistance;
	}

	public boolean isDebug()
	{
		return isDebug;
	}

	public void setDebug(boolean debug)
	{
		isDebug = debug;
	}

	private void chooseMove(Move move, Entity entity)
	{
		boolean missed = move.canUse();
		boolean isCritical = move.isCritical();
		String criticalHit = "";
		int damage;

		damage = move.calcDamage() + entity.stats.getAttack();

		battleMessage = true;
//		entity.stats.decHp(move.getRecoil());
		if (isCritical)
		{
			criticalHit = "A critical hit!";
			damage += Math.ceil(damage * 0.8f);
		}

		String message;

		if (missed)
		{
			message = String.format("%s used %s! It missed...      ", entity.getName(), move.getName());
		}
		else if (isChoosingMove)
		{
			damage -= battle.getOpponent().stats.getDefense();
			battle.getOpponent().stats.decHp(damage <= 0 ? 1 : damage);
			message = String.format("%s used %s! %s It did %d damage!          ", entity.getName(), move.getName(), criticalHit, damage <= 0 ? 1 : damage);
		}
		else
		{
			damage -= player.stats.getDefense();
			player.stats.decHp(damage <= 0 ? 1 : damage);
			message = String.format("%s used %s! %s It did %d damage!          ", entity.getName(), move.getName(), criticalHit, damage <= 0 ? 1 : damage);
		}
		battleMessageString = message;
	}

	private void chooseMoveOpponent()
	{
		chooseMove(battle.getOpponent().getRandomMove(), battle.getOpponent());
	}

	private void chooseMovePlayer(int pivot)
	{
		chooseMove(player.moves[pivot], player);
	}


	public void input()
	{
		int keyboard = Keyboard.getEventKey();
		if (Keyboard.getEventKeyState())
			return;

		if (keyboard == Keyboard.KEY_F12) // Restarts the program
		{
			setRunning(false);
			return;
		}

		if (isHelpScreen)
		{
			if (keyboard == game.config.keys.get("cancel"))
				isHelpScreen = false;

			return;
		}
		if (!paint.isDoneTextbox() && !isWaitingPrompt)
			return;
		if ((keyboard == game.config.keys.get("select") || keyboard == game.config.keys.get("cancel")) && isWaitingPrompt)
		{
			isWaitingPrompt = false;
			isDoneWaitingPrompt = true;
			return;
		}


		/* BATTLE: */
		if (isBattle && !isItemMenu)
		{
			/* OPPONENT'S TURN: */
			if (isOpponentTurn)
			{
				chooseMoveOpponent();

				isOpponentTurn = false;
				isChoosingMove = false;
				battleMessage = true;
				afterMove = true;

				isDrawingTextbox = true;
			}
			/* CHOOSING A MOVE: */
			else if (isChoosingMove)
			{
				if (keyboard == game.config.keys.get("select"))
				{
					chooseMovePlayer(movePivot - 1);

					battleMessage = true;
					isOpponentTurn = false;
					isChoosingMove = false;
					isDrawingTextbox = true;
					isWaitingPrompt = true;
					afterMove = false;
				}
				else if (keyboard == game.config.keys.get("cancel"))
				{
					isChoosingMove = false;
				}
				else if (keyboard == game.config.keys.get("menu_up"))
				{
					movePivot--;
					playSound("select");
				}
				else if (keyboard == game.config.keys.get("menu_down"))
				{
					movePivot++;
					playSound("select");
				}
				else if (keyboard == game.config.keys.get("menu_right"))
				{
					movePivot = player.moves.length;
				}
				else if (keyboard == game.config.keys.get("menu_left"))
				{
					movePivot = 1;
				}

				if (movePivot < 1)
					movePivot = player.moves.length;
				else if (movePivot > player.moves.length)
					movePivot = 1;
			}
			else
			{
				if (Keyboard.getEventKeyState())
					return;

				if (keyboard == game.config.keys.get("select"))
				{
					switch (battleOptions[battlePivot - 1].toLowerCase())
					{
						case "attack":
						{
							setChoosingMove(true);
							return;
						}
						case "item":
						{
							setItemMenu(true);
							setChoosingMove(false);

							return;
						}
					}
				}

				if (keyboard == game.config.keys.get("menu_up"))
				{
					battlePivot--;
					playSound("select");
				}
				else if (keyboard == game.config.keys.get("menu_down"))
				{
					battlePivot++;
					playSound("select");
				}

				if (battlePivot < 1)
					battlePivot = 2;
				else if (battlePivot > 2)
					battlePivot = 1;

				return;

			}
			if (!isItemMenu)
				return;
		}

//		if (keyboard == Keyboard.KEY_F1)
//			setDebug(!isDebug());

		if (isDrawingTextbox && !isItemMenu)
			return;

		if (keyboard == game.config.keys.get("menu") && !isMenu() && !isItemMenu() && !isTitleScreen() && !isEvent() && !isBattle)
		{
			isMenu = true;
			playSound("open_menu");
			return;
		}

		/* TITLE SCREEN: */
		if (isTitleScreen && !isSelectingFile && !creatingNewGame)
		{
//			if (keyboard > 0 && keyboard != Keyboard.KEY_TAB && keyboard != Keyboard.KEY_LSHIFT && keyboard != Keyboard.KEY_RSHIFT) // Disallow alt-tab, shift
			if (fade < 0.8f)
				return;

			else if (keyboard == game.config.keys.get("menu_up"))
			{
				titleScreenPivot--;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}
			else if (keyboard == game.config.keys.get("menu_down"))
			{
				titleScreenPivot++;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}
			else if (keyboard == game.config.keys.get("select")) // TODO: move this to the top (first if statement)
			{
				switch (titleScreenOptions[titleScreenPivot - 1].toLowerCase())
				{
					case "continue": // TODO: implement. Store a text file in saves/ with the name of the most recent save
						break;
					case "new game":
						creatingNewGame = true;
						creatingNewGameInput = true;
						return;
					case "load":
						setSelectingFile(scanDirectory("saves/")); // This line is a little confusing. Basically, if we scan the directory and find at least one save, then scanDirectory will return true, and so we will select a file. If not, then isSelectingFile will be false.
						return;
					case "help":
						isHelpScreen = true;
						return;
					case "options": // TODO: implement options menu
						break;

				}
			}

			if (titleScreenPivot > titleScreenOptions.length)
				titleScreenPivot = 1;
			else if (titleScreenPivot < 1)
				titleScreenPivot = titleScreenOptions.length;

		}

		/* CREATING NEW GAME SCREEN: */
		if (creatingNewGame)
		{
			if (creatingNewGameInput)
			{
				if (keyboard == Keyboard.KEY_RETURN)
				{
					creatingNewGameInput = false;
					creatingNewGame = false;
					setTitleScreen(false);
					game.config.music.get("title_screen").stop();
					if (!game.config.sounds.get("start_game").isPlaying())
						game.config.sounds.get("start_game").playAsSoundEffect(1f, 1f, false);
					playMusic("village");
					isPlayingGame = false;

					if (name.isEmpty())
						name = "???";
					if (name.length() >= 10)
						name = name.substring(0, 10);

					player.setName(name);
					game.savefile.writeSave(name);
				}
				else if (keyboard == Keyboard.KEY_BACK)
				{
					if (name.length() >= 1)
						name = name.substring(0, name.length() - 1);
				}
				else
				{
					name += Keyboard.getKeyName(Keyboard.getEventKey());
				}
			}
			return;
		}

		/* SELECTING A FILE SCREEN: */
		if (isSelectingFile)
		{
			if (keyboard == game.config.keys.get("cancel"))
			{
				isSelectingFile = false;
				return;
			}
			else if (keyboard == game.config.keys.get("menu_up"))
			{
				filePivot--;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}
			else if (keyboard == game.config.keys.get("menu_down"))
			{
				filePivot++;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}
			else if (keyboard == game.config.keys.get("select"))
			{
				if (saveFiles.isEmpty())
				{
					isSelectingFile = false;
					return;
				}

				name = saveFiles.get(filePivot - 1);

				creatingNewGameInput = false;
				creatingNewGame = false;
				isSelectingFile = false;
				isTitleScreen = false;
				isPlayingGame = false;

				game.config.music.get("title_screen").stop();
				if (!game.config.sounds.get("start_game").isPlaying())
					game.config.sounds.get("start_game").playAsSoundEffect(1f, 1f, false);
				playMusic("village");

				game.savefile.readSave(name);

			}

			if (filePivot < 1)
				filePivot = saveFiles.size();
			else if (filePivot > saveFiles.size())
				filePivot = 1;
			return;
		}

		if (isMenu && !isItemMenu && !isStatsMenu && !isStore)
		{
//			if (keyboard == game.config.keys.get("cancel"))
//				setMenu(false);
			if (keyboard == game.config.keys.get("menu_up"))
			{
				menuPivot--;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}
			else if (keyboard == game.config.keys.get("menu_down"))
			{
				menuPivot++;
				game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
			}

			if (menuPivot < 1)
				menuPivot = options.length;
			else if (menuPivot > options.length)
				menuPivot = 1;
			if (keyboard == game.config.keys.get("select") && !isInfoItemMenu)
			{
				switch (options[menuPivot - 1].toLowerCase())
				{
					case "items":
						setItemMenu(true);
						game.config.sounds.get("open_menu").playAsSoundEffect(1, 1, false);
//						itemPivot = 1;
						return;
					case "stats":
						isStatsMenu = true;
						playSound("open_menu");
						return;
					case "save":
						isMenu = false;
						game.savefile.writeSave(player.getName());
						paint.drawTextbox("Saved game.       ", 0.3f);
						break;
					case "read":
						game.savefile.readSave(player.getName());
						isMenu = false;
						break;
					case "store":
						isStore = true;
						playSound("open_menu");
						return;
				}
			}

		}

		/* STORE MENU: */
		if (isStore)
		{
			/* BOUGHT AN ITEM: */
			if (keyboard == game.config.keys.get("select") && storePivot <= storeItems.size() && storePivot >= 0)
			{
				Item item = storeItems.get(storePivot - 1);
				int res = player.stats.getMoney() - item.getPrice();
				if (res < 0) // Not enough money
				{
					paint.drawTextbox("Not enough money.               ", 0.3f);
					return;
				}
				// Have enough money:
				storeItems.remove(storePivot - 1);
				player.items.add(item);
				player.stats.setMoney(res);

				paint.drawTextbox(String.format("Bought the %s for %d!         ", item.getName(), item.getPrice()), 0.35f);
			}

			if (keyboard == game.config.keys.get("cancel"))
			{
				isStore = false;
				return;
			}

			if (keyboard == game.config.keys.get("menu_up"))
			{
				storePivot -= 12;
				playSound("select");
			}
			else if (keyboard == game.config.keys.get("menu_down"))
			{
				storePivot += 12;
				playSound("select");
			}
			else if (keyboard == game.config.keys.get("menu_right"))
			{
				storePivot++;
				playSound("select");
			}
			else if (keyboard == game.config.keys.get("menu_left"))
			{
				storePivot--;
				playSound("select");
			}


			if (storePivot > 12 * 6)
				storePivot = 1;
			else if (storePivot < 0)
				storePivot = 12 * 6;
		}

		/* ITEM MENU: */
		if (isItemMenu)
		{
			if (keyboard == game.config.keys.get("select") && !isItemMenuMore && !isInfoItemMenu)
			{
//				setItemOptionsPivot(0);
//				setItemPivot(-1);
				if (itemPivot >= 0 && itemPivot < player.items.size() && player.items.get(itemPivot) != null)
					setItemMenuMore(true);
				return;
			}

			if (isItemMenuMore())
			{
				if (keyboard == game.config.keys.get("cancel"))
				{
					setItemMenuMore(false);
					return;
				}
				if (keyboard == game.config.keys.get("menu_up"))
				{
					itemOptionsPivot--;
				}
				else if (keyboard == game.config.keys.get("menu_down"))
				{
					itemOptionsPivot++;
				}

				if (itemOptionsPivot > optionsItems.length)
					itemOptionsPivot = 1;
				else if (itemOptionsPivot < 1)
					itemOptionsPivot = optionsItems.length;

				if (keyboard == game.config.keys.get("select"))
				{
					System.out.println(optionsItems[itemOptionsPivot - 1].toLowerCase());
					switch (optionsItems[itemOptionsPivot - 1].toLowerCase())
					{
						case "use":
						{
							Item item = player.items.remove(itemPivot);
							if (item != null)
							{
								System.out.println("using item");
								item.use(player);
								setItemMenuMore(false);
								game.config.sounds.get("eat").playAsSoundEffect(1f, 1f, false);
							}
							return;
						}
						case "trash":
						{
							Item item = player.items.remove(itemPivot);
							if (item != null)
							{
								game.config.sounds.get("drop").playAsSoundEffect(1f, 1f, false);
								setItemMenuMore(false);
								Integer[] newValues = {0, 0};

								Integer[] values = player.itemsUsed.get(item.getRawName());
								if (values == null)
								{
									player.itemsUsed.put(item.getRawName(), newValues);
								}
								else
								{
									values[1]++;
									player.itemsUsed.put(item.getRawName(), values);
								}
							}
							return;
						}
						case "sell":
						{
							Item item = player.items.remove(itemPivot);
							if (item == null)
								return;
							player.stats.incMoney(item.getSellPrice());
							paint.drawTextbox(String.format("Sold the %s for %d!         ", item.getName().toLowerCase(), item.getSellPrice()), 0.35f);
							itemPivot = 1;
							return;
						}
					}
				}
			}
			else if (isInfoItemMenu())
			{
				if (keyboard > 0)
				{
					setInfoItemMenu(false);
					setItemOptionsPivot(1);
					return;
				}
			}
			else
			{
				if (keyboard == game.config.keys.get("menu_up"))
				{
					itemPivot -= 12;
					game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
				}
				else if (keyboard == game.config.keys.get("menu_down"))
				{
					itemPivot += 12;
					game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
				}
				else if (keyboard == game.config.keys.get("menu_right"))
				{
					game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
					itemPivot++;
				}
				else if (keyboard == game.config.keys.get("menu_left"))
				{
					game.config.sounds.get("select").playAsSoundEffect(1, 1, false);
					itemPivot--;
				}
			}

//			if (keyboard == game.config.keys.get("select") && itemPivot < player.items.size() - 1 && itemPivot >= 0)
//			{
//				Item item = player.items.remove(itemPivot);
//				if (item != null)
//					item.use(player);
//			}
			if (keyboard == game.config.keys.get("cancel"))
			{
				setItemMenu(false);
				return;
			}

			if (itemPivot > 12 * 6)
				itemPivot = 1;
			else if (itemPivot < 0)
				itemPivot = 12 * 6;

		}

		if (isStatsMenu)
		{
			if (keyboard == game.config.keys.get("cancel"))
			{
				isStatsMenu = false;
				return;
			}
		}

		if (keyboard == game.config.keys.get("cancel"))
		{
			if (isMenu() && !isItemMenu && !isStatsMenu)
				isMenu = false;
		}
	}

	public boolean isMenu()
	{
		return isMenu;
	}

	public AreaType getArea()
	{
		return area;
	}

	public void setArea(AreaType area)
	{
		this.area = area;
	}

	public String getCurrentArea()
	{
		return currentArea;
	}

	public void setCurrentArea(String currentArea)
	{
		this.currentArea = currentArea;
	}

	public boolean isBattle()
	{
		return isBattle;
	}

	public void setBattle(boolean battle)
	{
		this.isBattle = battle;
	}

	public void createBattle(String enemyName)
	{
		Entity enemy = SerializationUtils.clone(game.enemies.get(enemyName));
		enemy.setTexture(game.enemies.get(enemyName).getTexture());
		battle = new Battle(player, enemy);

		playMusic("battle", false);
		fade = -0.3f;
	}

	public boolean isItemMenu()
	{
		return isItemMenu;
	}

	public void setItemMenu(boolean itemMenu)
	{
		isItemMenu = itemMenu;
	}

	public int getItemPivot()
	{
		return itemPivot;
	}

	public void setItemPivot(int itemPivot)
	{
		this.itemPivot = itemPivot;
	}

	public float getTemperature()
	{
		return temperature;
	}

	public void setTemperature(float temperature)
	{
		this.temperature = temperature;
	}

	public boolean isTitleScreen()
	{
		return isTitleScreen;
	}

	public void setTitleScreen(boolean titleScreen)
	{
		isTitleScreen = titleScreen;
	}

	public AreaType getPreviousArea()
	{
		return previousArea;
	}

	public void setPreviousArea(AreaType previousArea)
	{
		this.previousArea = previousArea;
	}

	public void playMusic(String name, boolean save)
	{
		if (currentMusic != null)
			currentMusic.stop();
		Audio audio = game.config.music.get(name);
		if (audio != null)
		{
			audio.playAsMusic(1f, 1f, true);
			if (save)
				currentMusic = audio;
		}
	}

	public void playMusic(String name)
	{
		playMusic(name, true);
	}

	public void playSound(String name)
	{
		Audio audio = game.config.sounds.get(name);
		if (audio != null)
		{
			audio.playAsSoundEffect(1f, 1f, false);
			currentSound = audio;
		}
	}

	public void update()
	{
		if (isTitleScreen())
		{
//			if (!isPlayingGame && !currentMusic.isPlaying())
//			{
//				playMusic("village");
//				setTitleScreen(false);
//				fade = 0.85f;
//			}
		}
	}

	public boolean isDoneDrawing()
	{
		return doneDrawing;
	}

	public void setDoneDrawing(boolean doneDrawing)
	{
		this.doneDrawing = doneDrawing;
	}

	public boolean isItemMenuMore()
	{
		return isItemMenuMore;
	}

	public void setItemMenuMore(boolean itemMenuMore)
	{
		isItemMenuMore = itemMenuMore;
	}

	public int getItemOptionsPivot()
	{
		return itemOptionsPivot;
	}

	public void setItemOptionsPivot(int itemOptionsPivot)
	{
		this.itemOptionsPivot = itemOptionsPivot;
	}

	public boolean isInfoItemMenu()
	{
		return isInfoItemMenu;
	}

	public void setInfoItemMenu(boolean infoItemMenu)
	{
		isInfoItemMenu = infoItemMenu;
	}

	public boolean isEvent()
	{
		return isEvent;
	}

	public void setEvent(boolean event)
	{
		isEvent = event;
	}

	public int getBattlePivot()
	{
		return battlePivot;
	}

	public void setBattlePivot(int battlePivot)
	{
		this.battlePivot = battlePivot;
	}

	public boolean isChoosingMove()
	{
		return isChoosingMove;
	}

	public void setChoosingMove(boolean choosingMove)
	{
		isChoosingMove = choosingMove;
	}

	public int getMovePivot()
	{
		return movePivot;
	}

	public void setMovePivot(int movePivot)
	{
		this.movePivot = movePivot;
	}

	public boolean isBattleMessage()
	{
		return battleMessage;
	}

	public void setBattleMessage(boolean battleMessage)
	{
		this.battleMessage = battleMessage;
	}

	public String getBattleMessageString()
	{
		return battleMessageString;
	}

	public void setBattleMessageString(String battleMessageString)
	{
		this.battleMessageString = battleMessageString;
	}

	public boolean isEndingBattle()
	{
		return isEndingBattle;
	}

	public void setEndingBattle(boolean endingBattle)
	{
		isEndingBattle = endingBattle;
	}

	public boolean isOpponentTurn()
	{
		return isOpponentTurn;
	}

	public void setOpponentTurn(boolean opponentTurn)
	{
		isOpponentTurn = opponentTurn;
	}

	public boolean isLoadingScreen()
	{
		return isLoadingScreen;
	}

	public void setLoadingScreen(boolean loadingScreen)
	{
		isLoadingScreen = loadingScreen;
	}

	public boolean isLogo()
	{
		return isLogo;
	}

	public void setLogo(boolean logo)
	{
		isLogo = logo;
	}

	public boolean isMessage()
	{
		return isMessage;
	}

	public void setMessage(boolean message)
	{
		isMessage = message;
	}

	public boolean isDrawingTextbox()
	{
		return isDrawingTextbox;
	}

	public void setDrawingTextbox(boolean drawingTextbox)
	{
		isDrawingTextbox = drawingTextbox;
	}

	public boolean isWaitingPrompt()
	{
		return isWaitingPrompt;
	}

	public void setWaitingPrompt(boolean waitingPrompt)
	{
		isWaitingPrompt = waitingPrompt;
	}

	public boolean isDoneWaitingPrompt()
	{
		return isDoneWaitingPrompt;
	}

	public void setDoneWaitingPrompt(boolean doneWaitingPrompt)
	{
		isDoneWaitingPrompt = doneWaitingPrompt;
	}

	public boolean gainedHunger()
	{
		return hasGainedHunger;
	}

	public void setGainedHunger(boolean gainedHunger)
	{
		this.hasGainedHunger = gainedHunger;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setRunning(boolean running)
	{
		isRunning = running;
	}

	public boolean isLevelUp()
	{
		return isLevelUp;
	}

	public void setLevelUp(boolean levelUp)
	{
		isLevelUp = levelUp;
	}

	public boolean isStatsMenu()
	{
		return isStatsMenu;
	}

	public void setStatsMenu(boolean statsMenu)
	{
		isStatsMenu = statsMenu;
	}

	public boolean isSelectingFile()
	{
		return isSelectingFile;
	}

	public void setSelectingFile(boolean selectingFile)
	{
		isSelectingFile = selectingFile;
	}

	public boolean isCreatingNewGame()
	{
		return creatingNewGame;
	}

	public void setCreatingNewGame(boolean creatingNewGame)
	{
		this.creatingNewGame = creatingNewGame;
	}

	public String generateRandomEnemy(int id, int level)
	{
		System.out.println(level);
		int num = random.nextInt(10);
		switch (level)
		{
			case 1:
			{
				switch (num)
				{
					case 0:
						return "red_snake";
					default:
						return "snake";
				}
			}
			case 2:
			{
				switch (random.nextInt(5 * level))
				{
					case 0:
					case 1:
					case 2:
						return "death_snake";
					case 3:
						return "red_snake";
					default:
						return "blue_snake";
				}
			}
			case 3:
			case 4:
			case 5:
			case 6:
				switch (num * level)
				{
					case 0:
					case 1:
					case 2:
						return "tiger";
					default:
						return "death_snake";
				}
			default:
				return "snake";
		}
	}

	public boolean isStore()
	{
		return isStore;
	}

	public int getStorePivot()
	{
		return storePivot;
	}

	public void setStorePivot(int storePivot)
	{
		this.storePivot = storePivot;
	}

	public boolean isAfterMove()
	{
		return afterMove;
	}

	public void setAfterMove(boolean afterMove)
	{
		this.afterMove = afterMove;
	}

	public boolean isHelpScreen()
	{
		return isHelpScreen;
	}

	public void setHelpScreen(boolean helpScreen)
	{
		isHelpScreen = helpScreen;
	}

	public int getTitleScreenPivot()
	{
		return titleScreenPivot;
	}

	public void setTitleScreenPivot(int titleScreenPivot)
	{
		this.titleScreenPivot = titleScreenPivot;
	}
}
