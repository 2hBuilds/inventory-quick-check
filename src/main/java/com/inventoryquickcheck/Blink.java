package com.inventoryquickcheck;

/** The blink schedule of the marks. */
public final class Blink
{
	/** One cycle: lit for {@link #LIT_MS}, then dark until the cycle ends; each dark-then-lit is one flash. */
	private static final long CYCLE_MS = 500;
	private static final long LIT_MS = 300;

	private Blink()
	{
	}

	/**
	 * Whether the marks are lit this long after the check. Lit 0-299, dark 300-499, and so on; after {@code count}
	 * cycles lit for good (1 flash: lit from 500). Steady is always lit; Nonstop never settles.
	 */
	public static boolean lit(long elapsedMs, PotionFlashes flashes)
	{
		final int count = flashes.count();
		if (elapsedMs < 0 || (count >= 0 && elapsedMs >= count * CYCLE_MS))
		{
			return true;
		}
		return elapsedMs % CYCLE_MS < LIT_MS;
	}
}
