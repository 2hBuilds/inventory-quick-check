package com.inventoryquickcheck;

/** Where the Missing box sits. */
public enum MissingBoxPlace
{
	AUTOMATIC("Automatic"),
	LEFT_OF_INVENTORY("Left of the inventory"),
	RIGHT_OF_BANK("Right of the bank"),
	WHERE_I_DRAG("Where I drag it"),
	HIDDEN("Hidden");

	private final String label;

	MissingBoxPlace(String label)
	{
		this.label = label;
	}

	/** The label RuneLite shows in the settings. */
	@Override
	public String toString()
	{
		return label;
	}

	/** The choice after a drag: Where I drag it for Automatic, Left and Right; else null (no change). */
	static MissingBoxPlace afterDrag(MissingBoxPlace now)
	{
		if (now == AUTOMATIC || now == LEFT_OF_INVENTORY || now == RIGHT_OF_BANK)
		{
			return WHERE_I_DRAG;
		}
		return null;
	}

	/**
	 * The choice after the dragged spot is reset: from Where I drag it, the choice from before when that was Automatic,
	 * Left or Right, else Automatic; null (no change) for any other current choice.
	 */
	static MissingBoxPlace afterReset(MissingBoxPlace now, MissingBoxPlace before)
	{
		if (now != WHERE_I_DRAG)
		{
			return null;
		}
		if (before == AUTOMATIC || before == LEFT_OF_INVENTORY || before == RIGHT_OF_BANK)
		{
			return before;
		}
		return AUTOMATIC;
	}

	/**
	 * The spot rule a choice uses on a layout: Automatic and Where I drag it (until dragged) are right of the bank, but
	 * left of the inventory on the fixed layout, where the bank leaves no room beside it (the user, 2026-11-08: "id like
	 * the default settings for fixed resizable layout to also the beside the bank one we are using for modern"); the
	 * rest as they are.
	 */
	static MissingBoxPlace forLayout(MissingBoxPlace place, boolean fixedLayout)
	{
		if (place == AUTOMATIC || place == WHERE_I_DRAG)
		{
			return fixedLayout ? LEFT_OF_INVENTORY : RIGHT_OF_BANK;
		}
		return place;
	}
}
