package com.inventoryquickcheck;

import java.util.Collection;

/** What the check needs to know about an item id; the plugin answers from the client's item data. */
public interface ItemFacts
{
	String name(int id);

	/** The variation base of {@code id}; {@code id} itself when it has none. */
	int base(int id);

	/** Every id in {@code id}'s variation group, {@code id} included. */
	Collection<Integer> variations(int id);

	boolean stackable(int id);

	/** Whether {@code id} has a Wear, Wield or Equip inventory action. */
	boolean equipable(int id);

	/** Whether {@code id} has an Eat or Drink inventory action. */
	boolean consumable(int id);
}
