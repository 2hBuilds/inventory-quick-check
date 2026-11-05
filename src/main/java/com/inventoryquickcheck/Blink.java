package com.inventoryquickcheck;

/** The blink schedule of the marks. */
public final class Blink
{
	/**
	 * Two flashes, the second staying lit (the user, 2026-11-05: "they do 3 flashes tho, put that to 2"; the schedule
	 * before lit twice and then stayed lit, which reads as three).
	 */
	private static final long BLINKING_MS = 500;
	private static final long CYCLE_MS = 500;
	private static final long LIT_MS = 300;

	private Blink()
	{
	}

	/** Whether the marks are lit this long after the check: lit 0-299, dark 300-499; lit from 500. */
	public static boolean lit(long elapsedMs)
	{
		if (elapsedMs < 0 || elapsedMs >= BLINKING_MS)
		{
			return true;
		}
		return elapsedMs % CYCLE_MS < LIT_MS;
	}
}
