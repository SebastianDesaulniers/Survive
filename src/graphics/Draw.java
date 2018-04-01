package graphics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.*;

import java.awt.*;
import java.awt.Color;

import static core.Shared.game;

/**
 * Created by minnow on 12/7/17
 */
public class Draw
{
	/**
	 * Default scale factor
	 */
	private float previousScale = 1f;

	private String currentString = null;
	private String finalString = null;
	private float currentPivot = 0;

	private boolean doneTextbox = true;

	/**
	 * Previous color
	 */
	private Color previousColor = new Color(0xFFFFFF);

	private float previousSpeed = 0.1f;

	public void drawTextbox(@NotNull String string, float speed)
	{
		// Draw the borders for the textbox
		drawRectangle(220, 645, 570, 60, new Color(1f, 1f, 1f, 1f));
		drawRectangle(225, 650, 560, 50, new Color(0f, 0f, 0f, 1f));

//		if (finalString == null)
//		{
//			finalString = string;
//			currentPivot = 1;
//			doneTextbox = false;
//		}

		game.gameState.setDrawingTextbox(true);

		GL11.glEnable(GL11.GL_BLEND);
		String substring = string.substring(0, (int) (currentPivot += speed));

		drawString(230, 658, substring, game.config.fonts.get("debug"));

		if (currentPivot >= string.length() - 1)
			game.gameState.setDrawingTextbox(false);

//		GL11.glEnable(GL11.GL_BLEND);
	}

	/**
	 * Renders text to the screen.
	 *
	 * @param x    X coordinate on screen.
	 * @param y    Y coordinate on screen.
	 * @param text The text to be drawn on screen.
	 * @param font The font that is used.
	 */
	@SuppressWarnings("WeakerAccess")
	public void drawString(float x, float y, @NotNull String text, @NotNull RenderableText font)
	{
		font.drawString(x, y, text);
	}


	public void drawString(float x, float y, @NotNull String text, @NotNull RenderableText font, @Nullable Color color)
	{
//		GL11.glDisable(GL11.GL_BLEND);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		if (color == null)
			color = previousColor;
		org.newdawn.slick.Color c = org.newdawn.slick.Color.decode(Integer.toString(color.getRGB()));
		c.bind();
		font.drawString(x, y, text, c);
//		org.newdawn.slick.Color.white.bind();
		GL11.glEnable(GL11.GL_BLEND);
	}

	/**
	 * Renders text to the screen.
	 *
	 * @param point Point containing the xy coordinates. Defaults to 0, 0 if null.
	 * @param text  The text to be drawn on screen.
	 * @param font  The font that is used.
	 */
	public void drawString(@Nullable Point point, String text, RenderableText font)
	{
		if (point == null)
			point = new Point(0, 0);
		drawString((float) point.getX(), (float) point.getY(), text, font);
	}


	/**
	 * Scales the screen
	 *
	 * @param factor Factor to scale the screen by.
	 *               Must be greater than {@code 0}.
	 */
	public void scale(float factor)
	{
		if (factor <= 0)
			throw new RuntimeException(String.format("Factor is %f, must be greater than 0.", factor));
		GL11.glScalef(factor, factor, 1f);
		previousScale = factor;
	}

	/**
	 * Scales the screen by the last used value
	 */
	public void scale()
	{
		scale(previousScale);
	}

	/**
	 * Scales the screen by the previous scale factor's inverse.
	 * Example: 2 => 0.5
	 */
	public void scaleBack()
	{
		scale(1 / previousScale);
	}

