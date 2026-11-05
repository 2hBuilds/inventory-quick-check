package com.inventoryquickcheck;

import java.util.Set;

/**
 * What the player carries, read by the caller: 28 inventory ids and amounts and 14 worn ids ({@code -1} empty), and
 * the rune ids in the rune pouch ({@code null} when no rune pouch is carried). Immutable. Choice: public final fields,
 * the arrays copied in, so no accessor only a test would call.
 */
public final class Carried
{
	public final int[] inventoryIds;
	public final int[] inventoryAmounts;
	public final int[] wornIds;
	public final Set<Integer> pouchRunes;

	public Carried(int[] inventoryIds, int[] inventoryAmounts, int[] wornIds, Set<Integer> pouchRunes)
	{
		this.inventoryIds = inventoryIds.clone();
		this.inventoryAmounts = inventoryAmounts.clone();
		this.wornIds = wornIds.clone();
		this.pouchRunes = pouchRunes;
	}
}
