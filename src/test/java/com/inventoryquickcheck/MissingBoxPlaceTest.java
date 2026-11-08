package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class MissingBoxPlaceTest
{
	@Test
	public void n1_labelsInOrderAndTheDefaults()
	{
		final MissingBoxPlace[] all = MissingBoxPlace.values();
		assertEquals(5, all.length);
		assertEquals("Automatic", all[0].toString());
		assertEquals("Left of the inventory", all[1].toString());
		assertEquals("Right of the bank", all[2].toString());
		assertEquals("Where I drag it", all[3].toString());
		assertEquals("Hidden", all[4].toString());
		final InventoryQuickCheckConfig defaults = new InventoryQuickCheckConfig()
		{
		};
		assertEquals(MissingBoxPlace.AUTOMATIC, defaults.boxPlace());
		assertEquals(MissingBoxPlace.AUTOMATIC, defaults.boxPlaceBefore());
	}

	@Test
	public void n2_afterDragAndAfterReset()
	{
		assertEquals(MissingBoxPlace.WHERE_I_DRAG, MissingBoxPlace.afterDrag(MissingBoxPlace.AUTOMATIC));
		assertEquals(MissingBoxPlace.WHERE_I_DRAG, MissingBoxPlace.afterDrag(MissingBoxPlace.LEFT_OF_INVENTORY));
		assertEquals(MissingBoxPlace.WHERE_I_DRAG, MissingBoxPlace.afterDrag(MissingBoxPlace.RIGHT_OF_BANK));
		assertNull(MissingBoxPlace.afterDrag(MissingBoxPlace.WHERE_I_DRAG));
		assertNull(MissingBoxPlace.afterDrag(MissingBoxPlace.HIDDEN));
		assertEquals(MissingBoxPlace.RIGHT_OF_BANK,
			MissingBoxPlace.afterReset(MissingBoxPlace.WHERE_I_DRAG, MissingBoxPlace.RIGHT_OF_BANK));
		assertEquals(MissingBoxPlace.AUTOMATIC,
			MissingBoxPlace.afterReset(MissingBoxPlace.WHERE_I_DRAG, MissingBoxPlace.HIDDEN));
		assertEquals(MissingBoxPlace.AUTOMATIC, MissingBoxPlace.afterReset(MissingBoxPlace.WHERE_I_DRAG, null));
		assertNull(MissingBoxPlace.afterReset(MissingBoxPlace.LEFT_OF_INVENTORY, MissingBoxPlace.RIGHT_OF_BANK));
	}

	@Test
	public void n3_forLayout()
	{
		// The second argument is "the fixed layout is up" (L20: right of the bank on both resizable layouts).
		assertEquals(MissingBoxPlace.RIGHT_OF_BANK, MissingBoxPlace.forLayout(MissingBoxPlace.AUTOMATIC, false));
		assertEquals(MissingBoxPlace.RIGHT_OF_BANK, MissingBoxPlace.forLayout(MissingBoxPlace.WHERE_I_DRAG, false));
		assertEquals(MissingBoxPlace.LEFT_OF_INVENTORY, MissingBoxPlace.forLayout(MissingBoxPlace.AUTOMATIC, true));
		assertEquals(MissingBoxPlace.LEFT_OF_INVENTORY,
			MissingBoxPlace.forLayout(MissingBoxPlace.LEFT_OF_INVENTORY, false));
		assertEquals(MissingBoxPlace.HIDDEN, MissingBoxPlace.forLayout(MissingBoxPlace.HIDDEN, true));
	}
}
