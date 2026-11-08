package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import net.runelite.client.util.ImageUtil;
import org.junit.Test;

/** L1-L5: the Button look choices, their default, their pictures and their settings-list text. */
public class ButtonLookTest
{
	@Test
	public void l1_labelsInOrder()
	{
		final String[] expected = {"Slate check", "Gold ring check", "Teal check", "Sage question", "Potion", "Smiley",
			"Backpack", "Starburst", "Lightning", "Gold star", "Dark dot tick", "Stone tablet", "Gold coin"};
		final ButtonLook[] looks = ButtonLook.values();
		assertEquals(expected.length, looks.length);
		for (int i = 0; i < expected.length; i++)
		{
			assertEquals(expected[i], looks[i].label());
		}
	}

	@Test
	public void d2_defaultIsGoldRingCheck()
	{
		assertEquals(ButtonLook.GOLD_RING_CHECK, new InventoryQuickCheckConfig()
		{
		}.buttonLook());
	}

	@Test
	public void l3_picturesHaveTheirSizes()
	{
		for (final ButtonLook look : ButtonLook.values())
		{
			final BufferedImage big = ImageUtil.loadImageResource(ButtonLook.class, look.picture());
			assertNotNull(look.name(), big);
			assertEquals(look.name(), 24, big.getWidth());
			assertEquals(look.name(), 24, big.getHeight());
			final BufferedImage small = ImageUtil.loadImageResource(ButtonLook.class, look.smallPicture());
			assertNotNull(look.name(), small);
			assertEquals(look.name(), 16, small.getWidth());
			assertEquals(look.name(), 16, small.getHeight());
		}
	}

	@Test
	public void l4_menuTextWithoutAPictureIsTheLabel()
	{
		assertEquals("Teal check", ButtonLook.menuText("Teal check", null));
	}

	@Test
	public void l5_tealCheckShowsItsSmallPicture()
	{
		final String text = ButtonLook.TEAL_CHECK.toString();
		assertTrue(text, text.startsWith("<html><img src='"));
		assertTrue(text, text.contains("teal_check_16.png"));
		assertTrue(text, text.endsWith("' width=16 height=16>&nbsp;Teal check</html>"));
	}
}
