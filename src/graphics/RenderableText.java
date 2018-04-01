package graphics;

import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by minnow on 12/9/17
 */
public class RenderableText extends TrueTypeFont implements Serializable
{
	public RenderableText(Font font, boolean antiAlias)
	{
		super(font, antiAlias);
	}
}
