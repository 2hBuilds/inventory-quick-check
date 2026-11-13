package com.inventoryquickcheck;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
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

	private static ShownResult result(boolean startedAtBank, boolean fromButton)
	{
		return ShownResult.noSetup("hint", Collections.<Integer, Integer>emptyMap(), 1000, startedAtBank, fromButton);
	}

	@Test
	public void t1_t2_aHotkeyCheckAtTheBankStaysUntilTheBankCloses()
	{
		assertFalse(ResultOverlay.over(result(true, false), 61000, 6, true));
		assertTrue(ResultOverlay.over(result(true, false), 2000, 6, false));
	}

	@Test
	public void t3_aHotkeyCheckAwayFromTheBankGoesAfterItsSeconds()
	{
		assertFalse(ResultOverlay.over(result(false, false), 6999, 6, false));
		assertTrue(ResultOverlay.over(result(false, false), 7000, 6, false));
		assertTrue(ResultOverlay.over(result(false, false), 7000, 6, true));
	}

	@Test
	public void t4_aButtonCheckStaysUntilTheBankCloses()
	{
		assertFalse(ResultOverlay.over(result(true, true), 61000, 6, true));
		assertTrue(ResultOverlay.over(result(true, true), 61000, 6, false));
	}
}
