package com.inventoryquickcheck;

/**
 * One item of a saved setup. Immutable. Choice: plain public final fields, because nothing in {@code src/main} reads
 * the quantity (a stackable item wants one whatever it holds) and an accessor only a test calls would break the
 * no-test-only-members rule.
 */
public final class SetupItem
{
	public final int id;
	public final int quantity;
	public final boolean fuzzy;

	public SetupItem(int id, int quantity, boolean fuzzy)
	{
		this.id = id;
		this.quantity = quantity;
		this.fuzzy = fuzzy;
	}
}
