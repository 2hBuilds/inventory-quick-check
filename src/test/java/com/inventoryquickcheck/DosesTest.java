package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class DosesTest
{
	private final ItemFacts facts = new FakeItemFacts();

	@Test
	public void readsTheTrailingNumber()
	{
		assertEquals(3, Doses.dose("Saradomin brew(3)"));
		assertEquals(5, Doses.dose("Ring of wealth (5)"));
		assertEquals(-1, Doses.dose("Shark"));
	}

	@Test
	public void tellsPotionsFromJewellery()
	{
		assertTrue(Doses.isDoseItem(6685, facts));
		assertTrue(Doses.isDoseItem(11521, facts));
		assertFalse(Doses.isDoseItem(1712, facts));
		assertFalse(Doses.isDoseItem(2552, facts));
	}
}
