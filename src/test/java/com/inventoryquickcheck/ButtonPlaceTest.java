package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ButtonPlaceTest
{
	@Test
	public void b9_labels()
	{
		assertEquals("Above the inventory", ButtonPlace.ABOVE_INVENTORY.toString());
		assertEquals("Inside the first slot", ButtonPlace.INSIDE_FIRST_SLOT.toString());
		assertEquals("Attack tab", ButtonPlace.ATTACK_TAB.toString());
	}

	@Test
	public void f1_defaultIsInsideTheFirstSlot()
	{
		assertEquals(ButtonPlace.INSIDE_FIRST_SLOT, new InventoryQuickCheckConfig()
		{
		}.buttonPlace());
	}
}
