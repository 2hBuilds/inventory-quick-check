package com.inventoryquickcheck;

/** Where the check button sits while the bank is open. */
public enum ButtonPlace
{
	ABOVE_INVENTORY("Above the inventory"),
	INSIDE_FIRST_SLOT("Inside the first slot"),
	ATTACK_TAB("Attack tab");

	private final String label;

	ButtonPlace(String label)
	{
		this.label = label;
	}

	/** The label RuneLite shows in the settings. */
	@Override
	public String toString()
	{
		return label;
	}
}
