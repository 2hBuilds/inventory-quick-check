package com.inventoryquickcheck;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The potions that are not full, for a check with no setup to compare against. The user, 2026-11-05: "it didnt flash
 * the non full potions". A potion is any dose item you can Eat or Drink; its full dose is the highest dose in its
 * variation group, so a Zamorak mix is full at 2 and a Saradomin brew at 4. Pure: no client.
 */
public final class LowPotions
{
	private LowPotions()
	{
	}

	/** The inventory slots holding a potion below its full dose; empty slots ({@code -1}) are ignored. */
	public static SortedSet<Integer> slots(int[] inventoryIds, ItemFacts facts)
	{
		final SortedSet<Integer> slots = new TreeSet<>();
		for (int slot = 0; slot < inventoryIds.length; slot++)
		{
			if (isLow(inventoryIds[slot], facts))
			{
				slots.add(slot);
			}
		}
		return slots;
	}

	/**
	 * Whether an item is a potion below the highest dose of its variation group. Also used by {@link Checker} for a
	 * carried potion the setup does not list (the user, 2026-11-08: "its not outlining the potions while i have
	 * inventory setups active").
	 */
	public static boolean isLow(int id, ItemFacts facts)
	{
		if (!isPotion(id, facts))
		{
			return false;
		}
		int full = Doses.dose(facts.name(id));
		for (final int variation : facts.variations(id))
		{
			full = Math.max(full, Doses.dose(facts.name(variation)));
		}
		return Doses.dose(facts.name(id)) < full;
	}

	/** Every inventory slot holding a potion, full or not; empty slots ({@code -1}) are ignored. */
	public static SortedSet<Integer> potionSlots(int[] inventoryIds, ItemFacts facts)
	{
		final SortedSet<Integer> slots = new TreeSet<>();
		for (int slot = 0; slot < inventoryIds.length; slot++)
		{
			if (isPotion(inventoryIds[slot], facts))
			{
				slots.add(slot);
			}
		}
		return slots;
	}

	private static boolean isPotion(int id, ItemFacts facts)
	{
		return id != -1 && Doses.isDoseItem(id, facts) && facts.consumable(id);
	}
}
