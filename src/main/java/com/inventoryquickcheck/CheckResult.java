package com.inventoryquickcheck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/** The outcome of a check: the inventory slots holding low potions and what is wrong, one entry per thing. */
public final class CheckResult
{
	/** What kind of wrong an entry is. */
	public enum Kind
	{
		MISSING,
		NOT_WORN,
		NOT_IN_POUCH
	}

	/**
	 * One thing wrong: its kind, the item to picture, how many are missing (1 unless a missing group), its name, the
	 * item ids that answer it and whether it is a dose group (the bank number picks the strongest of those).
	 */
	public static final class Entry
	{
		public final Kind kind;
		public final int itemId;
		public final int count;
		public final String name;
		public final Set<Integer> accepts;
		public final boolean dose;

		Entry(Kind kind, int itemId, int count, String name, Set<Integer> accepts, boolean dose)
		{
			this.accepts = Collections.unmodifiableSet(new TreeSet<>(accepts));
			this.dose = dose;
			this.kind = kind;
			this.itemId = itemId;
			this.count = count;
			this.name = name;
		}
	}

	private final SortedSet<Integer> lowSlots;
	private final List<Entry> entries;

	CheckResult(SortedSet<Integer> lowSlots, List<Entry> entries)
	{
		this.lowSlots = Collections.unmodifiableSortedSet(new TreeSet<>(lowSlots));
		this.entries = Collections.unmodifiableList(new ArrayList<>(entries));
	}

	/** Inventory slot indexes holding a potion with fewer doses than the setup wants. */
	public SortedSet<Integer> lowSlots()
	{
		return lowSlots;
	}

	/** Everything missing, not worn or not in the rune pouch, uncut: missing by first setup slot, worn by slot, runes by setup order. */
	public List<Entry> entries()
	{
		return entries;
	}

	/** Whether nothing is wrong: no entries and no low slots. */
	public boolean allGood()
	{
		return entries.isEmpty() && lowSlots.isEmpty();
	}
}
