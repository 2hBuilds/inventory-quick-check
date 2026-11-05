package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Builds the outline ring around an item sprite: a band of one colour, a set number of pixels wide, hugging the
 * item's shape on the outside. Pure Java2D, nothing from the game.
 */
public final class Ring
{
	private Ring()
	{
	}

	/**
	 * The ring for {@code item}: an image {@code 2 * thickness} wider and taller, the item's own pixels at offset
	 * (thickness, thickness) left transparent, and every other pixel within {@code thickness} (square distance) of an
	 * opaque item pixel set to {@code colour}.
	 */
	public static BufferedImage of(BufferedImage item, int thickness, Color colour)
	{
		final int w = item.getWidth();
		final int h = item.getHeight();
		final BufferedImage out = new BufferedImage(w + 2 * thickness, h + 2 * thickness, BufferedImage.TYPE_INT_ARGB);
		final int argb = colour.getRGB() | 0xFF000000;
		for (int y = 0; y < h + 2 * thickness; y++)
		{
			for (int x = 0; x < w + 2 * thickness; x++)
			{
				if (opaque(item, x - thickness, y - thickness))
				{
					continue;
				}
				if (nearOpaque(item, x - thickness, y - thickness, thickness))
				{
					out.setRGB(x, y, argb);
				}
			}
		}
		return out;
	}

	/** Whether any pixel of the image is not fully transparent. */
	public static boolean hasPixels(BufferedImage image)
	{
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				if (opaque(image, x, y))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean nearOpaque(BufferedImage item, int cx, int cy, int thickness)
	{
		for (int dy = -thickness; dy <= thickness; dy++)
		{
			for (int dx = -thickness; dx <= thickness; dx++)
			{
				if (opaque(item, cx + dx, cy + dy))
				{
					return true;
				}
			}
		}
		return false;
	}

	private static boolean opaque(BufferedImage image, int x, int y)
	{
		if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight())
		{
			return false;
		}
		return (image.getRGB(x, y) >>> 24) != 0;
	}
}
