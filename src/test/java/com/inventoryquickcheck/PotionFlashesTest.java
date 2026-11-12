package com.inventoryquickcheck;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import org.junit.After;
import org.junit.Test;

/** F1-F4: the Potion flashes choices, their default, and their settings-list bar in the outline colour. */
public class PotionFlashesTest
{
	@After
	public void backToRed()
	{
		PotionFlashes.useColour(Color.RED);
	}

	@Test
	public void f1_labelsInOrderAndDefault()
	{
		final String[] expected = {"Steady", "1 flash", "2 flashes", "3 flashes", "5 flashes", "Nonstop"};
		final PotionFlashes[] all = PotionFlashes.values();
		assertEquals(expected.length, all.length);
		for (int i = 0; i < expected.length; i++)
		{
			assertEquals(expected[i], all[i].label());
		}
		assertEquals(PotionFlashes.ONE, new InventoryQuickCheckConfig()
		{
		}.potionFlashes());
	}

	@Test
	public void f2_runs()
	{
		assertArrayEquals(new int[]{42}, PotionFlashes.STEADY.runs());
		assertArrayEquals(new int[]{4, 3, 35}, PotionFlashes.ONE.runs());
		assertArrayEquals(new int[]{4, 3, 4, 3, 28}, PotionFlashes.TWO.runs());
		final int[] keep = PotionFlashes.KEEP_FLASHING.runs();
		assertEquals(12, keep.length);
		assertEquals(3, keep[11]);
		for (final PotionFlashes flashes : PotionFlashes.values())
		{
			int sum = 0;
			for (final int w : flashes.runs())
			{
				sum += w;
			}
			assertEquals(flashes.name(), 42, sum);
		}
	}

	@Test
	public void f3_menuTextDrawsTheBarInTheColour()
	{
		final String text = PotionFlashes.ONE.menuText(new Color(239, 16, 16));
		assertTrue(text, text.startsWith("<html><table"));
		assertTrue(text, text.contains("<td bgcolor=#ef1010 width=4></td>"));
		assertTrue(text, text.contains("<td bgcolor=#505050 width=3></td>"));
		assertTrue(text, text.endsWith("<td>&nbsp;1 flash</td></tr></table></html>"));
	}

	@Test
	public void f4_toStringUsesTheColourInUse()
	{
		final Color blue = new Color(0, 128, 255);
		PotionFlashes.useColour(blue);
		assertEquals(PotionFlashes.TWO.menuText(blue), PotionFlashes.TWO.toString());
	}
}
