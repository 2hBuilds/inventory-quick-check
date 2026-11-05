package com.inventoryquickcheck;

import com.google.gson.Gson;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class SavedSetupParserTest
{
	private static final String INV = "\"inv\":[{\"id\":6685,\"f\":true},{\"id\":2,\"q\":216},null,{\"id\":-1}]";
	private static final String EQ = "\"eq\":[null,{\"id\":21295,\"sc\":3}]";
	private static final String RP = "\"rp\":[{\"id\":565,\"q\":1000},{\"id\":560,\"q\":500}]";

	private final Gson gson = new Gson();

	@Test
	public void parsesTheRealShape()
	{
		final SavedSetup setup = SavedSetupParser.parse(gson, "{\"name\":\"x\"," + INV + "," + EQ + "," + RP + "}");
		assertNotNull(setup);
		assertEquals(28, setup.inventory().size());
		assertEquals(14, setup.equipment().size());
		assertEquals(6685, setup.inventory().get(0).id);
		assertTrue(setup.inventory().get(0).fuzzy);
		assertEquals(1, setup.inventory().get(0).quantity);
		assertEquals(2, setup.inventory().get(1).id);
		assertFalse(setup.inventory().get(1).fuzzy);
		assertEquals(216, setup.inventory().get(1).quantity);
		assertNull(setup.inventory().get(2));
		assertNull(setup.inventory().get(3));
		assertEquals(21295, setup.equipment().get(1).id);
		assertEquals(Arrays.asList(565, 560), setup.runePouch());
	}

	@Test
	public void noPouchGivesNull()
	{
		final SavedSetup setup = SavedSetupParser.parse(gson, "{" + INV + "," + EQ + "}");
		assertNotNull(setup);
		assertNull(setup.runePouch());
	}

	@Test
	public void badJsonGivesNull()
	{
		assertNull(SavedSetupParser.parse(gson, "not json"));
		assertNull(SavedSetupParser.parse(gson, null));
		assertNull(SavedSetupParser.parse(gson, ""));
		assertNull(SavedSetupParser.parse(gson, "{" + EQ + "}"));
	}
}
