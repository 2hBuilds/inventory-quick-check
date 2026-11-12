package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/** B1-B5: the blink schedule for each Potion flashes choice; a flash is one off-then-on (the user, 2026-11-11). */
public class BlinkTest
{
	private static void check(PotionFlashes flashes, long[] lit, long[] dark)
	{
		for (final long ms : lit)
		{
			assertEquals(flashes + " lit at " + ms, true, Blink.lit(ms, flashes));
		}
		for (final long ms : dark)
		{
			assertEquals(flashes + " dark at " + ms, false, Blink.lit(ms, flashes));
		}
	}

	@Test
	public void b1_oneFlashKeepsTodaysSchedule()
	{
		check(PotionFlashes.ONE, new long[]{0, 299, 500, 799, 60000}, new long[]{300, 499});
	}

	@Test
	public void b2_steadyIsAlwaysLit()
	{
		check(PotionFlashes.STEADY, new long[]{0, 300, 450, 60000}, new long[]{});
	}

	@Test
	public void b3_twoFlashes()
	{
		check(PotionFlashes.TWO, new long[]{500, 799, 1000, 60000}, new long[]{300, 499, 800, 999});
	}

	@Test
	public void b4_fiveFlashes()
	{
		check(PotionFlashes.FIVE, new long[]{2500, 60000}, new long[]{2300});
	}

	@Test
	public void b5_keepFlashingNeverStopsAndNegativeIsLit()
	{
		check(PotionFlashes.KEEP_FLASHING, new long[]{60000}, new long[]{60300});
		for (final PotionFlashes flashes : PotionFlashes.values())
		{
			assertEquals(flashes.name(), true, Blink.lit(-1, flashes));
		}
	}
}
