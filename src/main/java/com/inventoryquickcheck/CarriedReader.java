package com.inventoryquickcheck;

import java.util.HashSet;
import java.util.Set;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.EnumID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;

/** Reads what the player carries from the client. Client thread only. */
public final class CarriedReader
{
	private static final int INVENTORY_SLOTS = 28;
	private static final int WORN_SLOTS = 14;

	private CarriedReader()
	{
	}

	public static Carried read(Client client)
	{
		final int[] ids = new int[INVENTORY_SLOTS];
		final int[] amounts = new int[INVENTORY_SLOTS];
		final int[] worn = new int[WORN_SLOTS];
		fill(client.getItemContainer(InventoryID.INV), ids, amounts);
		fill(client.getItemContainer(InventoryID.WORN), worn, new int[WORN_SLOTS]);

		boolean pouch = false;
		for (final int id : ids)
		{
			pouch |= id == ItemID.BH_RUNE_POUCH || id == ItemID.BH_RUNE_POUCH_TROUVER
				|| id == ItemID.DIVINE_RUNE_POUCH || id == ItemID.DIVINE_RUNE_POUCH_TROUVER;
		}
		Set<Integer> runes = null;
		if (pouch)
		{
			runes = new HashSet<>();
			final EnumComposition table = client.getEnum(EnumID.RUNEPOUCH_RUNE);
			final int[] types = {
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_1), client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_2),
				client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_3), client.getVarbitValue(VarbitID.RUNE_POUCH_TYPE_4)};
			for (final int type : types)
			{
				if (type > 0)
				{
					runes.add(table.getIntValue(type));
				}
			}
		}
		return new Carried(ids, amounts, worn, runes);
	}

	private static void fill(ItemContainer container, int[] ids, int[] amounts)
	{
		for (int i = 0; i < ids.length; i++)
		{
			ids[i] = -1;
		}
		if (container == null)
		{
			return;
		}
		final Item[] items = container.getItems();
		for (int i = 0; i < ids.length && i < items.length; i++)
		{
			if (items[i] != null && items[i].getId() != -1)
			{
				ids[i] = items[i].getId();
				amounts[i] = items[i].getQuantity();
			}
		}
	}
}
