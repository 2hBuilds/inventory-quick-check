package com.inventoryquickcheck;

/** The blink schedule of the marks. */
public final class Blink
{
	/** Two blinks, then lit for good (the user, 2026-11-05: "perhaps just 2 blinks is enough"). */
	private static final long BLINKING_MS = 1000;
	private static final long CYCLE_MS = 500;
	private static final long LIT_MS = 300;

	private Blink()
	{
	}

	/** Whether the marks are lit this long after the check: lit 0-299, dark 300-499, lit 500-799, dark 800-999; lit from 1000. */
	public static boolean lit(long elapsedMs)
	{
		if (elapsedMs < 0 || elapsedMs >= BLINKING_MS)
		{
			return true;
		}
		return elapsedMs % CYCLE_MS < LIT_MS;
	}
}
