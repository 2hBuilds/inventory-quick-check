package com.inventoryquickcheck;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

/**
 * What the box and the marks show right now. Immutable: a row per thing wrong, or one message line, the low
 * inventory slots with the item id each held at the check, when the check ran (ms), whether the bank was open then
 * and whether the button ran it.
 */
public final class ShownResult
{
	/** One row of the box: the item, how many, and its picture (null while it loads). */
	public static final class Row
	{
		public final int itemId;
		public final int count;
		public final BufferedImage image;

		Row(int itemId, int count, BufferedImage image)
		{
			this.itemId = itemId;
			this.count = count;
			this.image = image;
		}
	}

	private final List<Row> rows;
	private final String message;
	private final Color messageColour;
	private final Map<Integer, Integer> lowSlots;
	private final Map<Integer, Integer> withdraw;
	private final long startMs;
	private final boolean startedAtBank;
	private final boolean fromButton;
	private final String hint;

	private ShownResult(List<Row> rows, String message, Color messageColour, Map<Integer, Integer> lowSlots,
		Map<Integer, Integer> withdraw, long startMs, boolean startedAtBank, boolean fromButton, String hint)
	{
		this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
		this.message = message;
		this.messageColour = messageColour;
		this.lowSlots = Collections.unmodifiableMap(new HashMap<>(lowSlots));
		this.withdraw = Collections.unmodifiableMap(new HashMap<>(withdraw));
		this.startMs = startMs;
		this.startedAtBank = startedAtBank;
		this.fromButton = fromButton;
		this.hint = hint;
	}

	/**
	 * A check with no setup to compare: one white line, Potions full or Potions only (when {@code lowSlots} holds a
	 * potion), no rows, marks only on the low potions in {@code lowSlots} (slot to item id), and the {@code hint} the
	 * button's hover text shows.
	 */
	public static ShownResult noSetup(String hint, Map<Integer, Integer> lowSlots, long startMs, boolean startedAtBank,
		boolean fromButton)
	{
		return new ShownResult(Collections.<Row>emptyList(), lowSlots.isEmpty() ? Texts.POTIONS_FULL : Texts.POTIONS_ONLY,
			Color.WHITE, lowSlots, Collections.<Integer, Integer>emptyMap(), startMs, startedAtBank, fromButton, hint);
	}

	/**
	 * A row per thing wrong, all of them (the box cuts to its layout), or All good in green. {@code images} is called
	 * once per row, on the client thread only.
	 */
	public static ShownResult of(CheckResult result, Carried carried, IntFunction<BufferedImage> images, long startMs,
		boolean startedAtBank, boolean fromButton,
		Map<Integer, Integer> withdraw)
	{
		final List<Row> rows = new ArrayList<>();
		for (final CheckResult.Entry entry : result.entries())
		{
			rows.add(new Row(entry.itemId, entry.count, images.apply(entry.itemId)));
		}
		final Map<Integer, Integer> slots = new HashMap<>();
		for (final int slot : result.lowSlots())
		{
			if (slot >= 0 && slot < carried.inventoryIds.length)
			{
				slots.put(slot, carried.inventoryIds[slot]);
			}
		}
		final boolean good = result.allGood();
		return new ShownResult(rows, good ? Texts.ALL_GOOD : null, good ? Color.GREEN : null, slots, withdraw, startMs,
			startedAtBank, fromButton, null);
	}

	public List<Row> rows()
	{
		return rows;
	}

	/** The one message line, or null when there are rows or nothing to say. */
	public String message()
	{
		return message;
	}

	public Color messageColour()
	{
		return messageColour;
	}

	/** Low inventory slot to the item id it held at the check. */
	public Map<Integer, Integer> lowSlots()
	{
		return lowSlots;
	}

	/** Bank item id to how many to take out, for the numbers drawn on the bank; empty when nothing answers. */
	public Map<Integer, Integer> withdraw()
	{
		return withdraw;
	}

	public long startMs()
	{
		return startMs;
	}

	public boolean startedAtBank()
	{
		return startedAtBank;
	}

	public boolean fromButton()
	{
		return fromButton;
	}

	/** The extra hover rows for the button after a no-setup check, or null for every other result. */
	public String hint()
	{
		return hint;
	}
}
