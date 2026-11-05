package com.inventoryquickcheck;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import net.runelite.client.events.PluginMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SetupSourceTest
{
	private static final String COLI_KEY = "setupsV3_198dafc9235845019f16c9e7aa6bf30e";

	private final Gson gson = new Gson();

	@Test
	public void noAnswerIsNotRunning()
	{
		final SetupSource source = new SetupSource(message -> { }, (group, key) -> null, gson);
		final SetupAnswer answer = source.read();
		assertEquals(SetupAnswer.State.NOT_RUNNING, answer.state());
		assertNull(answer.name());
		assertNull(answer.setup());
	}

	@Test
	public void emptyNameIsNoSetup()
	{
		final SetupSource source = new SetupSource(message -> message.getData().put("activeSetup", ""),
			(group, key) -> null, gson);
		assertEquals(SetupAnswer.State.NO_SETUP, source.read().state());
	}

	@Test
	public void aNameWithSavedJsonIsReadFromTheConfig()
	{
		final Map<String, String> asked = new HashMap<>();
		final SetupSource source = new SetupSource(message -> message.getData().put("activeSetup", "Coli"),
			(group, key) ->
			{
				asked.put(group, key);
				return COLI_KEY.equals(key) ? "{\"inv\":[{\"id\":385}],\"eq\":[{\"id\":21295}]}" : null;
			}, gson);
		final SetupAnswer answer = source.read();
		assertEquals(SetupAnswer.State.READY, answer.state());
		assertEquals("Coli", answer.name());
		assertEquals(COLI_KEY, asked.get("inventorysetups"));
		assertEquals(385, answer.setup().inventory().get(0).id);
		assertEquals(21295, answer.setup().equipment().get(0).id);
	}

	@Test
	public void aNameWithoutJsonIsBuiltFromTheIdLists()
	{
		final SetupSource source = new SetupSource(message ->
		{
			final Map<String, Object> data = message.getData();
			data.put("activeSetup", "Coli");
			final List<Integer> inventory = ids(28);
			inventory.set(0, 6685);
			inventory.set(5, 385);
			final List<Integer> equipment = ids(14);
			equipment.set(3, 21295);
			data.put("inventoryItemIds", inventory);
			data.put("equipmentItemIds", equipment);
		}, (group, key) -> null, gson);
		final SavedSetup setup = source.read().setup();
		assertNotNull(setup);
		assertEquals(28, setup.inventory().size());
		assertEquals(14, setup.equipment().size());
		assertEquals(6685, setup.inventory().get(0).id);
		assertFalse(setup.inventory().get(0).fuzzy);
		assertEquals(1, setup.inventory().get(0).quantity);
		assertNull(setup.inventory().get(1));
		assertEquals(385, setup.inventory().get(5).id);
		assertEquals(21295, setup.equipment().get(3).id);
		assertNull(setup.runePouch());
	}

	@Test
	public void postsTheRightMessageWithEmptyMutableCollections()
	{
		final AtomicReference<PluginMessage> posted = new AtomicReference<>();
		new SetupSource(posted::set, (group, key) -> null, gson).read();
		final PluginMessage message = posted.get();
		assertEquals("inventory-setups", message.getNamespace());
		assertEquals("get-active-setup-contents", message.getName());
		for (final String key : new String[]{"equipmentItemIds", "inventoryItemIds", "additionalItemIds"})
		{
			final Object value = message.getData().get(key);
			assertTrue(key, value instanceof List);
			@SuppressWarnings("unchecked")
			final List<Integer> list = (List<Integer>) value;
			assertTrue(key, list.isEmpty());
			list.add(1);
			assertEquals(1, list.size());
		}
	}

	private static List<Integer> ids(int size)
	{
		final List<Integer> out = new ArrayList<>();
		for (int i = 0; i < size; i++)
		{
			out.add(-1);
		}
		return out;
	}
}