	/**
	 * Draws a texture region to the screen.
	 *
	 * @param x       x coordinate on screen.
	 * @param y       y coordinate on screen.
	 * @param sx      image x.
	 * @param sy      image y.
	 * @param sw      image width.
	 * @param sh      image height.
	 * @param texture Texture to be rendered.
	 */
	public void drawImageRegion(float x, float y, float sx, float sy, float sw, float sh, float imageWidth, float imageHeight, @NotNull Texture texture)
	{
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		texture.texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(sx / imageWidth, sy / imageHeight);
			GL11.glVertex2f(x, y);

			GL11.glTexCoord2f((sx + sw) / imageWidth, sy / imageHeight);
			GL11.glVertex2f(x + 64, y);

			GL11.glTexCoord2f((sx + sw) / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(x + 64, y + 64);

			GL11.glTexCoord2f(sx / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(x, y + 64);
		}
		GL11.glEnd();
	}

	public void drawImageRegionRaw(float x, float y, float sx, float sy, float sw, float sh, float imageWidth, float imageHeight, @NotNull org.newdawn.slick.opengl.Texture texture)
	{
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(sx / imageWidth, sy / imageHeight);
			GL11.glVertex2f(x, y);

			GL11.glTexCoord2f((sx + sw) / imageWidth, sy / imageHeight);
			GL11.glVertex2f(x + 64, y);

			GL11.glTexCoord2f((sx + sw) / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(x + 64, y + 64);

			GL11.glTexCoord2f(sx / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(x, y + 64);
		}
		GL11.glEnd();
	}

	public void drawImageFull(float sx, float sy, float sw, float sh, float imageWidth, float imageHeight, @NotNull Texture texture)
	{
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		texture.texture.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(sx / imageWidth, sy / imageHeight);
			GL11.glVertex2f(0, 0);

			GL11.glTexCoord2f((sx + sw) / imageWidth, sy / imageHeight);
			GL11.glVertex2f(sw, 0);

			GL11.glTexCoord2f((sx + sw) / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(sw, sh);

			GL11.glTexCoord2f(sx / imageWidth, (sy + sh) / imageHeight);
			GL11.glVertex2f(0, sh);
		}
		GL11.glEnd();
	}


	/**
	 * Draws a texture to the screen
	 *
	 * @param x       x coordinate on screen.
	 * @param y       y coordinate on screen.
	 * @param width   width of texture.
	 * @param height  height of texture.
	 * @param texture texture to be rendered.
	 */
	public void drawImage(float x, float y, float width, float height, @NotNull Texture texture)
	{
		texture.texture.bind();

		float x1 = texture.getOffsetX(), y1 = texture.getOffsetY();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(x1 / 2048, y1 / 2048f);
			GL11.glVertex2f(x, y);

			GL11.glTexCoord2f((x1 + width) / 2048, y1 / 2048);
			GL11.glVertex2f(x + 64, y);

			GL11.glTexCoord2f((x1 + width) / 2048, (y1 + height) / 2048);
			GL11.glVertex2f(x + 64, y + 64);

			GL11.glTexCoord2f(x1 / 2048, (y1 + height) / 2048);
			GL11.glVertex2f(x, y + 64);
		}
		GL11.glEnd();
	}

	/**
	 * Draws a rectangle to the screen.
	 *
	 * @param x      x coordinate.
	 * @param y      y coordinate.
	 * @param width  width of rectangle.
	 * @param height height of rectangle.
	 */
	@SuppressWarnings("WeakerAccess")
	public void drawRectangle(float x, float y, float width, float height)
	{
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(x, y);
			GL11.glVertex2f(x + width, y);
			GL11.glVertex2f(x + width, y + height);
			GL11.glVertex2f(x, y + height);
		}
		GL11.glEnd();
	}

	/**
	 * Draws a rectangle to the screen.
	 *
	 * @param rectangle Rectangle area
	 */
	public void drawRectangle(@NotNull Rectangle rectangle)
	{
		setColor(previousColor);
		drawRectangle((float) rectangle.getX(), (float) rectangle.getY(), (float) rectangle.getWidth(), (float) rectangle.getHeight());
	}

