package com.inventoryquickcheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Decides which bank item carries the number of each entry of the box. Pure; touches no client. */
public final class Withdraw
{
	private Withdraw()
	{
	}

	/**
	 * Bank item id to how many to take out. A missing entry numbers the bank item it accepts (the strongest dose for a
	 * dose group, else its own item, else the first accepted in bank order); a not-worn entry does the same with 1,
	 * unless the inventory already holds an accepted item; a rune not in the pouch numbers the rune with 1. An entry
	 * nothing in the bank answers adds nothing; entries on the same bank item add up.
	 */
	public static Map<Integer, Integer> counts(List<CheckResult.Entry> entries, int[] bankIds, int[] inventoryIds,
		ItemFacts facts)
	{
		final Map<Integer, Integer> counts = new HashMap<>();
		for (final CheckResult.Entry entry : entries)
		{
			if (entry.kind == CheckResult.Kind.NOT_WORN && holds(inventoryIds, entry))
			{
				continue;
			}
			final int id = choose(entry, bankIds, facts);
			if (id != -1)
			{
				counts.merge(id, entry.count, Integer::sum);
			}
		}
		return counts;
	}

	private static boolean holds(int[] ids, CheckResult.Entry entry)
	{
		for (final int id : ids)
		{
			if (id != -1 && entry.accepts.contains(id))
			{
				return true;
			}
		}
		return false;
	}

	/** The bank item an entry's number goes on, or -1 when the bank holds nothing it accepts. */
	private static int choose(CheckResult.Entry entry, int[] bankIds, ItemFacts facts)
	{
		int first = -1;
		int strongest = -1;
		int strongestDose = -1;
		boolean own = false;
		for (final int id : bankIds)
		{
			if (!entry.accepts.contains(id))
			{
				continue;
			}
			if (first == -1)
			{
				first = id;
			}
			if (id == entry.itemId)
			{
				own = true;
			}
			final int dose = Doses.dose(facts.name(id));
			if (dose > strongestDose)
			{
				strongestDose = dose;
				strongest = id;
			}
		}
		if (entry.dose)
		{
			return strongest;
		}
		return own ? entry.itemId : first;
	}
}
