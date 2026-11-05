package com.inventoryquickcheck;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class WithdrawTest
{
	private static final int[] NONE = new int[0];

	private final ItemFacts facts = new FakeItemFacts();

	private static CheckResult.Entry brews(int count)
	{
		return new CheckResult.Entry(CheckResult.Kind.MISSING, 6685, count, "Saradomin brew",
			new TreeSet<>(Arrays.asList(6685, 6687, 6689, 6691)), true);
	}

	private Map<Integer, Integer> counts(List<CheckResult.Entry> entries, int[] bank, int[] inventory)
	{
		return Withdraw.counts(entries, bank, inventory, facts);
	}

	@Test
	public void t31a_aDoseGroupTakesTheStrongestDoseInTheBank()
	{
		assertEquals(Collections.singletonMap(6685, 3),
			counts(Collections.singletonList(brews(3)), new int[]{6689, 6685}, NONE));
	}

	@Test
	public void t31b_withoutTheFullDoseTheNextStrongestIsUsed()
	{
		assertEquals(Collections.singletonMap(6689, 3),
			counts(Collections.singletonList(brews(3)), new int[]{6691, 6689}, NONE));
	}

	@Test
	public void t31c_aWornItemAlreadyCarriedNeedsOnlyWearing()
	{
		final CheckResult.Entry cape = new CheckResult.Entry(CheckResult.Kind.NOT_WORN, 21295, 1, "Infernal cape",
			new TreeSet<>(Collections.singletonList(21295)), false);
		final List<CheckResult.Entry> entries = Collections.singletonList(cape);
		assertEquals(Collections.emptyMap(), counts(entries, new int[]{21295}, new int[]{-1, 21295}));
		assertEquals(Collections.singletonMap(21295, 1), counts(entries, new int[]{21295}, NONE));
	}

	@Test
	public void t31d_aFuzzyEntryTakesWhicheverVariationTheBankHas()
	{
		final CheckResult.Entry ring = new CheckResult.Entry(CheckResult.Kind.MISSING, 2552, 1, "Ring of dueling(8)",
			new TreeSet<>(Arrays.asList(2552, 2562)), false);
		assertEquals(Collections.singletonMap(2562, 1),
			counts(Collections.singletonList(ring), new int[]{2562}, NONE));
	}

	@Test
	public void t31e_nothingAnsweringInTheBankMeansNoNumber()
	{
		assertEquals(Collections.emptyMap(), counts(Collections.singletonList(brews(3)), new int[]{385}, NONE));
	}

	@Test
	public void t31f_aRuneNotInThePouchNumbersTheRune()
	{
		final CheckResult.Entry rune = new CheckResult.Entry(CheckResult.Kind.NOT_IN_POUCH, 560, 1, "Death rune",
			new TreeSet<>(Collections.singletonList(560)), false);
		assertEquals(Collections.singletonMap(560, 1), counts(Collections.singletonList(rune), new int[]{560}, NONE));
	}

	@Test
	public void t31g_twoEntriesOnOneBankItemAddUp()
	{
		final CheckResult.Entry rune = new CheckResult.Entry(CheckResult.Kind.NOT_IN_POUCH, 560, 1, "Death rune",
			new TreeSet<>(Collections.singletonList(560)), false);
		final CheckResult.Entry missing = new CheckResult.Entry(CheckResult.Kind.MISSING, 560, 4, "Death rune",
			new TreeSet<>(Collections.singletonList(560)), false);
		final Map<Integer, Integer> expected = new HashMap<>();
		expected.put(560, 5);
		assertEquals(expected, counts(Arrays.asList(rune, missing), new int[]{560}, NONE));
	}
}
