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
		description = "Shows the check button while the bank is open.",
		position = 1,
		section = SECTION
	)
	default boolean showButton()
	{
		return true;
	}

	@ConfigItem(
		keyName = "buttonPlace",
		name = "Button place",
		description = "Where the check button sits while the bank is open.",
		position = 2,
		section = SECTION
	)
	default ButtonPlace buttonPlace()
	{
		return ButtonPlace.ABOVE_INVENTORY;
	}

	@ConfigItem(
		keyName = "buttonLook",
		name = "Button look",
		description = "How the check button looks. Each choice shows its picture.",
		position = 3,
		section = SECTION
	)
	default ButtonLook buttonLook()
	{
		return ButtonLook.GOLD_RING_CHECK;
	}

	@ConfigItem(
		keyName = "hotkey",
		name = "Hotkey",
		description = "Runs the check anywhere.",
		position = 4,
		section = SECTION
	)
	default Keybind hotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "markColour",
		name = "Outline colour",
		description = "The colour of the outline on potions. For a live preview, Shift+right-click the button and choose Outline colour.",
		position = 5,
		section = SECTION
	)
	default Color markColour()
	{
		return new Color(255, 0, 0);
	}

	@Range(min = 1, max = 3)
	@ConfigItem(
		keyName = "markThickness",
		name = "Outline thickness",
		description = "How thick the outline on potions is, in pixels.",
		position = 6,
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
		position = 7,
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
		position = 8,
		section = SECTION
	)
	default BoxLayout boxLayout()
	{
		return BoxLayout.ONE_COLUMN;
	}

	@ConfigItem(
		keyName = "boxPlace",
		name = "Box place",
		description = "Where the Missing box sits. Automatic is right of the bank (left of the inventory on the fixed "
			+ "layout). Dragging it (Alt+drag) switches this to Where I drag it; Alt+right-click puts it back.",
		position = 9,
		section = SECTION
	)
	default MissingBoxPlace boxPlace()
	{
		return MissingBoxPlace.AUTOMATIC;
	}

	@ConfigItem(
		keyName = "numberBankItems",
		name = "Number the bank items",
		description = "Shows on each bank item how many to take out.",
		position = 10,
		section = SECTION
	)
	default boolean numberBankItems()
	{
		return true;
	}

	/** The Box place choice from before a drag switched it to Where I drag it; the plugin keeps it, never shown. */
	@ConfigItem(
		keyName = "boxPlaceBefore",
		name = "Box place before the drag",
		description = "The Box place choice to go back to when the dragged spot is reset.",
		hidden = true,
		position = 11,
		section = SECTION
	)
	default MissingBoxPlace boxPlaceBefore()
	{
		return MissingBoxPlace.AUTOMATIC;
	}
}
