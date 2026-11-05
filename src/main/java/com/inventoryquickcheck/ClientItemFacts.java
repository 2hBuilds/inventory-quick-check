package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;

/** Item facts from the client's item data. Client thread only. */
public final class ClientItemFacts implements ItemFacts
{
	private final ItemManager itemManager;
	private final Map<Integer, String> names = new HashMap<>();
	private final Map<Integer, Boolean> equipables = new HashMap<>();
	private final Map<Integer, Boolean> consumables = new HashMap<>();

	public ClientItemFacts(ItemManager itemManager)
	{
		this.itemManager = itemManager;
	}

	@Override
	public String name(int id)
	{
		String name = names.get(id);
		if (name == null)
		{
			name = itemManager.getItemComposition(id).getName();
			names.put(id, name);
		}
		return name;
	}

	@Override
	public int base(int id)
	{
		return ItemVariationMapping.map(id);
	}

	/**
	 * RuneLite keys the variation groups by their base id, so the group is looked up through {@link #base}: asked with
	 * Prayer potion(3) directly it answers only Prayer potion(3), and no potion would ever look low (the user,
	 * 2026-11-05: "still no flashing on the pots that are not full").
	 */
	@Override
	public Collection<Integer> variations(int id)
	{
		final Collection<Integer> out = new ArrayList<>(ItemVariationMapping.getVariations(ItemVariationMapping.map(id)));
		if (!out.contains(id))
		{
			out.add(id);
		}
		return out;
	}

	@Override
	public boolean stackable(int id)
	{
		return itemManager.getItemComposition(id).isStackable();
	}

	@Override
	public boolean equipable(int id)
	{
		return cached(equipables, id, "Wear", "Wield", "Equip");
	}

	@Override
	public boolean consumable(int id)
	{
		return cached(consumables, id, "Eat", "Drink");
	}

	private boolean cached(Map<Integer, Boolean> cache, int id, String... actions)
	{
		Boolean found = cache.get(id);
		if (found == null)
		{
			found = false;
			final String[] have = itemManager.getItemComposition(id).getInventoryActions();
			if (have != null)
			{
				for (final String action : have)
				{
					for (final String wanted : actions)
					{
						if (wanted.equals(action))
						{
							found = true;
						}
					}
				}
			}
			cache.put(id, found);
		}
		return found;
	}
}
