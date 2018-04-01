package file;

import java.io.Serializable;

public class GameClock implements Serializable
{
	private long timeOffset;
	private final float speed;
	private long time = 0;

	private int day = 1;

	public long prev = 0;

	public GameClock(float speed)
	{
		timeOffset = System.currentTimeMillis();
		this.speed = speed;
	}

	public long getTime()
	{
		return time;
	}

	public int getDay()
	{
		return day;
	}

	public void update()
	{
		prev = time;
		time = Math.round(((System.currentTimeMillis() - timeOffset) / speed));
		if (time >= 3600 * 24)
		{
			timeOffset = System.currentTimeMillis();
			day++;
		}
	}

	public int getSecond()
	{
		return (int) (time % 60);
	}

	public int getMinute()
	{
		return (int) ((time % 3600) / 60);
	}

	public int getHour()
	{
		return (int) (time / 3600);
	}
}
