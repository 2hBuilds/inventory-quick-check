package com.inventoryquickcheck;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the JSON Inventory Setups stores for one setup ({@code inv}, {@code eq}, {@code rp}). The Gson comes in from
 * the caller. Never throws: anything unreadable gives {@code null}.
 */
public final class SavedSetupParser
{
	private static final int INVENTORY_SLOTS = 28;
	private static final int EQUIPMENT_SLOTS = 14;

	private SavedSetupParser()
	{
	}

	/** The setup in {@code json}, or {@code null} for bad JSON, a null or empty string, or a missing {@code inv}. */
	public static SavedSetup parse(Gson gson, String json)
	{
		if (json == null || json.isEmpty())
		{
			return null;
		}
		try
		{
			final JsonObject root = gson.fromJson(json, JsonObject.class);
			if (root == null || !root.has("inv") || !root.get("inv").isJsonArray())
			{
				return null;
			}
			final List<SetupItem> inventory = items(root.getAsJsonArray("inv"), INVENTORY_SLOTS);
			final JsonArray eq = root.has("eq") && root.get("eq").isJsonArray()
				? root.getAsJsonArray("eq") : new JsonArray();
			final List<SetupItem> equipment = items(eq, EQUIPMENT_SLOTS);
			List<Integer> pouch = null;
			if (root.has("rp") && root.get("rp").isJsonArray())
			{
				pouch = new ArrayList<>();
				for (final JsonElement element : root.getAsJsonArray("rp"))
				{
					final SetupItem item = item(element);
					if (item != null)
					{
						pouch.add(item.id);
					}
				}
			}
			return new SavedSetup(inventory, equipment, pouch);
		}
		catch (RuntimeException e)
		{
			return null;
		}
	}

	private static List<SetupItem> items(JsonArray array, int size)
	{
		final List<SetupItem> out = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			out.add(i < array.size() ? item(array.get(i)) : null);
		}
		return out;
	}

	private static SetupItem item(JsonElement element)
	{
		if (element == null || !element.isJsonObject())
		{
			return null;
		}
		final JsonObject object = element.getAsJsonObject();
		if (!object.has("id"))
		{
			return null;
		}
		final int id = object.get("id").getAsInt();
		if (id == -1)
		{
			return null;
		}
		final int quantity = object.has("q") ? object.get("q").getAsInt() : 1;
		final boolean fuzzy = object.has("f") && object.get("f").getAsBoolean();
		return new SetupItem(id, quantity, fuzzy);
	}
}
