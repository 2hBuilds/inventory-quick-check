package com.inventoryquickcheck;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.runelite.client.events.PluginMessage;

/**
 * Asks Inventory Setups for the setup that is open, with a plugin message, and reads its saved copy from its config.
 * Call {@link #read()} on the client thread. Choice: when the saved JSON cannot be read, the setup is built from the
 * id lists the answer carries (fuzzy off, quantity 1, no rune pouch).
 */
public final class SetupSource
{
	private static final int INVENTORY_SLOTS = 28;
	private static final int EQUIPMENT_SLOTS = 14;

	private final Consumer<PluginMessage> poster;
	private final BiFunction<String, String, String> configReader;
	private final Gson gson;

	public SetupSource(Consumer<PluginMessage> poster, BiFunction<String, String, String> configReader, Gson gson)
	{
		this.poster = poster;
		this.configReader = configReader;
		this.gson = gson;
	}

	public SetupAnswer read()
	{
		final Map<String, Object> data = new HashMap<>();
		data.put("equipmentItemIds", new ArrayList<Integer>());
		data.put("inventoryItemIds", new ArrayList<Integer>());
		data.put("additionalItemIds", new ArrayList<Integer>());
		poster.accept(new PluginMessage("inventory-setups", "get-active-setup-contents", data));

		final Object active = data.get("activeSetup");
		if (active == null)
		{
			return new SetupAnswer(SetupAnswer.State.NOT_RUNNING, null, null);
		}
		final String name = active.toString();
		if (name.isEmpty())
		{
			return new SetupAnswer(SetupAnswer.State.NO_SETUP, null, null);
		}
		SavedSetup setup = SavedSetupParser.parse(gson, configReader.apply(SetupKey.GROUP, SetupKey.of(name)));
		if (setup == null)
		{
			setup = new SavedSetup(items(data.get("inventoryItemIds"), INVENTORY_SLOTS),
				items(data.get("equipmentItemIds"), EQUIPMENT_SLOTS), null);
		}
		return new SetupAnswer(SetupAnswer.State.READY, name, setup);
	}

	private static List<SetupItem> items(Object ids, int size)
	{
		final List<SetupItem> out = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			int id = -1;
			if (ids instanceof List && i < ((List<?>) ids).size())
			{
				final Object value = ((List<?>) ids).get(i);
				if (value instanceof Number)
				{
					id = ((Number) value).intValue();
				}
			}
			out.add(id == -1 ? null : new SetupItem(id, 1, false));
		}
		return out;
	}
}
