package com.inventoryquickcheck;

/** The fixed words the plugin shows. */
public final class Texts
{
	/** A check with no setup to compare, and at least one potion outlined. */
	public static final String POTIONS_ONLY = "Potions only";
	/** A check with no setup to compare, and no low potion. */
	public static final String POTIONS_FULL = "Potions full";
	/** The "!" hover text after a check with no setup open (the user, 2026-11-05); the break splits the tooltip rows. */
	public static final String HINT_OPEN_SETUP = "No setup open: click View setup</br>in the Inventory Setups plugin";
	/** The "!" hover text after a check with Inventory Setups off or missing (the two cannot be told apart). */
	public static final String HINT_TURN_ON = "For the full check, install or turn on</br>the Inventory Setups plugin";
	public static final String CHAT_NEEDS = "2h Inventory Setups Quick Check needs the Inventory Setups plugin.";
	public static final String ALL_GOOD = "All good";
	public static final String MISSING_TITLE = "Missing";
	/**
	 * The chat line when a drag switches Box place: RuneLite's settings panel does not redraw when a plugin changes a
	 * setting, so the switch is said where the player is looking.
	 */
	public static final String BOX_DRAGGED = "Box place is now Where I drag it. Alt+right-click the box to put it back.";
	/** The start of the chat line when Alt+right-click switches Box place back; the choice's name and "." follow. */
	public static final String BOX_BACK = "Box place is back to ";

	private Texts()
	{
	}
}
