package core;

import java.io.Serializable;

import static core.Shared.game;

public class Battle implements Serializable
{
	private Entity defender = null;
	private Entity opponent = null;

	private Entity[] current = {null, null};

	public int turn = 1;

	public Battle(Entity defender, Entity opponent)
	{
		this.defender = defender;
		this.opponent = opponent;

		this.current = new Entity[]{opponent, defender};
	}

	public void reset()
	{
		defender = opponent = null;
		current = null;
		turn = 1;
		game.gameState.setOpponentTurn(false);
		game.gameState.setBattle(false);
	}

	public void advance()
	{
//		game.gameState.setEndingBattle(false);
//		if (move.canUse() || true)
//			System.out.printf("MOVE: %s\n", move.getName());
//		current[turn].stats.decHp(damage);

		if (opponent.stats.getHp() <= 0)
		{
//			game.gameState.setDoneDrawing(false);
			game.gameState.setEndingBattle(true);
//			game.gameState.setBattleMessage(true);

			game.config.music.get("battle").stop();
			if (game.gameState.currentMusic != null)
				game.gameState.currentMusic.playAsMusic(1f, 1f, true);
			else
				game.gameState.playMusic("village");
		}
	}

	public Entity getOpponent()
	{
		return opponent;
	}

	public void setOpponent(Entity opponent)
	{
		this.opponent = opponent;
	}

	public Entity getDefender()
	{
		return defender;
	}

	public void setDefender(Entity defender)
	{
		this.defender = defender;
	}
}
