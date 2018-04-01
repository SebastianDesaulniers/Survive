package core;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Random;

public class Move implements Serializable
{
	private static Random random = new Random();

	private String name = null;

	private MoveType moveType = null;

	private int damage = 0;
	private float accuracy = 1;
	private int recoil = 0;
	private float criticalChance = 0f;

	private float selfHeal = 0f;
	private float opponentHeal = 0f;

	public Move(@NotNull MoveType moveType, int damage, float accuracy, int recoil, float criticalChance)
	{
		setDamage(damage);
		setAccuracy(accuracy);
		setRecoil(recoil);
		setCriticalChance(criticalChance);
	}

	private float criticalHitBonus()
	{
		return (random.nextInt(100) > (100 - getCriticalChance() * 100)) ? 1f : 1.25f;
	}

	public int calcDamage()
	{
		return Math.round(damage * criticalHitBonus());
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int damage)
	{
		this.damage = damage;
	}

	public float getAccuracy()
	{
		return accuracy;
	}

	public void setAccuracy(float accuracy)
	{
		this.accuracy = accuracy;
	}

	public int getRecoil()
	{
		return recoil;
	}

	public void setRecoil(int recoil)
	{
		this.recoil = recoil;
	}

	public float getCriticalChance()
	{
		return criticalChance;
	}

	public void setCriticalChance(float criticalChance)
	{
		this.criticalChance = criticalChance;
	}

	public float getOpponentHeal()
	{
		return opponentHeal;
	}

	public void setOpponentHeal(float opponentHeal)
	{
		this.opponentHeal = opponentHeal;
	}

	public float getSelfHeal()
	{
		return selfHeal;
	}

	public void setSelfHeal(float selfHeal)
	{
		this.selfHeal = selfHeal;
	}

	public MoveType getMoveType()
	{
		return moveType;
	}

	public void setMoveType(MoveType moveType)
	{
		this.moveType = moveType;
	}

	public boolean canUse()
	{
		return random.nextInt(100) < (100 - getAccuracy() * 100);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isCritical()
	{
		return random.nextInt(100) > (100 - getCriticalChance() * 100);
	}
}
