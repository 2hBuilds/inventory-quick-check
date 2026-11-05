package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** The rule engine: compares a saved setup with what is carried and says what is wrong. Pure; touches no client. */
public final class Checker
{
	private Checker()
	{
	}

	/** One thing the setup wants: a base (fuzzy and dose items) or an exact id, with how many places and their doses. */
	private static final class Group
	{
		final int firstId;
		final boolean dose;
		final boolean stackable;
		boolean byBase;
		int wanted;
		final List<Integer> wantedDoses = new ArrayList<>();
		int filled;
		final List<int[]> carriedDoses = new ArrayList<>();

		Group(int firstId, boolean dose, boolean stackable)
		{
			this.firstId = firstId;
			this.dose = dose;
			this.stackable = stackable;
		}
	}

	public static CheckResult check(SavedSetup setup, Carried carried, ItemFacts facts)
	{
		final TreeSet<Integer> lowSlots = new TreeSet<>();
		final List<CheckResult.Entry> entries = new ArrayList<>();

		// Inventory groups, order ignored. The map keeps insertion order, which is first-setup-slot order.
		final Map<String, Group> groups = new LinkedHashMap<>();
		for (final SetupItem item : setup.inventory())
		{
			if (item == null)
			{
				continue;
			}
			final boolean dose = Doses.isDoseItem(item.id, facts);
			final String key = dose || item.fuzzy ? "b:" + facts.base(item.id) : "e:" + item.id;
			Group group = groups.get(key);
			if (group == null)
			{
				group = new Group(item.id, dose, !dose && facts.stackable(item.id));
				group.byBase = dose || item.fuzzy;
				groups.put(key, group);
			}
			if (group.stackable)
			{
				group.wanted = 1;
			}
			else
			{
				group.wanted++;
				if (group.dose)
				{
					group.wantedDoses.add(Doses.dose(facts.name(item.id)));
				}
			}
		}

		// Fill from what is carried, in slot order.
		for (int slot = 0; slot < carried.inventoryIds.length; slot++)
		{
			final int id = carried.inventoryIds[slot];
			if (id == -1)
			{
				continue;
			}
			Group group = groups.get("e:" + id);
			if (group == null)
			{
				group = groups.get("b:" + facts.base(id));
			}
			if (group == null)
			{
				continue;
			}
			if (group.dose)
			{
				group.carriedDoses.add(new int[]{slot, Doses.dose(facts.name(id))});
			}
			else if (group.filled < group.wanted)
			{
				group.filled++;
			}
		}
		for (final Group group : groups.values())
		{
			if (!group.dose)
			{
				continue;
			}
			// Every carried potion below the strongest dose the setup wants is low, spares included (the user,
			// 2026-11-04: "mark all low").
			group.filled = Math.min(group.carriedDoses.size(), group.wantedDoses.size());
			final int full = Collections.max(group.wantedDoses);
			for (final int[] have : group.carriedDoses)
			{
				if (have[1] < full)
				{
					lowSlots.add(have[0]);
				}
			}
		}

		// A rune the setup wants loose counts when the carried pouch holds it.
		if (carried.pouchRunes != null)
		{
			for (final Group group : groups.values())
			{
				if (group.stackable && group.filled < group.wanted && carried.pouchRunes.contains(group.firstId))
				{
					group.filled = group.wanted;
				}
			}
		}

		// Missing, by first setup slot.
		for (final Group group : groups.values())
		{
			final int missing = group.wanted - group.filled;
			if (missing <= 0)
			{
				continue;
			}
			final String name = group.dose
				? Doses.withoutDose(facts.name(group.firstId)) : facts.name(group.firstId);
			final Set<Integer> accepts = group.byBase
				? new TreeSet<>(facts.variations(group.firstId)) : new TreeSet<>(Collections.singleton(group.firstId));
			entries.add(new CheckResult.Entry(CheckResult.Kind.MISSING, group.firstId, missing, name, accepts,
				group.dose));
		}

		// Worn.
		final List<SetupItem> equipment = setup.equipment();
		for (int slot = 0; slot < equipment.size(); slot++)
		{
			final SetupItem item = equipment.get(slot);
			if (item == null)
			{
				continue;
			}
			final int worn = slot < carried.wornIds.length ? carried.wornIds[slot] : -1;
			final boolean ok = worn != -1
				&& (item.fuzzy ? facts.base(worn) == facts.base(item.id) : worn == item.id);
			if (!ok)
			{
				final Set<Integer> accepts = item.fuzzy
					? new TreeSet<>(facts.variations(item.id)) : new TreeSet<>(Collections.singleton(item.id));
				accepts.add(item.id);
				entries.add(new CheckResult.Entry(CheckResult.Kind.NOT_WORN, item.id, 1, facts.name(item.id), accepts,
					false));
			}
		}

		// Rune pouch.
		if (setup.runePouch() != null && carried.pouchRunes != null)
		{
			for (final int rune : setup.runePouch())
			{
				if (!carried.pouchRunes.contains(rune) && !carriedLoose(carried, rune))
				{
					entries.add(new CheckResult.Entry(CheckResult.Kind.NOT_IN_POUCH, rune, 1, facts.name(rune),
					new TreeSet<>(Collections.singleton(rune)), false));
				}
			}
		}

		// Equipment, non-consumables, others, consumables; a stable sort keeps the order inside a group.
		final Map<CheckResult.Entry, Integer> groupOf = new IdentityHashMap<>();
		for (final CheckResult.Entry entry : entries)
		{
			groupOf.put(entry, sortGroup(entry, facts));
		}
		entries.sort((a, b) -> Integer.compare(groupOf.get(a), groupOf.get(b)));

		return new CheckResult(lowSlots, entries);
	}

	private static boolean carriedLoose(Carried carried, int id)
	{
		for (final int have : carried.inventoryIds)
		{
			if (have == id)
			{
				return true;
			}
		}
		return false;
	}

	/** 1 equipment, 2 non-consumables, 3 others (stackables), 4 consumables. */
	private static int sortGroup(CheckResult.Entry entry, ItemFacts facts)
	{
		if (entry.kind == CheckResult.Kind.NOT_WORN || facts.equipable(entry.itemId))
		{
			return 1;
		}
		if (facts.consumable(entry.itemId))
		{
			return 4;
		}
		return facts.stackable(entry.itemId) ? 3 : 2;
	}
}
