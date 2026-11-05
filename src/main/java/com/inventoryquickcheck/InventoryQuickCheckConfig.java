package com.inventoryquickcheck;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

/**
 * The plugin's settings, shown in RuneLite's own config panel (there is no sidebar). Flat on purpose: every item is
 * declared here and this interface extends {@link Config} alone, because RuneLite stores defaults only for the
 * methods declared on the interface it is given. The group and the keys are frozen at first release; renaming one
 * silently discards every player's setting.
 */
@ConfigGroup(InventoryQuickCheckConfig.GROUP)
public interface InventoryQuickCheckConfig extends Config
{
	String GROUP = "inventoryquickcheck";

	@ConfigSection(
		name = "Needs the Inventory Setups plugin",
		description = "These settings work with the setup open in Inventory Setups.",
		position = 0
	)
	String SECTION = "needsInventorySetups";

	@ConfigItem(
		keyName = "showButton",
		name = "Show the button",
		description = "Shows the check button on the attack tab while the bank is open.",
		position = 1,
		section = SECTION
	)
	default boolean showButton()
	{
		return true;
	}

	@ConfigItem(
		keyName = "hotkey",
		name = "Hotkey",
		description = "Runs the check anywhere.",
		position = 2,
		section = SECTION
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "markColour",
		name = "Mark colour",
		description = "Colour of the marks on low potions.",
		position = 3,
		section = SECTION
	)
	default Color markColour()
	{
		return new Color(255, 0, 0);
	}

	@Range(min = 1, max = 3)
	@ConfigItem(
		keyName = "markThickness",
		name = "Mark thickness",
		description = "How thick the outline on low potions is, in pixels.",
		position = 4,
		section = SECTION
	)
	default int markThickness()
	{
		return 2;
	}

	@Range(min = 2, max = 30)
	@ConfigItem(
		keyName = "secondsToShow",
		name = "Seconds to show",
		description = "How long the result stays up after the hotkey. After the button it stays until the bank closes.",
		position = 5,
		section = SECTION
	)
	default int secondsToShow()
	{
		return 6;
	}

	@ConfigItem(
		keyName = "boxLayout",
		name = "Box layout",
		description = "How the list of missing things wraps when it is long.",
		position = 6,
		section = SECTION
	)
	default BoxLayout boxLayout()
	{
		return BoxLayout.ONE_COLUMN;
	}

	@ConfigItem(
		keyName = "showMissingBox",
		name = "Show the missing box",
		description = "Shows the box of missing things. All good and the setup messages show either way.",
		position = 7,
		section = SECTION
	)
	default boolean showMissingBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "numberBankItems",
		name = "Number the bank items",
		description = "Shows on each bank item how many to take out.",
		position = 8,
		section = SECTION
	)
	default boolean numberBankItems()
	{
		return true;
	}
}