	/**
	 * Draws a rectangle to the screen.
	 *
	 * @param rectangle Rectangle
	 * @param color     color to be drawn, defaults to black
	 */
	public void drawRectangle(@NotNull Rectangle rectangle, @Nullable Color color)
	{
		setColor(color);
		drawRectangle(rectangle);
		setColor(previousColor);
	}

	/**
	 * Draws a rectangle to the screen.
	 *
	 * @param x      x coordinate.
	 * @param y      y coordinate.
	 * @param width  width of rectangle.
	 * @param height height of rectangle.
	 * @param color  Color of the rectangle
	 */
	public void drawRectangle(float x, float y, float width, float height, @Nullable Color color)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		setColor(color);
		drawRectangle(x, y, width, height);
		setColor(previousColor);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	/**
	 * Draws a triangle to the screen.
	 *
	 * @param p1 First point.
	 * @param p2 Second point.
	 * @param p3 Third point.
	 */
	public void drawTriangle(Point p1, Point p2, Point p3)
	{
		GL11.glBegin(GL11.GL_POLYGON);
		{
			GL11.glVertex2f((float) p1.getX(), (float) p1.getY());
			GL11.glVertex2f((float) p2.getX(), (float) p2.getY());
			GL11.glVertex2f((float) p3.getX(), (float) p3.getY());
		}
		GL11.glEnd();
	}


	/**
	 * Draws a triangle to the screen.
	 *
	 * @param p1    First point.
	 * @param p2    Second point.
	 * @param p3    Third point.
	 * @param color Triangle color.
	 */
	public void drawTriangle(Point p1, Point p2, Point p3, Color color)
	{
		setColor(color);
		drawTriangle(p1, p2, p3);
		setColor(previousColor);
	}

	/**
	 * Draws a line to the screen.
	 *
	 * @param p1 First point.
	 * @param p2 Second point.
	 */
	public void drawLine(Point p1, Point p2)
	{
		GL11.glBegin(GL11.GL_LINES);
		{
			GL11.glVertex2f(p1.x, p1.y);
			GL11.glVertex2f(p2.x, p2.y);
		}
		GL11.glEnd();
	}

	/**
	 * Draws a line to the screen.
	 *
	 * @param p1        First point.
	 * @param p2        Second point.
	 * @param lineWidth Line width.
	 */
	public void drawLine(Point p1, Point p2, float lineWidth)
	{
		if (lineWidth <= 0)
			throw new RuntimeException(String.format("Line width is: %f, must be greater than 0.", lineWidth));
		GL11.glLineWidth(lineWidth);
	}

	/**
	 * Sets the drawing color
	 *
	 * @param color The color to be set, defaults to black.
	 */
	@SuppressWarnings("WeakerAccess")
	public void setColor(@Nullable Color color)
	{
		if (color == null)
			color = previousColor.brighter();
		GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());
	}

	public void resetDrawing()
	{
		doneTextbox = true;
		finalString = currentString = null;
		currentPivot = 0;
		game.gameState.setDrawingTextbox(false);
		game.gameState.setDoneDrawing(false);
	}

	public void finish()
	{
		if (game.gameState.isDoneWaitingPrompt())
		{
			System.out.println("Done drawing by waitingPrompt");
			game.gameState.setDoneWaitingPrompt(true);
			game.gameState.setWaitingPrompt(false);
			resetDrawing();
			return;
		}

		if (finalString == null)
			return;

		if (game.gameState.isWaitingPrompt() && !game.gameState.isDoneWaitingPrompt())
		{
			drawTextbox(finalString, previousSpeed);
			return;
		}

		doneTextbox = false;
		if (currentPivot < finalString.length() - 1.81f)
		{
			drawTextbox(finalString, previousSpeed);
		}
		else if (currentPivot >= finalString.length() - 1.81f)
		{
			game.gameState.setWaitingPrompt(true);
			game.gameState.setDoneWaitingPrompt(false);
		}
	}

	public boolean isDoneTextbox()
	{
		return doneTextbox;
	}
}
