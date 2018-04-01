package core;

import com.opencsv.CSVReader;
import graphics.Tileset;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by minnow on 12/11/17
 */
public class GameMap implements Serializable
{
	public ArrayList<String[]> list = new ArrayList<>();
	public Tile[] map;
	public int width = 0, height = 0;
	public int length = 300;

	public Tileset tileset = new Tileset(32, 32);

	public void load(String filename)
	{
		try
		{
			CSVReader reader = new CSVReader(new FileReader(filename));
			String[] tmp;
			try
			{
				while ((tmp = reader.readNext()) != null)
					list.add(tmp);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		length = list.size();
		int k = -1;
		int i = 0, j = 0;
		for (String[] aList : list)
		{
			if (k == -1)
			{
				map = new Tile[length * length];
				k++;
				continue;
			}
			i = 0;
			for (String strings : aList)
			{
				map[k] = new Tile();
				map[k++].setId(Integer.parseInt(strings));
				i++;
			}
			if (width == 0)
				width = i - 1;
		}

		height = k + 1;
		System.out.printf("The size is: %d\n", length);
	}

}
