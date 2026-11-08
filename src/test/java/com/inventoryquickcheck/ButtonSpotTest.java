package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import net.runelite.api.gameval.InterfaceID;
import org.junit.Test;

public class ButtonSpotTest
{
	private static final Rectangle SLOT0 = new Rectangle(100, 200, 36, 32);

	@Test
	public void s1_insideFirstSlotHalfAGapLeftOfTheCorner()
	{
		assertEquals(new Point(89, 192), ButtonSpot.insideFirstSlot(SLOT0, 6, 16));
	}

	@Test
	public void s2_gap()
	{
		assertEquals(6, ButtonSpot.gap(SLOT0, new Rectangle(142, 200, 36, 32)));
		assertEquals(6, ButtonSpot.gap(SLOT0, null));
		assertEquals(6, ButtonSpot.gap(SLOT0, new Rectangle(130, 200, 36, 32)));
		assertEquals(10, ButtonSpot.gap(SLOT0, new Rectangle(146, 200, 36, 32)));
	}

	@Test
	public void s3_smallSize()
	{
		assertEquals(16, ButtonSpot.smallSize(SLOT0));
		assertEquals(20, ButtonSpot.smallSize(new Rectangle(0, 0, 40, 40)));
	}

	@Test
	public void b2_aboveWithoutTab()
	{
		assertEquals(new Point(106, 159), ButtonSpot.aboveInventory(SLOT0, 36, null, 24));
	}

	@Test
	public void b3_aboveWithTabOffset()
	{
		assertEquals(new Point(114, 156), ButtonSpot.aboveInventory(SLOT0, 36, new Point(26, -32), 24));
	}

	@Test
	public void b4_skillsOffset()
	{
		assertEquals(new Point(26, -32), ButtonSpot.tabOffset(new Rectangle(110, 150, 33, 36), SLOT0));
	}

	@Test
	public void b5_skillsOffsetNull()
	{
		assertNull(ButtonSpot.tabOffset(null, SLOT0));
		assertNull(ButtonSpot.tabOffset(new Rectangle(110, 150, 33, 36), null));
		assertNull(ButtonSpot.tabOffset(new Rectangle(110, 150, 0, 36), SLOT0));
	}

	@Test
	public void b6_pitch()
	{
		assertEquals(36, ButtonSpot.pitch(SLOT0, new Rectangle(100, 236, 36, 32)));
		assertEquals(36, ButtonSpot.pitch(SLOT0, null));
		assertEquals(36, ButtonSpot.pitch(SLOT0, new Rectangle(100, 200, 36, 32)));
		assertEquals(40, ButtonSpot.pitch(SLOT0, new Rectangle(100, 240, 36, 32)));
	}

	@Test
	public void b7_skillsStoneOf()
	{
		assertEquals(InterfaceID.Toplevel.STONE1, ButtonSpot.skillsStoneOf(InterfaceID.TOPLEVEL));
		assertEquals(InterfaceID.ToplevelOsrsStretch.STONE1,
			ButtonSpot.skillsStoneOf(InterfaceID.TOPLEVEL_OSRS_STRETCH));
		assertEquals(-1, ButtonSpot.skillsStoneOf(InterfaceID.TOPLEVEL_PRE_EOC));
		assertEquals(-1, ButtonSpot.skillsStoneOf(InterfaceID.TOPLEVEL_DISPLAY));
	}

	@Test
	public void f3_tabSpot()
	{
		assertEquals(new Rectangle(48, 158, 24, 24),
			ButtonSpot.tabSpot(new Rectangle(100, 200, 36, 32), new Point(-40, -30), 24));
		assertNull(ButtonSpot.tabSpot(null, new Point(1, 1), 24));
		assertNull(ButtonSpot.tabSpot(new Rectangle(100, 200, 36, 32), null, 24));
	}

	@Test
	public void n5_leftOfModernInventory()
	{
		assertEquals(new Point(834, 500), BoxAnchor.beside(ButtonSpot.leftOfModernInventory(
			new Rectangle(1000, 500, 36, 32), 42)).place(new Dimension(80, 300), new Dimension(1600, 900)));
	}
}
