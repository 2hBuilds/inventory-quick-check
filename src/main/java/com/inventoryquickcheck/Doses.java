package com.inventoryquickcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads the dose count a potion's name ends with, as in {@code Saradomin brew(3)}. */
public final class Doses
{
	private static final Pattern TRAILING = Pattern.compile("\\s*\\((\\d+)\\)\\s*$");

	private Doses()
	{
	}

	/** The number in a trailing {@code (n)}, spaces allowed before the bracket; -1 when there is none. */
	public static int dose(String name)
	{
		final Matcher matcher = TRAILING.matcher(name);
		return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
	}

	/** Whether {@code id} is a potion-like item: it has a dose and none of its variations has a dose above 4. */
	public static boolean isDoseItem(int id, ItemFacts facts)
	{
		if (dose(facts.name(id)) <= 0)
		{
			return false;
		}
		for (final int variation : facts.variations(id))
		{
			if (dose(facts.name(variation)) > 4)
			{
				return false;
			}
		}
		return true;
	}

	/** The name without its trailing {@code (n)} and the spaces before it. */
	public static String withoutDose(String name)
	{
		return TRAILING.matcher(name).replaceFirst("");
	}
}
