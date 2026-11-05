package com.inventoryquickcheck;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;

/** Reads which items the bank holds. Client thread only. */
public final class BankReader
{
	private BankReader()
	{
	}

	/** The ids of the bank's items with quantity above 0 (placeholders left out), in bank order; empty when not loaded. */
	public static int[] read(Client client)
	{
		final ItemContainer container = client.getItemContainer(InventoryID.BANK);
		if (container == null)
		{
			return new int[0];
		}
		final Item[] items = container.getItems();
		final int[] ids = new int[items.length];
		int count = 0;
		for (final Item item : items)
		{
			if (item != null && item.getId() != -1 && item.getQuantity() > 0)
			{
				ids[count++] = item.getId();
			}
		}
		return java.util.Arrays.copyOf(ids, count);
	}
}
