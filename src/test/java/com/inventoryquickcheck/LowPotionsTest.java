package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import org.junit.Test;

public class LowPotionsTest
{
	private final ItemFacts facts = new FakeItemFacts();

	@Test
	public void p1_potionSlotsAreEveryPotion()
	{
		assertEquals(new TreeSet<>(Arrays.asList(0, 1, 3)),
			LowPotions.potionSlots(new int[]{6685, 6687, 2562, 3024, 385, -1}, facts));
	}

	@Test
	public void c3_isLow()
	{
		assertTrue(LowPotions.isLow(6687, facts));
		assertTrue(LowPotions.isLow(11523, facts));
		assertFalse(LowPotions.isLow(6685, facts));
		assertFalse(LowPotions.isLow(3024, facts));
		assertFalse(LowPotions.isLow(385, facts));
		assertFalse(LowPotions.isLow(2562, facts));
		assertFalse(LowPotions.isLow(-1, facts));
	}

	@Test
	public void l1_brewBelowFourIsLow()
	{
		assertEquals(new TreeSet<>(Arrays.asList(1, 2, 3)), LowPotions.slots(new int[]{6685, 6687, 6689, 6691}, facts));
	}

	@Test
	public void l2_fullDoseComesFromTheGroup()
	{
		assertEquals(new TreeSet<>(Collections.singletonList(1)), LowPotions.slots(new int[]{11521, 11523}, facts));
	}

	@Test
	public void l3_ringsAndAmuletsAreNotPotions()
	{
		assertTrue(LowPotions.slots(new int[]{2562, 1712}, facts).isEmpty());
	}

	@Test
	public void l4_fullFoodStacksAndEmptySlotsAreIgnored()
	{
		assertTrue(LowPotions.slots(new int[]{3024, 385, 2, -1}, facts).isEmpty());
	}

	@Test
	public void l5_emptyInventories()
	{
		final int[] empty = new int[28];
		Arrays.fill(empty, -1);
		assertTrue(LowPotions.slots(empty, facts).isEmpty());
		assertTrue(LowPotions.slots(new int[0], facts).isEmpty());
	}
}
