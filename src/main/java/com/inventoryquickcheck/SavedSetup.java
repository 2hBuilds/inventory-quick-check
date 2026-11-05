package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A setup as Inventory Setups saved it, cut down to what the check reads. Immutable. Inventory has 28 entries and
 * equipment 14, {@code null} meaning empty; the rune pouch is the rune ids in the saved order, or {@code null} when the
 * saved setup has no rune pouch.
 */
public final class SavedSetup
{
	private final List<SetupItem> inventory;
	private final List<SetupItem> equipment;
	private final List<Integer> runePouch;

	public SavedSetup(List<SetupItem> inventory, List<SetupItem> equipment, List<Integer> runePouch)
	{
		this.inventory = Collections.unmodifiableList(new ArrayList<>(inventory));
		this.equipment = Collections.unmodifiableList(new ArrayList<>(equipment));
		this.runePouch = runePouch == null ? null : Collections.unmodifiableList(new ArrayList<>(runePouch));
	}

	public List<SetupItem> inventory()
	{
		return inventory;
	}

	public List<SetupItem> equipment()
	{
		return equipment;
	}

	public List<Integer> runePouch()
	{
		return runePouch;
	}
}
