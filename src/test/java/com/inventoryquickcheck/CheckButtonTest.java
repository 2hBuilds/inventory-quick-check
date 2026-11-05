package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;

/** T21 and T23: where the button goes, and when it is drawn. */
public class CheckButtonTest
{
	@Test
	public void spotCentresOnTheTab()
	{
		assertEquals(new Point(104, 206), CheckButton.spot(new Rectangle(100, 200, 33, 36), new Rectangle(1, 2, 3, 4), null, 24, 24));
	}

	@Test
	public void spotFallsBackToLeftOfTheItems()
	{
		assertEquals(new Point(472, 300), CheckButton.spot(null, new Rectangle(500, 300, 100, 100), null, 24, 24));
	}

	@Test
	public void spotKeepsTheSavedPoint()
	{
		assertEquals(new Point(50, 60),
			CheckButton.spot(new Rectangle(100, 200, 33, 36), new Rectangle(500, 300, 10, 10), new Point(50, 60), 24, 24));
	}

	@Test
	public void spotIsNullWithNothing()
	{
		assertNull(CheckButton.spot(null, null, null, 24, 24));
	}

	@Test
	public void stoneOfPicksTheAttackTabOfEachLayout()
	{
		assertEquals(net.runelite.api.gameval.InterfaceID.Toplevel.STONE0,
			CheckButton.stoneOf(net.runelite.api.gameval.InterfaceID.TOPLEVEL));
		assertEquals(net.runelite.api.gameval.InterfaceID.ToplevelOsrsStretch.STONE0,
			CheckButton.stoneOf(net.runelite.api.gameval.InterfaceID.TOPLEVEL_OSRS_STRETCH));
		assertEquals(net.runelite.api.gameval.InterfaceID.ToplevelPreEoc.STONE0,
			CheckButton.stoneOf(net.runelite.api.gameval.InterfaceID.TOPLEVEL_PRE_EOC));
		assertEquals(-1, CheckButton.stoneOf(net.runelite.api.gameval.InterfaceID.TOPLEVEL_DISPLAY));
	}

	@Test
	public void shouldDrawNeedsBothThings()
	{
		assertFalse(CheckButton.shouldDraw(false, true));
		assertFalse(CheckButton.shouldDraw(true, false));
		assertTrue(CheckButton.shouldDraw(true, true));
	}
}
