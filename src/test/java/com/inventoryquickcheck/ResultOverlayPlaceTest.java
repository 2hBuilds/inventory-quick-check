package com.inventoryquickcheck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResultOverlayPlaceTest
{
	@Test
	public void n6_everyChoiceButHiddenIsMovable()
	{
		assertTrue(ResultOverlay.movable(MissingBoxPlace.AUTOMATIC));
		assertTrue(ResultOverlay.movable(MissingBoxPlace.LEFT_OF_INVENTORY));
		assertTrue(ResultOverlay.movable(MissingBoxPlace.RIGHT_OF_BANK));
		assertTrue(ResultOverlay.movable(MissingBoxPlace.WHERE_I_DRAG));
		assertFalse(ResultOverlay.movable(MissingBoxPlace.HIDDEN));
	}

	@Test
	public void h1_withTheBankClosedTheBoxShowsOnlyWhenAskedFor()
	{
		assertTrue(ResultOverlay.showsBox(true, false));
		assertTrue(ResultOverlay.showsBox(true, true));
		assertTrue(ResultOverlay.showsBox(false, true));
		assertFalse(ResultOverlay.showsBox(false, false));
	}
}
