package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Item facts from a map, for the tests: the real ids the plan lists. No client, no files. */
final class FakeItemFacts implements ItemFacts
{
	private final Map<Integer, String> names = new HashMap<>();
	private final Map<Integer, Integer> bases = new HashMap<>();
	private final Map<Integer, List<Integer>> groups = new HashMap<>();
	private final Map<Integer, Boolean> stackables = new HashMap<>();
	private final Set<Integer> equipables = new HashSet<>(Arrays.asList(21295, 6570, 2552, 2562, 1712, 11978));
	private final Set<Integer> consumables = new HashSet<>(
		Arrays.asList(6685, 6687, 6689, 6691, 3024, 385, 11521, 11523));

	FakeItemFacts()
	{
		group(6685, "Saradomin brew(4)", 6687, "Saradomin brew(3)", 6689, "Saradomin brew(2)", 6691, "Saradomin brew(1)");
		single(3024, "Super restore(4)", false);
		single(385, "Shark", false);
		single(2, "Cannonball", true);
		group(2552, "Ring of dueling(8)", 2562, "Ring of dueling(3)");
		group(1712, "Amulet of glory(4)", 11978, "Amulet of glory(6)");
		group(11521, "Zamorak mix(2)", 11523, "Zamorak mix(1)");
		single(12791, "Rune pouch", false);
		single(565, "Blood rune", true);
		single(560, "Death rune", true);
		single(554, "Fire rune", true);
		single(566, "Soul rune", true);
		single(21295, "Infernal cape", false);
		single(6570, "Fire cape", false);
	}

	private void single(int id, String name, boolean stackable)
	{
		names.put(id, name);
		bases.put(id, id);
		groups.put(id, Collections.singletonList(id));
		stackables.put(id, stackable);
	}

	private void group(Object... idsAndNames)
	{
		final List<Integer> ids = new ArrayList<>();
		for (int i = 0; i < idsAndNames.length; i += 2)
		{
			ids.add((Integer) idsAndNames[i]);
		}
		for (int i = 0; i < idsAndNames.length; i += 2)
		{
			final int id = (Integer) idsAndNames[i];
			names.put(id, (String) idsAndNames[i + 1]);
			bases.put(id, ids.get(0));
			groups.put(id, ids);
			stackables.put(id, false);
		}
	}

	@Override
	public String name(int id)
	{
		return names.getOrDefault(id, "Item " + id);
	}

	@Override
	public int base(int id)
	{
		return bases.getOrDefault(id, id);
	}

	@Override
	public Collection<Integer> variations(int id)
	{
		return groups.getOrDefault(id, Collections.singletonList(id));
	}

	@Override
	public boolean stackable(int id)
	{
		return stackables.getOrDefault(id, false);
	}

	@Override
	public boolean equipable(int id)
	{
		return equipables.contains(id);
	}

	@Override
	public boolean consumable(int id)
	{
		return consumables.contains(id);
	}
}
