package core;

import java.io.Serializable;
import java.util.Random;

import static core.Shared.game;

public class Stats implements Serializable
{
	private Random random = new Random();

	private int money = 0;
	private long playtimeInSeconds = 0;

	private float exp = 0;
	private float nextExp = 30;
	private int level = 1;

	private int totalHp = 100;
	private int totalHunger = 100;

	private int hunger = 0;
	private int hydration = 0;

	private int totalHydration = 100;
	private int sleep = 0;
	private int totalSleep = 100;

	private int hp = 100;
	private int attack = 0;
	private int defense = 0;
	private int speed = 0;

	private int actualAmount = 0;

	private boolean alive = true;

	public float getNextExp()
	{
		return nextExp;
	}

	private void levelUp()
	{
		setTotalHp(getTotalHp() + random.nextInt(6) + 10);
		setHp(getHp() + 10);
		setAttack(getAttack() + random.nextInt(5) + 1);
		setDefense(getDefense() + random.nextInt(5) + 1);
	}


	private void updateExp()
	{
		if (exp >= nextExp) // level up!
		{
			nextExp += (float) Math.floor(level * 2.7f + 10);
			levelUp();
			level++;
			game.gameState.setLevelUp(true);
			exp = 0;
		}
	}

	public boolean checkAlive()
	{
		alive = false;

		if (getHp() <= 0)
			setHp(0);
		else if (getHunger() >= getTotalHunger())
			setHunger(getTotalHunger());
		else if (getHydration() >= getTotalHydration())
			setHydration(getTotalHydration());
		else if (getSleep() >= getTotalSleep())
			setSleep(getTotalSleep());
		else
			alive = true;

		return alive;
	}

	public void gainExp(int amt)
	{
		exp += amt;
		updateExp();
	}

	public void decHp(int amt)
	{
		setHp(getHp() - amt);
	}

	public long getPlaytimeInSeconds()
	{
		return playtimeInSeconds;
	}

	public void setPlaytimeInSeconds(long playtimeInSeconds)
	{
		this.playtimeInSeconds = playtimeInSeconds;
	}

	public int getHunger()
	{
		return hunger;
	}

	public void setHunger(int hunger)
	{
		if (hunger > getTotalHunger())
			hunger = getTotalHunger();
		this.hunger = (hunger < 0 ? 0 : hunger);
	}

	public int getHydration()
	{
		return hydration;
	}

	public void setHydration(int hydration)
	{
		this.hydration = (hydration < 0 ? 0 : hydration);
	}

	public int getSleep()
	{
		return sleep;
	}

	public void setSleep(int sleep)
	{
		this.sleep = sleep;
	}

	public int getAttack()
	{
		return attack;
	}

	public void setAttack(int attack)
	{
		this.attack = attack;
	}


	public int getHp()
	{
		return hp;
	}

	public void setHp(int hp)
	{
		if (hp > getTotalHp())
			hp = getTotalHp();
		this.hp = hp <= 0 ? 0 : hp;
	}

	public int getDefense()
	{
		return defense;
	}

	public void setDefense(int defense)
	{
		this.defense = defense;
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}

	public boolean isAlive()
	{
		return alive;
	}

	public void setAlive(boolean alive)
	{
		this.alive = alive;
	}

	public float getExp()
	{
		return exp;
	}

	public void setExp(float exp)
	{
		this.exp = exp;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public int getTotalHp()
	{
		return totalHp;
	}

	public void setTotalHp(int totalHp)
	{
		this.totalHp = totalHp;
	}

	public int getTotalHunger()
	{
		return totalHunger;
	}

	public void setTotalHunger(int totalHunger)
	{
		this.totalHunger = totalHunger;
	}

	public int getTotalHydration()
	{
		return totalHydration;
	}

	public void setTotalHydration(int totalHydration)
	{
		this.totalHydration = totalHydration;
	}

	public int getTotalSleep()
	{
		return totalSleep;
	}

	public void setTotalSleep(int totalSleep)
	{
		this.totalSleep = totalSleep;
	}

	public void incHunger(int i)
	{
		setHunger(getHunger() + i);
	}

	public void incHydration(int i)
	{
		setHydration(getHydration() + i);
	}

	public void incSleep(int i)
	{
		setSleep(sleep + i);
	}

	public void incMoney(int i)
	{
		setMoney(money + i);
	}

	public int getMoney()
	{
		return money;
	}

	public void setMoney(int money)
	{
		this.money = money;
	}
}
