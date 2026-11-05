package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TextsTest
{
	@Test
	public void theThreeTextsAreExact()
	{
		// The user, 2026-11-05: install-or-turn-on, and name the plugin to open the setup in.
		assertEquals("Needs the Inventory Setups plugin: install or turn it on", Texts.TURN_ON);
		assertEquals("Click View setup in the Inventory Setups plugin (bank filter on)", Texts.OPEN_SETUP);
		assertEquals("2h Inventory Setups Quick Check needs the Inventory Setups plugin.", Texts.CHAT_NEEDS);
		assertEquals("All good", Texts.ALL_GOOD);
		assertEquals("Missing", Texts.MISSING_TITLE);
	}
}
