package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BlinkTest
{
	@Test
	public void followsTheSchedule()
	{
		// Two blinks (the user, 2026-11-05), then lit for good from 1000 ms.
		final long[] lit = {0, 299, 500, 799, 1000, 1299, 1300, 1499, 60000};
		final long[] dark = {300, 499, 800, 999};
		for (final long ms : lit)
		{
			assertEquals("lit at " + ms, true, Blink.lit(ms));
		}
		for (final long ms : dark)
		{
			assertEquals("dark at " + ms, false, Blink.lit(ms));
		}
	}
}
