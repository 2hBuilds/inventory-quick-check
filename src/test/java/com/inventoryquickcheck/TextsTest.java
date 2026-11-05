package com.inventoryquickcheck;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TextsTest
{
	@Test
	public void theThreeTextsAreExact()
	{
		// The user, 2026-11-05: install-or-turn-on, and name the plugin to open the setup in.
		assertEquals("Potions only", Texts.POTIONS_ONLY);
		assertEquals("Potions full", Texts.POTIONS_FULL);
		assertEquals("No setup open: click View setup</br>in the Inventory Setups plugin", Texts.HINT_OPEN_SETUP);
		assertEquals("For the full check, install or turn on</br>the Inventory Setups plugin", Texts.HINT_TURN_ON);
		assertEquals("2h Inventory Setups Quick Check needs the Inventory Setups plugin.", Texts.CHAT_NEEDS);
		assertEquals("All good", Texts.ALL_GOOD);
		assertEquals("Missing", Texts.MISSING_TITLE);
	}
}
