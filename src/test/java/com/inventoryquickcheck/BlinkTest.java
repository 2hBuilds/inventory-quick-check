package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BlinkTest
{
	@Test
	public void followsTheSchedule()
	{
		// Two flashes, the second staying lit from 500 ms (the user, 2026-11-05: "put that to 2").
		final long[] lit = {0, 299, 500, 799, 800, 999, 1000, 1499, 60000};
		final long[] dark = {300, 499};
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
