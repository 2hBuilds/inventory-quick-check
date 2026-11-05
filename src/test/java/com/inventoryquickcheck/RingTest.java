package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.image.BufferedImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class RingTest
{
	private static final Color RED = new Color(255, 0, 0);

	private static BufferedImage item()
	{
		final BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		for (int y = 4; y <= 5; y++)
		{
			for (int x = 4; x <= 5; x++)
			{
				image.setRGB(x, y, 0xFF336699);
			}
		}
		return image;
	}

	private static void check(int thickness, int size, int blockFrom, int blockTo, int itemFrom, int itemTo, int count)
	{
		final BufferedImage ring = Ring.of(item(), thickness, RED);
		assertEquals(size, ring.getWidth());
		assertEquals(size, ring.getHeight());
		int seen = 0;
		for (int y = 0; y < size; y++)
		{
			for (int x = 0; x < size; x++)
			{
				final boolean inBlock = x >= blockFrom && x <= blockTo && y >= blockFrom && y <= blockTo;
				final boolean inItem = x >= itemFrom && x <= itemTo && y >= itemFrom && y <= itemTo;
				if (inBlock && !inItem)
				{
					assertEquals(RED.getRGB(), ring.getRGB(x, y));
					seen++;
				}
				else
				{
					assertEquals(0, ring.getRGB(x, y) >>> 24);
				}
			}
		}
		assertEquals(count, seen);
	}

	@Test
	public void thicknessOne()
	{
		check(1, 12, 4, 7, 5, 6, 12);
	}

	@Test
	public void thicknessTwo()
	{
		check(2, 14, 4, 9, 6, 7, 32);
	}

	@Test
	public void thicknessThree()
	{
		check(3, 16, 4, 11, 7, 8, 60);
	}

	@Test
	public void hasPixels()
	{
		final BufferedImage blank = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
		assertFalse(Ring.hasPixels(blank));
		blank.setRGB(2, 1, 0xFF000000);
		assertTrue(Ring.hasPixels(blank));
	}
}
